# Minecraft Server Manager（后端）

Minecraft 服务端管理面板后端，基于 Spring Boot 的前后端分离架构，数据以 JSON 传输。
绝大多数管理操作通过 WebSocket 完成，HTTP 接口主要用于登录、文件管理、Mod/插件管理与配置读写。

- 前端仓库：<https://github.com/NanoNM/Mine>
- 默认端口：`25567`

---

## 技术栈

| 类别 | 组件 |
|---|---|
| 框架 | Spring Boot 2.6.3、Spring MVC、Spring WebSocket |
| 模板 | Thymeleaf |
| 持久层 | MyBatis（注解式 Mapper）+ MySQL |
| 日志 | Log4j2 |
| 系统监控 | OSHI 6.1.1 |
| JSON | fastjson 1.2.62、Gson |
| 其他 | snakeyaml、OkHttp、JNA（Windows 取 PID） |
| JDK | 编译目标 1.8（实际开发使用 JDK 17） |

---

## 快速开始

### 1. 准备数据库

执行 [sql.sql](./sql.sql) 创建 `mine` 库及相关表：

```bash
mysql -u root -p < sql.sql
```

### 2. 修改配置

编辑 [application.properties](./src/main/resources/application.properties)：

```properties
spring.datasource.username=root
spring.datasource.password=你的密码
spring.datasource.url=jdbc:mysql://localhost:3306/mine?serverTimezone=GMT
```

### 3. 启动

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

或打包后运行：

```bash
mvnw.cmd clean package -DskipTests
java -jar target/core-Mine 0.1.jar
```

### 4. 首次登录

首次启动时若库中无管理员，`DefaultAdministratorService` 会自动创建：

- 用户名：`MCM.Default.administrator.username`（默认 `admin`）
- 密码：若配置为 `admin` 则随机生成，**请查看启动日志**

---

## 配置说明

| 配置项 | 默认值 | 说明 |
|---|---|---|
| `server.port` | `25567` | 服务端口 |
| `spring.servlet.multipart.max-file-size` | `20MB` | 单文件上传上限 |
| `spring.servlet.multipart.max-request-size` | `20MB` | 单次请求上限 |
| `minecraft.startservercmd` | `java -jar server.jar nogui` | MC 服务端启动命令 |
| `minecraft.java.path` | `java` | 启动 MC 服务端的 Java 可执行文件；可填写绝对路径，例如 `C:/Program Files/Java/jdk-17/bin/java.exe` |
| `minecraft.jvm-args-file` | `user_jvm_args.txt` | Java 启动参数文件路径，相对路径以管理面板进程的工作目录为基准 |
| `server.mcServerStatusSenderTimer` | `1000` | MC 运行状态推送间隔（ms） |
| `server.serverInfoSenderTimer` | `4000` | 系统信息推送间隔（ms） |
| `MCM.Default.administrator.username` | `admin` | 默认管理员用户名 |
| `MCM.Default.administrator.password` | `admin` | 默认管理员密码（为 `admin` 时随机生成） |
| `MCM.Minecraft.BindingForumWithLoginPlugin` | `true` | 是否与登录插件（authme）绑定 |
| `MCM.Minecraft.logplugin.name` | `authme` | 登录插件名 |
| `MCM.Minecraft.logplugin.table` | `authme` | 登录插件表名 |
| `MCM.login.maxAttempts` | `5` | 登录连续失败上限 |
| `MCM.login.lockMinutes` | `15` | 达到上限后的锁定分钟数 |

Minecraft JVM 参数（如 `-Xms`、`-Xmx`）写入工作目录下的 `user_jvm_args.txt`，启动时由 Java 参数文件加载。此功能要求 Minecraft 使用的 Java 为 9 或更高版本（建议 17 或 21）；如果启动日志显示 `Could not find or load main class @...user_jvm_args.txt`，请检查 `minecraft.java.path` 是否指向支持参数文件的 Java。jar 文件名及服务端参数由启动指令传入，例如 `server.jar nogui`（不要在该字段中重复填写 `java -jar`）。指定自定义 Java 安装路径时，将 `minecraft.java.path` 设置为该 Java 可执行文件的路径。

