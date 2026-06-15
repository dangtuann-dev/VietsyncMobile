package com.app.learning.data.api;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * AuthApi defines the Retrofit HTTP requests mapping directly to the
 * Supabase GoTrue Auth API routes (/auth/v1/signup and /auth/v1/token).
 */
public interface AuthApi {

    class SignInRequest {
        @SerializedName("email")
        private final String email;
        @SerializedName("password")
        private final String password;

        public SignInRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    class SignUpRequest {
        @SerializedName("email")
        private final String email;
        @SerializedName("password")
        private final String password;
        @SerializedName("data")
        private final UserMetadata data;

        public SignUpRequest(String email, String password, String fullName) {
            this.email = email;
            this.password = password;
            this.data = new UserMetadata(fullName);
        }

        private static class UserMetadata {
            @SerializedName("full_name")
            private final String fullName;

            public UserMetadata(String fullName) {
                this.fullName = fullName;
            }
        }
    }

    class AuthResponse {
        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("user")
        private AuthUser user;

        public String getAccessToken() {
            return accessToken;
        }

        public AuthUser getUser() {
            return user;
        }

        public static class AuthUser {
            @SerializedName("id")
            private String id;
            @SerializedName("email")
            private String email;
            @SerializedName("user_metadata")
            private UserMetadata userMetadata;

            public String getId() {
                return id;
            }

            public String getEmail() {
                return email;
            }

            public String getFullName() {
                return userMetadata != null ? userMetadata.fullName : "";
            }

            private static class UserMetadata {
                @SerializedName("full_name")
                private String fullName;
            }
        }
    }

    /**
     * Registers a new user with Supabase Auth.
     */
    @POST("auth/v1/signup")
    Call<AuthResponse> signUp(@Body SignUpRequest request);

    /**
     * Signs in an existing user and returns a JWT access token.
     */
    @POST("auth/v1/token?grant_type=password")
    Call<AuthResponse> signIn(@Body SignInRequest request);
}
