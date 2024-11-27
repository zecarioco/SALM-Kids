package com.unir.salmkids;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.nl.translate.Translation;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;

public class LicaoAudioPalavra extends AppCompatActivity {
    private Translator translator;
    private TextToSpeech textToSpeech;
    private RequestQueue requestQueue;
    private List<Word> wordList, easyWords, mediumWords, hardWords;
    private List<Word> currentWordList;
    private int currentWordIndex = 0;
    private String currentWord;
    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licaoaudiopalavra);

        requestQueue = Volley.newRequestQueue(this);
        EditText editTextAnswer = findViewById(R.id.editTextAnswer);

        String difficulty = getSharedPreferences("dificuldade", MODE_PRIVATE).getString("dificuldade", "facil");
        loadWordsBasedOnDifficulty(difficulty);

        Intent intent = getIntent();
        selectedLanguage = intent.getStringExtra("selected_language");

        initializeTranslator(selectedLanguage);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.forLanguageTag(selectedLanguage));
            }
        });

        ImageButton buttonPlayAudio = findViewById(R.id.buttonPlayAudio);
        buttonPlayAudio.setOnClickListener(v -> {
            categorizeWords();
            Log.d("TAM_LISTAS", "Easy: " + easyWords.size() + ", Medium: " + mediumWords.size() + ", Hard: " + hardWords.size());
            if (!currentWordList.isEmpty() && currentWordIndex < currentWordList.size()) {
                currentWord = currentWordList.get(currentWordIndex).word;
                textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                showAlert("Fim da lista", "Todas as palavras foram completadas!");
            }
        });

        Button buttonChecaResposta = findViewById(R.id.buttonChecaResposta);
        buttonChecaResposta.setOnClickListener(v -> {
            String userAnswer = editTextAnswer.getText().toString().trim();
            if (currentWord == null) {
                showAlert("Erro", "Nenhuma palavra foi reproduzida!");
                return;
            }
            if (userAnswer.equalsIgnoreCase(currentWord)) {
                showAlert("Correto!", "Parabéns!");
            } else {
                showAlert("Incorreto!", "A palavra correta era: " + currentWord);
            }
            currentWordIndex++;
            if (currentWordIndex >= currentWordList.size()) {
                showAlert("Fim da lição", "Você completou todas as palavras desta lição!");
            }
            editTextAnswer.setText("");
        });

        ImageButton buttonVoltarLicaoAudioPalavra = findViewById(R.id.buttonVoltarLicaoAudioPalavra);
        buttonVoltarLicaoAudioPalavra.setOnClickListener(v -> {
            finish();
        });
    }

    private void initializeTranslator(String languageCode) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(languageCode)
                .build();
        translator = Translation.getClient(options);
    }

    private void loadWordsBasedOnDifficulty(String difficulty) {
        wordList = new ArrayList<>();
        easyWords = new ArrayList<>();
        mediumWords = new ArrayList<>();
        hardWords = new ArrayList<>();
        currentWordList = new ArrayList<>();

        String urlEasy = "https://api.datamuse.com/words?rel_trg=animal&max=100&md=f";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, urlEasy, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject wordObject = response.getJSONObject(i);
                            String word = wordObject.getString("word");
                            int frequency = wordObject.optInt("score", 0);
                            String wordType = wordObject.optString("word_type", "noun");

                            if (isSingleWord(word)) {
                                translateWord(word, translatedWord -> {
                                    if (isSingleWord(translatedWord) && !isProperNoun(translatedWord)) {
                                        wordList.add(new Word(translatedWord, frequency, wordType));
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.e("API_ERROR", "Erro coletando informações.")
        );
        requestQueue.add(request);
    }


    private void translateWord(String word, OnTranslationCompleteListener listener) {
        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();

        RemoteModelManager modelManager = RemoteModelManager.getInstance();
        TranslateRemoteModel model = new TranslateRemoteModel.Builder(selectedLanguage).build();
        modelManager.download(model, conditions);

        modelManager.isModelDownloaded(model)
                .addOnSuccessListener(isDownloaded -> {
                    if (isDownloaded) {
                        Log.d("MLKit", "Model for " + selectedLanguage + " is downloaded.");
                    } else {
                        Log.d("MLKit", "Model for " + selectedLanguage + " is not downloaded.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MLKit", "Failed to check model status", e);
                });

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(aVoid -> {
                    translator.translate(word)
                            .addOnSuccessListener(translatedWord -> {
                                // Check if the translated word is in the desired language
                                detectLanguage(translatedWord, selectedLanguage, isMatch -> {
                                    if (isMatch) {
                                        listener.onComplete(translatedWord);
                                        Log.d("Translation", "Word translated and matched: " + translatedWord);
                                    } else {
                                        Log.d("Translation", "Word skipped (not in desired language): " + translatedWord);
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                Log.e("MLKit", "Translation error: ", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("MLKit", "Error downloading model: ", e);
                });
    }

    private void detectLanguage(String word, String expectedLanguageCode, OnLanguageCheckListener listener) {
        LanguageIdentifier languageIdentifier = LanguageIdentification.getClient(
                new LanguageIdentificationOptions.Builder()
                        .setConfidenceThreshold(0.5f)  // Ajuste conforme necessário
                        .build()
        );

        languageIdentifier.identifyLanguage(word)
                .addOnSuccessListener(languageCode -> {
                    if (languageCode.equalsIgnoreCase(expectedLanguageCode) || languageCode.equals("und")) {
                        listener.onMatch(true);  // Palavra aceita se idioma for desconhecido ou igual ao esperado
                    } else {
                        Log.d("Translation", "Word skipped (not in desired language): " + word);
                        listener.onMatch(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MLKit", "Language detection failed", e);
                    listener.onMatch(true);  // Falha na detecção: considere a palavra válida
                });
    }


    interface OnTranslationCompleteListener {
        void onComplete(String translatedText);
    }

    interface OnLanguageCheckListener {
        void onMatch(boolean isMatch);
    }


    private void categorizeWords() {
        Collections.sort(wordList, (w1, w2) -> Integer.compare(w2.frequency, w1.frequency));

        int totalWords = wordList.size();
        int easyThreshold = totalWords / 3;
        int mediumThreshold = 2 * totalWords / 3;

        easyWords = new ArrayList<>(wordList.subList(0, easyThreshold));
        mediumWords = new ArrayList<>(wordList.subList(easyThreshold, mediumThreshold));
        hardWords = new ArrayList<>(wordList.subList(mediumThreshold, totalWords));

        String difficulty = getSharedPreferences("dificuldade", MODE_PRIVATE).getString("dificuldade", "facil");
        switch (difficulty) {
            case "medio":
                currentWordList = mediumWords;
                break;
            case "dificil":
                currentWordList = hardWords;
                break;
            default:
                currentWordList = easyWords;
                break;
        }

        Log.d("Dificuldade", "Palavras carregadas: " + currentWordList.size());
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isSingleWord(String word) {
        return !word.contains(" ");
    }

    private boolean isProperNoun(String word){
        return Character.isUpperCase(word.charAt(0));
    }
}