package com.app.learning.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocaleManager {

    public static final String LANGUAGE_VI = "vi";
    public static final String LANGUAGE_EN = "en";

    public static Context setLocale(Context context) {
        String lang = UserPreference.getInstance(context).getAppLanguage();
        return updateResources(context, lang);
    }

    public static Context setNewLocale(Context context, String language) {
        UserPreference.getInstance(context).setAppLanguage(language);
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    public static String formatCurrency(double amount, String language) {
        if (LANGUAGE_EN.equalsIgnoreCase(language)) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            return nf.format(amount / 25000.0); // Converted to USD
        } else {
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            return nf.format(amount);
        }
    }

    public static String formatDate(Date date, String language) {
        if (date == null) return "";
        if (LANGUAGE_EN.equalsIgnoreCase(language)) {
            return new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date);
        } else {
            return new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN")).format(date);
        }
    }
}
