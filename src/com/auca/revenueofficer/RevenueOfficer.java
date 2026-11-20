package com.auca.revenueofficer;

import com.auca.abstracts.TaxParty;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RevenueOfficer extends TaxParty {

    // 6 required private fields for RevenueOfficer
    private String staffNumber;
    private String department;
    private String role;
    private LocalDate hireDate;
    private BigDecimal approvalLimit;
    private boolean active;

    // Constructor chains up to TaxParty using super(...)
    public RevenueOfficer(String partyId, String firstName, String lastName,
                           String email, String phoneNumber, LocalDate dateOfBirth,
                           String staffNumber, String department, String role,
                           LocalDate hireDate, BigDecimal approvalLimit, boolean active) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.department = department;
        this.role = role;
        this.hireDate = hireDate;
        this.approvalLimit = approvalLimit;
        this.active = active;
    }

    // Getters
    public String getStaffNumber() { return staffNumber; }
    public String getDepartment() { return department; }
    public String getRole() { return role; }
    public LocalDate getHireDate() { return hireDate; }
    public BigDecimal getApprovalLimit() { return approvalLimit; }

    // Setters
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }
    public void setDepartment(String department) { this.department = department; }
    public void setRole(String role) { this.role = role; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setApprovalLimit(BigDecimal approvalLimit) { this.approvalLimit = approvalLimit; }
    public void setActive(boolean active) { this.active = active; }

    // getDisplayName shows officer name and role
    @Override
    public String getDisplayName() {
        return "Officer: " + getFirstName() + " " + getLastName() + " (" + role + ")";
    }

    // isActive returns the active field
    @Override
    public boolean isActive() {
        return active;
    }

    // validate adds RevenueOfficer specific rules on top of TaxParty validation
    @Override
    public void validate() throws ValidationException {
        super.validate(); // run TaxParty checks first
        if (staffNumber == null || staffNumber.trim().length() < 2 || staffNumber.trim().length() > 30) {
            throw new ValidationException("Staff number must be 2-30 characters.");
        }
        if (department == null || department.trim().length() < 2 || department.trim().length() > 60) {
            throw new ValidationException("Department must be 2-60 characters.");
        }
        if (role == null || role.trim().length() < 2 || role.trim().length() > 60) {
            throw new ValidationException("Role must be 2-60 characters.");
        }
        if (hireDate == null) {
            throw new ValidationException("Hire date cannot be empty.");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Hire date cannot be in the future.");
        }
        if (getDateOfBirth() != null && hireDate.isBefore(getDateOfBirth())) {
            throw new ValidationException("Hire date must be after date of birth.");
        }
        if (approvalLimit == null || approvalLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Approval limit cannot be negative.");
        }
        if (approvalLimit.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Approval limit exceeds maximum allowed value.");
        }
        if (approvalLimit.scale() > 2) {
            throw new ValidationException("Approval limit can have at most 2 decimal places.");
        }
    }

    // printDetails shows all 12 fields (6 TaxParty + 6 own)
    @Override
    public void printDetails() {
        System.out.println("========== REVENUE OFFICER DETAILS ==========");
        super.printDetails();
        System.out.println("  Staff Number    : " + staffNumber);
        System.out.println("  Department      : " + department);
        System.out.println("  Role            : " + role);
        System.out.println("  Hire Date       : " + hireDate);
        System.out.println("  Approval Limit  : " + approvalLimit);
        System.out.println("  Active          : " + (active ? "YES" : "NO"));
        System.out.println("=============================================");
    }

    // getSummary gives a one-line description
    @Override
    public String getSummary() {
        return "OFFICER | " + super.getSummary() + " | Staff#: " + staffNumber + " | Active: " + (active ? "YES" : "NO");
    }
}
