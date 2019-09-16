package mil.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MilUtil {

	private MilUtil(){}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:sss");
	public static String dateFormat(Date date){
		return DATE_FORMAT.format(date);
	}
}
