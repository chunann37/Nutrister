package com.example.nutrister.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.nutrister.R;
import com.example.nutrister.Register;
import com.example.nutrister.UpdateProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.concurrent.Executor;

public class ProfileFragment extends Fragment {
    TextView username, weight, height, bmi, bmr,bmiStatus;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button profileBtn;

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = view.findViewById(R.id.text_profile);
        username = view.findViewById(R.id.profileName);
        weight = view.findViewById(R.id.profileWeight);
        height = view.findViewById(R.id.profileHeight);
        bmi = view.findViewById(R.id.profileBMI);
        bmr = view.findViewById(R.id.profileBMR);
        bmiStatus = view.findViewById(R.id.profileBMIstatus);
        profileBtn = view.findViewById(R.id.updateBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        //Retrieve Data
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                username.setText(documentSnapshot.getString("name"));
                weight.setText(documentSnapshot.getString("weightValue"));
                height.setText(documentSnapshot.getString("heightValue"));
                bmi.setText(documentSnapshot.getString("bmiValue"));
                bmiStatus.setText(documentSnapshot.getString("bmiStatus"));
                bmr.setText(documentSnapshot.getString("bmrValue"));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), UpdateProfile.class);
                startActivity(in);
            }
        });


        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return view;
    }

}