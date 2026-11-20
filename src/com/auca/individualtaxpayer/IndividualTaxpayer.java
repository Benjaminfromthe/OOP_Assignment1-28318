package com.auca.individualtaxpayer;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class IndividualTaxpayer extends RegisteredTaxpayer {

    // 6 required private fields for IndividualTaxpayer
    private String nationalId;
    private String occupation;
    private String businessName;
    private LocalDate businessStartDate;
    private BigDecimal annualTurnover;
    private boolean resident;

    // Constructor chains up to RegisteredTaxpayer using super(...)
    public IndividualTaxpayer(String partyId, String firstName, String lastName,
                               String email, String phoneNumber, LocalDate dateOfBirth,
                               String tin, LocalDate registrationDate, String taxpayerStatus,
                               String district, String sector, String taxOffice,
                               String nationalId, String occupation, String businessName,
                               LocalDate businessStartDate, BigDecimal annualTurnover, boolean resident) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth,
              tin, registrationDate, taxpayerStatus, district, sector, taxOffice);
        this.nationalId = nationalId;
        this.occupation = occupation;
        this.businessName = businessName;
        this.businessStartDate = businessStartDate;
        this.annualTurnover = annualTurnover;
        this.resident = resident;
    }

    // Getters
    public String getNationalId() { return nationalId; }
    public String getOccupation() { return occupation; }
    public String getBusinessName() { return businessName; }
    public LocalDate getBusinessStartDate() { return businessStartDate; }
    public BigDecimal getAnnualTurnover() { return annualTurnover; }
    public boolean isResident() { return resident; }

    // Setters
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setBusinessStartDate(LocalDate businessStartDate) { this.businessStartDate = businessStartDate; }
    public void setAnnualTurnover(BigDecimal annualTurnover) { this.annualTurnover = annualTurnover; }
    public void setResident(boolean resident) { this.resident = resident; }

    // getDisplayName shows the full name and type
    @Override
    public String getDisplayName() {
        return "Individual: " + getFirstName() + " " + getLastName();
    }

    // validate adds IndividualTaxpayer specific rules on top of parent validation
    @Override
    public void validate() throws ValidationException {
        super.validate(); // run RegisteredTaxpayer checks first
        if (nationalId == null || !nationalId.matches("\\d{16}")) {
            throw new ValidationException("National ID must be exactly 16 digits.");
        }
        if (occupation == null || occupation.trim().length() < 2 || occupation.trim().length() > 60) {
            throw new ValidationException("Occupation must be 2-60 characters.");
        }
        if (businessName == null || businessName.trim().length() < 2 || businessName.trim().length() > 60) {
            throw new ValidationException("Business name must be 2-60 characters.");
        }
        if (businessStartDate == null) {
            throw new ValidationException("Business start date cannot be empty.");
        }
        if (businessStartDate.isAfter(getRegistrationDate())) {
            throw new ValidationException("Business start date must be before or on registration date.");
        }
        if (annualTurnover == null || annualTurnover.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Annual turnover cannot be negative.");
        }
        if (annualTurnover.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Annual turnover exceeds maximum allowed value.");
        }
        // check max 2 decimal places
        if (annualTurnover.scale() > 2) {
            throw new ValidationException("Annual turnover can have at most 2 decimal places.");
        }
    }

    // printDetails shows all 18 fields (6 from TaxParty + 6 from RegisteredTaxpayer + 6 own)
    @Override
    public void printDetails() {
        System.out.println("========== INDIVIDUAL TAXPAYER DETAILS ==========");
        super.printDetails(); // prints TaxParty + RegisteredTaxpayer fields
        System.out.println("  National ID         : " + nationalId);
        System.out.println("  Occupation          : " + occupation);
        System.out.println("  Business Name       : " + businessName);
        System.out.println("  Business Start Date : " + businessStartDate);
        System.out.println("  Annual Turnover     : " + annualTurnover);
        System.out.println("  Resident            : " + (resident ? "YES" : "NO"));
        System.out.println("=================================================");
    }

    // getSummary gives a one-line description
    @Override
    public String getSummary() {
        return "INDIVIDUAL | " + super.getSummary() + " | NID: " + nationalId;
    }
}
