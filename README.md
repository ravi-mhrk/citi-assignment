## Project Overview

This project implements a simulated ATM cash withdrawal function in Java, focusing on core requirements like optimized banknote dispensing, denomination management, and concurrency. It's designed as a standalone module to demonstrate solid Object-Oriented Programming (OOP) principles, modularity, and the application of design patterns.

## Key Features
- Cash Withdrawal: Users can specify an amount to withdraw.
- Optimal Banknote Dispensing: The system prioritizes dispensing the largest available denominations first, minimizing the total number of banknotes given to the user.
- Flexible Denomination Handling: The ATM supports any banknote denomination as long as it's a multiple of 10.
- Real-time Denomination Availability: The system maintains and updates the availability of various denominations in the ATM, preventing withdrawals that exceed the machine's capacity.
- Parallel Withdrawals Support: Concurrency mechanisms ensure that multiple users can withdraw money simultaneously without data inconsistencies or race conditions.
- Robust Exception Handling: Catches and manages scenarios like insufficient funds in the machine (InsufficientFundsException) or invalid withdrawal amounts (InvalidAmountException).

## Technologies Used
- Java Development Kit (JDK) 17+
- Gradle (for build automation and dependency management)
- JUnit 5 (for unit testing)

## Getting Started
Follow these steps to set up the project locally and run the tests.
Prerequisites
- Java Development Kit (JDK) 17 or newer: Ensure Java is installed and configured on your system. You can download it from Oracle's website or use an OpenJDK distribution like Adoptium Temurin.
- Gradle: You don't necessarily need Gradle installed globally, as the project includes the Gradle Wrapper (./gradlew).

Installation and Setup
Clone the Repository: https://github.com/ravi-mhrk/citi-assignment.git
Open in IntelliJ IDEA (or preferred IDE)
 - Open IntelliJ IDEA.
 - Select File > Open and navigate to the cloned project directory.
 - IntelliJ IDEA should automatically detect the build.gradle file and prompt you to import the project as a Gradle project. Allow it to do so, which will set up dependencies and project structure. 
 - Navigate to the ATMTest.java file in the src/test/java/com/example/assignment package. 
 - Right-click on the ATMTest class name (or individual test methods) and select Run 'ATMTest'. 
 - The test results will appear in IntelliJ IDEA's "Run" or "Test Results" window.
 Using Gradle (Command Line)
   - Open a terminal or command prompt.
   - Navigate to the root directory of your project (where build.gradle is located).
   - Execute the Gradle test task: ./gradlew test or gradlew.bat test
 
