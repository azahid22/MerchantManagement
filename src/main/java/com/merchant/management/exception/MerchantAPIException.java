package com.merchant.management.exception;

public class MerchantAPIException extends Exception {
    /**
     * Constructs this class.
     *
     * @param message
     * @param cause
     */
    public MerchantAPIException(final String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Constructs this class.
     *
     * @param message
     */
    public MerchantAPIException(final String message) {
        super(message);
    }
}
