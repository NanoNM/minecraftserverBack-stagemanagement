package minecraftserveradmin.core.entity;

import javax.websocket.Session;

public class OlineUserModel {
    private String userID;
    private Session session;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public OlineUserModel(String userID, Session session) {
        this.userID = userID;
        this.session = session;
    }
}
