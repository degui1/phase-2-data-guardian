package com.api_application.phase_2_data_guardian.domain.entity;

import java.time.LocalTime;

public class OpeningPeriod {

    private LocalTime opensAt;
    private LocalTime closesAt;

    public OpeningPeriod() {
    }

    public OpeningPeriod(LocalTime opensAt, LocalTime closesAt) {
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    public LocalTime getOpensAt() {
        return opensAt;
    }

    public void setOpensAt(LocalTime opensAt) {
        this.opensAt = opensAt;
    }

    public LocalTime getClosesAt() {
        return closesAt;
    }

    public void setClosesAt(LocalTime closesAt) {
        this.closesAt = closesAt;
    }
}
