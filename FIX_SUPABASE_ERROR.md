# Hướng dẫn khắc phục lỗi kết nối Supabase (Lỗi tạo tài khoản / Đăng nhập)

Nếu bạn gặp thông báo lỗi màu đỏ khi đăng ký hoặc đăng nhập tài khoản:
> **"Không có kết nối mạng. Vui lòng thử lại. Chi tiết: Unable to resolve host..."**

Đây là lỗi do cấu hình địa chỉ URL của Supabase bị sai chính tả trong file cục bộ của bạn (`local.properties`). Hãy thực hiện các bước sau để khắc phục.

---

## Các bước xử lý:

### Bước 1: Cập nhật lại file `local.properties`
Mở file `local.properties` ở thư mục gốc của dự án (hoặc tạo mới nếu chưa có) và cập nhật lại thông tin chính xác như sau:

```properties
# Địa chỉ SDK của bạn (giữ nguyên cấu hình cũ của máy bạn)
sdk.dir=C\:\\Users\\Tên_User\\AppData\\Local\\Android\\Sdk

# 1. Cập nhật lại URL chính xác (Chú ý: qpvblttrxfkahfybpdvu - có chữ r và x đứng liền nhau)
supabase.url=https://qpvblttrxfkahfybpdvu.supabase.co

# 2. Key Anon (Giữ nguyên hoặc lấy từ local.properties.example)
supabase.anon.key=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFwdmJsdHRyeGZrYWhmeWJwZHZ1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODE5NTgwNjEsImV4cCI6MjA5NzUzNDA2MX0.KRcJ9U_mgaZbH4t2TWhGHU-mho1vin7_1TnG9XoCZIk
```

*Lưu ý: Domain đúng phải là `qpvblttrxfkahfybpdvu` (chữ `r` và `x` đứng cạnh nhau: `ttrx`). Bản cũ bị gõ sai thành `ttry` hoặc `ttryx`.*

---

### Bước 2: Đồng bộ và Rebuild lại ứng dụng trong Android Studio
Vì file cấu hình này được Gradle đọc và tạo ra class `BuildConfig` lúc build nên sau khi sửa xong, bạn **phải rebuild** để áp dụng thay đổi:

1. Trên thanh công cụ Android Studio, chọn: **Build** -> **Clean Project**.
2. Chọn tiếp: **Build** -> **Rebuild Project**.
3. Chạy lại ứng dụng (Run) trên máy ảo hoặc thiết bị vật lý của bạn.

---

Sau khi thực hiện 2 bước trên, ứng dụng sẽ kết nối được tới cơ sở dữ liệu Supabase và bạn có thể đăng ký/đăng nhập tài khoản bình thường.
