package com.app.learning.data.api;

import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.model.UserModel;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * AuthApi defines the Retrofit HTTP requests mapping directly to the
 * Supabase GoTrue Auth API routes.
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
        private final Map<String, Object> data;

        public SignUpRequest(String email, String password, Map<String, Object> data) {
            this.email = email;
            this.password = password;
            this.data = data;
        }
    }

    class RefreshTokenRequest {
        @SerializedName("refresh_token")
        private final String refreshToken;

        public RefreshTokenRequest(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    class RecoverRequest {
        @SerializedName("email")
        private final String email;

        public RecoverRequest(String email) {
            this.email = email;
        }
    }

    class UpdateUserRequest {
        @SerializedName("password")
        private final String password;

        public UpdateUserRequest(String password) {
            this.password = password;
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

    /**
     * Signs out the user on the server (invalidates current session).
     * Requires Authorization Bearer header.
     */
    @POST("auth/v1/logout")
    Call<Void> logout(@Header("Authorization") String bearerToken);

    /**
     * Refreshes access token using refresh token.
     */
    @POST("auth/v1/token?grant_type=refresh_token")
    Call<AuthResponse> refreshToken(@Body RefreshTokenRequest request);

    /**
     * Retrieves current logged in user details.
     * Requires Authorization Bearer header.
     */
    @GET("auth/v1/user")
    Call<UserModel> getUser(@Header("Authorization") String bearerToken);

    /**
     * Sends password recovery email.
     */
    @POST("auth/v1/recover")
    Call<Void> recoverPassword(@Body RecoverRequest request);

    /**
     * Updates user details (such as resetting password).
     */
    @PUT("auth/v1/user")
    Call<UserModel> updateUser(@Header("Authorization") String bearerToken, @Body UpdateUserRequest request);
}
