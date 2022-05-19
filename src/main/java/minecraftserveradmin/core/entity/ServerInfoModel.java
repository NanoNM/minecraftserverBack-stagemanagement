package minecraftserveradmin.core.entity;

import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan
public class ServerInfoModel {

    private String systemInfo;
    private String cpuInfo;
    private Double cpuUserInfo;
    private Integer memoryUserInfo;
    private String springBootRunningTime;
    private String springBootStartTime;
    private String mcServerRunningTime;
    private String mcServerStartTime;

    public String getSpringBootStartTime() {
        return springBootStartTime;
    }

    public void setSpringBootStartTime(String springBootStartTime) {
        this.springBootStartTime = springBootStartTime;
    }

    public String getMcServerRunningTime() {
        return mcServerRunningTime;
    }

    public void setMcServerRunningTime(String mcServerRunningTime) {
        this.mcServerRunningTime = mcServerRunningTime;
    }

    public String getMcServerStartTime() {
        return mcServerStartTime;
    }

    public void setMcServerStartTime(String mcServerStartTime) {
        this.mcServerStartTime = mcServerStartTime;
    }

    public String getSpringBootRunningTime() {
        return springBootRunningTime;
    }

    public void setSpringBootRunningTime(String springBootRunningTime) {
        this.springBootRunningTime = springBootRunningTime;
    }

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

    public double getCpuUserInfo() {
        return cpuUserInfo;
    }

    public void setCpuUserInfo(double cpuUserInfo) {
        this.cpuUserInfo = cpuUserInfo;
    }

    public Integer getMemoryUserInfo() {
        return memoryUserInfo;
    }

    public void setMemoryUserInfo(Integer memoryUserInfo) {
        this.memoryUserInfo = memoryUserInfo;
    }
}
