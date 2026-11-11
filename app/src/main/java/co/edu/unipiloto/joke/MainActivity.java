package co.edu.unipiloto.joke;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Desde Android 13 (API 33) hay que pedir permiso para mostrar notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    //This will run when the button gets clicked
    public void onClick(View view) {
        Intent intent = new Intent(this, DelayedMessageService.class);
        intent.putExtra(DelayedMessageService.EXTRA_MESSAGE,
                getResources().getString(R.string.response));
        startService(intent);
    }

    // ---------------- Funcionalidad del proyecto ----------------

    // El profesor publica la calificación de un taller o parcial: se notifica al estudiante
    public void onClickCalificar(View view) {
        EditText etActividad = findViewById(R.id.etActividad);
        EditText etNota = findViewById(R.id.etNota);
        String actividad = etActividad.getText().toString().trim();
        String nota = etNota.getText().toString().trim();

        if (actividad.isEmpty() || nota.isEmpty()) {
            Toast.makeText(this, "Escribe la actividad y la nota", Toast.LENGTH_SHORT).show();
            return;
        }
        double valor;
        try {
            valor = Double.parseDouble(nota.replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La nota debe ser un número", Toast.LENGTH_SHORT).show();
            return;
        }
        if (valor < 0 || valor > 5) {
            Toast.makeText(this, "La nota debe estar entre 0.0 y 5.0", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, NotificacionAcademicaService.class);
        intent.putExtra(NotificacionAcademicaService.EXTRA_TITULO, "Calificación publicada");
        intent.putExtra(NotificacionAcademicaService.EXTRA_MENSAJE,
                "Tu " + actividad + " fue calificado con " + valor + " / 5.0");
        startService(intent);
        Toast.makeText(this, "Calificación enviada al estudiante", Toast.LENGTH_SHORT).show();
    }

    // El estudiante completa una tarea: se notifica al profesor para que la califique
    public void onClickCompletarTarea(View view) {
        EditText etTarea = findViewById(R.id.etTarea);
        String tarea = etTarea.getText().toString().trim();

        if (tarea.isEmpty()) {
            Toast.makeText(this, "Escribe el nombre de la tarea", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, NotificacionAcademicaService.class);
        intent.putExtra(NotificacionAcademicaService.EXTRA_TITULO, "Tarea completada");
        intent.putExtra(NotificacionAcademicaService.EXTRA_MENSAJE,
                "Un estudiante completó la tarea \"" + tarea + "\". Está pendiente por calificar.");
        startService(intent);
        Toast.makeText(this, "Tarea enviada al profesor", Toast.LENGTH_SHORT).show();
    }
}
