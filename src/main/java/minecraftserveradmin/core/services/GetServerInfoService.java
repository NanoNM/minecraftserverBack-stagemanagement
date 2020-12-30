package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.ServerInfoModel;
import minecraftserveradmin.core.util.TimeUtil;
import org.apache.poi.ss.formula.functions.T;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class GetServerInfoService {
    ServerInfoModel serverInfoModel = new ServerInfoModel();

    private static final String systemInfo;

    static{
        systemInfo = getSystemInfo();
    }

    public String getSystem(){
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        return os.getFamily();
    }

    private static String getSystemInfo(){
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
        serverInfoModel.setSystemInfo(systemInfo);
        serverInfoModel.setCpuInfo(printProcessor(hal.getProcessor()));
        serverInfoModel.setCpuUserInfo(printProcessorUser(hal.getProcessor()));
        serverInfoModel.setMemoryUserInfo(printMemoryUser(hal.getMemory()));
        SimpleDateFormat SpringBootStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String SpringBootStartTimeString = SpringBootStartTime.format(new Date(TimeUtil.SpringBootStartTime)); // 时间戳转换日期
        serverInfoModel.setSpringBootStartTime(SpringBootStartTimeString);
        SimpleDateFormat SpringBootRunningTime = new SimpleDateFormat("HH:mm:ss");
//        String SpringBootRunningTimeString = SpringBootRunningTime.format(new Date()); // 时间戳转换日期
        serverInfoModel.setSpringBootRunningTime(System.currentTimeMillis()-TimeUtil.SpringBootStartTime + "ms");

//        serverInfoModel.setMcServerStartTime(TimeUtil.MCServerStartTime);
//        serverInfoModel.setMcServerRunningTime(System.currentTimeMillis()-TimeUtil.MCServerStartTime);
        return serverInfoModel;
    }
}
