package ch.epfl.sdp.healthplay.database;

import static org.junit.Assert.*;

import org.junit.Test;

public class FriendTest {

    @Test
    public void getUserTest() {
        String name = "name";
        Friend friend = new Friend(name);
        assertEquals(friend.getUserName(), name);

    }

    @Test
    public void setUserTest(){
        String name = "name";
        String newName = "newName";
        Friend friend = new Friend(name);
        friend.setUserName(newName);
        assertEquals(friend.getUserName(), newName);
    }
}