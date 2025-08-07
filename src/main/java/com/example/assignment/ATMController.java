package com.example.assignment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ATMController {
    private final ConcurrentHashMap<Currency, ATM> atmInstancesByCurrency;
    private final Lock controllerLock = new ReentrantLock(); // Lock for operations involving multiple ATMs or adding new currencies


    public ATMController(Map<Currency, Map<Integer, Integer>> initialCurrencyDenominations) {
        this.atmInstancesByCurrency = new ConcurrentHashMap<>();
        for (Map.Entry<Currency, Map<Integer, Integer>> entry : initialCurrencyDenominations.entrySet()) {
            this.atmInstancesByCurrency.put(entry.getKey(), new ATM(entry.getValue()));
        }
    }

    public Map<Integer, Integer> withdraw(int amount, Currency currency) throws
            InsufficientFundsException, InvalidAmountException, InvalidCurrencyException {
        // Validation for currency support
        if (!atmInstancesByCurrency.containsKey(currency)) {
            throw new InvalidAmountException("ATM does not support withdrawals in " + currency.name());
        }

        ATM specificATM = atmInstancesByCurrency.get(currency);
        if (specificATM == null) {
            throw new InvalidCurrencyException("ATM instance not found for currency: " + currency.name());
        }

        return specificATM.withdraw(amount);
    }

    public Map<Integer, Integer> getDenominations(Currency currency) {
        ATM atm = atmInstancesByCurrency.get(currency);
        if (atm == null) {
            return new ConcurrentHashMap<>(); // Return empty map if currency not supported
        }
        return atm.getDenominations();
    }

    public void addCurrencySupport(Currency currency, Map<Integer, Integer> initialDenominations) {
        controllerLock.lock(); // Protects against adding the same currency concurrently
        try {
            if (atmInstancesByCurrency.containsKey(currency)) {
                throw new IllegalArgumentException("Currency " + currency.name() + " is already supported.");
            }
            atmInstancesByCurrency.put(currency, new ATM(initialDenominations));
        } finally {
            controllerLock.unlock();
        }
    }

}
