package mil.util;

public interface MilPropertiesValues {
	/** アプリケーション名 */
	public static final String APPLICATION_NAME = "mil.application.name";

	/** ユーザ一覧 */
	public static final String USERS = "mil.application.users";

	public static final String APPLICATION_CLASS = "mil.application.class";

	public static final String LOGIN_ID = "mil.application.login.id";
	public static final String LOGIN_PASSWORD = "mil.application.login.password";

	public static final String RECEIVE_HOST = "mil.receive.mail.host";
	public static final String RECEIVE_AUTH = "mil.receive.mail.auth";
	public static final String RECEIVE_INTERVAL = "mil.receive.mail.interval";


	public static final String SEND_SETTING = "mil.send.mail.setting";
	public static final String SEND_ADDRESS = "mil.send.mail.address";
	public static final String SEND_HOST = "mil.send.mail.host";
	public static final String SEND_PORT = "mil.send.mail.port";
	public static final String SEND_PROTOCOL = "mil.send.mail.protocol";
	public static final String SEND_TIMEOUT = "mil.send.mail.timeout";
	public static final String SEND_CONNECTION_TIMEOUT = "mil.send.mail.connectiontimeout";
	public static final String SEND_AUTH = "mil.send.mail.auth";

	public static final String APPLICATION_FROM = "mil.application.mail.address";

	public static final String BACKUP_ADDRESS = "mil.send.backup.address";
	public static final String BACKUP_TAG = "mil.send.backup.tag";

	public static final String DEBUG = "mil.debug";
}
