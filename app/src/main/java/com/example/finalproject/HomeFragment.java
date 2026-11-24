package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class HomeFragment extends Fragment {
    ImageView imgAvatar;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    private ListenerRegistration avatarListener;

    // Đăng ký ActivityResultLauncher đúng chuẩn ở biến toàn cục để tránh crash khi recreate Fragment
    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imgAvatar.setImageURI(uri);
                    uploadAvatar(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        loadUserAvatarRealtime();
        setupCardClicks(view);

        return view;
    }

    private void initViews(View view) {
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        if (auth.getCurrentUser() != null && auth.getCurrentUser().getEmail() != null) {
            tvUserName.setText(auth.getCurrentUser().getEmail());
        }

        imgAvatar.setOnClickListener(v -> pickImage.launch("image/*"));
    }

    // Lắng nghe dữ liệu avatar từ Firestore theo thời gian thực (Realtime)
    private void loadUserAvatarRealtime() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        avatarListener = db.collection("users").document(uid)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) return;
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String avatarUrl = documentSnapshot.getString("avatar_url");
                        if (avatarUrl != null && !avatarUrl.isEmpty() && isAdded() && getContext() != null) {
                            Glide.with(requireContext())
                                    .load(avatarUrl)
                                    .into(imgAvatar);
                        }
                    }
                });
    }

    private void setupCardClicks(View view) {
        LinearLayout cardVocabulary = view.findViewById(R.id.cardVocabulary);
        LinearLayout cardGrammar = view.findViewById(R.id.cardGrammar);
        LinearLayout cardListening = view.findViewById(R.id.cardListening);
        LinearLayout cardReading = view.findViewById(R.id.cardReading);
        Button btnStartMockTest = view.findViewById(R.id.btnStartMockTest);

        cardVocabulary.setOnClickListener(v -> animateClick(v, () -> {
            startActivity(new Intent(requireActivity(), VocabTopic.class));
        }));

        cardGrammar.setOnClickListener(v -> animateClick(v, () -> {
            startActivity(new Intent(requireActivity(), GrammarTopic.class));
        }));

        cardListening.setOnClickListener(v -> animateClick(v, () -> {
            startActivity(new Intent(requireActivity(), ListeningList.class));
        }));

        cardReading.setOnClickListener(v -> animateClick(v, () -> {
            startActivity(new Intent(requireActivity(), ReadingList.class));
        }));

        btnStartMockTest.setOnClickListener(v -> animateClick(v, () -> {
            startActivity(new Intent(requireActivity(), TestList.class));
        }));
    }

    private void uploadAvatar(Uri uri) {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        if (getContext() != null) {
            Toast.makeText(getContext(), "Uploading avatar...", Toast.LENGTH_SHORT).show();
        }

        StorageReference avatarRef = storage.getReference()
                .child("avatars")
                .child(uid)
                .child("avatar.jpg");

        avatarRef.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return avatarRef.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    String imageUrl = downloadUri.toString();

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("avatar_url", imageUrl);

                    db.collection("users")
                            .document(uid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Avatar saved successfully!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Firestore error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    e.printStackTrace();
                });
    }

    private void animateClick(View view, Runnable action) {
        view.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(100)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction(action)
                        .start())
                .start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (avatarListener != null) {
            avatarListener.remove();
        }
    }
}
