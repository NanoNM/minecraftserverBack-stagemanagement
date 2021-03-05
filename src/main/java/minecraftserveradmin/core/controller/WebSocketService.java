package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.AOPtoken;
import minecraftserveradmin.core.entity.OlineUserModel;
import minecraftserveradmin.core.services.SocketHandlerServices;
import minecraftserveradmin.core.services.impl.AdminSocketImpl;
import minecraftserveradmin.core.services.impl.UserAdministeredImpl;
import minecraftserveradmin.core.util.LogUtil;


import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
@ServerEndpoint("/admin/tcpServer/{userId}")//标记此类为服务端
public class WebSocketService {

    static AdminSocketImpl adminSocketImpl;

    @Autowired
    SocketHandlerServices socketHandlerServices;

    static UserAdministeredImpl userAdministered;

//    @Autowired
//    AdminSocketImpl adminSocket;
    @Value("${server.serverInfoSenderTimer}")
    String serverInfoSender;

    @Autowired
    public void setAdminSocketImpl(AdminSocketImpl adminSocketImpl) {
        WebSocketService.adminSocketImpl = adminSocketImpl;
    }

//    public void setChatService(AdminSocketImpl adminSocketImpl) {
//        WebSocketService.adminSocketImpl = adminSocketImpl;
//    }
    //线程安全list
    public static final List<AOPtoken> AOPtokens = new CopyOnWriteArrayList<>();

    public static final List<AOPtoken> resBeatToken = new CopyOnWriteArrayList<>();


    /**
     * 全部在线会话  PS: 基于场景考虑 这里使用线程安全的Map存储会话对象。
     */
    public static final Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    @PostConstruct
    void serverInit(){
        AdminSocketImpl.serverInfoSender = serverInfoSender;
        LogUtil.log.info("socket服务启动");
        LogUtil.log.info("权限验证系统启动");
        AdminSocketImpl.authenticationThreadReader(AOPtokens, onlineSessions);
//        LogUtil.log.info("控制台信息发送线程启动");
//        AdminSocketImpl.conloseSender(onlineSessions);
        LogUtil.log.info("mc服务器状态发送线程启动");
        AdminSocketImpl.mcServerStateSender(onlineSessions);
        LogUtil.log.info("系统信息发送线程启动");
        AdminSocketImpl.ServerInfoSender(onlineSessions);
        heartBeat();
    }
    /**
     * 当客户端打开连接：1.添加会话对象 2.更新在线人数
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        try {
//            if ("233".equals(userId)){
//                LogUtil.log.warn("测试用户登录: " + userId);
//                onlineSessions.put(session.getId(), session);
//                adminSocketImpl.sendStatusToConnect(session);
//                return;
//            }
            if (UserAdministeredImpl.onlineadmin.size() > 0) {
                for (OlineUserModel s : UserAdministeredImpl.onlineadmin) {
                    if (s.getUserID().equals(userId)) {
                        if (s.getSession() != null){
                            LogUtil.log.warn("有一个重复的用户请求登录名为: " + userId);
                            session.close();
                            return;
                        }
                        s.setSession(session);
                        onlineSessions.put(session.getId(), session);
                        try {
                            adminSocketImpl.sendStatusToConnect(session);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                }
            }
            LogUtil.log.warn("有一个未登录的用户请求登录名为: " + userId);
            session.close();
        }catch (Exception e){
            e.printStackTrace();
        }
//        session.getBasicRemote().sendText("{\"serverInfo\":\"" + ErrorCode.USER_NOT_ONLINE + "\"}");
    }

    /**
     * 当客户端发送消息：1.获取它的用户名和消息 2.发送消息给所有人
     * <p>
     * PS: 这里约定传递的消息为JSON字符串 方便传递更多参数！
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) throws IOException {
        JSONObject jb = JSONObject.parseObject(jsonStr);
        if (jb == null)
            return;
        String name = jb.getString("value");
        if ("name".equals(jb.getString("name"))){
//            if(){
//                LogUtil.log.warn("有一个未登录的用户请求登录名为: " + name);
//                session.close();
//            }
            if (AOPtokens.size()>0){
                for (AOPtoken aoPtoken : AOPtokens) {
                    if(aoPtoken.getToken().equals(jb.getString("token"))){
                        if (aoPtoken.getName()==null){
                            aoPtoken.setName(jb.getString("value"));
                        }
                    }
                }
            }
        } else if (BaseTokenVerification(session, jsonStr)){
            String returnstr = adminSocketImpl.message(session, jsonStr, onlineSessions, AOPtokens);
            TokenUtil.getNewToken(session, AOPtokens);
        }else if("verification".equals(jb.getString("name"))){

        }
        else{
            LogUtil.log.warn("有一个token检查未通过的ws申请");
        }

    }

    /**
     * 当关闭连接：1.移除会话对象 2.更新在线人数
     */
    @OnClose
    public void onClose(Session session) {

        for (AOPtoken aoptoken : AOPtokens){
            if (session.getId().equals(aoptoken.getSession().getId())){
                int a = SocketHandlerServices.proxy.UserDao.deleteTokenByName(aoptoken.getName());
            }
//
        }
        onlineSessions.remove(session.getId());
        AOPtokens.removeIf(SessionId -> SessionId.getSession().getId().equals(session.getId()));
        onlineSessions.remove(session.getId());
        UserAdministeredImpl.onlineadmin.removeIf(SessionID -> SessionID.getSession().getId().equals((session.getId())));
        LogUtil.log.info("客户端: "+ session.getId() +" 退出");
    }

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 暂不使用
     */
    private static void heartBeat(){
        Thread threadSender = new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.log.info("heartBeat running");
                Map<String,Session> beatMap = new ConcurrentHashMap<>();
                while(true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (onlineSessions.size() > 0){
                        for (String key : onlineSessions.keySet()) {
                            Session session = onlineSessions.get(key);
                            try {
                                String randomKey = TokenUtil.getRandomString();
                                beatMap.put(randomKey, session);
                                LogUtil.log.info("heartBeat to " + onlineSessions.get(key).getId());
                                session.getBasicRemote().sendText("{\"heartBeat\":\""+ randomKey +"\"}");
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean BaseTokenVerification(Session session, String jsonStr){
        JSONObject jb = JSONObject.parseObject(jsonStr);
        for (AOPtoken aoPtoken : AOPtokens) {
            if (aoPtoken.getSession().getId().equals(session.getId())) {
                if (aoPtoken.getToken().equals(jb.getString("token"))) {
                    return true;
                }
            }
        }
        return false;
    }
}