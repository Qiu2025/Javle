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

        ((TextView) convertView.findViewById(R.id.tvId))
                .setText("ID " + p.getId());

        ((TextView) convertView.findViewById(R.id.tvTitle))
                .setText(p.getTitle());

        ((TextView) convertView.findViewById(R.id.tvDifficulty))
                .setText(p.getDifficulty());

        return convertView;
    }
}