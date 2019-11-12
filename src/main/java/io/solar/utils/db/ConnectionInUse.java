package io.solar.utils.db;

import java.sql.Connection;

public class ConnectionInUse {
    private final Connection connection;
    private boolean inUse;
    private Thread thread;

    public ConnectionInUse(Connection connection) {
        this.connection = connection;
        inUse = false;
        thread = null;
    }

    public Connection use(Thread thread) {
        if(inUse && thread != this.thread) {
            return null;
        }
        this.thread = thread;
        inUse = true;
        return connection;
    }

    public boolean release(Thread thread) {
        if(inUse && this.thread == thread) {
            inUse = false;
            this.thread = null;
            return true;
        }
        return false;
    }
}
