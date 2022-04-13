package ch.epfl.sdp.healthplay.database;

import java.util.Set;

public class Friend {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Friend(String userName) {
        this.userName = userName;
    }

}
