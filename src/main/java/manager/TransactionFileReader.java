package manager;

import com.google.gson.Gson;
import model.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class TransactionFileReader implements Runnable {

    private final BlockingQueue<Transaction> queue;
    private final String transactionFilePath;
    private final Gson gson;

    public TransactionFileReader(BlockingQueue<Transaction> queue, String transactionFilePath, Gson gson) {
        this.queue = queue;
        this.transactionFilePath = transactionFilePath;
        this.gson = gson;
    }

    @Override
    public void run() {
        System.out.println("Initializing thread 1");
        try {
            List<String> transactionLines = Files.readAllLines(Paths.get(transactionFilePath));

            for (String line : transactionLines) {
                Transaction transaction = gson.fromJson(line, Transaction.class);
                queue.put(transaction);
            }
        } catch (IOException e) {
            System.out.println("Unable to process the transaction file, Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Unable to process the transaction file, Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
