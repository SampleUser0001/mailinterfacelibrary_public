package mil.send;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import mil.util.MilProperties;
import mil.util.MilPropertiesValues;

public class SendMail implements MilPropertiesValues{

	/**
	 * メールを送信する。
	 * @param to
	 * @param subject
	 * @param text
	 * @throws IOException
	 */
	public static void send(List<String> toList , List<String> ccList , List<String> bccList ,  String subject , String text) throws IOException{
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", MilProperties.getProperty(SEND_PROTOCOL));
		props.setProperty("mail.smtp.host", MilProperties.getProperty(SEND_HOST));
		props.setProperty("mail.smtp.port", MilProperties.getProperty(SEND_PORT));
		props.setProperty("mail.smtp.connectiontimeout", MilProperties.getProperty(SEND_CONNECTION_TIMEOUT));
		props.setProperty("mail.smtp.timeout", MilProperties.getProperty(SEND_TIMEOUT));
		props.setProperty("mail.smtp.auth", MilProperties.getProperty(SEND_AUTH));
	    Session session=Session.getInstance(props,new Authenticator(){
	    	@Override
	    	protected PasswordAuthentication getPasswordAuthentication() {
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

	    MimeMessage mimeMessage=new MimeMessage(session);
	    // InternetAddressの作成
	    int ccSize=0;
	    if(ccList != null){
	    	ccSize = ccList.size();
	    }
	    int bccSize=0;
	    if(bccList != null){
	    	bccSize = bccList.size();
	    }
	    InternetAddress[] toAddress=new InternetAddress[toList.size()];
	    InternetAddress[] ccAddress=new InternetAddress[ccSize];
	    InternetAddress[] bccAddress=new InternetAddress[bccSize];
		try {
			for(int i=0;i<toList.size();i++){
				toAddress[i] = new InternetAddress(toList.get(i));
			}
			for(int i=0;i<ccSize;i++){
				ccAddress[i] = new InternetAddress(ccList.get(i));
			}
			for(int i=0;i<bccSize;i++){
				bccAddress[i] = new InternetAddress(bccList.get(i));
			}
		} catch (AddressException e1) {
			e1.printStackTrace();
		}

	    try{
	    	// 送信元の設定
	    	mimeMessage.setFrom(new InternetAddress(MilProperties.getProperty(APPLICATION_FROM)));
			// 宛先の設定
			mimeMessage.setRecipients(MimeMessage.RecipientType.TO, toAddress);
			mimeMessage.setRecipients(MimeMessage.RecipientType.CC, ccAddress);
			mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, bccAddress);
			// サブジェクトの設定
			mimeMessage.setSubject(subject + " " + MilProperties.getProperty(BACKUP_TAG));
			// 本文の設定
			mimeMessage.setText(text);
			// 設定の保存
			mimeMessage.saveChanges();
			// メールの送信
			Transport.send(mimeMessage);
	    }catch(MessagingException e){
			e.printStackTrace();
	    }

	}
}
