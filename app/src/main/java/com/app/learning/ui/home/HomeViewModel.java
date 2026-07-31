package com.app.learning.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.learning.data.model.Banner;
import com.app.learning.data.model.Category;
import com.app.learning.data.model.Course;
import com.app.learning.ui.base.BaseViewModel;
import com.example.vietsyncmobile.R;
import java.util.ArrayList;
import java.util.List;
import com.app.learning.data.repository.CourseRepository;

public class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<List<Banner>> banners = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> featuredCourses = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> continueLearning = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> popularCourses = new MutableLiveData<>();
    private CourseRepository courseRepository;

    public HomeViewModel() {
        courseRepository = new CourseRepository();
        loadBanners();
        loadCategories();
        loadFeaturedCourses();
        loadContinueLearning();
        loadPopularCourses();
        loadCategoryCourses();
    }

    public LiveData<List<Banner>> getBanners() {
        return banners;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<Course>> getFeaturedCourses() {
        return featuredCourses;
    }

    public LiveData<List<Course>> getContinueLearning() {
        return continueLearning;
    }

    public LiveData<List<Course>> getPopularCourses() {
        return popularCourses;
    }

    public void loadBanners() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=600", "Khóa Học Lập Trình 2026", "Nhận ngay ưu đãi 50% cho thành viên mới"));
        list.add(new Banner("https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=600", "Làm Chủ UI/UX Mobile", "Học từ các chuyên gia thiết kế hàng đầu thế giới"));
        list.add(new Banner("https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=600", "Anh Văn Giao Tiếp Cơ Bản", "Học phát âm chuẩn cùng giáo viên bản xứ"));
        banners.setValue(list);
    }

    public void loadCategories() {
        courseRepository.getCategories().observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                for (Category cat : resource.data) {
                    if (cat.getId() == 1L) {
                        cat.setColorHex("#3B82F6"); // Blue
                        cat.setColorLightHex("#EFF6FF");
                        cat.setIconResId(R.drawable.ic_book);
                    } else if (cat.getId() == 2L) {
                        cat.setColorHex("#EF4444"); // Red
                        cat.setColorLightHex("#FEF2F2");
                        cat.setIconResId(R.drawable.ic_edit);
                    } else if (cat.getId() == 3L) {
                        cat.setColorHex("#10B981"); // Green
                        cat.setColorLightHex("#ECFDF5");
                        cat.setIconResId(R.drawable.ic_certificate);
                    } else {
                        cat.setColorHex("#8B5CF6"); // Purple
                        cat.setColorLightHex("#F3E8FF");
                        cat.setIconResId(R.drawable.ic_grid);
                    }
                }
                categories.setValue(resource.data);
            } else {
                List<Category> list = new ArrayList<>();
                list.add(new Category(1L, "Công nghệ thông tin", R.drawable.ic_courses, "#3B82F6", "#EFF6FF"));
                list.add(new Category(2L, "Kinh doanh & Khởi nghiệp", R.drawable.ic_explore, "#10B981", "#ECFDF5"));
                list.add(new Category(3L, "Thiết kế đồ họa", R.drawable.ic_filter, "#F59E0B", "#FFFBEB"));
                list.add(new Category(4L, "Ngoại ngữ", R.drawable.ic_history, "#EF4444", "#FEF2F2"));
                categories.setValue(list);
            }
        });
    }

    public void loadFeaturedCourses() {
        // Load mock data
        List<Course> list = new ArrayList<>();
        Course c1 = new Course();
        c1.setId("f1");
        c1.setTitle("Lập trình Android nâng cao với Kotlin");
        c1.setLevel("Nâng cao");
        c1.setDuration(45);
        c1.setRating(4.9);
        c1.setPrice(0);
        c1.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        list.add(c1);

        Course c2 = new Course();
        c2.setId("f2");
        c2.setTitle("Thiết kế giao diện di động UI/UX");
        c2.setLevel("Mới bắt đầu");
        c2.setDuration(24);
        c2.setRating(4.6);
        c2.setPrice(0);
        c2.setThumbnail("https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=400");
        list.add(c2);

        Course c3 = new Course();
        c3.setId("f3");
        c3.setTitle("Kiến trúc phần mềm & Clean Architecture");
        c3.setLevel("Trung cấp");
        c3.setDuration(32);
        c3.setRating(4.8);
        c3.setPrice(0);
        c3.setThumbnail("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400");
        list.add(c3);

        featuredCourses.setValue(list);

        // Then fetch real data and prepend
        java.util.Map<String, String> options = new java.util.HashMap<>();
        options.put("select", "*,instructor:users(full_name)");
        options.put("order", "id.desc");
        options.put("limit", "5");
        courseRepository.searchCourses(options).observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                List<Course> combined = new ArrayList<>(resource.data);
                combined.addAll(featuredCourses.getValue() != null ? featuredCourses.getValue() : new ArrayList<>());
                featuredCourses.setValue(combined);
            }
        });
    }

    public void loadContinueLearning() {
        List<Course> list = new ArrayList<>();

        Course c1 = new Course();
        c1.setId("c1");
        c1.setTitle("Lập trình Android căn bản");
        c1.setDuration(30);
        c1.setThumbnail("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400");
        list.add(c1);

        Course c2 = new Course();
        c2.setId("c2");
        c2.setTitle("Tiếng Anh giao tiếp công sở");
        c2.setDuration(40);
        c2.setThumbnail("https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400");
        list.add(c2);

        continueLearning.setValue(list);
    }

    public void loadPopularCourses() {
        // Load mock data
        List<Course> list = new ArrayList<>();
        Course c1 = new Course();
        c1.setId("p1");
        c1.setTitle("Lập trình Web với React và Node.js");
        c1.setLevel("Trung cấp");
        c1.setDuration(52);
        c1.setRating(4.7);
        c1.setPrice(0);
        c1.setThumbnail("https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=400");
        list.add(c1);

        Course c2 = new Course();
        c2.setId("p2");
        c2.setTitle("Quản lý dự án Agile/Scrum");
        c2.setLevel("Mọi cấp độ");
        c2.setDuration(18);
        c2.setRating(4.5);
        c2.setPrice(0);
        c2.setThumbnail("https://images.unsplash.com/photo-1531403009284-440f080d1e12?w=400");
        list.add(c2);

        Course c3 = new Course();
        c3.setId("p3");
        c3.setTitle("Machine Learning thực chiến");
        c3.setLevel("Nâng cao");
        c3.setDuration(60);
        c3.setRating(4.9);
        c3.setPrice(0);
        c3.setThumbnail("https://images.unsplash.com/photo-1527474305487-b87b222841cc?w=400");
        list.add(c3);

        popularCourses.setValue(list);

        // Then fetch real data and prepend
        java.util.Map<String, String> options = new java.util.HashMap<>();
        options.put("select", "*,instructor:users(full_name)");
        options.put("order", "enrolled_count.desc");
        options.put("limit", "5");
        courseRepository.searchCourses(options).observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                List<Course> combined = new ArrayList<>(resource.data);
                combined.addAll(popularCourses.getValue() != null ? popularCourses.getValue() : new ArrayList<>());
                popularCourses.setValue(combined);
            }
        });
    }

    private final MutableLiveData<List<Course>> itCourses = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> bizCourses = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> designCourses = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> languageCourses = new MutableLiveData<>();

    public LiveData<List<Course>> getItCourses() { return itCourses; }
    public LiveData<List<Course>> getBizCourses() { return bizCourses; }
    public LiveData<List<Course>> getDesignCourses() { return designCourses; }
    public LiveData<List<Course>> getLanguageCourses() { return languageCourses; }

    public void loadCategoryCourses() {
        // IT Courses
        java.util.Map<String, String> options1 = new java.util.HashMap<>();
        options1.put("category_id", "eq.1");
        courseRepository.searchCourses(options1).observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                itCourses.setValue(resource.data);
            }
        });

        // Biz Courses
        java.util.Map<String, String> options2 = new java.util.HashMap<>();
        options2.put("category_id", "eq.2");
        courseRepository.searchCourses(options2).observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                bizCourses.setValue(resource.data);
            }
        });

        // Design Courses
        java.util.Map<String, String> options3 = new java.util.HashMap<>();
        options3.put("category_id", "eq.3");
        courseRepository.searchCourses(options3).observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                designCourses.setValue(resource.data);
            }
        });

        // Language Courses
        java.util.Map<String, String> options4 = new java.util.HashMap<>();
        options4.put("category_id", "eq.4");
        courseRepository.searchCourses(options4).observeForever(resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                languageCourses.setValue(resource.data);
            }
        });
    }
}
