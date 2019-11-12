package io.solar.utils.db;


public class TransactionWatcher extends Thread {

    private final int maxSec;
    private final Transaction transaction;

    public TransactionWatcher(Transaction transaction, int maxSec) {
        this.transaction = transaction;
        this.maxSec = maxSec;
    }

    public void run() {
        int sec = 0;
        while(sec < maxSec) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sec++;
            if(transaction.isFinished()) {
                return;
            }
        }
        transaction.rollback();
    }
}
