package com.merchant.management.service;

import com.merchant.management.beans.Merchant;
import com.merchant.management.dao.MerchantRepository;
import com.merchant.management.exception.MerchantAPIException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class MerchantService {
    private static final Logger LOG = LoggerFactory.getLogger(MerchantService.class);
    private final MerchantRepository repository;
    private final BackgroundJobService backgroundJobService;
    private final AuditTrailService auditTrailService;

    /**
     * This will create merchant with the provided details of Merchant in DB.
     *
     * @param merchant
     * @return It will return false if merchant exists with the same Identification Number. Will return true if merchant created successfully.
     */
    @Transactional
    public Merchant createMerchant(Merchant merchant) throws MerchantAPIException {
        try {
            Merchant merchantFromDB = findMerchantByIdentificationNumber(merchant.getIdentificationNumber());
            if (merchantFromDB != null) {
                LOG.warn("Unable to create merchant as a merchant exists with Same Identification Number");
                return null;
            }
            generateOtp(merchant);
            backgroundJobService.sendOtpEmail(merchant);
            if (merchant.getWebhookUrl() != null) {
                backgroundJobService.sendWebhookNotification(merchant);
            }
            return repository.insert(merchant);

        } catch (Exception ex) {
            LOG.error("Exception Occurred while creating merchant", ex);
            throw new MerchantAPIException("Exception Occurred while Creating merchant.", ex);
        }
    }

    /**
     * This will update the merchant with the provided details for the merchant.
     *
     * @param merchant
     * @return It will return false if merchant not exist with the identification number. Will return true if successfully updated.
     */

    @Transactional
    public Merchant updateMerchant(Merchant merchant) throws MerchantAPIException {
        try {
            Merchant merchantFromDB = findMerchantByIdentificationNumber(merchant.getIdentificationNumber());
            if (merchantFromDB == null) {
                LOG.warn("Unable to update merchant as no merchant exists with Identification Number");
                return null;
            }
            manageAuditTrail(merchant, merchantFromDB);
            Merchant createdMerchant = repository.save(merchant);
            return createdMerchant;
        } catch (Exception ex) {
            LOG.error("Exception Occurred while updating merchant", ex);
            throw new MerchantAPIException("Exception Occurred while Updating merchant.", ex);
        }
    }

    /**
     * @param merchantIdentificationNumber
     * @return This will return the merchant from DB with Identification Number.
     */
    public Merchant findMerchantByIdentificationNumber(String merchantIdentificationNumber) {
        Optional<Merchant> merchantOptional = repository.findById(merchantIdentificationNumber);
        if (merchantOptional.isPresent()) {
            return merchantOptional.get();
        }
        return null;
    }

    /**
     * This will return merchant info based on the page info passed.
     *
     * @param pageable
     * @return
     */
    public Page<Merchant> getAllMerchants(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void generateOtp(Merchant merchant) {
        int otp = new Random().nextInt(900000) + 100000;
        merchant.setOtp(String.valueOf(otp));
    }

    public Merchant verifyOTPAndUpdateStatus(Merchant requestMerchant) {
        Merchant merchantFromDB = findMerchantByIdentificationNumber(requestMerchant.getIdentificationNumber());
        if (merchantFromDB == null) {
            return null;
        }
        if (merchantFromDB.getOtp().equals(requestMerchant.getOtp())) {
            merchantFromDB.setStatus("Verified");
        } else {
            merchantFromDB.setStatus("Not Verified");
        }
        repository.save(merchantFromDB);
        return merchantFromDB;
    }

    private void manageAuditTrail(Merchant requestMerchant, Merchant merchantFromDB) {
        requestMerchant.setOtp(merchantFromDB.getOtp());
        requestMerchant.setStatus(merchantFromDB.getStatus());
        requestMerchant.setWebhookUrl(merchantFromDB.getWebhookUrl());
        auditTrailService.evaluateUpdates(merchantFromDB, requestMerchant);
    }
}
