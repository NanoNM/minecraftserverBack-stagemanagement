package minecraftserveradmin.core;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.ErrorCodeService;
import minecraftserveradmin.core.services.impl.FIleOperationImpl;
import minecraftserveradmin.core.services.impl.NewFIleOperationImpl;
import minecraftserveradmin.core.util.CharsetUtil;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;


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
        int x = -1233;
        int len = String.valueOf(x).length();
        int nums[] = new int[len];
        for(int i=0; i<len; i++){
            nums[i] = x % 10;
            x /= 10;
        }
        Stack<Integer> sta = new Stack<>();
        for (int s : nums) { sta.push(s);}
        int fanlNums[] = new int[len];
        for(int i=0; i<len; i++){
            fanlNums[len-i-1] = sta.pop();
        }
        long sum = 0;
        for(int i=0; i<len-1; i++){
            sum = sum * 10 + fanlNums[i];
        }
        if (sum>Integer.MIN_VALUE){
            return;
        }
        System.out.println(sum);

//        SystemInfo si = new SystemInfo();
//        HardwareAbstractionLayer hal = si.getHardware();
//        OperatingSystem os = si.getOperatingSystem();
//        LogUtil.log.info("${jndi:rmi://127.0.0.1:1099/obj}");
//        String s = "helloworld";
//        File file = new File(s);
//        System.out.println(CharsetUtil.guessCharacterSet(file));
//        String s = "§7";
//        String str = "";
//        for (int i = 0; i < s.length(); i++) {
//            int ch = s.charAt(i);
//            String s4 = Integer.toHexString(ch);
//            str = str + s4;
//        }
//        System.out.println(str);
//        String encoding = System.getProperty("file.encoding");
//        System.out.println();

//        new
//        System.out.println(TimeUtil.millisToStringShort(10000000l).toString());

    }
    @Autowired
    NewFIleOperationImpl newFIleOperation;

    @Test
    void Test() throws IOException {

    }
}

