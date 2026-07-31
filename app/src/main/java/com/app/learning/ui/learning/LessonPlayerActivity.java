package com.app.learning.ui.learning;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.ui.PlayerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietsyncmobile.R;
import com.app.learning.ui.discussion.DiscussionFragment;
import com.app.learning.ui.learning.player.PlayerManager;
import com.app.learning.ui.learning.player.QualitySelectorDialog;
import com.app.learning.ui.learning.player.SpeedSelectorDialog;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class LessonPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_LESSON_ID = "extra_lesson_id";
    public static final String EXTRA_COURSE_ID = "extra_course_id";
    public static final String EXTRA_LESSON_TITLE = "extra_lesson_title";
    public static final String EXTRA_VIDEO_URL = "extra_video_url";

    private MaterialToolbar toolbar;
    private PlayerView playerView;
    private ProgressBar lessonProgressBar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MaterialButton btnPrevLesson, btnNextLesson;
    private ImageButton btnPip;

    private PlayerManager playerManager;
    private LessonViewModel viewModel;

    private String lessonId;
    private String courseId;
    private String lessonTitle;
    private String videoUrl;

    private boolean isMuted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_player);

        toolbar = findViewById(R.id.toolbar);
        playerView = findViewById(R.id.playerView);
        lessonProgressBar = findViewById(R.id.lessonProgressBar);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnPrevLesson = findViewById(R.id.btnPrevLesson);
        btnNextLesson = findViewById(R.id.btnNextLesson);
        btnPip = findViewById(R.id.btnPip);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        lessonTitle = getIntent().getStringExtra(EXTRA_LESSON_TITLE);
        videoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);

        if (lessonId == null) lessonId = "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380011";
        if (courseId == null) courseId = "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";
        if (lessonTitle == null) lessonTitle = "Bài 1: Giới thiệu khóa học";
        if (videoUrl == null) videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";

        toolbar.setTitle(lessonTitle);
        syncCurrentLessonIndex();

        viewModel = new ViewModelProvider(this).get(LessonViewModel.class);
        playerManager = PlayerManager.getInstance(this);

        setupPlayerControls();
        setupViewPager();

        btnPip.setOnClickListener(v -> enterPipMode());
        btnPrevLesson.setOnClickListener(v -> navigateLesson(false));
        btnNextLesson.setOnClickListener(v -> navigateLesson(true));

        viewModel.getIsProgress80Percent().observe(this, is80 -> {
            if (Boolean.TRUE.equals(is80)) {
                lessonProgressBar.setProgress(100);
                Toast.makeText(this, "Chúc mừng! Bạn đã hoàn thành 80% bài học!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPlayerControls() {
        playerManager.attachPlayerView(playerView);
        playerManager.setMediaUrl(lessonId, videoUrl);

        View controlView = playerView.findViewById(R.id.btnSpeed);
        if (controlView != null) {
            controlView.setOnClickListener(v -> {
                SpeedSelectorDialog dialog = new SpeedSelectorDialog(speed -> {
                    playerManager.setPlaybackSpeed(speed);
                    ((TextView) controlView).setText(speed + "x");
                });
                dialog.show(getSupportFragmentManager(), "speed_dialog");
            });
        }

        View qualityBtn = playerView.findViewById(R.id.btnQuality);
        if (qualityBtn != null) {
            qualityBtn.setOnClickListener(v -> {
                QualitySelectorDialog dialog = new QualitySelectorDialog(quality -> {
                    Toast.makeText(this, "Đã chuyển chất lượng: " + quality, Toast.LENGTH_SHORT).show();
                });
                dialog.show(getSupportFragmentManager(), "quality_dialog");
            });
        }

        View muteBtn = playerView.findViewById(R.id.btnMute);
        if (muteBtn != null) {
            muteBtn.setOnClickListener(v -> {
                isMuted = !isMuted;
                playerManager.getPlayer().setVolume(isMuted ? 0f : 1f);
                Toast.makeText(this, isMuted ? "Đã tắt tiếng" : "Đã bật tiếng", Toast.LENGTH_SHORT).show();
            });
        }

        View fullscreenBtn = playerView.findViewById(R.id.btnFullscreen);
        if (fullscreenBtn != null) {
            fullscreenBtn.setOnClickListener(v -> {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            });
        }
    }

    private void setupViewPager() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return LessonContentFragment.newInstance(lessonTitle, "<p>Bài học này hướng dẫn các khái niệm cơ bản về lập trình Android với Java và MVVM Architecture.</p><pre><code>// Dynamic code block\nString app = \"VietsyncMobile\";</code></pre>");
                } else if (position == 1) {
                    return LessonNotesFragment.newInstance(courseId, lessonId, lessonTitle);
                } else {
                    return DiscussionFragment.newInstance(courseId);
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Nội dung"); break;
                case 1: tab.setText("Ghi chú"); break;
                case 2: tab.setText("Thảo luận"); break;
            }
        }).attach();
    }

    private void enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Rational aspectRatio = new Rational(16, 9);
            PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
            pipBuilder.setAspectRatio(aspectRatio);
            enterPictureInPictureMode(pipBuilder.build());
        } else {
            Toast.makeText(this, "Thiết bị không hỗ trợ chế độ Picture-in-Picture", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LessonItem {
        String id, title, videoUrl;
        LessonItem(String id, String title, String videoUrl) {
            this.id = id;
            this.title = title;
            this.videoUrl = videoUrl;
        }
    }

    private final java.util.List<LessonItem> lessonList = new java.util.ArrayList<>();
    private int currentLessonIndex = 0;

    private void initLessonList() {
        lessonList.clear();
        lessonList.add(new LessonItem("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380011", "Bài 1: Giới thiệu khóa học & Kiến trúc MVVM", "android.resource://" + getPackageName() + "/" + R.raw.sample_lesson));
        lessonList.add(new LessonItem("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380012", "Bài 2: Thiết kế giao diện và xử lý luồng dữ liệu", "android.resource://" + getPackageName() + "/" + R.raw.sample_lesson_2));
        lessonList.add(new LessonItem("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380013", "Bài 3: Tích hợp Supabase Database và API", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        lessonList.add(new LessonItem("d0eebc99-9c0b-4ef8-bb6d-6bb9bd380014", "Bài 4: Tối ưu hiệu năng và Kiểm thử ứng dụng", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"));
    }

    private void syncCurrentLessonIndex() {
        if (lessonList.isEmpty()) initLessonList();
        if (lessonTitle != null || lessonId != null) {
            for (int i = 0; i < lessonList.size(); i++) {
                LessonItem item = lessonList.get(i);
                if ((lessonId != null && lessonId.equalsIgnoreCase(item.id)) ||
                    (lessonTitle != null && (lessonTitle.contains("Bài " + (i + 1)) || lessonTitle.equalsIgnoreCase(item.title)))) {
                    currentLessonIndex = i;
                    break;
                }
            }
        }
    }

    private void navigateLesson(boolean next) {
        playerManager.saveCurrentPosition();
        if (lessonList.isEmpty()) initLessonList();

        if (next) {
            if (currentLessonIndex == 3) {
                // Lesson 4 -> Lesson 5 (Final Exam)
                currentLessonIndex = 4;
                Toast.makeText(this, "Chuyển sang Bài 5: Bài kiểm tra tổng hợp!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, com.app.learning.ui.exam.FinalExamActivity.class);
                intent.putExtra("lesson_id", lessonList.get(4).id);
                intent.putExtra(com.app.learning.ui.exam.FinalExamActivity.EXTRA_COURSE_ID, courseId);
                startActivity(intent);
                return;
            } else if (currentLessonIndex < lessonList.size() - 1) {
                currentLessonIndex++;
            } else {
                Toast.makeText(this, "Bạn đang ở bài học cuối cùng!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (currentLessonIndex > 0) {
                currentLessonIndex--;
            } else {
                Toast.makeText(this, "Bạn đang ở bài học đầu tiên!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        LessonItem currentLesson = lessonList.get(currentLessonIndex);
        lessonId = currentLesson.id;
        lessonTitle = currentLesson.title;
        videoUrl = currentLesson.videoUrl;

        toolbar.setTitle(lessonTitle);
        playerManager.setMediaUrl(lessonId, videoUrl);

        Toast.makeText(this, "Đã chuyển: " + lessonTitle, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerManager != null) playerManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerManager != null) playerManager.saveCurrentPosition();
    }
}
