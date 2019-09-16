package mil.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import mil.receive.ReceiveMail;
import mil.send.SendMail;
import mil.util.MilProperties;
import mil.util.MilPropertiesValues;
import mil.window.SettingWindow;

public class ReceiveRoutine extends Thread implements MilPropertiesValues {

	private ReceiveMail receiver;
	private boolean isRunning = false;

	@Override
	public void run() {
		try {
			// メール受信処理のインスタンス
			receiver = new ReceiveMail();

			// アプリケーションインスタンスを作成する。
			// （Milではユーザの任意のプログラム。mil.propertiesの"mil.application.classで指定したクラス。）
			Class appClass = Class.forName(MilProperties.getProperty(APPLICATION_CLASS));
			Application appInstance = (Application)appClass.newInstance();

			isRunning = true;
			while(isRunning){
				SettingWindow.setMessage("処理中…");

				// 受信処理開始。
				receiver.receive();

				// 受信したメールをMilが提供するメッセージに詰める。
				// とりあえず直近で使いたかったものから順に詰めておく。
				List<MilMessage> milMessageList = new ArrayList<MilMessage>();
				for(Message msg : receiver.getReceiveMessage()){
					MilMessage mm = new MilMessage();

					if(msg.getContentType().contains("text/plain")){
						mm.setText(msg.getContent().toString());
					} else if(msg.getContentType().contains("multipart/alternative")){
						MimeMultipart multi = (MimeMultipart) msg.getContent();
						mm.setText(multi.getBodyPart(0).getContent().toString());
					} else {
						mm.setText("");
					}

					mm.setFromAddress(((InternetAddress)msg.getFrom()[0]).getAddress());

					milMessageList.add(mm);
				}

				// アプリケーションを実行
				appInstance.application(receiver.getReceiveMessage(),milMessageList);

			    // メール送信するかしないかをmil.propertiesに持たせる。
			    // trueで送信する。
			    String isSend = MilProperties.getProperty(SEND_SETTING);
			    if(!new Boolean(isSend)){
			    	if(new Boolean(MilProperties.getProperty(DEBUG))){
			    		System.out.println("送信しない設定");
			    	}
			    } else {

					// 処理したメールをバックアップアドレスに送る。
					// バックアップアドレスはmil.propertiesのmil.send.backup.addressで指定した値。
					for(int i=0;i<receiver.getReceiveMessage().size();i++){
						Message msg = receiver.getReceiveMessage().get(i);
						String text = milMessageList.get(i).getText();
						List<String> toList = new ArrayList<String>();
						toList.add(MilProperties.getProperty(BACKUP_ADDRESS));

						StringBuffer _text = new StringBuffer();
						_text.append("subject : " + msg.getSubject()).append(System.getProperty("line.separator"));
						_text.append("from:" + msg.getFrom()[0]).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
						_text.append(text);

						SendMail.send(
								toList,
								null,
								null,
								"MilBackup : " + msg.getSentDate(),
								_text.toString());
					}
			    }
				// 受信処理終了。
				// closeを読んだ時点でmil.propertiesで指定しているアプリのメールはすべて消える。
				receiver.close();

				// 次の処理タイミング(mil.propertiesのmil.receive.mail.interval秒)まで待つ。
				int waitTime;
				waitTime = Integer.parseInt(MilProperties.getProperty(RECEIVE_INTERVAL));
				for( ; waitTime >= 0 ; waitTime-- ){
					if(isRunning){
						SettingWindow.setMessage("次回処理は" + waitTime + "秒後です。");
						sleep(1000);
					}
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}


}
