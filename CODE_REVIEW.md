# 代码扫描报告

> 扫描时间：2026-10-20
> 扫描范围：`src/main/java`、`src/main/resources`、`sql.sql`
> 说明：已排除此前已修复项（`ServerInfoSender` 重复发送、`setModel()` 循环内调用、`/userloginout` 未鉴权、登录失败限制、`autologin` 累积）

---

## 🔴 严重

### 1. 管理员登录后 WS 关闭会误删 autologin 记录

**位置**：`UserAdministeredImpl.java:141-144`、`WebSocketService.java:172`

管理员登录写入的是 `connect_token` 列：

```java
Cookie ConnectCookie = tokenUtil.getAutoLoginToken();   // cookie 名 "token"
response.addCookie(ConnectCookie);
userDao.deleteTokenByName(userModel.getUser_name());
userDao.insertConnect(userModel.getUser_name(), ConnectCookie.getValue());
// → insert into autologin(name,token,connect_token) values(#{name},'null',#{token})
```

而 WS 关闭时：

```java
for (AOPtoken aoptoken : AOPtokens){
    if (session.getId().equals(aoptoken.getSession().getId())){
        int a = SocketHandlerServices.proxy.UserDao.deleteTokenByName(aoptoken.getName());
    }
}
```

**问题**：

- `AOPtoken.name` 在 token 绑定前为 `null`，`deleteTokenByName(null)` 执行 `delete from autologin where name=null`，无意义；
- WS 断开即删除该用户全部 autologin 记录，导致 `connect_token` cookie 失效，`/userloginout` 反查失败。

**建议**：`onClose` 中先判空，且只删除与当前 session 关联的记录（按 token 精确删除）。

---

### 2. 拦截器 NPE 未修

**位置**：`AdminOperationIntercept.java:33`

```java
if(aoPtoken.getName().equals(name)){
```

`AOPtoken.name` 在 `WebSocketService.onMessage` 绑定前为 `null`。只要 `AOPtokens` 中存在未绑定用户名的条目（WS 刚连上、尚未发 `name` 消息），任何 `/admin/**` 请求都会抛 NPE → 500。

**建议**：

```java
if (name != null && name.equals(aoPtoken.getName())) {
```

---

### 3. 调试后门仍在

**位置**：`AdminOperationIntercept.java:28-30`

```java
// 为了方便调试
if ("233".equals(name) && "233".equals(token)){
    return true;
}
```

任何人可用 `name=233&token=233` 绕过全部鉴权。

**建议**：改为配置开关并默认关闭：

```java
@Value("${MCM.debug.bypass-auth:false}")
private boolean debugBypassAuth;

if (debugBypassAuth && "233".equals(name) && "233".equals(token)) {
    return true;
}
```

---

### 4. 命令注入

**位置**：`AdminSocketImpl.java:81-84`

```java
String cmd = jb.getString("startservercmd");
if (cmd!=null) StaticDataUtil.cmd += cmd;
runServerService.doCom(jb.getString("value"));
```

`StaticDataUtil.cmd` 最终传给 `Runtime.getRuntime().exec(...)`，客户端可拼接任意系统命令。

**建议**：启动命令改为固定模板 + 参数白名单，禁止拼接客户端字符串。

---

### 5. 文件接口路径穿越

**位置**：`FileController.java`、`NewFIleOperationImpl.java`

`path` 参数无校验，直接 `new File(path)`，可越出项目目录读写/删除宿主机任意文件。

**建议**：统一做 `Path.normalize()` + 白名单根目录校验，拒绝 `..` 与绝对路径。

---

## 🟠 高

### 6. `/admin/getServerInfo` 不存在，前端调用 404

**位置**：`MainPageController.java:31-33`（已注释）、`dashboard.06bf34a6.js`

前端调用 `/admin/getServerInfo`，但该映射已被注释，全项目无此端点。系统信息实际靠 WS 推送，HTTP 接口一直失败。

**建议**：删除前端调用，或恢复该端点。

---

### 7. `WebConfigurer` 未加 `@Configuration`，路径缺前导斜杠

