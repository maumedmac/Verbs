package bo.edu.cba; // Asegúrate de que este sea tu paquete

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech; // <<< --- IMPORTAR
import android.util.Log;                // <<< --- IMPORTAR
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;             // <<< --- IMPORTAR

// <<< --- IMPLEMENTAR TextToSpeech.OnInitListener
public class PracticeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private List<Verb> allVerbsList;
    private int currentIndex = 0;

    // Componentes de la UI
    private TextView textViewBaseForm;
    private EditText editTextPastTense;
    private EditText editTextPastParticiple;
    private Button buttonPrevious;
    private Button buttonCheck;
    private Button buttonNext;

    // Componentes de TTS
    private TextToSpeech tts; // <<< --- NUEVO
    private boolean isTtsInitialized = false; // <<< --- NUEVO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        // Inicializar vistas
        textViewBaseForm = findViewById(R.id.textViewBaseForm);
        editTextPastTense = findViewById(R.id.editTextPastTense);
        editTextPastParticiple = findViewById(R.id.editTextPastParticiple);
        buttonPrevious = findViewById(R.id.buttonPrevious);
        buttonCheck = findViewById(R.id.buttonCheck);
        buttonNext = findViewById(R.id.buttonNext);

        // Cargar la lista de verbos
        loadVerbs();

        // Configurar la actividad
        if (!allVerbsList.isEmpty()) {
            displayVerbChallenge(currentIndex);
            updateNavigationButtons();
        } else {
            Toast.makeText(this, "Lista de verbos vacía.", Toast.LENGTH_LONG).show();
            buttonPrevious.setEnabled(false);
            buttonCheck.setEnabled(false);
            buttonNext.setEnabled(false);
        }

        // --- Configurar Listeners ---
        buttonNext.setOnClickListener(v -> {
            if (currentIndex < allVerbsList.size() - 1) {
                currentIndex++;
                displayVerbChallenge(currentIndex);
                updateNavigationButtons();
            }
        });

        buttonPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayVerbChallenge(currentIndex);
                updateNavigationButtons();
            }
        });

        buttonCheck.setOnClickListener(v -> checkAnswers());

        // <<< --- INICIALIZAR TextToSpeech --- >>>
        tts = new TextToSpeech(this, this);
    }

    @Override
    protected void onDestroy() {
        // No olvides liberar los recursos de TTS
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void loadVerbs() {
        // Asume que tienes una clase VerbsList que te da la lista.
        allVerbsList = VerbsList.getVerbsList();
    }

    private void displayVerbChallenge(int index) {
        if (allVerbsList.isEmpty() || index < 0 || index >= allVerbsList.size()) return;
        Verb currentVerb = allVerbsList.get(index);
        textViewBaseForm.setText(currentVerb.getBaseForm());
        editTextPastTense.setText("");
        editTextPastParticiple.setText("");
        editTextPastTense.setBackgroundColor(Color.TRANSPARENT);
        editTextPastParticiple.setBackgroundColor(Color.TRANSPARENT);
    }

    private void updateNavigationButtons() {
        buttonPrevious.setEnabled(currentIndex > 0);
        buttonNext.setEnabled(currentIndex < allVerbsList.size() - 1);
    }

    private void checkAnswers() {
        if (allVerbsList.isEmpty() || currentIndex < 0 || currentIndex >= allVerbsList.size()) return;

        Verb correctVerb = allVerbsList.get(currentIndex);
        String pastTenseAnswer = editTextPastTense.getText().toString().trim();
        String pastParticipleAnswer = editTextPastParticiple.getText().toString().trim();

        // Comprobación flexible de las respuestas
        boolean isPastTenseCorrect = correctVerb.getPastTense().equalsIgnoreCase(pastTenseAnswer) ||
                correctVerb.getPastTense().toLowerCase().contains(pastTenseAnswer.toLowerCase());

        boolean isPastParticipleCorrect = correctVerb.getPastParticipie().equalsIgnoreCase(pastParticipleAnswer) ||
                correctVerb.getPastParticipie().toLowerCase().contains(pastParticipleAnswer.toLowerCase());

        editTextPastTense.setBackgroundColor(isPastTenseCorrect ? Color.parseColor("#A5D6A7") : Color.parseColor("#EF9A9A"));
        editTextPastParticiple.setBackgroundColor(isPastParticipleCorrect ? Color.parseColor("#A5D6A7") : Color.parseColor("#EF9A9A"));

        if (isPastTenseCorrect && isPastParticipleCorrect && !pastParticipleAnswer.isEmpty() && !pastTenseAnswer.isEmpty()) {
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();

            // <<< --- LÓGICA PARA LEER LAS 3 PALABRAS --- >>>
            if (isTtsInitialized) {
                String textoParaLeer = correctVerb.getBaseForm() + ",   " +
                        correctVerb.getPastTense() + ",  " +
                        correctVerb.getPastParticipie();
                speakOut(textoParaLeer);
            } else {
                Toast.makeText(this, "TTS no está listo.", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Algunas respuestas son incorrectas.", Toast.LENGTH_SHORT).show();
            if (isTtsInitialized)speakOut("Wrong answer. Try again");
        }
    }


    // <<< --- MÉTODOS DE TextToSpeech --- >>>

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Configura el idioma. Locale.US para inglés (para leer los verbos).
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "El idioma (Inglés) no es soportado.");
                Toast.makeText(this, "El paquete de voz en Inglés no está disponible.", Toast.LENGTH_SHORT).show();
            } else {
                isTtsInitialized = true;
                Log.i("TTS", "TextToSpeech inicializado correctamente.");
            }
        } else {
            Log.e("TTS", "Falló la inicialización de TextToSpeech!");
            Toast.makeText(this, "No se pudo iniciar el servicio de voz.", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakOut(String text) {
        if (isTtsInitialized && tts != null) {
            // Usa QUEUE_FLUSH para interrumpir cualquier lectura anterior y empezar la nueva.
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("TTS", "SpeakOut llamado pero TTS no está inicializado o es nulo.");
        }
    }
}
