package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.entity.PlayerModel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

/**
 * @author HASEE
 */
@Controller
public class PlayerController {
    @ResponseBody
    @GetMapping("/admin/testPlayer")
    public PlayerModel testPlayer(@Param("id")String id) throws IOException {
        PlayerModel playerModel= new PlayerModel();
        FileReader fr = new FileReader(new File("./ops.json"));
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        StringBuffer sb = new StringBuffer();
        while((line = br.readLine()) != null){
            sb.append(line);
        }
        String str = String.valueOf(sb);
        fr.close();
        if (str.indexOf(id)!=-1){
            playerModel.setOp(true);
            playerModel.setName(id);
        }
//        System.out.println(sb);
//        JSONArray opFileJson = JSON.parseArray(str);
//        System.out.println(opFileJson);
        return playerModel;
    }
}
