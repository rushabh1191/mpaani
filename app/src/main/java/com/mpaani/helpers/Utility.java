package com.mpaani.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;

import com.mpaani.mpaani.R;

public class Utility {



    public static void showNotification(String title,String message,Context context){
        showNotification(message,title,context,12331);

    }
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void cancelAll(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }

    public static void cancel(Context context, int notiId) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notiId);

    }

    public static void showNotification(String message, String title, Context context,int id) {


        try {


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentText(message)
                            .setTicker(message)
                            .setContentTitle(title);


            mBuilder.setAutoCancel(true);

            Notification notification = mBuilder.build();


            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            mNotificationManager.notify(id, notification);


        } catch (Exception e) {

            e.printStackTrace();


        } finally {


        }


    }
}
