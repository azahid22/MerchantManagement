package com.merchant.management.service;

import com.merchant.management.beans.AuditTrail;
import com.merchant.management.beans.Merchant;
import com.merchant.management.dao.AuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuditTrailService {
    private final AuditTrailRepository auditTrailRepository;

    public void evaluateUpdates(Merchant merchantFromDB, Merchant requestMerchant) {
        if (merchantFromDB == null || requestMerchant == null) {
            return;
        }
        if (StringUtils.compare(merchantFromDB.getName(), requestMerchant.getName()) != 0) {
            auditTrailRepository.save(createAuditTrailEntry(Merchant.class.getName(), "Name", merchantFromDB.getName(), requestMerchant.getName()));
        }
        if (StringUtils.compare(merchantFromDB.getEmailAddress(), requestMerchant.getEmailAddress()) != 0) {
            auditTrailRepository.save(createAuditTrailEntry(Merchant.class.getName(), "EmailAddress", merchantFromDB.getEmailAddress(), requestMerchant.getEmailAddress()));
        }
        if (StringUtils.compare(merchantFromDB.getIdentificationStatus(), requestMerchant.getIdentificationStatus()) != 0) {
            auditTrailRepository.save(createAuditTrailEntry(Merchant.class.getName(), "IdentificationStatus", merchantFromDB.getIdentificationStatus(), requestMerchant.getIdentificationStatus()));
        }
        if (StringUtils.compare(merchantFromDB.getLicenseNumber(), requestMerchant.getLicenseNumber()) != 0) {
            auditTrailRepository.save(createAuditTrailEntry(Merchant.class.getName(), "LicenseNumber", merchantFromDB.getLicenseNumber(), requestMerchant.getLicenseNumber()));
        }
        if (StringUtils.compare(merchantFromDB.getLicenseStatus(), requestMerchant.getLicenseStatus()) != 0) {
            auditTrailRepository.save(createAuditTrailEntry(Merchant.class.getName(), "LicenseStatus", merchantFromDB.getLicenseStatus(), requestMerchant.getLicenseStatus()));
        }
        if (StringUtils.compare(merchantFromDB.getStatus(), requestMerchant.getStatus()) != 0) {
            auditTrailRepository.save(createAuditTrailEntry(Merchant.class.getName(), "Status", merchantFromDB.getStatus(), requestMerchant.getStatus()));
        }
    }

    private AuditTrail createAuditTrailEntry(String entityName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail = new AuditTrail();
        auditTrail.setId(UUID.randomUUID().toString());
        auditTrail.setEntity(entityName);
        auditTrail.setField(fieldName);
        auditTrail.setOldValue(oldValue);
        auditTrail.setNewValue(newValue);
        auditTrail.setUpdatedAt(new Date());
        return auditTrail;
    }
}
