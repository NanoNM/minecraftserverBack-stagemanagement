package minecraftserveradmin.core.util;

import minecraftserveradmin.core.entity.AOPtoken;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Component
public class TokenUtil {
    static Random random = new Random(1000);

    public Cookie getConnect(){
        long tmp_connect_token = random.nextInt(1000)+System.currentTimeMillis()+random.nextInt(1000);
        String connect_token = DigestUtils.md5DigestAsHex(String.valueOf(tmp_connect_token).getBytes());
        Cookie connect_token_cookie = new Cookie("connect_token",connect_token);

        return connect_token_cookie;
    }
    public Cookie getAutoLoginToken(){
        long tmp_token = random.nextInt(1000)+System.currentTimeMillis()+random.nextInt(1000);
        String token = DigestUtils.md5DigestAsHex(String.valueOf(tmp_token).getBytes());
        Cookie cookie = new Cookie("token",token);
        cookie.setMaxAge(6000*60);
        return cookie;
    }
    public static String getRandomString(){
        long tmp_token = random.nextInt(1000)+System.currentTimeMillis()+random.nextInt(1000);
        String token = DigestUtils.md5DigestAsHex(String.valueOf(tmp_token).getBytes());
        return token;
    }
    public static void getNewToken(Session session, List<AOPtoken> AOPtokens) {
        String testString = TokenUtil.getRandomString();
        AOPtoken aoPtoken = new AOPtoken();
        aoPtoken.setSession(session);
        aoPtoken.setToken(testString);
        aoPtoken.setBand(false);
        AOPtokens.removeIf(Session -> Session.getSession().getId().equals(session.getId()));
        AOPtokens.add(aoPtoken);
        try {
            synchronized (session){
                session.getBasicRemote().sendText("{\"Authentication\":\"" + testString + "\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getPassword(String defaultAdministratorPassword, String uuid){
        String tmp_pass =
                DigestUtils.md5DigestAsHex(defaultAdministratorPassword.getBytes()) +
                        DigestUtils.md5DigestAsHex(uuid.getBytes()) +
                        DigestUtils.md5DigestAsHex(defaultAdministratorPassword.getBytes());
        String pass = DigestUtils.md5DigestAsHex(tmp_pass.getBytes());
        return pass;
    }
}
