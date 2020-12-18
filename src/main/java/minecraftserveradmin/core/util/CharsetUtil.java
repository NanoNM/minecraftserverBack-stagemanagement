package minecraftserveradmin.core.util;

import org.apache.any23.encoding.TikaEncodingDetector;

import java.io.*;
import java.nio.charset.Charset;

public class CharsetUtil {
    public static final String LOCAL_CHARACTER_SET = System.getProperty("file.encoding");

    public static String guessCharacterSet(File file) {
        try {
            InputStream is = new FileInputStream(file);
            return String.valueOf(Charset.forName(new TikaEncodingDetector().guessEncoding(is)));
        } catch (IOException e) {
            e.printStackTrace();
            return CharsetUtil.LOCAL_CHARACTER_SET;
        }
    }
}
