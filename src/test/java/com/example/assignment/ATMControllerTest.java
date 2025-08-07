package com.example.assignment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class ATMControllerTest {
    ATMController atmController;

    @BeforeEach
    void setUp() {
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

        // GBP Denominations
        Map<Integer, Integer> gbpDenominations = new HashMap<>();
        gbpDenominations.put(50, 10);
        gbpDenominations.put(20, 20);
        gbpDenominations.put(10, 50);
        initialCurrencyDenominations.put(Currency.GBP, eurDenominations);

        atmController = new ATMController(initialCurrencyDenominations);
    }

    @Test
    void testWithdrawalWithInsufficientFunds() {
        assertThrows(InsufficientFundsException.class, () -> atmController.withdraw(50000,Currency.USD)); // Trying to withdraw more than available
    }

    @Test
    void testWithdrawalOfInvalidAmount() {
        assertThrows(InvalidAmountException.class, () -> atmController.withdraw(75,Currency.INR)); // Not a multiple of 10
        assertThrows(InvalidAmountException.class, () -> atmController.withdraw(-100,Currency.GBP)); // Negative amount
    }

    @Test
    void testParallelWithdrawals() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Future<Map<Integer, Integer>> usdWithdrawal1 = executorService.submit(new WithdrawalProcessorForController(atmController, 300,Currency.USD));
        Future<Map<Integer, Integer>> usdWithdrawal2 = executorService.submit(new WithdrawalProcessorForController(atmController, 200,Currency.USD));
        Future<Map<Integer, Integer>> usdWithdrawal3 = executorService.submit(new WithdrawalProcessorForController(atmController, 100,Currency.USD));

        Future<Map<Integer, Integer>> gbpWithdrawal1 = executorService.submit(new WithdrawalProcessorForController(atmController, 300,Currency.GBP));
        Future<Map<Integer, Integer>> gbpWithdrawal2 = executorService.submit(new WithdrawalProcessorForController(atmController, 200,Currency.GBP));
        Future<Map<Integer, Integer>> gbpWithdrawal3 = executorService.submit(new WithdrawalProcessorForController(atmController, 100,Currency.GBP));
        try {
            Map<Integer, Integer> usdDispensed1 = usdWithdrawal1.get();
            Map<Integer, Integer> usdDispensed2 = usdWithdrawal2.get();
            Map<Integer, Integer> usdDispensed3 = usdWithdrawal3.get();
            Map<Integer, Integer> gbpDispensed1 = gbpWithdrawal1.get();
            Map<Integer, Integer> gbpDispensed2 = gbpWithdrawal2.get();
            Map<Integer, Integer> gbpDispensed3 = gbpWithdrawal3.get();

            // Verify dispensed notes for each withdrawal
            assertEquals(1, usdDispensed1.get(200));
            assertEquals(1, usdDispensed1.get(100));
            assertEquals(1, usdDispensed2.get(200));
            assertEquals(1, usdDispensed3.get(100));

            assertEquals(1, gbpDispensed1.get(200));
            assertEquals(1, usdDispensed1.get(100));
            assertEquals(1, gbpDispensed2.get(200));
            assertEquals(1, gbpDispensed3.get(100));

            // Verify remaining denominations in ATM
            assertEquals(5, atmController.getDenominations(Currency.USD).get(500));
            assertEquals(8, atmController.getDenominations(Currency.USD).get(200));
            assertEquals(18, atmController.getDenominations(Currency.USD).get(100));
            assertEquals(20, atmController.getDenominations(Currency.USD).get(50));

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
