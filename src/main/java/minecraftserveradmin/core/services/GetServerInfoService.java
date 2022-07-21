package minecraftserveradmin.core.services;

import minecraftserveradmin.core.MainecraftApplication;
import minecraftserveradmin.core.entity.ServerInfoModel;
import minecraftserveradmin.core.util.TimeUtil;
import oshi.SystemInfo;
import oshi.driver.windows.perfmon.ProcessorInformation;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import org.springframework.stereotype.Service;
import oshi.util.GlobalConfig;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

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
        //使用任务管理器调度 https://github.com/oshi/oshi/pull/1886/files FAQ.MD
        GlobalConfig.set("oshi.os.windows.cpu.utility", true);
        OperatingSystem os = si.getOperatingSystem();
        return os.getFamily()+" "+os.getVersionInfo();
    }

    private String printProcessor(CentralProcessor processor) {
        return processor.toString();
    }
    private double printProcessorUse(CentralProcessor processor) throws InterruptedException {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

//        processor.getSystemCpuLoadTicks();
////        double i = (Arrays.stream(processor.getCurrentFreq()).average().getAsDouble());
//        System.out.println(i);
//        System.out.println((idle * 1.0 / totalCpu)*100);
//        System.out.println((int);
        return ((1-(idle * 1.0 / totalCpu))*100);
    }
    private Integer printMemoryUse(GlobalMemory memory) {
        double i = (((double)memory.getTotal()-(double)memory.getAvailable())/(double)memory.getTotal()) *100;
        return (int)i;
    }

    public ServerInfoModel setModel() throws InterruptedException {

        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        serverInfoModel.setSystemInfo(systemInfo);
        serverInfoModel.setCpuInfo(printProcessor(hal.getProcessor()));
        serverInfoModel.setCpuUserInfo(printProcessorUse(hal.getProcessor()));
        serverInfoModel.setMemoryUserInfo(printMemoryUse(hal.getMemory()));
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


    public String synchronizationConsole() {
        StringBuilder s= new StringBuilder();
        try {
            File file = new File("logs/latest.log");
            FileInputStream f = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(f);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while((line = br.readLine())!=null){
                s.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            s.append("");
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(s.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Base64.getEncoder().encodeToString(out.toByteArray());


//        return Base64.getEncoder().encode(s.toString().getBytes());
    }
}
