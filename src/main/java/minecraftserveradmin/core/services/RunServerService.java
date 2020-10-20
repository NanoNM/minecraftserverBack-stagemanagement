package minecraftserveradmin.core.services;

import minecraftserveradmin.core.util.LogUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;

@Service
public class RunServerService {
    int a = 1;
    public static Process process;
    private Integer serverIsOpen = 0;
    String logname;
    //判断系统
    GetServerInfoService getServerInfoService = new GetServerInfoService();
    public boolean testSystem(){
        return "Windows".equals(getServerInfoService.getSystem());
    }

    public Integer doCom(String cmd){
//        String projectPath  = System.getProperty("user.dir");
//        String target = projectPath.concat("\\MineCraftServer");
//        System.setProperty("user.dir", target);
        String com = "java -jar server.jar";
        try {
            if ("startserver".equals(cmd) && serverIsOpen == 0){
                //System.out.println(System.getProperty("user.dir"));
                process = Runtime.getRuntime().exec(com);
            }

            Thread threadReader = new Thread(new Runnable() {
                @Override
                public void run(){
                    LogUtil.log.info("我的世界服务器启动");
                    InputStream inputStream = process.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF8")));
                    String line = null;
                    try {
                        while((line = br.readLine())!= null){
                            LogUtil.log.info(line);
                        }
                        serverIsOpen = 0;
                        inputStream.close();
                        process.destroy();
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
                            if (!befline.equals(line)){
                                LogUtil.log.error(line);
                                befline = line;
                            }
                        }
                    }catch (Exception e){
                        LogUtil.log.error(e.getMessage());
                    }
                }
            });

            Thread threadSender = new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.log.info("我的世界服务器指令响应!!!!");
                    try {
                        OutputStream outputStream = process.getOutputStream();
                        OutputStreamWriter ow = new OutputStreamWriter(outputStream);
                        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                        ow.write(cmd+"\n");
                        ow.flush();
                        LogUtil.log.info("我的世界服务器指令响应结束!!!!");
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
                    LogUtil.log.info("我的世界服务器强制关机指令送达!!!!");
                    process.destroy();
                    return 0;
                }
                threadSender.start();
                if("stop".equals(cmd)){
                    LogUtil.log.info("我的世界服务器关机指令送达!!!!");
                }
                LogUtil.log.info("我的世界服务器指令送达!");
            }else if (serverIsOpen == 0){

            }


        } catch (IOException e) {
            LogUtil.log.error(e.getMessage());
        }
        return serverIsOpen;
    }

    public Integer getServerIsOpen() {
        return serverIsOpen;
    }

    public void setServerIsOpen(Integer serverIsOpen) {
        this.serverIsOpen = serverIsOpen;
    }
}
