package ch.epfl.sdp.healthplay.database;

import static org.junit.Assert.*;

import org.junit.Test;

public class FriendTest {

    @Test
    public void getUserIdTest() {
        String id = "id";
        Friend friend = new Friend(id);
        assertEquals(friend.getUserId(), id);

    }

    @Test
    public void setUserIdTest(){
        String id = "name";
        String newId = "newName";
        Friend friend = new Friend(id);
        friend.setUserId(newId);
        assertEquals(friend.getUserId(), newId);
    }

    @Test
    public void getUserNameTest() {
        String name = "name";
        Friend friend = new Friend("" ,name);
        assertEquals(friend.getUsername(), name);

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