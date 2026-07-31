package com.app.learning.ui.teacher.course;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.app.learning.data.model.Course;
import com.app.learning.data.repository.TeacherCourseRepository;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditCourseActivity extends AppCompatActivity {

    private TextInputEditText edtTitle, edtDescription, edtPrice, edtDuration;
    private Spinner spinCategory, spinLevel;
    private MaterialCardView cardThumbnail;
    private ImageView ivThumbnail;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    private String[] categories = {"Công nghệ thông tin", "Kinh doanh", "Thiết kế", "Ngoại ngữ", "Khác"};
    private Long[] categoryIds = {1L, 2L, 3L, 4L, 5L};
    private String[] levels = {"Mới bắt đầu", "Trung bình", "Nâng cao", "Tất cả cấp độ"};

    private Course currentCourse;
    private Uri selectedImageUri = null;
    private byte[] imageBytes = null;
    private String mimeType = null;
    private TeacherCourseRepository repository;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            Glide.with(this).load(selectedImageUri).into(ivThumbnail);
                            InputStream is = getContentResolver().openInputStream(selectedImageUri);
                            mimeType = getContentResolver().getType(selectedImageUri);
                            if (mimeType == null) mimeType = "image/jpeg";
                            
                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            int nRead;
                            byte[] data = new byte[16384];
                            while ((nRead = is.read(data, 0, data.length)) != -1) {
                                buffer.write(data, 0, nRead);
                            }
                            imageBytes = buffer.toByteArray();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        repository = new TeacherCourseRepository(this);

        currentCourse = (Course) getIntent().getSerializableExtra("COURSE");
        if (currentCourse == null) {
            Toast.makeText(this, "Không tìm thấy thông tin khóa học", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupSpinners();
        populateData();
        setupListeners();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtDuration = findViewById(R.id.edtDuration);
        spinCategory = findViewById(R.id.spinCategory);
        spinLevel = findViewById(R.id.spinLevel);
        cardThumbnail = findViewById(R.id.cardThumbnail);
        ivThumbnail = findViewById(R.id.ivThumbnail);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);
        spinCategory.setAdapter(catAdapter);

        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, levels);
        spinLevel.setAdapter(levelAdapter);
    }

    private void populateData() {
        edtTitle.setText(currentCourse.getTitle());
        edtDescription.setText(currentCourse.getDescription());
        edtPrice.setText(String.valueOf((int) currentCourse.getPrice()));
        edtDuration.setText(String.valueOf(currentCourse.getDuration()));

        if (currentCourse.getCategoryId() != null) {
            for (int i = 0; i < categoryIds.length; i++) {
                if (categoryIds[i].equals(currentCourse.getCategoryId())) {
                    spinCategory.setSelection(i);
                    break;
                }
            }
        }

        if (currentCourse.getLevel() != null) {
            for (int i = 0; i < levels.length; i++) {
                if (levels[i].equals(currentCourse.getLevel())) {
                    spinLevel.setSelection(i);
                    break;
                }
            }
        }

        if (currentCourse.getThumbnail() != null && !currentCourse.getThumbnail().isEmpty()) {
            Glide.with(this).load(currentCourse.getThumbnail()).into(ivThumbnail);
        }
    }

    private void setupListeners() {
        cardThumbnail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveCourse();
            }
        });
    }

    private boolean validateInput() {
        if (edtTitle.getText().toString().trim().isEmpty()) {
            edtTitle.setError("Vui lòng nhập tên khóa học");
            return false;
        }
        if (edtPrice.getText().toString().trim().isEmpty()) {
            edtPrice.setError("Vui lòng nhập giá");
            return false;
        }
        return true;
    }

    private void saveCourse() {
        setLoading(true);

        if (imageBytes != null) {
            // Cần upload ảnh mới trước
            repository.uploadThumbnail(imageBytes, mimeType).observe(this, resource -> {
                if (resource.isLoading()) return;

                if (resource.isSuccess() && resource.data != null) {
                    updateCourseData(resource.data);
                } else {
                    setLoading(false);
                    Toast.makeText(this, "Lỗi khi upload ảnh", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Giữ nguyên ảnh cũ
            updateCourseData(currentCourse.getThumbnail());
        }
    }

    private void updateCourseData(String thumbnailUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("title", edtTitle.getText().toString().trim());
        updates.put("description", edtDescription.getText().toString().trim());
        updates.put("category_id", categoryIds[spinCategory.getSelectedItemPosition()]);
        updates.put("level", spinLevel.getSelectedItem().toString());
        
        try {
            updates.put("price", Double.parseDouble(edtPrice.getText().toString().trim()));
            updates.put("duration", Integer.parseInt(edtDuration.getText().toString().trim()));
        } catch (Exception e) {}
        
        updates.put("thumbnail", thumbnailUrl);

        repository.updateCourse(currentCourse.getId(), updates).observe(this, resource -> {
            if (resource.isLoading()) return;
            
            setLoading(false);
            if (resource.isSuccess()) {
                Toast.makeText(this, "Cập nhật khóa học thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi cập nhật: " + (resource.error != null ? resource.error.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);
        cardThumbnail.setEnabled(!isLoading);
    }
}
