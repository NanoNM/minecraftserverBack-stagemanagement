package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.PluginModel;
import minecraftserveradmin.core.util.LogUtil;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class GetPluginsListService {
    List<PluginModel> plugList = new ArrayList<>();

    public List doScan(){
        //清空集合
        int size = plugList.size();
        for(int i=size-1;i>=0;i--){
            plugList.remove(i);
        }
        //遍历mod
        String path = ".//plugins";
        File file = new File(path);
        File[] fs = file.listFiles();
        for(File f:fs) {
            if (!f.isDirectory()) {
                PluginModel p = new PluginModel();
                try{
                    p.setPlguinFilename(f.getName().split(".jar")[0]);
                }catch (Exception e){

                }
                if (f.getName().indexOf("disable") > 0){
                    p.setIsdisable(true);
                }
                if (f.getName().indexOf("removed") > 0){
                    p.setIsremoved(true);
                }
                plugList.add(p);
            }
        }
        return plugList;
    }

    public void able(String filename){
        String path = ".//plugins";
        File file = new File(path);
        File[] fs = file.listFiles();
        assert fs != null;
        for(File f:fs) {
            System.err.println(f.getName());
            if (f.getName().contains(filename) && f.isFile()){
                File oldName = new File("./plugins/"+f.getName());
                File newName = new File("./plugins/"+filename+".jar");
                oldName.renameTo(newName);
            }
        }
    }
    public void disable(String filename){
        String path = ".//plugins";
        File file = new File(path);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.getName().contains(filename) && f.isFile()) {
                File oldName = new File(".//plugins//" + f.getName());
                File newName = new File(".//plugins//" + filename + ".jardisable");
                oldName.renameTo(newName);
            }
        }
    }

    public Map<String, Object> morePluginInfo(String filename){
        String path = "./plugins";
        File file = new File(path);
        File[] fs = file.listFiles();
        assert fs != null;
        for(File f:fs) {
            if (!f.isDirectory()) {
                if (f.getName().contains(filename) && f.isFile()) {
                    try(FileInputStream input = new FileInputStream("./plugins/"+f.getName());
                        //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
                        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.forName("UTF8"))//定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
                        ){
                        //循环遍历
                        ZipEntry ze = null;
                        while ((ze = zipInputStream.getNextEntry()) != null) {
                            if (ze.getName().equals("plugin.yml")){
                                BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream, Charset.forName("UTF8")));
                                String line;
                                StringBuilder lis = new StringBuilder();
                                //内容不为空，输出
                                while ((line = br.readLine()) != null) {
                                    lis.append(line);
                                    lis.append("\n");
                                }
                                Yaml yaml = new Yaml();
                                Map<String,Object> map = yaml.loadAs(lis.toString(), HashMap.class);
                                return map;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        LogUtil.log.error("plugin文件信息读取错误"+e.getLocalizedMessage());
                    }
                }
            }
        }
        return null;
    }

    public void remove(String filename) {
        String path = ".//plugins";
        File file = new File(path);
        File[] fs = file.listFiles();
        System.out.println(filename);
        for (File f : fs) {
            if (f.getName().contains(filename) && f.isFile()) {
                File oldName = new File(".//plugins//" + f.getName());
                File newName = new File(".//plugins//" + filename + ".jarremoved");
                oldName.renameTo(newName);
            }
        }
    }
}
