# ỨNG DỤNG HỌC TOEIC

Ứng dụng Android được phát triển nhằm hỗ trợ người dùng cải thiện kỹ năng tiếng Anh và chuẩn bị cho kỳ thi TOEIC (Test of English for International Communication).

## 1. Tổng quan dự án (Project Overview)

- **Tên dự án:** Chưa đặt tên
- **Nền tảng:** Android
- **Ngôn ngữ lập trình:** Java
- **Thiết kế giao diện:** XML / Figma / Canva
- **IDE:** Android Studio
- **Số lượng thành viên:** 4

## 2. Thành viên nhóm (Team Members)

| STT | Họ và tên | Mã sinh viên | Vai trò |
|---|---|---|---|
| 1 | Trần Đông Bình | 745105011 | Trưởng nhóm |
| 2 | [Tên] | [Mã SV] | Thành viên |
| 3 | Vũ Thảo Ly | 745105064 | Thành viên |
| 4 | Trần Văn Sơn | 745105082 | Thành viên |

## 3. Các chức năng chính (Main Features)

### 3.1. Xác thực người dùng (User Authentication)

- Đăng ký tài khoản.
- Đăng nhập và đăng xuất.
- Quản lý thông tin cá nhân.

### 3.2. Học từ vựng (Vocabulary Learning)

