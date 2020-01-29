package io.solar.utils.server.params;

import io.solar.utils.db.Transaction;

import java.lang.reflect.Parameter;
import java.sql.SQLException;

public class TransactionParamUtils {

    public static boolean process(Object[] args, Parameter param, int i, Transaction transaction) throws SQLException {
        if(param.getType().equals(Transaction.class)) {
            if(transaction == null) {
                args[i] = Transaction.begin();
            } else {
                args[i] = transaction;
            }
            return true;
        }
        return false;
    }

}
