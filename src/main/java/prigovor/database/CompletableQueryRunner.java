package prigovor.database;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompletableQueryRunner extends AsyncQueryRunner {

    ExecutorService executorService;
    QueryRunner queryRunner;

    public CompletableQueryRunner(ExecutorService executorService, QueryRunner queryRunner) {
        super(executorService, queryRunner);
        this.executorService = executorService;
        this.queryRunner = queryRunner;
    }

    @Override
    public CompletableFuture<Integer> update(String sql) {
        return asFuture(()-> {
            try {
                return queryRunner.update(sql);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Integer> update(String sql, Object... params) {
        return asFuture(()-> {
            try {
                return queryRunner.update(sql, params);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T> Future<T> query(String sql, ResultSetHandler<T> rsh) {
        return asFuture(()-> {
            try {
                return queryRunner.query(sql, rsh);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T> Future<T> query(String sql, ResultSetHandler<T> rsh, Object... params) {
        return asFuture(()-> {
            try {
                return queryRunner.query(sql, rsh, params);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private <T> CompletableFuture<T> asFuture(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, this.executorService);
    }

}
