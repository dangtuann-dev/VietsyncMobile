package com.app.learning.ui.gamification;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.AchievementModel;
import com.google.android.material.button.MaterialButton;

public class AchievementUnlockDialog extends Dialog {

    private AchievementModel achievement;

    public AchievementUnlockDialog(@NonNull Context context, AchievementModel achievement) {
        super(context);
        this.achievement = achievement;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_achievement_unlock);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        }

        TextView tvTitle = findViewById(R.id.tvAchievementTitle);
        TextView tvDesc = findViewById(R.id.tvAchievementDesc);
        ImageView imgBadge = findViewById(R.id.imgBadgeGlow);
        MaterialButton btnCelebrate = findViewById(R.id.btnCelebrate);

        if (achievement != null) {
            tvTitle.setText(achievement.getTitle());
            tvDesc.setText(achievement.getDescription());
        }

        // Scale & Glow animation for badge
        ScaleAnimation scale = new ScaleAnimation(
                0.3f, 1.0f, 0.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(600);
        scale.setFillAfter(true);
        imgBadge.startAnimation(scale);

        btnCelebrate.setOnClickListener(v -> dismiss());
    }
}
