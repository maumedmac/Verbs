package bo.edu.cba;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

// Esta es una clase abstracta porque nunca la usaremos directamente,
// solo heredaremos de ella.
public abstract class BaseActivity extends AppCompatActivity {

    // Método abstracto que las clases hijas DEBEN implementar para
    // decirnos cómo navegar a un índice específico.
    protected abstract void navigateTo(int index);

    // Método que las clases hijas DEBEN implementar para devolver su lista de verbos.
    protected abstract List<Verb> getVerbList();

    // --- Lógica del Menú (será heredada por todas las actividades hijas) ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú; esto añade los ítems a la barra de acción.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Maneja los clics en los ítems de la barra de acción
        if (item.getItemId() == R.id.action_go_to_verb) {
            showGoToVerbDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGoToVerbDialog() {
        // Infla el layout personalizado para el diálogo
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_go_to_verb, null);
        final EditText editTextVerbNumber = dialogView.findViewById(R.id.editTextVerbNumber);

        final List<Verb> verbList = getVerbList();
        if (verbList == null || verbList.isEmpty()) {
            Toast.makeText(this, "La lista de verbos no está disponible.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construye el AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Ir a Verbo")
                .setView(dialogView)
                .setPositiveButton("Ir", (dialog, which) -> {
                    String numberStr = editTextVerbNumber.getText().toString();
                    if (!numberStr.isEmpty()) {
                        try {
                            int verbNumber = Integer.parseInt(numberStr);
                            // El ID del verbo es el número, y el índice de la lista es número - 1
                            int targetIndex = verbNumber - 1;

                            if (targetIndex >= 0 && targetIndex < verbList.size()) {
                                // Llama al método abstracto que la actividad hija implementará
                                navigateTo(targetIndex);
                            } else {
                                Toast.makeText(this, "Número de verbo fuera de rango (1-" + verbList.size() + ")", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Por favor, ingresa un número válido.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
