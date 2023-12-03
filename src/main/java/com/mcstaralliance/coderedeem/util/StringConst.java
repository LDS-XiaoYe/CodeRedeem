package com.mcstaralliance.coderedeem.util;

public class StringConst {
    public static final String INVALID_CODE = "无效的兑换码，请检查后再试。";
    public static final String CODE_EXPIRED = "兑换码已过期。";
    public static final String CODE_HAS_USED = "兑换码已经使用。";
    public static final String PERMISSION_DENIED = "你没有权限执行此命令。";
    public static final String INVALID_ARGUMENTS = "参数不正确。";
    public static final String USE_SUCCESSFULLY = "兑换成功。";
    public static final String ADD_COMMAND_HELP_1 = "Usage: /createredeem <code context> <expire time> <commands>";
    public static final String ADD_COMMAND_HELP_2 = "三个参数依次为兑换码文本、失效时间（永不失效填 0）、执行的命令（用分号分割，用下划线替代空格）";
    public static final String REDEEM_COMMAND_HELP = "Usage: /redeem <code context>";
}
