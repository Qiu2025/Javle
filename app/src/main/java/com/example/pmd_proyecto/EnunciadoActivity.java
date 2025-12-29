package com.example.pmd_proyecto;

import android.os.Bundle;
import android.view.View;
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
        ((TextView) findViewById(R.id.tvTitle)).setText("Cargando...");
        ((TextView) findViewById(R.id.tvDifficulty)).setText("");
        ((TextView) findViewById(R.id.tvContent)).setText("");

        String id = getIntent().getStringExtra("ID");
        ConsultarEnunciadoThread task = new ConsultarEnunciadoThread(this,id);
        new Thread(task).start();

    }

    public void mostrarEnunciado(EnunciadoProblema enunciado) {

        if (enunciado == null || enunciado.getContent() == null) {
            Toast.makeText(this, "Error cargando el enunciado", Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.tvTitle)).setText("Error");
            ((TextView) findViewById(R.id.tvContent)).setText("No se pudo obtener el enunciado desde la API.");
            return;
        }

        ((TextView) findViewById(R.id.tvQuestionId))
                .setText("ID " + enunciado.getQuestionId());

        ((TextView) findViewById(R.id.tvTitle))
                .setText(enunciado.getTitle());

        ((TextView) findViewById(R.id.tvDifficulty))
                .setText(enunciado.getDifficulty());

        ((TextView) findViewById(R.id.tvCategory))
                .setText(enunciado.getCategoryTitle());

        TextView tvHints = findViewById(R.id.tvHints);
        TextView hintsTitulo = findViewById(R.id.hints);

        if (enunciado.getHints() != null) {
            hintsTitulo.setText("Hints: " + enunciado.getHints().size());
        }

//        Botón para mostrar siguiente hint
        Button btnHint = findViewById(R.id.btnHints);
        List<String> hints = enunciado.getHints();
        btnHint.setEnabled(!hints.isEmpty());
        hintIndex = 0;
        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hintIndex < hints.size()){
                    StringBuilder builder = new StringBuilder(tvHints.getText().toString());
                    if(hintIndex != 0) builder.append("\n\n");
                    builder.append("• Hint "+ (hintIndex+1) + ": " +hints.get(hintIndex));
                    tvHints.setText(builder.toString());
                    hintIndex++;
                    hintsTitulo.setText("Hints: " + (hints.size()-hintIndex));
                    if(hintIndex==hints.size()) btnHint.setEnabled(false);
                }
            }
        });

//        Botón para mostrar solución
        Button btnSolution = findViewById(R.id.btnSolution);
        if(enunciado.getSolution().getContent()==null) btnSolution.setEnabled(false);
        btnSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView tvSolution = findViewById(R.id.tvSolution);

                String html = null;
                if (enunciado.getSolution() != null) {
                    html = enunciado.getSolution().getContent();
                }
                if (html == null) html = "";

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tvSolution.setText(android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvSolution.setText(android.text.Html.fromHtml(html));
                }
                btnSolution.setEnabled(false);
            }
        });


        TextView tvTags = findViewById(R.id.tvTags);
        if (enunciado.getTopicTags() != null && !enunciado.getTopicTags().isEmpty()) {
            StringBuilder tags = new StringBuilder();
            for (int i = 0; i < enunciado.getTopicTags().size(); i++) {
                if (i > 0) tags.append(" • ");
                tags.append(enunciado.getTopicTags().get(i).getName());
            }
            tvTags.setText(tags.toString());
        } else {
            tvTags.setText("");
        }

        // HTML
        TextView tvContent = findViewById(R.id.tvContent);
        String html = enunciado.getContent();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvContent.setText(android.text.Html.fromHtml(html, android.text.Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvContent.setText(android.text.Html.fromHtml(html));
        }
    }
}