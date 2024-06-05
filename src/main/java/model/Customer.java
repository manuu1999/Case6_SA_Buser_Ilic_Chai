package model;

import java.sql.Timestamp;

// Customer class definition
public class Customer {
    private int custId;
    private String name;
    private String email;
    private String phone;
    private String comment;
    private Timestamp createdOn;

    // Getters and setters

    public int getCustId() {
        return custId;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "Customer [custId=" + custId + ", name=" + name + ", email=" + email + ", phone=" + phone + ", comment="
                + comment + ", createdOn=" + createdOn + "]";
    }
}