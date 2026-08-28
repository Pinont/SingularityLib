package com.github.pinont.singularitylib.api.storage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;

/**
 * Async-first database facade (Phase 2). Supports MySQL and SQLite.
 *
 * <p>Usage:
 * <pre>{@code
 * Database db = Database.sqlite(plugin, "data.db");           // or .mySql(plugin, cfg)
 * db.update("CREATE TABLE IF NOT EXISTS ...");
 * db.query(conn -> { ... return rs ...; }).thenAccept(...);
 * }</pre>
 *
 * <p>All query/update methods run on a shared background executor (never the main
 * thread), and you schedule UI work back onto the server via the lib's Scheduler facade.
 */
public abstract class Database implements AutoCloseable {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "singularity-db");
        t.setDaemon(true);
        return t;
    });

    protected final Plugin plugin;
    private volatile Connection connection;

    protected Database(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Opens (and caches) a raw connection.
     */
    public abstract Connection openConnection() throws SQLException;

    /**
     * @return a cached, open connection
     */
    public Connection getConnection() throws SQLException {
        Connection c = connection;
        if (c == null || c.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    connection = openConnection();
                }
                return connection;
            }
        }
        return c;
    }

    /**
     * Executes an update/DDL statement asynchronously.
     *
     * @param sql        the SQL
     * @param parameters optional positional parameters (?)
     * @return a future completing when the update is done
     */
    public CompletableFuture<Void> update(String sql, Object... parameters) {
        return runAsync(conn -> {
            try (var ps = conn.prepareStatement(sql)) {
                bind(ps, parameters);
                ps.executeUpdate();
            }
        });
    }

    /**
     * Runs a query asynchronously; the function maps the result set to a value.
     *
     * @param mapper receives the connection, returns a result
     * @param <T>     result type
     * @return a future with the mapped result
     */
    public <T> CompletableFuture<T> query(ThrowingFunction<Connection, T> mapper) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = getConnection()) {
                return mapper.apply(conn);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, EXECUTOR);
    }

    /**
     * Runs an async DB task with a raw connection, completing when done.
     */
    public CompletableFuture<Void> runAsync(ThrowingConsumer<Connection> task) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = getConnection()) {
                task.accept(conn);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, EXECUTOR);
    }

    private void bind(java.sql.PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            sendConsoleMessage(Component.text("[DB] Error closing connection: " + e.getMessage(), NamedTextColor.RED));
        }
        connection = null;
    }

    /**
     * Creates a MySQL-backed database from a config section (database.host/port/name/user/password).
     */
    public static Database mySql(Plugin plugin, org.bukkit.configuration.ConfigurationSection cfg) {
        return new Database(plugin) {
            @Override
            public Connection openConnection() throws SQLException {
                String host = cfg.getString("host", "localhost");
                int port = cfg.getInt("port", 3306);
                String name = cfg.getString("databaseName", "minecraft");
                String user = cfg.getString("username", "root");
                String pass = cfg.getString("password", "");
                String ssl = cfg.getString("useSSL", "false");
                String timeZone = cfg.getString("timezone", "UTC");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                        + "?useSSL=" + ssl + "&serverTimezone=" + timeZone
                        + "&autoReconnect=true";
                sendConsoleMessage(Component.text("[DB] Connecting MySQL: " + host + ":" + port + "/" + name, NamedTextColor.YELLOW));
                return DriverManager.getConnection(url, user, pass);
            }
        };
    }

    /**
     * Creates a SQLite-backed database stored at the given file.
     */
    public static Database sqlite(Plugin plugin, String fileName) {
        return new Database(plugin) {
            @Override
            public Connection openConnection() throws SQLException {
                File file = new File(plugin.getDataFolder(), fileName);
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                String url = "jdbc:sqlite:" + file.getAbsolutePath();
                sendConsoleMessage(Component.text("[DB] Connecting SQLite: " + file.getName(), NamedTextColor.YELLOW));
                Connection conn = DriverManager.getConnection(url);
                // WAL for concurrent read/write
                try (var st = conn.createStatement()) {
                    st.execute("PRAGMA journal_mode=WAL");
                    st.execute("PRAGMA busy_timeout=3000");
                }
                return conn;
            }
        };
    }

    /** A consumer that may throw. */
    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

    /** A function that may throw. */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }
}