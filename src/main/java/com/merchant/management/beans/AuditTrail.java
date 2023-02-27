package com.merchant.management.beans;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Document
public class AuditTrail {
    @Id
    private String id;
    private String entity;
    private String field;
    private String oldValue;
    private String newValue;
    private Date updatedAt;
}
