package com.auca.taxtype;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;

public class TaxType implements Identifiable, Validatable {

    // 6 required private fields for TaxType
    private String taxTypeId;
    private String taxCode;
    private String taxName;
    private BigDecimal rate;
    private String filingFrequency;
    private boolean active;

    // Constructor
    public TaxType(String taxTypeId, String taxCode, String taxName,
                   BigDecimal rate, String filingFrequency, boolean active) {
        this.taxTypeId = taxTypeId;
        this.taxCode = taxCode;
        this.taxName = taxName;
        this.rate = rate;
        this.filingFrequency = filingFrequency;
        this.active = active;
    }

    // Getters
    public String getTaxTypeId() { return taxTypeId; }
    public String getTaxCode() { return taxCode; }
    public String getTaxName() { return taxName; }
    public BigDecimal getRate() { return rate; }
    public String getFilingFrequency() { return filingFrequency; }

    // Setters
    public void setTaxTypeId(String taxTypeId) { this.taxTypeId = taxTypeId; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    public void setTaxName(String taxName) { this.taxName = taxName; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public void setFilingFrequency(String filingFrequency) { this.filingFrequency = filingFrequency; }
    public void setActive(boolean active) { this.active = active; }

    // Identifiable interface methods
    @Override
    public String getId() { return taxTypeId; }

    @Override
    public String getDisplayName() { return taxName + " (" + taxCode + ")"; }

    @Override
    public boolean isActive() { return active; }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (taxTypeId == null || taxTypeId.trim().length() < 2 || taxTypeId.trim().length() > 30) {
            throw new ValidationException("Tax Type ID must be 2-30 characters.");
        }
        if (taxCode == null || taxCode.trim().length() < 2 || taxCode.trim().length() > 20) {
            throw new ValidationException("Tax code must be 2-20 characters.");
        }
        if (taxName == null || taxName.trim().length() < 2 || taxName.trim().length() > 60) {
            throw new ValidationException("Tax name must be 2-60 characters.");
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(new BigDecimal("100")) > 0) {
            throw new ValidationException("Tax rate must be between 0 and 100.");
        }
        if (rate.scale() > 2) {
            throw new ValidationException("Tax rate can have at most 2 decimal places.");
        }
        if (filingFrequency == null ||
            (!filingFrequency.equalsIgnoreCase("MONTHLY") &&
             !filingFrequency.equalsIgnoreCase("QUARTERLY") &&
             !filingFrequency.equalsIgnoreCase("ANNUAL"))) {
            throw new ValidationException("Filing frequency must be MONTHLY, QUARTERLY or ANNUAL.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX TYPE DETAILS ==========");
        System.out.println("  Tax Type ID       : " + taxTypeId);
        System.out.println("  Tax Code          : " + taxCode);
        System.out.println("  Tax Name          : " + taxName);
        System.out.println("  Rate (%)          : " + rate);
        System.out.println("  Filing Frequency  : " + filingFrequency);
        System.out.println("  Active            : " + (active ? "YES" : "NO"));
        System.out.println("======================================");
    }

    @Override
    public String getSummary() {
        return "[" + taxTypeId + "] " + taxName + " | Code: " + taxCode + " | Rate: " + rate + "% | " + filingFrequency;
    }
}
