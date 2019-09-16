package mil.receive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.mail.pop3.POP3Store;

import mil.util.MilProperties;
import mil.util.MilPropertiesValues;
import mil.util.MilUtil;


/**
 * メールの受信をするクラス。
 * @author Satoru
 *
 */
public class ReceiveMail implements MilPropertiesValues{

	private static Logger logger = LogManager.getLogger("ReceiveMail");

	/** 受信したメッセージ一覧。アプリ名と送信元アドレスでフィルタリング済み。 */
	private List<Message> receiveMessages = new ArrayList<Message>();

	private Session session;
	private POP3Store store;
	private Folder folder;

	private List<String> userList;

	public ReceiveMail(){
		logger.debug("constructor");
		userList = new ArrayList<String>();
		try {
			String users = MilProperties.getProperty(USERS);
			logger.debug("{}:{}",USERS,users);
			for(String user : users.split(",")){
				userList.add(user);
			}
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * メッセージを受信する。受信したメッセージはgetReceiveMessagesで取得できる。
	 * @return
	 * @throws IOException
	 * @throws MessagingException
	 */
	public void receive() throws IOException, MessagingException{
		logger.debug("receive()");

		List<Message> returnMessages = new ArrayList<Message>();

		Properties props = new Properties();

		// メール取得元ホスト
		props.setProperty("mail.pop3.host", MilProperties.getProperty(RECEIVE_HOST));
		logger.debug("{}:{}","mail.pop3.host",MilProperties.getProperty(RECEIVE_HOST));
		// メール取得時に承認を要求するか
		props.setProperty("mail.pop3.auth", MilProperties.getProperty(RECEIVE_AUTH));
		logger.debug("{}:{}","mail.pop3.auth",MilProperties.getProperty(RECEIVE_AUTH));


		// ログイン承認
		session = Session.getInstance(props,new Authenticator(){
	    	@Override
	    	protected PasswordAuthentication getPasswordAuthentication() {
	    		// IDとパスワードを使用してログインする。失敗したらnullを返す。
	    		PasswordAuthentication auth = null;
		        try {
					auth = new PasswordAuthentication(
							MilProperties.getProperty(LOGIN_ID),
							MilProperties.getProperty(LOGIN_PASSWORD));
				} catch (IOException e) {
					e.printStackTrace();
				}
		        return auth;
	    	}
	    });
		logger.debug("LoginID:{}",MilProperties.getProperty(LOGIN_ID));
		logger.trace("LoginPassword:{}",MilProperties.getProperty(LOGIN_PASSWORD));

		// pop3で接続する。
		POP3Store store = (POP3Store) session.getStore("pop3");
		store.connect();

		// 処理対象のメールは拾った後にサーバから削除する。
		folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);

		if(!new Boolean(MilProperties.getProperty(DEBUG))){
			// アプリを使用しているメールアドレス一覧を取得
			// メールアドレス一覧がnullだったらメールの取得もしない。
			if(!userList.isEmpty()){
				// メール読み込み
				Message[] msgs = folder.getMessages();
				for(Message msg : msgs){
					// メールタイトルが指定のアプリケーション名で、
					// 送信元が登録されているアドレスのみ取得する。
					String subject = msg.getSubject();
					String address = ((InternetAddress) msg.getFrom()[0]).getAddress();
					if(subject.equals(MilProperties.getProperty(APPLICATION_NAME)) && userList.contains(address)){
						receiveMessages.add(msg);
						writeMessageLogInfo(msg);
					}
				}
				if(writeMessageLogIndex == 0){
					logger.info("{} is Nothing.",MilProperties.getProperty(APPLICATION_NAME));
				}
				writeMessageLogIndex = 0;
			}
		} else {
			// デバッグ用
			// mil.propertiesで指定している受信メールアドレスのメールの中身を標準出力に吐き出す。
			if(!userList.isEmpty()){
				System.out.println("---------------------------------");
				// メール読み込み
				Message[] msgs = folder.getMessages();
				for(Message msg : msgs){
					// メールタイトルが指定のアプリケーション名で、
					// 送信元が登録されているアドレスのみ取得する。
					InternetAddress[] addresses = (InternetAddress[]) msg.getFrom();
//					String addressOnly = msg.getFrom()[0].toString().split("<")[1].split(">")[0];
					String subject = msg.getSubject();
					String applicationName = MilProperties.getProperty(APPLICATION_NAME);
//					System.out.println("addressOnly:"+addressOnly);
					int addressIndex = 0;
					for(InternetAddress address : addresses){
						System.out.println("addresses[" + addressIndex + "] address :" + address.getAddress() );
						System.out.println("addresses[" + addressIndex + "] personal :" + address.getPersonal() );
						System.out.println("addresses[" + addressIndex + "] toUnicodeString :" + address.toUnicodeString() );
						addressIndex++;
					}
					String mailAddress = addresses[0].getAddress();
					System.out.println("subject:"+subject);
					System.out.println("applicationName:"+applicationName);
//					System.out.println("userList.contains(addressOnly):" + userList.contains(addressOnly));
					System.out.println("sendDate:" + msg.getSentDate());
					System.out.println();
					if(subject.equals(applicationName) && userList.contains(mailAddress)){
						receiveMessages.add(msg);
					}
				}
				System.out.println("---------------------------------");
			}
		}
	}

	/**
	 * 取得したメールのクリアを行う。
	 * @throws MessagingException
	 */
	public void close() throws MessagingException{

		for(Message msg : receiveMessages){
			msg.setFlag(Flags.Flag.DELETED, true);
		}
	    folder.close(true);
	    store = null;
	    session = null;
	    receiveMessages.clear();

	}

	public List<Message> getReceiveMessage(){
		return receiveMessages;
	}

	private int writeMessageLogIndex = 0;
	private void writeMessageLogInfo(Message msg){
		try {
			// 送信元
			logger.info("Sender[{}]:{}",writeMessageLogIndex,((InternetAddress) msg.getFrom()[0]).getAddress());

			// 送信日
			logger.info("SendDate[{}]:{}",writeMessageLogIndex,MilUtil.dateFormat(msg.getSentDate()));

			// 送信内容
			String sendText = "";
			if(msg.getContentType().contains("text/plain")){
				sendText = msg.getContent().toString();
			} else if(msg.getContentType().contains("multipart/alternative")){
				MimeMultipart multi = (MimeMultipart) msg.getContent();
				sendText = multi.getBodyPart(0).getContent().toString();
			}
			logger.info("Text[{}]:{}",writeMessageLogIndex, sendText);

		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			writeMessageLogIndex++;
		}
	}
}
