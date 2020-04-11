package minecraftserveradmin.core.services;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import minecraftserveradmin.core.entity.ServerSettingModel;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service

public class FormatServerSettingService {
    public String getModel() throws IOException {
        //读取properties文件
        Properties pro = new Properties();
        FileInputStream in = new FileInputStream("./server.properties");
        pro.load(in);
        in.close();
        //存储properties文件为map
        Map<String,String> map = new HashMap<String,String>();
        Enumeration<Object> keys = pro.keys();
        while(keys.hasMoreElements()){
            String key = (String)keys.nextElement();
            String value = (String)pro.get(key);
            map.put(key,value);
        }
        //pro 2 json
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        return jsonStr;
    }

    public void settingFormat(JSONObject jsonParam) throws IOException {
        jsonToProp(jsonParam,"./server.properties");
    }

    public  void jsonToProp(JSONObject jsonObject,String Path) {
        Map<String, Object> map= jsonObject.getInnerMap();
        Properties properties=new Properties();
        for (String key : map.keySet()) {
            properties.setProperty(key, map.get(key).toString());
        }
        try {
            OutputStream fos = new FileOutputStream(Path);
            System.err.println(properties.toString());
            try {
                properties.store(fos, properties.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
