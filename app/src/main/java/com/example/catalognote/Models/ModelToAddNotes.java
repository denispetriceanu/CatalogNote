package com.example.catalognote.Models;

public class ModelToAddNotes {
    private String note, date;

    public ModelToAddNotes(String note, String date) {
        this.note = note;
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
