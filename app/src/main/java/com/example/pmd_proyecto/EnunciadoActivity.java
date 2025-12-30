package com.example.pmd_proyecto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pmd_proyecto.model.EnunciadoProblema;

import java.util.List;

public class EnunciadoActivity extends AppCompatActivity {
    private int hintIndex = 0;
    private WebView wvEnun;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enunciado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ((TextView) findViewById(R.id.tvQuestionId)).setText("");
        String cargando = "Cargando...";
        ((TextView) findViewById(R.id.tvEnunciado)).setText(cargando);
        ((TextView) findViewById(R.id.tvRespuestaCorrecta)).setText("");

        String id = getIntent().getStringExtra("ID");
        ConsultarEnunciadoThread task = new ConsultarEnunciadoThread(this,id);
        new Thread(task).start();


//      No permitir hacer scroll sobre el webview
        wvEnun = findViewById(R.id.wv_enunciado);
        wvEnun.setOnTouchListener((v, event) -> (event.getAction() == MotionEvent.ACTION_MOVE));

        android.webkit.WebSettings ws = wvEnun.getSettings();
        ws.setLayoutAlgorithm(android.webkit.WebSettings.LayoutAlgorithm.NORMAL);
        ws.setJavaScriptEnabled(false);

        wvEnun.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    public void mostrarEnunciado(EnunciadoProblema enunciado) {

        if (enunciado == null || enunciado.content == null) {
            Toast.makeText(this, "El enunciado no está disponible", Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.tvEnunciado)).setText("");
            return;
        }

        String id = "ID " + enunciado.questionFrontendId;
        ((TextView) findViewById(R.id.tvQuestionId)).setText(id);
        ((TextView) findViewById(R.id.tvEnunciado)).setText(enunciado.title);
        ((TextView) findViewById(R.id.tvRespuestaCorrecta)).setText(enunciado.difficulty);
        ((TextView) findViewById(R.id.tvCategory)).setText(enunciado.categoryTitle);

        TextView tvHints = findViewById(R.id.tvHints);
        TextView hintsTitulo = findViewById(R.id.hints);
        if (enunciado.hints != null) {
            String hints = "Hints: " + enunciado.hints.size();
            hintsTitulo.setText(hints);
        }

        TextView tvTags = findViewById(R.id.tvTags);
        if (enunciado.topicTags != null && !enunciado.topicTags.isEmpty()) {
            StringBuilder tags = new StringBuilder();
            for (int i = 0; i < enunciado.topicTags.size(); i++) {
                if (i > 0) tags.append(" • ");
                tags.append(enunciado.topicTags.get(i).name);
            }
            tvTags.setText(tags.toString());
        } else {
            tvTags.setText("");
        }

        CardView cardHints = findViewById(R.id.cardHints);
//        Botón para mostrar siguiente hint
        Button btnHint = findViewById(R.id.btnHints);
        List<String> hints = enunciado.hints;
        if (hints.isEmpty())  {
            cardHints.setVisibility(View.GONE);
        } else {
            btnHint.setEnabled(true);
            hintIndex = 0;
            btnHint.setOnClickListener(v -> {
                if(hintIndex < hints.size()){
                    StringBuilder builder = new StringBuilder(tvHints.getText().toString());
                    if(hintIndex != 0) builder.append("\n\n");
                    builder.append("• Hint ").append(hintIndex+1).append(": ").append(hints.get(hintIndex));
                    tvHints.setText(builder.toString());
                    hintIndex++;
                    String hintsString = "Hints: " + (hints.size()-hintIndex);
                    hintsTitulo.setText(hintsString);
                    if(hintIndex == hints.size()) btnHint.setEnabled(false);
                }
            });
        }

//        Usar WebView para mostrar el enunciado
        String html = enunciado.content;
        String wrappedHtml = "<html><head><style>" +
                "body { word-wrap: break-word; margin: 0; padding: 8px; font-family: sans-serif; }" +
                "pre, code { white-space: pre-wrap; word-break: break-all; }" +
                "img { max-width: 100%; height: auto; }" +
                "</style></head><body>" + html + "</body></html>";
        wvEnun.loadDataWithBaseURL(null, wrappedHtml, "text/html", "UTF-8", null);

//        Botón para abrir en web
        Button btnOpenWeb = findViewById(R.id.btnOpenWeb);
        btnOpenWeb.setOnClickListener(v -> {
            String url = enunciado.url;
            if (url != null && !url.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } else {
                Toast.makeText(this, "No hay enlace disponible", Toast.LENGTH_SHORT).show();
            }
        });

//        Botón para compartir URL
        Button btnShareUrl = findViewById(R.id.btnShareUrl);
        btnShareUrl.setOnClickListener(v -> {
            String url = enunciado.url;

            if (url == null) {
                Toast.makeText(this, "No hay URL para compartir", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, url);

            // Hacer que abra el panel aplicación a compartir
            Intent shareIntent = Intent.createChooser(i, "Compartir enlace");
            startActivity(shareIntent);
        });


    }
}