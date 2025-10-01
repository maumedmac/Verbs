package bo.edu.cba;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech; // <<< --- IMPORTAR TTS
import android.util.Log;                // <<< --- IMPORTAR Log
import android.view.View;
import android.widget.AdapterView;
// ArrayAdapter no se usa directamente aquí si VerbAdapter está bien definido
// import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;             // <<< --- IMPORTAR Locale

//                                      <<< --- IMPLEMENTAR TextToSpeech.OnInitListener
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts; // <<< --- Instancia de TextToSpeech
    private boolean isTtsInitialized = false; // <<< --- Flag para saber si TTS está listo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Asegúrate que este es tu archivo de diseño

        ListView listView = findViewById(R.id.myListView);

        // 1. Crea tu lista de objetos
        List<Verb> miListaDeObjetos = new ArrayList<>();
        miListaDeObjetos.add(new Verb(1, "Be", "Was/Were", "Been", "ser o estar"));
        miListaDeObjetos.add(new Verb(2, "Beat", "Beat/Beats", "Beaten", "correr"));
        miListaDeObjetos.add(new Verb(3, "Become", "Became", "Become", "ser"));// Ejemplo
        miListaDeObjetos.add(new Verb(4, "Begin", "Began", "Begun", "iniciar"));
        miListaDeObjetos.add(new Verb(5, "Bend", "Bent", "Bent", "bendar"));
        miListaDeObjetos.add(new Verb(6, "Bet", "Bet", "Bet", "apostar"));
        miListaDeObjetos.add(new Verb(7, "Bite", "Bit", "Bitten", "morder"));
        miListaDeObjetos.add(new Verb(8, "Blow", "Blew", "Blown", "soplar/aterrizar"));
        miListaDeObjetos.add(new Verb(9, "Break", "Broke", "Broken", "romper"));
        miListaDeObjetos.add(new Verb(10,"Bring", "Brought", "Brought", "llevar"));
        miListaDeObjetos.add(new Verb(11,"Burn", "Burnt", "Burnt", "quemar"));
        miListaDeObjetos.add(new Verb(12,"Build", "Built", "Built", "construir"));
        miListaDeObjetos.add(new Verb(13,"Buy", "Bought", "Bought", "comprar"));
        miListaDeObjetos.add(new Verb(14,"Catch", "Caught", "Caught", "atrapar"));
        miListaDeObjetos.add(new Verb(15,"Choose", "Chose", "Chosen", "elegir"));
        miListaDeObjetos.add(new Verb(16,"Come", "Came", "Come", "venir"));
        miListaDeObjetos.add(new Verb(17,"Cost", "Cost", "Cost", "costar"));
        miListaDeObjetos.add(new Verb(18,"Cut", "Cut", "Cut", "cortar"));
        miListaDeObjetos.add(new Verb(19,"Deal", "Dealt", "Dealt", "negociar"));
        miListaDeObjetos.add(new Verb(20,"Dig", "Dug", "Dug", "escavar"));
        miListaDeObjetos.add(new Verb(21,"Do", "Did", "Done", "hacer"));

        // ... añade más objetos

        // 2. Crea el ArrayAdapter
        // Si usas un diseño de elemento personalizado (list_item.xml):
        VerbAdapter adapter = new VerbAdapter(this, miListaDeObjetos);

        // Si quieres usar un diseño simple predefinido por Android (solo mostrará el resultado de toString()):
        // ArrayAdapter<Verb> adapter = new ArrayAdapter<>( // Cambiado MiObjeto a Verb
        //         this,
        //         android.R.layout.simple_list_item_1, // Diseño predefinido
        //         miListaDeObjetos                    // Tu lista de objetos
        // );

        // 3. Asigna el adaptador al ListView
        listView.setAdapter(adapter);

        // <<< --- INICIALIZA TextToSpeech --- >>>
        tts = new TextToSpeech(this, this);

        // 4. (Opcional) Configura un listener para los clics en los elementos
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Verb objetoSeleccionado = (Verb) parent.getItemAtPosition(position);

                if (objetoSeleccionado != null) { // Buena práctica verificar nulidad
                    Toast.makeText(
                            getApplicationContext(),
                            objetoSeleccionado.getDescription().toUpperCase(),
                            Toast.LENGTH_SHORT
                    ).show();

                    // <<< --- LÓGICA PARA LEER CON TTS --- >>>
                    if (isTtsInitialized) {
                        String textoParaLeer = objetoSeleccionado.getBaseForm() +",   "+
                                objetoSeleccionado.getPastTense() +",  "+
                                objetoSeleccionado.getPastParticipie();
                        // Opcional: añadir la descripción a la lectura
                        // textoParaLeer += ". Meaning: " + objetoSeleccionado.getDescription();
                        speakOut(textoParaLeer);
                    } else {
                        Toast.makeText(MainActivity.this, "Text-to-Speech no está listo.", Toast.LENGTH_SHORT).show();
                    }
                    // Aquí puedes hacer algo más con el objetoSeleccionado si es necesario
                }
            }
        });
    }

    // <<< --- MÉTODOS DE TextToSpeech --- >>>
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Configura el idioma. Locale.US para inglés.
            // Para español: new Locale("es", "ES") o new Locale("es", "MX")
            // Locale.getDefault() para el idioma del dispositivo.
            int result = tts.setLanguage(Locale.US); // Configurado para inglés por defecto

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "El idioma especificado no es soportado por el motor de TTS.");
                Toast.makeText(this, "El idioma para TTS no es soportado.", Toast.LENGTH_SHORT).show();
            } else {
                isTtsInitialized = true;
                Log.i("TTS", "TextToSpeech inicializado correctamente.");
            }
        } else {
            Log.e("TTS", "Falló la inicialización de TextToSpeech!");
            Toast.makeText(this, "No se pudo iniciar Text-to-Speech.", Toast.LENGTH_SHORT).show();
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
        // No olvides liberar los recursos de TTS cuando la actividad se destruya
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            Log.d("TTS", "TextToSpeech detenido y liberado.");
        }
        super.onDestroy();
    }

    // Asumo que tienes tu clase Verb y VerbAdapter definidas correctamente en alguna parte de tu proyecto.
    // Si la clase Verb está anidada dentro de MainActivity y no es estática, podría dar problemas
    // al ser instanciada desde el adaptador si este no es una clase interna también.
    // Lo ideal es que Verb y VerbAdapter estén en sus propios archivos .java.
    //
    // Por referencia, una posible estructura para Verb si está en su propio archivo (Verb.java):
    /*
    package bo.edu.cba; // o el paquete donde la coloques

    public class Verb {
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

        public int getId() { return id; }
        public String getBaseForm() { return baseForm; }
        public String getPastTense() { return pastTense; }
        public String getPastParticiple() { return pastParticiple; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            // Esto sería usado por un ArrayAdapter simple, no por tu VerbAdapter personalizado
            return baseForm + " (" + description + ")";
        }
    }
    */
}

