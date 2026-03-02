package com.gymmanagement.app.models;

public class Member {
    private int id;
    private String fullName;
    private String phone;
    private String joinDate;
    private String plan;
    private double feeAmount;
    private String paymentStatus;
    private String expiryDate;

    // Constructor
    public Member() {
    }

    public Member(int id, String fullName, String phone, String joinDate, 
                  String plan, double feeAmount, String paymentStatus, String expiryDate) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.joinDate = joinDate;
        this.plan = plan;
        this.feeAmount = feeAmount;
        this.paymentStatus = paymentStatus;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public double getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(double feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Helper method to get membership status
    public String getMembershipStatus() {
        try {
            long currentTime = System.currentTimeMillis();
            String[] parts = expiryDate.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);
            
            java.util.Calendar expiryCal = java.util.Calendar.getInstance();
            expiryCal.set(year, month, day);
            long expiryTime = expiryCal.getTimeInMillis();
            
            long diffDays = (expiryTime - currentTime) / (1000 * 60 * 60 * 24);
            
            if (diffDays < 0) {
                return "Expired";
            } else if (diffDays <= 3) {
                return "Expiring Soon";
            } else {
                return "Active";
            }
        } catch (Exception e) {
            return "Active";
        }
    }
}
