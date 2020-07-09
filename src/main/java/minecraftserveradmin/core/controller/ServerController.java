package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.services.FormatServerSettingService;
import minecraftserveradmin.core.services.RunServerService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

@Controller
public class ServerController {

    @Autowired
    RunServerService runServerService;

    @Autowired
    FormatServerSettingService formatServerSettingService;

    /**
     *
     * @param cmd 命令
     * @return 服务器指令传递
     */
    @ResponseBody
    @GetMapping("/admin/server/s")
    private Integer index(@Param("cmd") String cmd) {
        return runServerService.doCom(cmd);
    }

    /**
     *
     * @return string 控制台log信息
     */
    @ResponseBody
    @GetMapping("/admin/server")
    private StringBuffer serverInfoGetter(){
        StringBuffer s= new StringBuffer();
        try {
            File file = new File("./logs/latest.log");
            FileInputStream f = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(f);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while((line = br.readLine())!=null){
                s.append(line+"\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
            s.append("");
        }
        return s;
    }

    /**
     * 服务器状态获取
     * @return 0关机 1开始
     */
    @ResponseBody
    @GetMapping("/admin/serverTest")
    private Integer serverTest(){
       return runServerService.getServerIsOpen();
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
    @RequestMapping("/admin/setsetting")
    public boolean setSetting(@RequestBody JSONObject jsonParam) throws IOException {
        formatServerSettingService.settingFormat(jsonParam);
        return true;
    }
}
