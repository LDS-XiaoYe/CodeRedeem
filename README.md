# CodeRedeem

一个简单的类似某二字游戏新版本前瞻兑换码的 Minecraft 插件。基于 Bukkit 1.12.2。

指令:
- `/redeem <code>`  使用兑换码
- `/createredeem <code> <expire_at> <commands>` 创建兑换码。可以设置过期时间，命令要用分号分隔。
    > `expire_at` 填时间戳，0 为永不过期。