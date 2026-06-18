package com.app.learning.data.api;

import com.example.vietsyncmobile.BuildConfig;

import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.SupabaseClientBuilderKt;
import io.github.jan.supabase.gotrue.Auth;
import io.github.jan.supabase.postgrest.Postgrest;
import kotlin.Unit;

/**
 * SupabaseConfig is a thread-safe singleton configuration manager for Supabase services.
 * It builds the official SupabaseClient using variables exposed via BuildConfig.
 */
public final class SupabaseConfig {

    private static volatile SupabaseConfig instance;
    private final SupabaseClient supabaseClient;

    private SupabaseConfig() {
        // Read URL and Key from BuildConfig safely
        String supabaseUrl = BuildConfig.SUPABASE_URL;
        String supabaseAnonKey = BuildConfig.SUPABASE_ANON_KEY;

        if (supabaseUrl == null || supabaseUrl.trim().isEmpty() || supabaseUrl.contains("your-project")) {
            throw new IllegalStateException("Supabase URL is not configured. Please check local.properties.");
        }
        if (supabaseAnonKey == null || supabaseAnonKey.trim().isEmpty() || supabaseAnonKey.contains("your-anon-public-key")) {
            throw new IllegalStateException("Supabase Anon Key is not configured. Please check local.properties.");
        }

        // Initialize Supabase Kotlin Client in Java
        this.supabaseClient = SupabaseClientBuilderKt.createSupabaseClient(
                supabaseUrl,
                supabaseAnonKey,
                builder -> {
                    // Install Auth and Postgrest plugins
                    builder.install(Auth.Companion, authConfig -> Unit.INSTANCE);
                    builder.install(Postgrest.Companion, postgrestConfig -> Unit.INSTANCE);
                    return Unit.INSTANCE;
                }
        );
    }

    /**
     * Retrieves the singleton configuration instance of SupabaseConfig.
     *
     * @return The singleton SupabaseConfig instance
     */
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

    /**
     * Exposes the configured SupabaseClient singleton instance.
     *
     * @return The configured SupabaseClient instance
     */
    public SupabaseClient getClient() {
        return supabaseClient;
    }
}
