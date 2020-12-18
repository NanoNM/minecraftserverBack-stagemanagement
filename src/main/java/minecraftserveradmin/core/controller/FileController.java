package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.impl.NewFIleOperationImpl;
import minecraftserveradmin.core.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class FileController {

//  新的文件类避免用户隐私泄露
    @Autowired
    NewFIleOperationImpl fIleOperation;
    @ResponseBody
    @GetMapping("/admin/file")
    private List fileManger(@RequestParam(value = "path",required = false) String path){
        if (path == null){
            try {
                return fIleOperation.rootDir();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }else{
            try {
                return fIleOperation.getFIle(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
    @ResponseBody
    @GetMapping("/admin/renameFile")
    private Integer fileRename(@RequestParam(value = "path") String path,@RequestParam(value = "rename")String name){
        if (fIleOperation.renameFile(path, name)){
            return ErrorCode.FILE_RENAME_SUCCESS;
        }
        return ErrorCode.FILE_RENAME_ERROR;
    }
    @ResponseBody
    @GetMapping("/admin/download")
    private void fileDownloader(@RequestParam(value = "path") String path,HttpServletRequest request, HttpServletResponse response){
        try {
            fIleOperation.fileDownloader(path, request, response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
