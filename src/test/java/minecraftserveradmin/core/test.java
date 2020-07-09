package minecraftserveradmin.core;

import minecraftserveradmin.core.services.impl.FIleOperationImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@SpringBootTest
public class test {
    @Autowired
    FIleOperationImpl fIleOperation;

    @Test
    void Test() throws IOException {
        System.out.println(fIleOperation.rootDir());
    }
}
