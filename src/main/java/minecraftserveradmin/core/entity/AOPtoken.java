package minecraftserveradmin.core.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class AOPtoken {
    private String SessionID;
    private String token;
    private boolean band;

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isBand() {
        return band;
    }

    public void setBand(boolean band) {
        this.band = band;
    }
}
