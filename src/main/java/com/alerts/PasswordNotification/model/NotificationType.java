package com.alerts.PasswordNotification.model;

public enum NotificationType {
    THIRTY_DAYS_BEFORE(30),
    FIFTEEN_DAYS_BEFORE(15);

    private final int daysBeforeRotation;

    NotificationType(int daysBeforeRotation) {
        this.daysBeforeRotation = daysBeforeRotation;
    }

    public int getDaysBeforeRotation() {
        return daysBeforeRotation;
    }
}