---

## 鉴权机制

### HTTP 接口

`/admin/**` 由 `AdminOperationIntercept` 拦截，请求需携带：

```
?name=<用户名>&token=<token>
```

token 由 WebSocket 下发，**一次性使用**：校验通过后立即轮换，旧 token 失效。

### WebSocket

端点：`ws://<host>:25567/admin/tcpServer/{userId}`

连接建立后服务端下发 `Authentication` 消息，客户端需回发身份：

```json
{"name":"name","value":"<用户名>","token":"<token>"}
```

之后每条业务消息都需携带当前 token，服务端校验通过后轮换新 token。

### 登录失败限制

连续失败达到 `MCM.login.maxAttempts` 次后锁定 `MCM.login.lockMinutes` 分钟，锁定期间直接返回 `LOGIN_LOCKED`（3032），不查库、不比对密码。登录成功后计数清零。

### 会话管理

- 同一账号重复登录时，**新登录会踢掉旧会话**（含 WebSocket 连接）。
- 每次登录会清理该账号旧的 `autologin` 记录，保证只保留最新一次登录。

---

## 接口一览

### 用户与登录

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/login` | 普通用户登录 |
| POST | `/admin/login` | 管理员登录（JSON body） |
| GET | `/autologin` | 自动登录（当前未启用） |
| POST | `/userloginout` | 登出，身份由 cookie 反查 |
| GET | `/admin/getalladmin` | 分页查询管理员 |
| GET | `/admin/getalluser` | 分页查询普通用户 |
| GET | `/admin/suchuserbyname` | 按用户名查询 |
| POST | `/admin/modifypassword` | 修改密码 |
| GET | `/admin/deleteAdministrator` | 删除管理员 |
| POST | `/admin/register` | 注册管理员 |
| POST | `/admin/registeruser` | 注册普通用户 |

### 服务器

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/getjar` | 列出可用的服务端 jar |
| GET | `/admin/getsetting` | 读取 `server.properties` |
| GET | `/admin/setsetting` | 写入 `server.properties` |
| GET | `/admin/synchronizationConsole` | 获取控制台日志（Base64 + GZIP） |
| GET | `/admin/server/s` | 已废弃 |
| GET | `/admin/server` | 已废弃 |
| GET | `/admin/serverTest` | 已废弃 |

### 文件管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/file` | 列出目录（不传 `path` 为根目录） |
| GET | `/admin/renameFile` | 重命名 |
| GET | `/admin/download` | 下载文件 |
| POST | `/admin/fileeditor` | 编辑文件内容 |
| GET | `/admin/filedelete` | 删除文件/目录 |
| GET | `/admin/mkdir` | 新建目录 |
| GET | `/admin/makefile` | 新建文件 |
| GET | `/admin/filecopy` | 复制 |
| GET | `/admin/filemove` | 移动 |

### Mod 与插件

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/modScanning` | 扫描 Mod，支持 `able`/`disable`/`remove`/`completelyDelete` |
| GET | `/admin/moremodinfo` | 读取 `mcmod.info` |
| POST | `/admin/upfilemods` | 上传 Mod |
| GET | `/admin/pluginScanning` | 扫描插件，操作同上 |
| GET | `/admin/moreplugininfo` | 读取 `plugin.yml` |
| POST | `/admin/upfileplugins` | 上传插件 |

### 其他

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/` | 首页 |
| GET | `/errorcode` | 获取全部错误码 |

---

## 详细 HTTP 接口文档

### 1. 通用约定

- 基础地址：`http://<host>:25567`
- 所有 `/admin/**` 接口都由 `AdminOperationIntercept` 进行鉴权校验；除了 `OPTIONS`，必须在 URL 中附带：

