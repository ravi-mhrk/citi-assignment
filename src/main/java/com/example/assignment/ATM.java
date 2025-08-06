package com.example.assignment;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ATM {
    private final ConcurrentSkipListMap<Integer, Integer> denominations; // Denomination value -> quantity
    private final Lock lock = new ReentrantLock(); // For thread safety during withdrawals

    public ATM(Map<Integer, Integer> initialDenominations) {
        // Since the requirement is to get the least number of denominations so sorting the keys
        this.denominations = new ConcurrentSkipListMap<>(new TreeMap<>(initialDenominations).descendingMap());
    }

    public Map<Integer, Integer> getDenominations() {
        return new ConcurrentSkipListMap<>(denominations); // Return a copy for immutability
    }

    public synchronized Map<Integer, Integer> withdraw(int amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0 || amount % 10 != 0) {
            throw new InvalidAmountException("Withdrawal amount must be a positive multiple of 10.");
        }

        lock.lock(); // Acquire lock for thread safety
        try {
            int remainingAmount = amount;
            int denomination;
            int availableNotes;
            NavigableMap<Integer, Integer> dispensedNotes = new TreeMap<Integer, Integer>(Integer::compare).descendingMap(); // Denominations dispensed
            //HashMap<Integer, Integer> dispensedNotes = new HashMap<>();
            for (Map.Entry<Integer, Integer> entry : denominations.entrySet()) {
                denomination = entry.getKey();
                availableNotes = entry.getValue();

                if (remainingAmount >= denomination && availableNotes > 0) {
                    int notesToDispense = Math.min(remainingAmount / denomination, availableNotes);
                    dispensedNotes.put(denomination, notesToDispense);
                    remainingAmount -= notesToDispense * denomination;
                }
            }

            if (remainingAmount > 0) {
                // If ATM does not have enough notes  then throw exception
                throw new InsufficientFundsException("Insufficient funds are available denominations to fulfill the request.");
            }

            // Decrement ATM denominations after successful withdrawal
            for (Map.Entry<Integer, Integer> entry : dispensedNotes.entrySet()) {
                denomination = entry.getKey();
                int dispensedCount = entry.getValue();
                denominations.computeIfPresent(denomination, (k, v) -> v - dispensedCount);
            }

            return dispensedNotes;

        } finally {
            lock.unlock(); // Releasing the lock
        }
    }
}
