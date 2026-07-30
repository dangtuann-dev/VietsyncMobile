package com.app.learning.ui.gamification;

import com.app.learning.data.model.AchievementModel;
import java.util.ArrayList;
import java.util.List;

public class AchievementManager {

    private static AchievementManager instance;

    private AchievementManager() {}

    public static synchronized AchievementManager getInstance() {
        if (instance == null) {
            instance = new AchievementManager();
        }
        return instance;
    }

    public List<AchievementModel> getAllAchievements() {
        List<AchievementModel> list = new ArrayList<>();
        list.add(new AchievementModel("FIRST_LESSON", "Khởi Đầu Mới", "Hoàn thành bài học đầu tiên", "ic_book", 1, 1, true, "2026-07-01"));
        list.add(new AchievementModel("QUIZ_MASTER", "Bậc Thầy Trắc Nghiệm", "Đạt 100% điểm trong 5 quiz liên tiếp", "ic_star", 5, 5, true, "2026-07-20"));
        list.add(new AchievementModel("SPEED_LEARNER", "Học Tốc Độ", "Hoàn thành khóa học trong vòng 3 ngày", "ic_fire", 1, 1, true, "2026-07-22"));
        list.add(new AchievementModel("SOCIAL_BUTTERFLY", "Sôi Nổi Thảo Luận", "Tạo 10 bài đăng thảo luận trong cộng đồng", "ic_chat", 10, 7, false, null));
        list.add(new AchievementModel("NIGHT_OWL", "Cú Đêm Mẫn Cán", "Hoàn thành bài học sau 11:00 PM", "ic_moon", 1, 1, true, "2026-07-26"));
        list.add(new AchievementModel("STREAK_7_DAYS", "Chuỗi 7 Ngày", "Duy trì học tập liên tục trong 7 ngày", "ic_fire", 7, 7, true, "2026-07-25"));
        list.add(new AchievementModel("CERTIFIED_PRO", "Chứng Chỉ Đầu Tiên", "Nhận được chứng chỉ hoàn thành khóa học", "ic_certificate", 1, 1, true, "2026-07-28"));
        list.add(new AchievementModel("PEER_REVIEWER", "Chuyên Gia Góp Ý", "Đánh giá 3 bài tập của bạn học", "ic_check", 3, 3, true, "2026-07-30"));
        list.add(new AchievementModel("EARLY_BIRD", "Sơn Dầu Đón Nắng", "Học bài trước 6:00 AM", "ic_sun", 1, 0, false, null));
        list.add(new AchievementModel("PERFECT_SCORE", "Điểm Tuyệt Đối", "Đạt điểm 10/10 trong bài thi Final Exam", "ic_award", 1, 0, false, null));
        list.add(new AchievementModel("NOTE_TAKER", "Ghi Chép Chăm Chỉ", "Tạo 15 ghi chú cá nhân trong bài học", "ic_note", 15, 12, false, null));
        list.add(new AchievementModel("DOWNLOAD_KING", "Tải Bài Offline", "Tải xuống 5 bài học để học ngoại tuyến", "ic_download", 5, 5, true, "2026-07-15"));
        list.add(new AchievementModel("STREAK_30_DAYS", "Thần Thoại 30 Ngày", "Chuỗi học tập liên tục 30 ngày", "ic_crown", 30, 14, false, null));
        list.add(new AchievementModel("MULTILINGUAL", "Đa Ngôn Ngữ", "Chuyển đổi giao diện sang Tiếng Anh", "ic_globe", 1, 1, true, "2026-07-29"));
        list.add(new AchievementModel("DARK_MODE_USER", "Chiến Sĩ Đêm Đen", "Kích hoạt chế độ Dark Mode", "ic_moon", 1, 1, true, "2026-07-29"));
        list.add(new AchievementModel("SUPABASE_EXPLORER", "Chinh Phục Backend", "Hoàn thành khóa học Supabase Database", "ic_db", 1, 0, false, null));
        list.add(new AchievementModel("FEEDBACK_GIVER", "Đóng Góp Ý Kiến", "Gửi đánh giá phản hồi ứng dụng", "ic_star", 1, 1, true, "2026-07-30"));
        list.add(new AchievementModel("SHARE_MASTER", "Lan Tỏa Tri Thức", "Chia sẻ thành tích học tập lên mạng xã hội", "ic_share", 1, 1, true, "2026-07-30"));
        list.add(new AchievementModel("AUDIO_LEARNER", "Lắng Nghe Sâu Sắc", "Phát lại video bài học ở tốc độ 1.5x", "ic_play", 1, 1, true, "2026-07-27"));
        list.add(new AchievementModel("GRANDMASTER", "Học Giả Toàn Năng", "Mở khóa 15 danh hiệu thành tích", "ic_trophy", 15, 12, false, null));

        return list;
    }
}
