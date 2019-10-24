package com.example.project_1_clickn.ui.dashboard;

public class EventModel {
    private String title, description;
    private Double latitude, longitude;

    // Getters

    public String getEventTitle() {
        return this.title;
    }

    public String getEventDescription() {
        return this.description;
    }

    public Double getEventLatitude() {
        return this.latitude;
    }

    public Double getEventLongitude() {
        return this.longitude;
    }

    // Setters

    public void setEventTitle(String title) {
        this.title = title;
    }

    public void setEventDescription(String description) {
        this.description = description;
    }
    

    public void setEventLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setEventLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
