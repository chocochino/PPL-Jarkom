package chat;

/**
 * 
 * @author Steven Albert
 *
 */
public enum MessageType {
	
	USER_CONNECT("UserConnect"),
	USER_DISCONNECT("UserDisconnect"),
	CHAT_MESSAGE("ChatMessage"),
	CHANGE_USERNAME("ChangeUsername"),
	FILE("File"),
	END_FILE("EndFile"),
	SERVER_CONNECT("ServerConnect"),
	SERVER_DISCONNECT("ServerDisconnect");
	
	public static final char TYPE_SEPARATOR = '$';
	public static final char FILENAME_SEPARATOR = '&';
	public static final String PRIVATE = "!PRIVATE!";
	
	/**
	 * String that will be used in a message
	 */
	private String description;
	
	/**
	 * Initialize the Enumeration Object
	 * @param desc
	 */
	private MessageType(String desc) {
		this.description = desc;
	}
	
	@Override
	public String toString() {
		return description;	
	}
}
