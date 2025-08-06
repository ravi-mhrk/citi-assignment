package com.example.assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ATMTest {
    private ATM atm;

    @BeforeEach
    void setUp() {
        Map<Integer, Integer> initialDenominations = new HashMap<>();
        initialDenominations.put(500, 5); // 5 x Rs500 notes
        initialDenominations.put(200, 10); // 10 x Rs200 notes
        initialDenominations.put(100, 20); // 20 x Rs100 notes
        initialDenominations.put(50, 20); // 20 x Rs50 notes
        initialDenominations.put(20, 50); // 50 x Rs20 notes
        initialDenominations.put(10, 100); // 100 x Rs10 notes
        atm = new ATM(initialDenominations);
    }

    @Test
    void testSuccessfulWithdrawal() throws InsufficientFundsException, InvalidAmountException {
        Map<Integer, Integer> dispensedNotes = atm.withdraw(770);
        assertEquals(1, dispensedNotes.get(500));
        assertEquals(1, dispensedNotes.get(200));
        assertEquals(1, dispensedNotes.get(50));
        assertEquals(1, dispensedNotes.get(20));
        assertNull(dispensedNotes.get(10)); // Should not dispense $10 notes for $770
        assertEquals(4, atm.getDenominations().get(500));
        assertEquals(9, atm.getDenominations().get(200));
        assertEquals(19, atm.getDenominations().get(50));
        assertEquals(49, atm.getDenominations().get(20));
    }

    @Test
    void testWithdrawalWithInsufficientFunds() {
        assertThrows(InsufficientFundsException.class, () -> atm.withdraw(50000)); // Trying to withdraw more than available
    }

    @Test
    void testWithdrawalOfInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(75)); // Not a multiple of 10
        assertThrows(InvalidAmountException.class, () -> atm.withdraw(-100)); // Negative amount
    }

    @Test
    void testWithdrawalWithOnlySpecificDenominations() throws InsufficientFundsException, InvalidAmountException {
        // Clear existing denominations and set specific ones
        atm = new ATM(Map.of(100, 2, 50, 1)); 

        Map<Integer, Integer> dispensedNotes = atm.withdraw(250); // Need two $100 notes and one $50 note
        assertEquals(2, dispensedNotes.get(100));
        assertEquals(1, dispensedNotes.get(50));
        assertEquals(0, atm.getDenominations().get(100));
        assertEquals(0, atm.getDenominations().get(50));
    }

    @Test
    void testParallelWithdrawals() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future<Map<Integer, Integer>> withdrawal1 = executorService.submit(new WithdrawalProcessor(atm, 300));
        Future<Map<Integer, Integer>> withdrawal2 = executorService.submit(new WithdrawalProcessor(atm, 200));
        Future<Map<Integer, Integer>> withdrawal3 = executorService.submit(new WithdrawalProcessor(atm, 100));

        try {
            Map<Integer, Integer> dispensed1 = withdrawal1.get();
            Map<Integer, Integer> dispensed2 = withdrawal2.get();
            Map<Integer, Integer> dispensed3 = withdrawal3.get();

            // Verify dispensed notes for each withdrawal
            assertEquals(1, dispensed1.get(200)); 
            assertEquals(1, dispensed1.get(100));
            assertEquals(1, dispensed2.get(200));
            assertEquals(1, dispensed3.get(100));

            // Verify remaining denominations in ATM
            assertEquals(5, atm.getDenominations().get(500));
            assertEquals(8, atm.getDenominations().get(200));
            assertEquals(18, atm.getDenominations().get(100));
            assertEquals(20, atm.getDenominations().get(50));

        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InsufficientFundsException) {
                fail("One of the parallel withdrawals failed due to InsufficientFundsException.");
            } else if (cause instanceof InvalidAmountException) {
                fail("One of the parallel withdrawals failed due to InvalidAmountException.");
            } else {
                fail("Unexpected exception during parallel withdrawal: " + cause.getMessage());
            }
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
