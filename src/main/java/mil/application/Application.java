package mil.application;

import java.util.List;

import javax.mail.Message;

public interface Application {

	abstract void application(List<Message> messageList, List<MilMessage> textMessageList);
}
