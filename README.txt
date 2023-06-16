#Steps to run the transaction-manager

Step-1:
Make sure you have the following installed and working in your system.
 - Git
 - Maven 3+
 - Java 8+
Clone the repository from https://github.com/Abdullah8006/transaction-manager.git using the following command
 - git clone https://github.com/Abdullah8006/transaction-manager.git

Step-2:
Open the transaction-manager directory in your terminal/command prompt and run
 - mvn package

Step-3:
After the app is packaged and the jar is built run the jar present in the target folder and provide the complete file path for transactions.json file
 -  java -jar ./target/transaction-manager-0.1.0.jar /home/abd/Downloads/transactions.json

Step-4
Check the logs for the events triggered.

Step-5
Exit the running process by hitting the following keys
 - CTRL+C
