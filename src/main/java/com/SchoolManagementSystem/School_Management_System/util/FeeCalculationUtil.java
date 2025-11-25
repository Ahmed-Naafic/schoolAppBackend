package com.SchoolManagementSystem.School_Management_System.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for fee-related calculations and categorizations
 */
public class FeeCalculationUtil {

    private static final String FREE_CATEGORY = "FREE";
    private static final String DISCOUNT_CATEGORY = "DISCOUNT";
    private static final String FULL_PAYMENT_CATEGORY = "FULL_PAYMENT";

    /**
     * Calculate discount amount based on type and value
     * @param totalAmount Original total amount
     * @param discountType "percentage" or "fixed"
     * @param discountValue Discount value (percentage or fixed amount)
     * @return Calculated discount amount
     */
    public static Double calculateDiscountAmount(Double totalAmount, String discountType, Double discountValue) {
        if (totalAmount == null || totalAmount <= 0) {
            return 0.0;
        }
        
        if (discountValue == null || discountValue <= 0) {
            return 0.0;
        }

        if ("percentage".equalsIgnoreCase(discountType)) {
            // Validate percentage is between 0-100
            double percentage = Math.min(100.0, Math.max(0.0, discountValue));
            return (totalAmount * percentage) / 100.0;
        } else if ("fixed".equalsIgnoreCase(discountType)) {
            // Fixed amount cannot exceed total amount
            return Math.min(discountValue, totalAmount);
        }
        
        return 0.0;
    }

    /**
     * Calculate amount due (totalAmount - discountAmount)
     * @param totalAmount Original total amount
     * @param discountAmount Calculated discount amount
     * @return Amount due after discount
     */
    public static Double calculateAmountDue(Double totalAmount, Double discountAmount) {
        if (totalAmount == null) {
            return 0.0;
        }
        if (discountAmount == null) {
            discountAmount = 0.0;
        }
        return Math.max(0.0, totalAmount - discountAmount);
    }

    /**
     * Calculate balance (amountDue - amountPaid)
     * @param amountDue Amount due after discount
     * @param amountPaid Amount already paid
     * @return Balance remaining (cannot be negative)
     */
    public static Double calculateBalance(Double amountDue, Double amountPaid) {
        if (amountDue == null) {
            amountDue = 0.0;
        }
        if (amountPaid == null) {
            amountPaid = 0.0;
        }
        // Balance cannot be negative
        return Math.max(0.0, amountDue - amountPaid);
    }

    /**
     * Determine fee category based on discount percentage
     * @param totalAmount Original total amount
     * @param discountAmount Calculated discount amount
     * @return Category: "FREE", "DISCOUNT", or "FULL_PAYMENT"
     */
    public static String determineCategory(Double totalAmount, Double discountAmount) {
        if (totalAmount == null || totalAmount <= 0) {
            return FREE_CATEGORY;
        }
        
        if (discountAmount == null || discountAmount <= 0) {
            return FULL_PAYMENT_CATEGORY;
        }

        // Calculate discount percentage
        double discountPercentage = (discountAmount / totalAmount) * 100.0;

        if (discountPercentage >= 100.0) {
            return FREE_CATEGORY;
        } else if (discountPercentage > 0.0 && discountPercentage < 100.0) {
            return DISCOUNT_CATEGORY;
        } else {
            return FULL_PAYMENT_CATEGORY;
        }
    }

    /**
     * Generate invoice number in format: INV-YYYYMMDD-XXXX
     * @param sequenceNumber Sequential number for the day
     * @return Generated invoice number
     */
    public static String generateInvoiceNumber(Long sequenceNumber) {
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%04d", sequenceNumber != null ? sequenceNumber : 1);
        return "INV-" + datePrefix + "-" + sequence;
    }

    /**
     * Validate discount percentage (0-100)
     * @param discountValue Discount value
     * @param discountType Discount type
     * @return true if valid, false otherwise
     */
    public static boolean validateDiscount(Double discountValue, String discountType) {
        if (discountValue == null || discountValue < 0) {
            return false;
        }
        
        if ("percentage".equalsIgnoreCase(discountType)) {
            return discountValue <= 100.0;
        }
        
        return true; // Fixed amount validation is done in calculateDiscountAmount
    }

    /**
     * Get discount percentage from discount amount and total amount
     * @param totalAmount Original total amount
     * @param discountAmount Discount amount
     * @return Discount percentage (0-100)
     */
    public static Double getDiscountPercentage(Double totalAmount, Double discountAmount) {
        if (totalAmount == null || totalAmount <= 0) {
            return 0.0;
        }
        if (discountAmount == null || discountAmount <= 0) {
            return 0.0;
        }
        return Math.min(100.0, (discountAmount / totalAmount) * 100.0);
    }
}

