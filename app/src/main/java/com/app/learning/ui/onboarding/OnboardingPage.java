package com.app.learning.ui.onboarding;

/**
 * OnboardingPage represents the model structure for each page of the onboarding screen.
 */
public class OnboardingPage {
    private final int lottieRawRes;
    private final String title;
    private final String subtitle;

    public OnboardingPage(int lottieRawRes, String title, String subtitle) {
        this.lottieRawRes = lottieRawRes;
        this.title = title;
        this.subtitle = subtitle;
    }

    public int getLottieRawRes() {
        return lottieRawRes;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
