package chat;

public enum MessageType {

	CONNECT("Connect"), DISCONNECT("Disconnect"), MESSAGE("Message");
	
	/**
	 * String that will be used in a message
	 */
	private String description;
	
	/**
	 * 
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
