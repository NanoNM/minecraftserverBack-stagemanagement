package minecraftserveradmin.core.services;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.controller.WebSocketService;
import minecraftserveradmin.core.services.impl.AdminSocketImpl;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.StaticDataUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static minecraftserveradmin.core.util.TimeUtil.MCServerStartTime;

@Service
public class RunServerService {
    int a = 1;
    public static Process process;
    private Integer serverIsOpen = 0;

    @Autowired
    SocketHandlerServices socketHandlerServices;

    String logname;
    //判断系统
    GetServerInfoService getServerInfoService = new GetServerInfoService();
    public boolean testSystem(){
        return "Windows".equals(getServerInfoService.getSystem());
    }

//    @Value("${minecraft.startservercmd}")
//    public void setCom(String com) {
//        RunServerService.com = com;
//    }

    public Integer doCom(String cmd){
//        String projectPath  = System.getProperty("user.dir");
//        String target = projectPath.concat("\\MineCraftServer");
//        System.setProperty("user.dir", target);
        try {
            if ("startserver".equals(cmd) && serverIsOpen == 0){
                //System.out.println(System.getProperty("user.dir"));
                LogUtil.log.info("jar包通过指令==>" + StaticDataUtil.cmd +"执行了");
                process = Runtime.getRuntime().exec(StaticDataUtil.cmd);
                MCServerStartTime = System.currentTimeMillis();
            }

            Thread threadReader = new Thread(new Runnable() {
                @Override
                public void run(){
                    MCServerStartTime = System.currentTimeMillis();
                    LogUtil.log.info("我的世界服务器启动");
                    InputStream inputStream = process.getInputStream();
                    String line = null;
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, System.getProperties().get("sun.jnu.encoding").toString()));
                        while((line = br.readLine())!= null){
//                            LogUtil.log.info();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("console", new String(line.getBytes(), StandardCharsets.UTF_8) + "\n");
                            AdminSocketImpl.sendMessageToAll(jsonObject.toJSONString(),WebSocketService.onlineSessions);
                        }
                        serverIsOpen = 0;
                        inputStream.close();
                        process.destroy();
                        LogUtil.log.info("我的世界服务器关闭");
                        MCServerStartTime = -1;
                    }catch (Exception e){ LogUtil.log.error(e.getMessage()); }
                }
            });

            Thread threadError = new Thread(new Runnable() {
                @Override
                public void run() {
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
                                LogUtil.log.error("==================异常=================");
                                LogUtil.log.error(line);
                                LogUtil.log.error("================异常结束================");

                                befline = line;
                            }
                            Thread.sleep(250);
                        }
                    }catch (Exception e){
                        LogUtil.log.error(e.getMessage());
                    }
                }
            });

            Thread threadSender = new Thread(new Runnable() {
                @Override
                public void run() {
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
                    if (process.isAlive()){
                        process.destroy();
                        process.destroy();
                        process.destroyForcibly();
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
