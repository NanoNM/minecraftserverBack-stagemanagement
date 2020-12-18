package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.NewFileOperationService;
import minecraftserveradmin.core.util.CharsetUtil;
import minecraftserveradmin.core.util.LogUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewFIleOperationImpl implements NewFileOperationService {
    private static final String projectPath;
    static{
        projectPath  = System.getProperty("user.dir");
    }

    public int renameFile(){
        return 1;
    }

    public boolean renameFile(String path, String name){
        try {
            if (reNameFile(path,name))
                return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void fileDownloader(String string, HttpServletRequest request, HttpServletResponse httpResponse) throws UnsupportedEncodingException {
        File file = new File(string);
        if (file.exists()) {
            httpResponse.setHeader("content-type", "application/octet-stream" + ";charset=" + CharsetUtil.guessCharacterSet(file));
            httpResponse.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpResponse.setContentType("application/octet-stream");
            // 下载文件能正常显示中文
            httpResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

            // 实现文件下载
            byte[] buffer = new byte[4096];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = httpResponse.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                LogUtil.log.info("文件下载成功");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public List rootDir() throws IOException {
        List<NewFileModel> fileModelList = new ArrayList<>();
        File file = new File(projectPath);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中

        for(File f:fs){//遍历File[]数组
//            String rootPath = System.getProperty("user.dir");
            String s = f.getPath().replace(projectPath+File.separator,"");
            if(!f.isDirectory())		//若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                    NewFileModel fileModel = new NewFileModel(f.getName(),fileTyle,1,f.length(),null, null, s, null);
                    fileModelList.add(fileModel);
                }catch (Exception ignored){
                }
            }else {
                NewFileModel fileModel = new NewFileModel(f.getName(),"",0,0,null,null, s, null);
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }

    public List<NewFileModel> getFIle(String name) throws IOException {
        List<NewFileModel> fileModelList = new ArrayList<>();
        File file = new File(name);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中
        for(File f:fs) {
            String s = f.getPath().replace(projectPath,"");//遍历File[]数组
            if (!f.isDirectory())        //若非目录(即文件)，则打印
            {
                String fileTyle = "";
                try {
                    fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                }catch (Exception ignored){
                }
                NewFileModel fileModel = new NewFileModel(f.getName(),fileTyle,1,f.length(),getFather(f.getPath()), null, s, null);
                fileModelList.add(fileModel);
            }else {
                NewFileModel fileModel = new NewFileModel(f.getName(),"",0,0,getFather(f.getPath()),null, s,  null);
                fileModelList.add(fileModel);
            }
        }
        return fileModelList;
    }

    private String getFather(String path) {
        File file = new File(path);
        String fatherName = file.getParentFile().getAbsolutePath();
        String s = fatherName.replace(projectPath+File.separator,"");
        return s;
    }

    private boolean reNameFile(String path,String name) throws IOException {
        // 旧的文件或目录
        File oldName = new File(path);
        // 新的文件或目录
        File newName = new File(name);
        if (newName.exists()) {  //  确保新的文件名不存在
            throw new java.io.IOException("文件已存在");
        }
        if(oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
        }
    }
}
