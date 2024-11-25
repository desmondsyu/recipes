package com.kexin.recipes.adapter;

import static android.text.TextUtils.indexOf;

import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kexin.recipes.R;
import com.kexin.recipes.models.Ingredient;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private final List<Ingredient> ingredientList;

    public IngredientAdapter(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private EditText tvQuantity;
        private Spinner spUnit;
        private EditText etIngredient;

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
        }

        public void bind(Ingredient ingredient) {
            tvQuantity.setText(String.valueOf(ingredient.getQuantity()));
            etIngredient.setText(ingredient.getName());

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spUnit.getAdapter();
            int position = adapter.getPosition(ingredient.getUnit());
            if (position >= 0) {
                spUnit.setSelection(position);
            }

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
        holder.bind(ingredient);
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
