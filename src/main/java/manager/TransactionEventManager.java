package manager;

import model.Event;
import model.EventLog;
import model.Transaction;

import java.util.concurrent.BlockingQueue;

public class TransactionEventManager implements Runnable {

    private final BlockingQueue<Event> eventQueue;

    public TransactionEventManager(BlockingQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    @Override
    public void run() {
        System.out.println("Initializing thread 3");
        try {
            while (true) {
                Event event = eventQueue.take();
                Transaction transaction = event.getTransaction();

                System.out.println(new EventLog(transaction.getAccNumber(), event.getRuleId(), transaction));
            }
        } catch (Exception e) {
            System.out.println("Unable to process/log the event, Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
