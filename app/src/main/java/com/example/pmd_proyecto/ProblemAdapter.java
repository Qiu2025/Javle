package com.example.pmd_proyecto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pmd_proyecto.model.Problem;

import java.util.List;

public class ProblemAdapter extends BaseAdapter {
    Context ctx;
    List<Problem> datos;
    public ProblemAdapter(Context ctx, List<Problem> datos){
        this.ctx = ctx;
        this.datos = datos;
    }

    @Override
    public int getCount() {
        return (datos != null) ? datos.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.item_problem_layout, parent, false);
        }

        Problem p = datos.get(i);

        ((TextView) convertView.findViewById(R.id.tvName))
                .setText(p.getName());

        // Ej: "5C" o "5C • acmsguru"
        ((TextView) convertView.findViewById(R.id.tvCode))
                .setText(p.getContestId() + "" + p.getIndex() +
                        (p.getProblemsetName() != null ? " • " + p.getProblemsetName() : ""));

        ((TextView) convertView.findViewById(R.id.tvRating))
                .setText(p.getRating() + "");

        ((TextView) convertView.findViewById(R.id.tvType))
                .setText(p.getType());

        // tags como texto: "implementation • math"
        ((TextView) convertView.findViewById(R.id.tvTags))
                .setText(p.getTags() != null ? String.join(" • ", p.getTags()) : "");

        return convertView;
    }

}
