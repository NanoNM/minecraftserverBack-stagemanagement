package minecraftserveradmin.core.util;

/**
 * 用来主动调用垃圾回收机制
 * @author  nanometer
 * @since   Ver.snapshot
 */
public class GCUtil {
    public static void doGC(){
        System.gc();
        System.gc();
        System.gc();
    }
}
