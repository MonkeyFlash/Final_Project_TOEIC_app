package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewProfile = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView tvFullName = viewProfile.findViewById(R.id.tvFullName);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tvFullName.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        TextView menuLogout = viewProfile.findViewById(R.id.menuLogout);

        //logout
        menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(),
                        "Logout successful",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        });
        return viewProfile;
    }



}
