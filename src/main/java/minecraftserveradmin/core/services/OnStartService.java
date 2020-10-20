package minecraftserveradmin.core.services;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public interface OnStartService extends ApplicationRunner {
    @Override
    void run(ApplicationArguments args) throws Exception;
}
