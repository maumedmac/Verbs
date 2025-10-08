package bo.edu.cba;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PracticeSentencesActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private List<Verb> verbList;
    private Random random = new Random();

    // Vistas
    private TextView textViewSentence;
    private Button buttonOption1, buttonOption2, buttonOption3;
    private Button buttonNextExercise;

    // TTS
    private TextToSpeech tts;
    private boolean isTtsInitialized = false;

    // Variables de estado del ejercicio
    private String correctVerbForm;
    private String fullSentence;
    private String fullSentenceSpanish; // <<< AÑADIDO: Para guardar la traducción

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_sentences);

        initializeViews();
        initializeTextToSpeech();
        loadVerbs();

        if (verbList.isEmpty()) {
            Toast.makeText(this, "No se encontraron verbos con oraciones.", Toast.LENGTH_LONG).show();
        } else {
            generateNewExercise();
            setupListeners();
        }
    }

    private void initializeViews() {
        textViewSentence = findViewById(R.id.textViewSentence);
        buttonOption1 = findViewById(R.id.buttonOption1);
        buttonOption2 = findViewById(R.id.buttonOption2);
        buttonOption3 = findViewById(R.id.buttonOption3);
        buttonNextExercise = findViewById(R.id.buttonNextExercise);
    }

    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, this);
    }

    private void loadVerbs() {
        List<Verb> allVerbs = VerbsList.getVerbsList();
        verbList = new ArrayList<>();
        // Filtramos para usar solo verbos que tengan las 3 oraciones y sus traducciones
        for (Verb verb : allVerbs) {
            if (verb.getBaseFormSentense() != null && !verb.getBaseFormSentense().isEmpty() &&
                    verb.getPastTenseSentense() != null && !verb.getPastTenseSentense().isEmpty() &&
                    verb.getPastParticipleSentense() != null && !verb.getPastParticipleSentense().isEmpty() &&
                    verb.getBaseFormSentenseEsp() != null && !verb.getBaseFormSentenseEsp().isEmpty()) { // Verificamos que al menos una traducción exista
                verbList.add(verb);
            }
        }
    }

    private void generateNewExercise() {
        buttonNextExercise.setVisibility(View.GONE);
        enableOptionButtons(true);
        resetButtonColors();

        Verb randomVerb = verbList.get(random.nextInt(verbList.size()));
        int sentenceType = random.nextInt(3);

        switch (sentenceType) {
            case 0:
                fullSentence = randomVerb.getBaseFormSentense();
                fullSentenceSpanish = randomVerb.getBaseFormSentenseEsp(); // <<< AÑADIDO
                correctVerbForm = randomVerb.getBaseForm();
                break;
            case 1:
                fullSentence = randomVerb.getPastTenseSentense();
                fullSentenceSpanish = randomVerb.getPastTenseSentenseEsp(); // <<< AÑADIDO
                correctVerbForm = randomVerb.getPastTense();
                break;
            case 2:
                fullSentence = randomVerb.getPastParticipleSentense();
                fullSentenceSpanish = randomVerb.getPastParticipleSentenseEsp(); // <<< AÑADIDO
                correctVerbForm = randomVerb.getPastParticiple();
                break;
        }

        String blank = "_________";
        String sentenceWithBlank = fullSentence.replaceAll("(?i)" + correctVerbForm, blank);
        textViewSentence.setText(sentenceWithBlank);

        List<String> options = new ArrayList<>();
        options.add(randomVerb.getBaseForm());
        options.add(randomVerb.getPastTense());
        options.add(randomVerb.getPastParticiple());
        Collections.shuffle(options);

        buttonOption1.setText(options.get(0));
        buttonOption2.setText(options.get(1));
        buttonOption3.setText(options.get(2));
    }

    private void setupListeners() {
        buttonOption1.setOnClickListener(v -> checkAnswer((Button) v));
        buttonOption2.setOnClickListener(v -> checkAnswer((Button) v));
        buttonOption3.setOnClickListener(v -> checkAnswer((Button) v));
        buttonNextExercise.setOnClickListener(v -> generateNewExercise());

        // <<< AÑADIDO: Listener para el TextView de la oración >>>
        textViewSentence.setOnClickListener(v -> toggleSentenceTranslation());
    }

    // <<< AÑADIDO: Nuevo método para alternar la traducción >>>
    private void toggleSentenceTranslation() {
        String blank = "_________";
        String currentText = textViewSentence.getText().toString();

        // Creamos las versiones con el espacio en blanco
        String sentenceWithBlankEnglish = fullSentence.replaceAll("(?i)" + correctVerbForm, blank);
        String sentenceWithBlankSpanish = fullSentenceSpanish.replaceAll("(?i)" + correctVerbForm, blank);

        // Si el texto actual es el inglés, lo cambiamos a español, y viceversa
        if (currentText.equals(sentenceWithBlankEnglish)) {
            textViewSentence.setText(sentenceWithBlankSpanish);
        } else {
            textViewSentence.setText(sentenceWithBlankEnglish);
        }
    }


    private void checkAnswer(Button selectedButton) {
        enableOptionButtons(false);
        String selectedAnswer = selectedButton.getText().toString();

        if (selectedAnswer.equalsIgnoreCase(correctVerbForm)) {
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light));
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            // <<< MODIFICADO: Mostramos la oración correcta completa antes de leerla >>>
            textViewSentence.setText(fullSentence);
            speak(fullSentence);
        } else {
            selectedButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show();
            speak("Wrong answer. Try again");
        }
        buttonNextExercise.setVisibility(View.VISIBLE);
    }

    private void enableOptionButtons(boolean isEnabled) {
        buttonOption1.setEnabled(isEnabled);
        buttonOption2.setEnabled(isEnabled);
        buttonOption3.setEnabled(isEnabled);
    }

    private void resetButtonColors() {
        // Usar un color de botón por defecto del tema puede ser más robusto
        buttonOption1.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
        buttonOption2.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
        buttonOption3.setBackgroundColor(ContextCompat.getColor(this, com.google.android.material.R.color.design_default_color_primary));
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "El idioma Inglés (US) no es soportado.");
            } else {
                isTtsInitialized = true;
            }
        } else {
            Log.e("TTS", "Inicialización de TTS fallida.");
        }
    }

    private void speak(String text) {
        if (isTtsInitialized && text != null && !text.isEmpty()) {
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
