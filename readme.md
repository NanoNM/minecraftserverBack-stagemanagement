### 请使用Linux_adapters分支中的版本master分支中代码不是被linux估计不会进一步开发




### Readme 
    
    欢迎使用本后端
    本端使用的是springboot框架前后端分离模式
    数据传输都以json形式
    
    注意 绝大多数的管理操作已经改为socket访问
    
### 因为硬件原因导致硬盘损坏 前段代码已经损坏 
    前段页面重新设计中 https://github.com/NanoNM/Mine
    
### 个个接口和使用说明(早起版本 现已不适用)
    main.core
            |controller 
                      |CorsConfog 用来通过跨域名访问
                      |MainPageController 基础访问 
                      |ModSettingContorller mod相关操作 比如禁用启用删除恢复等
                      |PlayerController 功能未实现 预计要配合自制插件使用
                      |PluginsSettingContorller 插件相关操作 类似于mod
                      |ServerContorller 服务器相关 具体看内部
            |entity
                      |ModModel mod相关模型
                      |PlayerModel 玩家相关模型 未实现
                      |PluginModel 插件相关模型
                      |ServerInfoModel 服务器主机相关信息模型 比如cpu占用 内存占用
                      |ServerSettingModel 不再使用
            |service  
                      |impl
                            |SaveAsModImpl 上传的mod文件保存实现类
                            |SaveAsPluginImpl 上传的插件文件保存实现类
                      |FormatServerSettingService 整理server.properties这个文件
                      |GetModListService 早晚要重写
                      |GetPluginsListService 早晚要重写
                      |GetServerInfoService 服务器信息获取 比如cpu占用 内存占用
                      |RunServiceService 启动mc服务端的核心文件
                      |SaveFileService 文件保存接口
            |util
                      |LogUtil 日志保存
                  
###错误代码类
    
        public static final Integer LOGIN_SUCCESS = 1000;
        public static final Integer REGISTER_SUCCESS = 1001;
        public static final Integer FILE_UPDATE_SUCCESS = 1002;
        public static final Integer FILE_RENAME_SUCCESS = 1003;
        public static final Integer FILE_DOWNLOAD_SUCCESS = 1004;
        public static final Integer FILE_EDIT_SUCCES = 1005;
    
        public static final Integer SAME_USER_NAME = 1102;
        public static final Integer NO_USER_FIND = 1103;
        public static final Integer WRONG_PASSWORD = 1104;
        public static final Integer USER_NOT_ONLINE = 1105;
        public static final Integer ADMIN_EARLY_ONLINE = 1106;
        public static final Integer NO_FILE_UPDATE = 1107;
        public static final Integer FILE_RENAME_ERROR= 1108;
        public static final Integer FILE_DOWNLOAD_ERROR = 1009;
        public static final Integer FILE_EDIT_FAIL = 1110;
         
希望大佬给我提些建议 
个人邮箱 wfprivate@outlook.com
个人QQ 270884295          
            
                            
