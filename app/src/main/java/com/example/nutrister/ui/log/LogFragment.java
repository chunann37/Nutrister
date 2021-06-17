package com.example.nutrister.ui.log;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nutrister.R;
import com.example.nutrister.UpdateProfile;
import com.example.nutrister.utils.Accumulation;
import com.example.nutrister.utils.FoodAdapter;
import com.example.nutrister.utils.FoodItem;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class LogFragment extends Fragment {

    LogViewModel logViewModel;
    FoodAdapter adapterBf, adapterLc, adapterDn, adapterSn;
    Button addFood1, addFood2, addFood3, addFood4;
    String userID;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        logViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);
        View root = inflater.inflate(R.layout.fragment_log, container, false);


        userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        //Breakfast
        Query queryBf = db.collection("users_food_log").document(userID).collection("Breakfast");
        FirestoreRecyclerOptions<FoodItem> optionsBf = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(queryBf, FoodItem.class)
                .build();
        adapterBf = new FoodAdapter(optionsBf);
        RecyclerView recyclerBF = root.findViewById(R.id.breakfastView);
        recyclerBF.setLayoutManager(new LinearLayoutManager(LogFragment.this.getContext()));
        adapterBf.notifyDataSetChanged();
        recyclerBF.setAdapter(adapterBf);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapterBf.deleteItem(viewHolder.getAdapterPosition());
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapterBf.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerBF);

        //Lunch
        Query queryLc = db.collection("users_food_log").document(userID).collection("Lunch");
        FirestoreRecyclerOptions<FoodItem> optionsLc = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(queryLc, FoodItem.class)
                .build();
        adapterLc = new FoodAdapter(optionsLc);
        RecyclerView recyclerLc = root.findViewById(R.id.lunchView);
        recyclerLc.setLayoutManager(new LinearLayoutManager(LogFragment.this.getContext()));
        adapterLc.notifyDataSetChanged();
        recyclerLc.setAdapter(adapterLc);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapterLc.deleteItem(viewHolder.getAdapterPosition());
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapterLc.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerLc);

        //Dinner
        Query queryDn = db.collection("users_food_log").document(userID).collection("Dinner");
        FirestoreRecyclerOptions<FoodItem> optionsDn = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(queryDn, FoodItem.class)
                .build();
        adapterDn = new FoodAdapter(optionsDn);
        RecyclerView recyclerDn = root.findViewById(R.id.dinnerView);
        recyclerDn.setLayoutManager(new LinearLayoutManager(LogFragment.this.getContext()));
        adapterDn.notifyDataSetChanged();
        recyclerDn.setAdapter(adapterDn);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapterDn.deleteItem(viewHolder.getAdapterPosition());
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapterDn.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerDn);

        //Snack
        Query querySn = db.collection("users_food_log").document(userID).collection("Snack");
        FirestoreRecyclerOptions<FoodItem> optionsSn = new FirestoreRecyclerOptions.Builder<FoodItem>()
                .setQuery(querySn, FoodItem.class)
                .build();
        adapterSn = new FoodAdapter(optionsSn);
        RecyclerView recyclerSn = root.findViewById(R.id.snackView);
        recyclerSn.setLayoutManager(new LinearLayoutManager(LogFragment.this.getContext()));
        adapterSn.notifyDataSetChanged();
        recyclerSn.setAdapter(adapterSn);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapterSn.deleteItem(viewHolder.getAdapterPosition());
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapterSn.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                        .create()
                        .show();
            }
        }).attachToRecyclerView(recyclerSn);


        //AddFood Button
        this.addFood1 = root.findViewById(R.id.addBreakfast);
        this.addFood2 = root.findViewById(R.id.addLunch);
        this.addFood3 = root.findViewById(R.id.addDinner);
        this.addFood4 = root.findViewById(R.id.addSnack);
        this.addFood1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogFragment.this.getActivity(), LogToSearch.class);
                intent.putExtra("mealDefault", "Breakfast");
                startActivity(intent);
            }
        });
        this.addFood2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogFragment.this.getActivity(), LogToSearch.class);
                intent.putExtra("mealDefault", "Lunch");
                startActivity(intent);
            }
        });
        this.addFood3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogFragment.this.getActivity(), LogToSearch.class);
                intent.putExtra("mealDefault", "Dinner");
                startActivity(intent);
            }
        });
        this.addFood4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogFragment.this.getActivity(), LogToSearch.class);
                intent.putExtra("mealDefault", "Snack");
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterBf.notifyDataSetChanged();
        adapterBf.startListening();

        adapterLc.notifyDataSetChanged();
        adapterLc.startListening();

        adapterDn.notifyDataSetChanged();
        adapterDn.startListening();

        adapterSn.notifyDataSetChanged();
        adapterSn.startListening();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStop() {
        super.onStop();
        Accumulation accumulation = new Accumulation(userID, fAuth, db);
        accumulation.doCalculation();
        adapterBf.stopListening();
        adapterLc.stopListening();
        adapterDn.stopListening();
        adapterSn.stopListening();
    }
}

