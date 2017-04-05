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
	PRIVATE_CHAT_MESSAGE("PrivateChatMessage"),
	CHANGE_USERNAME("ChangeUsername"),
	SERVER_CONNECT("ServerConnect"),
	SERVER_DISCONNECT("ServerDisconnect");
	
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