**位置**：`WebConfigurer.java:11`

```java
public class WebConfigurer implements WebMvcConfigurer {   // 缺 @Configuration
    registry.addInterceptor(adminOperationIntercept).addPathPatterns("admin/**");  // 缺 /
```

该类不生效，且与 `ConfigAdapter` 功能重复。

**建议**：删除 `WebConfigurer`，保留 `ConfigAdapter`。

---

### 8. `excludePathPatterns("/register")` 是死配置

**位置**：`ConfigAdapter.java:26`

实际映射是 `/admin/register`，不存在 `/register`，该排除项无意义。

---

### 9. `@Param`（MyBatis）误用于 Spring MVC 参数

**位置**：`ServerController.java:34`、`PlayerController.java`、`ModSettingContorller.java`

`@Param` 是 MyBatis 注解，Spring MVC 不识别，参数无法绑定。

**建议**：改为 `@RequestParam`。

---

### 10. `new XxxService()` 绕过 Spring 容器

**位置**：`AdminSocketImpl.java:29-31`、`RunServerService.java:32`、`TestOS.java`、`FIleOperationImpl.java`

```java
private static final RunServerService runServerService = new RunServerService();
private static final GetServerInfoService getServerInfoService = new GetServerInfoService();
```

导致 `@Autowired` 字段为 null，且**服务器状态多实例不一致**（`AdminSocketImpl` 的静态实例 vs `ServerController` 注入的 Bean）。

**建议**：统一由 Spring 管理，通过 `ApplicationContextUtil` 或构造注入获取。

---

### 11. `RunServerService.serverIsOpen` 非线程安全

**位置**：`RunServerService.java:27`

```java
private Integer serverIsOpen = 0;
```

被 WS 线程写、`mcServerStateSender` 线程读，无 `volatile`/同步。

**建议**：改为 `private volatile int serverIsOpen;` 或 `AtomicInteger`。

---

## 🟡 中

### 12. `mcServerStateSender` 重复发送两次

**位置**：`AdminSocketImpl.java:110-121`

`synchronized` 块内发一次，块外又发一次。`ServerInfoSender` 已修，此处未修。

---

### 13. `authenticationThreadReader` 与拦截器竞态

**位置**：`AdminSocketImpl.java:188`、`AdminOperationIntercept.java:35`

后台线程每秒强制轮换 token，与拦截器校验后的轮换冲突，客户端可能收到两个新 token 而用错一个，表现为偶发 401。

---

### 14. `onClose` 中 `aoptoken.getName()` 可能为 null

**位置**：`WebSocketService.java:172`

未判空即传入 DAO。

---

### 15. `heartBeat()` 定义了但从未启动

**位置**：`WebSocketService.java:82`、`WebSocketService.java:216`

`serverInit()` 调用了 `heartBeat()`，但方法内部只创建 Thread 未调用 `start()`，属死代码。

---

### 16. `LoginAttemptLimiter.purgeIfNeeded` 逻辑缺陷（内存泄漏）

**位置**：`LoginAttemptLimiter.java:113-121`

```java
attempts.entrySet().removeIf(entry -> {
    Attempt attempt = entry.getValue();
    return attempt.lockUntil != 0 && attempt.lockUntil <= now;
});
```

只清理「已锁定且已过期」的条目，**未锁定但计数中的条目永不清理**。攻击者用大量不同用户名（每个失败 1–4 次）可让 Map 无限增长。

**建议**：为 `Attempt` 增加 `lastFailureTime`，清理超过窗口期的条目。

---

### 17. `LoginAttemptLimiter` 按用户名计数，可被撞库绕过

轮换用户名即可绕过限制。如需按 IP 限制，需从 `HttpServletRequest` 取远端地址，当前 `doLogin` 签名不含 request。

---

### 18. `sql.sql` 与 `UserDao` 的 `authme_id` 不一致

**位置**：`sql.sql`、`UserDao.java:38,42`

`user` 表无 `authme_id` 列，但 `selectAllAdmin`/`selectAllUser` 都 `left join authme b on a.authme_id = b.id`，运行必报错。

