
# Banking Management System

This is a simple Banking Management System built using Java.

The project allows users to create bank accounts, deposit and withdraw money, transfer money between accounts, and view transaction statements.

The project also uses validation to make sure the user enters correct information.

## Features

- Open a new bank account
- Create a customer while opening an account
- Support for Savings and Current accounts
- Deposit money
- Withdraw money
- Transfer money from one account to another
- Check account balance
- View transaction history
- Search accounts using customer name
- Validate customer name
- Validate email
- Validate account type
- Validate transaction amount
- Handle account-not-found errors
- Handle insufficient balance errors

## Project Structure

The project is divided into different packages so that each part has its own responsibility.

```
src
├── domain
│   ├── Account.java
│   ├── Customer.java
│   ├── Transaction.java
│   └── Type.java
│
├── exceptions
│   ├── AccountNotFoundException.java
│   ├── InsufficientFundsException.java
│   └── ValidationException.java
│
├── repository
│   ├── AccountRepository.java
│   ├── CustomerRepository.java
│   └── TransactionRepository.java
│
├── service
│   ├── BankService.java
│   └── impl
│       └── BankServiceImpl.java
│
└── util
    └── Validation.java
```

## Main Classes

### Account

The `Account` class stores account-related information.

It contains details such as:

- Account number
- Account type
- Balance
- Customer ID

Account numbers are generated automatically in this format:

```
AC000001
AC000002
AC000003
```

### Customer

The `Customer` class stores customer information.

It contains:

- Customer ID
- Customer name
- Email

### Transaction

The `Transaction` class stores information about money transactions.

A transaction contains:

- Account number
- Amount
- Transaction ID
- Note
- Transaction time
- Transaction type

The transaction types include:

```
DEPOSIT
WITHDRAW
TRANSFER_IN
TRANSFER_OUT
```

## Bank Service

`BankService` contains the main operations of the banking system.

`BankServiceImpl` provides the actual implementation.

Some important methods are:

```
openAccount()
listAccounts()
deposit()
withdraw()
transfer()
getStatement()
searchAccountsByCustomerName()
```

## Opening an Account

To open an account, the user needs to provide:

- Name
- Email
- Account type

The account type can be:

```
SAVINGS
CURRENT
```

Example:

```
openAccount(
    "Rahul",
    "rahul@gmail.com",
    "SAVINGS"
);
```

After creating the account, the system generates an account number automatically.

## Validation

The project uses a simple `Validation<T>` functional interface for validation.

For example, name validation checks that the name is not empty.

```
private final Validation<String> validateName = name -> {
    if (name == null || name.isBlank()) {
        throw new ValidationException("Name is required");
    }
};
```

### Email Validation

The email should follow a basic format such as:

```
name@gmail.com
john.doe@yahoo.com
user123@company.co.in
```

The project checks the email using a regular expression.

```
^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
```

### Account Type Validation

Only these two account types are accepted:

```
SAVINGS
CURRENT
```

If the user enters another type, a `ValidationException` is thrown.

### Amount Validation

Deposit, withdrawal, and transfer amounts must be greater than zero.

For example:

```
1000       valid
500.50     valid
0          invalid
-100       invalid
```

## Deposit

The `deposit()` method adds money to an account.

Example:

```
deposit("AC000001", 1000.0, "Initial deposit");
```

The account balance is increased and a `DEPOSIT` transaction is created.

## Withdraw

The `withdraw()` method removes money from an account.

Before withdrawing, the system checks whether the account has enough balance.

If the balance is not enough, it throws:

```
InsufficientFundsException
```

Example:

```
withdraw("AC000001", 500.0, "Cash withdrawal");
```

## Transfer

The `transfer()` method moves money from one account to another.

Example:

```
transfer(
    "AC000001",
    "AC000002",
    1000.0,
    "Money transfer"
);
```

The system creates two transactions:

```
TRANSFER_OUT
TRANSFER_IN
```

The source account balance is decreased and the destination account balance is increased.

