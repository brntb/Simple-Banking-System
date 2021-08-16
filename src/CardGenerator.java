import java.util.Random;

public class CardGenerator {

    private final Random random = new Random();

    /**
     *
     * @return  return a valid 16 digit visa number as a string
     */
    public String generateVisaCard() {
        String bankIdentificationNumber = "400000";
        String accountNumber;

        //accountNumber ranges from 100_000 - 999_999
        int lower = 100_000_000;
        int upper = 999_999_999;

        int randomNumber = random.nextInt(upper - lower + 1) + lower;
        accountNumber = String.valueOf(randomNumber);
        String checkSum = String.valueOf(getCheckSum(bankIdentificationNumber + accountNumber));

        return bankIdentificationNumber + accountNumber + checkSum;
    }

    /**
     *
     * @param number number to find check sum for
     * @return       proper check sum number to make valid card
     */
    private int getCheckSum(String number) {
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

        //now answer the question, sum + x = multiple of 10
        if (sum % 10 == 0) {
            return 0;
        } else {
            int rounded = (10 - sum % 10) + sum;
            return rounded - sum;
        }
    }

    /**
     *
     * @return    return a four digit random number as string
     */
    public String generatePIN() {
        int lower = 1_000;
        int upper = 9_999;
        int pin = random.nextInt(upper - lower + 1) + lower;

        return String.valueOf(pin);
    }



}
