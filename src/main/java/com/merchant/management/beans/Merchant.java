package com.merchant.management.beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;


@Getter
@Setter
@AllArgsConstructor
@Document
public class Merchant {
    @Id
    private String identificationNumber;
    @NotEmpty(message = "LicenseNumber cannot be null/empty")
    private String licenseNumber;
    private String name;
    @NotEmpty(message = "EmailAddress cannot be null/empty")
    private String emailAddress;
    private String otp;
    private String identificationStatus;
    private String licenseStatus;
    private String status = "Pending";
    private String webhookUrl;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("identificationNumber", identificationNumber)
                .toString();
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }
}
