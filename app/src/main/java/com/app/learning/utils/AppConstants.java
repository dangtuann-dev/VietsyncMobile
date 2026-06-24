package com.app.learning.utils;






public final class AppConstants {


    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }




    public static final String API_BASE_URL = "https://api.learningapp.com/v1/";
    public static final int CONNECT_TIMEOUT_SECONDS = 30;
    public static final int READ_TIMEOUT_SECONDS = 30;
    public static final int WRITE_TIMEOUT_SECONDS = 30;


    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER_PREFIX = "Bearer ";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_JSON = "application/json";




    public static final String PREF_NAME = "LearningAppPrefs";
    public static final String PREF_KEY_ACCESS_TOKEN = "key_access_token";
    public static final String PREF_KEY_REFRESH_TOKEN = "key_refresh_token";
    public static final String PREF_KEY_IS_LOGGED_IN = "key_is_logged_in";
    public static final String PREF_KEY_USER_ID = "key_user_id";
    public static final String PREF_KEY_USER_EMAIL = "key_user_email";
    public static final String PREF_KEY_USER_NAME = "key_user_name";
    public static final String PREF_KEY_USER_ROLE = "key_user_role";
    public static final String PREF_KEY_THEME_MODE = "key_theme_mode";




    public static final String EXTRA_COURSE_ID = "extra_course_id";
    public static final String EXTRA_COURSE_TITLE = "extra_course_title";
    public static final String EXTRA_LESSON_ID = "extra_lesson_id";
    public static final String EXTRA_LESSON_TITLE = "extra_lesson_title";
    public static final String EXTRA_VIDEO_URL = "extra_video_url";
    public static final String EXTRA_USER_PROFILE = "extra_user_profile";
    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";
    public static final String EXTRA_IS_COMPLETED = "extra_is_completed";




    public static final String DATABASE_NAME = "learning_database.db";
    public static final int DATABASE_VERSION = 1;




    public static final String ERROR_NETWORK_UNAVAILABLE = "Không có kết nối mạng. Vui lòng kiểm tra lại.";
    public static final String ERROR_TIMEOUT = "Kết nối quá hạn. Vui lòng thử lại sau.";
    public static final String ERROR_UNAUTHORIZED = "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.";
    public static final String ERROR_UNKNOWN = "Đã xảy ra lỗi không xác định. Vui lòng thử lại.";
    public static final String ERROR_SERVER = "Lỗi hệ thống. Chúng tôi đang khắc phục.";




    public static final int RC_SIGN_IN = 9001;
    public static final int RC_STORAGE_PERMISSION = 9002;
    public static final int RC_CAMERA_PERMISSION = 9003;


    public static final String STATUS_NOT_STARTED = "NOT_STARTED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
}
