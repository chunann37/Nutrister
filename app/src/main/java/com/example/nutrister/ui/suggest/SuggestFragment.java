package com.example.nutrister.ui.suggest;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
import com.example.nutrister.utils.Suggestion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SuggestFragment extends Fragment {
    private TextView title0, title1, title2, title3, title4, title5;
    private TextView advice0, advice1, advice2, advice3, advice4, advice5;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private Button mRefresh;

    private SuggestViewModel suggestViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        suggestViewModel =
                new ViewModelProvider(this).get(SuggestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_suggest, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        title0 = root.findViewById(R.id.suggestTitle0);
        title1 = root.findViewById(R.id.suggestTitle1);
        title2 = root.findViewById(R.id.suggestTitle2);
        title3 = root.findViewById(R.id.suggestTitle3);
        title4 = root.findViewById(R.id.suggestTitle4);
        title5 = root.findViewById(R.id.suggestTitle5);

        advice0 = root.findViewById(R.id.suggestAdvice0);
        advice1 = root.findViewById(R.id.suggestAdvice1);
        advice2 = root.findViewById(R.id.suggestAdvice2);
        advice3 = root.findViewById(R.id.suggestAdvice3);
        advice4 = root.findViewById(R.id.suggestAdvice4);
        advice5 = root.findViewById(R.id.suggestAdvice5);

        mRefresh = root.findViewById(R.id.refreshButton);

        //Retrieve Data
        DocumentReference documentReference = fStore.collection("user_advice").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                title0.setText(documentSnapshot.getString("title0"));
                advice0.setText(documentSnapshot.getString("advice0"));
                title1.setText(documentSnapshot.getString("title1"));
                advice1.setText(documentSnapshot.getString("advice1"));
                title2.setText(documentSnapshot.getString("title2"));
                advice2.setText(documentSnapshot.getString("advice2"));
                title3.setText(documentSnapshot.getString("title3"));
                advice3.setText(documentSnapshot.getString("advice3"));
                title4.setText(documentSnapshot.getString("title4"));
                advice4.setText(documentSnapshot.getString("advice4"));
                title5.setText(documentSnapshot.getString("title5"));
                advice5.setText(documentSnapshot.getString("advice5"));

            }
        });

        mRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = fStore.collection("users").document(userID);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String healthIndex2 = documentSnapshot.getString("healthIndex2");
                        String healthIndex1 = documentSnapshot.getString("healthIndex1");

                        //Generate suggestion
                        Suggestion suggestion = new Suggestion(healthIndex2, healthIndex1);
                        suggestion.generateSuggestion();

                        DocumentReference documentReference = fStore.collection("user_advice").document(userID);
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                title0.setText(documentSnapshot.getString("title0"));
                                advice0.setText(documentSnapshot.getString("advice0"));
                                title1.setText(documentSnapshot.getString("title1"));
                                advice1.setText(documentSnapshot.getString("advice1"));
                                title2.setText(documentSnapshot.getString("title2"));
                                advice2.setText(documentSnapshot.getString("advice2"));
                                title3.setText(documentSnapshot.getString("title3"));
                                advice3.setText(documentSnapshot.getString("advice3"));
                                title4.setText(documentSnapshot.getString("title4"));
                                advice4.setText(documentSnapshot.getString("advice4"));
                                title5.setText(documentSnapshot.getString("title5"));
                                advice5.setText(documentSnapshot.getString("advice5"));
                            }
                        });
                    }
                });
            }


        });



        return root;
    }
}