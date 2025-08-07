import com.example.assignment.ATM;
import com.example.assignment.ATMController;
import com.example.assignment.Currency;
import com.example.assignment.WithdrawalProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Running ATM application");
        App.testSingleTransaction();
        App.parallelTransactions();
        App.multipleCurrencies();
    }

    public static void testSingleTransaction() throws Exception {

        ATM atm;
        Map<Integer, Integer> initialDenominations = new HashMap<>();
        initialDenominations.put(500, 5); // 5 x Rs500 notes
        initialDenominations.put(200, 10); // 10 x Rs200 notes
        initialDenominations.put(100, 20); // 20 x Rs100 notes
        initialDenominations.put(50, 20); // 20 x Rs50 notes
        initialDenominations.put(20, 50); // 50 x Rs20 notes
        initialDenominations.put(10, 100); // 100 x Rs10 notes
        atm = new ATM(initialDenominations);

        System.out.println("Withdrawing 770 dollars");
        Map<Integer, Integer> dispensedNotes = atm.withdraw(770);
        dispensedNotes.forEach((key, value) -> System.out.println("Denomination:Rs" + key + " x " + value));
        System.out.println("Denominations in ATM after withdrawl of 770");
        Map<Integer, Integer> denominations = atm.getDenominations();
        denominations.forEach((key, value) -> System.out.println("Denomination:Rs" + key + " x " + value));
    }

    public static void parallelTransactions() throws Exception{
        System.out.println("Running Parallel Transactions");
        ATM atm;
        Map<Integer, Integer> initialDenominations = new HashMap<>();
        initialDenominations.put(500, 5); // 5 x Rs500 notes
        initialDenominations.put(200, 10); // 10 x Rs200 notes
        initialDenominations.put(100, 20); // 20 x Rs100 notes
        initialDenominations.put(50, 20); // 20 x Rs50 notes
        initialDenominations.put(20, 50); // 50 x Rs20 notes
        initialDenominations.put(10, 100); // 100 x Rs10 notes
        atm = new ATM(initialDenominations);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future<Map<Integer, Integer>> withdrawal1 = executorService.submit(new WithdrawalProcessor(atm, 300));
        Future<Map<Integer, Integer>> withdrawal2 = executorService.submit(new WithdrawalProcessor(atm, 200));
        Future<Map<Integer, Integer>> withdrawal3 = executorService.submit(new WithdrawalProcessor(atm, 100));

        try {
            Map<Integer, Integer> dispensed1 = withdrawal1.get();
            Map<Integer, Integer> dispensed2 = withdrawal2.get();
            Map<Integer, Integer> dispensed3 = withdrawal3.get();

            // Verify dispensed notes for each withdrawal
            dispensed1.forEach((key, value) -> System.out.println("T1::Denomination:Rs" + key + " x " + value));
            dispensed2.forEach((key, value) -> System.out.println("T2::Denomination:Rs" + key + " x " + value));
            dispensed3.forEach((key, value) -> System.out.println("T3::Denomination:Rs" + key + " x " + value));


            System.out.println("Denominations in ATM after 3 parallel transactions ");
            Map<Integer, Integer> denominations = atm.getDenominations();
            denominations.forEach((key, value) -> System.out.println("Denomination:Rs" + key + " x " + value));


        } catch (ExecutionException e) {
          //  Throwable cause = e.getCause();
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    public static void multipleCurrencies() throws Exception{
        System.out.println("Running Multiple CUrriences");
        // --- Initialize Denominations for Multiple Currencies ---
        Map<Currency, Map<Integer, Integer>> initialCurrencyDenominations = new HashMap<>();

        // USD Denominations for the  ATM instance.
        Map<Integer, Integer> usdDenominations = new HashMap<>();
        usdDenominations.put(500, 5);
        usdDenominations.put(200, 10);
        usdDenominations.put(100, 20);
        usdDenominations.put(50, 20);
        usdDenominations.put(20, 50);
        usdDenominations.put(10, 100);
        initialCurrencyDenominations.put(Currency.USD, usdDenominations);

        // EUR Denominations for a new ATM instance
        Map<Integer, Integer> eurDenominations = new HashMap<>();
        eurDenominations.put(500, 2);
        eurDenominations.put(200, 5);
        eurDenominations.put(100, 10);
        eurDenominations.put(50, 10);
        eurDenominations.put(20, 25);
        eurDenominations.put(10, 50);
        initialCurrencyDenominations.put(Currency.EUR, eurDenominations);

        // GBP Denominations (example for testing addCurrencySupport)
        Map<Integer, Integer> gbpDenominations = new HashMap<>();
        gbpDenominations.put(50, 10);
        gbpDenominations.put(20, 20);
        gbpDenominations.put(10, 50);

        ATMController atmController = new ATMController(initialCurrencyDenominations);
        Map<Integer, Integer> dispensedNotes = atmController.withdraw(770, Currency.USD);
        dispensedNotes.forEach((key, value) -> System.out.println("Denomination:Rs" + key + " x " + value));

        System.out.println("Denominations in ATM after withdrawl of 770");
        Map<Integer, Integer> denominations = atmController.getDenominations(Currency.USD);
        denominations.forEach((key, value) -> System.out.println(Currency.USD+"Denomination:" + key + " x " + value));

    }

}



