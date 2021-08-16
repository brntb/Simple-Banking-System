import java.util.Scanner;

public class Bank {

    private final Scanner scanner;
    private final CardGenerator cardGenerator;
    private final DatabaseOperations databaseOperations;
    private boolean isOpen = true;

    public Bank(Scanner scanner, DatabaseOperations databaseOperations) {
        this.scanner = scanner;
        this.databaseOperations = databaseOperations;
        this.cardGenerator = new CardGenerator();
    }

    public void run() {

        while (isOpen) {
            String menu = "1. Create an account\n2. Log into account\n0. Exit";
            System.out.println(menu);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    createAccount();
                    break;

                case "2":
                    login();
                    break;

                case "0":
                    System.out.println("\nBye!");
                    isOpen = false;
                    break;

                default:
                    System.out.println("\nUnknown Bank Operation. Try again!\n");
            }
        }
    }

    /**
     * creates a new number and pin for user
     */
    private void createAccount() {
        String number = cardGenerator.generateVisaCard();
        String pin = cardGenerator.generatePIN();

        System.out.println("\nYour card has been created");
        System.out.println("Your card number:");
        System.out.println(number);
        System.out.println("Your card PIN:");
        System.out.println(pin + "\n");

        databaseOperations.insertNewCard(new Card(number, pin, 0));
    }


    /**
     *
     * @return    returns card from database if found, else returns null
     */
    private Card findCard() {
        System.out.println("\nEnter your card number:");
        String number = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        Card foundCard = databaseOperations.findCard(number);

        //verify pin
        if (foundCard !=null && !foundCard.getPin().equals(pin)) {
            return null;
        }

        return databaseOperations.findCard(number);
    }

    /**
     * logs into a user account
     */
    private void login() {
        Card card = findCard();

        if (card == null) {
            System.out.println("\nWrong card number or PIN!\n");
            return;
        } else {
            System.out.println("\nYou have successfully logged in!\n");
        }

        String userMenu = "1. Balance\n" +
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit";

        boolean isLoggedIn = true;

        while (isLoggedIn) {
            System.out.println(userMenu);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayBalance(card);
                    break;

                case "2":
                    addIncome(card);
                    break;

                case "3":
                    transferIncome(card);
                    break;

                case "4":
                    closeAccount(card);
                    break;

                case "5":
                    System.out.println("\nYou have successfully logged out!\n");
                    isLoggedIn = false;
                    break;

                case "0":
                    isOpen = false;
                    isLoggedIn = false;
                    break;

                default:
                    System.out.println("\nUnknown Operation. Try again!\n");
            }
        }
    }

    /**
     *
     * @param card   card to have balance printed
     */
    private void displayBalance(Card card) {
        System.out.println("\nBalance: " + card.getBalance() + "\n");
    }

    /**
     *
     * @param card  card to have income added to
     */
    private void addIncome(Card card) {
        System.out.println("\nEnter income: ");

        try {
            int incomeToAdd = Integer.parseInt(scanner.nextLine());
            int currentBalance = card.getBalance();
            int newBalance = incomeToAdd + currentBalance;
            card.setBalance(newBalance);

            //save to database
            databaseOperations.updateBalance(card, card.getBalance());
            System.out.println("Income was added!\n");

        } catch (NumberFormatException e) {
            System.out.println("\nInvalid income entered!\n");
        }
    }

    /**
     *
     * @param from  transfer income from a card to another card
     */
    private void transferIncome(Card from) {
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        String number = scanner.nextLine();

        //first check if number is actually a valid credit card number
        if (!isValidCardNumber(number)) {
            System.out.println("\nProbably you made a mistake in the card number. Please try again!\n");
            return;
        }

        //now check if card is actually in database
        Card to = databaseOperations.findCard(number);

        if (to == null) {
            System.out.println("\nSuch a card does not exist.\n");
            return;
        }

        //card exists at this point, get transfer amount
        System.out.println("Enter how much money you want to transfer:");
        int amount;

        try {
            amount = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount!\n");
            return;
        }

        //now check if card has enough money to transfer
        if (from.getBalance() < amount) {
            System.out.println("Not enough money!\n");
            return;
        }

        //card has enough money, do transfer
        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        //save changes to database
        databaseOperations.updateBalance(from, from.getBalance());
        databaseOperations.updateBalance(to, to.getBalance());

        System.out.println("Success!\n");
    }

    /**
     *
     * @param card  card to be removed from database
     */
    private void closeAccount(Card card) {
        boolean isRemoved = databaseOperations.removeCard(card);

        if (isRemoved) {
            System.out.println("\nThe account has been closed!\n");
        } else {
            System.out.println("\nError closing account!\n");
        }
    }

    /**
     *
     * @param number     visa number to validate
     * @return           true if card passes luhn algorithm else false
     */
    public boolean isValidCardNumber(String number) {
        if (number.length() != 16) {
            return false;
        }

        //todo some duplicate code here
        //using Luhn algorithm to find checksum
        int[] holder = new int[number.length()];

        //first multiply odd digits by two
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));

            if (i % 2 == 0) {
                digit *= 2;
            }
            holder[i] = digit;
        }

        //subtract 9 from digits over 9
        for (int i = 0; i < number.length(); i++) {
            int digit = holder[i];

            if (digit > 9) {
                holder[i] = digit - 9;
            }
        }

        //add all digits
        int sum = 0;
        for (int digit : holder) {
            sum += digit;
        }

        return sum % 10 == 0;
    }


}
