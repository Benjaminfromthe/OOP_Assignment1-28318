package com.auca.taxclearance;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.revenueofficer.RevenueOfficer;
import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public class TaxClearance implements Identifiable, Validatable {

    // 6 required private fields for TaxClearance
    private String clearanceId;
    private RegisteredTaxpayer taxpayer;   // object reference
    private LocalDate requestDate;
    private LocalDate expiryDate;
    private String status;                 // PENDING, APPROVED, REJECTED
    private RevenueOfficer approvedBy;     // object reference

    // Constructor
    public TaxClearance(String clearanceId, RegisteredTaxpayer taxpayer, LocalDate requestDate,
                        LocalDate expiryDate, String status, RevenueOfficer approvedBy) {
        this.clearanceId = clearanceId;
        this.taxpayer = taxpayer;
        this.requestDate = requestDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.approvedBy = approvedBy;
    }

    // Getters
    public String getClearanceId() { return clearanceId; }
    public RegisteredTaxpayer getTaxpayer() { return taxpayer; }
    public LocalDate getRequestDate() { return requestDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public String getStatus() { return status; }
    public RevenueOfficer getApprovedBy() { return approvedBy; }

    // Setters
    public void setClearanceId(String clearanceId) { this.clearanceId = clearanceId; }
    public void setTaxpayer(RegisteredTaxpayer taxpayer) { this.taxpayer = taxpayer; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setStatus(String status) { this.status = status; }
    public void setApprovedBy(RevenueOfficer approvedBy) { this.approvedBy = approvedBy; }

    // Identifiable interface methods
    @Override
    public String getId() { return clearanceId; }

    @Override
    public String getDisplayName() {
        return "Clearance [" + clearanceId + "] for TIN: " + taxpayer.getTin() + " | Status: " + status;
    }

    @Override
    public boolean isActive() {
        return "APPROVED".equalsIgnoreCase(status);
    }

    // Validatable interface methods
    @Override
    public void validate() throws ValidationException {
        if (clearanceId == null || clearanceId.trim().length() < 2 || clearanceId.trim().length() > 30) {
            throw new ValidationException("Clearance ID must be 2-30 characters.");
        }
        if (taxpayer == null) {
            throw new ValidationException("Taxpayer reference cannot be null.");
        }
        if (!taxpayer.isActive()) {
            throw new ValidationException("Taxpayer must be ACTIVE to request clearance.");
        }
        if (requestDate == null) {
            throw new ValidationException("Request date cannot be empty.");
        }
        if (requestDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Request date cannot be in the future.");
        }
        if (expiryDate == null) {
            throw new ValidationException("Expiry date cannot be empty.");
        }
        // expiry must be after request date
        if (!expiryDate.isAfter(requestDate)) {
            throw new ValidationException("Expiry date must be after request date.");
        }
        if (status == null || (!status.equalsIgnoreCase("PENDING")
                && !status.equalsIgnoreCase("APPROVED")
                && !status.equalsIgnoreCase("REJECTED"))) {
            throw new ValidationException("Clearance status must be PENDING, APPROVED or REJECTED.");
        }
        // if status is APPROVED, approvedBy officer must be set and active
        if ("APPROVED".equalsIgnoreCase(status)) {
            if (approvedBy == null) {
                throw new ValidationException("An approving officer must be assigned for APPROVED clearance.");
            }
            if (!approvedBy.isActive()) {
                throw new ValidationException("Approving officer must be active.");
            }
        }
    }

    @Override
    public void printDetails() {
        System.out.println("========== TAX CLEARANCE DETAILS ==========");
        System.out.println("  Clearance ID    : " + clearanceId);
        System.out.println("  Taxpayer TIN    : " + taxpayer.getTin());
        System.out.println("  Taxpayer Name   : " + taxpayer.getDisplayName());
        System.out.println("  Request Date    : " + requestDate);
        System.out.println("  Expiry Date     : " + expiryDate);
        System.out.println("  Status          : " + status);
        System.out.println("  Approved By     : " + (approvedBy != null ? approvedBy.getDisplayName() : "N/A"));
        System.out.println("===========================================");
    }

    @Override
    public String getSummary() {
        return "[" + clearanceId + "] TIN: " + taxpayer.getTin() + " | Status: " + status
               + " | Expires: " + expiryDate
               + " | Officer: " + (approvedBy != null ? approvedBy.getStaffNumber() : "N/A");
    }
}
