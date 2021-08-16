import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseOperations databaseOperations;
        Scanner scanner = new Scanner(System.in);

        //check if database is passed as argument
        if (args.length == 2 && args[0].equals("-fileName")) {
            databaseOperations = new DatabaseOperations(args[1]);
        } else {
            databaseOperations = new DatabaseOperations();
        }

        Bank bank = new Bank(scanner, databaseOperations);
        bank.run();
    }
}
