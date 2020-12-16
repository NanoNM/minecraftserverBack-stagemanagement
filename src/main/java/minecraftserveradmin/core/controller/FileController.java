package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.impl.FIleOperationImpl;
import minecraftserveradmin.core.services.impl.NewFIleOperationImpl;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class FileController {

//  新的文件类避免用户隐私泄露
    @Autowired
    NewFIleOperationImpl fIleOperation;
    @ResponseBody
    @GetMapping("/admin/file")
    private List fileManger(@Param("path") String path){
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
//                String[] arrays = System.getProperty("user.dir").split("\\\\");
//                String rootDir = arrays[arrays.length-1];
//                if (path.indexOf(rootDir) > 0){
//
//                }
//                return null; //这里要返回一个错误代码
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
