package com.auca.abstracts;

import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public abstract class TaxParty implements Identifiable, Validatable {

    // 6 required private fields for TaxParty
    private String partyId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    // Constructor using constructor chaining pattern
    public TaxParty(String partyId, String firstName, String lastName,
                    String email, String phoneNumber, LocalDate dateOfBirth) {
        this.partyId = partyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters
    public String getPartyId() { return partyId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }

    // Setters
    public void setPartyId(String partyId) { this.partyId = partyId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    // Identifiable interface - getId() maps to partyId
    @Override
    public String getId() {
        return partyId;
    }

    // getDisplayName and isActive are left abstract so each subclass defines them
    @Override
    public abstract String getDisplayName();

    @Override
    public abstract boolean isActive();

    // Validatable interface - validate common TaxParty fields
    @Override
    public void validate() throws ValidationException {
        if (partyId == null || partyId.trim().isEmpty()) {
            throw new ValidationException("Party ID cannot be empty.");
        }
        if (partyId.trim().length() < 4 || partyId.trim().length() > 30) {
            throw new ValidationException("Party ID must be 4-30 characters.");
        }
        if (firstName == null || firstName.trim().length() < 2 || firstName.trim().length() > 60) {
            throw new ValidationException("First name must be 2-60 characters.");
        }
        if (lastName == null || lastName.trim().length() < 2 || lastName.trim().length() > 60) {
            throw new ValidationException("Last name must be 2-60 characters.");
        }
        if (email == null || !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$") || email.length() > 120) {
            throw new ValidationException("Email is not valid or exceeds 120 characters.");
        }
        if (phoneNumber == null || !phoneNumber.matches("^\\+?\\d{10,15}$")) {
            throw new ValidationException("Phone number must be 10-15 digits, optional leading +.");
        }
        if (dateOfBirth == null) {
            throw new ValidationException("Date of birth cannot be empty.");
        }
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future.");
        }
    }

    // printDetails shows all TaxParty fields
    @Override
    public void printDetails() {
        System.out.println("  Party ID      : " + partyId);
        System.out.println("  First Name    : " + firstName);
        System.out.println("  Last Name     : " + lastName);
        System.out.println("  Email         : " + email);
        System.out.println("  Phone         : " + phoneNumber);
        System.out.println("  Date of Birth : " + dateOfBirth);
    }

    // getSummary returns one-line summary
    @Override
    public String getSummary() {
        return "[" + partyId + "] " + firstName + " " + lastName + " | " + email;
    }
}
