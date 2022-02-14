package com.shurman.linkbar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class LinkNotification {
    private static final int CONVERT_ICON_SIZE = 192;
    private static final String CHANNEL_ID = "main";
    private static final String CHANNEL_NAME = "LinkBar";
    private static final String CHANNEL_DESCRIPTION = "";
    private static final int NOTIFICATION_ID = 1;

    public static void show(Context context, AppLinkKit[] linkKits) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, createNotification(context, linkKits));
    }

    public static void clear(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .cancel(NOTIFICATION_ID);
    }

    private static Notification createNotification(Context context, AppLinkKit[] linkKits) {
        createChannel(context);
        RemoteViews rw = new RemoteViews(context.getPackageName(), R.layout.notification);
        for (AppLinkKit kit : linkKits) {
            addIcon(context, rw, kit);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.small_icon)
                .setContent(rw)
                .setOngoing(true)
                .setSilent(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        return builder.build();
    }

    private static void addIcon(Context context, RemoteViews root, AppLinkKit linkKit) {
        PendingIntent pi = createPendingIntent(context, linkKit.getPackage());
        if (null == pi) return;
        RemoteViews newIcon = new RemoteViews(context.getPackageName(), R.layout.single_icon);

        Bitmap bmp = drawableToBitmap(linkKit.getIcon());
        newIcon.setImageViewBitmap(R.id.icon, bmp);
        newIcon.setOnClickPendingIntent(R.id.icon, pi);
        root.addView(R.id.root, newIcon);
    }

    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(CHANNEL_DESCRIPTION);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    private static PendingIntent createPendingIntent(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        return null == intent ?
                null :
                PendingIntent.getActivity(context,0, intent,0);
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) return bitmap;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                    drawable.getIntrinsicHeight(),
                                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            ((AdaptiveIconDrawable) drawable).getBackground().draw(canvas);
            ((AdaptiveIconDrawable) drawable).getForeground().draw(canvas);
            return bitmap;
        }
        bitmap = Bitmap.createBitmap(CONVERT_ICON_SIZE,CONVERT_ICON_SIZE,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }
}
