package minecraftserveradmin.core.entity;

import java.util.List;

/**
 * MOD实体类 存放自定义的mod信息
 */
public class ModModel {
    String modFilename;
    String modName;
    List<String> modAuthors;
    String modVersion;

    boolean isdisable;
    boolean isremoved;

    public boolean isIsremoved() {
        return isremoved;
    }

    public void setIsremoved(boolean isremoved) {
        this.isremoved = isremoved;
    }

    public String getModFilename() {
        return modFilename;
    }

    public void setModFilename(String modFilename) {
        this.modFilename = modFilename;
    }

    public String getModName() {
        return modName;
    }

    public void setModName(String modName) {
        this.modName = modName;
    }

    public List<String> getModAuthors() {
        return modAuthors;
    }

    public void setModAuthors(List<String> modAuthors) {
        this.modAuthors = modAuthors;
    }

    public String getModVersion() {
        return modVersion;
    }

    public void setModVersion(String modVersion) {
        this.modVersion = modVersion;
    }

    public boolean isIsdisable() {
        return isdisable;
    }

    public void setIsdisable(boolean isdisable) {
        this.isdisable = isdisable;
    }
}
