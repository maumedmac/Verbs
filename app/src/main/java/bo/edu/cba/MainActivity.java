package bo.edu.cba;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button vocabularyButton = findViewById(R.id.button);
        vocabularyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, VerbsActivity.class);
            startActivity(intent);
        });
        Button randomButton = findViewById(R.id.button2);
        randomButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RandomVerbActivity.class);
            startActivity(intent);
        });
        Button ListButton = findViewById(R.id.button3);
        ListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListVerbs.class);
            startActivity(intent);
        });
        Button PracticeButton = findViewById(R.id.button4);
        PracticeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PracticeActivity.class);
            startActivity(intent);
        });
        Button SentenseButton = findViewById(R.id.button5);
        SentenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SentenceActivity.class);
            startActivity(intent);
        });
        Button PracticeSentenseButton = findViewById(R.id.button6);
        PracticeSentenseButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PracticeSentencesActivity.class);
            startActivity(intent);
        });

            }
        }


