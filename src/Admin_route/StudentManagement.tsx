import React, { useState, useEffect } from "react";
import "./StudentManagement.css";

interface Student {
  userId: string;
  maSV: string | null;
  userName: string;
  passWord: string;
  fullName: string | null;
  gender: string | null;
  roomNameStudent: string | null;
  cccd: string | null;
  phoneNumber: string | null;
  status: string | null;
  country: string | null;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

const StudentManagement: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [room, setRoom] = useState("");

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  useEffect(() => {
    const token = localStorage.getItem("token"); // Lấy token từ localStorage
    if (!token) {
      console.error("Vui lòng đăng nhập trước khi truy cập trang này.");
      alert("Vui lòng đăng nhập trước khi truy cập trang này.");
      return;
    }
  
    const fetchStudents = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/v1/user/list", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`, // Đính kèm token
            "Content-Type": "application/json",
          },
        });
  
        if (!response.ok) {
          throw new Error("Lỗi khi lấy dữ liệu!");
        }
  
        const data = await response.json();
        console.log("Dữ liệu nhận được:", data); // Kiểm tra dữ liệu API
  
        // Đảm bảo dữ liệu là một mảng trước khi cập nhật state
        if (data.result && Array.isArray(data.result)) {
          // Lọc danh sách chỉ những user có role là "student"
          const filteredStudents = data.result.filter((student: Student) =>
            student.roles.includes("STUDENT")
          );
          setStudents(filteredStudents);
        } else {
          console.error("Dữ liệu API không hợp lệ:", data);
        }
      } catch (error) {
        console.error("Lỗi:", error);
      }
    };
  
    fetchStudents();
  }, []);
  

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const handleAssignRoom = () => {
    if (selectedStudent && room) {
      setStudents((prev) =>
        prev.map((s) =>
          s.userId === selectedStudent.userId ? { ...s, room } : s
        )
      );
      setSelectedStudent(null);
      setRoom("");
    }
  };

  return (
    <div className="student-management">
      <h2>Quản lý sinh viên</h2>

      <input
        type="text"
        placeholder="Tìm kiếm sinh viên..."
        value={searchTerm}
        onChange={handleSearch}
      />

      <table>
        <thead>
          <tr>
            <th>Mã SV</th>
            <th>Họ tên</th>
            <th>Tên đăng nhập</th>
            <th>Giới tính</th>
            <th>CCCD</th>
            <th>Số điện thoại</th>
            <th>Phòng</th>
            <th>Trạng thái</th>
            <th>Quốc gia</th>
            <th>Vai trò</th>
            <th>Ngày tạo</th>
            <th>Ngày cập nhật</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student) => (
            <tr key={student.userId}>
              <td>{student.maSV || "N/A"}</td>
              <td>{student.fullName || "Không có tên"}</td>
              <td>{student.userName}</td>
              <td>{student.gender || "Chưa cập nhật"}</td>
              <td>{student.cccd || "N/A"}</td>
              <td>{student.phoneNumber || "N/A"}</td>
              <td>{student.roomNameStudent || "Chưa có"}</td>
              <td>{student.status || "Chưa cập nhật"}</td>
              <td>{student.country || "Chưa cập nhật"}</td>
              <td>{student.roles.join(", ")}</td>
              <td>{new Date(student.createdAt).toLocaleString()}</td>
              <td>{new Date(student.updatedAt).toLocaleString()}</td>
              <td>
                <button onClick={() => setSelectedStudent(student)}>
                  Xếp phòng
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedStudent && (
        <div className="assign-room">
          <h3>Xếp phòng cho {selectedStudent.fullName || "Không có tên"}</h3>
          <input
            type="text"
            placeholder="Nhập số phòng"
            value={room}
            onChange={(e) => setRoom(e.target.value)}
          />
          <button onClick={handleAssignRoom}>Lưu</button>
          <button onClick={() => setSelectedStudent(null)}>Hủy</button>
        </div>
      )}
    </div>
  );
};

export default StudentManagement;