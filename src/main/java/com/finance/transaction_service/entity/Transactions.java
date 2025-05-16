package com.finance.transaction_service.entity;

import com.finance.transaction_service.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id",nullable = false)
    private UUID user_id;

    @Column(name="type",nullable = false)
    private TransactionType type;

    @Column(name="amount",precision = 10,scale = 2, nullable = false)
    private Double amount;

    @Column(name="category",nullable = false)
    private String category;

    @Column(name = "date",nullable = false)
    private Date date;

    @Column(name = "description",length = 255)
    private String description;

    @Column(name = "attachment")
    private String attachment;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
