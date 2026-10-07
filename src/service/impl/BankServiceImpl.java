package service.impl;

import domain.Account;
import domain.Transaction;
import domain.Type;
import domain.Customer;
import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.ValidationException;
import repository.AccountRepository;

import repository.CustomerRepository;
import repository.TransactionRepository;
import service.BankService;
import util.Validation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class BankServiceImpl implements BankService {

    private final AccountRepository accountRepository = new AccountRepository();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();

    private final Validation<String> validateName = name ->{
        if(name == null || name.isBlank()) throw new ValidationException("Name is required");
    };
    private final Validation<String> validateEmail = email -> {
        if(email == null || email.isBlank()) {
            throw new ValidationException("Email is required");
        }
        if (!email.matches(
                "^[Z-aa-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new ValidationException(
                    "Please enter a valid email"
            );
        }
    };
    private final Validation<String> validateType = type -> {
        if(type == null || type.isBlank() ){
            throw new ValidationException("Account type is required");
        }
        if (!(type.equalsIgnoreCase("SAVINGS")
                || type.equalsIgnoreCase("CURRENT"))) {

            throw new ValidationException("Type must be SAVINGS or CURRENT");
        }
    };
    private final Validation<Double> validateAmountPositive = amount -> {
        if(amount == null || amount <= 0 )
            throw new ValidationException("Please enter valid amount");
    };
//
@Override
public String openAccount(String name, String email, String accountType) {
 validateName.validate(name);
    validateEmail.validate(email);
    validateType.validate(accountType);
    String customerId = UUID.randomUUID().toString();

    Customer customer = new Customer(email, customerId, name);

    customerRepository.save(customer);

    String accountNumber = getAccountNumber();

    Account account = new Account(
            accountNumber,
            accountType,
            0.0,
            customerId
    );

    accountRepository.save(account);

    return accountNumber;
}


    @Override
    public List<Account> listAccounts() {
        return accountRepository.findAll().stream()
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());
    }

    @Override
    public void deposit(String accountNumber, Double amount, String note) {
     validateAmountPositive.validate(amount);
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(()-> new AccountNotFoundException("Account not found" + accountNumber));
        account.setBalance(account.getBalance() + amount);
        Transaction  transaction = new Transaction(account.getAccountNumber(),
                amount, UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.DEPOSIT);

        transactionRepository.add(transaction);

    }

    @Override
    public void withdraw(String accountNumber, Double amount, String note) {
        Account account = accountRepository.findByNumber(accountNumber)
                .orElseThrow(()-> new AccountNotFoundException("Account not found" + accountNumber));
        if(account.getBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException("Insufficient Balance");
        account.setBalance(account.getBalance() - amount);
        Transaction  transaction = new Transaction(account.getAccountNumber(),
                amount, UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.WITHDRAW);

        transactionRepository.add(transaction);
    }

    @Override
    public void transfer(String fromAcc, String toAcc, Double amount, String note) {
        if(fromAcc.equals(toAcc))
            throw new ValidationException("Cannot transfer to your own account");
        Account from = accountRepository.findByNumber(fromAcc)
                .orElseThrow(()-> new AccountNotFoundException("Account not found" + fromAcc));
        Account to = accountRepository.findByNumber(toAcc)
                .orElseThrow(()-> new AccountNotFoundException("Account not found" + toAcc));
        if(from.getBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException("Insufficient Balance");

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        /*Transaction  fromtransaction = new Transaction(from.getAccountNumber(),
                amount, UUID.randomUUID().toString(),note, LocalDateTime.now(), Type.WITHDRAW);

        transactionRepository.add(fromtransaction);*/
        // use inline veriable.

        transactionRepository.add(new Transaction(from.getAccountNumber(),
                amount, UUID.randomUUID().toString(),note,
                LocalDateTime.now(), Type.TRANSFER_OUT));

        transactionRepository.add(new Transaction(to.getAccountNumber(),
                amount, UUID.randomUUID().toString(),note,
                LocalDateTime.now(), Type.TRANSFER_IN));


    }

    @Override
    public List<Transaction> getStatement(String account) {
        return transactionRepository.findByAccount(account).stream()
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());
    }
    @Override
    public List<Account> searchAccountsByCustomerName(String q){
        String query = (q == null) ? "": q.toLowerCase();
    /*    List<Account> result = new ArrayList<>();
        for(Customer c : customerRepository.findAll());{
            if(c.getName().toLowerCase().contains(query))
               result.addAll(accountRepository.findByCustomerId(c.getId()));
        }
        result.sort(Comparator.comparing(Account::getAccountNumber));
        return result;*/
        //create with the helf of stream
//        return customerRepository.findAll().stream()
//                .filter(c -> c.getName().toLowerCase().contains(query))
//                .flatMap(c -> accountRepository.findByCustomerId((c.getId()).stream())
//                        .sorted(Comparator.comparing(Account::getAccountNumber))
//                        .collect(Collectors.toList()));
//

        return customerRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(query))
                .flatMap(c -> accountRepository.findByCustomerId(c.getId()).stream())
                .sorted(Comparator.comparing(Account::getAccountNumber))
                .collect(Collectors.toList());

    }

    private String getAccountNumber() {
        int size = accountRepository.findAll().size() +1;
        String accountNumber = String.format("AC%06d",size);
        return accountNumber;
    }
}
