package com.app.learning.utils;

public class CertificateTemplate {

    public static String getHTMLTemplate(String userName, String courseTitle, String date, String instructorName, int hours, String qrCodeBase64) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<style>\n" +
                "    body {\n" +
                "        font-family: 'Georgia', serif;\n" +
                "        background-color: #0F172A;\n" +
                "        color: #F8FAFC;\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "        display: flex;\n" +
                "        justify-content: center;\n" +
                "        align-items: center;\n" +
                "        height: 100vh;\n" +
                "    }\n" +
                "    .certificate-container {\n" +
                "        border: 10px double #F59E0B;\n" +
                "        background-color: #1E293B;\n" +
                "        padding: 40px;\n" +
                "        width: 800px;\n" +
                "        text-align: center;\n" +
                "        box-sizing: border-box;\n" +
                "        position: relative;\n" +
                "    }\n" +
                "    .header-logo {\n" +
                "        font-size: 28px;\n" +
                "        font-weight: bold;\n" +
                "        color: #38BDF8;\n" +
                "        margin-bottom: 20px;\n" +
                "        text-transform: uppercase;\n" +
                "        letter-spacing: 2px;\n" +
                "    }\n" +
                "    .title {\n" +
                "        font-size: 36px;\n" +
                "        color: #F59E0B;\n" +
                "        margin-bottom: 10px;\n" +
                "        font-style: italic;\n" +
                "    }\n" +
                "    .subtitle {\n" +
                "        font-size: 16px;\n" +
                "        color: #94A3B8;\n" +
                "        margin-bottom: 40px;\n" +
                "        text-transform: uppercase;\n" +
                "        letter-spacing: 1px;\n" +
                "    }\n" +
                "    .user-name {\n" +
                "        font-size: 40px;\n" +
                "        color: #FFFFFF;\n" +
                "        font-weight: bold;\n" +
                "        margin-bottom: 20px;\n" +
                "        border-bottom: 2px solid #334155;\n" +
                "        display: inline-block;\n" +
                "        padding-bottom: 10px;\n" +
                "    }\n" +
                "    .course-details {\n" +
                "        font-size: 18px;\n" +
                "        color: #E2E8F0;\n" +
                "        margin-bottom: 30px;\n" +
                "        line-height: 1.6;\n" +
                "    }\n" +
                "    .course-title {\n" +
                "        color: #38BDF8;\n" +
                "        font-weight: bold;\n" +
                "        font-size: 22px;\n" +
                "    }\n" +
                "    .footer-section {\n" +
                "        display: flex;\n" +
                "        justify-content: space-between;\n" +
                "        align-items: flex-end;\n" +
                "        margin-top: 50px;\n" +
                "        padding: 0 40px;\n" +
                "    }\n" +
                "    .signature-block {\n" +
                "        text-align: center;\n" +
                "        width: 200px;\n" +
                "    }\n" +
                "    .signature-line {\n" +
                "        border-top: 1px solid #94A3B8;\n" +
                "        margin-top: 40px;\n" +
                "        color: #CBD5E1;\n" +
                "        font-size: 14px;\n" +
                "        padding-top: 5px;\n" +
                "    }\n" +
                "    .qr-code {\n" +
                "        width: 100px;\n" +
                "        height: 100px;\n" +
                "        border: 2px solid #F59E0B;\n" +
                "        padding: 5px;\n" +
                "        background-color: white;\n" +
                "    }\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"certificate-container\">\n" +
                "        <div class=\"header-logo\">Vietsync Learning</div>\n" +
                "        <div class=\"title\">Certificate of Completion</div>\n" +
                "        <div class=\"subtitle\">Chứng Nhận Hoàn Thành Khóa Học</div>\n" +
                "        <div class=\"course-details\">Chứng nhận này được trân trọng trao cho</div>\n" +
                "        <div class=\"user-name\">" + userName + "</div>\n" +
                "        <div class=\"course-details\">\n" +
                "            Vì đã xuất sắc hoàn thành khóa học chuyên sâu<br>\n" +
                "            <span class=\"course-title\">" + courseTitle + "</span><br>\n" +
                "            với tổng thời lượng học tập là <b>" + hours + " giờ</b> học tập lý thuyết & thực hành.\n" +
                "        </div>\n" +
                "        <div class=\"footer-section\">\n" +
                "            <div class=\"signature-block\">\n" +
                "                <div style=\"font-family: 'Brush Script MT', cursive; font-size: 24px; color: #38BDF8;\">" + instructorName + "</div>\n" +
                "                <div class=\"signature-line\">Instructor</div>\n" +
                "            </div>\n" +
                "            <div>\n" +
                "                <img class=\"qr-code\" src=\"data:image/png;base64," + qrCodeBase64 + "\" alt=\"QR Code Verification\" />\n" +
                "                <div style=\"font-size: 10px; color: #94A3B8; margin-top: 5px;\">Mã xác thực trực tuyến</div>\n" +
                "            </div>\n" +
                "            <div class=\"signature-block\">\n" +
                "                <div style=\"font-size: 16px; color: #E2E8F0; font-weight: bold;\">" + date + "</div>\n" +
                "                <div class=\"signature-line\">Date Issued</div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
