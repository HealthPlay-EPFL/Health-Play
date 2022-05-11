package ch.epfl.sdp.healthplay.chat;

import static org.junit.Assert.*;

import org.junit.Test;

public class ModelChatTest {

    private ModelChat modelChat;
    @Test
    public void getMessage() {
        String message = "message";
        modelChat = new ModelChat(message, "", "", "", "", false);
        assertEquals(modelChat.getMessage(), message);
    }

    @Test
    public void setMessage() {
        String message = "message";
        modelChat = new ModelChat();
        modelChat.setMessage(message);
        assertEquals(modelChat.getMessage(), message);

    }

    @Test
    public void getReceiver() {
        String receiver = "receiver";
        modelChat = new ModelChat("", receiver, "", "", "", false);
        assertEquals(modelChat.getReceiver(), receiver);
    }

    @Test
    public void setReceiver() {
        String receiver = "receiver";
        modelChat = new ModelChat();
        modelChat.setReceiver(receiver);
        assertEquals(modelChat.getReceiver(), receiver);
    }

    @Test
    public void getSender() {
        String sender = "sender";
        modelChat = new ModelChat("", "", sender, "", "", false);
        assertEquals(modelChat.getSender(), sender);
    }

    @Test
    public void setSender() {
        String sender = "sender";
        modelChat = new ModelChat();
        modelChat.setSender(sender);
        assertEquals(modelChat.getSender(), sender);
    }

    @Test
    public void getTimestamp() {
        String timestamp = "timestamp";
        modelChat = new ModelChat("", "", "", "", timestamp, false);
        assertEquals(modelChat.getTimestamp(), timestamp);
    }

    @Test
    public void setTimestamp() {
        String timestamp = "timestamp";
        modelChat = new ModelChat();
        modelChat.setTimestamp(timestamp);
        assertEquals(modelChat.getTimestamp(), timestamp);
    }

    @Test
    public void isDilihat() {
        modelChat = new ModelChat("", "", "", "", "", true);
        assertTrue(modelChat.isDilihat());
    }

    @Test
    public void setDilihat() {
        modelChat = new ModelChat();
        modelChat.setDilihat(false);
        assertFalse(modelChat.isDilihat());
    }

    @Test
    public void getType() {
        String type = "type";
        modelChat = new ModelChat("", "", "", type, "", false);
        assertEquals(modelChat.getType(), type);
    }

    @Test
    public void setType() {
        String type = "type";
        modelChat = new ModelChat();
        modelChat.setType(type);
        assertEquals(modelChat.getType(), type);
    }
}