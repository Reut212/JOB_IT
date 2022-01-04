package com.example.myapplication.Model;

public class Data {

    private String title;
    private String description;
    private String dateJob;
    private String salary;

    private String id;
    private String date;
    private String timeNow;
    private String location;
    private String contactName;
    private String email;



    private String imageUrl;


    public Data(){
        this.imageUrl="null";
    }

    public Data(String title, String location, String description, String dateJob, String salary, String id, String date, String timeNow, String contactName, String email) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.dateJob = dateJob;
        this.salary = salary;
        this.id = id;
        this.date = date;
        this.timeNow = timeNow;
        this.contactName = contactName;
        this.email = email;

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

    public String getDateJob() {
        return dateJob;
    }

    public void setDateJob(String dateJob) {
        this.dateJob = dateJob;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeNow() {
        return timeNow;
    }

    public void setTimeNow(String timeNow) {
        this.timeNow = timeNow;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}