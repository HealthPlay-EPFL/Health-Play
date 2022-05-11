package ch.epfl.sdp.healthplay.chat;

// Model of a message
public class ModelChat {
    private String message;
    private String type;
    private String timestamp;
    private String receiver;
    private String sender;

    /**
     * Construct a ModelChat
     * @param message
     * @param receiver
     * @param sender
     * @param type
     * @param timestamp
     */
    public ModelChat(String message, String receiver, String sender, String type, String timestamp) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.timestamp = timestamp;
    }

    /**
     * Construct an empty ModelChat
     */
    public ModelChat() {
    }

    /**
     * Get message
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set message
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get receiver
     * @return
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Set receiver
     * @param receiver
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * Get sender
     * @return
     */
    public String getSender() {
        return sender;
    }

    /**
     * Set sender
     * @param sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Get timestamp
     * @return
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Set timestamp
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get type
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Set type
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }


}
