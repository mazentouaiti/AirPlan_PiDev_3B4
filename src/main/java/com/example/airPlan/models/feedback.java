package com.example.airPlan.models;

import java.sql.Date;

public class feedback {
    private int idfeed;
    private String titlefeed;
    private Date datefeed;
    private String reponsefeed;
    private String statutfeed;
    private int id_user;

    public feedback() {
    }

    public feedback(int idfeed, String titlefeed,  Date datefeed, String reponsefeed, String statutfeed, int id_user) {
        this.idfeed = idfeed;
        this.titlefeed = titlefeed;
        this.datefeed = datefeed;
        this.reponsefeed = reponsefeed;
        this.statutfeed = statutfeed;
        this.id_user = id_user;
    }

    public int getIdfeed() {
        return idfeed;
    }

    public void setIdfeed(int idfeed) {
        this.idfeed = idfeed;
    }

    public String getTitlefeed() {
        return titlefeed;
    }

    public void setTitlefeed(String titlefeed) {
        this.titlefeed = titlefeed;
    }

    public Date getDatefeed() {
        return datefeed;
    }

    public void setDatefeed(Date datefeed) {
        this.datefeed = datefeed;
    }

    public String getReponsefeed() {
        return reponsefeed;
    }

    public void setReponsefeed(String reponsefeed) {
        this.reponsefeed = reponsefeed;
    }

    public String getStatutfeed() {
        return statutfeed;
    }

    public void setStatutfeed(String statutfeed) {
        this.statutfeed = statutfeed;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    @Override
    public String toString() {
        return "feedback{" +
                "idfeed=" + idfeed +
                ", titlefeed='" + titlefeed + '\'' +
                ", datefeed=" + datefeed +
                ", reponsefeed='" + reponsefeed + '\'' +
                ", statutfeed='" + statutfeed + '\'' +
                ", id_user='" + id_user + '\'' +
                '}';
    }
}
