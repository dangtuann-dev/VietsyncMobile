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

public class HomeViewModel extends BaseViewModel {

    private final MutableLiveData<List<Banner>> banners = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> featuredCourses = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> continueLearning = new MutableLiveData<>();
    private final MutableLiveData<List<Course>> popularCourses = new MutableLiveData<>();

    public HomeViewModel() {
        loadBanners();
        loadCategories();
        loadFeaturedCourses();
        loadContinueLearning();
        loadPopularCourses();
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
        List<Category> list = new ArrayList<>();
        list.add(new Category(1L, "Lập trình", R.drawable.ic_courses, "#3B82F6", "#EFF6FF"));
        list.add(new Category(2L, "Thiết kế", R.drawable.ic_edit, "#EF4444", "#FEF2F2"));
        list.add(new Category(3L, "Ngoại ngữ", R.drawable.ic_certificate, "#10B981", "#ECFDF5"));
        list.add(new Category(4L, "Marketing", R.drawable.ic_dashboard, "#8B5CF6", "#F5F3FF"));
        list.add(new Category(5L, "Cá nhân", R.drawable.ic_person, "#F59E0B", "#FFFBEB"));
        categories.setValue(list);
    }

    public void loadFeaturedCourses() {
        List<Course> list = new ArrayList<>();

        Course c1 = new Course();
        c1.setId("f1");
        c1.setTitle("Lập trình Android nâng cao với Kotlin");
        c1.setLevel("Nâng cao");
        c1.setDuration(45);
        c1.setRating(4.9);
        c1.setPrice(499000);
        c1.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        list.add(c1);

        Course c2 = new Course();
        c2.setId("f2");
        c2.setTitle("Thiết kế giao diện di động UI/UX");
        c2.setLevel("Mới bắt đầu");
        c2.setDuration(24);
        c2.setRating(4.6);
        c2.setPrice(299000);
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
        List<Course> list = new ArrayList<>();

        Course c1 = new Course();
        c1.setId("p1");
        c1.setTitle("Lập trình Web với React và Node.js");
        c1.setLevel("Trung cấp");
        c1.setDuration(52);
        c1.setRating(4.7);
        c1.setPrice(399000);
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
        c3.setPrice(599000);
        c3.setThumbnail("https://images.unsplash.com/photo-1527474305487-b87b222841cc?w=400");
        list.add(c3);

        popularCourses.setValue(list);
    }
}
