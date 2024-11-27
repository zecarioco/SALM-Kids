package com.unir.salmkids;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TelaInicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button botaoIniciar = findViewById(R.id.buttonIniciar);
        botaoIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicial.this, TelaLinguagens.class);
            startActivity(intent);
        });

        Button botaoConfiguracoes = findViewById(R.id.buttonConfiguracoes);
        botaoConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent(TelaInicial.this, TelaConfig.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}