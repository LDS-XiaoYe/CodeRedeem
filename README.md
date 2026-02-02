# CodeRedeem

一个功能强大的 Minecraft 兑换码插件，支持 MySQL 和 YAML 两种存储方式。

## 功能特性

- ✅ 支持创建和使用兑换码
- ✅ 兑换码可设置过期时间
- ✅ 每个兑换码支持执行多条命令
- ✅ 防止重复使用（每个玩家只能使用一次）
- ✅ 支持 MySQL 数据库和 YAML 配置文件两种存储方式
- ✅ 命令 TAB 补全提示
- ✅ 基于 Spigot 1.20.1 API

## 安装

1. 下载插件 JAR 文件
2. 将 JAR 文件放入服务器的 `plugins` 目录
3. 重启服务器
4. 编辑 `plugins/CodeRedeem/config.yml` 配置文件
5. 重新加载插件或重启服务器

## 配置说明

### config.yml

```yaml
# 是否启用MySQL数据库存储（true=MySQL, false=YAML配置文件）
enable-mysql: false

# MySQL数据库配置（仅在enable-mysql为true时生效）
mysql:
  host: localhost
  port: 3306
  database: coderedeem
  username: root
  password: ''
  useSSL: false
```

### 存储模式选择

#### YAML 模式（默认）
- 设置 `enable-mysql: false`
- 数据保存在 `plugins/CodeRedeem/data.yml`
- 无需配置数据库，开箱即用
- 适合小型服务器

#### MySQL 模式
- 设置 `enable-mysql: true`
- 数据保存在 MySQL 数据库
- 需要正确配置 mysql 连接信息
- 插件会自动创建所需的数据库表
- 适合大型服务器或需要高性能的场景

### MySQL 数据库表结构

启用 MySQL 模式时，插件会自动创建以下三个表：

- `redeem_codes` - 存储兑换码和过期时间
- `redeem_commands` - 存储兑换码关联的命令
- `redeem_usage` - 存储玩家使用记录

## 命令使用

### `/redeem <兑换码>`

**功能：** 使用兑换码领取奖励

**权限：** 无（所有玩家可用）

**示例：**
```
/redeem WELCOME2024
```

**说明：**
- 每个玩家对同一个兑换码只能使用一次
- 已过期的兑换码无法使用
- 使用成功后会自动执行预设的命令

### `/createredeem <兑换码> <过期时间> <命令>`

**功能：** 创建一个新的兑换码

**权限：** 需要 OP 权限

**参数说明：**
- `<兑换码>` - 兑换码文本，建议使用英文和数字
- `<过期时间>` - Unix 时间戳（毫秒），填 `0` 表示永不过期
- `<命令>` - 要执行的命令，多条命令用分号 `;` 分隔，空格用下划线 `_` 替代

**示例：**

1. 创建永不过期的兑换码：
```
/createredeem WELCOME2024 0 give_%player%_diamond_5;money_give_%player%_1000
```

2. 创建有过期时间的兑换码：
```
/createredeem NEWYEAR2024 1735689600000 give_%player%_golden_apple_1;tp_%player%_100_64_100
```

3. 创建单条命令的兑换码：
```
/createredeem STARTER 0 give_%player%_iron_sword_1
```

**命令格式说明：**
- 使用 `%player%` 占位符代表使用兑换码的玩家名
- 空格用下划线 `_` 替代
- 多条命令用分号 `;` 分隔
- 命令会按顺序执行

**过期时间获取方法：**

在线时间戳转换工具：
- https://tool.lu/timestamp/
- https://www.unixtimestamp.com/

或使用 Python：
```python
import time
# 获取当前时间戳（毫秒）
print(int(time.time() * 1000))

# 获取指定日期的时间戳
import datetime
dt = datetime.datetime(2024, 12, 31, 23, 59, 59)
print(int(dt.timestamp() * 1000))
```

## TAB 补全

两个命令都支持 TAB 键补全，会显示参数提示：

- `/redeem` + TAB → 显示 `<兑换码>`
- `/createredeem` + TAB → 显示参数提示和示例

## 使用示例

### 场景一：新手礼包
```
/createredeem NEWBIE2024 0 give_%player%_iron_sword_1;give_%player%_bread_10;money_give_%player%_500
```
玩家使用后会获得：铁剑×1、面包×10、500金币

### 场景二：活动兑换码（限时）
```
/createredeem SPRING2024 1735689600000 give_%player%_diamond_10;give_%player%_emerald_5
```
玩家在过期前使用可获得：钻石×10、绿宝石×5

### 场景三：VIP奖励
```
/createredeem VIP888 0 pex_user_%player%_group_add_VIP;give_%player%_elytra_1
```
玩家使用后会：加入VIP权限组、获得鞘翅×1

## 常见问题

### Q: 如何切换存储模式？
A: 修改 `config.yml` 中的 `enable-mysql` 为 `true`（MySQL）或 `false`（YAML），然后重启服务器。

### Q: MySQL 连接失败怎么办？
A: 检查以下几点：
1. 数据库服务是否正常运行
2. config.yml 中的数据库地址、端口、用户名、密码是否正确
3. 数据库是否已创建（需要手动创建数据库）
4. 用户是否有足够的权限

### Q: 如何查看所有兑换码？
A: 
- YAML 模式：查看 `plugins/CodeRedeem/data.yml` 文件
- MySQL 模式：查询数据库表 `redeem_codes`

### Q: 兑换码大小写敏感吗？
A: 是的，兑换码区分大小写。`TEST` 和 `test` 是不同的兑换码。

### Q: 可以删除兑换码吗？
A: 
- YAML 模式：编辑 `data.yml` 删除对应的兑换码配置
- MySQL 模式：删除数据库中对应的记录（会自动级联删除相关命令和使用记录）

### Q: 从 YAML 模式迁移到 MySQL 模式？
A: 需要手动将 `data.yml` 中的数据通过 `/createredeem` 命令重新创建到 MySQL。

## 技术信息

- **版本：** 1.0-SNAPSHOT
- **API：** Spigot 1.20.1
- **Java 版本：** 1.8+
- **依赖：** MySQL Connector/J 8.0.33（已内置）

## 更新日志

### v1.0-SNAPSHOT
- ✅ 基础兑换码功能
- ✅ 支持 MySQL 和 YAML 双存储模式
- ✅ 命令 TAB 补全
- ✅ 升级到 Spigot 1.20.1 API
- ✅ 数据与配置分离（YAML 模式使用 data.yml）
- ✅ 自动创建数据库表结构