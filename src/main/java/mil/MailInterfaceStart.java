package mil;

import java.awt.HeadlessException;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mil.application.ReceiveRoutine;
import mil.util.MilProperties;
import mil.util.MilPropertiesValues;
import mil.window.SettingWindow;

public class MailInterfaceStart implements MilPropertiesValues{

	private static Logger logger = LogManager.getLogger("MailInterfaceStart");

	public static final String START_WINDOW = "window";
	public static final String START_INLINE = "inline";

	public static void main(String[] args){

        SettingWindow window;
		try {
			// mainに渡される引数から画面を起動するかどうか判断する。
			// windowで画面起動。
			log(args);

			if(args == null || args.length == 0 || args[0].equals("window")){
				window = new SettingWindow(MilProperties.getProperty(APPLICATION_NAME));
				SettingWindow.setMessage("hoge");
			} else if(args[0].equals(START_INLINE)){
				// inlineは画面起動しない。
				Thread receiveThread = new ReceiveRoutine();
		        receiveThread.start();
			} else {
				throw new IllegalArgumentException("Param is \"window\" or \"inline\". \r\n ");
			}
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void log(String[] args){
		logger.info("Start.");
		if(args == null || args.length == 0){
			logger.info("args is empty.");
		} else {
			logger.info("args[0]:{}" , args[0]);
		}
	}
}
