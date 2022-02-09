package minecraftserveradmin.core.services.impl;

import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.NewFileOperationService;
import minecraftserveradmin.core.util.CharsetUtil;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.GCUtil;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
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
//            FileInputStream fis = null;
//            BufferedInputStream bis = null;
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis)){

                OutputStream os = httpResponse.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
                GCUtil.doGC();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List rootDir() {
        List<NewFileModel> fileModelList = new ArrayList<>();
        File file = new File(projectPath);
        File[] fs = file.listFiles();	//遍历path下的文件和目录，放在File数组中

        for(File f:fs){//遍历File[]数组
//            String rootPath = System.getProperty("user.dir");
            String s = f.getPath().replace(projectPath+File.separator,"");
            if(!f.isDirectory()){
                String fileTyle = "";
                try {
                    try{
                        fileTyle = f.getName().substring(f.getName().lastIndexOf("."));
                    }catch (StringIndexOutOfBoundsException se){
                        fileTyle = null;
                    }
                    NewFileModel fileModel = new NewFileModel(f.getName(),fileTyle,1,f.length(),null, null, s, null);
                    fileModelList.add(fileModel);
                }catch (Exception e){
                    e.printStackTrace();
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
                    fileTyle = null;
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
        File oldName = new File(path);
        File newName = new File(name);
        if (newName.exists()) {
            throw new java.io.IOException("文件已存在" + newName);
        }
        if(oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean fileEditor(String path, String str){
        File f = new File(path);
        try (OutputStream os = new FileOutputStream(f);){
            os.write(str.getBytes());
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean fileDelete(String path) {
        File f = new File(path);
        return doDelete(f);
    }
    private boolean doDelete(File f){
        if (f.isDirectory()){
            boolean flag = false;
            File[] fs = f.listFiles();
            if (fs!=null){
                for (File file:fs){
                    if (file.isDirectory()){
                        doDelete(file);
                    }
                    flag = file.delete();
                }
            }else{
                return f.delete();
            }
            if (flag){
                return f.delete();
            }
        }
        if(f.exists()) {
            return f.delete();
        }
        return false;
    }

    public Integer mkdir(String path) {
        File f = new File(path);
        if(f.exists()){
            return ErrorCode.FOLDER_ALREADY_EXISTS;
        }
        boolean flag = f.mkdir();
        if (flag){
            return ErrorCode.FOLDER_CREATED_SUCCESSFULLY;
        }
        return ErrorCode.FOLDER_CREATED_FAIL;
    }

    public Integer makeFile(String path) {
        File f = new File(path);
        if(f.exists()){
            return ErrorCode.FILE_ALREADY_EXISTS;
        }
        boolean flag = false;
        try {
            flag = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.FILE_CREATED_FAIL;
        }
        if (flag){
            return ErrorCode.FILE_CREATED_SUCCESSFULLY;
        }
        return ErrorCode.FILE_CREATED_FAIL;
    }

    public Integer fileCopy(String source, String dest) {
        File f = new File(source);
        File f2 = new File(dest);
        try {
            Files.copy(f.toPath(), f2.toPath());
            return ErrorCode.FILE_COPY_SUCCESS;
        } catch (FileAlreadyExistsException e) {
            return ErrorCode.FILE_ALREADY_EXISTS;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.FILE_COPY_FAIL;
        }
    }

    public Integer fileMove(String source, String dest) {
        File f = new File(source);
        File f2 = new File(dest);
        try {
            Files.copy(f.toPath(), f2.toPath());
            f.delete();
            return ErrorCode.FILE_MOVE_SUCCESS;
        } catch (FileAlreadyExistsException e) {
            return ErrorCode.FILE_ALREADY_EXISTS;
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorCode.FILE_MOVE_FAIL;
        }
    }
}
