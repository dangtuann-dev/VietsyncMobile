package com.app.learning.ui.onboarding;




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
