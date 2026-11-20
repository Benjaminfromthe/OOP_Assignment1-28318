package com.auca.taxobligation;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.taxtype.TaxType;
import com.auca.exceptions.ValidationException;
import com.auca.exceptions.InvalidTaxPeriodException;
import java.time.LocalDate;

public class TaxObligation implements Identifiable, Validatable {

    // 6 required private fields for TaxObligation
    private String obligationId;
    private RegisteredTaxpayer taxpayer;   // object reference, not just an ID
    private TaxType taxType;               // object reference, not just an ID
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDate dueDate;

    // Constructor
    public TaxObligation(String obligationId, RegisteredTaxpayer taxpayer, TaxType taxType,
                         LocalDate periodStart, LocalDate periodEnd, LocalDate dueDate) {
        this.obligationId = obligationId;
        this.taxpayer = taxpayer;
        this.taxType = taxType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.dueDate = dueDate;
    }

    // Getters
    public String getObligationId() { return obligationId; }
    public RegisteredTaxpayer getTaxpayer() { return taxpayer; }
    public TaxType getTaxType() { return taxType; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public LocalDate getDueDate() { return dueDate; }

    // Setters
    public void setObligationId(String obligationId) { this.obligationId = obligationId; }
    public void setTaxpayer(RegisteredTaxpayer taxpayer) { this.taxpayer = taxpayer; }
    public void setTaxType(TaxType taxType) { this.taxType = taxType; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    // Identifiable interface methods
    @Override
    public String getId() { return obligationId; }

    @Override
    public String getDisplayName() {
        return "Obligation [" + obligationId + "] for " + taxpayer.getTin() + " - " + taxType.getTaxName();
    }

    @Override
    public boolean isActive() {
        // obligation is active if both taxpayer and taxType are active
        return taxpayer.isActive() && taxType.isActive();
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (obligationId == null || obligationId.trim().length() < 2 || obligationId.trim().length() > 30) {
            throw new ValidationException("Obligation ID must be 2-30 characters.");
        }
        if (taxpayer == null) {
            throw new ValidationException("Taxpayer reference cannot be null.");
        }
        if (!taxpayer.isActive()) {
            throw new ValidationException("Taxpayer must be ACTIVE to create an obligation.");
        }
        if (taxType == null) {
            throw new ValidationException("Tax type reference cannot be null.");
        }
        if (!taxType.isActive()) {
            throw new ValidationException("Tax type must be active to create an obligation.");
        }
        if (periodStart == null) {
            throw new ValidationException("Period start date cannot be empty.");
        }
        if (periodEnd == null) {
            throw new ValidationException("Period end date cannot be empty.");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new ValidationException("Period start must be on or before period end.");
        }
        if (dueDate == null) {
            throw new ValidationException("Due date cannot be empty.");
        }
        if (dueDate.isBefore(periodEnd)) {
            throw new ValidationException("Due date must not be before period end.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX OBLIGATION DETAILS ==========");
        System.out.println("  Obligation ID  : " + obligationId);
        System.out.println("  Taxpayer       : " + taxpayer.getDisplayName() + " | TIN: " + taxpayer.getTin());
        System.out.println("  Tax Type       : " + taxType.getTaxName() + " (" + taxType.getTaxCode() + ")");
        System.out.println("  Period Start   : " + periodStart);
        System.out.println("  Period End     : " + periodEnd);
        System.out.println("  Due Date       : " + dueDate);
        System.out.println("============================================");
    }

    @Override
    public String getSummary() {
        return "[" + obligationId + "] TIN: " + taxpayer.getTin() + " | " + taxType.getTaxName()
               + " | " + periodStart + " to " + periodEnd + " | Due: " + dueDate;
    }
}