The system also prevents transferring money to the same account.

## Transaction Statement

The `getStatement()` method returns all transactions for an account.

Example:

```
getStatement("AC000001");
```

Transactions are sorted using their timestamp.

## Search Accounts

Accounts can be searched using the customer's name.

For example:

```
searchAccountsByCustomerName("rahul");
```

The search is case-insensitive.

The method also uses Java Streams to find the matching customers and their accounts.

## Repositories

The repository classes are responsible for storing and retrieving data.

### AccountRepository

Used for account-related operations.

For example:

```
save account
find account by number
find all accounts
find accounts by customer ID
```

### CustomerRepository

Used for customer-related operations.

For example:

```
save customer
find customers
```

### TransactionRepository

Used for transaction-related operations.

For example:

```
add transaction
find transactions by account
```

## Exception Handling

The project has custom exceptions for different situations.

### AccountNotFoundException

Used when an account cannot be found.

Example:

```
Account not found AC000001
```

### InsufficientFundsException

Used when an account does not have enough money for a withdrawal or transfer.

Example:

```
Insufficient Balance
```

### ValidationException

Used when the user provides invalid information.

For example:

```
Name is required
Email is required
Please enter a valid email
Account type is required
Type must be SAVINGS or CURRENT
Please enter valid amount
```

## Java Concepts Used

This project is mainly created to practice Java concepts.

Some of the important concepts used are:

- Classes and objects
- Interfaces
- Functional interfaces
- Lambda expressions
- Generics
- Java Streams
- `Optional`
- Exception handling
- `UUID`
- `LocalDateTime`
- Collections
- `Comparator`
- `Collectors`
- Method overriding

## Streams Used in the Project

For example, accounts are sorted using:

```
return accountRepository.findAll().stream()
        .sorted(Comparator.comparing(Account::getAccountNumber))
        .collect(Collectors.toList());
```

Customer search also uses streams:

```
return customerRepository.findAll().stream()
        .filter(c -> c.getName().toLowerCase().contains(query))
        .flatMap(c -> accountRepository.findByCustomerId(c.getId()).stream())
        .sorted(Comparator.comparing(Account::getAccountNumber))
        .collect(Collectors.toList());
```

This makes the code shorter and easier to read compared to writing the same logic using multiple loops.

## How the Application Works

The basic flow is:

```
User
  |
  v
BankService
  |
  v
BankServiceImpl
  |
  +---- AccountRepository
  |
  +---- CustomerRepository
  |
  +---- TransactionRepository
```

For example, when opening an account:

```
Enter customer details
        |
        v
Validate details
        |
        v
Create Customer
        |
        v
Create Account
        |
        v
Save Customer and Account
        |
        v
Return Account Number
```

## Example

A simple flow can look like this:

```
1. Open account
   Name: Rahul
   Email: rahul@gmail.com
   Type: SAVINGS

2. Account created
   Account Number: AC000001

3. Deposit
   Amount: 5000

4. Withdraw
   Amount: 1000

5. Transfer
   From: AC000001
   To: AC000002
   Amount: 500

6. Check statement
```

## Important Notes

This is a learning project. The repositories currently work as the application's data storage layer, so this is not a production banking application.

A real banking system would need additional features such as:

- Database storage
- User authentication
- Authorization
- Password/security handling
- Proper transaction management
- Concurrency handling
- Audit logging
- Stronger email validation
- API layer
- Proper database transactions

## Future Improvements

Some things that can be added later:

- Add a database such as MySQL or PostgreSQL
- Create REST APIs using Spring Boot
- Add login and authentication
- Add unit tests
- Add transaction rollback
- Add account closing functionality
- Add transaction search
- Add pagination
- Add better validation
- Add a web or mobile frontend

## Conclusion

This project is a simple way to understand how a banking service can be designed using Java.

It focuses mainly on Java OOP, interfaces, functional interfaces, lambda expressions, streams, repositories, validation, and exception handling. :::
