package com.app.learning.data.api;

import com.example.vietsyncmobile.BuildConfig;

import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.SupabaseClientBuilderKt;
import io.github.jan.supabase.gotrue.Auth;
import io.github.jan.supabase.postgrest.Postgrest;
import kotlin.Unit;





public final class SupabaseConfig {

    private static volatile SupabaseConfig instance;
    private final SupabaseClient supabaseClient;

    private SupabaseConfig() {

        String supabaseUrl = BuildConfig.SUPABASE_URL;
        String supabaseAnonKey = BuildConfig.SUPABASE_ANON_KEY;

        if (supabaseUrl == null || supabaseUrl.trim().isEmpty() || supabaseUrl.contains("your-project")) {
            throw new IllegalStateException("Supabase URL is not configured. Please check local.properties.");
        }
        if (supabaseAnonKey == null || supabaseAnonKey.trim().isEmpty() || supabaseAnonKey.contains("your-anon-public-key")) {
            throw new IllegalStateException("Supabase Anon Key is not configured. Please check local.properties.");
        }


        this.supabaseClient = SupabaseClientBuilderKt.createSupabaseClient(
                supabaseUrl,
                supabaseAnonKey,
                builder -> {

                    builder.install(Auth.Companion, authConfig -> Unit.INSTANCE);
                    builder.install(Postgrest.Companion, postgrestConfig -> Unit.INSTANCE);
                    return Unit.INSTANCE;
                }
        );
    }






    public static SupabaseConfig getInstance() {
        if (instance == null) {
            synchronized (SupabaseConfig.class) {
                if (instance == null) {
                    instance = new SupabaseConfig();
                }
            }
        }
        return instance;
    }






    public SupabaseClient getClient() {
        return supabaseClient;
    }
}
