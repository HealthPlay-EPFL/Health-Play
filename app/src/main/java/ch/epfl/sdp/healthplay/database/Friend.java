package ch.epfl.sdp.healthplay.database;

import java.util.Set;

public class Friend {
    private String username;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Friend(String userId) {
        this.userId = userId;
    }

    public Friend(String userId, String username){
        this.userId = userId;
        this.username = username;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "userId=" + userId +
                " username='" + username + '\'' +
                '}';
    }
}
