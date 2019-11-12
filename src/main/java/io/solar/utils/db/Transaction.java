package io.solar.utils.db;

import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

    private final Connection connection;
    private boolean finished;

    public static Transaction begin() throws SQLException {
        return begin(3600);
    }
    public static Transaction begin(int secLifetime) {
        Connection connection = Pool.getConnection();
        return new Transaction(connection, secLifetime);
    }

    private Transaction(Connection connection, int maxSec) {
        finished = false;
        this.connection = connection;
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("could not set autocommit false for transaction instantiation");
        }
        new TransactionWatcher(this, maxSec).start();
    }

    public Query query(String query) {
        return new Query(connection, query);
    }

    public void commit() {
        end(true);
    }
    public void rollback() {
        end(false);
    }
    private void end(boolean status) {
        synchronized (this) {
            if (finished) {
                return;
            }
            finished = true;
            try {
                if (status) {
                    connection.commit();
                } else {
                    connection.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Pool.release();
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
