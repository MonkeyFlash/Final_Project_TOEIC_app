package com.example.finalproject;

import android.app.AlertDialog;
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
        menuLogout.setOnClickListener(v -> {

            // Hiệu ứng nhấn
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .alpha(0.7f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .alpha(1f)
                                .setDuration(150)
                                .start();

                        // Dialog xác nhận
                       AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder
                                .setTitle("Logout")
                                .setMessage("Do you want to logout?")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Logout", (dialog, which) -> {

                                    FirebaseAuth.getInstance().signOut();

                                    Toast.makeText(getContext(),
                                            "Logout successful",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(requireActivity(), Login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .show();
                    })
                    .start();
        });
        return viewProfile;
    }



}
