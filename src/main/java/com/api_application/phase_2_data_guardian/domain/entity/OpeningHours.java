package com.api_application.phase_2_data_guardian.domain.entity;

import java.util.ArrayList;
import java.util.List;

public class OpeningHours {

    private List<OpeningPeriod> monday;
    private List<OpeningPeriod> tuesday;
    private List<OpeningPeriod> wednesday;
    private List<OpeningPeriod> thursday;
    private List<OpeningPeriod> friday;
    private List<OpeningPeriod> saturday;
    private List<OpeningPeriod> sunday;

    public OpeningHours() {
        this.monday = new ArrayList<>();
        this.tuesday = new ArrayList<>();
        this.wednesday = new ArrayList<>();
        this.thursday = new ArrayList<>();
        this.friday = new ArrayList<>();
        this.saturday = new ArrayList<>();
        this.sunday = new ArrayList<>();
    }

    public OpeningHours(
            List<OpeningPeriod> monday,
            List<OpeningPeriod> tuesday,
            List<OpeningPeriod> wednesday,
            List<OpeningPeriod> thursday,
            List<OpeningPeriod> friday,
            List<OpeningPeriod> saturday,
            List<OpeningPeriod> sunday
    ) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public List<OpeningPeriod> getMonday() {
        return monday;
    }

    public void setMonday(List<OpeningPeriod> monday) {
        this.monday = monday;
    }

    public List<OpeningPeriod> getTuesday() {
        return tuesday;
    }

    public void setTuesday(List<OpeningPeriod> tuesday) {
        this.tuesday = tuesday;
    }

    public List<OpeningPeriod> getWednesday() {
        return wednesday;
    }

    public void setWednesday(List<OpeningPeriod> wednesday) {
        this.wednesday = wednesday;
    }

    public List<OpeningPeriod> getThursday() {
        return thursday;
    }

    public void setThursday(List<OpeningPeriod> thursday) {
        this.thursday = thursday;
    }

    public List<OpeningPeriod> getFriday() {
        return friday;
    }

    public void setFriday(List<OpeningPeriod> friday) {
        this.friday = friday;
    }

    public List<OpeningPeriod> getSaturday() {
        return saturday;
    }

    public void setSaturday(List<OpeningPeriod> saturday) {
        this.saturday = saturday;
    }

    public List<OpeningPeriod> getSunday() {
        return sunday;
    }

    public void setSunday(List<OpeningPeriod> sunday) {
        this.sunday = sunday;
    }
}
