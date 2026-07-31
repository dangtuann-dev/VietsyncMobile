package com.app.learning.data.api;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.repository.AuthRepository;
import com.app.learning.utils.AppConstants;
import com.app.learning.utils.SessionManager;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class TokenAuthenticator implements Authenticator {

    private final Context context;
    private AuthRepository authRepository;
    private final SessionManager sessionManager;

    public TokenAuthenticator(Context context) {
        this.context = context.getApplicationContext();
        this.sessionManager = SessionManager.getInstance(this.context);
    }

    private AuthRepository getAuthRepository() {
        if (authRepository == null) {
            authRepository = new AuthRepository(context);
        }
        return authRepository;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        // Prevent infinite loops if the refresh token API itself returns 401
        if (response.request().url().encodedPath().contains("auth/v1/token")) {
            sessionManager.clearSession();
            return null; 
        }

        synchronized (this) {
            // Check if another thread already refreshed the token
            String currentToken = sessionManager.getAccessToken();
            String requestToken = getHeaderToken(response.request());

            if (currentToken != null && !currentToken.equals(requestToken)) {
                return response.request().newBuilder()
                        .header(AppConstants.HEADER_AUTHORIZATION, AppConstants.HEADER_BEARER_PREFIX + currentToken)
                        .build();
            }

            try {
                // Try to refresh token synchronously
                AuthResponse newAuthResponse = getAuthRepository().refreshTokenSync();
                if (newAuthResponse != null && newAuthResponse.getAccessToken() != null) {
                    return response.request().newBuilder()
                            .header(AppConstants.HEADER_AUTHORIZATION, AppConstants.HEADER_BEARER_PREFIX + newAuthResponse.getAccessToken())
                            .build();
                } else {
                    // Refresh failed, clear session
                    sessionManager.clearSession();
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                sessionManager.clearSession();
                return null;
            }
        }
    }

    private String getHeaderToken(Request request) {
        String header = request.header(AppConstants.HEADER_AUTHORIZATION);
        if (header != null && header.startsWith(AppConstants.HEADER_BEARER_PREFIX)) {
            return header.substring(AppConstants.HEADER_BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
