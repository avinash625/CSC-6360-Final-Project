package com.avinash.requestresource;

public class Requests extends Throwable {
    private int requestID;
    private String userID;
    private String title;
    private String description;
    private int quantity;
    private String priority;
    private boolean completed;
    private String addressedBy;

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getAddressedBy() {
        return addressedBy;
    }

    public void setAddressedBy(String addressedBy) {
        this.addressedBy = addressedBy;
    }
}
