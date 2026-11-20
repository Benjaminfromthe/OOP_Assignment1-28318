package com.auca.taxdeclaration;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxobligation.TaxObligation;
import com.auca.exceptions.ValidationException;
import com.auca.exceptions.InvalidTaxCalculationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class TaxDeclaration implements Identifiable, Validatable {

    // 6 required private fields for TaxDeclaration
    private String declarationId;
    private TaxObligation obligation;    // object reference
    private LocalDate filingDate;
    private BigDecimal taxableAmount;
    private BigDecimal declaredTax;
    private String status;              // PENDING, ACCEPTED, REJECTED, LATE

    // Constructor
    public TaxDeclaration(String declarationId, TaxObligation obligation, LocalDate filingDate,
                          BigDecimal taxableAmount, BigDecimal declaredTax, String status) {
        this.declarationId = declarationId;
        this.obligation = obligation;
        this.filingDate = filingDate;
        this.taxableAmount = taxableAmount;
        this.declaredTax = declaredTax;
        this.status = status;
    }

    // Getters
    public String getDeclarationId() { return declarationId; }
    public TaxObligation getObligation() { return obligation; }
    public LocalDate getFilingDate() { return filingDate; }
    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public BigDecimal getDeclaredTax() { return declaredTax; }
    public String getStatus() { return status; }

    // Setters
    public void setDeclarationId(String declarationId) { this.declarationId = declarationId; }
    public void setObligation(TaxObligation obligation) { this.obligation = obligation; }
    public void setFilingDate(LocalDate filingDate) { this.filingDate = filingDate; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }
    public void setDeclaredTax(BigDecimal declaredTax) { this.declaredTax = declaredTax; }
    public void setStatus(String status) { this.status = status; }

    // Check if this declaration was filed late
    public boolean isLate() {
        return filingDate != null && obligation != null
               && filingDate.isAfter(obligation.getDueDate());
    }

    // Calculate what the expected tax should be based on the tax type rate
    public BigDecimal calculateExpectedTax() {
        // expected tax = taxableAmount x rate / 100, rounded HALF_UP to 2 decimals
        BigDecimal rate = obligation.getTaxType().getRate();
        return taxableAmount.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    // Identifiable interface methods
    @Override
    public String getId() { return declarationId; }

    @Override
    public String getDisplayName() {
        return "Declaration [" + declarationId + "] for Obligation " + obligation.getObligationId();
    }

    @Override
    public boolean isActive() {
        // a declaration is active if it is PENDING or ACCEPTED (not REJECTED)
        return "PENDING".equalsIgnoreCase(status) || "ACCEPTED".equalsIgnoreCase(status)
               || "LATE".equalsIgnoreCase(status);
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (declarationId == null || declarationId.trim().length() < 2 || declarationId.trim().length() > 30) {
            throw new ValidationException("Declaration ID must be 2-30 characters.");
        }
        if (obligation == null) {
            throw new ValidationException("Obligation reference cannot be null.");
        }
        if (filingDate == null) {
            throw new ValidationException("Filing date cannot be empty.");
        }
        if (filingDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Filing date cannot be in the future.");
        }
        if (taxableAmount == null || taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Taxable amount cannot be negative.");
        }
        if (taxableAmount.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Taxable amount exceeds maximum allowed value.");
        }
        if (taxableAmount.scale() > 2) {
            throw new ValidationException("Taxable amount can have at most 2 decimal places.");
        }
        if (declaredTax == null || declaredTax.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Declared tax cannot be negative.");
        }
        // check declared tax matches expected tax within 0.01
        BigDecimal expected = calculateExpectedTax();
        if (declaredTax.subtract(expected).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new ValidationException("Declared tax " + declaredTax
                + " does not match expected tax " + expected + " (must be within 0.01).");
        }
        if (status == null || (!status.equalsIgnoreCase("PENDING") && !status.equalsIgnoreCase("ACCEPTED")
                && !status.equalsIgnoreCase("REJECTED") && !status.equalsIgnoreCase("LATE"))) {
            throw new ValidationException("Declaration status must be PENDING, ACCEPTED, REJECTED or LATE.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX DECLARATION DETAILS ==========");
        System.out.println("  Declaration ID   : " + declarationId);
        System.out.println("  Obligation ID    : " + obligation.getObligationId());
        System.out.println("  Taxpayer TIN     : " + obligation.getTaxpayer().getTin());
        System.out.println("  Filing Date      : " + filingDate);
        System.out.println("  Taxable Amount   : " + taxableAmount);
        System.out.println("  Declared Tax     : " + declaredTax);
        System.out.println("  Expected Tax     : " + calculateExpectedTax());
        System.out.println("  Status           : " + status);
        System.out.println("  Filed Late       : " + (isLate() ? "YES" : "NO"));
        System.out.println("=============================================");
    }

    @Override
    public String getSummary() {
        return "[" + declarationId + "] Obligation: " + obligation.getObligationId()
               + " | Filed: " + filingDate + " | Tax: " + declaredTax + " | Status: " + status;
    }
}
