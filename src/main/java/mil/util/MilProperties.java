package mil.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Satoru
 *
 */
public class MilProperties {
	public static final String MIL_PROPERTIES_FILE
		= System.getProperty("user.dir") + File.separator +
			"src" + File.separator +
			"main" + File.separator +
			"resources" + File.separator +
			"mil.properties";

	private static Properties milProperty = null;
	private MilProperties() throws IOException{}

	public static String getProperty(String key) throws IOException{
		if(milProperty == null){
			milProperty = new Properties();
			InputStream inputStream = new FileInputStream(new File(MIL_PROPERTIES_FILE));
			milProperty.load(inputStream);
			if(new Boolean(milProperty.getProperty(MilPropertiesValues.DEBUG))){
				System.out.println("mil.properties : " + MIL_PROPERTIES_FILE);
			}
		}
		return milProperty.getProperty(key);
	}
}