```http
GET /admin/getalladmin?page=1&name=admin&token=<token>
```

- `token` 是 WebSocket 登录成功后下发的临时令牌，校验通过后会立即轮换，旧 token 失效。
- 仅少数接口使用 `application/json`，多数接口采用表单参数或查询参数，返回值可能是原始对象、数组、字符串或状态码。
- 业务状态码统一由 `ErrorCode` 定义，成功值多为 `1000+`，失败值为 `3000+`，调用 `/errorcode` 可获取完整映射。

### 2. 用户与登录

#### `POST /login`

普通用户登录。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | string | 是 | 用户名 |
| `passwd` | string | 是 | 密码 |
| `isautologin` | string | 是 | 是否自动登录，通常传 `true`/`false` |

返回：`UserLoginModel`

```json
{
  "userModel": {
    "ID": 1,
    "user_name": "alice",
    "email": "alice@example.com",
    "authority": "user"
  },
  "code": 1000,
  "connect_token": "<token>"
}
```

#### `POST /admin/login`

管理员登录，`requestBody` 为 JSON：

```json
{
  "username": "admin",
  "password": "123456"
}
```

返回：`UserLoginModel`，成功后可拿到 `connect_token`，后续需通过 WebSocket 绑定身份。

#### `GET /autologin?token=...`

自动登录接口（当前未正式启用）。

#### `POST /userloginout`

退出登录。服务端通过 cookie 中的 `token` / `connect_token` 反查用户名，不信任客户端传入的用户名。

Cookie：

- `token`
- `connect_token`

返回：

- `failure`：未找到有效登录态
- 其他：业务层退出处理结果

#### `GET /admin/getalladmin?page=1`

分页查询管理员列表。

返回：`UserModel[]`

#### `GET /admin/getalluser?page=1`

分页查询普通用户列表。

#### `GET /admin/suchuserbyname?suchname=alice`

按用户名搜索用户。

返回：`UserModel[]`

#### `POST /admin/modifypassword`

修改密码。

请求参数：

| 参数 | 说明 |
|---|---|
| `newps` | 新密码 |
| `username` | 目标用户名 |

返回：`Integer`，成功时返回 `USER_CHANGE_PASSWORD_SUCCESS`（1012），失败返回 `USER_CHANGE_PASSWORD_FAIL`（3030）。

#### `GET /admin/deleteAdministrator?name=<adminName>&username=<targetUser>`

删除管理员账号。

返回：`Integer`，成功返回 `DELETE_ADMINUSER_SUCCESS`（1013），失败返回 `DELETE_ADMINUSER_FAIL`（3031）。

#### `POST /admin/register`

注册管理员。

表单参数：

| 参数 | 说明 |
|---|---|
| `name` | 当前操作管理员用户名 |
| `regname` | 新管理员用户名 |
| `passwd` | 密码 |
| `email` | 邮箱 |

返回：`Integer`，成功返回 `ADMIN_REG_SUCCESS`（1011）。

#### `POST /admin/registeruser`

注册普通用户。

参数与 `/admin/register` 一致，返回 `USER_REG_SUCCESS`（1014）。

---

### 3. 服务器管理

#### `GET /admin/getjar`

列出服务器可用的 jar 包文件名。

返回：`List<String>`

#### `GET /admin/getsetting`

读取当前 `server.properties` 配置。

返回：JSON 字符串，格式为 `server.properties` 内容解析后的文本/结构化字符串。

#### `GET /admin/setsetting?jsonstr=...`

写入 `server.properties`。

请求参数：

| 参数 | 说明 |
|---|---|
| `jsonstr` | 需要写入的 JSON 配置串 |

返回：`boolean`，成功 `true`，失败 `false`。

#### `GET /admin/synchronizationConsole`

获取服务器控制台日志。

返回：字符串内容；实际实现中可包含日志输出或者压缩/编码后的文本，需按服务端返回值处理。

#### 已废弃接口

- `GET /admin/server/s`
- `GET /admin/server`
- `GET /admin/serverTest`

