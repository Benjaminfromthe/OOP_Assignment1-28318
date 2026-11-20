package com.auca.taxrefund;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.taxassessment.TaxAssessment;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxRefund implements Identifiable, Validatable {

    // 6 required private fields for TaxRefund
    private String refundId;
    private RegisteredTaxpayer taxpayer;   // object reference
    private TaxAssessment assessment;      // object reference
    private BigDecimal requestedAmount;
    private LocalDate requestDate;
    private String status;                 // PENDING, APPROVED, REJECTED

    // Constructor
    public TaxRefund(String refundId, RegisteredTaxpayer taxpayer, TaxAssessment assessment,
                     BigDecimal requestedAmount, LocalDate requestDate, String status) {
        this.refundId = refundId;
        this.taxpayer = taxpayer;
        this.assessment = assessment;
        this.requestedAmount = requestedAmount;
        this.requestDate = requestDate;
        this.status = status;
    }

    // Getters
    public String getRefundId() { return refundId; }
    public RegisteredTaxpayer getTaxpayer() { return taxpayer; }
    public TaxAssessment getAssessment() { return assessment; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public LocalDate getRequestDate() { return requestDate; }
    public String getStatus() { return status; }

    // Setters
    public void setRefundId(String refundId) { this.refundId = refundId; }
    public void setTaxpayer(RegisteredTaxpayer taxpayer) { this.taxpayer = taxpayer; }
    public void setAssessment(TaxAssessment assessment) { this.assessment = assessment; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    public void setStatus(String status) { this.status = status; }

    // Identifiable interface methods
    @Override
    public String getId() { return refundId; }

    @Override
    public String getDisplayName() {
        return "Refund [" + refundId + "] for TIN: " + taxpayer.getTin() + " Amount: " + requestedAmount;
    }

    @Override
    public boolean isActive() {
        return "PENDING".equalsIgnoreCase(status);
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (refundId == null || refundId.trim().length() < 2 || refundId.trim().length() > 30) {
            throw new ValidationException("Refund ID must be 2-30 characters.");
        }
        if (taxpayer == null) {
            throw new ValidationException("Taxpayer reference cannot be null.");
        }
        if (assessment == null) {
            throw new ValidationException("Assessment reference cannot be null.");
        }
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Requested refund amount must be greater than zero.");
        }
        if (requestedAmount.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Requested amount exceeds maximum allowed value.");
        }
        if (requestedAmount.scale() > 2) {
            throw new ValidationException("Requested amount can have at most 2 decimal places.");
        }
        if (requestDate == null) {
            throw new ValidationException("Request date cannot be empty.");
        }
        if (requestDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Request date cannot be in the future.");
        }
        if (status == null || (!status.equalsIgnoreCase("PENDING")
                && !status.equalsIgnoreCase("APPROVED")
                && !status.equalsIgnoreCase("REJECTED"))) {
            throw new ValidationException("Refund status must be PENDING, APPROVED or REJECTED.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX REFUND DETAILS ==========");
        System.out.println("  Refund ID         : " + refundId);
        System.out.println("  Taxpayer TIN      : " + taxpayer.getTin());
        System.out.println("  Taxpayer Name     : " + taxpayer.getDisplayName());
        System.out.println("  Assessment ID     : " + assessment.getAssessmentId());
        System.out.println("  Requested Amount  : " + requestedAmount);
        System.out.println("  Request Date      : " + requestDate);
        System.out.println("  Status            : " + status);
        System.out.println("========================================");
    }

    @Override
    public String getSummary() {
        return "[" + refundId + "] TIN: " + taxpayer.getTin() + " | Assessment: "
               + assessment.getAssessmentId() + " | Amount: " + requestedAmount + " | Status: " + status;
    }
}
