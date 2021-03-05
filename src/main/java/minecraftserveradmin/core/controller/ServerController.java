package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.FormatServerSettingService;
import minecraftserveradmin.core.services.GetServerInfoService;
import minecraftserveradmin.core.services.RunServerService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;

@Controller
public class ServerController {

    @Autowired
    RunServerService runServerService;

    @Autowired
    GetServerInfoService getServerInfoService;

    @Autowired
    FormatServerSettingService formatServerSettingService;

    /**
     *
     * @param cmd 命令
     * @return 服务器指令传递
     */
    @ResponseBody
    @GetMapping("/admin/server/s")
    private String index(@Param("cmd") String cmd) {
//        return runServerService.doCom(cmd);
        return "接口废弃";
    }

    @ResponseBody
    @GetMapping("/admin/getjar")
    private List<String> getJar() {
        try {
            return getServerInfoService.getJar();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        return runServerService.doCom();
        return null;
    }

    /**
     *
     * @return string 控制台log信息
     */
    @ResponseBody
    @GetMapping("/admin/server")
    private String serverInfoGetter(){
//        StringBuffer s= new StringBuffer();
//        try {
//            File file = new File("./logs/latest.log");
//            FileInputStream f = new FileInputStream(file);
//            InputStreamReader isr = new InputStreamReader(f);
//            BufferedReader br = new BufferedReader(isr);
//            String line = null;
//            while((line = br.readLine())!=null){
//                s.append(line+"\n");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            s.append("");
//        }
        return "接口废弃";
    }

    /**
     * 服务器状态获取
     * @return 0关机 1开始
     */
    @ResponseBody
    @GetMapping("/admin/serverTest")
    private String serverTest(){
//       return runServerService.getServerIsOpen();
        return "接口废弃";
    }

    /**
     * 服务器当前设置
     * @return json字符串
     * @throws IOException 无
     */
    @ResponseBody
    @GetMapping("/admin/getsetting")
    public String infoEdit() throws IOException {
        return formatServerSettingService.getModel();
    }

    /**
     *
     * @param jsonParam json字符串
     * @return 无用
     * @throws IOException 无
     */
    @ResponseBody
    @GetMapping("/admin/setsetting")
    public boolean setSetting(@RequestParam("jsonstr") String jsonParam) {
        try {
            formatServerSettingService.settingFormat(jsonParam);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

//    @ResponseBody
//    @GetMapping("/admin/setsetting")
//    public boolean setSetting(@Param("jsonstr") JSONObject jsonParam) throws IOException {
////        formatServerSettingService.settingFormat(jsonParam);
//        return true;
//    }
}


