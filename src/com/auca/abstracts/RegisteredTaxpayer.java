package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public abstract class RegisteredTaxpayer extends TaxParty {

    // 6 required private fields for RegisteredTaxpayer
    private String tin;
    private LocalDate registrationDate;
    private String taxpayerStatus;
    private String district;
    private String sector;
    private String taxOffice;

    // Constructor chains up to TaxParty using super(...)
    public RegisteredTaxpayer(String partyId, String firstName, String lastName,
                               String email, String phoneNumber, LocalDate dateOfBirth,
                               String tin, LocalDate registrationDate, String taxpayerStatus,
                               String district, String sector, String taxOffice) {
        super(partyId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.tin = tin;
        this.registrationDate = registrationDate;
        this.taxpayerStatus = taxpayerStatus;
        this.district = district;
        this.sector = sector;
        this.taxOffice = taxOffice;
    }

    // Getters
    public String getTin() { return tin; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getTaxpayerStatus() { return taxpayerStatus; }
    public String getDistrict() { return district; }
    public String getSector() { return sector; }
    public String getTaxOffice() { return taxOffice; }

    // Setters
    public void setTin(String tin) { this.tin = tin; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public void setTaxpayerStatus(String taxpayerStatus) { this.taxpayerStatus = taxpayerStatus; }
    public void setDistrict(String district) { this.district = district; }
    public void setSector(String sector) { this.sector = sector; }
    public void setTaxOffice(String taxOffice) { this.taxOffice = taxOffice; }

    // isActive checks the taxpayerStatus field
    @Override
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(taxpayerStatus);
    }

    // Validate extends TaxParty validation and adds RegisteredTaxpayer rules
    @Override
    public void validate() throws ValidationException {
        super.validate(); // run TaxParty checks first
        if (tin == null || !tin.matches("\\d{9}")) {
            throw new ValidationException("TIN must be exactly 9 digits.");
        }
        if (registrationDate == null) {
            throw new ValidationException("Registration date cannot be empty.");
        }
        if (registrationDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Registration date cannot be in the future.");
        }
        if (getDateOfBirth() != null && registrationDate.isBefore(getDateOfBirth())) {
            throw new ValidationException("Registration date must be after date of birth.");
        }
        if (taxpayerStatus == null || taxpayerStatus.trim().isEmpty()) {
            throw new ValidationException("Taxpayer status cannot be empty.");
        }
        if (!taxpayerStatus.equalsIgnoreCase("ACTIVE") && !taxpayerStatus.equalsIgnoreCase("INACTIVE") && !taxpayerStatus.equalsIgnoreCase("SUSPENDED")) {
            throw new ValidationException("Taxpayer status must be ACTIVE, INACTIVE or SUSPENDED.");
        }
        if (district == null || district.trim().length() < 2 || district.trim().length() > 60) {
            throw new ValidationException("District must be 2-60 characters.");
        }
        if (sector == null || sector.trim().length() < 2 || sector.trim().length() > 60) {
            throw new ValidationException("Sector must be 2-60 characters.");
        }
        if (taxOffice == null || taxOffice.trim().length() < 2 || taxOffice.trim().length() > 60) {
            throw new ValidationException("Tax office must be 2-60 characters.");
        }
    }

    // printDetails adds RegisteredTaxpayer fields on top of TaxParty fields
    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  TIN               : " + tin);
        System.out.println("  Registration Date : " + registrationDate);
        System.out.println("  Status            : " + taxpayerStatus);
        System.out.println("  District          : " + district);
        System.out.println("  Sector            : " + sector);
        System.out.println("  Tax Office        : " + taxOffice);
    }

    // getSummary overrides to include TIN
    @Override
    public String getSummary() {
        return super.getSummary() + " | TIN: " + tin + " | Status: " + taxpayerStatus;
    }
}
