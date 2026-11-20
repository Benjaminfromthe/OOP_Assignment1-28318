package com.auca.main;

import com.auca.interfaces.Identifiable;
import com.auca.abstracts.TaxParty;
import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.individualtaxpayer.IndividualTaxpayer;
import com.auca.corporatetaxpayer.CorporateTaxpayer;
import com.auca.revenueofficer.RevenueOfficer;
import com.auca.taxtype.TaxType;
import com.auca.taxobligation.TaxObligation;
import com.auca.taxdeclaration.TaxDeclaration;
import com.auca.taxassessment.TaxAssessment;
import com.auca.taxpayment.TaxPayment;
import com.auca.penalty.Penalty;
import com.auca.taxrefund.TaxRefund;
import com.auca.taxclearance.TaxClearance;
import com.auca.exceptions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    // ---- Single shared Scanner (never mix nextInt with nextLine) ----
    static Scanner input = new Scanner(System.in);

    // ---- Fixed-size arrays and counters (no collections allowed) ----
    static IndividualTaxpayer[] individuals   = new IndividualTaxpayer[100];
    static int individualCount = 0;

    static CorporateTaxpayer[] corporates     = new CorporateTaxpayer[100];
    static int corporateCount = 0;

    static RevenueOfficer[] officers          = new RevenueOfficer[50];
    static int officerCount = 0;

    static TaxType[] taxTypes                 = new TaxType[50];
    static int taxTypeCount = 0;

    static TaxObligation[] obligations        = new TaxObligation[200];
    static int obligationCount = 0;

    static TaxDeclaration[] declarations      = new TaxDeclaration[200];
    static int declarationCount = 0;

    static TaxAssessment[] assessments        = new TaxAssessment[200];
    static int assessmentCount = 0;

    static TaxPayment[] payments              = new TaxPayment[300];
    static int paymentCount = 0;

    static Penalty[] penalties                = new Penalty[200];
    static int penaltyCount = 0;

    static TaxRefund[] refunds                = new TaxRefund[100];
    static int refundCount = 0;

    static TaxClearance[] clearances          = new TaxClearance[100];
    static int clearanceCount = 0;

    // ================================================================
    //  MAIN METHOD
    // ================================================================
    public static void main(String[] args) {
        System.out.println("Welcome to the Tax Administration Management System (TAMS)");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = input.nextLine().trim();
            switch (choice) {
                case "1":  registerIndividualTaxpayer(); break;
                case "2":  registerCorporateTaxpayer();  break;
                case "3":  registerRevenueOfficer();     break;
                case "4":  registerTaxType();             break;
                case "5":  createTaxObligation();        break;
                case "6":  fileTaxDeclaration();         break;
                case "7":  assessDeclaration();          break;
                case "8":  confirmAssessment();          break;
                case "9":  viewAllRecords();             break;
                case "10": viewDeclarationHistory();     break;
                case "11": viewAssessmentHistory();      break;
                case "12": recordTaxPayment();           break;
                case "13": calculatePenalty();           break;
                case "14": requestApproveRefund();       break;
                case "15": requestIssueClearance();      break;
                case "16": searchRecord();               break;
                case "17": updateRecord();               break;
                case "18": viewTaxpayerStatement();      break;
                case "19": viewOutstandingBalances();    break;
                case "20": viewTaxReports();             break;
                case "21": demonstratePolymorphism();    break;
                case "0":
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please enter a number from 0 to 21.");
            }
        }
    }

    // ================================================================
    //  MENU DISPLAY
    // ================================================================
    static void printMenu() {
        System.out.println();
        System.out.println("================ TAX ADMINISTRATION MANAGEMENT ================");
        System.out.println("  1  Register Individual Taxpayer    12  Record Tax Payment");
        System.out.println("  2  Register Corporate Taxpayer     13  Calculate / Record Penalty");
        System.out.println("  3  Register Revenue Officer        14  Request / Approve Refund");
        System.out.println("  4  Register Tax Type               15  Request / Issue Tax Clearance");
        System.out.println("  5  Create Tax Obligation           16  Search Record by ID / TIN");
        System.out.println("  6  File Tax Declaration            17  Update Eligible Record");
        System.out.println("  7  Assess Declaration              18  View Taxpayer Statement");
        System.out.println("  8  Confirm Assessment              19  View Outstanding Balances");
        System.out.println("  9  View All Records                20  View Tax Reports");
        System.out.println(" 10  View Declaration History        21  Demonstrate Polymorphism");
        System.out.println(" 11  View Assessment History          0  Exit");
        System.out.println("================================================================");
        System.out.print("Select option: ");
    }

    // ================================================================
    //  HELPER: read a non-blank line
    // ================================================================
    static String readLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("  Input cannot be blank. Please try again.");
        }
    }

    // ================================================================
    //  HELPER: read a LocalDate
    // ================================================================
    static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (value.equalsIgnoreCase("CANCEL")) return null;
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date format. Use YYYY-MM-DD (e.g. 2000-01-31). Type CANCEL to abort.");
            }
        }
    }

    // ================================================================
    //  HELPER: read a BigDecimal
    // ================================================================
    static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            if (value.equalsIgnoreCase("CANCEL")) return null;
            try {
                BigDecimal bd = new BigDecimal(value);
                return bd;
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a valid decimal (e.g. 1500.00). Type CANCEL to abort.");
            }
        }
    }

    // ================================================================
    //  HELPER: find RegisteredTaxpayer by TIN (searches both arrays)
    // ================================================================
    static RegisteredTaxpayer findTaxpayerByTin(String tin) {
        for (int i = 0; i < individualCount; i++) {
            if (individuals[i].getTin().equals(tin)) return individuals[i];
        }
        for (int i = 0; i < corporateCount; i++) {
            if (corporates[i].getTin().equals(tin)) return corporates[i];
        }
        return null;
    }

    // HELPER: find TaxType by ID
    static TaxType findTaxTypeById(String id) {
        for (int i = 0; i < taxTypeCount; i++) {
            if (taxTypes[i].getTaxTypeId().equals(id)) return taxTypes[i];
        }
        return null;
    }

    // HELPER: find TaxObligation by ID
    static TaxObligation findObligationById(String id) {
        for (int i = 0; i < obligationCount; i++) {
            if (obligations[i].getObligationId().equals(id)) return obligations[i];
        }
        return null;
    }

    // HELPER: find TaxDeclaration by ID
    static TaxDeclaration findDeclarationById(String id) {
        for (int i = 0; i < declarationCount; i++) {
            if (declarations[i].getDeclarationId().equals(id)) return declarations[i];
        }
        return null;
    }

    // HELPER: find TaxAssessment by ID
    static TaxAssessment findAssessmentById(String id) {
        for (int i = 0; i < assessmentCount; i++) {
            if (assessments[i].getAssessmentId().equals(id)) return assessments[i];
        }
        return null;
    }

    // HELPER: find RevenueOfficer by staff number
    static RevenueOfficer findOfficerByStaffNumber(String staffNo) {
        for (int i = 0; i < officerCount; i++) {
            if (officers[i].getStaffNumber().equals(staffNo)) return officers[i];
        }
        return null;
    }

    // HELPER: check if a TIN already exists
    static boolean tinExists(String tin) {
        return findTaxpayerByTin(tin) != null;
    }

    // HELPER: check if a national ID already exists
    static boolean nationalIdExists(String nid) {
        for (int i = 0; i < individualCount; i++) {
            if (individuals[i].getNationalId().equals(nid)) return true;
        }
        return false;
    }

    // HELPER: check if a company reg number already exists
    static boolean companyRegExists(String reg) {
        for (int i = 0; i < corporateCount; i++) {
            if (corporates[i].getCompanyRegistrationNo().equals(reg)) return true;
        }
        return false;
    }

    // HELPER: check if a staff number already exists
    static boolean staffNumberExists(String staffNo) {
        for (int i = 0; i < officerCount; i++) {
            if (officers[i].getStaffNumber().equals(staffNo)) return true;
        }
        return false;
    }

    // HELPER: check if a tax code already exists
    static boolean taxCodeExists(String code) {
        for (int i = 0; i < taxTypeCount; i++) {
            if (taxTypes[i].getTaxCode().equals(code)) return true;
        }
        return false;
    }

    // HELPER: check if payment reference already exists
    static boolean paymentRefExists(String ref) {
        for (int i = 0; i < paymentCount; i++) {
            if (payments[i].getPaymentReference().equals(ref)) return true;
        }
        return false;
    }

    // HELPER: sum all confirmed payments for an assessment
    static BigDecimal getTotalPayments(TaxAssessment assessment) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < paymentCount; i++) {
            if (payments[i].getAssessment().getAssessmentId().equals(assessment.getAssessmentId())) {
                total = total.add(payments[i].getAmount());
            }
        }
        return total;
    }

    // HELPER: sum all penalties for an assessment
    static BigDecimal getTotalPenalties(TaxAssessment assessment) {
        BigDecimal total = BigDecimal.ZERO;
        String obligationId = assessment.getDeclaration().getObligation().getObligationId();
        for (int i = 0; i < penaltyCount; i++) {
            if (penalties[i].getObligation().getObligationId().equals(obligationId)) {
                total = total.add(penalties[i].getPenaltyAmount());
            }
        }
        return total;
    }

    // HELPER: get outstanding balance for an assessment
    static BigDecimal getOutstandingBalance(TaxAssessment assessment) {
        BigDecimal total = assessment.getTotal().add(getTotalPenalties(assessment));
        BigDecimal paid  = getTotalPayments(assessment);
        return total.subtract(paid);
    }

    // ================================================================
    //  OPTION 1: Register Individual Taxpayer  (4-step form)
    // ================================================================
    static void registerIndividualTaxpayer() {
        System.out.println("\n+------------------ REGISTER INDIVIDUAL TAXPAYER ----------------+");

        // --- STEP 1: Personal details (TaxParty fields) ---
        System.out.println("| STEP 1 OF 4 - PERSONAL DETAILS                                |");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");

        String partyId, firstName, lastName, email, phone;
        LocalDate dob;

        System.out.println("+---------------------------------------------------------------+");
        System.out.print("  01 Party ID        [String, unique, 4-30]      : ");
        partyId = input.nextLine().trim();
        if (partyId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  02 First Name      [String, 2-60]              : ");
        firstName = input.nextLine().trim();
        if (firstName.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  03 Last Name       [String, 2-60]              : ");
        lastName = input.nextLine().trim();
        if (lastName.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  04 Email           [valid email, <=120 chars]  : ");
        email = input.nextLine().trim();
        if (email.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  05 Phone           [10-15 digits, optional +]  : ");
        phone = input.nextLine().trim();
        if (phone.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  06 Date of Birth   [YYYY-MM-DD]                : ");
        String dobStr = input.nextLine().trim();
        if (dobStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        try { dob = LocalDate.parse(dobStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date format. Registration cancelled."); return; }

        // --- STEP 2: Registered taxpayer fields ---
        System.out.println("\n| STEP 2 OF 4 - REGISTRATION DETAILS                            |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  07 TIN             [exactly 9 digits, unique]  : ");
        String tin = input.nextLine().trim();
        if (tin.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  08 Registration Date [YYYY-MM-DD]              : ");
        String regDateStr = input.nextLine().trim();
        if (regDateStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        LocalDate regDate;
        try { regDate = LocalDate.parse(regDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date format. Registration cancelled."); return; }

        System.out.print("  09 Status          [ACTIVE / INACTIVE / SUSPENDED] : ");
        String status = input.nextLine().trim().toUpperCase();
        if (status.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  10 District        [String, 2-60]              : ");
        String district = input.nextLine().trim();
        if (district.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  11 Sector          [String, 2-60]              : ");
        String sector = input.nextLine().trim();
        if (sector.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  12 Tax Office      [String, 2-60]              : ");
        String taxOffice = input.nextLine().trim();
        if (taxOffice.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        // --- STEP 3: Individual-specific fields ---
        System.out.println("\n| STEP 3 OF 4 - INDIVIDUAL DETAILS                              |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  13 National ID     [exactly 16 digits, unique] : ");
        String nationalId = input.nextLine().trim();
        if (nationalId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  14 Occupation      [String, 2-60]              : ");
        String occupation = input.nextLine().trim();
        if (occupation.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  15 Business Name   [String, 2-60]              : ");
        String businessName = input.nextLine().trim();
        if (businessName.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  16 Business Start Date [YYYY-MM-DD]            : ");
        String bsdStr = input.nextLine().trim();
        if (bsdStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        LocalDate businessStartDate;
        try { businessStartDate = LocalDate.parse(bsdStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date format. Registration cancelled."); return; }

        System.out.print("  17 Annual Turnover [BigDecimal, >= 0]          : ");
        String turnoverStr = input.nextLine().trim();
        if (turnoverStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        BigDecimal annualTurnover;
        try { annualTurnover = new BigDecimal(turnoverStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Registration cancelled."); return; }

        System.out.print("  18 Resident        [YES / NO]                  : ");
        String residentStr = input.nextLine().trim();
        if (residentStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        boolean resident = residentStr.equalsIgnoreCase("YES");

        // --- STEP 4: Review and Save ---
        System.out.println("\n| STEP 4 OF 4 - REVIEW ALL 18 FIELDS                            |");
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  01 Party ID            : " + partyId);
        System.out.println("  02 First Name          : " + firstName);
        System.out.println("  03 Last Name           : " + lastName);
        System.out.println("  04 Email               : " + email);
        System.out.println("  05 Phone               : " + phone);
        System.out.println("  06 Date of Birth       : " + dobStr);
        System.out.println("  07 TIN                 : " + tin);
        System.out.println("  08 Registration Date   : " + regDateStr);
        System.out.println("  09 Status              : " + status);
        System.out.println("  10 District            : " + district);
        System.out.println("  11 Sector              : " + sector);
        System.out.println("  12 Tax Office          : " + taxOffice);
        System.out.println("  13 National ID         : " + nationalId);
        System.out.println("  14 Occupation          : " + occupation);
        System.out.println("  15 Business Name       : " + businessName);
        System.out.println("  16 Business Start Date : " + bsdStr);
        System.out.println("  17 Annual Turnover     : " + turnoverStr);
        System.out.println("  18 Resident            : " + (resident ? "YES" : "NO"));
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Registration cancelled. No record saved."); return; }

        // Build object and validate
        try {
            if (individualCount >= individuals.length) {
                System.out.println("  Storage full. Cannot register more individual taxpayers.");
                return;
            }
            if (tinExists(tin)) {
                throw new DuplicateRecordException("TIN " + tin + " already exists in the system.");
            }
            if (nationalIdExists(nationalId)) {
                throw new DuplicateRecordException("National ID " + nationalId + " already exists.");
            }

            IndividualTaxpayer taxpayer = new IndividualTaxpayer(
                partyId, firstName, lastName, email, phone, dob,
                tin, regDate, status, district, sector, taxOffice,
                nationalId, occupation, businessName, businessStartDate, annualTurnover, resident
            );
            taxpayer.validate();
            individuals[individualCount++] = taxpayer;
            System.out.println("  Individual taxpayer registered successfully! TIN: " + tin);

        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 2: Register Corporate Taxpayer  (4-step form)
    // ================================================================
    static void registerCorporateTaxpayer() {
        System.out.println("\n+------------------ REGISTER CORPORATE TAXPAYER ----------------+");

        System.out.println("| STEP 1 OF 4 - CONTACT PERSON DETAILS                          |");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  01 Party ID        [String, unique, 4-30]      : ");
        String partyId = input.nextLine().trim();
        if (partyId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  02 First Name      [String, 2-60]              : ");
        String firstName = input.nextLine().trim();
        if (firstName.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  03 Last Name       [String, 2-60]              : ");
        String lastName = input.nextLine().trim();
        if (lastName.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  04 Email           [valid email, <=120 chars]  : ");
        String email = input.nextLine().trim();
        if (email.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  05 Phone           [10-15 digits, optional +]  : ");
        String phone = input.nextLine().trim();
        if (phone.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  06 Date of Birth   [YYYY-MM-DD]                : ");
        String dobStr = input.nextLine().trim();
        if (dobStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        LocalDate dob;
        try { dob = LocalDate.parse(dobStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.println("\n| STEP 2 OF 4 - REGISTRATION DETAILS                            |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  07 TIN             [exactly 9 digits, unique]  : ");
        String tin = input.nextLine().trim();
        if (tin.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  08 Registration Date [YYYY-MM-DD]              : ");
        String regDateStr = input.nextLine().trim();
        if (regDateStr.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }
        LocalDate regDate;
        try { regDate = LocalDate.parse(regDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  09 Status          [ACTIVE / INACTIVE / SUSPENDED] : ");
        String status = input.nextLine().trim().toUpperCase();

        System.out.print("  10 District        [String, 2-60]              : ");
        String district = input.nextLine().trim();

        System.out.print("  11 Sector          [String, 2-60]              : ");
        String sector = input.nextLine().trim();

        System.out.print("  12 Tax Office      [String, 2-60]              : ");
        String taxOffice = input.nextLine().trim();

        System.out.println("\n| STEP 3 OF 4 - COMPANY DETAILS                                 |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  13 Company Reg No  [5-30 UPPERCASE letters/digits/hyphens] : ");
        String companyRegNo = input.nextLine().trim();
        if (companyRegNo.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  14 Legal Name      [String, 3-120]             : ");
        String legalName = input.nextLine().trim();

        System.out.print("  15 Incorporation Date [YYYY-MM-DD]             : ");
        String incDateStr = input.nextLine().trim();
        LocalDate incorporationDate;
        try { incorporationDate = LocalDate.parse(incDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  16 Business Sector [String, 2-60]              : ");
        String businessSector = input.nextLine().trim();

        System.out.print("  17 Authorized Rep  [String, 2-60]              : ");
        String authorizedRep = input.nextLine().trim();

        System.out.print("  18 Annual Turnover [BigDecimal, >= 0]          : ");
        String turnoverStr = input.nextLine().trim();
        BigDecimal annualTurnover;
        try { annualTurnover = new BigDecimal(turnoverStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        System.out.println("\n| STEP 4 OF 4 - REVIEW ALL 18 FIELDS                            |");
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  01 Party ID            : " + partyId);
        System.out.println("  02 First Name          : " + firstName);
        System.out.println("  03 Last Name           : " + lastName);
        System.out.println("  04 Email               : " + email);
        System.out.println("  05 Phone               : " + phone);
        System.out.println("  06 Date of Birth       : " + dobStr);
        System.out.println("  07 TIN                 : " + tin);
        System.out.println("  08 Registration Date   : " + regDateStr);
        System.out.println("  09 Status              : " + status);
        System.out.println("  10 District            : " + district);
        System.out.println("  11 Sector              : " + sector);
        System.out.println("  12 Tax Office          : " + taxOffice);
        System.out.println("  13 Company Reg No      : " + companyRegNo);
        System.out.println("  14 Legal Name          : " + legalName);
        System.out.println("  15 Incorporation Date  : " + incDateStr);
        System.out.println("  16 Business Sector     : " + businessSector);
        System.out.println("  17 Authorized Rep      : " + authorizedRep);
        System.out.println("  18 Annual Turnover     : " + turnoverStr);
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Registration cancelled. No record saved."); return; }

        try {
            if (corporateCount >= corporates.length) {
                System.out.println("  Storage full. Cannot register more corporate taxpayers.");
                return;
            }
            if (tinExists(tin)) {
                throw new DuplicateRecordException("TIN " + tin + " already exists.");
            }
            if (companyRegExists(companyRegNo)) {
                throw new DuplicateRecordException("Company registration number " + companyRegNo + " already exists.");
            }

            CorporateTaxpayer taxpayer = new CorporateTaxpayer(
                partyId, firstName, lastName, email, phone, dob,
                tin, regDate, status, district, sector, taxOffice,
                companyRegNo, legalName, incorporationDate, businessSector, authorizedRep, annualTurnover
            );
            taxpayer.validate();
            corporates[corporateCount++] = taxpayer;
            System.out.println("  Corporate taxpayer registered successfully! TIN: " + tin);

        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 3: Register Revenue Officer  (4-step form)
    // ================================================================
    static void registerRevenueOfficer() {
        System.out.println("\n+------------------ REGISTER REVENUE OFFICER -------------------+");
        System.out.println("| STEP 1 OF 4 - PERSONAL DETAILS                                |");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  01 Party ID        [String, unique, 4-30]      : ");
        String partyId = input.nextLine().trim();
        if (partyId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  02 First Name      [String, 2-60]              : ");
        String firstName = input.nextLine().trim();

        System.out.print("  03 Last Name       [String, 2-60]              : ");
        String lastName = input.nextLine().trim();

        System.out.print("  04 Email           [valid email, <=120 chars]  : ");
        String email = input.nextLine().trim();

        System.out.print("  05 Phone           [10-15 digits, optional +]  : ");
        String phone = input.nextLine().trim();

        System.out.print("  06 Date of Birth   [YYYY-MM-DD]                : ");
        String dobStr = input.nextLine().trim();
        LocalDate dob;
        try { dob = LocalDate.parse(dobStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.println("\n| STEP 2 OF 4 - EMPLOYMENT DETAILS                              |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  07 Staff Number    [String, 2-30, unique]      : ");
        String staffNumber = input.nextLine().trim();
        if (staffNumber.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  08 Department      [String, 2-60]              : ");
        String department = input.nextLine().trim();

        System.out.print("  09 Role            [String, 2-60]              : ");
        String role = input.nextLine().trim();

        System.out.print("  10 Hire Date       [YYYY-MM-DD]                : ");
        String hireDateStr = input.nextLine().trim();
        LocalDate hireDate;
        try { hireDate = LocalDate.parse(hireDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  11 Approval Limit  [BigDecimal, >= 0]          : ");
        String limitStr = input.nextLine().trim();
        BigDecimal approvalLimit;
        try { approvalLimit = new BigDecimal(limitStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        System.out.print("  12 Active          [YES / NO]                  : ");
        String activeStr = input.nextLine().trim();
        boolean active = activeStr.equalsIgnoreCase("YES");

        System.out.println("\n| STEP 3 OF 4 - (No additional fields for officer)              |");
        System.out.println("| STEP 4 OF 4 - REVIEW ALL 12 FIELDS                            |");
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  01 Party ID        : " + partyId);
        System.out.println("  02 First Name      : " + firstName);
        System.out.println("  03 Last Name       : " + lastName);
        System.out.println("  04 Email           : " + email);
        System.out.println("  05 Phone           : " + phone);
        System.out.println("  06 Date of Birth   : " + dobStr);
        System.out.println("  07 Staff Number    : " + staffNumber);
        System.out.println("  08 Department      : " + department);
        System.out.println("  09 Role            : " + role);
        System.out.println("  10 Hire Date       : " + hireDateStr);
        System.out.println("  11 Approval Limit  : " + limitStr);
        System.out.println("  12 Active          : " + (active ? "YES" : "NO"));
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Registration cancelled. No record saved."); return; }

        try {
            if (officerCount >= officers.length) {
                System.out.println("  Storage full. Cannot register more officers.");
                return;
            }
            if (staffNumberExists(staffNumber)) {
                throw new DuplicateRecordException("Staff number " + staffNumber + " already exists.");
            }

            RevenueOfficer officer = new RevenueOfficer(
                partyId, firstName, lastName, email, phone, dob,
                staffNumber, department, role, hireDate, approvalLimit, active
            );
            officer.validate();
            officers[officerCount++] = officer;
            System.out.println("  Revenue officer registered successfully! Staff#: " + staffNumber);

        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 4: Register Tax Type  (4-step form)
    // ================================================================
    static void registerTaxType() {
        System.out.println("\n+------------------ REGISTER TAX TYPE --------------------------+");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  01 Tax Type ID     [String, 2-30]              : ");
        String taxTypeId = input.nextLine().trim();
        if (taxTypeId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  02 Tax Code        [String, 2-20, unique]      : ");
        String taxCode = input.nextLine().trim();
        if (taxCode.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  03 Tax Name        [String, 2-60]              : ");
        String taxName = input.nextLine().trim();

        System.out.print("  04 Rate            [BigDecimal, 0-100]         : ");
        String rateStr = input.nextLine().trim();
        BigDecimal rate;
        try { rate = new BigDecimal(rateStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        System.out.print("  05 Filing Frequency [MONTHLY / QUARTERLY / ANNUAL] : ");
        String filingFrequency = input.nextLine().trim().toUpperCase();

        System.out.print("  06 Active          [YES / NO]                  : ");
        String activeStr = input.nextLine().trim();
        boolean active = activeStr.equalsIgnoreCase("YES");

        System.out.println("\n| REVIEW                                                         |");
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  01 Tax Type ID       : " + taxTypeId);
        System.out.println("  02 Tax Code          : " + taxCode);
        System.out.println("  03 Tax Name          : " + taxName);
        System.out.println("  04 Rate              : " + rateStr + "%");
        System.out.println("  05 Filing Frequency  : " + filingFrequency);
        System.out.println("  06 Active            : " + (active ? "YES" : "NO"));
        System.out.println("+---------------------------------------------------------------+");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (taxTypeCount >= taxTypes.length) {
                System.out.println("  Storage full. Cannot register more tax types.");
                return;
            }
            if (taxCodeExists(taxCode)) {
                throw new DuplicateRecordException("Tax code " + taxCode + " already exists.");
            }

            TaxType taxType = new TaxType(taxTypeId, taxCode, taxName, rate, filingFrequency, active);
            taxType.validate();
            taxTypes[taxTypeCount++] = taxType;
            System.out.println("  Tax type registered successfully! Code: " + taxCode);

        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 5: Create Tax Obligation
    // ================================================================
    static void createTaxObligation() {
        System.out.println("\n+------------------ CREATE TAX OBLIGATION -----------------------+");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  Obligation ID     [String, 2-30, unique]      : ");
        String obligationId = input.nextLine().trim();
        if (obligationId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        // Check if obligation ID is unique
        if (findObligationById(obligationId) != null) {
            System.out.println("  ERROR: Obligation ID " + obligationId + " already exists.");
            return;
        }

        System.out.print("  Taxpayer TIN      [9 digits]                   : ");
        String tin = input.nextLine().trim();
        RegisteredTaxpayer taxpayer = findTaxpayerByTin(tin);
        if (taxpayer == null) {
            System.out.println("  ERROR: No taxpayer found with TIN " + tin);
            return;
        }
        if (!taxpayer.isActive()) {
            System.out.println("  ERROR: Taxpayer with TIN " + tin + " is not ACTIVE.");
            return;
        }

        System.out.print("  Tax Type ID       [enter ID]                   : ");
        String taxTypeId = input.nextLine().trim();
        TaxType taxType = findTaxTypeById(taxTypeId);
        if (taxType == null) {
            System.out.println("  ERROR: No tax type found with ID " + taxTypeId);
            return;
        }
        if (!taxType.isActive()) {
            System.out.println("  ERROR: Tax type " + taxTypeId + " is not active.");
            return;
        }

        System.out.print("  Period Start      [YYYY-MM-DD]                 : ");
        String psStr = input.nextLine().trim();
        LocalDate periodStart;
        try { periodStart = LocalDate.parse(psStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  Period End        [YYYY-MM-DD]                 : ");
        String peStr = input.nextLine().trim();
        LocalDate periodEnd;
        try { periodEnd = LocalDate.parse(peStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  Due Date          [YYYY-MM-DD]                 : ");
        String ddStr = input.nextLine().trim();
        LocalDate dueDate;
        try { dueDate = LocalDate.parse(ddStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        // Check for overlapping obligations for same taxpayer + tax type
        for (int i = 0; i < obligationCount; i++) {
            TaxObligation existing = obligations[i];
            if (existing.getTaxpayer().getTin().equals(tin)
                    && existing.getTaxType().getTaxTypeId().equals(taxTypeId)) {
                // overlapping if periods intersect
                boolean overlaps = !periodStart.isAfter(existing.getPeriodEnd())
                                && !periodEnd.isBefore(existing.getPeriodStart());
                if (overlaps) {
                    System.out.println("  ERROR: Overlapping obligation already exists for this taxpayer and tax type.");
                    return;
                }
            }
        }

        System.out.println("\n  REVIEW:");
        System.out.println("  Obligation ID  : " + obligationId);
        System.out.println("  Taxpayer       : " + taxpayer.getDisplayName() + " | TIN: " + tin);
        System.out.println("  Tax Type       : " + taxType.getTaxName());
        System.out.println("  Period         : " + psStr + " to " + peStr);
        System.out.println("  Due Date       : " + ddStr);
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (obligationCount >= obligations.length) {
                System.out.println("  Storage full.");
                return;
            }
            TaxObligation obligation = new TaxObligation(obligationId, taxpayer, taxType, periodStart, periodEnd, dueDate);
            obligation.validate();
            obligations[obligationCount++] = obligation;
            System.out.println("  Tax obligation created successfully! ID: " + obligationId);

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 6: File Tax Declaration
    // ================================================================
    static void fileTaxDeclaration() {
        System.out.println("\n+------------------ FILE TAX DECLARATION -----------------------+");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  Declaration ID    [String, 2-30, unique]      : ");
        String declarationId = input.nextLine().trim();
        if (declarationId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        if (findDeclarationById(declarationId) != null) {
            System.out.println("  ERROR: Declaration ID " + declarationId + " already exists.");
            return;
        }

        System.out.print("  Obligation ID     [enter ID]                   : ");
        String obligationId = input.nextLine().trim();
        TaxObligation obligation = findObligationById(obligationId);
        if (obligation == null) {
            System.out.println("  ERROR: No obligation found with ID " + obligationId);
            return;
        }

        // Check: only one active declaration per obligation
        for (int i = 0; i < declarationCount; i++) {
            if (declarations[i].getObligation().getObligationId().equals(obligationId)
                    && declarations[i].isActive()) {
                System.out.println("  ERROR: An active declaration already exists for obligation " + obligationId);
                return;
            }
        }

        System.out.print("  Filing Date       [YYYY-MM-DD]                 : ");
        String filingDateStr = input.nextLine().trim();
        LocalDate filingDate;
        try { filingDate = LocalDate.parse(filingDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        if (filingDate.isAfter(LocalDate.now())) {
            System.out.println("  ERROR: Filing date cannot be in the future.");
            return;
        }

        System.out.print("  Taxable Amount    [BigDecimal, >= 0]           : ");
        String taxableStr = input.nextLine().trim();
        BigDecimal taxableAmount;
        try { taxableAmount = new BigDecimal(taxableStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        // Show expected tax to help the user
        BigDecimal expectedTax = taxableAmount.multiply(obligation.getTaxType().getRate())
                                              .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        System.out.println("  Expected Tax (auto-calculated) : " + expectedTax
                + "  [taxableAmount x " + obligation.getTaxType().getRate() + "% / 100]");

        System.out.print("  Declared Tax      [BigDecimal, must match expected within 0.01] : ");
        String declaredTaxStr = input.nextLine().trim();
        BigDecimal declaredTax;
        try { declaredTax = new BigDecimal(declaredTaxStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        // Determine status: LATE if filing after due date, else PENDING
        boolean isLate = filingDate.isAfter(obligation.getDueDate());
        String status = isLate ? "LATE" : "PENDING";
        if (isLate) {
            System.out.println("  WARNING: Filing date is after due date (" + obligation.getDueDate()
                    + "). Declaration will be marked LATE.");
        }

        System.out.println("\n  REVIEW:");
        System.out.println("  Declaration ID   : " + declarationId);
        System.out.println("  Obligation ID    : " + obligationId);
        System.out.println("  Filing Date      : " + filingDateStr);
        System.out.println("  Taxable Amount   : " + taxableStr);
        System.out.println("  Declared Tax     : " + declaredTaxStr);
        System.out.println("  Expected Tax     : " + expectedTax);
        System.out.println("  Status           : " + status);
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (declarationCount >= declarations.length) {
                System.out.println("  Storage full.");
                return;
            }
            TaxDeclaration declaration = new TaxDeclaration(declarationId, obligation, filingDate,
                    taxableAmount, declaredTax, status);
            declaration.validate();
            declarations[declarationCount++] = declaration;
            System.out.println("  Tax declaration filed successfully! ID: " + declarationId
                    + (isLate ? " [LATE]" : ""));

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 7: Assess Declaration
    // ================================================================
    static void assessDeclaration() {
        System.out.println("\n+------------------ ASSESS DECLARATION -------------------------+");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  Assessment ID     [String, 2-30, unique]      : ");
        String assessmentId = input.nextLine().trim();
        if (assessmentId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        if (findAssessmentById(assessmentId) != null) {
            System.out.println("  ERROR: Assessment ID " + assessmentId + " already exists.");
            return;
        }

        System.out.print("  Declaration ID    [enter ID]                   : ");
        String declarationId = input.nextLine().trim();
        TaxDeclaration declaration = findDeclarationById(declarationId);
        if (declaration == null) {
            System.out.println("  ERROR: No declaration found with ID " + declarationId);
            return;
        }
        if ("REJECTED".equalsIgnoreCase(declaration.getStatus())) {
            System.out.println("  ERROR: A rejected declaration cannot be assessed.");
            return;
        }

        BigDecimal assessedTax = declaration.getDeclaredTax();
        System.out.println("  Assessed Tax (from declaration)  : " + assessedTax);

        System.out.print("  Adjustment Amount [BigDecimal, can be negative if credit] : ");
        String adjustStr = input.nextLine().trim();
        BigDecimal adjustmentAmount;
        try { adjustmentAmount = new BigDecimal(adjustStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        BigDecimal total = assessedTax.add(adjustmentAmount);
        System.out.println("  Total (assessedTax + adjustment) : " + total);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("  ERROR: Assessment total cannot be negative.");
            return;
        }

        System.out.print("  Assessment Date   [YYYY-MM-DD]                 : ");
        String assessDateStr = input.nextLine().trim();
        LocalDate assessmentDate;
        try { assessmentDate = LocalDate.parse(assessDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.println("\n  REVIEW:");
        System.out.println("  Assessment ID      : " + assessmentId);
        System.out.println("  Declaration ID     : " + declarationId);
        System.out.println("  Assessed Tax       : " + assessedTax);
        System.out.println("  Adjustment Amount  : " + adjustStr);
        System.out.println("  Total              : " + total);
        System.out.println("  Assessment Date    : " + assessDateStr);
        System.out.println("  Initial Status     : DRAFT");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (assessmentCount >= assessments.length) {
                System.out.println("  Storage full.");
                return;
            }
            TaxAssessment assessment = new TaxAssessment(assessmentId, declaration, assessedTax,
                    adjustmentAmount, assessmentDate, "DRAFT");
            assessment.validate();
            assessments[assessmentCount++] = assessment;
            System.out.println("  Assessment created successfully! ID: " + assessmentId + " | Status: DRAFT");

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 8: Confirm Assessment
    // ================================================================
    static void confirmAssessment() {
        System.out.println("\n+------------------ CONFIRM ASSESSMENT -------------------------+");

        System.out.print("  Assessment ID to confirm: ");
        String assessmentId = input.nextLine().trim();
        TaxAssessment assessment = findAssessmentById(assessmentId);

        if (assessment == null) {
            System.out.println("  ERROR: No assessment found with ID " + assessmentId);
            return;
        }
        if ("CONFIRMED".equalsIgnoreCase(assessment.getStatus())) {
            System.out.println("  Assessment is already CONFIRMED.");
            return;
        }
        if ("CANCELLED".equalsIgnoreCase(assessment.getStatus())) {
            System.out.println("  ERROR: Cannot confirm a CANCELLED assessment.");
            return;
        }

        System.out.println("  Assessment: " + assessment.getSummary());
        System.out.println("  Current Status: " + assessment.getStatus());
        System.out.println("  Confirm this assessment? [YES / NO]");
        System.out.print("  Choice: ");
        String confirm = input.nextLine().trim();
        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("  Confirmation cancelled.");
            return;
        }

        try {
            assessment.setStatus("CONFIRMED");
            System.out.println("  Assessment " + assessmentId + " is now CONFIRMED.");
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 9: View All Records
    // ================================================================
    static void viewAllRecords() {
        System.out.println("\n================ ALL RECORDS ================");

        System.out.println("\n--- Individual Taxpayers (" + individualCount + ") ---");
        for (int i = 0; i < individualCount; i++) {
            System.out.println("  " + (i+1) + ". " + individuals[i].getSummary());
        }

        System.out.println("\n--- Corporate Taxpayers (" + corporateCount + ") ---");
        for (int i = 0; i < corporateCount; i++) {
            System.out.println("  " + (i+1) + ". " + corporates[i].getSummary());
        }

        System.out.println("\n--- Revenue Officers (" + officerCount + ") ---");
        for (int i = 0; i < officerCount; i++) {
            System.out.println("  " + (i+1) + ". " + officers[i].getSummary());
        }

        System.out.println("\n--- Tax Types (" + taxTypeCount + ") ---");
        for (int i = 0; i < taxTypeCount; i++) {
            System.out.println("  " + (i+1) + ". " + taxTypes[i].getSummary());
        }

        System.out.println("\n--- Tax Obligations (" + obligationCount + ") ---");
        for (int i = 0; i < obligationCount; i++) {
            System.out.println("  " + (i+1) + ". " + obligations[i].getSummary());
        }

        System.out.println("\n--- Tax Declarations (" + declarationCount + ") ---");
        for (int i = 0; i < declarationCount; i++) {
            System.out.println("  " + (i+1) + ". " + declarations[i].getSummary());
        }

        System.out.println("\n--- Tax Assessments (" + assessmentCount + ") ---");
        for (int i = 0; i < assessmentCount; i++) {
            System.out.println("  " + (i+1) + ". " + assessments[i].getSummary());
        }

        System.out.println("\n--- Tax Payments (" + paymentCount + ") ---");
        for (int i = 0; i < paymentCount; i++) {
            System.out.println("  " + (i+1) + ". " + payments[i].getSummary());
        }

        System.out.println("\n--- Penalties (" + penaltyCount + ") ---");
        for (int i = 0; i < penaltyCount; i++) {
            System.out.println("  " + (i+1) + ". " + penalties[i].getSummary());
        }

        System.out.println("\n--- Tax Refunds (" + refundCount + ") ---");
        for (int i = 0; i < refundCount; i++) {
            System.out.println("  " + (i+1) + ". " + refunds[i].getSummary());
        }

        System.out.println("\n--- Tax Clearances (" + clearanceCount + ") ---");
        for (int i = 0; i < clearanceCount; i++) {
            System.out.println("  " + (i+1) + ". " + clearances[i].getSummary());
        }
        System.out.println("=============================================");
    }

    // ================================================================
    //  OPTION 10: View Declaration History
    // ================================================================
    static void viewDeclarationHistory() {
        System.out.println("\n+------------------ DECLARATION HISTORY ------------------------+");
        System.out.print("  Enter Taxpayer TIN (or press ENTER to view all): ");
        String tin = input.nextLine().trim();

        int found = 0;
        for (int i = 0; i < declarationCount; i++) {
            String decTin = declarations[i].getObligation().getTaxpayer().getTin();
            if (tin.isEmpty() || decTin.equals(tin)) {
                declarations[i].printDetails();
                found++;
            }
        }
        if (found == 0) {
            System.out.println("  No declarations found" + (tin.isEmpty() ? "." : " for TIN: " + tin));
        } else {
            System.out.println("  Total: " + found + " declaration(s).");
        }
    }

    // ================================================================
    //  OPTION 11: View Assessment History
    // ================================================================
    static void viewAssessmentHistory() {
        System.out.println("\n+------------------ ASSESSMENT HISTORY -------------------------+");
        System.out.print("  Enter Taxpayer TIN (or press ENTER to view all): ");
        String tin = input.nextLine().trim();

        int found = 0;
        for (int i = 0; i < assessmentCount; i++) {
            String assTin = assessments[i].getDeclaration().getObligation().getTaxpayer().getTin();
            if (tin.isEmpty() || assTin.equals(tin)) {
                assessments[i].printDetails();
                found++;
            }
        }
        if (found == 0) {
            System.out.println("  No assessments found" + (tin.isEmpty() ? "." : " for TIN: " + tin));
        } else {
            System.out.println("  Total: " + found + " assessment(s).");
        }
    }

    // ================================================================
    //  OPTION 12: Record Tax Payment
    // ================================================================
    static void recordTaxPayment() {
        System.out.println("\n+------------------ RECORD TAX PAYMENT -------------------------+");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  Payment ID        [String, 2-30, unique]      : ");
        String paymentId = input.nextLine().trim();
        if (paymentId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  Assessment ID     [enter ID]                   : ");
        String assessmentId = input.nextLine().trim();
        TaxAssessment assessment = findAssessmentById(assessmentId);
        if (assessment == null) {
            System.out.println("  ERROR: No assessment found with ID " + assessmentId);
            return;
        }
        if (!"CONFIRMED".equalsIgnoreCase(assessment.getStatus())) {
            System.out.println("  ERROR: Payments can only be made against a CONFIRMED assessment.");
            return;
        }

        BigDecimal outstanding = getOutstandingBalance(assessment);
        System.out.println("  Outstanding Balance: " + outstanding);
        if (outstanding.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("  This assessment is already fully paid.");
            return;
        }

        System.out.print("  Payment Reference [String, 2-30, globally unique] : ");
        String paymentRef = input.nextLine().trim();
        if (paymentRef.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        if (paymentRefExists(paymentRef)) {
            System.out.println("  ERROR: Payment reference " + paymentRef + " already exists.");
            return;
        }

        System.out.print("  Amount            [BigDecimal, > 0, <= " + outstanding + "] : ");
        String amountStr = input.nextLine().trim();
        BigDecimal amount;
        try { amount = new BigDecimal(amountStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("  ERROR: Payment amount must be greater than zero.");
            return;
        }
        if (amount.compareTo(outstanding) > 0) {
            System.out.println("  ERROR: Payment amount exceeds outstanding balance of " + outstanding);
            return;
        }

        System.out.print("  Payment Date      [YYYY-MM-DD]                 : ");
        String payDateStr = input.nextLine().trim();
        LocalDate paymentDate;
        try { paymentDate = LocalDate.parse(payDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  Payment Method    [e.g. BANK_TRANSFER, MOBILE_MONEY, CASH] : ");
        String paymentMethod = input.nextLine().trim();

        System.out.println("\n  REVIEW:");
        System.out.println("  Payment ID         : " + paymentId);
        System.out.println("  Assessment ID      : " + assessmentId);
        System.out.println("  Payment Reference  : " + paymentRef);
        System.out.println("  Amount             : " + amountStr);
        System.out.println("  Payment Date       : " + payDateStr);
        System.out.println("  Payment Method     : " + paymentMethod);
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (paymentCount >= payments.length) {
                System.out.println("  Storage full.");
                return;
            }
            TaxPayment payment = new TaxPayment(paymentId, assessment, paymentRef, amount, paymentDate, paymentMethod);
            payment.validate();
            payments[paymentCount++] = payment;
            System.out.println("  Payment recorded successfully! ID: " + paymentId);
            BigDecimal newBalance = outstanding.subtract(amount);
            System.out.println("  Remaining balance: " + newBalance);

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 13: Calculate / Record Penalty
    // ================================================================
    static void calculatePenalty() {
        System.out.println("\n+------------------ CALCULATE / RECORD PENALTY -----------------+");
        System.out.println("| (Type CANCEL at any prompt to abort)                          |");
        System.out.println("+---------------------------------------------------------------+");

        System.out.print("  Penalty ID        [String, 2-30, unique]      : ");
        String penaltyId = input.nextLine().trim();
        if (penaltyId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        System.out.print("  Obligation ID     [enter ID]                   : ");
        String obligationId = input.nextLine().trim();
        TaxObligation obligation = findObligationById(obligationId);
        if (obligation == null) {
            System.out.println("  ERROR: No obligation found with ID " + obligationId);
            return;
        }

        // Check penalty uniqueness by obligation + penaltyType
        System.out.print("  Penalty Type      [e.g. LATE_FILING]           : ");
        String penaltyType = input.nextLine().trim().toUpperCase();

        for (int i = 0; i < penaltyCount; i++) {
            if (penalties[i].getObligation().getObligationId().equals(obligationId)
                    && penalties[i].getPenaltyType().equalsIgnoreCase(penaltyType)) {
                System.out.println("  ERROR: A " + penaltyType + " penalty already exists for obligation " + obligationId);
                return;
            }
        }

        System.out.print("  Base Amount       [BigDecimal, >= 0]           : ");
        String baseStr = input.nextLine().trim();
        BigDecimal baseAmount;
        try { baseAmount = new BigDecimal(baseStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        // Use illustrative 5% rate for late filing
        BigDecimal penaltyRate = new BigDecimal("5.00");
        System.out.println("  Penalty Rate      : " + penaltyRate + "% (illustrative rate)");

        BigDecimal penaltyAmount = Penalty.calculatePenalty(baseAmount, penaltyRate);
        System.out.println("  Calculated Penalty Amount : " + penaltyAmount
                + "  [" + baseStr + " x " + penaltyRate + "% / 100]");

        System.out.println("\n  REVIEW:");
        System.out.println("  Penalty ID      : " + penaltyId);
        System.out.println("  Obligation ID   : " + obligationId);
        System.out.println("  Penalty Type    : " + penaltyType);
        System.out.println("  Base Amount     : " + baseStr);
        System.out.println("  Penalty Rate    : " + penaltyRate + "%");
        System.out.println("  Penalty Amount  : " + penaltyAmount);
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (penaltyCount >= penalties.length) {
                System.out.println("  Storage full.");
                return;
            }
            Penalty penalty = new Penalty(penaltyId, obligation, penaltyType, baseAmount, penaltyRate, penaltyAmount);
            penalty.validate();
            penalties[penaltyCount++] = penalty;
            System.out.println("  Penalty recorded successfully! ID: " + penaltyId + " | Amount: " + penaltyAmount);

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 14: Request / Approve Refund
    // ================================================================
    static void requestApproveRefund() {
        System.out.println("\n+------------------ REQUEST / APPROVE REFUND -------------------+");
        System.out.println("  [1] Request a Refund   [2] Approve a Refund   [0] Back");
        System.out.print("  Choice: ");
        String choice = input.nextLine().trim();

        if (choice.equals("1")) {
            requestRefund();
        } else if (choice.equals("2")) {
            approveRefund();
        } else {
            System.out.println("  Returning to main menu.");
        }
    }

    static void requestRefund() {
        System.out.println("\n  -- REQUEST REFUND --");
        System.out.println("  (Type CANCEL to abort)");

        System.out.print("  Refund ID         [String, 2-30, unique]      : ");
        String refundId = input.nextLine().trim();
        if (refundId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        // Check uniqueness
        for (int i = 0; i < refundCount; i++) {
            if (refunds[i].getRefundId().equals(refundId)) {
                System.out.println("  ERROR: Refund ID " + refundId + " already exists.");
                return;
            }
        }

        System.out.print("  Taxpayer TIN      [9 digits]                   : ");
        String tin = input.nextLine().trim();
        RegisteredTaxpayer taxpayer = findTaxpayerByTin(tin);
        if (taxpayer == null) {
            System.out.println("  ERROR: No taxpayer found with TIN " + tin);
            return;
        }

        System.out.print("  Assessment ID     [enter ID]                   : ");
        String assessmentId = input.nextLine().trim();
        TaxAssessment assessment = findAssessmentById(assessmentId);
        if (assessment == null) {
            System.out.println("  ERROR: No assessment found with ID " + assessmentId);
            return;
        }

        // Check no duplicate pending refund for same assessment
        for (int i = 0; i < refundCount; i++) {
            if (refunds[i].getAssessment().getAssessmentId().equals(assessmentId)
                    && "PENDING".equalsIgnoreCase(refunds[i].getStatus())) {
                System.out.println("  ERROR: A pending refund already exists for assessment " + assessmentId);
                return;
            }
        }

        // Calculate overpayment
        BigDecimal totalPaid = getTotalPayments(assessment);
        BigDecimal assessmentTotal = assessment.getTotal().add(getTotalPenalties(assessment));
        BigDecimal overpayment = totalPaid.subtract(assessmentTotal);

        if (overpayment.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("  ERROR: No overpayment detected. Paid=" + totalPaid
                    + " | Due=" + assessmentTotal + ". Refund not allowed.");
            return;
        }
        System.out.println("  Overpayment detected: " + overpayment);

        System.out.print("  Requested Amount  [BigDecimal, > 0, <= " + overpayment + "] : ");
        String amountStr = input.nextLine().trim();
        BigDecimal requestedAmount;
        try { requestedAmount = new BigDecimal(amountStr); }
        catch (NumberFormatException e) { System.out.println("  Invalid number. Cancelled."); return; }

        if (requestedAmount.compareTo(overpayment) > 0) {
            System.out.println("  ERROR: Requested amount exceeds overpayment of " + overpayment);
            return;
        }

        System.out.print("  Request Date      [YYYY-MM-DD]                 : ");
        String reqDateStr = input.nextLine().trim();
        LocalDate requestDate;
        try { requestDate = LocalDate.parse(reqDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.println("\n  REVIEW:");
        System.out.println("  Refund ID         : " + refundId);
        System.out.println("  Taxpayer TIN      : " + tin);
        System.out.println("  Assessment ID     : " + assessmentId);
        System.out.println("  Requested Amount  : " + amountStr);
        System.out.println("  Request Date      : " + reqDateStr);
        System.out.println("  Status            : PENDING");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (refundCount >= refunds.length) {
                System.out.println("  Storage full.");
                return;
            }
            TaxRefund refund = new TaxRefund(refundId, taxpayer, assessment, requestedAmount, requestDate, "PENDING");
            refund.validate();
            refunds[refundCount++] = refund;
            System.out.println("  Refund request submitted successfully! ID: " + refundId);

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    static void approveRefund() {
        System.out.println("\n  -- APPROVE REFUND --");

        System.out.print("  Refund ID to approve: ");
        String refundId = input.nextLine().trim();
        TaxRefund refund = null;
        for (int i = 0; i < refundCount; i++) {
            if (refunds[i].getRefundId().equals(refundId)) {
                refund = refunds[i];
                break;
            }
        }
        if (refund == null) {
            System.out.println("  ERROR: No refund found with ID " + refundId);
            return;
        }
        if (!"PENDING".equalsIgnoreCase(refund.getStatus())) {
            System.out.println("  ERROR: Only PENDING refunds can be approved. Status: " + refund.getStatus());
            return;
        }

        System.out.print("  Approving Officer Staff Number: ");
        String staffNo = input.nextLine().trim();
        RevenueOfficer officer = findOfficerByStaffNumber(staffNo);
        if (officer == null) {
            System.out.println("  ERROR: No officer found with staff number " + staffNo);
            return;
        }
        if (!officer.isActive()) {
            System.out.println("  ERROR: Officer " + staffNo + " is not active.");
            return;
        }
        if (refund.getRequestedAmount().compareTo(officer.getApprovalLimit()) > 0) {
            System.out.println("  ERROR: Refund amount " + refund.getRequestedAmount()
                    + " exceeds officer approval limit of " + officer.getApprovalLimit());
            return;
        }

        System.out.println("  Refund: " + refund.getSummary());
        System.out.println("  Officer: " + officer.getDisplayName());
        System.out.println("  Approve this refund? [YES / NO]");
        System.out.print("  Choice: ");
        String confirm = input.nextLine().trim();
        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("  Approval cancelled.");
            return;
        }

        try {
            refund.setStatus("APPROVED");
            System.out.println("  Refund " + refundId + " has been APPROVED by officer " + staffNo);
        } catch (Exception e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    // ================================================================
    //  OPTION 15: Request / Issue Tax Clearance
    // ================================================================
    static void requestIssueClearance() {
        System.out.println("\n+------------------ REQUEST / ISSUE TAX CLEARANCE --------------+");
        System.out.println("  [1] Request Clearance   [2] Issue (Approve) Clearance   [0] Back");
        System.out.print("  Choice: ");
        String choice = input.nextLine().trim();

        if (choice.equals("1")) {
            requestClearance();
        } else if (choice.equals("2")) {
            issueClearance();
        } else {
            System.out.println("  Returning to main menu.");
        }
    }

    static void requestClearance() {
        System.out.println("\n  -- REQUEST TAX CLEARANCE --");
        System.out.println("  (Type CANCEL to abort)");

        System.out.print("  Clearance ID      [String, 2-30, unique]      : ");
        String clearanceId = input.nextLine().trim();
        if (clearanceId.equalsIgnoreCase("CANCEL")) { System.out.println("  Cancelled."); return; }

        for (int i = 0; i < clearanceCount; i++) {
            if (clearances[i].getClearanceId().equals(clearanceId)) {
                System.out.println("  ERROR: Clearance ID " + clearanceId + " already exists.");
                return;
            }
        }

        System.out.print("  Taxpayer TIN      [9 digits]                   : ");
        String tin = input.nextLine().trim();
        RegisteredTaxpayer taxpayer = findTaxpayerByTin(tin);
        if (taxpayer == null) {
            System.out.println("  ERROR: No taxpayer found with TIN " + tin);
            return;
        }
        if (!taxpayer.isActive()) {
            System.out.println("  ERROR: Taxpayer must be ACTIVE to request clearance.");
            return;
        }

        // Check for outstanding balances (overdue obligations)
        boolean hasOutstanding = false;
        for (int i = 0; i < assessmentCount; i++) {
            String assTin = assessments[i].getDeclaration().getObligation().getTaxpayer().getTin();
            if (assTin.equals(tin) && "CONFIRMED".equalsIgnoreCase(assessments[i].getStatus())) {
                BigDecimal balance = getOutstandingBalance(assessments[i]);
                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    hasOutstanding = true;
                    System.out.println("  WARNING: Assessment " + assessments[i].getAssessmentId()
                            + " has outstanding balance: " + balance);
                }
            }
        }
        if (hasOutstanding) {
            System.out.println("  ERROR: Taxpayer has outstanding balances. Clearance cannot be issued.");
            return;
        }

        // Check for unresolved declarations
        boolean hasUnresolved = false;
        for (int i = 0; i < declarationCount; i++) {
            String decTin = declarations[i].getObligation().getTaxpayer().getTin();
            if (decTin.equals(tin) && "PENDING".equalsIgnoreCase(declarations[i].getStatus())) {
                hasUnresolved = true;
                System.out.println("  WARNING: Declaration " + declarations[i].getDeclarationId() + " is still PENDING.");
            }
        }
        if (hasUnresolved) {
            System.out.println("  ERROR: Taxpayer has unresolved declarations. Clearance cannot be issued.");
            return;
        }

        System.out.print("  Request Date      [YYYY-MM-DD]                 : ");
        String reqDateStr = input.nextLine().trim();
        LocalDate requestDate;
        try { requestDate = LocalDate.parse(reqDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.print("  Expiry Date       [YYYY-MM-DD, must be after request date] : ");
        String expiryDateStr = input.nextLine().trim();
        LocalDate expiryDate;
        try { expiryDate = LocalDate.parse(expiryDateStr); }
        catch (DateTimeParseException e) { System.out.println("  Invalid date. Cancelled."); return; }

        System.out.println("\n  REVIEW:");
        System.out.println("  Clearance ID    : " + clearanceId);
        System.out.println("  Taxpayer TIN    : " + tin);
        System.out.println("  Taxpayer Name   : " + taxpayer.getDisplayName());
        System.out.println("  Request Date    : " + reqDateStr);
        System.out.println("  Expiry Date     : " + expiryDateStr);
        System.out.println("  Status          : PENDING");
        System.out.println("  [1] SAVE   [0] CANCEL");
        System.out.print("  Choice: ");
        String saveChoice = input.nextLine().trim();
        if (!saveChoice.equals("1")) { System.out.println("  Cancelled. No record saved."); return; }

        try {
            if (clearanceCount >= clearances.length) {
                System.out.println("  Storage full.");
                return;
            }
            TaxClearance clearance = new TaxClearance(clearanceId, taxpayer, requestDate, expiryDate, "PENDING", null);
            // validate without approvedBy (status is PENDING so it passes)
            clearance.validate();
            clearances[clearanceCount++] = clearance;
            System.out.println("  Clearance request submitted! ID: " + clearanceId);

        } catch (ValidationException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    static void issueClearance() {
        System.out.println("\n  -- ISSUE (APPROVE) CLEARANCE --");

        System.out.print("  Clearance ID to approve: ");
        String clearanceId = input.nextLine().trim();
        TaxClearance clearance = null;
        for (int i = 0; i < clearanceCount; i++) {
            if (clearances[i].getClearanceId().equals(clearanceId)) {
                clearance = clearances[i];
                break;
            }
        }
        if (clearance == null) {
            System.out.println("  ERROR: No clearance found with ID " + clearanceId);
            return;
        }
        if (!"PENDING".equalsIgnoreCase(clearance.getStatus())) {
            System.out.println("  ERROR: Only PENDING clearances can be approved.");
            return;
        }

        System.out.print("  Approving Officer Staff Number: ");
        String staffNo = input.nextLine().trim();
        RevenueOfficer officer = findOfficerByStaffNumber(staffNo);
        if (officer == null) {
            System.out.println("  ERROR: No officer found with staff number " + staffNo);
            return;
        }
        if (!officer.isActive()) {
            System.out.println("  ERROR: Officer " + staffNo + " is not active.");
            return;
        }

        System.out.println("  Clearance: " + clearance.getSummary());
        System.out.println("  Officer: " + officer.getDisplayName());
        System.out.println("  Issue (approve) this clearance? [YES / NO]");
        System.out.print("  Choice: ");
        String confirm = input.nextLine().trim();
        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("  Approval cancelled.");
            return;
        }

        clearance.setStatus("APPROVED");
        clearance.setApprovedBy(officer);
        System.out.println("  Clearance " + clearanceId + " has been APPROVED by officer " + staffNo);
    }

    // ================================================================
    //  OPTION 16: Search Record by ID / TIN
    // ================================================================
    static void searchRecord() {
        System.out.println("\n+------------------ SEARCH RECORD ------------------------------+");
        System.out.println("  [1] Search Taxpayer by TIN");
        System.out.println("  [2] Search Taxpayer by Party ID");
        System.out.println("  [3] Search Obligation by ID");
        System.out.println("  [4] Search Declaration by ID");
        System.out.println("  [5] Search Assessment by ID");
        System.out.println("  [6] Search Officer by Staff Number");
        System.out.println("  [0] Back");
        System.out.print("  Choice: ");
        String choice = input.nextLine().trim();

        switch (choice) {
            case "1": {
                System.out.print("  Enter TIN: ");
                String tin = input.nextLine().trim();
                RegisteredTaxpayer tp = findTaxpayerByTin(tin);
                if (tp == null) System.out.println("  No taxpayer found with TIN: " + tin);
                else tp.printDetails();
                break;
            }
            case "2": {
                System.out.print("  Enter Party ID: ");
                String pid = input.nextLine().trim();
                boolean found = false;
                for (int i = 0; i < individualCount; i++) {
                    if (individuals[i].getPartyId().equals(pid)) { individuals[i].printDetails(); found = true; }
                }
                for (int i = 0; i < corporateCount; i++) {
                    if (corporates[i].getPartyId().equals(pid)) { corporates[i].printDetails(); found = true; }
                }
                for (int i = 0; i < officerCount; i++) {
                    if (officers[i].getPartyId().equals(pid)) { officers[i].printDetails(); found = true; }
                }
                if (!found) System.out.println("  No party found with ID: " + pid);
                break;
            }
            case "3": {
                System.out.print("  Enter Obligation ID: ");
                String oid = input.nextLine().trim();
                TaxObligation ob = findObligationById(oid);
                if (ob == null) System.out.println("  No obligation found with ID: " + oid);
                else ob.printDetails();
                break;
            }
            case "4": {
                System.out.print("  Enter Declaration ID: ");
                String did = input.nextLine().trim();
                TaxDeclaration dec = findDeclarationById(did);
                if (dec == null) System.out.println("  No declaration found with ID: " + did);
                else dec.printDetails();
                break;
            }
            case "5": {
                System.out.print("  Enter Assessment ID: ");
                String aid = input.nextLine().trim();
                TaxAssessment ass = findAssessmentById(aid);
                if (ass == null) System.out.println("  No assessment found with ID: " + aid);
                else ass.printDetails();
                break;
            }
            case "6": {
                System.out.print("  Enter Staff Number: ");
                String sn = input.nextLine().trim();
                RevenueOfficer off = findOfficerByStaffNumber(sn);
                if (off == null) System.out.println("  No officer found with staff number: " + sn);
                else off.printDetails();
                break;
            }
            default:
                System.out.println("  Returning to main menu.");
        }
    }

    // ================================================================
    //  OPTION 17: Update Eligible Record
    // ================================================================
    static void updateRecord() {
        System.out.println("\n+------------------ UPDATE RECORD ------------------------------+");
        System.out.println("  [1] Update Taxpayer Status");
        System.out.println("  [2] Update Tax Type Active Status");
        System.out.println("  [3] Update Officer Active Status");
        System.out.println("  [0] Back");
        System.out.print("  Choice: ");
        String choice = input.nextLine().trim();

        if (choice.equals("1")) {
            System.out.print("  Enter Taxpayer TIN: ");
            String tin = input.nextLine().trim();
            RegisteredTaxpayer tp = findTaxpayerByTin(tin);
            if (tp == null) { System.out.println("  No taxpayer found with TIN: " + tin); return; }
            System.out.println("  Current Status: " + tp.getTaxpayerStatus());
            System.out.print("  New Status [ACTIVE / INACTIVE / SUSPENDED]: ");
            String newStatus = input.nextLine().trim().toUpperCase();
            if (!newStatus.equals("ACTIVE") && !newStatus.equals("INACTIVE") && !newStatus.equals("SUSPENDED")) {
                System.out.println("  Invalid status. Update cancelled.");
                return;
            }
            tp.setTaxpayerStatus(newStatus);
            System.out.println("  Status updated to: " + newStatus);

        } else if (choice.equals("2")) {
            System.out.print("  Enter Tax Type ID: ");
            String id = input.nextLine().trim();
            TaxType tt = findTaxTypeById(id);
            if (tt == null) { System.out.println("  No tax type found with ID: " + id); return; }
            System.out.println("  Current Active: " + (tt.isActive() ? "YES" : "NO"));
            System.out.print("  New Active [YES / NO]: ");
            String newActive = input.nextLine().trim();
            tt.setActive(newActive.equalsIgnoreCase("YES"));
            System.out.println("  Tax type active status updated.");

        } else if (choice.equals("3")) {
            System.out.print("  Enter Officer Staff Number: ");
            String sn = input.nextLine().trim();
            RevenueOfficer off = findOfficerByStaffNumber(sn);
            if (off == null) { System.out.println("  No officer found."); return; }
            System.out.println("  Current Active: " + (off.isActive() ? "YES" : "NO"));
            System.out.print("  New Active [YES / NO]: ");
            String newActive = input.nextLine().trim();
            off.setActive(newActive.equalsIgnoreCase("YES"));
            System.out.println("  Officer active status updated.");
        } else {
            System.out.println("  Returning to main menu.");
        }
    }

    // ================================================================
    //  OPTION 18: View Taxpayer Statement
    // ================================================================
    static void viewTaxpayerStatement() {
        System.out.println("\n+------------------ TAXPAYER STATEMENT -------------------------+");
        System.out.print("  Enter Taxpayer TIN: ");
        String tin = input.nextLine().trim();
        RegisteredTaxpayer taxpayer = findTaxpayerByTin(tin);
        if (taxpayer == null) {
            System.out.println("  ERROR: No taxpayer found with TIN " + tin);
            return;
        }

        System.out.println("\n========== STATEMENT OF ACCOUNT ==========");
        taxpayer.printDetails();

        System.out.println("\n  -- Obligations --");
        int obCount = 0;
        for (int i = 0; i < obligationCount; i++) {
            if (obligations[i].getTaxpayer().getTin().equals(tin)) {
                System.out.println("  " + obligations[i].getSummary());
                obCount++;
            }
        }
        if (obCount == 0) System.out.println("  No obligations.");

        System.out.println("\n  -- Declarations --");
        int decCount = 0;
        for (int i = 0; i < declarationCount; i++) {
            if (declarations[i].getObligation().getTaxpayer().getTin().equals(tin)) {
                System.out.println("  " + declarations[i].getSummary());
                decCount++;
            }
        }
        if (decCount == 0) System.out.println("  No declarations.");

        System.out.println("\n  -- Assessments & Balances --");
        BigDecimal totalDue = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        int assCount = 0;
        for (int i = 0; i < assessmentCount; i++) {
            if (assessments[i].getDeclaration().getObligation().getTaxpayer().getTin().equals(tin)) {
                BigDecimal balance = getOutstandingBalance(assessments[i]);
                System.out.println("  " + assessments[i].getSummary() + " | Balance: " + balance);
                totalDue  = totalDue.add(assessments[i].getTotal());
                totalPaid = totalPaid.add(getTotalPayments(assessments[i]));
                assCount++;
            }
        }
        if (assCount == 0) System.out.println("  No assessments.");

        System.out.println("\n  -- Penalties --");
        BigDecimal totalPenalties = BigDecimal.ZERO;
        int penCount = 0;
        for (int i = 0; i < penaltyCount; i++) {
            if (penalties[i].getObligation().getTaxpayer().getTin().equals(tin)) {
                System.out.println("  " + penalties[i].getSummary());
                totalPenalties = totalPenalties.add(penalties[i].getPenaltyAmount());
                penCount++;
            }
        }
        if (penCount == 0) System.out.println("  No penalties.");

        System.out.println("\n  -- Refunds --");
        int refCount = 0;
        for (int i = 0; i < refundCount; i++) {
            if (refunds[i].getTaxpayer().getTin().equals(tin)) {
                System.out.println("  " + refunds[i].getSummary());
                refCount++;
            }
        }
        if (refCount == 0) System.out.println("  No refunds.");

        System.out.println("\n  -- Clearances --");
        int clCount = 0;
        for (int i = 0; i < clearanceCount; i++) {
            if (clearances[i].getTaxpayer().getTin().equals(tin)) {
                System.out.println("  " + clearances[i].getSummary());
                clCount++;
            }
        }
        if (clCount == 0) System.out.println("  No clearances.");

        System.out.println("\n  ---- SUMMARY ----");
        System.out.println("  Total Assessed   : " + totalDue);
        System.out.println("  Total Penalties  : " + totalPenalties);
        System.out.println("  Total Paid       : " + totalPaid);
        System.out.println("  Net Outstanding  : " + totalDue.add(totalPenalties).subtract(totalPaid));
        System.out.println("==========================================");
    }

    // ================================================================
    //  OPTION 19: View Outstanding Balances
    // ================================================================
    static void viewOutstandingBalances() {
        System.out.println("\n+------------------ OUTSTANDING BALANCES -----------------------+");
        BigDecimal grandTotal = BigDecimal.ZERO;
        int count = 0;
        for (int i = 0; i < assessmentCount; i++) {
            if ("CONFIRMED".equalsIgnoreCase(assessments[i].getStatus())) {
                BigDecimal balance = getOutstandingBalance(assessments[i]);
                if (balance.compareTo(BigDecimal.ZERO) > 0) {
                    String tin = assessments[i].getDeclaration().getObligation().getTaxpayer().getTin();
                    System.out.println("  TIN: " + tin
                            + " | Assessment: " + assessments[i].getAssessmentId()
                            + " | Balance: " + balance);
                    grandTotal = grandTotal.add(balance);
                    count++;
                }
            }
        }
        if (count == 0) {
            System.out.println("  No outstanding balances.");
        } else {
            System.out.println("\n  Total Outstanding: " + grandTotal + " (" + count + " assessment(s))");
        }
        System.out.println("+---------------------------------------------------------------+");
    }

    // ================================================================
    //  OPTION 20: View Tax Reports
    // ================================================================
    static void viewTaxReports() {
        System.out.println("\n+------------------ TAX REPORTS ---------------------------------+");
        System.out.println("  [1] Summary Report");
        System.out.println("  [2] Declarations by Status");
        System.out.println("  [3] Payments Report");
        System.out.println("  [0] Back");
        System.out.print("  Choice: ");
        String choice = input.nextLine().trim();

        if (choice.equals("1")) {
            System.out.println("\n  ====== SYSTEM SUMMARY REPORT ======");
            System.out.println("  Individual Taxpayers : " + individualCount);
            System.out.println("  Corporate Taxpayers  : " + corporateCount);
            System.out.println("  Revenue Officers     : " + officerCount);
            System.out.println("  Tax Types            : " + taxTypeCount);
            System.out.println("  Tax Obligations      : " + obligationCount);
            System.out.println("  Tax Declarations     : " + declarationCount);
            System.out.println("  Tax Assessments      : " + assessmentCount);
            System.out.println("  Tax Payments         : " + paymentCount);
            System.out.println("  Penalties            : " + penaltyCount);
            System.out.println("  Refunds              : " + refundCount);
            System.out.println("  Clearances           : " + clearanceCount);
            BigDecimal totalCollected = BigDecimal.ZERO;
            for (int i = 0; i < paymentCount; i++) totalCollected = totalCollected.add(payments[i].getAmount());
            System.out.println("  Total Revenue Collected: " + totalCollected);

        } else if (choice.equals("2")) {
            System.out.println("\n  ====== DECLARATIONS BY STATUS ======");
            int pending = 0, accepted = 0, rejected = 0, late = 0;
            for (int i = 0; i < declarationCount; i++) {
                String s = declarations[i].getStatus().toUpperCase();
                if (s.equals("PENDING")) pending++;
                else if (s.equals("ACCEPTED")) accepted++;
                else if (s.equals("REJECTED")) rejected++;
                else if (s.equals("LATE")) late++;
            }
            System.out.println("  PENDING  : " + pending);
            System.out.println("  ACCEPTED : " + accepted);
            System.out.println("  REJECTED : " + rejected);
            System.out.println("  LATE     : " + late);

        } else if (choice.equals("3")) {
            System.out.println("\n  ====== PAYMENTS REPORT ======");
            BigDecimal total = BigDecimal.ZERO;
            for (int i = 0; i < paymentCount; i++) {
                System.out.println("  " + payments[i].getSummary());
                total = total.add(payments[i].getAmount());
            }
            System.out.println("  --------------------------");
            System.out.println("  Total Payments: " + total);
        }
    }

    // ================================================================
    //  OPTION 21: Demonstrate Polymorphism
    // ================================================================
    static void demonstratePolymorphism() {
        System.out.println("\n+================ POLYMORPHISM DEMONSTRATION ================+");

        // --- Using TaxParty[] array ---
        System.out.println("\n  1. TaxParty[] array - holding different concrete types:");
        System.out.println("  ---------------------------------------------------------");

        // Build a TaxParty array with all registered parties
        int totalParties = individualCount + corporateCount + officerCount;
        TaxParty[] allParties = new TaxParty[totalParties > 0 ? totalParties : 1];
        int idx = 0;
        for (int i = 0; i < individualCount; i++) allParties[idx++] = individuals[i];
        for (int i = 0; i < corporateCount;  i++) allParties[idx++] = corporates[i];
        for (int i = 0; i < officerCount;    i++) allParties[idx++] = officers[i];

        if (totalParties == 0) {
            // Show with example objects when no data exists yet
            System.out.println("  (No registered parties yet — showing example objects)");
            IndividualTaxpayer demo1 = new IndividualTaxpayer(
                "P001","Alice","Uwase","alice@email.com","0781234567",
                LocalDate.of(1990,1,15),
                "123456789",LocalDate.of(2020,1,1),"ACTIVE",
                "Kigali","Nyarugenge","KIG-01",
                "1199800012345678","Teacher","Self Business",
                LocalDate.of(2015,6,1),new BigDecimal("5000000.00"),true
            );
            CorporateTaxpayer demo2 = new CorporateTaxpayer(
                "P002","Bob","Nzeyimana","bob@corp.com","0789876543",
                LocalDate.of(1975,3,20),
                "987654321",LocalDate.of(2018,5,10),"ACTIVE",
                "Kigali","Gasabo","KIG-02",
                "REG-12345","Rwanda Tech Ltd",LocalDate.of(2010,1,1),
                "Technology","Bob Nzeyimana",new BigDecimal("50000000.00")
            );
            RevenueOfficer demo3 = new RevenueOfficer(
                "P003","Claire","Mukamana","claire@rra.gov.rw","0790000001",
                LocalDate.of(1985,7,10),
                "STAFF001","Audit","Senior Officer",
                LocalDate.of(2015,8,1),new BigDecimal("2000000.00"),true
            );
            allParties = new TaxParty[]{demo1, demo2, demo3};
            totalParties = 3;
        }

        // Runtime polymorphism: calling overridden methods on TaxParty reference
        for (int i = 0; i < totalParties; i++) {
            TaxParty party = allParties[i];
            System.out.println("\n  Party #" + (i+1) + ":");
            System.out.println("    getDisplayName() -> " + party.getDisplayName());   // polymorphic call
            System.out.println("    getSummary()     -> " + party.getSummary());       // polymorphic call
            System.out.println("    isActive()       -> " + party.isActive());         // polymorphic call
            System.out.println("    getId()          -> " + party.getId());            // from Identifiable
            // instanceof check to show the actual type
            if (party instanceof IndividualTaxpayer) {
                System.out.println("    Runtime type     -> IndividualTaxpayer");
            } else if (party instanceof CorporateTaxpayer) {
                System.out.println("    Runtime type     -> CorporateTaxpayer");
            } else if (party instanceof RevenueOfficer) {
                System.out.println("    Runtime type     -> RevenueOfficer");
            }
        }

        // --- Using Identifiable[] array ---
        System.out.println("\n\n  2. Identifiable[] array - holding ALL domain types:");
        System.out.println("  -----------------------------------------------------");

        // Mix different domain types in one Identifiable array
        Identifiable[] mixed = new Identifiable[6];
        int mIdx = 0;
        if (individualCount > 0 && mIdx < 6) mixed[mIdx++] = individuals[0];
        if (corporateCount  > 0 && mIdx < 6) mixed[mIdx++] = corporates[0];
        if (officerCount    > 0 && mIdx < 6) mixed[mIdx++] = officers[0];
        if (taxTypeCount    > 0 && mIdx < 6) mixed[mIdx++] = taxTypes[0];
        if (obligationCount > 0 && mIdx < 6) mixed[mIdx++] = obligations[0];
        if (assessmentCount > 0 && mIdx < 6) mixed[mIdx++] = assessments[0];

        if (mIdx == 0) {
            System.out.println("  (No records yet — register some data first to see live polymorphism)");
        } else {
            for (int i = 0; i < mIdx; i++) {
                Identifiable item = mixed[i];
                System.out.println("\n  Item #" + (i+1) + " [" + item.getClass().getSimpleName() + "]:");
                System.out.println("    getId()          -> " + item.getId());
                System.out.println("    getDisplayName() -> " + item.getDisplayName());
                System.out.println("    isActive()       -> " + item.isActive());
            }
        }

        System.out.println("\n  KEY TAKEAWAY:");
        System.out.println("  - getDisplayName() returns a DIFFERENT string for each concrete type");
        System.out.println("    even though we call it through the same TaxParty or Identifiable reference.");
        System.out.println("  - This is runtime polymorphism: the JVM decides which override to call");
        System.out.println("    based on the actual object type, not the reference type.");
        System.out.println("+===========================================================+");
    }
}
