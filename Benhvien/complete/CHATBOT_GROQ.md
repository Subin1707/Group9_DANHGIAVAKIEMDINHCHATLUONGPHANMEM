# Chatbot Groq và kho tài liệu

## Chạy chatbot

Không đặt API key trong `application.properties`. Trong PowerShell, cấu hình key cho phiên terminal hiện tại rồi chạy ứng dụng:

```powershell
$env:GROQ_API_KEY="gsk_your_key_here"
mvn spring-boot:run
```

Model mặc định là `llama-3.3-70b-versatile`. Có thể đổi bằng biến môi trường `GROQ_MODEL`.

## Bổ sung kiến thức

Đặt các tài liệu `.md` hoặc `.txt` vào:

```text
src/main/resources/knowledge/
```

Sau khi sửa hoặc thêm tài liệu, khởi động lại ứng dụng. Hệ thống tự chia tài liệu thành các đoạn, tìm đoạn gần nhất với câu hỏi và đưa vào ngữ cảnh cho Groq. Đây là RAG cục bộ đơn giản; không gửi toàn bộ kho tài liệu trong mỗi câu hỏi và không cần fine-tune model.

Nên viết tài liệu với tiêu đề rõ ràng, mỗi chủ đề là một đoạn riêng. Không đặt API key, mật khẩu hoặc dữ liệu sức khỏe cá nhân vào thư mục tài liệu.
