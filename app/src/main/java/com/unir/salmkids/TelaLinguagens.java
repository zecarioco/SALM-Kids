package com.unir.salmkids;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class TelaLinguagens extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_linguagens);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setNavigationBarColor(getResources().getColor(R.color.yellow)); // Defina a cor desejada
        }

        ListView listView = findViewById(R.id.listViewLinguagens);
        listView.setDivider(null);
        String[] linguagens = {"pt", "en", "es", "fr", "de"};
        String[] traducoes = {"Português", "Inglês", "Espanhol", "Francês", "Alemão"};
        int[] imagens = {R.drawable.portugues, R.drawable.ingles, R.drawable.espanhol, R.drawable.frances, R.drawable.alemao};

        CustomAdapter adapter = new CustomAdapter(this, traducoes, imagens);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = linguagens[position];

                // Pass the selected language to the next activity
                Intent intent = new Intent(TelaLinguagens.this, LicaoAudioPalavra.class);
                intent.putExtra("selected_language", selectedLanguage);  // Pass the language
                startActivity(intent);
            }
        });

        ImageButton buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonVoltar.setOnClickListener(v -> finish());
    }
}
