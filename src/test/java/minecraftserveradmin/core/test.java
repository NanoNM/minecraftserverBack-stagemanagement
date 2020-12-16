package minecraftserveradmin.core;

import minecraftserveradmin.core.entity.NewFileModel;
import minecraftserveradmin.core.services.ErrorCodeService;
import minecraftserveradmin.core.services.impl.FIleOperationImpl;
import minecraftserveradmin.core.services.impl.NewFIleOperationImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;


@SpringBootTest
public class test {

    @Autowired
    NewFIleOperationImpl newFIleOperation;

    @Test
    void Test() throws IOException {
        List<NewFileModel> fileModelList = newFIleOperation.rootDir();
        System.out.println(fileModelList);

        List<NewFileModel> fileModelList2 = newFIleOperation.getFIle(".git\\hooks");
        System.out.println(fileModelList2);
    }
}

