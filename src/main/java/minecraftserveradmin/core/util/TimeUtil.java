package minecraftserveradmin.core.util;

import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
    public static final long SpringBootStartTime = System.currentTimeMillis();
    public static long MCServerStartTime = -1;
}
