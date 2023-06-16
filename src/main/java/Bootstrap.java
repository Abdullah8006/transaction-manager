import com.google.gson.*;
import model.Event;
import model.EventLog;
import model.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;

public class Bootstrap {

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong() * 1000).atZone(ZoneId.of("GMT")).toLocalDateTime()).create();

    private static final BlockingQueue<Transaction> transactionQueue = new ArrayBlockingQueue<>(1);
    private static final BlockingQueue<Event> eventQueue = new LinkedBlockingDeque<>();

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a valid file path for the transactions.json file");

            return;
        }

        final String transactionFilePath = args[0];
        System.out.println("Transaction file path: " + transactionFilePath);

        final Map<String, List<Transaction>> userTransactions = new HashMap<>();
        System.out.println("Initializing the worker threads");

        Thread th1 = new Thread(() -> {
            System.out.println("Initializing thread 1");
            try {
                List<String> transactionLines = Files.readAllLines(Paths.get(transactionFilePath));

                for (String line : transactionLines) {
                    Transaction transaction = gson.fromJson(line, Transaction.class);
                    transactionQueue.put(transaction);
                }
            } catch (IOException e) {
                System.out.println("Unable to process the transaction file, Error: " + e.getMessage());
                e.printStackTrace();
            } catch (InterruptedException e) {
                System.out.println("Unable to process the transaction file, Error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });

        Thread th2 = new Thread(() -> {
            System.out.println("Initializing thread 2");
            try {
                while (true) {
                    Transaction activeTransaction = transactionQueue.take();
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
        });

        Thread th3 = new Thread(() -> {
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
        });

        th1.start();
        th2.start();
        th3.start();
    }

    private static void process(Transaction activeTransaction, List<Transaction> userTransactions) {
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
    private static boolean processRule2(LocalDateTime tranTime, List<Transaction> userTransactions) {
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