这些接口当前仅返回字符串 `"接口废弃"`，前端不应继续依赖。

---

### 4. 文件管理

所有文件管理接口均位于 `/admin`，且需要先完成鉴权。

#### `GET /admin/file?path=<目录路径>`

列出目录内容。若不传 `path`，默认列出根目录。

返回：`List`，每个元素为文件/目录信息。

#### `GET /admin/renameFile?path=<旧路径>&rename=<新名称>`

重命名文件或目录。

返回：`Integer` 错误码，成功 `FILE_RENAME_SUCCESS`（1003），失败 `FILE_RENAME_ERROR`（3008）。

#### `GET /admin/download?path=<文件路径>`

下载文件。

返回：二进制文件流。此接口通过 `HttpServletResponse` 直接输出内容。

#### `POST /admin/fileeditor`

编辑文件内容。

表单参数：

| 参数 | 说明 |
|---|---|
| `path` | 文件路径 |
| `string` | 新文件内容 |

返回：`Integer`，成功 `FILE_EDIT_SUCCES`（1005），失败 `FILE_EDIT_FAIL`（3010）。

#### `GET /admin/filedelete?path=<路径>`

删除文件或目录。

返回：`Integer`，成功 `FILE_REMOVE_SUCCES`（1006），失败 `FILE_REMOVE_FILE`（3011）。

#### `GET /admin/mkdir?path=<目录路径>`

新建目录。

返回：`Integer`，成功 `FOLDER_CREATED_SUCCESSFULLY`（1007），失败 `FOLDER_CREATED_FAIL`（3013）。

#### `GET /admin/makefile?path=<文件路径>`

新建文件。

返回：`Integer`，成功 `FILE_CREATED_SUCCESSFULLY`（1008），失败 `FILE_CREATED_FAIL`（3015）。

#### `GET /admin/filecopy?source=<源路径>&dest=<目标路径>`

复制文件或目录。

返回：`Integer`，成功 `FILE_COPY_SUCCESS`（1009），失败 `FILE_COPY_FAIL`（3016）。

#### `GET /admin/filemove?source=<源路径>&dest=<目标路径>`

移动文件或目录。

返回：`Integer`，成功 `FILE_MOVE_SUCCESS`（1010），失败 `FILE_MOVE_FAIL`（3017）。

---

### 5. Mod 与插件管理

#### `GET /admin/modScanning?name=<管理员名>&cmd=<操作>&filename=<文件名>`

扫描 Mod 列表、启用/禁用/移除/彻底删除。

`cmd` 可选值：

- `able`：启用
- `disable`：禁用
- `remove`：移除
- `completelyDelete`：彻底删除
- 不传 `cmd`：返回列表

返回：`List`（扫描结果列表）

#### `GET /admin/moremodinfo?filename=<mod文件名>`

读取 Mod 元数据，如 `mcmod.info`。

返回：`String`

#### `POST /admin/upfilemods`

上传 Mod 文件。

表单参数：

| 参数 | 说明 |
|---|---|
| `file` | Multipart 文件 |

返回：`Integer`，成功 `FILE_UPDATE_SUCCESS`（1002），失败 `NO_FILE_UPDATE`（3007）。

#### `GET /admin/pluginScanning?name=<管理员名>&cmd=<操作>&filename=<插件文件名>`

扫描插件列表，逻辑与 Mod 一致。

#### `GET /admin/moreplugininfo?filename=<插件文件名>`

读取插件配置元数据，如 `plugin.yml`。

返回：`Map<String, Object>`

#### `POST /admin/upfileplugins`

上传插件文件，参数同 `upfilemods`。

---

### 6. 错误码

`GET /errorcode` 返回所有错误码定义，示例：

```json
{
  "info": "errorCode",
  "codes": {
    "LOGIN_SUCCESS": 1000,
    "REGISTER_SUCCESS": 1001,
    "NO_USER_FIND": 3003,
    "WRONG_PASSWORD": 3004,
    "LOGIN_LOCKED": 3032
  }
}
```

