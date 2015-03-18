package fr.lassiergedeon.dontbreakthechain.model;

import java.net.URI;
import java.util.Calendar;

/**
 * Created by Antoine on 18/03/2015.
 */
public class Task {
    private int id;
    private String title;
    private Calendar notificationHour;
    private URI ringToneURI;

    public Task() {}

    public Task(String title, Calendar notificationHour, URI ringToneURI) {
        super();
        this.title = title;
        this.notificationHour = notificationHour;
        this.ringToneURI = ringToneURI;
    }

    public Task(int id, String title, Calendar notificationHour, URI ringToneURI) {
        super();
        this.id = id;
        this.title = title;
        this.notificationHour = notificationHour;
        this.ringToneURI = ringToneURI;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getNotificationHour() {
        return notificationHour;
    }

    public void setNotificationHour(Calendar notificationHour) {
        this.notificationHour = notificationHour;
    }

    public URI getRingToneURI() {
        return ringToneURI;
    }

    public void setRingToneURI(URI ringToneURI) {
        this.ringToneURI = ringToneURI;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", notificationHour=" + notificationHour +
                ", ringToneURI=" + ringToneURI +
                '}';
    }
}
