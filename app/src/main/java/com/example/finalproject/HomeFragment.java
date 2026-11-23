package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            tvUserName.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        LinearLayout cardVocabulary = view.findViewById(R.id.cardVocabulary);
        LinearLayout cardGrammar = view.findViewById(R.id.cardGrammar);
        LinearLayout cardListening = view.findViewById(R.id.cardListening);
        LinearLayout cardReading = view.findViewById(R.id.cardReading);
        Button btnStartMockTest = view.findViewById(R.id.btnStartMockTest);

        //Intent card
        cardVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), VocabTopic.class);
                startActivity(intent);
            }
        });
        cardGrammar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), GrammarTopic.class);
                startActivity(intent);
            }
        });

        cardListening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), ListeningList.class);
                startActivity(intent);
            }
        });

        cardReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), ReadingList.class);
                startActivity(intent);
            }
        });

        btnStartMockTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), TestList.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
