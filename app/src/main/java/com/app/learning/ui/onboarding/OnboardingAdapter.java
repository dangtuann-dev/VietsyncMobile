package com.app.learning.ui.onboarding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.vietsyncmobile.R;

import java.util.List;

/**
 * OnboardingAdapter is a RecyclerView adapter used by ViewPager2 to show onboarding pages.
 */
public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private final List<OnboardingPage> pages;

    public OnboardingAdapter(List<OnboardingPage> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(pages.get(position));
    }

    @Override
    public int getItemCount() {
        return pages != null ? pages.size() : 0;
    }

    static class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private final LottieAnimationView lottieView;
        private final TextView titleView;
        private final TextView subtitleView;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            lottieView = itemView.findViewById(R.id.image_onboarding);
            titleView = itemView.findViewById(R.id.text_title);
            subtitleView = itemView.findViewById(R.id.text_subtitle);
        }

        public void bind(OnboardingPage page) {
            lottieView.setAnimation(page.getLottieRawRes());
            lottieView.playAnimation();
            titleView.setText(page.getTitle());
            subtitleView.setText(page.getSubtitle());
        }
    }
}