关键错误码：

| 名称 | 值 | 含义 |
|---|---:|---|
| `LOGIN_SUCCESS` | `1000` | 登录成功 |
| `FILE_UPDATE_SUCCESS` | `1002` | 文件上传成功 |
| `USER_CHANGE_PASSWORD_SUCCESS` | `1012` | 修改密码成功 |
| `NO_USER_FIND` | `3003` | 用户不存在 |
| `WRONG_PASSWORD` | `3004` | 密码错误 |
| `NO_FILE_UPDATE` | `3007` | 上传失败/为空 |
| `LOGIN_LOCKED` | `3032` | 账户已被锁定 |

---

## WebSocket 消息格式

统一使用 `SocketResult` 结构：

```json
{
  "name": "消息类型",
  "message": "附加信息",
  "object": "数据"
}
```

| `name` | 方向 | 说明 |
|---|---|---|
| `Authentication` | 服务端 → 客户端 | 下发新 token |
| `ServerStat` | 服务端 → 客户端 | MC 服务端运行状态（0 停止 / 1 运行） |
| `systemInfo` | 服务端 → 客户端 | CPU、内存、运行时长等 |
| `console` | 服务端 → 客户端 | MC 控制台输出 |
| `ERROR` | 服务端 → 客户端 | 错误信息 |

客户端 → 服务端：

```json
{"name":"cmd","value":"startserver","username":"admin","token":"xxx"}
{"name":"cmd","value":"stop","username":"admin","token":"xxx"}
{"name":"cmd","value":"hardstop","username":"admin","token":"xxx"}
```

---

## 项目结构

```
src/main/java/minecraftserveradmin/core
├── MainecraftApplication.java      启动类
├── config/
│   ├── CorsConfig.java             跨域配置
│   ├── WebSocketConfig.java        WebSocket 端点导出
│   └── intercepors/
│       ├── AdminOperationIntercept.java   /admin/** 鉴权拦截器
│       └── ConfigAdapter.java             拦截器注册
├── controller/
│   ├── UserController.java         用户与登录
│   ├── ServerController.java       服务器配置与控制台
│   ├── FileController.java         文件管理
│   ├── ModSettingContorller.java   Mod 管理
│   ├── PluginsSettingController.java 插件管理
│   ├── PlayerController.java       玩家信息（未完成）
│   ├── MainPageController.java     首页与错误码
│   └── WebSocketService.java       WebSocket 端点
├── dao/
│   └── UserDao.java                MyBatis 注解 Mapper
├── dto/
│   ├── ResultBody.java             HTTP 统一返回体
│   └── SocketResult.java           WebSocket 统一返回体
├── entity/                         数据模型
├── exception/
│   └── ExceptionController.java    全局异常处理
├── services/
│   ├── RunServerService.java       MC 进程启动与指令转发
│   ├── GetServerInfoService.java   系统信息采集（OSHI）
│   ├── FormatServerSettingService.java  server.properties 读写
│   ├── GetModListService.java      Mod 扫描
│   ├── GetPluginsListService.java  插件扫描
│   ├── ErrorCodeService.java       错误码导出
│   └── impl/
│       ├── AdminSocketImpl.java    WebSocket 业务处理与推送线程
│       ├── UserAdministeredImpl.java 管理员业务
│       ├── UserUserImpl.java       普通用户业务
│       ├── NewFIleOperationImpl.java 文件操作
│       ├── SaveAsModImpl.java      Mod 上传保存
│       ├── SaveAsPluginImpl.java   插件上传保存
│       └── DefaultAdministratorService.java 默认管理员初始化
└── util/
    ├── TokenUtil.java              token 生成与轮换
    ├── LoginAttemptLimiter.java    登录失败限制
    ├── ErrorCode.java              错误码常量
    ├── OshiUtil.java               系统信息工具
    ├── TimeUtil.java               时间格式化
    └── LogUtil.java                日志
```

