package minecraftserveradmin.core.controller;

import minecraftserveradmin.core.services.impl.FIleOperationImpl;
import minecraftserveradmin.core.entity.FileModel;
import minecraftserveradmin.core.entity.ServerInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

@Controller
public class FileController {
    @Autowired
    FIleOperationImpl fIleOperation;
    @ResponseBody
    @GetMapping("/admin/file")
    private List fileManger(){
        try {
            return fIleOperation.rootDir();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