- **Học theo chủ đề (Topic-based Learning):** Duyệt các chủ đề từ vựng và xem danh sách từ thuộc từng chủ đề.
- **Thẻ ghi nhớ (Flashcards):** Học từ vựng thông qua các thẻ được hiển thị ngẫu nhiên.
- **Đánh giá mức độ ghi nhớ (Learning Assessment):** Đánh giá khả năng ghi nhớ từ vựng thông qua ba lựa chọn:
    - **Dễ (Easy):** Ôn tập sau 3 ngày.
    - **Khó (Hard):** Ôn tập sau 10–20 phút.
    - **Không nhớ (Can't Remember):** Ôn tập sau 1–5 phút.
- **Ôn tập từ vựng (Vocabulary Review):** Luyện tập các từ đã học thông qua những bài kiểm tra tương tác, bao gồm điền vào chỗ trống và câu hỏi trắc nghiệm.

### 3.3. Học ngữ pháp (Grammar Learning)

- **Chủ đề ngữ pháp (Grammar Topics):** Duyệt các bài học ngữ pháp được phân loại theo từng chủ đề.
- **Bài học ngữ pháp (Grammar Lessons):** Xem các quy tắc, giải thích, cấu trúc và ví dụ ngữ pháp.
- **Luyện tập ngữ pháp (Grammar Practice):** Củng cố kiến thức ngữ pháp thông qua các bài tập tương tác.
- **Kiểm tra (Quiz):** Đánh giá mức độ hiểu bài bằng nhiều dạng câu hỏi, bao gồm trắc nghiệm và điền vào chỗ trống.
- **Theo dõi tiến độ học tập (Learning Progress):** Theo dõi các bài học đã hoàn thành và kết quả bài kiểm tra.

### 3.4. Luyện thi TOEIC (TOEIC Practice)

- **Luyện nghe (Listening Practice):** Luyện kỹ năng nghe TOEIC thông qua các Part 1–4.
- **Luyện đọc (Reading Practice):** Luyện kỹ năng đọc TOEIC thông qua các Part 5–7.
- **Luyện tập theo từng Part (Part-based Practice):** Cho phép người dùng lựa chọn từng Part riêng biệt để tập trung cải thiện kỹ năng cụ thể.
- **Phản hồi tức thì (Instant Feedback):** Hiển thị đáp án đúng, giải thích và kết quả sau khi hoàn thành bài tập.

### 3.5. Thi thử TOEIC (Mock Tests)

- **Bài thi TOEIC đầy đủ (Full-length TOEIC Tests):** Thực hiện các bài thi thử hoàn chỉnh bao gồm Listening và Reading.
- **Đồng hồ đếm ngược (Countdown Timer):** Theo dõi và quản lý thời gian làm bài.
- **Tự động chấm điểm (Automatic Scoring):** Tự động tính điểm sau khi nộp bài.
- **Kết quả chi tiết (Detailed Results):** Xem điểm số, đáp án đúng và các câu trả lời sai sau khi hoàn thành bài thi.

### 3.6. Thống kê học tập (Learning Statistics)

- **Lịch sử thi (Test History):** Xem lại các lần thi trước đó và kết quả tương ứng.
- **Theo dõi điểm số (Score Tracking):** Theo dõi sự thay đổi điểm TOEIC theo thời gian.
- **Tiến độ học tập (Learning Progress):** Theo dõi các hoạt động học tập và tiến độ tổng thể của người dùng.

## 4. Công nghệ sử dụng (Technologies)

- Java
- XML
- Android Studio
- Android SDK
- Git & GitHub
- [SQLite / Room / Firebase]

## 5. Cấu trúc dự án (Project Structure)

```text
TOEIC_App/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           └── res/
├── gradle/
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 6. Tiến độ phát triển (Development Progress)

- [ ] Phân tích yêu cầu (Requirement Analysis)
- [ ] Thiết kế giao diện (UI/UX Design)
- [ ] Thiết kế cơ sở dữ liệu (Database Design)
- [ ] Xác thực người dùng (User Authentication)
- [ ] Chức năng học từ vựng (Vocabulary Module)
- [ ] Chức năng học ngữ pháp (Grammar Module)
- [ ] Chức năng luyện thi TOEIC (TOEIC Practice Module)
- [ ] Chức năng thi thử (Mock Test Module)
- [ ] Chức năng thống kê (Statistics Module)
- [ ] Kiểm thử và sửa lỗi (Testing and Debugging)
- [ ] Hoàn thiện và phát hành ứng dụng (Final Release)

## 7. Hướng dẫn cài đặt (Installation)

Thực hiện các bước sau để thiết lập và chạy dự án trên máy tính cá nhân.

### Bước 1. Sao chép repository (Clone the repository)

```bash
git clone https://github.com/MonkeyFlash/Final_Project_TOEIC_app.git
```

### Bước 2. Mở dự án (Open the project)

- Khởi động Android Studio.
- Chọn **Open** và tìm đến thư mục dự án vừa clone về.
- Chờ quá trình Gradle Sync hoàn tất.

### Bước 3. Cấu hình Android SDK (Configure the Android SDK)

- Đảm bảo các thành phần Android SDK cần thiết đã được cài đặt.
- Cài đặt bổ sung các thành phần SDK còn thiếu nếu Android Studio yêu cầu.

### Bước 4. Chạy ứng dụng (Run the application)

- Kết nối thiết bị Android thông qua USB và bật chế độ USB Debugging, hoặc khởi động Android Emulator.
- Nhấn nút **Run** trong Android Studio để biên dịch và khởi chạy ứng dụng.

## 8. Quy trình làm việc nhóm (Team Workflow)

Để đảm bảo quá trình làm việc nhóm diễn ra hiệu quả và duy trì chất lượng mã nguồn, tất cả thành viên cần tuân thủ các quy định sau:

### 8.1. Quản lý công việc (Task Management)

- Mỗi thành viên được phân công những nhiệm vụ và trách nhiệm cụ thể.
- Hoàn thành các nhiệm vụ được giao theo thời hạn đã thống nhất.
- Thường xuyên cập nhật tiến độ công việc để mọi người nắm được những phần đã hoàn thành và những phần đang thực hiện.
- Thông báo cho trưởng nhóm khi gặp khó khăn hoặc có nguy cơ chậm tiến độ.

### 8.2. Git & GitHub

- Toàn bộ mã nguồn dự án phải được quản lý và lưu trữ trên repository GitHub của nhóm.
- Nhánh `main` là phiên bản chính thức của dự án. Không chỉnh sửa trực tiếp trên nhánh này.
- Trước khi thực hiện một chức năng, mỗi thành viên cần tạo một nhánh riêng từ phiên bản `main` mới nhất.
- Sử dụng quy tắc đặt tên nhánh theo định dạng: `feature/feature-name`.
- Thường xuyên commit và push những thay đổi lên GitHub với nội dung commit rõ ràng, có ý nghĩa.
- Trước khi bắt đầu công việc mới, cần pull những thay đổi mới nhất để tránh làm việc trên phiên bản mã nguồn đã cũ.
- Sau khi hoàn thành một chức năng, thành viên cần tạo Pull Request (PR) để đề xuất hợp nhất mã nguồn vào nhánh `main`.
- Tất cả Pull Request phải được trưởng nhóm kiểm tra và phê duyệt trước khi tiến hành merge.

### 8.3. Quy định lập trình (Coding Rules)

- Sử dụng Java để xử lý logic ứng dụng và XML để xây dựng giao diện người dùng.
- Tuân thủ quy tắc đặt tên thống nhất đối với class, method, biến và các file layout.
- Sắp xếp các file theo cấu trúc dự án đã được thống nhất.
- Không tự ý chỉnh sửa mã nguồn của thành viên khác khi chưa trao đổi và thống nhất.
- Hạn chế viết trùng lặp mã nguồn hoặc tạo các file không cần thiết.

### 8.4. Kiểm tra và đánh giá mã nguồn (Code Review & Testing)

- Kiểm tra chức năng trước khi tạo Pull Request.
- Rà soát mã nguồn và kiểm tra lỗi trước khi merge.
- Đảm bảo ứng dụng vẫn hoạt động bình thường sau khi tích hợp các chức năng.
- Khắc phục những lỗi được phát hiện trước khi xác nhận hoàn thành nhiệm vụ.

### 8.5. Trao đổi và phối hợp nhóm (Team Communication)

- Trao đổi với các thành viên trước khi triển khai những thay đổi quan trọng.
- Chủ động yêu cầu hỗ trợ khi gặp khó khăn thay vì để công việc bị gián đoạn.
- Thường xuyên thông báo cho các thành viên về tiến độ và những vấn đề có thể phát sinh.
- Cập nhật tài liệu dự án mỗi khi có thay đổi lớn về chức năng hoặc cấu trúc.

## 9. Giấy phép và mục đích sử dụng (License)

Dự án được phát triển với mục đích học tập, phục vụ cho môn học Lập trình Android.
