package com.auca.penalty;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxobligation.TaxObligation;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Penalty implements Identifiable, Validatable {

    // 6 required private fields for Penalty
    private String penaltyId;
    private TaxObligation obligation;    // object reference
    private String penaltyType;
    private BigDecimal baseAmount;
    private BigDecimal penaltyRate;
    private BigDecimal penaltyAmount;

    // Constructor
    public Penalty(String penaltyId, TaxObligation obligation, String penaltyType,
                   BigDecimal baseAmount, BigDecimal penaltyRate, BigDecimal penaltyAmount) {
        this.penaltyId = penaltyId;
        this.obligation = obligation;
        this.penaltyType = penaltyType;
        this.baseAmount = baseAmount;
        this.penaltyRate = penaltyRate;
        this.penaltyAmount = penaltyAmount;
    }

    // Getters
    public String getPenaltyId() { return penaltyId; }
    public TaxObligation getObligation() { return obligation; }
    public String getPenaltyType() { return penaltyType; }
    public BigDecimal getBaseAmount() { return baseAmount; }
    public BigDecimal getPenaltyRate() { return penaltyRate; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; }

    // Setters
    public void setPenaltyId(String penaltyId) { this.penaltyId = penaltyId; }
    public void setObligation(TaxObligation obligation) { this.obligation = obligation; }
    public void setPenaltyType(String penaltyType) { this.penaltyType = penaltyType; }
    public void setBaseAmount(BigDecimal baseAmount) { this.baseAmount = baseAmount; }
    public void setPenaltyRate(BigDecimal penaltyRate) { this.penaltyRate = penaltyRate; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }

    // Calculate penalty: baseAmount x penaltyRate / 100, rounded HALF_UP to 2 decimals
    public static BigDecimal calculatePenalty(BigDecimal baseAmount, BigDecimal penaltyRate) {
        return baseAmount.multiply(penaltyRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    // Identifiable interface methods
    @Override
    public String getId() { return penaltyId; }

    @Override
    public String getDisplayName() {
        return "Penalty [" + penaltyId + "] Type: " + penaltyType + " Amount: " + penaltyAmount;
    }

    @Override
    public boolean isActive() {
        return true; // penalty is always active once recorded
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (penaltyId == null || penaltyId.trim().length() < 2 || penaltyId.trim().length() > 30) {
            throw new ValidationException("Penalty ID must be 2-30 characters.");
        }
        if (obligation == null) {
            throw new ValidationException("Obligation reference cannot be null.");
        }
        if (penaltyType == null || penaltyType.trim().length() < 2 || penaltyType.trim().length() > 30) {
            throw new ValidationException("Penalty type must be 2-30 characters.");
        }
        if (baseAmount == null || baseAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Base amount cannot be negative.");
        }
        if (baseAmount.scale() > 2) {
            throw new ValidationException("Base amount can have at most 2 decimal places.");
        }
        if (penaltyRate == null || penaltyRate.compareTo(BigDecimal.ZERO) < 0
                || penaltyRate.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Penalty rate must be between 0 and 100.");
        }
        if (penaltyAmount == null || penaltyAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Penalty amount cannot be negative.");
        }
        if (penaltyAmount.scale() > 2) {
            throw new ValidationException("Penalty amount can have at most 2 decimal places.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== PENALTY DETAILS ==========");
        System.out.println("  Penalty ID      : " + penaltyId);
        System.out.println("  Obligation ID   : " + obligation.getObligationId());
        System.out.println("  Taxpayer TIN    : " + obligation.getTaxpayer().getTin());
        System.out.println("  Penalty Type    : " + penaltyType);
        System.out.println("  Base Amount     : " + baseAmount);
        System.out.println("  Penalty Rate    : " + penaltyRate + "%");
        System.out.println("  Penalty Amount  : " + penaltyAmount);
        System.out.println("=====================================");
    }

    @Override
    public String getSummary() {
        return "[" + penaltyId + "] Type: " + penaltyType + " | Obligation: "
               + obligation.getObligationId() + " | Amount: " + penaltyAmount;
    }
}
