package com.kexin.recipes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kexin.recipes.R;
import com.kexin.recipes.models.Step;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private final List<Step> stepList;

    public StepAdapter(List<Step> stepList) {
        this.stepList = stepList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStep;
        private EditText etDescription;

        public ViewHolder(View view) {
            super(view);
            tvStep = view.findViewById(R.id.tv_step);
            etDescription = view.findViewById(R.id.et_description);
        }

        public void bind(Step step) {
            int position = getAdapterPosition();
            tvStep.setText("Step " + (position + 1));
            etDescription.setText(step.getDescription());

            etDescription.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    step.setDescription(etDescription.getText().toString());
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.widget_step, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Step step = stepList.get(position);
        holder.bind(step);
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    public void addStep(Step step) {
        stepList.add(step);
        notifyItemInserted(stepList.size() - 1);
    }

    public void removeStep(int position) {
        stepList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, stepList.size() - position);
    }

    public List<Step> getSteps() {
        return stepList;
    }
}
