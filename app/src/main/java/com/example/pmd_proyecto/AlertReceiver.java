package com.example.pmd_proyecto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Usamos NotificationHandler para lanzar la notificación
        NotificationHandler handler = new NotificationHandler(context);

        Notification.Builder nb = handler.createNotification(
                "¡Hora del reto diario!",
                "Entra y resuelve el reto de hoy."
        );

        // ID 2 para que no se pise con la de descarga (que usaba ID 1)
        handler.getManager().notify(2, nb.build());
    }
}