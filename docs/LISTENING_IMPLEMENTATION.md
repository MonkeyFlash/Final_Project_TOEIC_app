# Thiết kế và triển khai Listening demo

Listening dùng bốn collection riêng, bốn model dữ liệu và một màn hình luyện nghe chung. Luồng tương tự Part 5: chọn Part, đọc giới thiệu, nhấn Start, làm bài, nộp và xem lại. Reading vẫn dùng `Question.java`, collection `questions` và các Activity hiện có.

## Luồng màn hình

```text
HomeFragment
  → ListeningList
  → ListeningIntro(part)
  → ListeningLearn(session)
  → ListeningResult(session, answers)
  → ListeningLearn(session, answers, review=true)
```

`ListeningList` dùng lại giao diện bốn Part. `ListeningIntro` được dựng từ mẫu giới thiệu Part 5. `ListeningLearn` mở rộng XML Listening hiện có bằng vùng câu hỏi, Previous/Next và giải thích. `ListeningResult` dùng trực tiếp `activity_part5_result.xml` mà không sửa layout hay Activity kết quả của Reading.

Mỗi lượt demo lấy một câu cho Part 1 hoặc 2, một bộ ba câu cho Part 3 hoặc 4. Không thêm màn hình danh sách bài trong giai đoạn này.

## Quy tắc tên và cấu trúc dữ liệu

| Collection | Model | Một document |
| --- | --- | --- |
| `listening_part1` | `ListeningPart1Question` | Một câu có ảnh và bốn lựa chọn nghe |
| `listening_part2` | `ListeningPart2Question` | Một câu có ba lựa chọn nghe |
| `listening_part3` | `ListeningPart3Set` | Một hội thoại và mảng ba câu hỏi |
| `listening_part4` | `ListeningPart4Set` | Một bài nói và mảng ba câu hỏi |

Tên trường dùng camelCase. Document có ID ổn định theo nguồn, phiên bản đề và ID nguồn. Không dùng `add()` tạo ID ngẫu nhiên khi nhập lại cùng mẫu.

Các trường chung:

```text
schemaVersion: number
part: number
title: string
order: number
status: "draft" hoặc "ready"
demoOnly: boolean
audioUrl: HTTPS URL
audioText: string, để rỗng nếu chưa có transcript
imageUrl: HTTPS URL hoặc chuỗi rỗng
questionCount: number
sourceUrl, sourceTestId, sourceQuestionId
answerStatus, mediaStatus, rightsStatus
```

Part 1–2 chứa trực tiếp `question`, `questionNumber`, `correctAnswer`, `explanation`, các trường `optionA` đến `optionD` tương ứng số lựa chọn, và `answerSourceUrls`. Part 2 không cần `optionD` trên Firestore. Nội dung lựa chọn nghe có thể rỗng khi nguồn chỉ cung cấp nhãn.

Part 3–4 có mảng `questions`. Mỗi phần tử ánh xạ sang `ListeningSubQuestion`, gồm `id`, `questionNumber`, `question`, bốn lựa chọn, `correctAnswer`, `explanation` và thông tin đối chiếu đáp án. Audio nằm ở document bộ, không nhân bản trong từng câu con.

`ListeningSession` là snapshot nhỏ cho một lượt làm bài, gom bốn kiểu dữ liệu về một cấu trúc để giao diện và chấm điểm dùng chung. Snapshot giữ nguyên thứ tự câu và được lưu vào saved state; không thay thế bốn model Firestore.

## Dữ liệu demo và nhập Firestore

