package com.jordanforsythe.repairloginsystem.Repair;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Repair {

    private String repairStatus;
    private String customerName;
    private String customerPhoneNumber;
    private String customerEmailAddress;
    private String imeiNumber;
    private String faultDescription;
    private int jobNumber;
    private String databaseAutomaticKey;
    private String jobNotes;
    private long timeDateBookedIn;
    private String standbyPhoneIMEI;
    private String engineerNotes;


    public Repair() {
    }

    public Repair(String customerName,String customerPhoneNumber,String customerEmailAddress, String imeiNumber, String faultDescription, int jobNumber, String repairStatus, long timeDateBookedIn, String standbyPhoneIMEI, String engineerNotes){
        this.jobNumber = jobNumber;
        this.customerName = customerName;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerEmailAddress = customerEmailAddress;
        this.imeiNumber = imeiNumber;
        this.faultDescription = faultDescription;
        this.repairStatus = repairStatus;
        this.timeDateBookedIn = timeDateBookedIn;
        this.standbyPhoneIMEI = standbyPhoneIMEI;
        this.engineerNotes = engineerNotes;


    }

    public String getCustomerName(){ return customerName;}
    public String getImeiNumber() { return imeiNumber;}
    public String getFaultDescription(){return faultDescription;}

    public int getJobNumber(){return jobNumber;}
    public void setJobNumber(int jobNumber) {this.jobNumber = jobNumber;}

    public String getRepairStatus() {return repairStatus;}
    public void setRepairStatus(String repairStatus) {this.repairStatus = repairStatus;}

    public String getDatabaseAutomaticKey() {return databaseAutomaticKey;}
    public void setDatabaseAutomaticKey(String databaseAutomaticKey) {this.databaseAutomaticKey = databaseAutomaticKey;}

    public String getJobNotes() {return jobNotes;}
    public void setJobNotes(String jobNotes) {this.jobNotes = jobNotes;}

    public void setTimeDateBookedIn(long timeDateBookedIn) {this.timeDateBookedIn = timeDateBookedIn;}

    public String getCustomerPhoneNumber() {return customerPhoneNumber;}
    public String getCustomerEmailAddress() {return customerEmailAddress;}

    public String getStandbyPhoneIMEI() {return standbyPhoneIMEI;}

    public String getEngineerNotes() {return engineerNotes;}

    public void setEngineerNotes(String engineerNotes) {this.engineerNotes = engineerNotes;}

    public String getFormattedTimestamp() {
        String datePattern = "EEE, MMM d, h:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date repairCreationDate = new Date(timeDateBookedIn);
        return dateFormat.format(repairCreationDate);
    }
}
