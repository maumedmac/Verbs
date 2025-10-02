package bo.edu.cba; // Asegúrate de que este sea tu paquete

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Locale;

// El nombre de la clase ya no refleja la funcionalidad (no es aleatoria), pero la mantenemos por simplicidad.
public class ListVerbs extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private List<Verb> allVerbsList;
    private int currentIndex = 0; // <<< --- NUEVO: Índice para rastrear la posición actual

    private TextView textViewBaseForm;
    private TextView textViewPastTense;
    private TextView textViewPastParticiple;
    private TextView textViewDescription;
    private Button buttonNext; // <<< --- Renombrado
    private Button buttonPrevious; // <<< --- NUEVO
    private FloatingActionButton fabSpeakRandomVerb;

    private TextToSpeech tts;
    private boolean isTtsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_vebs);

        // Inicializar vistas
        textViewBaseForm = findViewById(R.id.textViewBaseForm);
        textViewPastTense = findViewById(R.id.textViewPastTense);
        textViewPastParticiple = findViewById(R.id.textViewPastParticiple);
        textViewDescription = findViewById(R.id.textViewDescription);
        buttonNext = findViewById(R.id.buttonNext); // <<< --- ID actualizado
        buttonPrevious = findViewById(R.id.buttonPrevious); // <<< --- ID actualizado
        fabSpeakRandomVerb = findViewById(R.id.fabSpeakRandomVerb);

        // Cargar la lista de verbos
        loadVerbs();

        // Mostrar el primer verbo si la lista no está vacía
        if (!allVerbsList.isEmpty()) {
            displayVerbAtIndex(currentIndex);
            updateNavigationButtons();
        } else {
            Toast.makeText(this, "Lista de verbos vacía.", Toast.LENGTH_LONG).show();
            // Deshabilitar todos los botones si no hay datos
            buttonNext.setEnabled(false);
            buttonPrevious.setEnabled(false);
            fabSpeakRandomVerb.setEnabled(false);
        }

        // Configurar listener para el botón "Siguiente"
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex < allVerbsList.size() - 1) {
                    currentIndex++;
                    displayVerbAtIndex(currentIndex);
                    updateNavigationButtons();
                }
            }
        });

        // Configurar listener para el botón "Anterior"
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex > 0) {
                    currentIndex--;
                    displayVerbAtIndex(currentIndex);
                    updateNavigationButtons();
                }
            }
        });

        // Configurar listener para el FAB de hablar
        fabSpeakRandomVerb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakCurrentVerb();
            }
        });

        // Inicializar TextToSpeech
        tts = new TextToSpeech(this, this);
    }

    private void loadVerbs() {
        allVerbsList = VerbsList.getVerbsList();
    }

    // <<< --- NUEVO: Muestra el verbo en el índice especificado
    private void displayVerbAtIndex(int index) {
        if (allVerbsList.isEmpty() || index < 0 || index >= allVerbsList.size()) return;

        Verb verb = allVerbsList.get(index);
        if (verb != null) {
            textViewBaseForm.setText(verb.getBaseForm());
            textViewPastTense.setText(verb.getPastTense());
            textViewPastParticiple.setText(verb.getPastParticipie());
            textViewDescription.setText(verb.getDescription());
        }
    }

    // <<< --- NUEVO: Habilita/deshabilita los botones de navegación
    private void updateNavigationButtons() {
        buttonPrevious.setEnabled(currentIndex > 0);
        buttonNext.setEnabled(currentIndex < allVerbsList.size() - 1);
    }

    private void speakCurrentVerb() {
        if (allVerbsList.isEmpty() || currentIndex < 0 || currentIndex >= allVerbsList.size()) {
            Toast.makeText(this, "Ningún verbo seleccionado para leer.", Toast.LENGTH_SHORT).show();
            return;
        }

        Verb currentVerb = allVerbsList.get(currentIndex);
        if (isTtsInitialized) {
            String textoParaLeer = currentVerb.getBaseForm() + ",   " +
                    currentVerb.getPastTense() + ",  " +
                    currentVerb.getPastParticipie();
            speakOut(textoParaLeer);
        } else {
            Toast.makeText(this, "Text-to-Speech no está listo.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Métodos de TextToSpeech ---
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "El idioma especificado no es soportado.");
            } else {
                isTtsInitialized = true;
                fabSpeakRandomVerb.setEnabled(!allVerbsList.isEmpty()); // Habilitar si hay verbos
            }
        } else {
            Log.e("TTS", "Falló la inicialización de TextToSpeech!");
            fabSpeakRandomVerb.setEnabled(false);
        }
    }

    private void speakOut(String text) {
        if (isTtsInitialized && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
