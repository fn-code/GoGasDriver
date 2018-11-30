package gaskudriver.funcode.funcode.com.gogasdriver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by funcode on 12/9/17.
 */

public class Notifikasi extends FirebaseMessagingService {
    private static final String TAG = "Notifikasi";
    final static String GROUP_KEY_EMAILS = "group_key_emails";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        String click_action =remoteMessage.getNotification().getClickAction();
        /*String userId = remoteMessage.getData().get("idUser");
        String idDriver = remoteMessage.getData().get("idDriver");
        String UserToken = remoteMessage.getData().get("UserToken");
        String idTransaksi = remoteMessage.getData().get("idTransaksi");*/

        Log.d(TAG, "FROM:" + remoteMessage.getFrom());

        //Check if the message contains data
        if (remoteMessage.getData().size() > 0) {

        }

        //Check if the message contains notification

        if (remoteMessage.getNotification() != null) {
            Intent intent = new Intent(click_action);

            /*intent.putExtra("IDDriver", idDriver);
            intent.putExtra("IDUser", userId);
            intent.putExtra("UserToken", UserToken);
            intent.putExtra("IDTransaksi", idTransaksi);*/
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //Set sound of notification
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            int color = getResources().getColor(R.color.colorAccent);

            NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)

                    .setSmallIcon(R.drawable.gas)
                    .setColor(color)
                    .setBadgeIconType(1)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setGroup(GROUP_KEY_EMAILS)
                    .setVibrate(new long[] { 100, 250, 100, 250, 100, 250 })
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0 /*ID of notification*/, notifiBuilder.build());
        }

    }



    private void sendBroadcast(String title, String body, String click_action) {
    }


    /**
     * Dispay the notification
     *
     * @param body
     */


    private void sendNotification(String title, String body, String click_action) {

        Intent intent = new Intent(click_action);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //Set sound of notification
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setPriority(Notification.PRIORITY_HIGH)
                .setGroup(GROUP_KEY_EMAILS)
                .setVibrate(new long[] { 100, 250, 100, 250, 100, 250 })
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0 /*ID of notification*/, notifiBuilder.build());
    }
}
