package com.example.airPlan.models;

import java.sql.Date;

public class reclamation {
    private int id;
    private String type;
    private Date datee;
    private int note;
    private String description;
    private String statut;
    private int id_user;

    public reclamation() {
    }

    public reclamation(int id, String type, Date datee, String description,int note, String statut, int id_user) {
        this.id = id;
        this.type = type;
        this.datee = datee;
        this.description = description;
        this.note = note;
        this.statut = statut;
        this.id_user = id_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDatee() {
        return datee;
    }

    public void setDatee(Date datee) {
        this.datee = datee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "reclamation{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", datee=" + datee +
                ", note=" + note +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", id_user=" + id_user +
                '}';
    }

}
