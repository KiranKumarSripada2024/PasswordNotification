package com.alerts.PasswordNotification.model;

import java.time.LocalDate;

public class User {
    private String username;
    private String application;
    private String environment;
    private int passwordRotationFrequency; // in days
    private LocalDate startDate;
    private String supportTeamEmail;

    public User() {
    }

    public User(String username, String application, String environment,
                int passwordRotationFrequency, LocalDate startDate, String supportTeamEmail) {
        this.username = username;
        this.application = application;
        this.environment = environment;
        this.passwordRotationFrequency = passwordRotationFrequency;
        this.startDate = startDate;
        this.supportTeamEmail = supportTeamEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public int getPasswordRotationFrequency() {
        return passwordRotationFrequency;
    }

    public void setPasswordRotationFrequency(int passwordRotationFrequency) {
        this.passwordRotationFrequency = passwordRotationFrequency;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getSupportTeamEmail() {
        return supportTeamEmail;
    }

    public void setSupportTeamEmail(String supportTeamEmail) {
        this.supportTeamEmail = supportTeamEmail;
    }

    public LocalDate getNextRotationDate() {
        // Calculate the next rotation date based on the start date and frequency
        LocalDate currentDate = LocalDate.now();
        LocalDate nextRotation = startDate;

        while (nextRotation.isBefore(currentDate) || nextRotation.isEqual(currentDate)) {
            nextRotation = nextRotation.plusDays(passwordRotationFrequency);
        }

        return nextRotation;
    }

    public int getDaysUntilRotation() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), getNextRotationDate());
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", application='" + application + '\'' +
                ", environment='" + environment + '\'' +
                ", passwordRotationFrequency=" + passwordRotationFrequency +
                ", startDate=" + startDate +
                ", supportTeamEmail='" + supportTeamEmail + '\'' +
                '}';
    }
}