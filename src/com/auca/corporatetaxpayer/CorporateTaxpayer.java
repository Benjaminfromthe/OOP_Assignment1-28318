package com.auca.corporatetaxpayer;

import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CorporateTaxpayer extends RegisteredTaxpayer {

    // 6 required private fields for CorporateTaxpayer
    private String companyRegistrationNo;
    private String legalName;
    private LocalDate incorporationDate;
    private String businessSector;
    private String authorizedRepresentative;
    private BigDecimal annualTurnover;

    // Constructor chains up to RegisteredTaxpayer using super(...)
    public CorporateTaxpayer(String partyId, String firstName, String lastName,
                              String email, String phoneNumber, LocalDate dateOfBirth,
                              String tin, LocalDate registrationDate, String taxpayerStatus,
                              String district, String sector, String taxOffice,
                              String companyRegistrationNo, String legalName, LocalDate incorporationDate,
                              String businessSector, String authorizedRepresentative, BigDecimal annualTurnover) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth,
              tin, registrationDate, taxpayerStatus, district, sector, taxOffice);
        this.companyRegistrationNo = companyRegistrationNo;
        this.legalName = legalName;
        this.incorporationDate = incorporationDate;
        this.businessSector = businessSector;
        this.authorizedRepresentative = authorizedRepresentative;
        this.annualTurnover = annualTurnover;
    }

    // Getters
    public String getCompanyRegistrationNo() { return companyRegistrationNo; }
    public String getLegalName() { return legalName; }
    public LocalDate getIncorporationDate() { return incorporationDate; }
    public String getBusinessSector() { return businessSector; }
    public String getAuthorizedRepresentative() { return authorizedRepresentative; }
    public BigDecimal getAnnualTurnover() { return annualTurnover; }

    // Setters
    public void setCompanyRegistrationNo(String companyRegistrationNo) { this.companyRegistrationNo = companyRegistrationNo; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public void setIncorporationDate(LocalDate incorporationDate) { this.incorporationDate = incorporationDate; }
    public void setBusinessSector(String businessSector) { this.businessSector = businessSector; }
    public void setAuthorizedRepresentative(String authorizedRepresentative) { this.authorizedRepresentative = authorizedRepresentative; }
    public void setAnnualTurnover(BigDecimal annualTurnover) { this.annualTurnover = annualTurnover; }

    // getDisplayName shows the company name and type
    @Override
    public String getDisplayName() {
        return "Corporate: " + legalName;
    }

    // validate adds CorporateTaxpayer specific rules on top of parent validation
    @Override
    public void validate() throws ValidationException {
        super.validate(); // run RegisteredTaxpayer checks first
        // company registration: 5-30 uppercase letters/digits/hyphens, unique
        if (companyRegistrationNo == null || !companyRegistrationNo.matches("[A-Z0-9\\-]{5,30}")) {
            throw new ValidationException("Company registration number must be 5-30 uppercase letters, digits or hyphens.");
        }
        if (legalName == null || legalName.trim().length() < 3 || legalName.trim().length() > 120) {
            throw new ValidationException("Legal name must be 3-120 characters.");
        }
        if (incorporationDate == null) {
            throw new ValidationException("Incorporation date cannot be empty.");
        }
        if (incorporationDate.isAfter(getRegistrationDate())) {
            throw new ValidationException("Incorporation date must be before or on registration date.");
        }
        if (businessSector == null || businessSector.trim().length() < 2 || businessSector.trim().length() > 60) {
            throw new ValidationException("Business sector must be 2-60 characters.");
        }
        if (authorizedRepresentative == null || authorizedRepresentative.trim().length() < 2 || authorizedRepresentative.trim().length() > 60) {
            throw new ValidationException("Authorized representative must be 2-60 characters.");
        }
        if (annualTurnover == null || annualTurnover.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Annual turnover cannot be negative.");
        }
        if (annualTurnover.compareTo(new BigDecimal("999999999999.99")) > 0) {
            throw new ValidationException("Annual turnover exceeds maximum allowed value.");
        }
        if (annualTurnover.scale() > 2) {
            throw new ValidationException("Annual turnover can have at most 2 decimal places.");
        }
    }

    // printDetails shows all 18 fields (6 TaxParty + 6 RegisteredTaxpayer + 6 own)
    @Override
    public void printDetails() {
        System.out.println("========== CORPORATE TAXPAYER DETAILS ==========");
        super.printDetails();
        System.out.println("  Company Reg No          : " + companyRegistrationNo);
        System.out.println("  Legal Name              : " + legalName);
        System.out.println("  Incorporation Date      : " + incorporationDate);
        System.out.println("  Business Sector         : " + businessSector);
        System.out.println("  Authorized Rep.         : " + authorizedRepresentative);
        System.out.println("  Annual Turnover         : " + annualTurnover);
        System.out.println("================================================");
    }

    // getSummary gives a one-line description
    @Override
    public String getSummary() {
        return "CORPORATE | " + super.getSummary() + " | Reg: " + companyRegistrationNo;
    }
}
