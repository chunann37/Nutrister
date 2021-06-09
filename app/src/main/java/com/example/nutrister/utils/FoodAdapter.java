package com.example.nutrister.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrister.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.jetbrains.annotations.NotNull;

public class FoodAdapter extends FirestoreRecyclerAdapter<FoodItem, FoodAdapter.FoodHolder> {


    public FoodAdapter(@NonNull @NotNull FirestoreRecyclerOptions<FoodItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull @NotNull FoodHolder holder, int position, @NonNull @NotNull FoodItem model) {
        holder.foodLabel.setText(model.getFoodLabel());
        holder.foodServing.setText(model.getFoodServing());
        holder.foodEnergy.setText(model.getFoodEnergy());
    }

    @NonNull
    @NotNull
    @Override
    public FoodHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item,parent,false);
        return new FoodAdapter.FoodHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    static class FoodHolder extends RecyclerView.ViewHolder{
        TextView foodLabel;
        TextView foodServing;
        TextView foodEnergy;

        public FoodHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            foodLabel = itemView.findViewById(R.id.foodLabel_item);
            foodServing = itemView.findViewById(R.id.foodServing_item);
            foodEnergy = itemView.findViewById(R.id.foodEnergy_item);
        }
    }


}
