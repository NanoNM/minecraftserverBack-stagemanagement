package minecraftserveradmin.core;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.ErrorCodeService;
import minecraftserveradmin.core.services.impl.FIleOperationImpl;
import minecraftserveradmin.core.services.impl.NewFIleOperationImpl;
import minecraftserveradmin.core.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@SpringBootTest
public class test {

//    public static void main(String[] args) throws IOException {
////        HashSet h = new HashSet(list);
//        StringBuffer ss= new StringBuffer();
//        while(true){
//            StringBuffer s= new StringBuffer();
//            try {
//                File file = new File("logs/latest.log");
//                FileInputStream f = new FileInputStream(file);
//                InputStreamReader isr = new InputStreamReader(f);
//                BufferedReader br = new BufferedReader(isr);
//                String line = null;
//                while((line = br.readLine())!=null){
//                    s.append(line+"\n");
//                }
//                br.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                s.append("");
//            }
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            JSONObject jsonObject = new JSONObject();
//
//            StringBuffer s2 = new StringBuffer();
//            s2.setLength(0);
//            s2.append(s);
//            s2.delete(0,ss.length());
//            jsonObject.put("console", s2.toString());
//            if (!ss.toString().equals(s.toString())) {
//                ss.setLength(0);
//                ss.append(s);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(jsonObject.toJSONString());
//            }
//        }
//    }
    public static void main(String[] args) {
//        String encoding = System.getProperty("file.encoding");
        System.out.println();

//        new
//        System.out.println(TimeUtil.millisToStringShort(10000000l).toString());

    }
    @Autowired
    NewFIleOperationImpl newFIleOperation;

    @Test
    void Test() throws IOException {

    }
}

