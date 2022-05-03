package ch.epfl.sdp.healthplay.database;

import static org.junit.Assert.*;

import org.junit.Test;

public class FriendTest {

    @Test
    public void getUserIdTest() {
        String name = "id";
        Friend friend = new Friend(name);
        assertEquals(friend.getUserId(), name);

    }

    @Test
    public void setUserIdTest(){
        String name = "name";
        String newName = "newName";
        Friend friend = new Friend(name);
        friend.setUserId(newName);
        assertEquals(friend.getUserId(), newName);
    }

    @Test
    public void getUserNameTest() {
        String name = "id";
        Friend friend = new Friend("" ,name);
        assertEquals(friend.getUserId(), name);

    }

    @Test
    public void setUserNameTest(){
        String name = "name";
        String newName = "newName";
        Friend friend = new Friend("", name);
        friend.setUsername(newName);
        assertEquals(friend.getUsername(), newName);
    }
}