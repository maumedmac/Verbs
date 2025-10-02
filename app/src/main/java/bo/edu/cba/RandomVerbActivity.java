package bo.edu.cba; // Asegúrate de que este sea tu paquete

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton; // Importar FAB

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random; // Importar Random

public class RandomVerbActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private List<Verb> allVerbsList;
    private Verb currentRandomVerb;
    private Random randomGenerator;

    private TextView textViewBaseForm;
    private TextView textViewPastTense;
    private TextView textViewPastParticiple;
    private TextView textViewDescription;
    private Button buttonNextVerb;
    private FloatingActionButton fabSpeakRandomVerb; // FAB para hablar

    private TextToSpeech tts;
    private boolean isTtsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que el nombre del layout coincida con el que creaste o modificaste
        setContentView(R.layout.activity_random_verb); // Por ejemplo, activity_random_verb.xml

        // Inicializar vistas
        textViewBaseForm = findViewById(R.id.textViewBaseForm);
        textViewPastTense = findViewById(R.id.textViewPastTense);
        textViewPastParticiple = findViewById(R.id.textViewPastParticiple);
        textViewDescription = findViewById(R.id.textViewDescription);
        buttonNextVerb = findViewById(R.id.buttonNextVerb);
        fabSpeakRandomVerb = findViewById(R.id.fabSpeakRandomVerb); // Inicializar FAB

        // Inicializar generador aleatorio
        randomGenerator = new Random();

        // Cargar la lista de verbos (puedes obtenerla de tu clase VerbsList o definirla aquí)
        loadVerbs();

        // Mostrar el primer verbo aleatorio si la lista no está vacía
        if (!allVerbsList.isEmpty()) {
            selectAndDisplayRandomVerb();
        } else {
            Toast.makeText(this, "Lista de verbos vacía.", Toast.LENGTH_LONG).show();
            // Podrías deshabilitar el botón aquí o mostrar un mensaje más prominente
            buttonNextVerb.setEnabled(false);
            fabSpeakRandomVerb.setEnabled(false);
        }

        // Configurar listener para el botón
        buttonNextVerb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allVerbsList.isEmpty()) {
                    selectAndDisplayRandomVerb();
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

    private void selectAndDisplayRandomVerb() {
        if (allVerbsList.isEmpty()) return;

        int randomIndex = randomGenerator.nextInt(allVerbsList.size());
        currentRandomVerb = allVerbsList.get(randomIndex);
        displayVerb(currentRandomVerb);
    }

    private void displayVerb(Verb verb) {
        if (verb != null) {
            textViewBaseForm.setText(verb.getBaseForm());
            textViewPastTense.setText(verb.getPastTense());
            textViewPastParticiple.setText(verb.getPastParticipie());
            textViewDescription.setText(verb.getDescription());
        }
    }

    private void speakCurrentVerb() {
        if (currentRandomVerb == null) {
            Toast.makeText(this, "Ningún verbo seleccionado para leer.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTtsInitialized) {
            String textoParaLeer = currentRandomVerb.getBaseForm() + ",   " +
                    currentRandomVerb.getPastTense() + ",  " +
                    currentRandomVerb.getPastParticipie(); // Quitada la "i" extra
            // Opcional: añadir la descripción a la lectura
            // textoParaLeer += ". Significado: " + currentRandomVerb.getDescription();
            speakOut(textoParaLeer);
        } else {
            Toast.makeText(this, "Text-to-Speech no está listo.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Métodos de TextToSpeech ---
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US); // Configurado para inglés por defecto
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "El idioma especificado no es soportado.");
                Toast.makeText(this, "El idioma para TTS no es soportado.", Toast.LENGTH_SHORT).show();
            } else {
                isTtsInitialized = true;
                fabSpeakRandomVerb.setEnabled(true); // Habilitar el FAB de hablar
                Log.i("TTS", "TextToSpeech inicializado correctamente.");
            }
        } else {
            Log.e("TTS", "Falló la inicialización de TextToSpeech!");
            Toast.makeText(this, "No se pudo iniciar Text-to-Speech.", Toast.LENGTH_SHORT).show();
            fabSpeakRandomVerb.setEnabled(false);
        }
    }

    private void speakOut(String text) {
        if (isTtsInitialized && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("TTS", "SpeakOut llamado pero TTS no está inicializado o es nulo.");
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

    // Asegúrate de tener tu clase Verb definida (puede estar en su propio archivo Verb.java)
    // Ejemplo de la clase Verb:
    /*
    public static class Verb {
        private int id;
        private String baseForm;
        private String pastTense;
        private String pastParticiple;
        private String description;

        public Verb(int id, String baseForm, String pastTense, String pastParticiple, String description) {
            this.id = id;
            this.baseForm = baseForm;
            this.pastTense = pastTense;
            this.pastParticiple = pastParticiple;
            this.description = description;
        }

        public String getBaseForm() { return baseForm; }
        public String getPastTense() { return pastTense; }
        public String getPastParticiple() { return pastParticiple; }
        public String getDescription() { return description; }
        // ... otros getters si son necesarios
    }
    */
}