---

## 错误码

### 成功（10xx）

| 常量 | 值 | 说明 |
|---|---|---|
| `LOGIN_SUCCESS` | 1000 | 登录成功 |
| `REGISTER_SUCCESS` | 1001 | 注册成功 |
| `FILE_UPDATE_SUCCESS` | 1002 | 文件上传成功 |
| `FILE_RENAME_SUCCESS` | 1003 | 重命名成功 |
| `FILE_DOWNLOAD_SUCCESS` | 1004 | 下载成功 |
| `FILE_EDIT_SUCCES` | 1005 | 编辑成功 |
| `FILE_REMOVE_SUCCES` | 1006 | 删除成功 |
| `FOLDER_CREATED_SUCCESSFULLY` | 1007 | 目录创建成功 |
| `FILE_CREATED_SUCCESSFULLY` | 1008 | 文件创建成功 |
| `FILE_COPY_SUCCESS` | 1009 | 复制成功 |
| `FILE_MOVE_SUCCESS` | 1010 | 移动成功 |
| `ADMIN_REG_SUCCESS` | 1011 | 管理员注册成功 |
| `USER_CHANGE_PASSWORD_SUCCESS` | 1012 | 改密成功 |
| `DELETE_ADMINUSER_SUCCESS` | 1013 | 删除管理员成功 |
| `USER_REG_SUCCESS` | 1014 | 用户注册成功 |

### 失败（30xx）

| 常量 | 值 | 说明 |
|---|---|---|
| `SAME_USER_NAME` | 3002 | 用户名已存在 |
| `NO_USER_FIND` | 3003 | 用户不存在 |
| `WRONG_PASSWORD` | 3004 | 密码错误 |
| `USER_NOT_ONLINE` | 3005 | 用户不在线 |
| `ADMIN_EARLY_ONLINE` | 3006 | 管理员已在线（已不再使用） |
| `NO_FILE_UPDATE` | 3007 | 文件未更新 |
| `FILE_RENAME_ERROR` | 3008 | 重命名失败 |
| `FILE_DOWNLOAD_ERROR` | 3009 | 下载失败 |
| `FILE_EDIT_FAIL` | 3010 | 编辑失败 |
| `FILE_REMOVE_FILE` | 3011 | 删除失败 |
| `FOLDER_ALREADY_EXISTS` | 3012 | 目录已存在 |
| `FOLDER_CREATED_FAIL` | 3013 | 目录创建失败 |
| `FILE_ALREADY_EXISTS` | 3014 | 文件已存在 |
| `FILE_CREATED_FAIL` | 3015 | 文件创建失败 |
| `FILE_COPY_FAIL` | 3016 | 复制失败 |
| `FILE_MOVE_FAIL` | 3017 | 移动失败 |
| `USER_NOT_ADMIN` | 3018 | 非管理员 |
| `ADMIN_ALREADY_EXISTS` | 3019 | 管理员已存在 |
| `ADMIN_ALREADY_FAIL` | 3029 | 管理员操作失败 |
| `USER_CHANGE_PASSWORD_FAIL` | 3030 | 改密失败 |
| `DELETE_ADMINUSER_FAIL` | 3031 | 删除管理员失败 |
| `LOGIN_LOCKED` | 3032 | 登录失败次数过多，已锁定 |

---

## 已知问题

详见 [CODE_REVIEW.md](./CODE_REVIEW.md)，主要包括：

- 调试后门 `233/233` 仍存在于拦截器
- 启动命令拼接客户端输入，存在命令注入风险
- 文件接口未做路径校验，存在路径穿越风险
- `sql.sql` 中 `user` 表缺少 `authme_id` 列，与 `UserDao` 的 join 语句不一致
- 部分服务通过 `new` 实例化，绕过 Spring 容器

---

## 反馈

- 邮箱：wfprivate@outlook.com
- QQ：270884295
            
                            
