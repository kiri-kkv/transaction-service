package com.finance.transaction_service.controller;

import com.finance.transaction_service.dto.ApiResponse;
import com.finance.transaction_service.dto.UserCategoryDto;
import com.finance.transaction_service.dto.UserIdDto;
import com.finance.transaction_service.service.TransactionCategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/category")
public class UserCategoryController {
    private final TransactionCategoryService transactionCategoryService;

    public UserCategoryController(TransactionCategoryService transactionCategoryService) {
        this.transactionCategoryService = transactionCategoryService;
    }

    @GetMapping("/get-category")
    ResponseEntity<ApiResponse<Object>> getSystemCategories(@Valid @RequestBody UserIdDto userIdDto){
        return transactionCategoryService.getSystemCategories(userIdDto);
    }

    @PostMapping("/add-category")
    ResponseEntity<ApiResponse<Object>> addUserCategory(@Valid @RequestBody UserCategoryDto userCategoryDto){
        return transactionCategoryService.addUserCategory(userCategoryDto);
    }
}
