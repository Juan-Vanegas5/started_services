package co.edu.unipiloto.joke;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

/**
 * Started service (IntentService) del proyecto académico.
 * - Cuando el profesor califica un taller o parcial, notifica al estudiante del curso.
 * - Cuando el estudiante completa una tarea, notifica al profesor para que la califique.
 */
public class NotificacionAcademicaService extends IntentService {

    public static final String EXTRA_TITULO = "titulo";
    public static final String EXTRA_MENSAJE = "mensaje";
    public static final String CHANNEL_ID = "academico_channel";

    public NotificacionAcademicaService() {
        super("NotificacionAcademicaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            try {
                wait(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String titulo = intent.getStringExtra(EXTRA_TITULO);
        String mensaje = intent.getStringExtra(EXTRA_MENSAJE);
        Log.v("NotificacionAcademica", titulo + ": " + mensaje);
        mostrarNotificacion(titulo, mensaje);
    }

    private void mostrarNotificacion(String titulo, String mensaje) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notificaciones académicas", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(new long[] {0, 1000})
                        .setAutoCancel(true);

        Intent actionIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                this,
                1,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(actionPendingIntent);

        // ID distinto en cada notificación para que no se reemplacen entre sí
        int notificationId = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
        notificationManager.notify(notificationId, builder.build());
    }
}
