package com.example.nutrister.ui.suggest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.nutrister.facts.Calcium;
import com.example.nutrister.facts.Magnesium;
import com.example.nutrister.facts.Potassium;
import com.example.nutrister.facts.VitaminA;
import com.example.nutrister.facts.VitaminB;
import com.example.nutrister.facts.VitaminC;
import com.example.nutrister.facts.VitaminD;
import com.example.nutrister.facts.VitaminE;
import com.example.nutrister.facts.VitaminK;
import com.example.nutrister.ui.log.LogFragment;
import com.example.nutrister.ui.log.LogToSearch;
import com.example.nutrister.utils.Suggestion;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

public class SuggestFragment extends Fragment {
    private TextView weightAdvice, bmrValue;
    private TextView title0, title1, title2, title3, title4, title5;
    private TextView advice0, advice1, advice2, advice3, advice4, advice5;
    private TextView diseaseList;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private Button mRefresh,mCalcium,mMagnesium,mPotassium,mVitaminA,mVitaminB,mVitaminC,mVitaminD,mVitaminE,mVitaminK;
    private LinkedList<String> diseaseArray = new LinkedList<>();

    private SuggestViewModel suggestViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        suggestViewModel =
                new ViewModelProvider(this).get(SuggestViewModel.class);
        View root = inflater.inflate(R.layout.fragment_suggest, container, false);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        weightAdvice = root.findViewById(R.id.weightSuggestion);
        bmrValue = root.findViewById(R.id.bmrValue);

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

        mCalcium = root.findViewById(R.id.calciumButton);
        mMagnesium = root.findViewById(R.id.magnesiumButton);
        mPotassium = root.findViewById(R.id.potButton);
        mVitaminA = root.findViewById(R.id.vitAButton);
        mVitaminB = root.findViewById(R.id.vitBButton);
        mVitaminC = root.findViewById(R.id.vitCButton);
        mVitaminD = root.findViewById(R.id.vitDButton);
        mVitaminE = root.findViewById(R.id.vitEButton);
        mVitaminK = root.findViewById(R.id.vitKButton);

        diseaseList = root.findViewById(R.id.diseaseList);

        //Retrieve Data advices
        DocumentReference documentReference = fStore.collection("user_advice").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                title0.setText(documentSnapshot.getString("title0"));
                checkTitle(title0);
                advice0.setText(documentSnapshot.getString("advice0"));
                title1.setText(documentSnapshot.getString("title1"));
                checkTitle(title1);
                advice1.setText(documentSnapshot.getString("advice1"));
                title2.setText(documentSnapshot.getString("title2"));
                checkTitle(title2);
                advice2.setText(documentSnapshot.getString("advice2"));
                title3.setText(documentSnapshot.getString("title3"));
                checkTitle(title3);
                advice3.setText(documentSnapshot.getString("advice3"));
                title4.setText(documentSnapshot.getString("title4"));
                checkTitle(title4);
                advice4.setText(documentSnapshot.getString("advice4"));
                title5.setText(documentSnapshot.getString("title5"));
                checkTitle(title5);
                advice5.setText(documentSnapshot.getString("advice5"));
            }
        });

        //Retrieve Data weight
        DocumentReference weightAdviceRef = fStore.collection("users").document(userID);
        weightAdviceRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                weightAdvice.setText(documentSnapshot.getString("bmiAdvice"));
                bmrValue.setText(documentSnapshot.getString("bmrValue"));
            }
        });

        mRefresh.setOnClickListener(new View.OnClickListener() {
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

        this.mCalcium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), Calcium.class));
            }
        });
        this.mMagnesium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), Magnesium.class));
            }
        });
        this.mPotassium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), Potassium.class));
            }
        });
        this.mVitaminA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), VitaminA.class));
            }
        });
        this.mVitaminB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), VitaminB.class));
            }
        });this.mVitaminC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), VitaminC.class));
            }
        });
        this.mVitaminD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), VitaminD.class));
            }
        });
        this.mVitaminE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), VitaminE.class));
            }
        });
        this.mVitaminK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuggestFragment.this.getActivity(), VitaminK.class));
            }
        });


        return root;
    }

    //create icon
    private void checkTitle(TextView title) {

        if (title.getText().toString().contains("Alcohol")) {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.whiskey_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
            diseaseArray.addAll(new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.alcohol_disease))));

        } else if (title.getText().toString().contains("Exercise")) {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jogging_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
            diseaseArray.addAll(new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.exercise_disease))));

        } else if (title.getText().toString().contains("Smoking")) {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tobacco_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
            diseaseArray.addAll(new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.smoke_disease))));

        } else if (title.getText().toString().contains("Blood Sugar")) {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sugar_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
            diseaseArray.addAll(new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.sugar_disease))));

        } else if (title.getText().toString().contains("Blood Pressure")) {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pressure_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
            diseaseArray.addAll(new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.pressure_disease))));

        } else if (title.getText().toString().contains("Cholesterol")) {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cholesterol_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
            diseaseArray.addAll(new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.cholesterol_disease))));

        } else {
            title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.healthcare_24, 0, 0, 0);
            title.setCompoundDrawablePadding(getActivity().getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
        }
        printDiseases(diseaseArray);
    }

    private void printDiseases(LinkedList<String> diseaseArray) {
        HashSet<String> hashSet = new HashSet<>(diseaseArray);
        diseaseList.setText(TextUtils.join("", hashSet));


    }
}