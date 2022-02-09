package minecraftserveradmin.core.util;

import org.springframework.stereotype.Component;

@Component
public class TimeUtil {
    public static final long SpringBootStartTime = System.currentTimeMillis();
    public static long MCServerStartTime = -1;

    public static StringBuffer millisToStringShort(long l){
        StringBuffer sb=new StringBuffer();
        long millis=1;
        long seconds=1000*millis;
        long minutes=60*seconds;
        long hours=60*minutes;
        long days=24*hours;
        if(l/days>=1)
            sb.append((int)(l/days)+"天");
        if(l%days/hours>=1)
            sb.append((int)(l%days/hours)+"小时");
        if(l%days%hours/minutes>=1)
            sb.append((int)(l%days%hours/minutes)+"分钟");
        if(l%days%hours%minutes/seconds>=1)
            sb.append((int)(l%days%hours%minutes/seconds)+"秒");
        if(l%days%hours%minutes%seconds/millis>=1)
            sb.append((int)(l%days%hours%minutes%seconds/millis)+"毫秒");
        return sb;
    }
}
