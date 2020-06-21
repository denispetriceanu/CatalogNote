package com.example.catalognote.Models;

public class Message {
    String message, date, isTeacher;

    public Message(String message, String date, String isTeacher) {
        this.message = message;
        this.date = date;
        this.isTeacher = isTeacher;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(String isTeacher) {
        this.isTeacher = isTeacher;
    }

}
