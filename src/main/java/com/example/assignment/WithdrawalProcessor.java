package com.example.assignment;

import java.util.Map;
import java.util.concurrent.Callable;

public class WithdrawalProcessor implements Callable<Map<Integer, Integer>> {
    private final ATM atm;
    private final int amount;

    public WithdrawalProcessor(ATM atm, int amount) {
        this.atm = atm;
        this.amount = amount;
    }

    @Override
    public Map<Integer, Integer> call() throws InsufficientFundsException, InvalidAmountException {
        return atm.withdraw(amount);
    }
}
