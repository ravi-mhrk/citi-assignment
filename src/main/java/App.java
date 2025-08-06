import com.citi.assignment.ATM;
import com.citi.assignment.InsufficientFundsException;
import com.citi.assignment.InvalidAmountException;
import com.citi.assignment.WithdrawalProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Running ATM application");
        App.testSingleTransaction();
        App.parallelTransactions();
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
            Throwable cause = e.getCause();
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
    }


