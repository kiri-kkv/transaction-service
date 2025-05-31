package com.finance.transaction_service.service;

import com.finance.transaction_service.constants.AppConstants;
import com.finance.transaction_service.dto.ApiResponse;
import com.finance.transaction_service.dto.FinanceOverviewDto;
import com.finance.transaction_service.dto.TransactionDto;
import com.finance.transaction_service.entity.Transactions;
import com.finance.transaction_service.entity.UserBalance;
import com.finance.transaction_service.repository.TransactionsRepository;
import com.finance.transaction_service.repository.UserBalanceRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class TransactionService {
    private final TransactionsRepository transactionsRepository;
    private final ModelMapper modelMapper;
    private final MessageSource messageSource;
    private final UserBalanceRepository userBalanceRepository;

    public TransactionService(TransactionsRepository transactionsRepository, ModelMapper modelMapper, MessageSource messageSource, UserBalanceRepository userBalanceRepository) {
        this.transactionsRepository = transactionsRepository;
        this.modelMapper = modelMapper;
        this.messageSource = messageSource;
        this.userBalanceRepository = userBalanceRepository;
    }

    public ResponseEntity<ApiResponse<Object>> addTransactions(TransactionDto transactionDto) {
        TypeMap<TransactionDto, Transactions> typeMap = modelMapper.getTypeMap(TransactionDto.class, Transactions.class);

        if (typeMap == null) {
            typeMap = modelMapper.createTypeMap(TransactionDto.class, Transactions.class);
            typeMap.addMappings(mapper -> mapper.skip(Transactions::setId));
        }
        Transactions transactions = modelMapper.map(transactionDto, Transactions.class);
        transactionsRepository.save(transactions);

        Date currentDate = new Date();

        Optional<UserBalance> userBalance = Optional.ofNullable(userBalanceRepository.findDateBySameMonthAndYear(currentDate, transactionDto.getUserId()));
        if(userBalance.isPresent()) {
            if (transactionDto.getType().name().equals(AppConstants.EXPENSE))
                userBalanceRepository.updateUserExpense(transactionDto.getUserId(), transactionDto.getAmount());
            else
                userBalanceRepository.updateUserIncome(transactionDto.getUserId(), transactionDto.getAmount());
        } else{
            UserBalance userMonthBalance = new UserBalance();
            userMonthBalance.setUserId(transactionDto.getUserId());
            userMonthBalance.setDate(currentDate);
            if (transactionDto.getType().name().equals(AppConstants.EXPENSE)) userMonthBalance.setExpense(transactionDto.getAmount());
            else userMonthBalance.setIncome(transactionDto.getAmount());
            userBalanceRepository.save(userMonthBalance);
        }
        ApiResponse<Object> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                messageSource.getMessage("transaction.add.success",null, Locale.ENGLISH),
                null
        );
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<ApiResponse<Page<Transactions>>> getTransactions(int page,int size,FinanceOverviewDto financeOverviewDto) throws ParseException {
        Pageable pageable = PageRequest.of(page, size);
        UUID userId = financeOverviewDto.getUserId();

        // Formatting dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse(financeOverviewDto.getFromDate());
        Date toDate = sdf.parse(financeOverviewDto.getToDate());
        System.out.println(userId+" " + fromDate +" " +toDate);

        Page<Transactions> transactions = transactionsRepository.fetchTransactionsBetweenDates(fromDate,toDate,userId,pageable);

        ApiResponse<Page<Transactions>> response = new ApiResponse<>(
                HttpStatus.OK,
                messageSource.getMessage("transaction.fetch.success",null, Locale.getDefault()),
                transactions
        );
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<Object>> getFinanceOverview(FinanceOverviewDto financeOverviewDto) throws ParseException {
        UUID userId = financeOverviewDto.getUserId();

        // Formatting user selected date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate = sdf.parse(financeOverviewDto.getFromDate());

        // Get date with previous month and year
        Calendar cal = Calendar.getInstance();
        cal.setTime(inputDate);
        cal.add(Calendar.MONTH,-1);
        Date previousMonthDate = cal.getTime();

        UserBalance userCurrentBalance = userBalanceRepository.findDateBySameMonthAndYear(inputDate,userId);
        UserBalance userPreviousBalance = userBalanceRepository.findDateBySameMonthAndYear(previousMonthDate,userId);

        Map<String, BigDecimal> financeOverview = new HashMap<>();
        financeOverview.put(AppConstants.BALANCE.toLowerCase(),userCurrentBalance.getBalance());
        financeOverview.put(AppConstants.EXPENSE.toLowerCase(),userCurrentBalance.getExpense());
        financeOverview.put(AppConstants.INCOME.toLowerCase(),userCurrentBalance.getIncome());
        financeOverview.put(AppConstants.PREVIOUS_BALANCE.toLowerCase(),userPreviousBalance.getBalance());
        financeOverview.put(AppConstants.PREVIOUS_EXPENSE.toLowerCase(),userPreviousBalance.getExpense());
        financeOverview.put(AppConstants.PREVIOUS_INCOME.toLowerCase(),userPreviousBalance.getIncome());

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK,
                messageSource.getMessage("finance.fetch.success",null, Locale.getDefault()),
                financeOverview
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ApiResponse<Object>> getTransactionsByCategory(FinanceOverviewDto financeOverviewDto) throws ParseException {
        UUID userId = financeOverviewDto.getUserId();

        // Formatting dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = sdf.parse(financeOverviewDto.getFromDate());
        Date toDate = sdf.parse(financeOverviewDto.getToDate());

        Pageable wholePage = Pageable.unpaged();
        Page<Transactions> transactions = transactionsRepository.fetchTransactionsBetweenDates(fromDate,toDate,userId,wholePage);
        Map<String,List<Map<String, Object>>> categoryTransactions= new HashMap<>();
        Map<String,BigDecimal> categoryAmount= new HashMap<>();
        BigDecimal totalAmountSpent = BigDecimal.valueOf(0.0);

        for(Transactions x:transactions){
            String category = x.getCategory();
            BigDecimal amount = x.getAmount();

            // Create a map to represent a transaction object
            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("category", category);
            transactionDetails.put("amount", amount);
            transactionDetails.put("type", x.getType());
            transactionDetails.put("attachment", x.getAttachment());
            transactionDetails.put("date", x.getDate());
            transactionDetails.put("description", x.getDescription());

            categoryTransactions
                    .computeIfAbsent(category, k -> new ArrayList<>())
                    .add(transactionDetails);

            if(x.getType().name().equals(AppConstants.EXPENSE)) {
                totalAmountSpent = totalAmountSpent.add(amount);

                categoryAmount.merge(category, amount, BigDecimal::add);
            }
        }

        Map<String, BigDecimal> categoryPercentage = new HashMap<>();

        for (Map.Entry<String, BigDecimal> entry : categoryAmount.entrySet()) {
            String category = entry.getKey();
            BigDecimal amount = entry.getValue();
            System.out.println("amount==== "+totalAmountSpent +" " + amount);

            BigDecimal percentage = amount
                    .divide(totalAmountSpent, 2, RoundingMode.HALF_UP)  // 2 decimal places
                    .multiply(BigDecimal.valueOf(100));

            categoryPercentage.put(category, percentage);
        }

        Map<String, Object> categoryTransactionsOverview = new HashMap<>();
        categoryTransactionsOverview.put("categoryTransactions",categoryTransactions);
        categoryTransactionsOverview.put("categoryAmount",categoryAmount);
        categoryTransactionsOverview.put("categoryPercentage",categoryPercentage);

        ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK,
                messageSource.getMessage("category.transactions.fetch.success",null, Locale.getDefault()),
                categoryTransactionsOverview
        );

        return ResponseEntity.ok(response);
    }
}