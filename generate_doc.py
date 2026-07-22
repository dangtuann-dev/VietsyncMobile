import os
import glob
import html

files = [
    "fragment_home.xml", "explore_fragment.xml", "course_list_fragment.xml", 
    "fragment_courses.xml", "fragment_my_courses.xml", "fragment_my_learning.xml", 
    "fragment_lesson_detail.xml", "fragment_create_course.xml", "fragment_teacher_dashboard.xml", 
    "fragment_wishlist.xml", "fragment_profile.xml", "fragment_notification.xml", 
    "fragment_notification_settings.xml"
]

base_dir = r"C:\Users\vuchi\AndroidStudioProjects\VietsyncMobile\app\src\main\res\layout"

descriptions = {
    "fragment_home.xml": "Trang chủ ứng dụng, nơi người dùng nhìn thấy đầu tiên sau khi đăng nhập.",
    "explore_fragment.xml": "Màn hình khám phá và tìm kiếm khóa học.",
    "course_list_fragment.xml": "Phân mảnh dùng chung để hiển thị một danh sách các khóa học.",
    "fragment_courses.xml": "Màn hình tổng hợp liên quan đến khóa học (container chứa các tab).",
    "fragment_my_courses.xml": "Hiển thị danh sách các khóa học mà người dùng đã mua hoặc đăng ký.",
    "fragment_my_learning.xml": "Màn hình theo dõi tiến trình học tập của các khóa học đang tham gia.",
    "fragment_lesson_detail.xml": "Màn hình chi tiết bài học (phát video, đọc tài liệu).",
    "fragment_create_course.xml": "Giao diện dành cho Giảng viên để tạo mới một khóa học.",
    "fragment_teacher_dashboard.xml": "Bảng điều khiển (Dashboard) dành cho Giảng viên.",
    "fragment_wishlist.xml": "Màn hình danh sách yêu thích các khóa học.",
    "fragment_profile.xml": "Màn hình quản lý hồ sơ cá nhân của người dùng.",
    "fragment_notification.xml": "Màn hình hiển thị danh sách các thông báo hệ thống.",
    "fragment_notification_settings.xml": "Màn hình cài đặt thông báo (bật/tắt các loại thông báo)."
}

html_content = '''<html xmlns:o="urn:schemas-microsoft-com:office:office"
xmlns:w="urn:schemas-microsoft-com:office:word"
xmlns="http://www.w3.org/TR/REC-html40">
<head>
<meta charset="utf-8">
<title>Nhóm 2: Main Fragments & UI Flows</title>
<style>
body { font-family: 'Times New Roman', serif; font-size: 14pt; }
h1 { text-align: center; font-size: 18pt; }
h2 { font-size: 16pt; color: #2e74b5; margin-top: 30px; }
.code-block { 
    background-color: #2b2b2b; 
    color: #a9b7c6; 
    padding: 10px; 
    border-radius: 5px; 
    font-family: Consolas, monospace; 
    font-size: 11pt;
    white-space: pre-wrap; 
    margin-top: 10px;
}
.evidence-title { font-weight: bold; font-style: italic; color: #555; }
</style>
</head>
<body>
<h1>Nhóm 2: Main Fragments &amp; UI Flows</h1>
'''

for idx, file in enumerate(files, 1):
    path = os.path.join(base_dir, file)
    desc = descriptions.get(file, "")
    
    code_snippet = ""
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            lines = f.readlines()
            # Lấy 20 dòng đầu tiên làm minh chứng
            snippet_lines = lines[:20]
            code_snippet = "".join(snippet_lines)
            if len(lines) > 20:
                code_snippet += "    ...\n    <!-- Content Truncated for Evidence -->\n</RootTag>"
    else:
        code_snippet = "<!-- File not found in local workspace -->"
        
    escaped_code = html.escape(code_snippet)
    
    html_content += f"<h2>{{idx}}. {{file}}</h2>\n"
    html_content += f"<p><b>Chức năng chính:</b> {{desc}}</p>\n"
    html_content += f"<p class='evidence-title'>Minh chứng mã nguồn (Code Snippet):</p>\n"
    html_content += f"<div class='code-block'>{{escaped_code}}</div>\n"

html_content += "</body></html>"

output_path = r"C:\Users\vuchi\AndroidStudioProjects\VietsyncMobile\Nhom2_Main_Fragments.doc"
with open(output_path, "w", encoding="utf-8") as f:
    f.write(html_content)

print(f"Doc file generated at {output_path}")
