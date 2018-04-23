package com.jordanforsythe.repairloginsystem.ServiceJob;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceJob {

    //variables that are contained within a service job class
    private String serviceJobCustomerName;
    private String serviceJobCustomerPhone;
    private String serviceJobFaultNotes;
    private int serviceJobNumber;
    private long serviceJobtimeDateBookedIn;
    private String serviceJobUsername;
    private String serviceDatabaseAutomaticKey;

    //Empty method with the class name for firebase
    public ServiceJob(){
    }

    public ServiceJob(String serviceJobcustomerName, String serviceJobCustomerPhone, String serviceJobFaultNotes, int serviceJobNumber,
                      long serviceJobtimeDateBookedIn, String serviceJobUsername) {

        this.serviceJobCustomerName = serviceJobcustomerName;
        this.serviceJobCustomerPhone = serviceJobCustomerPhone;
        this.serviceJobFaultNotes = serviceJobFaultNotes;
        this.serviceJobNumber = serviceJobNumber;
        this.serviceJobtimeDateBookedIn = serviceJobtimeDateBookedIn;
        this.serviceJobUsername = serviceJobUsername;
    }

    public String getServiceJobCustomerName() {return serviceJobCustomerName;}
    public void setServiceJobCustomerName(String serviceJobCustomerName) {this.serviceJobCustomerName = serviceJobCustomerName;}

    public String getServiceJobCustomerPhone() {return serviceJobCustomerPhone;}
    public void setServiceJobCustomerPhone(String serviceJobCustomerPhone) {this.serviceJobCustomerPhone = serviceJobCustomerPhone;}

    public String getServiceJobFaultNotes() {return serviceJobFaultNotes;}
    public void setServiceJobFaultNotes(String serviceJobFaultNotes) {this.serviceJobFaultNotes = serviceJobFaultNotes;}

    public int getServiceJobNumber() {return serviceJobNumber;}
    public void setServiceJobNumber(int serviceJobNumber) {this.serviceJobNumber = serviceJobNumber;}

    public void setServiceJobtimeDateBookedIn(long serviceJobtimeDateBookedIn) {this.serviceJobtimeDateBookedIn = serviceJobtimeDateBookedIn;}

    public String getServiceJobUsername() {return serviceJobUsername;}

    public void setServiceJobUsername(String serviceJobUsername) {this.serviceJobUsername = serviceJobUsername;}

    public String getserviceDatabaseAutomaticKey() {return serviceDatabaseAutomaticKey;}
    public void setserviceDatabaseAutomaticKey(String serviceDatabaseAutomaticKey) {this.serviceDatabaseAutomaticKey = serviceDatabaseAutomaticKey;}

    public String getFormattedTimestamp() {
        String datePattern = "d MMM, h:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date repairCreationDate = new Date(serviceJobtimeDateBookedIn);
        return dateFormat.format(repairCreationDate);
    }

}
