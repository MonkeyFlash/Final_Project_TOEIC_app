
package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    LinearLayout navHome, navStats, navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top,
                    systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        if (!authCheck()) return;
        tabChange(savedInstanceState);
    }

    private void tabChange(Bundle savedInstanceState) {
        // Chỉ load Home lần đầu
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            updateNavSelection(navHome);
        }

        // Click Home
        navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavSelection(navHome);
        });

        // Click Stats
        navStats.setOnClickListener(v -> {
            loadFragment(new StatsFragment());
            updateNavSelection(navStats);
        });

        // Click Profile
        navProfile.setOnClickListener(v -> {
            loadFragment(new ProfileFragment());
            updateNavSelection(navProfile);
        });
    }

    private boolean authCheck() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return false;
        }
        return true;
    }


    // Chuyển Fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Cập nhật màu Nav khi chọn
    private void updateNavSelection(LinearLayout selectedNav) {
        // Reset tất cả tab
        resetNav(navHome);
        resetNav(navStats);
        resetNav(navProfile);

        // Tab được chọn
        selectedNav.setBackgroundResource(R.drawable.bg_nav_selected);
        setNavColor(selectedNav, "#1764CC");
    }
    private void resetNav(LinearLayout navLayout) {
        navLayout.setBackgroundColor(Color.TRANSPARENT);
        setNavColor(navLayout, "#DCEBFF");
    }
    private void setNavColor(LinearLayout navLayout, String hexColor) {
        int color = Color.parseColor(hexColor);

        TextView icon = (TextView) navLayout.getChildAt(0);
        TextView label = (TextView) navLayout.getChildAt(1);

        icon.setTextColor(color);
        label.setTextColor(color);
    }
    private void initView() {
        navHome = findViewById(R.id.navHome);
        navStats = findViewById(R.id.navStats);
        navProfile = findViewById(R.id.navProfile);

    }
}

