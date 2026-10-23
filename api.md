# Minecraft Server Manager 后端 API 文档

> 本文档对应 `ResultBody` 统一响应改造后的接口定义。
> 最后更新：2026-10-23

---

## 目录

- [1. 通用约定](#1-通用约定)
  - [1.1 基础信息](#11-基础信息)
  - [1.2 统一响应结构](#12-统一响应结构)
  - [1.3 鉴权方式](#13-鉴权方式)
  - [1.4 错误码表](#14-错误码表)
- [2. 用户与登录](#2-用户与登录)
- [3. 文件管理](#3-文件管理)
- [4. 服务器管理](#4-服务器管理)
- [5. Mod 管理](#5-mod-管理)
- [6. 插件管理](#6-插件管理)
- [7. 玩家管理](#7-玩家管理)
- [8. 系统信息](#8-系统信息)
- [9. WebSocket 接口](#9-websocket-接口)
  - [9.1 连接管理通道](#91-连接管理通道)
  - [9.2 鉴权与 Token 轮换](#92-鉴权与-token-轮换)
  - [9.3 上行消息（客户端 → 服务端）](#93-上行消息客户端--服务端)
  - [9.4 下行消息（服务端 → 客户端）](#94-下行消息服务端--客户端)
  - [9.5 连接关闭](#95-连接关闭)
  - [9.6 客户端接入示例](#96-客户端接入示例)
- [10. 数据结构](#10-数据结构)

---

## 1. 通用约定

### 1.1 基础信息

| 项目 | 值 |
|---|---|
| 默认端口 | `25567`（`server.port`，当前工作区配置为 `80`） |
| 协议 | HTTP / WebSocket |
| 请求编码 | UTF-8 |
| 响应格式 | `application/json`（文件下载除外） |
| 跨域 | 已开启，允许任意来源、方法、请求头，允许携带 Cookie |

### 1.2 统一响应结构

除 **文件下载** 与 **首页视图** 外，所有接口均返回如下结构：

```json
{
  "resultCode": 0,
  "message": "success",
  "object": {}
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `resultCode` | `Integer` | 业务结果码。`0` 表示成功；`10xx` 表示业务成功；`3xxx` 表示业务失败；`5xxx` 表示系统/框架错误 |
| `message` | `String` | 结果描述。成功时为 `success`，失败时为可读的错误说明 |
| `object` | `T` | 业务数据，类型随接口而定；无数据时为 `null` |

> **注意**：HTTP 状态码固定为 `200`（鉴权失败为 `401`），业务结果一律通过 `resultCode` 判断。
> 前端取值应使用 `res.object`，而非直接使用响应体。

**成功示例**

```json
{
  "resultCode": 0,
  "message": "success",
  "object": [
    { "name": "server.jar", "type": ".jar", "attr": 1, "size": 62294556 }
  ]
}
```

**失败示例**

```json
{
  "resultCode": 3008,
  "message": "重命名失败",
  "object": null
}
```

### 1.3 鉴权方式

所有 `/admin/**` 接口由 `AdminOperationIntercept` 拦截，需在 **Query 参数** 中携带：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | `String` | 是 | 管理员用户名 |
| `token` | `String` | 是 | 由 WebSocket 连接下发的会话令牌 |

鉴权失败返回 **HTTP 401**：

```json
{
  "resultCode": 5007,
  "message": "Fail: UnAuthorized Request",
  "object": null
}
```

> 调试后门：`name=233&token=233` 可直接通过鉴权（仅用于本地调试，生产环境应移除）。

### 1.4 错误码表

#### 通用

| 码值 | 常量 | 说明 |
|---|---|---|
| `0` | `SUCCESS` | 成功 |

#### 业务成功（10xx）

| 码值 | 常量 | 说明 |
|---|---|---|
| `1000` | `LOGIN_SUCCESS` | 登录成功 |
| `1001` | `REGISTER_SUCCESS` | 注册成功 |
| `1002` | `FILE_UPDATE_SUCCESS` | 文件上传成功 |
| `1003` | `FILE_RENAME_SUCCESS` | 重命名成功 |
| `1004` | `FILE_DOWNLOAD_SUCCESS` | 下载成功 |
| `1005` | `FILE_EDIT_SUCCES` | 文件保存成功 |
| `1006` | `FILE_REMOVE_SUCCES` | 文件删除成功 |
| `1007` | `FOLDER_CREATED_SUCCESSFULLY` | 目录创建成功 |
| `1008` | `FILE_CREATED_SUCCESSFULLY` | 文件创建成功 |
| `1009` | `FILE_COPY_SUCCESS` | 复制成功 |
| `1010` | `FILE_MOVE_SUCCESS` | 移动成功 |
| `1011` | `ADMIN_REG_SUCCESS` | 管理员注册成功 |
| `1012` | `USER_CHANGE_PASSWORD_SUCCESS` | 修改密码成功 |
| `1013` | `DELETE_ADMINUSER_SUCCESS` | 删除管理员成功 |
| `1014` | `USER_REG_SUCCESS` | 用户注册成功 |

#### 业务失败（3xxx）

| 码值 | 常量 | 说明 |
|---|---|---|
| `3002` | `SAME_USER_NAME` | 用户名已存在 |
| `3003` | `NO_USER_FIND` | 用户不存在 |
| `3004` | `WRONG_PASSWORD` | 密码错误 |
| `3005` | `USER_NOT_ONLINE` | 用户未登录 |
| `3006` | `ADMIN_EARLY_ONLINE` | 管理员已在线 |
| `3007` | `NO_FILE_UPDATE` | 文件上传失败 |
| `3008` | `FILE_RENAME_ERROR` | 重命名失败 |
| `3009` | `FILE_DOWNLOAD_ERROR` | 下载失败 |
| `3010` | `FILE_EDIT_FAIL` | 文件保存失败 |
| `3011` | `FILE_REMOVE_FILE` | 文件删除失败 |
| `3012` | `FOLDER_ALREADY_EXISTS` | 目录已存在 |
| `3013` | `FOLDER_CREATED_FAIL` | 目录创建失败 |
| `3014` | `FILE_ALREADY_EXISTS` | 文件已存在 |
| `3015` | `FILE_CREATED_FAIL` | 文件创建失败 |
| `3016` | `FILE_COPY_FAIL` | 复制失败 |
| `3017` | `FILE_MOVE_FAIL` | 移动失败 |
| `3018` | `USER_NOT_ADMIN` | 无管理员权限 |
| `3019` | `ADMIN_ALREADY_EXISTS` | 管理员已存在 |
| `3029` | `ADMIN_ALREADY_FAIL` | 管理员注册失败 |
| `3030` | `USER_CHANGE_PASSWORD_FAIL` | 修改密码失败 |
| `3031` | `DELETE_ADMINUSER_FAIL` | 删除管理员失败 |
| `3032` | `LOGIN_LOCKED` | 登录已锁定 |

#### 系统错误（5xxx）

| 码值 | 常量 | 说明 |
|---|---|---|
| `5000` | `SYSTEM_ERROR` | 服务器内部错误 |
| `5001` | `PARAM_ERROR` | 请求参数错误 |
| `5002` | `REQUEST_METHOD_NOT_SUPPORTED` | 请求方法不支持 |
| `5003` | `MEDIA_TYPE_NOT_SUPPORTED` | 请求内容类型不支持 |
| `5004` | `UPLOAD_SIZE_EXCEEDED` | 上传文件超过大小限制 |
| `5005` | `NOT_FOUND` | 接口不存在 |
| `5006` | `API_DEPRECATED` | 接口已废弃 / 功能未实现 |
| `5007` | `UNAUTHORIZED` | 未授权 |

> 完整错误码可通过 `GET /errorcode` 动态获取。

---

## 2. 用户与登录

### 2.1 用户登录

`POST /login`

**Content-Type**：`application/x-www-form-urlencoded`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `username` | `String` | 是 | 用户名 |
| `passwd` | `String` | 是 | 密码 |
| `isautologin` | `String` | 是 | 是否自动登录，`"true"` / `"false"` |

**响应** `ResultBody<UserLoginModel>`

```json
{
  "resultCode": 0,
  "message": "success",
  "object": {
    "userModel": {
      "user_name": "admin",
      "email": "a@b.c",
      "authority": "admin",
      "passwd": null,
      "uuid": null
    },
    "code": 1000,
    "connect_token": "48fe13df5079bd3c59c0a86f388dc64d"
  }
}
```

> 登录结果需同时判断 `resultCode`（传输层）与 `object.code`（业务层，见 10xx/3xxx）。

---

### 2.2 管理员登录

`POST /admin/login`

**Content-Type**：`application/json`

```json
{
  "username": "admin",
  "password": "******"
}
```

**响应** `ResultBody<UserLoginModel>`，结构同 [2.1](#21-用户登录)。

---

### 2.3 自动登录

`GET /autologin`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `token` | `String` | 是 | 自动登录令牌 |

**响应** `ResultBody<UserLoginModel>`；令牌无效时 `object` 为 `null`。

---

### 2.4 退出登录

`POST /userloginout`

**Cookie**：`token` 或 `connect_token`（二者其一即可）

**响应** `ResultBody<String>`

| 场景 | resultCode | object |
|---|---|---|
| 退出成功 | `0` | `"success"` |
| 未登录 | `3005` | `null` |

---

### 2.5 查询管理员列表

`GET /admin/getalladmin`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `page` | `Integer` | 是 | 页码，从 `1` 开始，每页 10 条 |

**响应** `ResultBody<UserModel[]>`

---

### 2.6 查询普通用户列表

`GET /admin/getalluser`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `page` | `Integer` | 是 | 页码，从 `1` 开始，每页 10 条 |

**响应** `ResultBody<Object[]>`

---

### 2.7 按用户名查询用户

`GET /admin/suchuserbyname`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `suchname` | `String` | 是 | 待查询的用户名 |

**响应** `ResultBody<UserModel[]>`

---

### 2.8 修改密码

`POST /admin/modifypassword`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `newps` | `String` | 是 | 新密码 |
| `username` | `String` | 是 | 目标用户名 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1012` |
| 失败 | `3030` |

---

### 2.9 删除管理员

`GET /admin/deleteAdministrator`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | `String` | 是 | 操作者用户名（鉴权用） |
| `username` | `String` | 是 | 待删除的管理员用户名 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1013` |
| 失败 | `3031` |

---

### 2.10 注册管理员

`POST /admin/register`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | `String` | 是 | 操作者用户名（须为管理员） |
| `regname` | `String` | 是 | 新管理员用户名 |
| `passwd` | `String` | 是 | 密码 |
| `email` | `String` | 是 | 邮箱 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1011` |
| 用户名已存在 | `3019` |
| 注册失败 | `3029` |
| 操作者非管理员 | `3018` |

---

### 2.11 注册普通用户

`POST /admin/registeruser`

参数同 [2.10](#210-注册管理员)。

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1014` |
| 失败 | `3029` |
| 操作者非管理员 | `3018` |

---

## 3. 文件管理

> 所有路径参数均为 **相对于服务端工作目录** 的路径，服务层已做路径穿越校验。

### 3.1 列出文件

`GET /admin/file`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 否 | 目录路径。省略时返回根目录 |

**响应** `ResultBody<NewFileModel[]>`

```json
{
  "resultCode": 0,
  "message": "success",
  "object": [
    {
      "md5": null,
      "name": "server.jar",
      "type": ".jar",
      "attr": 1,
      "size": 62294556,
      "father": null,
      "onwer": null,
      "filePath": "server.jar",
      "patent": null
    }
  ]
}
```

> `attr`：`1` 表示文件，`0` 表示目录。

---

### 3.2 重命名

`GET /admin/renameFile`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 是 | 原路径 |
| `rename` | `String` | 是 | 新名称 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1003` |
| 失败 | `3008` |

---

### 3.3 下载文件

`GET /admin/download`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 是 | 文件路径 |

**响应**：**二进制文件流**（`application/octet-stream`），**不包装为 `ResultBody`**。

响应头：

```
Content-Type: application/octet-stream;charset=<自动探测的编码>
Content-Disposition: attachment;filename=<URL 编码后的文件名>
Access-Control-Expose-Headers: Content-Disposition
```

---

### 3.4 保存文件内容

`POST /admin/fileeditor`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 是 | 文件路径 |
| `string` | `String` | 是 | 新内容 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1005` |
| 失败 | `3010` |

---

### 3.5 删除文件

`GET /admin/filedelete`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 是 | 文件路径 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1006` |
| 失败 | `3011` |

---

### 3.6 创建目录

`GET /admin/mkdir`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 是 | 目录路径 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1007` |
| 已存在 | `3012` |
| 失败 | `3013` |

---

### 3.7 创建文件

`GET /admin/makefile`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `path` | `String` | 是 | 文件路径 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1008` |
| 已存在 | `3014` |
| 失败 | `3015` |

---

### 3.8 复制文件

`GET /admin/filecopy`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `source` | `String` | 是 | 源路径 |
| `dest` | `String` | 是 | 目标路径 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1009` |
| 目标已存在 | `3014` |
| 失败 | `3016` |

---

### 3.9 移动文件

`GET /admin/filemove`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `source` | `String` | 是 | 源路径 |
| `dest` | `String` | 是 | 目标路径 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1010` |
| 目标已存在 | `3014` |
| 失败 | `3017` |

---

## 4. 服务器管理

### 4.1 获取服务器配置

`GET /admin/getsetting`

读取工作目录下的 `server.properties`。

**响应** `ResultBody<String>`，`object` 为配置项的 JSON 字符串：

```json
{
  "resultCode": 0,
  "message": "success",
  "object": "{\"max-players\":\"20\",\"server-port\":\"25565\"}"
}
```

> `server.properties` 不存在时返回 `resultCode: 5000`。

---

### 4.2 保存服务器配置

`GET /admin/setsetting`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `jsonstr` | `String` | 是 | 配置项 JSON 字符串 |

**响应** `ResultBody<Boolean>`

| 场景 | resultCode | object |
|---|---|---|
| 成功 | `0` | `true` |
| 失败 | `5000` | `null` |

---

### 4.3 获取 jar 列表

`GET /admin/getjar`

列出工作目录下所有 `.jar` 文件。

**响应** `ResultBody<String[]>`

```json
{
  "resultCode": 0,
  "message": "success",
  "object": ["server.jar", "server - 副本.jar"]
}
```

---

### 4.4 同步控制台日志

`GET /admin/synchronizationConsole`

读取 `logs/latest.log`，GZIP 压缩后 Base64 编码。

**响应** `ResultBody<String>`，`object` 为 Base64 字符串。

---

### 4.5 已废弃接口

以下接口保留路由但固定返回 `resultCode: 5006`：

| 接口 | 说明 |
|---|---|
| `GET /admin/server/s` | 服务器指令传递（已废弃） |
| `GET /admin/server` | 控制台日志（已废弃） |
| `GET /admin/serverTest` | 服务器状态（已废弃） |

```json
{
  "resultCode": 5006,
  "message": "接口废弃",
  "object": null
}
```

---

## 5. Mod 管理

### 5.1 扫描 / 操作 Mod

`GET /admin/modScanning`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | `String` | 是 | 管理员用户名 |
| `cmd` | `String` | 否 | 操作指令，见下表。省略时返回 Mod 列表 |
| `filename` | `String` | 否 | 目标 Mod 文件名（`cmd` 非空时必填） |

| `cmd` 值 | 说明 |
|---|---|
| （省略） | 扫描并返回 Mod 列表 |
| `able` | 启用 |
| `disable` | 禁用 |
| `remove` | 移除 |
| `completelyDelete` | 彻底删除 |

**响应** `ResultBody<ModModel[]>`

- `cmd` 省略时：`object` 为 Mod 列表
- `cmd` 非空时：`object` 为 `null`

---

### 5.2 获取 Mod 详情

`GET /admin/moremodinfo`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `filename` | `String` | 是 | Mod 文件名 |

读取 jar 内 `mcmod.info` 的内容。

**响应** `ResultBody<String>`，`object` 为 `mcmod.info` 原始文本。

---

### 5.3 上传 Mod

`POST /admin/upfilemods`

**Content-Type**：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | `File` | 是 | Mod 文件 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1002` |
| 文件为空 / 上传失败 | `3007` |

---

### 5.4 删除 Mod（未实现）

`GET /admin/reMove`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `cmd` | `String` | 是 | 指令 |
| `filename` | `String` | 是 | 文件名 |

**响应** `ResultBody<String>`，固定返回 `resultCode: 5006`（功能未实现）。

---

## 6. 插件管理

### 6.1 扫描 / 操作插件

`GET /admin/pluginScanning`

参数与 `cmd` 取值同 [5.1](#51-扫描--操作-mod)。

**响应** `ResultBody<PluginModel[]>`

---

### 6.2 获取插件详情

`GET /admin/moreplugininfo`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `filename` | `String` | 是 | 插件文件名 |

读取 jar 内 `plugin.yml` 并解析为 YAML。

**响应** `ResultBody<Map<String, Object>>`

```json
{
  "resultCode": 0,
  "message": "success",
  "object": {
    "name": "AuthMe",
    "version": "5.6.0",
    "main": "fr.xephi.authme.AuthMe"
  }
}
```

---

### 6.3 上传插件

`POST /admin/upfileplugins`

**Content-Type**：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | `File` | 是 | 插件文件 |

**响应** `ResultBody<Integer>`

| 场景 | resultCode |
|---|---|
| 成功 | `1002` |
| 文件为空 / 上传失败 | `3007` |

---

### 6.4 删除插件（未实现）

`GET /admin/repluginMove`

参数同 [5.4](#54-删除-mod未实现)。

**响应** `ResultBody<String>`，固定返回 `resultCode: 5006`。

---

## 7. 玩家管理

### 7.1 查询玩家 OP 状态

`GET /admin/testPlayer`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `id` | `String` | 是 | 玩家名 |

读取工作目录下的 `ops.json` 判断是否为 OP。

**响应** `ResultBody<PlayerModel>`

```json
{
  "resultCode": 0,
  "message": "success",
  "object": {
    "name": "Steve",
    "op": true,
    "idBaned": false,
    "ipBaned": false,
    "whiteList": false
  }
}
```

> `ops.json` 不存在时返回 `resultCode: 5000`。

---

## 8. 系统信息

### 8.1 首页

`GET /`

返回 Thymeleaf 视图 `index`，**不包装为 `ResultBody`**。

---

### 8.2 获取错误码表

`GET /errorcode`

**响应** `ResultBody<Map<String, Object>>`

```json
{
  "resultCode": 0,
  "message": "success",
  "object": {
    "SUCCESS": 0,
    "LOGIN_SUCCESS": 1000,
    "SYSTEM_ERROR": 5000
  }
}
```

---

## 9. WebSocket 接口

WebSocket 由 `WebSocketService`（`@ServerEndpoint`）实现，`WebSocketConfig` 注册 `ServerEndpointExporter`。
所有消息均为 **JSON 字符串**，服务端下行统一使用 `SocketResult` 结构（见 [10.8](#108-socketresult)）。

### 9.1 连接管理通道

```
ws://<host>:<port>/admin/tcpServer/{userId}
```

| 路径参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `userId` | `String` | 是 | 管理员用户名，必须与登录时写入在线列表的用户名一致 |

**连接前置条件**：`userId` 必须已存在于服务端在线管理员列表（即已通过 `POST /admin/login` 登录）。
否则服务端记录告警并 **立即关闭连接**，不会下发任何消息。

**重复连接**：同一 `userId` 再次连接时，服务端会踢掉旧会话（关闭旧连接、清理其 token 与在线记录），
由新连接顶替，保证同一账号只保留一个活动会话。

**连接建立后**：服务端延迟约 2 秒主动推送一次当前 MC 服务器状态（`ServerStat`）。

### 9.2 鉴权与 Token 轮换

连接建立后，token 轮换线程检测到在线会话数量变化，会立即下发首个 token（通常 < 250ms）：

```json
{ "name": "Authentication", "message": "", "object": "<token>" }
```

客户端必须保存该 token，并在后续每条业务消息中携带。token 规则：

| 规则 | 说明 |
|---|---|
| 一次性 | 每条通过校验的业务消息处理完成后，服务端立即轮换新 token 并下发新的 `Authentication` 消息，旧 token 失效 |
| 定时轮换 | 后台线程每 250ms 检查一次：在线会话数量变化时立即为所有会话轮换 token；否则当距上次轮换的时间差恰好为 1000ms 时轮换（该判断为精确相等，实际可能不触发） |
| 绑定会话 | token 与 `Session` 一一对应，其他会话的 token 无效 |
| 清理 | 连接关闭时删除该会话的 token 记录 |

**身份绑定**：客户端收到 token 后应发送一次身份消息，把用户名绑定到 token 条目：

```json
{ "name": "name", "value": "<username>", "token": "<token>" }
```

> 该消息不参与 token 校验，仅用于绑定用户名；绑定后 `onClose` 才能按用户名清理登录记录。
> 由于每次轮换都会重建 token 条目（用户名被清空），客户端应在 **每次收到 `Authentication` 后重新发送一次身份绑定消息**。

### 9.3 上行消息（客户端 → 服务端）

通用字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | `String` | 是 | 消息类型：`name` / `cmd` / `message` / `verification` |
| `value` | `String` | 是 | 消息内容，含义随 `name` 而定 |
| `username` | `String` | 业务消息必填 | 管理员用户名，服务端会校验其是否在线 |
| `token` | `String` | 业务消息必填 | 服务端下发的最新 token |
| `startservercmd` | `String` | 否 | 仅 `name=cmd` 时有效，追加到 MC 启动命令尾部 |

#### 9.3.1 身份绑定 `name = "name"`

```json
{ "name": "name", "value": "admin", "token": "<token>" }
```

遍历服务端 token 列表，找到 token 匹配且尚未绑定用户名的条目，把 `value` 写入用户名。
不校验 token 有效性，也不返回响应。

#### 9.3.2 服务器指令 `name = "cmd"`

```json
{ "name": "cmd", "value": "startserver", "username": "admin", "token": "<token>" }
```

| `value` | 说明 |
|---|---|
| `startserver` | 启动 MC 服务器（仅在服务器未运行时生效；已运行时忽略） |
| `stop` | 向 MC 控制台标准输入写入 `stop`，优雅关服 |
| `hardstop` | 强制结束进程（最多重试 10 次，仍失败则尝试 `TASKKILL /PID` / `kill`） |
| 其他任意字符串 | 原样写入 MC 服务器标准输入，作为控制台指令执行（服务器未运行时无效） |

可选字段 `startservercmd` 会以空格拼接到启动命令 `StaticDataUtil.cmd` 之后（仅本次启动有效，执行后重置为 `java -jar`）。

#### 9.3.3 会话控制 `name = "message"`

```json
{ "name": "message", "value": "SocketClosed", "username": "admin", "token": "<token>" }
```

`value = "SocketClosed"` 时服务端主动关闭当前会话；其他值忽略。

#### 9.3.4 心跳 / 校验 `name = "verification"`

```json
{ "name": "verification", "value": "", "username": "admin", "token": "<token>" }
```

该消息本身没有业务处理逻辑，仅用于保活。注意分支顺序：**token 有效时会先进入业务分支**，
即校验 `username` 是否在线（不在线则下发 `ERROR` 3005）并轮换 token；token 无效时落入本分支，静默忽略。
**注意**：服务端内置的 `heartBeat` 推送线程未启用，不会主动下发 `{"heartBeat": "..."}` 消息。

#### 9.3.5 处理流程

1. JSON 解析失败 → 直接忽略。
2. `name = "name"` → 绑定用户名（不校验 token，不回复）。
3. token 校验通过 → 校验 `username` 是否在线 → 执行业务（`cmd` / `message`）→ 轮换 token。
4. token 校验失败且 `name = "verification"` → 静默忽略。
5. 其他（token 校验失败）→ 仅记录告警日志，**不回复客户端**。

### 9.4 下行消息（服务端 → 客户端）

所有下行消息结构为 `SocketResult`：

```json
{ "name": "<类型>", "message": "<描述>", "object": <数据> }
```

| `name` | 触发时机 | `object` 类型 | 说明 |
|---|---|---|---|
| `Authentication` | 连接建立、每次 token 轮换 | `String` | 新的会话 token |
| `ServerStat` | 连接建立约 2s 后；MC 服务器运行状态变化时 | `Integer` | `1` 运行中 / `0` 已停止 |
| `systemInfo` | 每 `server.serverInfoSenderTimer`（默认 4000ms） | `ServerInfoModel` | 系统与 MC 服务器运行信息 |
| `ERROR` | 业务消息校验失败 | `Integer` | 错误码，见 [1.4](#14-错误码表) |

**示例**

```json
{ "name": "Authentication", "message": "", "object": "9f2c1d7a4b8e0c5d6a3f1e2b7c8d9a0e" }
{ "name": "ServerStat", "message": "", "object": 1 }
{ "name": "systemInfo", "message": "", "object": { "systemInfo": "Windows 11", "cpuInfo": "...", "cpuUserInfo": 12.5, "memoryUserInfo": 43, "springBootStartTime": "2026-10-23 10:00:00", "springBootRunningTime": "1天2小时", "mcServerStartTime": "2026-10-23 10:05:00", "mcServerRunningTime": "1天1小时" } }
{ "name": "ERROR", "message": "3005", "object": 3005 }
```

**错误场景**

| 场景 | 服务端行为 |
|---|---|
| 消息缺少 `username` 或该用户不在线 | 移除会话与 token，下发 `ERROR`（`3005` `USER_NOT_ONLINE`），并记录告警 |
| `systemInfo` 采集失败 | 下发 `ERROR`，`message` 为 `获取服务器信息失败`，`object` 为 `null` |
| token 校验失败 | 不下发任何消息，仅记录告警 |

> **已知行为**：`ServerStat` 在状态变化时对同一会话会连续发送两次相同消息，客户端需容忍重复推送。
> 前端打包产物（`static/js/*.js`）仍按旧格式（如 `{"ServerStat": 1}`）解析，与当前后端 `SocketResult` 结构不一致。

### 9.5 连接关闭

`@OnClose` 触发时服务端依次执行：

1. 按用户名删除 `autologin` 表中的登录记录。
2. 从在线会话表 `onlineSessions` 中移除该会话。
3. 从 token 列表 `AOPtokens` 中移除该会话的 token。
4. 从在线管理员列表 `onlineadmin` 中移除该会话。

### 9.6 客户端接入示例

```javascript
const ws = new WebSocket(`ws://${host}:${port}/admin/tcpServer/${username}`);
let token = "";

ws.onmessage = (evt) => {
  const msg = JSON.parse(evt.data);
  switch (msg.name) {
    case "Authentication":
      token = msg.object;
      // 首次拿到 token 后绑定用户名
      ws.send(JSON.stringify({ name: "name", value: username, token }));
      break;
    case "ServerStat":
      console.log("MC 服务器状态:", msg.object === 1 ? "运行中" : "已停止");
      break;
    case "systemInfo":
      console.log("系统信息:", msg.object);
      break;
    case "ERROR":
      console.warn("错误码:", msg.object);
      break;
  }
};

// 启动服务器
ws.send(JSON.stringify({ name: "cmd", value: "startserver", username, token }));
```

---

## 10. 数据结构

### 10.1 ResultBody\<T\>

| 字段 | 类型 | 说明 |
|---|---|---|
| `resultCode` | `Integer` | 业务结果码 |
| `message` | `String` | 结果描述 |
| `object` | `T` | 业务数据 |

### 10.2 UserModel

| 字段 | 类型 | 说明 |
|---|---|---|
| `ID` | `Integer` | 主键 |
| `user_name` | `String` | 用户名 |
| `passwd` | `String` | 密码（响应中恒为 `null`） |
| `email` | `String` | 邮箱 |
| `vip_level` | `Integer` | VIP 等级 |
| `authority` | `String` | 权限，`admin` / 其他 |
| `UUID` | `String` | 用户 UUID（响应中恒为 `null`） |
| `create_time` | `Timestamp` | 创建时间 |
| `modify_time` | `Timestamp` | 修改时间 |
| `last_login_time` | `Timestamp` | 最后登录时间 |
| `create_by` | `String` | 创建者 |
| `realname` | `String` | 真实姓名（authme 关联） |

### 10.3 UserLoginModel

| 字段 | 类型 | 说明 |
|---|---|---|
| `userModel` | `UserModel` | 用户信息 |
| `code` | `Integer` | 登录业务码（10xx / 3xxx） |
| `connect_token` | `String` | 连接令牌 |

### 10.4 NewFileModel

| 字段 | 类型 | 说明 |
|---|---|---|
| `md5` | `String` | 文件 MD5 |
| `name` | `String` | 文件名或目录名 |
| `type` | `String` | 文件扩展名 |
| `attr` | `int` | `1` 文件 / `0` 目录 |
| `size` | `long` | 文件大小（字节） |
| `father` | `String` | 上级目录 |
| `onwer` | `String` | 所有者 |
| `filePath` | `String` | 相对路径 |
| `patent` | `String` | 父路径 |

### 10.5 ModModel

| 字段 | 类型 | 说明 |
|---|---|---|
| `modFilename` | `String` | Mod 文件名 |
| `modName` | `String` | Mod 名称 |
| `modAuthors` | `String[]` | 作者列表 |
| `modVersion` | `String` | 版本 |
| `status` | `String` | 状态：`normal` / `disable` / `removed` |
| `isdisable` | `boolean` | 是否禁用 |
| `isremoved` | `boolean` | 是否已移除 |

### 10.6 PluginModel

| 字段 | 类型 | 说明 |
|---|---|---|
| `plguinFilename` | `String` | 插件文件名 |
| `plguinName` | `String` | 插件名称 |
| `plguinAuthors` | `String[]` | 作者列表 |
| `plguinVersion` | `String` | 版本 |
| `status` | `String` | 状态：`normal` / `disable` / `removed` |
| `isdisable` | `boolean` | 是否禁用 |
| `isremoved` | `boolean` | 是否已移除 |

### 10.7 PlayerModel

| 字段 | 类型 | 说明 |
|---|---|---|
| `name` | `String` | 玩家名 |
| `op` | `boolean` | 是否 OP |
| `idBaned` | `boolean` | ID 是否被封禁 |
| `ipBaned` | `boolean` | IP 是否被封禁 |
| `whiteList` | `boolean` | 是否在白名单 |

### 10.8 SocketResult

WebSocket 下行消息统一结构。

| 字段 | 类型 | 说明 |
|---|---|---|
| `name` | `String` | 消息类型：`Authentication` / `ServerStat` / `systemInfo` / `ERROR` |
| `message` | `String` | 描述信息，无内容时为空字符串 |
| `object` | `Object` | 业务数据，类型随 `name` 而定 |

### 10.9 ServerInfoModel

`systemInfo` 消息的 `object` 结构。

| 字段 | 类型 | 说明 |
|---|---|---|
| `systemInfo` | `String` | 操作系统名称与版本 |
| `cpuInfo` | `String` | CPU 型号信息 |
| `cpuUserInfo` | `Double` | CPU 使用率（百分比） |
| `memoryUserInfo` | `Integer` | 内存使用率（百分比） |
| `springBootStartTime` | `String` | 管理端启动时间 |
| `springBootRunningTime` | `String` | 管理端已运行时长 |
| `mcServerStartTime` | `String` | MC 服务器启动时间，未运行时为 `Power off` |
| `mcServerRunningTime` | `String` | MC 服务器已运行时长，未运行时为 `0` |

---

## 附录：接口总览

| # | 方法 | 路径 | 响应类型 | 鉴权 |
|---|---|---|---|---|
| 1 | POST | `/login` | `ResultBody<UserLoginModel>` | 否 |
| 2 | POST | `/admin/login` | `ResultBody<UserLoginModel>` | 否 |
| 3 | GET | `/autologin` | `ResultBody<UserLoginModel>` | 否 |
| 4 | POST | `/userloginout` | `ResultBody<String>` | 否 |
| 5 | GET | `/admin/getalladmin` | `ResultBody<UserModel[]>` | 是 |
| 6 | GET | `/admin/getalluser` | `ResultBody<Object[]>` | 是 |
| 7 | GET | `/admin/suchuserbyname` | `ResultBody<UserModel[]>` | 是 |
| 8 | POST | `/admin/modifypassword` | `ResultBody<Integer>` | 是 |
| 9 | GET | `/admin/deleteAdministrator` | `ResultBody<Integer>` | 是 |
| 10 | POST | `/admin/register` | `ResultBody<Integer>` | 是 |
| 11 | POST | `/admin/registeruser` | `ResultBody<Integer>` | 是 |
| 12 | GET | `/admin/file` | `ResultBody<NewFileModel[]>` | 是 |
| 13 | GET | `/admin/renameFile` | `ResultBody<Integer>` | 是 |
| 14 | GET | `/admin/download` | 二进制流 | 是 |
| 15 | POST | `/admin/fileeditor` | `ResultBody<Integer>` | 是 |
| 16 | GET | `/admin/filedelete` | `ResultBody<Integer>` | 是 |
| 17 | GET | `/admin/mkdir` | `ResultBody<Integer>` | 是 |
| 18 | GET | `/admin/makefile` | `ResultBody<Integer>` | 是 |
| 19 | GET | `/admin/filecopy` | `ResultBody<Integer>` | 是 |
| 20 | GET | `/admin/filemove` | `ResultBody<Integer>` | 是 |
| 21 | GET | `/admin/getsetting` | `ResultBody<String>` | 是 |
| 22 | GET | `/admin/setsetting` | `ResultBody<Boolean>` | 是 |
| 23 | GET | `/admin/getjar` | `ResultBody<String[]>` | 是 |
| 24 | GET | `/admin/synchronizationConsole` | `ResultBody<String>` | 是 |
| 25 | GET | `/admin/server/s` | `ResultBody<String>` | 是 |
| 26 | GET | `/admin/server` | `ResultBody<String>` | 是 |
| 27 | GET | `/admin/serverTest` | `ResultBody<String>` | 是 |
| 28 | GET | `/admin/modScanning` | `ResultBody<ModModel[]>` | 是 |
| 29 | GET | `/admin/moremodinfo` | `ResultBody<String>` | 是 |
| 30 | POST | `/admin/upfilemods` | `ResultBody<Integer>` | 是 |
| 31 | GET | `/admin/reMove` | `ResultBody<String>` | 是 |
| 32 | GET | `/admin/pluginScanning` | `ResultBody<PluginModel[]>` | 是 |
| 33 | GET | `/admin/moreplugininfo` | `ResultBody<Map<String,Object>>` | 是 |
| 34 | POST | `/admin/upfileplugins` | `ResultBody<Integer>` | 是 |
| 35 | GET | `/admin/repluginMove` | `ResultBody<String>` | 是 |
| 36 | GET | `/admin/testPlayer` | `ResultBody<PlayerModel>` | 是 |
| 37 | GET | `/` | Thymeleaf 视图 | 否 |
| 38 | GET | `/errorcode` | `ResultBody<Map<String,Object>>` | 否 |
| 39 | WS | `/admin/tcpServer/{userId}` | `SocketResult` JSON 消息 | 消息内 token 校验 |
