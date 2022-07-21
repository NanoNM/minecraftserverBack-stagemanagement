package minecraftserveradmin.core.services;

import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Platform;
import minecraftserveradmin.core.dto.SocketResult;
import minecraftserveradmin.core.controller.WebSocketService;
import minecraftserveradmin.core.services.impl.AdminSocketImpl;
import minecraftserveradmin.core.util.Kernel32;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.StaticDataUtil;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;

import static minecraftserveradmin.core.util.TimeUtil.MCServerStartTime;

@Service
public class RunServerService {
    int a = 1;
    public static Process process;
    public static long pid = -1;
    private Integer serverIsOpen = 0;

    @Autowired
    SocketHandlerServices socketHandlerServices;

    String logname;
    //判断系统
    GetServerInfoService getServerInfoService = new GetServerInfoService();
    public boolean testSystem(){
        return "Windows".equals(getServerInfoService.getSystem());
    }


    public Integer doCom(String cmd){
        try {
            if ("startserver".equals(cmd) && serverIsOpen == 0){
                //System.out.println(System.getProperty("user.dir"));
                LogUtil.log.info("jar包通过指令==>" + StaticDataUtil.cmd +"执行了");
                process = Runtime.getRuntime().exec(StaticDataUtil.cmd);

                //获取jar包进程的pid
                Field field = null;
                if (Platform.isWindows()) {
                    try {
                        field = process.getClass().getDeclaredField("handle");
                        field.setAccessible(true);
                        pid = Kernel32.INSTANCE.GetProcessId((Long) field.get(process));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (Platform.isLinux() || Platform.isAIX()) {
                    try {
                        Class<?> clazz = Class.forName("java.lang.UNIXProcess");
                        field = clazz.getDeclaredField("pid");
                        field.setAccessible(true);
                        pid = (Integer) field.get(process);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
            Thread threadReader = new Thread(() -> {
                MCServerStartTime = System.currentTimeMillis();
                LogUtil.log.info("我的世界服务器启动");
                InputStream inputStream = process.getInputStream();
                String line = null;
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, System.getProperties().get("sun.jnu.encoding").toString()));
                    while((line = br.readLine())!= null){
//                            LogUtil.log.info();
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("console", new String(line.getBytes(), System.getProperty("file.encoding")) + "\n");
                        SocketResult socketResult = new SocketResult("console","",new String(("[Minecraft]: "+line).getBytes(), System.getProperty("file.encoding")) + "\n");
                        JSONObject socketResultJson = (JSONObject) JSONObject.toJSON(socketResult);
                        AdminSocketImpl.sendMessageToAll(socketResultJson.toJSONString(),WebSocketService.onlineSessions);
                    }
                    serverIsOpen = 0;
                    inputStream.close();
                    process.destroy();
                    LogUtil.log.info("我的世界服务器关闭");
                    MCServerStartTime = -1;
                }catch (Exception e){ LogUtil.log.error(e.getMessage()); }
            });

            Thread threadError = new Thread(() -> {
                LogUtil.log.info("服务器异常捕获启动");
                try{
                    BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                    String readLine = br.readLine();
                    String befline = "";
                    String line = null;
                    while ((line = readLine)!= null) {
                        if(serverIsOpen == 0){
                            LogUtil.log.info("服务器异常捕获关闭");
                            break;
                        }
                        if (!befline.equals(line)){
                            LogUtil.log.error(
                                    "==================异常=================\n"
                                    + line +
                                    "\n================结束=================");

                            befline = line;
                        }
                        Thread.sleep(250);
                    }
                }catch (Exception e){
                    LogUtil.log.error(e.getMessage());
                }
            });

            Thread threadSender = new Thread(() -> {
                LogUtil.log.info("我的世界服务器指令响应");
                try {
                    OutputStream outputStream = process.getOutputStream();
                    OutputStreamWriter ow = new OutputStreamWriter(outputStream);
                    DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                    ow.write(cmd+"\n");
                    ow.flush();
                    LogUtil.log.info("我的世界服务器指令响应结束");
                }catch (Exception e){
                    LogUtil.log.info(e.getMessage());
                }
            });

            if (serverIsOpen == 0 & "startserver".equals(cmd)){
                this.serverIsOpen = 1;
                threadReader.start();
                threadError.start();
            }else if (cmd!=null && !"startserver".equals(cmd)){
                // 极度不安全的强制关机
                if("hardstop".equals(cmd)){
                    LogUtil.log.info("我的世界服务器强制关机指令送达");
                    int destroyTime = 1;
                    while (process.isAlive()){
                        LogUtil.log.info("关闭进程尝试 " + destroyTime +"次！");
                        process.destroy();
                        process = process.destroyForcibly();
                        if (destroyTime > 10){
                            Time.sleep(200);
                            LogUtil.log.info("关闭进程尝试10次还是失败 使用TASKKILL");
                            if (pid!=-1){
                                if (Platform.isWindows()) {
                                    Process processTasKill = Runtime.getRuntime().exec("TASKKILL /PID " + pid);
                                } else if (Platform.isLinux() || Platform.isAIX()) {
                                    Process processTasKill = Runtime.getRuntime().exec("kill " + pid);
                                } else {
                                }
                                if (process.isAlive()){
                                    LogUtil.log.error("草 使用TASKKILL也失败了 你干了啥？？？ 你启动了啥？？？ 建议重启管理面板主程序 实在不行你关机吧");
                                }
                            }else {
                                LogUtil.log.error("获取pid失败");
                            }
                            break;
                        }
                        destroyTime++;
                    }
                    return 0;
                }
                threadSender.start();
                if("stop".equals(cmd)){
                    LogUtil.log.info("我的世界服务器关机指令送达");
                }
                LogUtil.log.info("我的世界服务器指令送达");
            }else if (serverIsOpen == 0){

            }


        } catch (IOException e) {
            LogUtil.log.error(e.getMessage());
        }
        StaticDataUtil.cmd = "java -jar";
        return serverIsOpen;
    }

    public Integer getServerIsOpen() {
        return serverIsOpen;
    }

    public void setServerIsOpen(Integer serverIsOpen) {
        this.serverIsOpen = serverIsOpen;
    }
}
