package minecraftserveradmin.core.entity;

import java.util.List;

/**
 * Plugin实体类 存放自定义的Plugin信息
 */
public class PluginModel {
    String plguinFilename;
    String plguinName;
    List<String> plguinAuthors;
    String plguinVersion;

    boolean isdisable;
    boolean isremoved;

    public String getPlguinFilename() {
        return plguinFilename;
    }

    public void setPlguinFilename(String plguinFilename) {
        this.plguinFilename = plguinFilename;
    }

    public String getPlguinName() {
        return plguinName;
    }

    public void setPlguinName(String plguinName) {
        this.plguinName = plguinName;
    }

    public List<String> getPlguinAuthors() {
        return plguinAuthors;
    }

    public void setPlguinAuthors(List<String> plguinAuthors) {
        this.plguinAuthors = plguinAuthors;
    }

    public String getPlguinVersion() {
        return plguinVersion;
    }

    public void setPlguinVersion(String plguinVersion) {
        this.plguinVersion = plguinVersion;
    }

    public boolean isIsdisable() {
        return isdisable;
    }

    public void setIsdisable(boolean isdisable) {
        this.isdisable = isdisable;
    }

    public boolean isIsremoved() {
        return isremoved;
    }

    public void setIsremoved(boolean isremoved) {
        this.isremoved = isremoved;
    }
}
