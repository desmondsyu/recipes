package com.kexin.recipes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kexin.recipes.R;
import com.kexin.recipes.models.Step;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {
    private final List<Step> stepList;
    private boolean isEditMode = false;

    public StepAdapter(List<Step> stepList) {
        this.stepList = stepList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStep;
        private EditText etDescription;
        private ImageButton btRemove;

        public ViewHolder(View view) {
            super(view);
            tvStep = view.findViewById(R.id.tv_step);
            etDescription = view.findViewById(R.id.et_description);
            btRemove = view.findViewById(R.id.btn_minus);
        }

        public void bind(Step step, boolean isEditMode) {
            int position = getAdapterPosition();
            tvStep.setText("Step " + (position + 1));
            etDescription.setText(step.getDescription());

            etDescription.setEnabled(isEditMode);
            btRemove.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

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
        holder.bind(step, isEditMode);
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

    public void setSteps(List<Step> steps) {
        stepList.clear();
        stepList.addAll(steps);
        notifyDataSetChanged();
    }
}
