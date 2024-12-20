package com.kexin.recipes.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kexin.recipes.R;
import com.kexin.recipes.models.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private final List<Ingredient> ingredientList;
    private boolean isEditMode = false;

    public IngredientAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private EditText tvQuantity;
        private Spinner spUnit;
        private EditText etIngredient;
        private ImageButton btRemove;

        public ViewHolder(View view) {
            super(view);
            tvQuantity = view.findViewById(R.id.tv_quantity);
            spUnit = view.findViewById(R.id.sp_unit);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    view.getContext(),
                    R.array.unit_array,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spUnit.setAdapter(adapter);
            etIngredient = view.findViewById(R.id.et_ingredient);
            btRemove = view.findViewById(R.id.btn_minus);
        }

        public void bind(Ingredient ingredient, boolean isEditMode) {
            tvQuantity.setText(String.valueOf(ingredient.getQuantity()));
            etIngredient.setText(ingredient.getName());

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spUnit.getAdapter();
            int position = adapter.getPosition(ingredient.getUnit());
            if (position >= 0) {
                spUnit.setSelection(position);
            }

            tvQuantity.setEnabled(isEditMode);
            etIngredient.setEnabled(isEditMode);
            spUnit.setEnabled(isEditMode);
            btRemove.setVisibility(isEditMode ? View.VISIBLE : View.GONE);

            spUnit.post(() -> {
                TextView selectedView = (TextView) spUnit.getSelectedView();
                if (selectedView != null) {
                    int colorResId = isEditMode ? R.color.enabled_color : R.color.disabled_color;
                    selectedView.setTextColor(selectedView.getContext().getResources().getColor(colorResId));
                }
            });

            tvQuantity.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    ingredient.setQuantity(tvQuantity.getText().toString());
                }
            });

            etIngredient.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    ingredient.setName(etIngredient.getText().toString());
                }
            });

            spUnit.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parentView) {
                }

                @Override
                public void onItemSelected(android.widget.AdapterView<?> parentView, View view, int position, long id) {
                    ingredient.setUnit(spUnit.getSelectedItem().toString());
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.widget_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.bind(ingredient, isEditMode);
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientList.add(ingredient);
        notifyItemInserted(ingredientList.size() - 1);
    }

    public void removeIngredient(int position) {
        ingredientList.remove(position);
        notifyItemRemoved(position);
    }

    public List<Ingredient> getIngredients() {
        return ingredientList;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        ingredientList.clear();
        ingredientList.addAll(ingredients);
        notifyDataSetChanged();
    }
}
