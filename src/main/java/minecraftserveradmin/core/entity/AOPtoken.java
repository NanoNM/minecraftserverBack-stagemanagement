package minecraftserveradmin.core.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.websocket.Session;

@EntityScan
public class AOPtoken {
    private Session session;
    private String token;
    private String name;
    private boolean band;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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