Mẫu có bốn document, tổng tám câu: số gốc 1, 7, 32–34 và 71–73. Audio/ảnh được tham chiếu từ [Test 1 năm 2026 của Zenlish](https://zenlishtoeic.vn/stm-quizzes/test-1-2026/). Không tải toàn bộ đề hoặc đưa media vào APK.

Đáp án đối chiếu cho mẫu:

| Câu | Đáp án | Nguồn đối chiếu |
| --- | --- | --- |
| 1 và 7 | B và B | [Tài liệu script và key](https://www.studocu.vn/vn/document/truong-dai-hoc-van-lang/toan-cao-cap/ets-2026-listening-test-1-script-and-answer-key/152320500) |
| 32, 33, 34 | B, D, B | Tài liệu trên và [flashcard cùng câu hỏi](https://quizlet.com/vn/1225429887/nghe-toeic-k12-flash-cards/) |
| 71, 72, 73 | B, C, A | [Tài liệu giải Listening](https://www.scribd.com/document/1068422208/ETS-2026-Gi%E1%BA%A3i-Chi-Ti%E1%BA%BFt-Full-LC-Test-1) |

Đây là đối chiếu tài liệu bên thứ ba, chưa phải đáp án trả về từ máy chủ Zenlish hoặc kết quả nghe kiểm duyệt độc lập toàn bộ mẫu. Giữ `status=draft`, `answerStatus=cross_referenced`, `demoOnly=true`. Màn hình giới thiệu hiển thị tình trạng demo. Transcript và giải thích đang để rỗng, không suy diễn nội dung chưa có.

URL audio và ảnh đã kiểm tra HEAD trả về đúng loại media; việc này xác nhận truy cập URL ở thời điểm kiểm tra, không chứng minh đầy đủ nội dung hoặc quyền phân phối lại. Giữ `rightsStatus=not_verified_for_redistribution` đến khi có cơ sở sử dụng cho phát hành.

Các file:

- `data/listening_demo/zenlish_demo.json`: manifest để nhập.
- `data/listening_demo/media_check.json`: kết quả kiểm tra URL, chỉ lưu local và được Git ignore.
- `data/listening_demo/firestore_setup.json`: biên nhận thiết lập Firebase, chỉ lưu local và được Git ignore.
- `scripts/import_listening_demo.cjs`: kiểm tra và nhập mẫu.

Chạy kiểm tra local:

```bash
node scripts/import_listening_demo.cjs
```

Sau khi đăng nhập Firebase CLI, nhập bằng phiên quản trị local:

```bash
node scripts/import_listening_demo.cjs --apply --firebase-cli-dir /duong/dan/node_modules/firebase-tools
```

Script mặc định không ghi mạng. Khi dùng `--apply`, script chỉ cho phép project `login-register-toeic`, database mặc định và bốn collection đã chốt. Nó kiểm tra document cũ trước; mẫu giống hệt được bỏ qua, mẫu khác nội dung khiến script dừng. Các document thiếu được tạo trong một commit với điều kiện chưa tồn tại, sau đó đọc lại để xác nhận. Không đưa token hay mã đăng nhập vào manifest.

Collection được Firestore tạo khi document đầu tiên được ghi. Bản demo đọc đúng ID mẫu đã khai báo trong `ListeningRepository`, không truy vấn ngẫu nhiên các document draft. Khi mở rộng, thay lựa chọn bài trong Repository; không thay ID mẫu chỉ ở Firestore mà quên cập nhật phần chọn bài.

## Quy tắc làm bài và giao diện

1. Part 1 hiển thị ảnh bằng `fitCenter`, giữ đầy đủ hình và chỉ hiện nhãn A–D khi đang làm.
2. Part 2 ẩn ảnh nếu không có, ẩn D và chỉ hiện A–C khi đang làm.
3. Part 3–4 hiển thị nội dung câu hỏi và lựa chọn. Previous/Next chuyển câu trong bộ, không đổi nguồn audio.
4. Mỗi câu chỉ có một lựa chọn tại một thời điểm. Người dùng có thể sửa trước khi nộp và có thể bỏ qua câu.
5. Nộp bài có xác nhận và số câu bỏ qua. Không hiện đáp án đúng trước khi nộp.
6. Kết quả hiển thị đúng, sai, bỏ qua và tỷ lệ đúng. Không quy đổi sang điểm TOEIC.
7. Xem lại dùng snapshot bài và đáp án, không phụ thuộc Activity làm bài cũ còn sống. Khóa sửa lựa chọn và hiện giải thích/transcript nếu có.
8. Tái tạo Activity khôi phục bài, lựa chọn, câu hiện tại, chế độ xem lại, vị trí audio và tốc độ. Khi quay lại, audio chờ người dùng nhấn phát.
9. Back trong bài chưa nộp có xác nhận thoát. Không lưu lịch sử, bookmark hoặc thống kê trong demo. Các số liệu giả và nút bookmark được ẩn.

## Audio

`ListeningAudioPlayer` dùng MediaPlayer, chuẩn bị bất đồng bộ, timeout tải 25 giây và nút thử tải lại khi lỗi. Play/Pause, Replay, SeekBar, thời gian và tốc độ 0.75x, 1x, 1.25x, 1.5x dùng cùng player. Player yêu cầu audio focus khi phát, tạm dừng khi mất focus và giải phóng khi Activity dừng. Các thao tác player bị vô hiệu khi chưa chuẩn bị xong.

Theo [vòng đời MediaPlayer của Android](https://developer.android.com/media/platform/mediaplayer/state-resources), việc phát, tua và giải phóng tài nguyên phải gắn với trạng thái player. Audio không chạy nền; khi trở lại màn hình, app tải lại và khôi phục vị trí đã giữ.

## Quyền truy cập Firestore

`firebase/firestore.rules` thêm đúng bốn match cho Listening: người đã đăng nhập được đọc; client không được ghi nội dung câu hỏi. Khối quyền `users` và `questions` được giữ nguyên. Bản sao lưu trước thay đổi, nếu có trên máy, nằm ở `firebase/firestore.before-listening.rules`; file này chỉ lưu local và được Git ignore.

Các file rules là snapshot tại lần thiết lập này. Trước khi deploy lại phải đối chiếu Rules đang áp dụng để không ghi đè cập nhật của thành viên khác. Không mở quyền đọc/ghi toàn bộ database để chạy demo.

## Các bước triển khai và kiểm chứng

| Bước | Kết quả |
| --- | --- |
| Chuẩn hóa mẫu | Bốn document, nguồn đối chiếu, trạng thái draft và kiểm tra URL |
| Thiết lập Firebase | Tạo document, đọc lại xác nhận, bổ sung quyền đọc xác thực |
| Model và Repository | Bốn model, model câu con, snapshot lượt làm và kiểm tra dữ liệu |
| Điều hướng và giao diện | Chọn Part, giới thiệu, Start, làm bài, kết quả, xem lại |
| Audio | Phát/tạm dừng, replay, tua, tốc độ, lỗi, lifecycle |
| Chấm điểm và khôi phục | Đúng/sai/bỏ qua, sửa trước nộp, khóa sau nộp, saved state |
| Kiểm thử | Unit test chấm điểm/serialization; kiểm thử UI và đọc Firestore thật; phát audio Part 3 qua mạng |

Build và unit test:

```bash
JAVA_HOME=/opt/android-studio/jbr bash gradlew :app:assembleDebug :app:testDebugUnitTest --offline
```

Kiểm thử Android dùng `ListeningFlowTest` và `ListeningFirebaseTest`. Bài Firebase chỉ đọc dữ liệu và cần app đã đăng nhập trên emulator; nếu chưa đăng nhập thì bài này bị bỏ qua. Khi UTP chưa có trong cache, build `:app:assembleDebugAndroidTest`, cài hai APK bằng ADB rồi chạy AndroidJUnitRunner trực tiếp.

Trước khi chuyển từ demo sang dữ liệu chính thức: kiểm duyệt đủ audio/ảnh và đáp án, bổ sung transcript/giải thích có nguồn, xác nhận phạm vi sử dụng media, rồi chuyển trạng thái sang ready và thay bộ chọn bài demo. Khi tăng số câu, Part 3–4 luôn được chọn theo bộ; không trộn riêng từng câu con.
