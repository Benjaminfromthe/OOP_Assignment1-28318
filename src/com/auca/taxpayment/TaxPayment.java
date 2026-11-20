package com.auca.taxpayment;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxassessment.TaxAssessment;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxPayment implements Identifiable, Validatable {

    // 6 required private fields for TaxPayment
    private String paymentId;
    private TaxAssessment assessment;     // object reference
    private String paymentReference;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;

    // Constructor
    public TaxPayment(String paymentId, TaxAssessment assessment, String paymentReference,
                      BigDecimal amount, LocalDate paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.assessment = assessment;
        this.paymentReference = paymentReference;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public TaxAssessment getAssessment() { return assessment; }
    public String getPaymentReference() { return paymentReference; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }

    // Setters
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setAssessment(TaxAssessment assessment) { this.assessment = assessment; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // Identifiable interface methods
    @Override
    public String getId() { return paymentId; }

    @Override
    public String getDisplayName() {
        return "Payment [" + paymentId + "] Ref: " + paymentReference + " Amount: " + amount;
    }

    @Override
    public boolean isActive() {
        // a payment is active once it is recorded (always true once saved)
        return true;
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (paymentId == null || paymentId.trim().length() < 2 || paymentId.trim().length() > 30) {
            throw new ValidationException("Payment ID must be 2-30 characters.");
        }
        if (assessment == null) {
            throw new ValidationException("Assessment reference cannot be null.");
        }
        // payments require a CONFIRMED assessment
        if (!"CONFIRMED".equalsIgnoreCase(assessment.getStatus())) {
            throw new ValidationException("Payments can only be made against a CONFIRMED assessment.");
        }
        if (paymentReference == null || paymentReference.trim().length() < 2 || paymentReference.trim().length() > 30) {
            throw new ValidationException("Payment reference must be 2-30 characters.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than zero.");
        }
        if (amount.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Payment amount exceeds maximum allowed value.");
        }
        if (amount.scale() > 2) {
            throw new ValidationException("Payment amount can have at most 2 decimal places.");
        }
        if (paymentDate == null) {
            throw new ValidationException("Payment date cannot be empty.");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Payment date cannot be in the future.");
        }
        if (paymentMethod == null || paymentMethod.trim().length() < 2 || paymentMethod.trim().length() > 30) {
            throw new ValidationException("Payment method must be 2-30 characters.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX PAYMENT DETAILS ==========");
        System.out.println("  Payment ID         : " + paymentId);
        System.out.println("  Assessment ID      : " + assessment.getAssessmentId());
        System.out.println("  Taxpayer TIN       : " + assessment.getDeclaration().getObligation().getTaxpayer().getTin());
        System.out.println("  Payment Reference  : " + paymentReference);
        System.out.println("  Amount             : " + amount);
        System.out.println("  Payment Date       : " + paymentDate);
        System.out.println("  Payment Method     : " + paymentMethod);
        System.out.println("=========================================");
    }

    @Override
    public String getSummary() {
        return "[" + paymentId + "] Ref: " + paymentReference + " | Assessment: "
               + assessment.getAssessmentId() + " | Amount: " + amount + " | Date: " + paymentDate;
    }
}
