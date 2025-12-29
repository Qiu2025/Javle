package com.example.pmd_proyecto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pmd_proyecto.model.EnunciadoProblema;

import java.util.List;

public class EnunciadoActivity extends AppCompatActivity {
    private int hintIndex = 0;
    private WebView wvEnun;
    private WebView wvSol;

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
        ((TextView) findViewById(R.id.tvTitle)).setText(cargando);
        ((TextView) findViewById(R.id.tvDifficulty)).setText("");

        String id = getIntent().getStringExtra("ID");
        ConsultarEnunciadoThread task = new ConsultarEnunciadoThread(this,id);
        new Thread(task).start();


//        Permitir mover dentro de los webviews
        wvEnun = findViewById(R.id.wv_enunciado);
        wvEnun.setOnTouchListener((v, ev) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        wvSol = findViewById(R.id.wv_solution);
        wvSol.setVisibility(View.GONE); // Ocultar
        wvSol.setOnTouchListener((v, ev) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });


    }

    public void mostrarEnunciado(EnunciadoProblema enunciado) {

        if (enunciado == null || enunciado.content == null) {
            Toast.makeText(this, "Error cargando el enunciado", Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.tvTitle)).setText("");
            return;
        }

        String id = "ID " + enunciado.questionId;
        ((TextView) findViewById(R.id.tvQuestionId)).setText(id);
        ((TextView) findViewById(R.id.tvTitle)).setText(enunciado.title);
        ((TextView) findViewById(R.id.tvDifficulty)).setText(enunciado.difficulty);
        ((TextView) findViewById(R.id.tvCategory)).setText(enunciado.categoryTitle);

        TextView tvHints = findViewById(R.id.tvHints);
        TextView hintsTitulo = findViewById(R.id.hints);
        if (enunciado.hints != null) {
            String hints = "Hints: " + enunciado.hints.size();
            hintsTitulo.setText(hints);
        }

//        Botón para mostrar siguiente hint
        Button btnHint = findViewById(R.id.btnHints);
        List<String> hints = enunciado.hints;
        btnHint.setEnabled(!hints.isEmpty());
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
                if(hintIndex==hints.size()) btnHint.setEnabled(false);
            }
        });

//        Botón para mostrar solución
        Button btnSolution = findViewById(R.id.btnSolution);
        if(enunciado.solution.content==null) btnSolution.setEnabled(false);
        btnSolution.setOnClickListener(v -> {

            wvSol.setVisibility(View.VISIBLE);
            String html = "";
            if (enunciado.solution != null && enunciado.solution.content != null) {
                html = enunciado.solution.content;
            }
            String wrappedHtml =
                    "<!doctype html><html><head>" +
                            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                            "<style>body{font-family:sans-serif; padding:12px; line-height:1.4;}</style>" +
                            "</head><body>" +
                            html +
                            "</body></html>";
            wvSol.loadDataWithBaseURL(null, wrappedHtml, "text/html", "UTF-8", null);

            btnSolution.setEnabled(false);
        });


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

        String html = enunciado.content;
        String wrappedHtml =
                "<!doctype html><html><head>" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                        "<style>body{font-family:sans-serif; padding:12px; line-height:1.4;}</style>" +
                        "</head><body>" +
                        html +
                        "</body></html>";
        wvEnun.loadDataWithBaseURL(null, wrappedHtml, "text/html", "UTF-8", null);
    }
}