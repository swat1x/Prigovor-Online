package prigovor.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.dbutils.QueryRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HikariDatabase implements Database {

    ExecutorService executor;
    HikariDataSource dataSource;

    public HikariDatabase(
            String poolName, String driver, String driverUrl,
            String host, int port,
            String database, String user, String password) {
        if (driverUrl == null) {
            driverUrl = "jdbc:mysql";
        }

        ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setNameFormat("mysql-worker-%d").build();
        this.executor = Executors.newFixedThreadPool(4, threadFactory);

        HikariConfig config = new HikariConfig();
        config.setPoolName(poolName);
        config.setJdbcUrl(driverUrl + "://" + host + ":" + port + "/" + database + "?useUnicode=yes&useSSL=false&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&jdbcCompliantTruncation=false");
        config.setUsername(user);
        config.setPassword(password);
        config.setConnectionTimeout(30000L);
        config.setIdleTimeout(600000L);
        config.setMaxLifetime(1800000L);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        // Установка драйвера если нужно
        if (driver != null) {
            config.setDriverClassName(driver);
        }

        this.dataSource = new HikariDataSource(config);
    }

    @Override
    public QueryRunner sync() {
        return new QueryRunner(dataSource);
    }

    @Override
    public CompletableQueryRunner async() {
        return new CompletableQueryRunner(executor, sync());
    }
}
