package minecraftserveradmin.core.services;

import minecraftserveradmin.core.MainecraftApplication;
import minecraftserveradmin.core.entity.ServerInfoModel;
import minecraftserveradmin.core.util.TimeUtil;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GetServerInfoService {
    ServerInfoModel serverInfoModel = new ServerInfoModel();

    private static final String systemInfo;

    static{
        systemInfo = getSystemInfo();
    }

    public List<String> getJar() throws UnsupportedEncodingException {
        String jarLocation = URLDecoder.decode(MainecraftApplication.class.getProtectionDomain().getCodeSource().getLocation().getFile(), "UTF-8" );
        File file = new File("./");
        List<String> jarFileNameList = new ArrayList<>();
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile() && !jarLocation.contains(tempList[i].getName() + "!/BOOT-INF/classes!/")) {
                String[] strings = tempList[i].toString().split("\\.");
                if (strings.length!=0)
                    if ("jar".equals(strings[strings.length-1])){
                        jarFileNameList.add(tempList[i].getName());
                    }
            }
            if (tempList[i].isDirectory()) {
            }
        }
        return jarFileNameList;
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
        serverInfoModel.setSpringBootRunningTime(TimeUtil.millisToStringShort(System.currentTimeMillis()-TimeUtil.SpringBootStartTime).toString());


        if (TimeUtil.MCServerStartTime > 0){
            SimpleDateFormat mcStartTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mcStartTimeString = mcStartTime.format(new Date(TimeUtil.MCServerStartTime)); // 时间戳转换日期
            serverInfoModel.setMcServerStartTime(mcStartTimeString);
            serverInfoModel.setMcServerRunningTime(TimeUtil.millisToStringShort(System.currentTimeMillis()-TimeUtil.MCServerStartTime).toString());
        }else{
            serverInfoModel.setMcServerStartTime("Power off");
            serverInfoModel.setMcServerRunningTime("0");
        }



//        serverInfoModel.setMcServerStartTime(TimeUtil.MCServerStartTime);
//        serverInfoModel.setMcServerRunningTime(System.currentTimeMillis()-TimeUtil.MCServerStartTime);
        return serverInfoModel;
    }
}
