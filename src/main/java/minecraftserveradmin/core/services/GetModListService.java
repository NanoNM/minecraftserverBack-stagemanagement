package minecraftserveradmin.core.services;

import minecraftserveradmin.core.entity.ModModel;
import minecraftserveradmin.core.util.LogUtil;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class GetModListService {
    List<ModModel> modModelList = new ArrayList<>();

    public List doScan(){
        //清空集合
        int size = modModelList.size();
        for(int i=size-1;i>=0;i--){
            modModelList.remove(i);
        }
        //遍历mod
        String path = ".//mods";
        File file = new File(path);
        File[] fs = file.listFiles();
        if (fs == null){
            return null;
        }
        for(File f:fs) {
            if (!f.isDirectory()) {
                ModModel m = new ModModel();
                try{
                    m.setModFilename(f.getName().split(".jar")[0]);
                }catch (Exception e){
                    e.printStackTrace();
                }
                m.setStatus("normal");
                if (f.getName().indexOf("disable") > 0){
                    m.setStatus("disable");
                    m.setIsdisable(true);
                }
                if (f.getName().indexOf("removed") > 0){
                    m.setStatus("removed");
                    m.setIsremoved(true);
                }
                modModelList.add(m);
            }
        }
        return modModelList;
    }

    public void able(String filename){
        String path = ".//mods";
        File file = new File(path);
        File[] fs = file.listFiles();
        for(File f:fs) {
            if (f.getName().contains(filename) && f.isFile()){
                File oldName = new File(".//mods//"+f.getName());
                File newName = new File(".//mods//"+filename+".jar");
                oldName.renameTo(newName);
            }
        }
    }

    public void disable(String filename){
        String path = ".//mods";
        File file = new File(path);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.getName().contains(filename) && f.isFile()) {
                File oldName = new File(".//mods//" + f.getName());
                File newName = new File(".//mods//" + filename + ".jardisable");
                oldName.renameTo(newName);
            }
        }
    }

    public String moreModInfo(String filename){
        String path = "./mods";
        File file = new File(path);
        File[] fs = file.listFiles();
        assert fs != null;
        for(File f:fs) {
            if (!f.isDirectory()) {
                if (f.getName().contains(filename) && f.isFile()) {
                    try(FileInputStream input = new FileInputStream("./mods/"+f.getName());
                        //获取ZIP输入流(一定要指定字符集Charset.forName("GBK")否则会报java.lang.IllegalArgumentException: MALFORMED)
                        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(input), Charset.forName("UTF8"))){
                        //定义ZipEntry置为null,避免由于重复调用zipInputStream.getNextEntry造成的不必要的问题
                        //循环遍历
                        ZipEntry ze = null;
                        while ((ze = zipInputStream.getNextEntry()) != null) {
                            if (ze.getName().equals("mcmod.info")){
                                BufferedReader br = new BufferedReader(new InputStreamReader(zipInputStream, Charset.forName("UTF8")));
                                String line;
                                StringBuilder lis = new StringBuilder();
                                //内容不为空，输出
                                while ((line = br.readLine()) != null) {
                                    lis.append(line);
                                }
                                return lis.toString();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        LogUtil.log.error("mod文件信息读取错误"+e.getLocalizedMessage());
                    }
                }
            }
        }
        return null;
    }

    public void remove(String filename) {
        String path = ".//mods";
        File file = new File(path);
        File[] fs = file.listFiles();
        for (File f : fs) {
            if (f.getName().contains(filename) && f.isFile()) {
                File oldName = new File(".//mods//" + f.getName());
                File newName = new File(".//mods//" + filename + ".jarremoved");
                oldName.renameTo(newName);
            }
        }
    }
}
