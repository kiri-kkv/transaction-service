package com.finance.transaction_service.service;

import com.finance.transaction_service.dto.ApiResponse;
import com.finance.transaction_service.dto.UserCategoryDto;
import com.finance.transaction_service.dto.UserIdDto;
import com.finance.transaction_service.entity.UserTransactionCategory;
import com.finance.transaction_service.repository.UserTransactionCategoryRepository;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.finance.transaction_service.constants.AppConstants.SYSTEM_CATEGORIES;

@Service
public class TransactionCategoryService {
    private final MessageSource messageSource;
    private final UserTransactionCategoryRepository userTransactionCategoryRepository;

    public TransactionCategoryService(MessageSource messageSource, UserTransactionCategoryRepository userTransactionCategoryRepository) {
        this.messageSource = messageSource;
        this.userTransactionCategoryRepository = userTransactionCategoryRepository;
    }

    public ResponseEntity<ApiResponse<Object>> getSystemCategories(UserIdDto userIdDto) {
        List<String> categories = new ArrayList<>(SYSTEM_CATEGORIES);
        UserTransactionCategory userTransactionCategory = userTransactionCategoryRepository.getByUserId(userIdDto.getUserId());
        System.out.println("userTransactionCategory== "+userTransactionCategory);
        if(userTransactionCategory!=null){
            System.out.println("userTransactionCategory== "+categories);
            System.out.println("userTransactionCategory== "+ Arrays.toString(userTransactionCategory.getCategory()));
            categories.addAll(Arrays.asList(userTransactionCategory.getCategory()));
            System.out.println("userTransactionCategory== "+categories);
        }
        ApiResponse<Object> apiResponse = new ApiResponse<>(
                HttpStatus.OK,
                messageSource.getMessage("category.fetch.successfully",null, Locale.ENGLISH),
                categories
        );
        return ResponseEntity.ok(apiResponse);
    }

    public ResponseEntity<ApiResponse<Object>> addUserCategory(UserCategoryDto userCategoryDto) {
        UUID userId = userCategoryDto.getUserId();
        String category = userCategoryDto.getCategory();

        UserTransactionCategory userCategories = userTransactionCategoryRepository.getByUserId(userId);

        if(userCategories == null) {
            userCategories = new UserTransactionCategory();
            userCategories.setUserId(userId);
            userCategories.setCategory(new String[]{category});
        }else {
            String [] existingCategory = userCategories.getCategory();
            List<String> userCategory = existingCategory !=null ? new ArrayList<>(Arrays.asList(existingCategory)) : new ArrayList<>();
            if(userCategory.contains(category)){
                return ResponseEntity.ok(new ApiResponse<>(
                        HttpStatus.OK,
                        messageSource.getMessage("category.exists", null, Locale.ENGLISH),
                        null
                ));
            }

            userCategory.add(category);
            userCategories.setCategory(userCategory.toArray(new String[0]));
        }
        userTransactionCategoryRepository.save(userCategories);

        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK,
                messageSource.getMessage("category.added.successfully", null, Locale.ENGLISH),
                null
        ));
    }
}
