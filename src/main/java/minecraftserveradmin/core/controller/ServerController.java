package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.RunServerService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

@Controller
public class ServerController {
    @Autowired
    RunServerService runServerService;
    @ResponseBody
    @GetMapping("/admin/server/s")
    private Integer index(@Param("cmd") String cmd) {
        return runServerService.doCom(cmd);
    }
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
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/admin/serverTest")
    private Integer serverTest(){
       return runServerService.getServerIsOpen();
    }

}
