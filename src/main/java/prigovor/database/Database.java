package prigovor.database;

import org.apache.commons.dbutils.QueryRunner;

public interface Database {

    QueryRunner sync();

    CompletableQueryRunner async();

}
