package io.solar.utils.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Pool {
    private final String url;
    private final String username;
    private final String password;

    private final List<ConnectionInUse> pool;

    private static Pool instance;

    public static void init(String url, String username,String password, int size) {
        instance = new Pool(url, username, password, size);
    }

    public Pool(String url, String username,String  password, int size) {
        this.url = url;
        this.username = username;
        this.password = password;
        pool = new ArrayList<>(size);
        while(size > 0) {
            pool.add(new ConnectionInUse(createConnection()));
            size--;
        }
        keepConnectionsAlive();
    }

    private void keepConnectionsAlive() {
        (new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (pool) {
                    Thread current = Thread.currentThread();
                    for (ConnectionInUse con : instance.pool) {
                        Connection connection = con.use(current);
                        if (connection != null) {
                            try {
                                PreparedStatement statement = connection.prepareStatement("select 1");
                                statement.execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            con.release(current);
                        }
                    }
                }
            }
        })).start();
    }

    public static Connection getConnection() {
        Thread current = Thread.currentThread();
        synchronized (instance.pool) {
            for (ConnectionInUse con : instance.pool) {
                Connection connection = con.use(current);
                if(connection != null) {
                    return connection;
                }
            }
        }
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getConnection();
    }

    public static void release() {
        Thread current = Thread.currentThread();
        synchronized (instance.pool) {
            for (ConnectionInUse con : instance.pool) {
                if(con.release(current)) {
                    break;
                }
            }
        }
    }

    private Connection createConnection() {
        try {
            Connection out = DriverManager.getConnection(url, username, password);
            out.setAutoCommit(false);
            return out;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("can't create db connection");
        }
    }
}
