package minecraftserveradmin.core.services.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.AOPtoken;
import minecraftserveradmin.core.services.FormatServerSettingService;
import minecraftserveradmin.core.services.GetServerInfoService;
import minecraftserveradmin.core.services.RunServerService;
import minecraftserveradmin.core.services.SocketRelatedService;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LogUtil;
import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class AdminSocketImpl implements SocketRelatedService {
    private static String testString;
    private static final RunServerService runServerService = new RunServerService();
    private static final GetServerInfoService getServerInfoService = new GetServerInfoService();
    private static final FormatServerSettingService formatServerSettingService = new FormatServerSettingService();

    @Autowired
    UserDao userDao;

    static public String serverInfoSender;

    public void sendStatusToConnect(Session session) throws IOException, InterruptedException {
        Thread.sleep(2000);
        String message = "{\"ServerStat\":\"" + runServerService.getServerIsOpen().toString() + "\"}";
        session.getBasicRemote().sendText(message);
    }

    public String message(Session session, String jsonStr, Map<String, Session> onlineSessions, List<AOPtoken> AOPtokens){
        JSONObject jb = JSONObject.parseObject(jsonStr);
        if (jb==null)
            return null;
        String username = jb.getString("username");

        Integer onlineUser = userDao.selectOnlineByName(username);
        if (onlineUser == null || onlineUser == 0){
            try {
                onlineSessions.remove(session.getId());
                AOPtokens.removeIf(SessionId -> SessionId.getSession().getId().equals(session.getId()));
                session.getBasicRemote().sendText("{\"ERROR\":\"" + ErrorCode.USER_NOT_ONLINE + "\"}");
                LogUtil.log.warn("侦测到企图未登陆请求");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonStr;
        }
        if ("cmd".equals(jb.getString("name"))){
            runServerService.doCom(jb.getString("value"));
        }
        if ("message".equals(jb.getString("name"))) {
//            System.out.println(jb.getString("value"));
            if (jb.getString("value").equals("SocketClosed")){
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static void mcServerStateSender(Map<String, Session> onlineSessions){
        Thread mcServerStateSender = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = -1;
                while(true){
                    int index2 = runServerService.getServerIsOpen();
                    if (index2!=index && onlineSessions.size()>0){
                        for (String key:onlineSessions.keySet()) {
                            try {
                                synchronized(onlineSessions.get(key)){
                                    onlineSessions.get(key).getBasicRemote().sendText("{\"ServerStat\":\"" + index2 + "\"}");
                                }
                                onlineSessions.get(key).getBasicRemote().sendText("{\"ServerStat\":\"" + index2 + "\"}");
                                index = index2;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(Integer.parseInt(serverInfoSender));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mcServerStateSender.start();
    }

    public static void ServerInfoSender(Map<String, Session> onlineSessions){
        Thread ServerInfoSender = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = -1;
                while(true){
                    for (String key:onlineSessions.keySet()) {
                        try {
                            synchronized(onlineSessions.get(key)){
                                onlineSessions.get(key).getBasicRemote().sendText("{\"systemInfo\":[" + JSON.toJSONString(getServerInfoService.setModel()) + "]}");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ServerInfoSender.start();
    }

    public static void authenticationThreadReader(List<AOPtoken> aoPtokens, Map<String, Session> onlineSessions) {
        Thread authenticationThreadReader = new Thread(new Runnable() {
            int size = 0;
            Long startTs = System.currentTimeMillis();
            @Override
            public void run() {
                while (true) {
                    Long stopTs = System.currentTimeMillis();
                    int size2 = onlineSessions.size();
                    if (size != size2 || (stopTs - startTs) == 1000) {
                        startTs = stopTs;
                        size = size2;
                        onlineSessions.forEach((id, session) -> {
                            try {
                                LogUtil.log.info("向客户端: " + id + " 更新token");
                                synchronized (session) {
                                    TokenUtil.getNewToken(session, aoPtokens);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        authenticationThreadReader.start();
    }

    public static void conloseSender(Map<String, Session> onlineSessions){
        Thread conloseSender = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(!onlineSessions.isEmpty()){
                        StringBuffer ss= new StringBuffer();
                        while(runServerService.getServerIsOpen() == 1){
                            StringBuffer s= new StringBuffer();
                            try {
                                File file = new File("./logs/latest.log");
                                FileInputStream f = new FileInputStream(file);
                                InputStreamReader isr = new InputStreamReader(f);
                                BufferedReader br = new BufferedReader(isr);
                                String line = null;
                                while((line = br.readLine())!=null){
                                    s.append(line+"\n");
                                }
                                br.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                s.append("");
                            }
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("console", s.toString());
                            if (!ss.toString().equals(s.toString())) {
                                ss.setLength(0);
                                ss.append(s);
                                sendMessageToAll(jsonObject.toJSONString(), onlineSessions);
                            }
                        }
                    }
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        conloseSender.start();
    }

    private static void sendMessageToAll(String msg, Map<String, Session> onlineSessions) {
        onlineSessions.forEach((id, session) -> {
            try {
                synchronized(session){
                    session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
