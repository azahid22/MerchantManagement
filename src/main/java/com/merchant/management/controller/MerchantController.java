package com.merchant.management.controller;


import com.merchant.management.beans.Merchant;
import com.merchant.management.exception.MerchantAPIException;
import com.merchant.management.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Validator;

@RequestMapping("/merchant")
@RequiredArgsConstructor
@RestController
@Validated
public class MerchantController {
    private static final Logger LOG = LoggerFactory.getLogger(MerchantController.class);

    public final MerchantService merchantService;
    private final Validator validator;

    @PostMapping("/create")
    public ResponseEntity<?> createMerchant(@RequestBody Merchant merchant) {
        try {
            if (validator.validate(merchant).isEmpty()) {
                Merchant createdMerchant = merchantService.createMerchant(merchant);
                if (createdMerchant != null) {
                    return ResponseEntity.ok(createdMerchant);

                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Merchant Already Exists with Same ID");
                }
            } else {
                StringBuilder errorMsg = new StringBuilder();
                validator.validate(merchant).forEach(error -> errorMsg.append(error.getMessage()).append("\n"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg.toString());
            }
        } catch (MerchantAPIException ex) {
            LOG.error("Exception Occurred while creating merchant", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal Server Error");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateMerchant(@RequestBody Merchant merchant) {
        try {
            if (validator.validate(merchant).isEmpty()) {
                Merchant createdMerchant = merchantService.updateMerchant(merchant);
                if (createdMerchant != null) {
                    return ResponseEntity.ok(createdMerchant);

                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Merchant Not Exists with ID");
                }
            } else {
                StringBuilder errorMsg = new StringBuilder();
                validator.validate(merchant).forEach(error -> errorMsg.append(error.getMessage()).append("\n"));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg.toString());
            }
        } catch (MerchantAPIException ex) {
            LOG.error("Exception Occurred while updating merchant", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Internal Server Error");
        }
    }

    @GetMapping("/find")

    public ResponseEntity<Object> findMerchant(@RequestParam String identificationNumber) {
        Merchant merchant = merchantService.findMerchantByIdentificationNumber(identificationNumber);
        if (merchant != null) {
            return ResponseEntity.ok(merchant);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Merchant not found");
    }

    @PostMapping("/verifyOTP")
    public ResponseEntity<Object> verifyOTP(@RequestBody Merchant merchant) {
        Merchant merchantFromDb = merchantService.verifyOTPAndUpdateStatus(merchant);
        if (merchantFromDb == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Merchant not found");
        }
        return ResponseEntity.ok(merchantFromDb.getStatus());
    }

    @GetMapping
    public Page<Merchant> getMerchants(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return merchantService.getAllMerchants(PageRequest.of(page, size));
    }
}
