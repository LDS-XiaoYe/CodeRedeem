package com.mcstaralliance.coderedeem.database;

import com.mcstaralliance.coderedeem.CodeRedeem;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * MySQL数据库存储实现
 */
public class MySQLManager implements DataStorage {
    private final CodeRedeem plugin;
    private Connection connection;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private boolean useSSL;

    public MySQLManager(CodeRedeem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean initialize() {
        loadConfig();
        return connect();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.host = config.getString("mysql.host", "localhost");
        this.port = config.getInt("mysql.port", 3306);
        this.database = config.getString("mysql.database", "coderedeem");
        this.username = config.getString("mysql.username", "root");
        this.password = config.getString("mysql.password", "");
        this.useSSL = config.getBoolean("mysql.useSSL", false);
    }

    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) {
                return true;
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database 
                    + "?autoReconnect=true&useSSL=" + useSSL 
                    + "&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8"
                    + "&maxReconnects=3&initialTimeout=2";
            
            connection = DriverManager.getConnection(url, username, password);
            plugin.getLogger().info("成功连接到MySQL数据库！");
            createTables();
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "找不到MySQL驱动！请确保已添加MySQL驱动依赖。", e);
            return false;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "连接MySQL数据库失败！", e);
            return false;
        }
    }

    @Override
    public void close() {
        disconnect();
    }

    private void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                plugin.getLogger().info("已断开MySQL数据库连接。");
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "断开MySQL数据库连接时出错！", e);
            }
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            // 创建兑换码表
            String createCodesTable = "CREATE TABLE IF NOT EXISTS redeem_codes (" +
                    "code VARCHAR(64) PRIMARY KEY," +
                    "expire_at BIGINT NOT NULL DEFAULT 0," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            statement.executeUpdate(createCodesTable);

            // 创建命令表
            String createCommandsTable = "CREATE TABLE IF NOT EXISTS redeem_commands (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "code VARCHAR(64) NOT NULL," +
                    "command TEXT NOT NULL," +
                    "command_order INT NOT NULL DEFAULT 0," +
                    "FOREIGN KEY (code) REFERENCES redeem_codes(code) ON DELETE CASCADE," +
                    "INDEX idx_code (code)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            statement.executeUpdate(createCommandsTable);

            // 创建使用记录表
            String createUsageTable = "CREATE TABLE IF NOT EXISTS redeem_usage (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "code VARCHAR(64) NOT NULL," +
                    "player_name VARCHAR(64) NOT NULL," +
                    "used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (code) REFERENCES redeem_codes(code) ON DELETE CASCADE," +
                    "INDEX idx_code (code)," +
                    "INDEX idx_player (player_name)," +
                    "UNIQUE KEY unique_code_player (code, player_name)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            statement.executeUpdate(createUsageTable);

            plugin.getLogger().info("数据库表已创建或已存在。");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "创建数据库表时出错！", e);
        }
    }

    public boolean isConnected() {
        try { && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private void checkConnection() throws SQLException {
        if (!isConnected()) {
            plugin.getLogger().warning("MySQL连接已断开，正在尝试重新连接...");ion() throws SQLException {
        if (!isConnected()) {
            connect();
        }
    }

    public boolean saveCode(String code, long expireAt, List<String> commands) {
        try {
            checkConnection();
            
            // 插入兑换码
            String insertCode = "INSERT INTO redeem_codes (code, expire_at) VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE expire_at = ?";
            try (PreparedStatement ps = connection.prepareStatement(insertCode)) {
                ps.setString(1, code);
                ps.setLong(2, expireAt);
                ps.setLong(3, expireAt);
                ps.executeUpdate();
            }

            // 删除旧命令
            String deleteCommands = "DELETE FROM redeem_commands WHERE code = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteCommands)) {
                ps.setString(1, code);
                ps.executeUpdate();
            }

            // 插入新命令
            String insertCommand = "INSERT INTO redeem_commands (code, command, command_order) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insertCommand)) {
                for (int i = 0; i < commands.size(); i++) {
                    ps.setString(1, code);
                    ps.setString(2, commands.get(i));
                    ps.setInt(3, i);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "保存兑换码到数据库时出错！", e);
            return false;
        }
    }

    public boolean isValidCode(String code) {
        try {
            checkConnection();
            String query = "SELECT code FROM redeem_codes WHERE code = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "检查兑换码有效性时出错！", e);
            return false;
        }
    }

    public List<String> getCommands(String code) {
        List<String> commands = new ArrayList<>();
        try {
            checkConnection();
            String query = "SELECT command FROM redeem_commands WHERE code = ? ORDER BY command_order";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        commands.add(rs.getString("command"));
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取兑换码命令时出错！", e);
        }
        return commands;
    }

    public long getExpireTime(String code) {
        try {
            checkConnection();
            String query = "SELECT expire_at FROM redeem_codes WHERE code = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("expire_at");
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取兑换码过期时间时出错！", e);
        }
        return 0;
    }

    public boolean hasPlayerUsed(String code, String playerName) {
        try {
            checkConnection();
            String query = "SELECT id FROM redeem_usage WHERE code = ? AND player_name = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, code);
                ps.setString(2, playerName);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "检查玩家是否使用过兑换码时出错！", e);
            return false;
        }
    }

    public boolean savePlayerUsage(String code, String playerName) {
        try {
            checkConnection();
            String insert = "INSERT INTO redeem_usage (code, player_name) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(insert)) {
                ps.setString(1, code);
                ps.setString(2, playerName);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "保存玩家使用记录时出错！", e);
            return false;
        }
    }

    public List<String> getUsedPlayers(String code) {
        List<String> players = new ArrayList<>();
        try {
            checkConnection();
            String query = "SELECT player_name FROM redeem_usage WHERE code = ? ORDER BY used_at";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, code);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        players.add(rs.getString("player_name"));
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "获取使用过兑换码的玩家列表时出错！", e);
        }
        return players;
    }

    @Override
    public String getStorageType() {
        return "MySQL";
    }
}
