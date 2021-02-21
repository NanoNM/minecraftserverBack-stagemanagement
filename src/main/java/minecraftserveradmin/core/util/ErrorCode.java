package minecraftserveradmin.core.util;

public class ErrorCode {
    //用户相关 正常10xx 异常3xxx
    public static final Integer LOGIN_SUCCESS = 1000;
    public static final Integer REGISTER_SUCCESS = 1001;
    public static final Integer FILE_UPDATE_SUCCESS = 1002;
    public static final Integer FILE_RENAME_SUCCESS = 1003;
    public static final Integer FILE_DOWNLOAD_SUCCESS = 1004;
    public static final Integer FILE_EDIT_SUCCES = 1005;
    public static final Integer FILE_REMOVE_SUCCES = 1006;
    public static final Integer FOLDER_CREATED_SUCCESSFULLY = 1007;
    public static final Integer FILE_CREATED_SUCCESSFULLY = 1008;
    public static final Integer FILE_COPY_SUCCESS = 1009;
    public static final Integer FILE_MOVE_SUCCESS = 1010;
    public static final Integer ADMIN_REG_SUCCESS = 1011;
    public static final Integer USER_CHANGE_PASSWORD_SUCCESS = 1012;
    public static final Integer DELETE_ADMINUSER_SUCCESS = 1013;
    public static final Integer USER_REG_SUCCESS = 1014;



    public static final Integer SAME_USER_NAME = 3002;
    public static final Integer NO_USER_FIND = 3003;
    public static final Integer WRONG_PASSWORD = 3004;
    public static final Integer USER_NOT_ONLINE = 3005;
    public static final Integer ADMIN_EARLY_ONLINE = 3006;
    public static final Integer NO_FILE_UPDATE = 3007;
    public static final Integer FILE_RENAME_ERROR = 3008;
    public static final Integer FILE_DOWNLOAD_ERROR = 3009;
    public static final Integer FILE_EDIT_FAIL = 3010;
    public static final Integer FILE_REMOVE_FILE = 3011;
    public static final Integer FOLDER_ALREADY_EXISTS = 3012;
    public static final Integer FOLDER_CREATED_FAIL = 3013;
    public static final Integer FILE_ALREADY_EXISTS = 3014;
    public static final Integer FILE_CREATED_FAIL = 3015;
    public static final Integer FILE_COPY_FAIL = 3016;
    public static final Integer FILE_MOVE_FAIL = 3017;
    public static final Integer USER_NOT_ADMIN = 3018;
    public static final Integer ADMIN_ALREADY_EXISTS = 3019;
    public static final Integer ADMIN_ALREADY_FAIL = 3029;
    public static final Integer USER_CHANGE_PASSWORD_FAIL = 3030;
    public static final Integer DELETE_ADMINUSER_FAIL = 3031;
}
