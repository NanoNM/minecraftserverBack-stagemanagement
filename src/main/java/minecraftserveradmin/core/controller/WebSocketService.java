package minecraftserveradmin.core.controller;

import com.alibaba.fastjson.JSONObject;
import minecraftserveradmin.core.dao.UserDao;
import minecraftserveradmin.core.entity.AOPtoken;
import minecraftserveradmin.core.services.impl.AdminSocketImpl;
import minecraftserveradmin.core.util.ErrorCode;
import minecraftserveradmin.core.util.LogUtil;


import minecraftserveradmin.core.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component
@ServerEndpoint("/admin/tcpServer")//标记此类为服务端
public class WebSocketService {
    @Autowired
    static AdminSocketImpl adminSocketImpl;

    @Autowired
    public void setAdminSocketImpl(AdminSocketImpl adminSocketImpl) {
        WebSocketService.adminSocketImpl = adminSocketImpl;
    }

//    public void setChatService(AdminSocketImpl adminSocketImpl) {
//        WebSocketService.adminSocketImpl = adminSocketImpl;
//    }
    //线程安全list
    private static final List<AOPtoken> AOPtokens = new CopyOnWriteArrayList<>();

    private static final List<AOPtoken> resBeatToken = new CopyOnWriteArrayList<>();

    /**
     * 全部在线会话  PS: 基于场景考虑 这里使用线程安全的Map存储会话对象。
     */
    private static final Map<String, Session> onlineSessions = new ConcurrentHashMap<>();


    static{
        LogUtil.log.info("============= socket服务启动 =================");
        LogUtil.log.info("============= 权限验证系统启动 ==============");
        AdminSocketImpl.authenticationThreadReader(AOPtokens, onlineSessions);
        LogUtil.log.info("============= 控制台信息发送线程启动 ==============");
        AdminSocketImpl.conloseSender(onlineSessions);
        heartBeat();
    }

    /**
     * 当客户端打开连接：1.添加会话对象 2.更新在线人数
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
//        System.out.println(session.getBasicRemote().);
        onlineSessions.put(session.getId(), session);
//        session.getBasicRemote().sendText("{\"serverInfo\":\"" + ErrorCode.USER_NOT_ONLINE + "\"}");
    }

    /**
     * 当客户端发送消息：1.获取它的用户名和消息 2.发送消息给所有人
     * <p>
     * PS: 这里约定传递的消息为JSON字符串 方便传递更多参数！
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        if (BaseTokenVerification(session, jsonStr)){
            String returnstr = adminSocketImpl.message(session, jsonStr, onlineSessions, AOPtokens);
            TokenUtil.getNewToken(session, AOPtokens);
        }else{
            LogUtil.log.warn("有一个token检查未通过的wss申请");
        }

    }



    /**
     * 当关闭连接：1.移除会话对象 2.更新在线人数
     */
    @OnClose
    public void onClose(Session session) {
        onlineSessions.remove(session.getId());
        AOPtokens.removeIf(SessionId -> SessionId.getSessionID().equals(session.getId()));
//        if (AOPtokens.isEmpty()){
//            LogUtil.log.info("所有管理员均不在线 List重置");
//            AOPtokens = new ArrayList<>();
//        }
        LogUtil.log.info("============= 客户端: "+ session.getId() +" 退出 ==============");
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
//        threadSender.start();
    }

    private boolean BaseTokenVerification(Session session, String jsonStr){
        JSONObject jb = JSONObject.parseObject(jsonStr);
        for (AOPtoken aoPtoken : AOPtokens) {
            if (aoPtoken.getSessionID().equals(session.getId())) {
                if (aoPtoken.getToken().equals(jb.getString("token"))) {
                    return true;
                }
            }
        }
        return false;
    }
}