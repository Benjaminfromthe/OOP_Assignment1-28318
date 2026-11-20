package com.auca.taxassessment;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.taxdeclaration.TaxDeclaration;
import com.auca.exceptions.ValidationException;
import com.auca.exceptions.InvalidStatusTransitionException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TaxAssessment implements Identifiable, Validatable {

    // 6 required private fields for TaxAssessment
    private String assessmentId;
    private TaxDeclaration declaration;   // object reference
    private BigDecimal assessedTax;
    private BigDecimal adjustmentAmount;
    private LocalDate assessmentDate;
    private String status;               // DRAFT, CONFIRMED, CANCELLED

    // Constructor
    public TaxAssessment(String assessmentId, TaxDeclaration declaration, BigDecimal assessedTax,
                         BigDecimal adjustmentAmount, LocalDate assessmentDate, String status) {
        this.assessmentId = assessmentId;
        this.declaration = declaration;
        this.assessedTax = assessedTax;
        this.adjustmentAmount = adjustmentAmount;
        this.assessmentDate = assessmentDate;
        this.status = status;
    }

    // Getters
    public String getAssessmentId() { return assessmentId; }
    public TaxDeclaration getDeclaration() { return declaration; }
    public BigDecimal getAssessedTax() { return assessedTax; }
    public BigDecimal getAdjustmentAmount() { return adjustmentAmount; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public String getStatus() { return status; }

    // Setters
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    public void setDeclaration(TaxDeclaration declaration) { this.declaration = declaration; }
    public void setAssessedTax(BigDecimal assessedTax) { this.assessedTax = assessedTax; }
    public void setAdjustmentAmount(BigDecimal adjustmentAmount) { this.adjustmentAmount = adjustmentAmount; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }
    public void setStatus(String status) { this.status = status; }

    // Assessment total = assessedTax + adjustmentAmount
    public BigDecimal getTotal() {
        return assessedTax.add(adjustmentAmount);
    }

    // Identifiable interface methods
    @Override
    public String getId() { return assessmentId; }

    @Override
    public String getDisplayName() {
        return "Assessment [" + assessmentId + "] for Declaration " + declaration.getDeclarationId();
    }

    @Override
    public boolean isActive() {
        // assessment is active if it is CONFIRMED
        return "CONFIRMED".equalsIgnoreCase(status);
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (assessmentId == null || assessmentId.trim().length() < 2 || assessmentId.trim().length() > 30) {
            throw new ValidationException("Assessment ID must be 2-30 characters.");
        }
        if (declaration == null) {
            throw new ValidationException("Declaration reference cannot be null.");
        }
        if ("REJECTED".equalsIgnoreCase(declaration.getStatus())) {
            throw new ValidationException("A rejected declaration cannot be assessed.");
        }
        if (assessedTax == null || assessedTax.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Assessed tax cannot be negative.");
        }
        if (assessedTax.scale() > 2) {
            throw new ValidationException("Assessed tax can have at most 2 decimal places.");
        }
        if (adjustmentAmount == null) {
            throw new ValidationException("Adjustment amount cannot be null.");
        }
        if (adjustmentAmount.scale() > 2) {
            throw new ValidationException("Adjustment amount can have at most 2 decimal places.");
        }
        // total = assessedTax + adjustmentAmount, cannot be negative
        if (getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Assessment total (assessedTax + adjustmentAmount) cannot be negative.");
        }
        if (assessmentDate == null) {
            throw new ValidationException("Assessment date cannot be empty.");
        }
        if (assessmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Assessment date cannot be in the future.");
        }
        if (status == null || (!status.equalsIgnoreCase("DRAFT")
                && !status.equalsIgnoreCase("CONFIRMED")
                && !status.equalsIgnoreCase("CANCELLED"))) {
            throw new ValidationException("Assessment status must be DRAFT, CONFIRMED or CANCELLED.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX ASSESSMENT DETAILS ==========");
        System.out.println("  Assessment ID      : " + assessmentId);
        System.out.println("  Declaration ID     : " + declaration.getDeclarationId());
        System.out.println("  Taxpayer TIN       : " + declaration.getObligation().getTaxpayer().getTin());
        System.out.println("  Assessed Tax       : " + assessedTax);
        System.out.println("  Adjustment Amount  : " + adjustmentAmount);
        System.out.println("  Total              : " + getTotal());
        System.out.println("  Assessment Date    : " + assessmentDate);
        System.out.println("  Status             : " + status);
        System.out.println("============================================");
    }

    @Override
    public String getSummary() {
        return "[" + assessmentId + "] Declaration: " + declaration.getDeclarationId()
               + " | Total: " + getTotal() + " | Status: " + status;
    }
}
