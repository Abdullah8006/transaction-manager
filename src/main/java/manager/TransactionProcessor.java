package manager;

import model.Event;
import model.Transaction;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class TransactionProcessor implements Runnable {

    private final BlockingQueue<Transaction> queue;
    private final Map<String, List<Transaction>> userTransactions;
    private final BlockingQueue<Event> eventQueue;

    public TransactionProcessor(BlockingQueue<Transaction> queue, Map<String, List<Transaction>> userTransactions, BlockingQueue<Event> eventQueue) {
        this.queue = queue;
        this.userTransactions = userTransactions;
        this.eventQueue = eventQueue;
    }

    @Override
    public void run() {
        System.out.println("Initializing thread 2");
        try {
            while (true) {
                Transaction activeTransaction = queue.take();
                String accNumber = activeTransaction.getAccNumber();

                if (userTransactions.containsKey(accNumber)) {
                    List<Transaction> transactions = userTransactions.get(accNumber);
                    transactions.add(activeTransaction);

                    userTransactions.put(accNumber, transactions);
                } else {
                    List<Transaction> newTransactions = new ArrayList<>();
                    newTransactions.add(activeTransaction);

                    userTransactions.put(accNumber, newTransactions);
                }

                process(activeTransaction, userTransactions.get(accNumber));
            }
        } catch (InterruptedException e) {
            System.out.println("Unable to process the transaction, Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void process(Transaction activeTransaction, List<Transaction> userTransactions) {
        LocalDateTime tranTime = activeTransaction.getTranTime();

        if (activeTransaction.getTranAmount() > 10000 || processRule1(tranTime, userTransactions)) { //Process Rule 1.
            eventQueue.add(new Event("1", activeTransaction));
        } else if (activeTransaction.getTranDirection().equals("IN") && (activeTransaction.getTranAmount() > 50000 || processRule2(tranTime, userTransactions))) { //Process Rule 2
            eventQueue.add(new Event("2", activeTransaction));
        }
    }

    /**
     * Check if for a given tranTime the monthly rule satisfies for the list of user transactions.
     *
     * @param tranTime Transaction time of the active transaction
     * @param userTransactions The list of all the user transactions
     * @return true/false
     */
    private boolean processRule2(LocalDateTime tranTime, List<Transaction> userTransactions) {
        return userTransactions.stream()
                .filter(t -> t.getTranDirection().equals("IN") && YearMonth.from(t.getTranTime()).equals(YearMonth.from(tranTime.toLocalDate())))
                .map(Transaction::getTranAmount)
                .reduce(0.0, Double::sum) > 50000;
    }

    /**
     * Check if for a given tranTime the daily rule satisfies for the list of user transactions.
     *
     * @param tranTime  Transaction time of the active transaction
     * @param userTransactions The list of all the user transactions
     * @return true/false
     */
    private static boolean processRule1(LocalDateTime tranTime, List<Transaction> userTransactions) {
        return userTransactions.stream()
                .filter(t -> t.getTranTime().toLocalDate().equals(tranTime.toLocalDate()))
                .map(Transaction::getTranAmount)
                .reduce(0.0, Double::sum) > 10000;
    }

}
