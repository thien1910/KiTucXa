# Hệ thống quản lý ký túc xá (Backend)

## Giới Thiệu

Dự án này là một hệ thống quản lý ký túc xá trên nền tảng web, được xây dựng bằng Spring Boot (Java). Hệ thống giúp quản lý sinh viên, hợp đồng, hóa đơn, dịch vụ tiện ích và các phòng ở trong ký túc xá một cách hiệu quả.

## Tính Năng

- Xác thực người dùng (đăng nhập/đăng xuất)
- Quản lý thông tin cá nhân
- Quản lý sinh viên và hợp đồng
- Quản lý hóa đơn và dịch vụ tiện ích
- Quản lý phòng ở
- Hệ thống báo cáo và thống kê
- Hỗ trợ thanh toán hóa đơn trực tuyến
- Giao diện API RESTful

## Cài Đặt

### Yêu Cầu

- Phiên bản Java 21
- Maven
- Cơ sở dữ liệu MySQL

### Sao Chép Kho Lưu Trữ

```sh
git clone https://github.com/thien1910/KiTucXa.git
cd KTX
```

### Cài Đặt Các Gói Phụ Thuộc

```sh
./mvnw clean install
```

### Chạy Ứng Dụng

```sh
./mvnw spring-boot:run
```

Ứng dụng sẽ chạy tại `http://localhost:8080`.

## Testing

Dự án bao gồm các bài kiểm thử bằng JUnit và Mockito. Để chạy kiểm thử, sử dụng lệnh:

```sh
./mvnw test
```

## Cấu Trúc Thư Mục

```
KyTucXa/
│── src/
│   ├── main/
│   │   ├── java/com/project/KyTucXa/       # Mã nguồn chính
│   │   ├── resources/                      # File cấu hình
│   ├── test/                               # Kiểm thử
│── pom.xml                                 # File cấu hình Maven
│── README.md                               # Tài liệu dự án
```

## Người Đóng Góp

- Nguyễn Đình Việt - Developer
- Nguyễn Sơn Tùng - Developer
- Nguyễn Minh Huy - Developer
- Nguyễn Văn Thiện - Developer
- Trần Mai Trung - Developer

## Giảng Viên Hướng Dẫn

- Vũ Việt Vũ
- Trương Anh Hoàng
- Đặng Quốc Hữu

## Liên Hệ

Nếu bạn có bất kỳ câu hỏi hoặc góp ý nào, vui lòng liên hệ qua email hoặc GitHub của nhóm phát triển.
