package com.track.safezone.notification;

import android.app.NotificationChannel;
import android.os.Build;

public class Channel {

    private NotificationChannel channel;

    private String channelId;


    public Channel(String id, String name, String description, int importance) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.channel = new NotificationChannel(id, name, importance);
            this.channel.setDescription(description);
        }
        this.channelId = id;

    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public String getChannelId() {
        return channelId;
    }
}
