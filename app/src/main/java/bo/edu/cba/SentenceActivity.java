package bo.edu.cba;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Locale;

public class SentenceActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private List<Verb> verbList;
    private int currentIndex = 0;

    // --- Vistas de la UI ---
    // Textos
    private TextView textViewCurrentVerb;
    private TextView textViewBaseSentence, textViewPastSentence, textViewParticipleSentence;
    // Botones de acción
    private Button buttonListenBase, buttonTranslateBase;
    private Button buttonListenPast, buttonTranslatePast;
    private Button buttonListenParticiple, buttonTranslateParticiple;
    // Botones de navegación
    private Button buttonNext, buttonPrevious;

    // --- Motor de Text-to-Speech ---
    private TextToSpeech tts;
    private boolean isTtsInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);

        initializeViews();
        loadVerbs();
        setupListeners();
        initializeTextToSpeech();

        // Mostrar el primer verbo si la lista no está vacía
        if (!verbList.isEmpty()) {
            displayVerbAtIndex(currentIndex);
        } else {
            Toast.makeText(this, "La lista de verbos está vacía.", Toast.LENGTH_LONG).show();
            // Desactivar todos los botones si no hay datos
            findViewById(R.id.sentences_container).setVisibility(TextView.GONE);
            buttonNext.setEnabled(false);
            buttonPrevious.setEnabled(false);
        }
    }

    // Método para centralizar la inicialización de vistas
    private void initializeViews() {
        textViewCurrentVerb = findViewById(R.id.textViewCurrentVerb);
        // Oraciones
        textViewBaseSentence = findViewById(R.id.textViewBaseSentence);
        textViewPastSentence = findViewById(R.id.textViewPastSentence);
        textViewParticipleSentence = findViewById(R.id.textViewParticipleSentence);
        // Botones de Escuchar
        buttonListenBase = findViewById(R.id.buttonListenBase);
        buttonListenPast = findViewById(R.id.buttonListenPast);
        buttonListenParticiple = findViewById(R.id.buttonListenParticiple);
        // Botones de Traducir
        buttonTranslateBase = findViewById(R.id.buttonTranslateBase);
        buttonTranslatePast = findViewById(R.id.buttonTranslatePast);
        buttonTranslateParticiple = findViewById(R.id.buttonTranslateParticiple);
        // Botones de Navegación
        buttonNext = findViewById(R.id.buttonNext);
        buttonPrevious = findViewById(R.id.buttonPrevious);
    }

    // Carga los datos desde tu clase VerbsList
    private void loadVerbs() {
        verbList = VerbsList.getVerbsList();
    }

    // Inicializa el motor de voz
    private void initializeTextToSpeech() {
        tts = new TextToSpeech(this, this);
    }

    // Configura todos los OnClickListeners
    private void setupListeners() {
        // Navegación
        buttonNext.setOnClickListener(v -> navigate(1));
        buttonPrevious.setOnClickListener(v -> navigate(-1));

        // Listeners para ESCUCHAR
        buttonListenBase.setOnClickListener(v -> speak(verbList.get(currentIndex).getBaseFormSentense()));
        buttonListenPast.setOnClickListener(v -> speak(verbList.get(currentIndex).getPastTenseSentense()));
        buttonListenParticiple.setOnClickListener(v -> speak(verbList.get(currentIndex).getPastParticipleSentense()));

        // Listeners para TRADUCIR (alterna entre inglés y español)
        buttonTranslateBase.setOnClickListener(v -> toggleTranslation(textViewBaseSentence, currentIndex, "base"));
        buttonTranslatePast.setOnClickListener(v -> toggleTranslation(textViewPastSentence, currentIndex, "past"));
        buttonTranslateParticiple.setOnClickListener(v -> toggleTranslation(textViewParticipleSentence, currentIndex, "participle"));
    }

    // Muestra los datos del verbo en la posición del índice actual
    private void displayVerbAtIndex(int index) {
        Verb currentVerb = verbList.get(index);

        // Asegurarse de que el verbo y sus oraciones no sean nulos para evitar crasheos
        if (currentVerb.getBaseForm() != null)
            textViewCurrentVerb.setText("Verbo: " + currentVerb.getBaseForm());

        // Mostrar siempre la oración en inglés por defecto
        if (currentVerb.getBaseFormSentense() != null)
            textViewBaseSentence.setText(currentVerb.getBaseFormSentense());

        if (currentVerb.getPastTenseSentense() != null)
            textViewPastSentence.setText(currentVerb.getPastTenseSentense());

        if (currentVerb.getPastParticipleSentense() != null)
            textViewParticipleSentence.setText(currentVerb.getPastParticipleSentense());

        updateNavigationButtons();
    }

    // Lógica para cambiar entre la oración en inglés y español
    private void toggleTranslation(TextView textView, int index, String type) {
        Verb verb = verbList.get(index);
        String englishSentence = "";
        String spanishSentence = "";

        switch (type) {
            case "base":
                englishSentence = verb.getBaseFormSentense();
                spanishSentence = verb.getBaseFormSentenseEsp();
                break;
            case "past":
                englishSentence = verb.getPastTenseSentense();
                spanishSentence = verb.getPastTenseSentenseEsp();
                break;
            case "participle":
                englishSentence = verb.getPastParticipleSentense();
                spanishSentence = verb.getPastParticipleSentenseEsp();
                break;
        }

        // Si el texto actual es la oración en inglés, lo cambiamos a español, y viceversa
        if (textView.getText().toString().equals(englishSentence)) {
            textView.setText(spanishSentence);
        } else {
            textView.setText(englishSentence);
        }
    }

    // Controla la navegación hacia adelante y hacia atrás
    private void navigate(int direction) {
        int newIndex = currentIndex + direction;
        if (newIndex >= 0 && newIndex < verbList.size()) {
            currentIndex = newIndex;
            displayVerbAtIndex(currentIndex);
        }
    }

    // Habilita/deshabilita los botones de navegación
    private void updateNavigationButtons() {
        buttonPrevious.setEnabled(currentIndex > 0);
        buttonNext.setEnabled(currentIndex < verbList.size() - 1);
    }

    // --- Métodos de Text-to-Speech ---
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US); // Configurar a Inglés de EEUU
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Idioma no soportado.");
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
        } else {
            Toast.makeText(this, "No se puede leer el texto.", Toast.LENGTH_SHORT).show();
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
