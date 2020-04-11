package minecraftserveradmin.core;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.jna.platform.mac.SystemB;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@SpringBootTest
public class test {
    @Test
    public void main() throws IOException {
        //获取文件输入流
        FileInputStream input = new FileInputStream(".//mods//[CCCP]Soviet+Mod-0.4.jar");
        //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.forName("UTF8"));
        //定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
        ZipEntry ze = null;
        //循环遍历
        while ((ze = zipInputStream.getNextEntry()) != null) {
            if (ze.getName().equals("mcmod.info")){
                BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream, Charset.forName("UTF8")));
                String line;
                //内容不为空，输出
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
        }
        //一定记得关闭流
        zipInputStream.closeEntry();
        input.close();
    }
    @Test
    public void test1(){
        Scanner Sc=new Scanner(System.in);
        Sc.next();
        System.out.println("我也爱你");
    }
}