**建议**：补列或修正 SQL。

---

### 19. `UserModel.getAuthme_id()` 映射错误

**位置**：`UserModel.java`

```java
public String getAuthme_id() {
    return realname;
}
```

命名与语义不符，易误导调用方。

---

### 20. `name` 参数语义重叠

**位置**：`UserController.java:46-53`、`AdminOperationIntercept.java:24`

`@RequestParam("name") String adminName` 与拦截器读取的 `name`（鉴权标识）同名，同一参数被同时用作「操作者」和「鉴权标识」，易出错。

**建议**：操作者身份改为从 token 反查，不再由客户端传参。

---

## ⚪ 低

### 21. `UserUserImpl` 未使用的字段与导入

`bindingForumWithLoginPlugin`、`logpluginName`、`logpluginTable` 三个 `@Value` 字段从未使用；`import java.util.Date` 未使用。

---

### 22. `UserAdministeredImpl` 大量未使用导入

`PostConstruct`、`Date`、`Map`、`ConcurrentHashMap` 等导入未使用。

---

### 23. `AdminSocketImpl` 未使用变量

`AdminSocketImpl.java:142` 的 `int index = -1;` 仍未使用。

---

### 24. `UserAdministeredImpl.doLogout` 永远返回 `"failure"`

**位置**：`UserAdministeredImpl.java:221-226`

```java
public String doLogout(String name){
    if (name!=null){
        userDao.deleteTokenByName(name);
    }
    return "failure";   // 逻辑错误
}
```

删除成功后仍返回 `"failure"`。

---

### 25. `UserUserImpl.doLogout` 遍历时修改集合

**位置**：`UserUserImpl.java:137-142`

在 `for` 循环中 `remove`。`CopyOnWriteArrayList` 虽不抛 `ConcurrentModificationException`，但语义上应改用 `removeIf`。

---

### 26. `doAutoLogin` 恒返回 `null`

**位置**：`UserUserImpl.java:170`、`UserAdministeredImpl.java:225`

前端 `app.f9445324.js` 启动时依赖 `/autologin` 恢复会话，但两个实现均返回 `null`，自动登录功能实际不可用。

---

### 27. `TokenUtil` 随机数熵不足

**位置**：`TokenUtil.java:19`

已改用 `SecureRandom`，但生成方式仍是：

```java
long tmp_token = random.nextInt(1000) + System.currentTimeMillis() + random.nextInt(1000);
return DigestUtils.md5DigestAsHex(String.valueOf(tmp_token).getBytes());
```

熵受限于毫秒时间戳。建议直接 `nextBytes(32)`。

---

### 28. `TokenUtil` 实例化与静态混用

**位置**：`UserAdministeredImpl.java:92`

```java
TokenUtil tokenUtil = new TokenUtil();   // 手动实例化
```

而类中同时有 `@Autowired TokenUtil tokenUtil` 字段，风格不一致。

---

### 29. Cookie 缺少 `HttpOnly` / `Secure` / `SameSite`

**位置**：`TokenUtil.java:26-29`

`token` cookie 未设置这些属性。前端 JS 需要读取，不能加 `HttpOnly`，但生产环境建议至少加 `Secure`。

---

### 30. `application.properties` 明文凭据

数据库密码、默认管理员密码明文存储。建议移至环境变量或配置中心。

---

## 优先级汇总

| 优先级 | 编号 | 理由 |
|---|---|---|
| P0 | 1、2 | 当前代码功能性回归，管理员登录后 WS/登出会异常 |
| P0 | 3、4、5 | 安全漏洞 |
| P1 | 6、10、11 | 功能不可用 / 状态不一致 |
| P1 | 16 | 内存泄漏 |
| P2 | 12、13、14、15 | 稳定性 |
| P2 | 18、24、26 | 逻辑错误 |
| P3 | 其余 | 代码质量 |

---

## 统计

| 级别 | 数量 |
|---|---|
| 🔴 严重 | 5 |
| 🟠 高 | 6 |
| 🟡 中 | 9 |
| ⚪ 低 | 10 |
| **合计** | **30** |
