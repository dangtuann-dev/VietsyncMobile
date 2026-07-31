package com.app.learning.ui.teacher.course;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.TeacherCourseRepository;
import com.app.learning.utils.UserPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class CreateCourseActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private int currentStep = 0;

    private TextView tvStep1, tvStep2, tvStep3, tvStepTitle;
    private MaterialButton btnNext, btnBack, btnPickImage;
    private ImageView ivThumbnail;

    private TextInputEditText etTitle, etDescription, etPrice, etVideoUrl;
    private AutoCompleteTextView spinCategory, spinLevel;

    private TeacherCourseRepository repository;
    private UserPreference userPreference;
    
    private Uri selectedImageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        repository = new TeacherCourseRepository(this);
        userPreference = UserPreference.getInstance(this);

        initViews();
        setupListeners();
        setupDropdowns();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        ivThumbnail.setImageURI(uri);
                    }
                }
        );
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        viewFlipper = findViewById(R.id.viewFlipper);
        tvStep1 = findViewById(R.id.tvStep1);
        tvStep2 = findViewById(R.id.tvStep2);
        tvStep3 = findViewById(R.id.tvStep3);
        tvStepTitle = findViewById(R.id.tvStepTitle);

        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        btnPickImage = findViewById(R.id.btnPickImage);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etPrice = findViewById(R.id.etPrice);
        etVideoUrl = findViewById(R.id.etVideoUrl);

        spinCategory = findViewById(R.id.spinCategory);
        spinLevel = findViewById(R.id.spinLevel);
        ivThumbnail = findViewById(R.id.ivThumbnail);
    }

    private void setupDropdowns() {
        String[] categories = {"Công nghệ thông tin", "Kinh doanh & Khởi nghiệp", "Thiết kế đồ họa", "Ngoại ngữ"}; 
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        spinCategory.setAdapter(catAdapter);

        String[] levels = {"Beginner", "Intermediate", "Advanced"};
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, levels);
        spinLevel.setAdapter(levelAdapter);
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> {
            if (currentStep < 2) {
                if (validateStep(currentStep)) {
                    currentStep++;
                    updateStepper();
                }
            } else {
                publishCourse();
            }
        });

        btnBack.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                updateStepper();
            }
        });

        btnPickImage.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });
    }

    private boolean validateStep(int step) {
        if (step == 0) {
            if (etTitle.getText().toString().isEmpty()) {
                etTitle.setError("Required");
                return false;
            }
            if (etPrice.getText().toString().isEmpty()) {
                etPrice.setError("Required");
                return false;
            }
        }
        return true;
    }

    private void updateStepper() {
        viewFlipper.setDisplayedChild(currentStep);

        tvStep1.setBackgroundColor(currentStep >= 0 ? 0xFF2196F3 : 0xFFE0E0E0);
        tvStep1.setTextColor(currentStep >= 0 ? 0xFFFFFFFF : 0xFF9E9E9E);

        tvStep2.setBackgroundColor(currentStep >= 1 ? 0xFF2196F3 : 0xFFE0E0E0);
        tvStep2.setTextColor(currentStep >= 1 ? 0xFFFFFFFF : 0xFF9E9E9E);

        tvStep3.setBackgroundColor(currentStep >= 2 ? 0xFF2196F3 : 0xFFE0E0E0);
        tvStep3.setTextColor(currentStep >= 2 ? 0xFFFFFFFF : 0xFF9E9E9E);

        btnBack.setVisibility(currentStep > 0 ? View.VISIBLE : View.INVISIBLE);

        if (currentStep == 0) {
            tvStepTitle.setText("Thông tin cơ bản");
            btnNext.setText("Tiếp tục");
        } else if (currentStep == 1) {
            tvStepTitle.setText("Ảnh bìa & Video");
            btnNext.setText("Tiếp tục");
        } else {
            tvStepTitle.setText("Xác nhận");
            btnNext.setText("Xuất bản");
        }
    }

    private void publishCourse() {
        btnNext.setEnabled(false);
        Toast.makeText(this, "Đang xuất bản khóa học...", Toast.LENGTH_SHORT).show();

        if (selectedImageUri != null) {
            uploadThumbnailAndCreateCourse();
        } else {
            createCourseInDb("");
        }
    }

    private void uploadThumbnailAndCreateCourse() {
        try {
            InputStream is = getContentResolver().openInputStream(selectedImageUri);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();

            repository.uploadThumbnail(bytes, "image/jpeg").observe(this, resource -> {
                if (resource.isSuccess()) {
                    createCourseInDb(resource.data);
                } else if (resource.isError()) {
                    Toast.makeText(this, "Lỗi tải ảnh: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
                    btnNext.setEnabled(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            btnNext.setEnabled(true);
        }
    }

    private void createCourseInDb(String thumbnailUrl) {
        User user = userPreference.getUserProfile();
        if (user == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course();
        course.setTitle(etTitle.getText().toString());
        course.setDescription(etDescription.getText().toString());
        course.setPrice(Double.parseDouble(etPrice.getText().toString()));
        course.setLevel(spinLevel.getText().toString().toLowerCase());
        
        long categoryId = 1L;
        String selectedCategory = spinCategory.getText().toString();
        if (selectedCategory.equals("Công nghệ thông tin")) categoryId = 1L;
        else if (selectedCategory.equals("Kinh doanh & Khởi nghiệp")) categoryId = 2L;
        else if (selectedCategory.equals("Thiết kế đồ họa")) categoryId = 3L;
        else if (selectedCategory.equals("Ngoại ngữ")) categoryId = 4L;
        
        course.setCategoryId(categoryId);
        course.setInstructorId(user.getId());
        if (course.getId() == null || course.getId().isEmpty()) {
            course.setId("course_teacher_" + System.currentTimeMillis());
        }
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            course.setThumbnail(thumbnailUrl);
        } else {
            course.setThumbnail("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400");
        }

        // Register course globally across the entire app
        com.app.learning.data.repository.CourseRepository.addCreatedCourse(course);

        repository.createCourse(course).observe(this, resource -> {
            Toast.makeText(this, "Đã tạo và tải lên khóa học thành công!", Toast.LENGTH_SHORT).show();
            
            // Go to manage lessons
            Intent intent = new Intent(this, ManageLessonsActivity.class);
            intent.putExtra("COURSE_ID", course.getId());
            startActivity(intent);
            finish();
        });
    }
}
