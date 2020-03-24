package com.track.safezone.notification.impl;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.track.safezone.notification.Channel;

public class UrgentSafezoneNotification extends AbstractSafezoneNotification {


    private final Notification notification;

    public UrgentSafezoneNotification(Channel channel, Context context, NotificationManager notificationManager, Class<?> confirmObservationStatusActivityClass) {
        super(channel);
        this.notification = createNotification(context, notificationManager, confirmObservationStatusActivityClass);
    }

    @Override
    public Notification getNotification() {
        return notification;
    }
}
