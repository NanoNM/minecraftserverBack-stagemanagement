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
import java.util.concurrent.atomic.AtomicBoolean;


@Component
@ServerEndpoint("/admin/tcpServer/{userId}")//标记此类为服务端
public class WebSocketService {

    static AdminSocketImpl adminSocketImpl;

    @Autowired
    SocketHandlerServices socketHandlerServices;

    static UserAdministeredImpl userAdministered;

//    @Autowired
//    AdminSocketImpl adminSocket;
    @Value("${server.mcServerStatusSenderTimer}")
    String mcServerStatusSenderTimer;

    @Value("${server.serverInfoSenderTimer}")
    String serverInfoSenderTimer;

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

    private volatile Thread authenticationThread;
    private volatile Thread serverStatusThread;
    private volatile Thread serverInfoThread;
    private final AtomicBoolean watchdogStarted = new AtomicBoolean();

    @PostConstruct
    void serverInit(){
        AdminSocketImpl.mcServerStatusSenderTimer = mcServerStatusSenderTimer;
        AdminSocketImpl.serverInfoSenderTimer = serverInfoSenderTimer;
        LogUtil.log.info("socket服务启动");
        LogUtil.log.info("权限验证系统启动");
        authenticationThread = AdminSocketImpl.authenticationThreadReader(AOPtokens, onlineSessions);
//        LogUtil.log.info("控制台信息发送线程启动");
//        AdminSocketImpl.conloseSender(onlineSessions);
        LogUtil.log.info("mc服务器状态发送线程启动");
        serverStatusThread = AdminSocketImpl.mcServerStateSender(onlineSessions);
        LogUtil.log.info("系统信息发送线程启动");
        serverInfoThread = AdminSocketImpl.ServerInfoSender(onlineSessions);
        startThreadWatchdog();
        heartBeat();
    }

    private void startThreadWatchdog() {
        if (!watchdogStarted.compareAndSet(false, true)) {
            return;
        }
        Thread watchdog = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(5000);
                    if (!isAlive(authenticationThread)) {
                        LogUtil.log.error("token更新线程已退出，正在重启");
                        authenticationThread = AdminSocketImpl.authenticationThreadReader(AOPtokens, onlineSessions);
                    }
                    if (!isAlive(serverStatusThread)) {
                        LogUtil.log.error("服务器状态推送线程已退出，正在重启");
                        serverStatusThread = AdminSocketImpl.mcServerStateSender(onlineSessions);
                    }
                    if (!isAlive(serverInfoThread)) {
                        LogUtil.log.error("系统信息推送线程已退出，正在重启");
                        serverInfoThread = AdminSocketImpl.ServerInfoSender(onlineSessions);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (RuntimeException e) {
                    LogUtil.log.error("后台线程守护检查失败: " + e.getMessage(), e);
                }
            }
        }, "server-thread-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
    }

    private boolean isAlive(Thread thread) {
        return thread != null && thread.isAlive();
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
                        // 已有旧会话时踢掉旧的，允许新连接顶替
                        if (s.getSession() != null){
                            String oldSessionId = s.getSession().getId();
                            LogUtil.log.warn("用户 " + userId + " 重复连接，踢掉旧会话: " + oldSessionId);
                            onlineSessions.remove(oldSessionId);
                            AOPtokens.removeIf(token -> token.getSession() != null
                                    && oldSessionId.equals(token.getSession().getId()));
                            try {
                                s.getSession().close();
                            } catch (IOException e) {
                                LogUtil.log.error("关闭旧会话失败: " + e.getMessage());
                            }
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
     * <p>
     * 消息按 {@code name} 字段分为三类，处理逻辑如下：
     * <ol>
     *   <li>{@code name = "name"}：客户端首次上报身份。遍历 {@link #AOPtokens}，
     *       找到 token 匹配且尚未绑定用户名的条目，把 {@code value} 作为用户名绑定上去。
     *       这是后续所有鉴权的前置步骤。</li>
     *   <li>{@code name = "verification"}：心跳/校验消息，当前不做任何处理。</li>
     *   <li>其他：先经 {@link #BaseTokenVerification} 校验 token，
     *       通过后交给 {@link AdminSocketImpl#message} 执行业务指令，
     *       并立即轮换新 token（旧 token 失效，保证一次性使用）。</li>
     * </ol>
     * 校验未通过时仅记录告警日志，不回复客户端。
     *
     * @param session 当前 WebSocket 会话
     * @param jsonStr 客户端发来的 JSON 字符串，约定包含 name、value、token 等字段
     * @throws IOException 发送消息失败时抛出
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) throws IOException {
        JSONObject jb = JSONObject.parseObject(jsonStr);
        // 非法 JSON，直接忽略
        if (jb == null)
            return;
        String name = jb.getString("value");
        // 分支一：客户端上报身份，把用户名绑定到对应的 token 条目上
        if ("name".equals(jb.getString("name"))){
//            if(){
//                LogUtil.log.warn("有一个未登录的用户请求登录名为: " + name);
//                session.close();
//            }
            if (AOPtokens.size()>0){
                for (AOPtoken aoPtoken : AOPtokens) {
                    if(aoPtoken.getToken().equals(jb.getString("token"))){
                        // 仅在尚未绑定时写入，避免被后续消息覆盖
                        if (aoPtoken.getName()==null){
                            aoPtoken.setName(jb.getString("value"));
                        }
                    }
                }
            }
        // 分支二：token 校验通过，执行业务指令并轮换 token
        } else if (BaseTokenVerification(session, jsonStr)){
            String returnstr = adminSocketImpl.message(session, jsonStr, onlineSessions, AOPtokens);
            TokenUtil.getNewToken(session, AOPtokens);
        // 分支三：心跳/校验消息，暂不处理
        }else if("verification".equals(jb.getString("name"))){

        }
        // 分支四：token 校验未通过
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