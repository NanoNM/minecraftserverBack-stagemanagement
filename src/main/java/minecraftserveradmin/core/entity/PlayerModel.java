package minecraftserveradmin.core.entity;

public class PlayerModel {
    private String name;
    private boolean isOp;
    private boolean isIDBaned;
    private boolean isIpBaned;
    private boolean isWhiteList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOp() {
        return isOp;
    }

    public void setOp(boolean op) {
        isOp = op;
    }

    public boolean isIDBaned() {
        return isIDBaned;
    }

    public void setIDBaned(boolean IDBaned) {
        isIDBaned = IDBaned;
    }

    public boolean isIpBaned() {
        return isIpBaned;
    }

    public void setIpBaned(boolean ipBaned) {
        isIpBaned = ipBaned;
    }

    public boolean isWhiteList() {
        return isWhiteList;
    }

    public void setWhiteList(boolean whiteList) {
        isWhiteList = whiteList;
    }
}
