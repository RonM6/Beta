package com.example.beta;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.beta.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class AlarmHelper {
    public static void setAlarm(Context context, String choreId, String dueDate, String dueTime, String title, String detail) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        LocalDate parsedDate = LocalDate.parse(dueDate, formatter);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime parsedTime = LocalTime.parse(dueTime, formatter2);
        LocalDateTime localDateTime = parsedDate.atTime(parsedTime.minusHours(1));
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("choreId", choreId);
        intent.putExtra("title", title);
        intent.putExtra("detail", detail);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                choreId.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long alarmTimeMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//             Set the alarm
        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmTimeMillis, alarmIntent);
    }

    public static void cancelAlarm(Context context, String choreId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, choreId.hashCode(), intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public static boolean isAlarmSet(Context context, String choreId) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, choreId.hashCode(), intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        return pendingIntent != null;
    }
}
