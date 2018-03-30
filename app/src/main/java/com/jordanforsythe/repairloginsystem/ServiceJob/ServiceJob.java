package com.jordanforsythe.repairloginsystem.ServiceJob;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServiceJob {


    private String serviceJobcustomerName;
    private String serviceJobCustomerPhone;
    private String serviceJobFaultNotes;
    private int serviceJobNumber;
    private long serviceJobtimeDateBookedIn;


    public ServiceJob(){
    }

    public ServiceJob(String serviceJobcustomerName, String serviceJobCustomerPhone, String serviceJobFaultNotes, int serviceJobNumber,
                      long serviceJobtimeDateBookedIn) {

        this.serviceJobcustomerName = serviceJobcustomerName;
        this.serviceJobCustomerPhone = serviceJobCustomerPhone;
        this.serviceJobFaultNotes = serviceJobFaultNotes;
        this.serviceJobNumber = serviceJobNumber;
        this.serviceJobtimeDateBookedIn = serviceJobtimeDateBookedIn;
    }

    public String getServiceJobcustomerName() {return serviceJobcustomerName;}
    public void setServiceJobcustomerName(String serviceJobcustomerName) {this.serviceJobcustomerName = serviceJobcustomerName;}

    public String getServiceJobCustomerPhone() {return serviceJobCustomerPhone;}
    public void setServiceJobCustomerPhone(String serviceJobCustomerPhone) {this.serviceJobCustomerPhone = serviceJobCustomerPhone;}

    public String getServiceJobFaultNotes() {return serviceJobFaultNotes;}
    public void setServiceJobFaultNotes(String serviceJobFaultNotes) {this.serviceJobFaultNotes = serviceJobFaultNotes;}

    public int getServiceJobNumber() {return serviceJobNumber;}
    public void setServiceJobNumber(int serviceJobNumber) {this.serviceJobNumber = serviceJobNumber;}

    public void setServiceJobtimeDateBookedIn(long serviceJobtimeDateBookedIn) {this.serviceJobtimeDateBookedIn = serviceJobtimeDateBookedIn;}

    public String getFormattedTimestamp() {
        String datePattern = "EEE, MMM d, h:mm a";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        Date repairCreationDate = new Date(serviceJobtimeDateBookedIn);
        return dateFormat.format(repairCreationDate);
    }

}