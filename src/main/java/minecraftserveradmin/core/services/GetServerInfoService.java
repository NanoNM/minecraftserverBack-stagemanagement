package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.ServerInfoModel;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

@Service
public class GetServerInfoService {
    ServerInfoModel serverInfoModel = new ServerInfoModel();

    public String getSystem(){
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        return os.getFamily();
    }

    private String getSystemInfo(){
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        return os.getFamily()+" "+os.getVersion();
    }
    private String printProcessor(CentralProcessor processor) {
        return processor.getName();
    }
    private Integer printProcessorUser(CentralProcessor processor) {
        double i = (processor.getSystemCpuLoad() * 100);
        return (int) i;
    }
    private Integer printMemoryUser(GlobalMemory memory) {
        double i = (((double)memory.getTotal()-(double)memory.getAvailable())/(double)memory.getTotal()) *100;
        return (int)i;
    }

    public ServerInfoModel setModel(){
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        serverInfoModel.setSystemInfo(getSystemInfo());
        serverInfoModel.setCpuInfo(printProcessor(hal.getProcessor()));
        serverInfoModel.setCpuUserInfo(printProcessorUser(hal.getProcessor()));
        serverInfoModel.setMemoryUserInfo(printMemoryUser(hal.getMemory()));
        return serverInfoModel;
    }
}
