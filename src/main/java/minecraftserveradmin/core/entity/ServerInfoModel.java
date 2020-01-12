package minecraftserveradmin.core.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class ServerInfoModel {

    private String systemInfo;
    private String cpuInfo;
    private Integer cpuUserInfo;
    private Integer memoryUserInfo;

    public String getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(String systemInfo) {
        this.systemInfo = systemInfo;
    }

    public String getCpuInfo() {
        return cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public Integer getCpuUserInfo() {
        return cpuUserInfo;
    }

    public void setCpuUserInfo(Integer cpuUserInfo) {
        this.cpuUserInfo = cpuUserInfo;
    }

    public Integer getMemoryUserInfo() {
        return memoryUserInfo;
    }

    public void setMemoryUserInfo(Integer memoryUserInfo) {
        this.memoryUserInfo = memoryUserInfo;
    }
}
