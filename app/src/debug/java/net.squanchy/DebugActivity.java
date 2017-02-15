package net.squanchy;

import android.app.Activity;
import android.app.Notification;
import android.os.Bundle;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import net.squanchy.notification.NotificationCreator;
import net.squanchy.notification.Notifier;
import net.squanchy.schedule.domain.view.Event;
import net.squanchy.service.firebase.model.FirebaseSpeaker;
import net.squanchy.speaker.Speaker;

@SuppressWarnings("checkstyle:magicnumber")
public class DebugActivity extends Activity {

    private NotificationCreator notificationCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Button buttonSingleNotification = (Button) findViewById(R.id.button_test_single_notification);
        buttonSingleNotification.setOnClickListener(view -> testSingleNotification());

        Button buttonMultipleNotifications = (Button) findViewById(R.id.button_test_multiple_notifications);
        buttonMultipleNotifications.setOnClickListener(view -> testMultipleNotifications());

        notificationCreator = new NotificationCreator(this);
    }

    private void testSingleNotification() {
        createAndNotifyTalksCount(1);
    }

    private void testMultipleNotifications() {
        createAndNotifyTalksCount(3);
    }

    private void createAndNotifyTalksCount(int count) {
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(createTestEvent(i));
        }
        List<Notification> notifications = notificationCreator.createFrom(events);

        Notifier notifier = Notifier.from(this);
        notifier.showNotifications(notifications);
    }

    private Event createTestEvent(int id) {
        return Event.create(
                id,
                1,
                "A very interesting talk",
                "That room over there",
                3,
                createTalkSpeakers()
        );
    }

    private List<Speaker> createTalkSpeakers() {
        List<Speaker> speakers = new ArrayList<>(2);
        FirebaseSpeaker firebaseSpeaker = new FirebaseSpeaker();
        firebaseSpeaker.speakerId = 101L;
        firebaseSpeaker.firstName = "Ajeje";
        firebaseSpeaker.lastName = "Brazorf";
        firebaseSpeaker.jobTitle = "Uber-Experienced Pusher of PHP";
        firebaseSpeaker.avatarImageURL = "https://yt3.ggpht.com/-d35Rq8vqvmE/AAAAAAAAAAI/AAAAAAAAAAA/zy1VyiRTNec/s900-c-k-no-mo-rj-c0xffffff/photo.jpg";

        speakers.add(Speaker.create(firebaseSpeaker));
        return speakers;
    }

}
