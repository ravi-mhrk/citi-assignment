package com.example.assignment;

import java.util.Map;
import java.util.concurrent.Callable;

public class WithdrawalProcessorForController implements Callable<Map<Integer, Integer>> {
        private final ATMController atmController; // This refers to the new ATMController class
        private final int amount;
        private final Currency currency;

        public WithdrawalProcessorForController(ATMController atmController, int amount, Currency currency) {
            this.atmController = atmController;
            this.amount = amount;
            this.currency = currency;
        }

        @Override
        public Map<Integer, Integer> call() throws InsufficientFundsException, InvalidAmountException, InvalidCurrencyException {
            return atmController.withdraw(amount, currency);
        }

}
