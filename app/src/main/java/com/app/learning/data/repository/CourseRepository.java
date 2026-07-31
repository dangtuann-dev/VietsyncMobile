package com.app.learning.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.CourseApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Category;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import retrofit2.Call;

public class CourseRepository extends BaseRepository {

    private final CourseApi courseApi;

    public CourseRepository() {
        super();
        this.courseApi = ApiClient.getInstance().createService(CourseApi.class);
    }

    public CourseRepository(@NonNull Context context) {
        this();
    }

    public interface CourseListCallback {
        void onSuccess(List<Course> courseList);
        void onError(String errorMessage);
    }

    private static final List<Course> createdCourses = new ArrayList<>();

    public static void addCreatedCourse(Course course) {
        if (course != null) {
            createdCourses.add(0, course);
        }
    }

    public LiveData<Resource<List<Course>>> searchCourses(Map<String, String> options) {
        MutableLiveData<Resource<List<Course>>> resultLiveData = new MutableLiveData<>();
        Call<List<Course>> call = courseApi.searchCourses(options);

        MutableLiveData<Resource<List<Course>>> rawLiveData = new MutableLiveData<>();
        executeCall(call, rawLiveData);

        rawLiveData.observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(resource);
            } else if (resource == null || resource.status == Resource.Status.ERROR || resource.status == Resource.Status.SUCCESS) {
                List<Course> fallback = filterFallbackCourses(options);
                resultLiveData.setValue(Resource.success(fallback));
            }
        });
        return resultLiveData;
    }

    private List<Course> getAllKnownCourses() {
        List<Course> list = new ArrayList<>(createdCourses);

        Course c1 = new Course();
        c1.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001");
        c1.setTitle("Lập trình Android với Java (MVVM)");
        c1.setDescription("Khóa học từ cơ bản đến nâng cao về phát triển ứng dụng di động Android dùng Java, cấu trúc MVVM và tích hợp Supabase.");
        c1.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        c1.setCategoryId(1L);
        c1.setLevel("intermediate");
        c1.setDuration(1200);
        c1.setRating(4.85);
        c1.setPrice(0);
        list.add(c1);

        Course c2 = new Course();
        c2.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002");
        c2.setTitle("UI/UX Design chuyên nghiệp");
        c2.setDescription("Làm chủ thiết kế giao diện Figma, nghiên cứu người dùng và tối ưu trải nghiệm thiết kế cho Mobile & Web.");
        c2.setThumbnail("https://images.unsplash.com/photo-1561070791-26c113006238?w=400");
        c2.setCategoryId(3L);
        c2.setLevel("beginner");
        c2.setDuration(900);
        c2.setRating(4.90);
        c2.setPrice(0);
        list.add(c2);

        Course c3 = new Course();
        c3.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003");
        c3.setTitle("Lập trình Android nâng cao với Kotlin");
        c3.setDescription("Xây dựng ứng dụng Android hiện đại với Jetpack, Coroutines, Flow và Clean Architecture.");
        c3.setThumbnail("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400");
        c3.setCategoryId(1L);
        c3.setLevel("advanced");
        c3.setDuration(1500);
        c3.setRating(4.90);
        c3.setPrice(0);
        list.add(c3);

        Course c4 = new Course();
        c4.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380004");
        c4.setTitle("Tiếng Anh giao tiếp công sở & CNTT");
        c4.setDescription("Nâng cao khả năng giao tiếp tiếng Anh chuyên ngành công nghệ thông tin và môi trường doanh nghiệp.");
        c4.setThumbnail("https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400");
        c4.setCategoryId(4L);
        c4.setLevel("beginner");
        c4.setDuration(800);
        c4.setRating(4.75);
        c4.setPrice(0);
        list.add(c4);

        Course c5 = new Course();
        c5.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380005");
        c5.setTitle("Khởi nghiệp Thực chiến & Quản trị Kinh doanh");
        c5.setDescription("Kiến thức quản trị kinh doanh toàn diện, xây dựng mô hình kinh doanh và chiến lược phát triển.");
        c5.setThumbnail("https://images.unsplash.com/photo-1531403009284-440f080d1e12?w=400");
        c5.setCategoryId(2L);
        c5.setLevel("intermediate");
        c5.setDuration(1100);
        c5.setRating(4.80);
        c5.setPrice(0);
        list.add(c5);

        return list;
    }

    private List<Course> filterFallbackCourses(Map<String, String> options) {
        List<Course> all = getAllKnownCourses();
        if (options == null || options.isEmpty()) return all;

        List<Course> result = new ArrayList<>();

        String titleParam = options.get("title");
        String query = null;
        if (titleParam != null && titleParam.startsWith("ilike.*") && titleParam.endsWith("*")) {
            query = titleParam.substring(7, titleParam.length() - 1).toLowerCase().trim();
        }

        String catParam = options.get("category_id");
        Long targetCatId = null;
        if (catParam != null && catParam.startsWith("eq.")) {
            try { targetCatId = Long.parseLong(catParam.substring(3)); } catch (Exception ignored) {}
        }

        String levelParam = options.get("level");
        String targetLevel = null;
        if (levelParam != null && levelParam.startsWith("eq.")) {
            targetLevel = levelParam.substring(3).toLowerCase();
        }

        for (Course c : all) {
            boolean matches = true;

            if (query != null && !query.isEmpty()) {
                String fullText = (c.getTitle() + " " + c.getDescription()).toLowerCase();
                if (!fullText.contains(query)) matches = false;
            }

            if (targetCatId != null) {
                if (c.getCategoryId() == null || !c.getCategoryId().equals(targetCatId)) matches = false;
            }

            if (targetLevel != null) {
                if (c.getLevel() == null || !c.getLevel().toLowerCase().equals(targetLevel)) matches = false;
            }

            if (matches) {
                result.add(c);
            }
        }
        return result;
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        MutableLiveData<Resource<List<Category>>> resultLiveData = new MutableLiveData<>();
        Call<List<Category>> call = courseApi.getCategories("*");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<List<Course>>> getCoursesByCategory(String categoryId, String sortBy, int page) {
        MutableLiveData<Resource<List<Course>>> resultLiveData = new MutableLiveData<>();
        
        Map<String, String> options = new HashMap<>();
        options.put("select", "*,instructor:users(full_name)");
        
        String resolvedId = resolveCategoryId(categoryId);
        if (resolvedId != null) {
            options.put("category_id", "eq." + resolvedId);
        }
        
        String order = "enrolled_count.desc";
        if (sortBy != null) {
            if (sortBy.equals("Phổ biến") || sortBy.equalsIgnoreCase("Popular")) {
                order = "enrolled_count.desc";
            } else if (sortBy.equals("Mới nhất") || sortBy.equalsIgnoreCase("Newest")) {
                order = "id.desc";
            } else if (sortBy.equals("Đánh giá") || sortBy.equalsIgnoreCase("Rating")) {
                order = "rating.desc";
            } else if (sortBy.equals("Giá thấp đến cao") || sortBy.equalsIgnoreCase("Price Low-High")) {
                order = "price.asc";
            }
        }
        options.put("order", order);
        
        int limit = 20;
        int offset = (page - 1) * limit;
        options.put("limit", String.valueOf(limit));
        options.put("offset", String.valueOf(offset));
        
        Call<List<Course>> call = courseApi.searchCourses(options);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public void getCoursesByCategory(String categoryId, String sortBy, int page, CourseListCallback callback) {
        Map<String, String> options = new HashMap<>();
        options.put("select", "*,instructor:users(full_name)");
        
        String resolvedId = resolveCategoryId(categoryId);
        if (resolvedId != null) {
            options.put("category_id", "eq." + resolvedId);
        }
        
        String order = "enrolled_count.desc";
        if (sortBy != null) {
            if (sortBy.equals("Phổ biến") || sortBy.equalsIgnoreCase("Popular")) {
                order = "enrolled_count.desc";
            } else if (sortBy.equals("Mới nhất") || sortBy.equalsIgnoreCase("Newest")) {
                order = "id.desc";
            } else if (sortBy.equals("Đánh giá") || sortBy.equalsIgnoreCase("Rating")) {
                order = "rating.desc";
            } else if (sortBy.equals("Giá thấp đến cao") || sortBy.equalsIgnoreCase("Price Low-High")) {
                order = "price.asc";
            }
        }
        options.put("order", order);
        
        int limit = 20;
        int offset = (page - 1) * limit;
        options.put("limit", String.valueOf(limit));
        options.put("offset", String.valueOf(offset));

        Call<List<Course>> call = courseApi.searchCourses(options);
        executors.networkIO().execute(() -> {
            try {
                retrofit2.Response<List<Course>> response = call.execute();
                if (response.isSuccessful()) {
                    executors.mainThread().execute(() -> callback.onSuccess(response.body()));
                } else {
                    executors.mainThread().execute(() -> callback.onError("Server response error: " + response.code()));
                }
            } catch (IOException e) {
                executors.mainThread().execute(() -> callback.onError(e.getLocalizedMessage()));
            }
        });
    }

    public LiveData<Resource<Course>> getCourseById(String id) {
        MutableLiveData<Resource<Course>> resultLiveData = new MutableLiveData<>();
        Call<List<Course>> call = courseApi.getCourseById("eq." + id, "*,instructor:users(full_name)");
        
        MutableLiveData<Resource<List<Course>>> listLiveData = new MutableLiveData<>();
        executeCall(call, listLiveData);
        
        listLiveData.observeForever(resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                if (resource.data != null && !resource.data.isEmpty()) {
                    resultLiveData.setValue(Resource.success(resource.data.get(0)));
                } else {
                    resultLiveData.setValue(Resource.error(new com.app.learning.data.api.ApiError("404", "Không tìm thấy khóa học", null, null)));
                }
            } else if (resource.status == Resource.Status.ERROR) {
                resultLiveData.setValue(Resource.error(resource.error));
            } else if (resource.status == Resource.Status.LOADING) {
                resultLiveData.setValue(Resource.loading());
            }
        });
        return resultLiveData;
    }

    private String resolveCategoryId(String categoryIdOrName) {
        if (categoryIdOrName == null || categoryIdOrName.trim().isEmpty()) {
            return null;
        }
        try {
            Long.parseLong(categoryIdOrName);
            return categoryIdOrName;
        } catch (NumberFormatException e) {
            String lower = categoryIdOrName.toLowerCase().trim();
            if (lower.contains("công nghệ") || lower.equals("technology")) {
                return "1";
            } else if (lower.contains("kinh doanh") || lower.equals("business")) {
                return "2";
            } else if (lower.contains("thiết kế") || lower.equals("design")) {
                return "3";
            } else if (lower.contains("ngôn ngữ") || lower.contains("ngoại ngữ") || lower.equals("language")) {
                return "4";
            }
            return null;
        }
    }

    public LiveData<Resource<List<Lesson>>> getLessons(String courseId) {
        MutableLiveData<Resource<List<Lesson>>> resultLiveData = new MutableLiveData<>();
        Call<List<Lesson>> call = courseApi.getLessonsByCourseId("eq." + courseId, "order_index.asc");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Boolean>> checkEnrollment(String userId, String courseId) {
        MutableLiveData<Resource<Boolean>> resultLiveData = new MutableLiveData<>();
        Call<List<Map<String, Object>>> call = courseApi.checkEnrollment("eq." + userId, "eq." + courseId);
        
        MutableLiveData<Resource<List<Map<String, Object>>>> checkLiveData = new MutableLiveData<>();
        executeCall(call, checkLiveData);
        
        checkLiveData.observeForever(resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                boolean isEnrolled = resource.data != null && !resource.data.isEmpty();
                resultLiveData.setValue(Resource.success(isEnrolled));
            } else if (resource.status == Resource.Status.ERROR) {
                resultLiveData.setValue(Resource.error(resource.error));
            } else if (resource.status == Resource.Status.LOADING) {
                resultLiveData.setValue(Resource.loading());
            }
        });
        return resultLiveData;
    }

    public LiveData<Resource<Void>> enrollInCourse(String userId, String courseId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("course_id", courseId);
        body.put("progress_percent", 0);
        Call<Void> call = courseApi.enrollInCourse(body, "representation");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }
}
