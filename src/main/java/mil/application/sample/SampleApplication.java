package mil.application.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import mil.application.Application;
import mil.application.MilMessage;

public class SampleApplication implements Application {

	public static final String OUTPUT_FILE = System.getProperty("user.dir") + File.separator + "sample"+ File.separator +"MailLog.txt";

	@Override
	public void application(List<Message> messageList, List<MilMessage> textMessageList) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE),true));
			writer.write("---------------------------------" + System.getProperty("line.separator"));
			for (int i=0 ; i<messageList.size() ; i++) {
				Message msg = messageList.get(i);
				String text = textMessageList.get(i).getText();
				writer.write("Email Number " + msg.getMessageNumber() + System.getProperty("line.separator"));
				writer.write("Subject: " + msg.getSubject() + System.getProperty("line.separator"));
				writer.write("Date: " + msg.getSentDate() + System.getProperty("line.separator"));
				writer.write("From: " + msg.getFrom()[0] + System.getProperty("line.separator"));
				writer.write("Text: " + text + System.getProperty("line.separator"));
			}
			writer.write("---------------------------------" + System.getProperty("line.separator"));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

}
