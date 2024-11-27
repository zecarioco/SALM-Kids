package com.unir.salmkids;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class TelaConfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_config);

        ImageButton buttonVoltar = findViewById(R.id.buttonVoltar);
        buttonVoltar.setOnClickListener(v -> {
            finish();
        });

        Button buttonFacil = findViewById(R.id.buttonFacil);
        Button buttonMedio = findViewById(R.id.buttonMedio);
        Button buttonDificil = findViewById(R.id.buttonDificil);

        String dificuldade = getSharedPreferences("dificuldade", MODE_PRIVATE).getString("dificuldade", "facil");
        setButtonColor(dificuldade);

        SharedPreferences prefs = getSharedPreferences("dificuldade", MODE_PRIVATE);
        buttonFacil.setOnClickListener(v -> {
            prefs.edit().putString("dificuldade", "facil").apply();
            setButtonColor("facil");
        });

        buttonMedio.setOnClickListener(v -> {
            prefs.edit().putString("dificuldade", "medio").apply();
            setButtonColor("medio");
        });

        buttonDificil.setOnClickListener(v -> {
            prefs.edit().putString("dificuldade", "dificil").apply();
            setButtonColor("dificil");
        });
    }

    private void setButtonColor(String dificuldade) {
        Button buttonFacil = findViewById(R.id.buttonFacil);
        Button buttonMedio = findViewById(R.id.buttonMedio);
        Button buttonDificil = findViewById(R.id.buttonDificil);
        switch (dificuldade) {
            case "facil":
                buttonFacil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
                buttonMedio.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                buttonDificil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                break;

            case "medio":
                buttonFacil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                buttonMedio.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
                buttonDificil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                break;

            case "dificil":
                buttonFacil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                buttonMedio.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                buttonDificil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
                break;

            default:
                buttonFacil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
                buttonMedio.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                buttonDificil.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue)));
                break;
        }
    }
}