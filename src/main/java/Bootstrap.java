import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import manager.TransactionEventManager;
import manager.TransactionFileReader;
import manager.TransactionProcessor;
import model.Event;
import model.Transaction;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Bootstrap {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please provide a valid file path for the transactions.json file");

            return;
        }

        final String transactionFilePath = args[0];
        System.out.println("Transaction file path: " + transactionFilePath);

        final Map<String, List<Transaction>> userTransactions = new HashMap<>();
        System.out.println("Initializing the worker threads");

        final BlockingQueue<Transaction> transactionQueue = new ArrayBlockingQueue<>(20);
        final BlockingQueue<Event> eventQueue = new LinkedBlockingDeque<>();

        final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext) -> Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong() * 1000).atZone(ZoneId.of("GMT")).toLocalDateTime()).create();

        TransactionFileReader transactionFileReader = new TransactionFileReader(transactionQueue, transactionFilePath, gson);
        TransactionProcessor transactionProcessor = new TransactionProcessor(transactionQueue, userTransactions, eventQueue);
        TransactionEventManager transactionEventManager = new TransactionEventManager(eventQueue);

        Thread th1 = new Thread(transactionFileReader);
        Thread th2 = new Thread(transactionProcessor);
        Thread th3 = new Thread(transactionEventManager);

        th1.start();
        th2.start();
        th3.start();
    }

}
