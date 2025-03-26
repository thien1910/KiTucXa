import React, { useState, useEffect } from "react";
import "./StudentManagement.css";
import { Modal, Form, Input, DatePicker, Select, Button, message, Alert } from "antd";
import moment from "moment";
import { useNavigate } from "react-router-dom";

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

interface Room {
  roomId: string;
  roomName: string;
  roomStatus: string;
  roomPrice: number;
}

const StudentManagement: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [activeContract, setActiveContract] = useState<any>(null);
  const [contractError, setContractError] = useState<string>("");

  // State cho Modal Xếp phòng
  const [assignModalVisible, setAssignModalVisible] = useState(false);
  const [availableRooms, setAvailableRooms] = useState<Room[]>([]);
  
  // Sử dụng form của Ant Design cho Modal Xếp phòng
  const [assignForm] = Form.useForm();

  const token = localStorage.getItem("token");

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("Vui lòng đăng nhập trước khi truy cập trang này.");
      alert("Vui lòng đăng nhập trước khi truy cập trang này.");
      return;
    }

    const fetchStudents = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/v1/user/manager/list", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error("Lỗi khi lấy dữ liệu!");
        }

        const data = await response.json();
        console.log("Dữ liệu nhận được:", data);

        if (data.result && Array.isArray(data.result)) {
          // Lọc theo role STUDENT
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

  // Hàm lấy danh sách phòng còn chỗ trống (giả sử phòng có roomStatus === "empty_room")
  const fetchAvailableRooms = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/rooms/list", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error("Lỗi khi lấy danh sách phòng");
      const data = await response.json();
      const available = data
        .filter((room: any) => room.roomStatus === "empty_room")
        .map((room: any) => ({
          roomId: room.roomId,
          roomName: room.roomName,
          roomStatus: room.roomStatus,
          roomPrice: room.roomPrice,
        }));
      setAvailableRooms(available);
    } catch (error) {
      console.error("Lỗi:", error);
    }
  };

  // Khi nhấn nút "Xếp phòng" cho sinh viên
  const handleAssignButton = async (student: Student) => {
    try {
      const contractResponse = await fetch(
        `http://localhost:8080/api/v1/contracts/user/${student.userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );
  
      if (!contractResponse.ok) {
        throw new Error("Lỗi khi lấy hợp đồng của sinh viên");
      }
  
      const contractData = await contractResponse.json();
  
      // Giả sử API trả về hợp đồng ở dạng contractData.result là một mảng hợp đồng
      const active = contractData.result && Array.isArray(contractData.result)
        ? contractData.result.find((contract: any) => contract.contractStatus === "Active")
        : null;
  
      // Lưu thông tin hợp đồng còn hiệu lực vào state
      setActiveContract(active);
    } catch (error) {
      console.error("Lỗi khi kiểm tra hợp đồng:", error);
      setActiveContract(null);
      message.error("Không thể kiểm tra hợp đồng của sinh viên!");
    }
  
    // Hiển thị modal luôn, cho dù có hợp đồng hay không
    setSelectedStudent(student);
    fetchAvailableRooms();
    assignForm.resetFields();
    setAssignModalVisible(true);
  };

  // Hàm xử lý khi xác nhận xếp phòng và tạo hợp đồng
  const handleAssignRoom = async (values: any) => {
    if (!selectedStudent) return;
    try {
      // Reset lỗi trước khi bắt đầu xử lý
      setContractError("");
      
      // Tìm phòng được chọn từ availableRooms để lấy giá thuê tự động
      const selectedRoom = availableRooms.find((room) => room.roomId === values.roomId);
      if (!selectedRoom) {
        setContractError("Phòng được chọn không hợp lệ!");
        return;
      }
      // Tạo payload cho hợp đồng, lấy giá thuê từ selectedRoom.roomPrice
      const payload = {
        userId: selectedStudent.userId,
        roomId: selectedRoom.roomId,
        startDate: values.startDate.format("YYYY-MM-DD"),
        endDate: values.endDate.format("YYYY-MM-DD"),
        price: String(selectedRoom.roomPrice),
        depositStatus: "COMPLETED",
        contractStatus: "Active",
        note: values.note || "",
      };
  
      const response = await fetch("http://localhost:8080/api/v1/contracts/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });
  
      const result = await response.json();
  
      if (result.code === 1000) {
        message.success("Tạo hợp đồng thành công!");
        setAssignModalVisible(false);
        setSelectedStudent(null);
      } else {
        if (
          result.message &&
          result.message.includes("User still has an active contract") &&
          result.activeContractId
        ) {
          setContractError(
            `Lỗi khi tạo hợp đồng: ${result.message}. Hợp đồng hiện tại: ${result.activeContractId}`
          );
        } else {
          setContractError("Lỗi khi tạo hợp đồng: " + (result.message || "Không rõ lỗi"));
        }
      }
    } catch (error: any) {
      setContractError("Tạo hợp đồng thất bại: " + (error.message || "Lỗi không xác định"));
      console.error("Lỗi:", error);
    }
  };

  // Hàm tìm kiếm chỉ theo username
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  // Lọc sinh viên theo username
  const filteredStudents = students.filter((student) =>
    student.userName.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Hàm xem hợp đồng của sinh viên từ bảng danh sách
// ...
const navigate = useNavigate();

const handleViewContract = (student: Student) => {
  // Chuyển hướng đến trang quản lý hợp đồng với query parameter userId
  navigate(`/contracts?userId=${student.userId}`);
};



  return (
    <div className="student-management">
      <h2>Quản lý sinh viên</h2>
      <input
        type="text"
        placeholder="Tìm kiếm theo username..."
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
            <th>Trạng thái</th>
            <th>Quốc gia</th>
            <th>Vai trò</th>
            <th>Ngày tạo</th>
            <th>Ngày cập nhật</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {filteredStudents.map((student) => (
            <tr key={student.userId}>
              <td>{student.maSV || "N/A"}</td>
              <td>{student.fullName || "Không có tên"}</td>
              <td>{student.userName}</td>
              <td>{student.gender || "Chưa cập nhật"}</td>
              <td>{student.cccd || "N/A"}</td>
              <td>{student.phoneNumber || "N/A"}</td>
              <td>{student.status || "Chưa cập nhật"}</td>
              <td>{student.country || "Chưa cập nhật"}</td>
              <td>{student.roles.join(", ")}</td>
              <td>{new Date(student.createdAt).toLocaleString()}</td>
              <td>{new Date(student.updatedAt).toLocaleString()}</td>
              <td>
                <button onClick={() => handleAssignButton(student)}>
                  Xếp phòng
                </button>
                {/* Nút xem hợp đồng đã có của sinh viên */}
                <button onClick={() => handleViewContract(student)}>
                  Xem hợp đồng
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Modal Xếp phòng */}
      <Modal
        title={`Xếp phòng cho ${selectedStudent?.fullName || "sinh viên"}`}
        open={assignModalVisible}
        onCancel={() => {
          setAssignModalVisible(false);
          setSelectedStudent(null);
          setActiveContract(null);
          setContractError("");
        }}
        footer={null}
      >
        {activeContract ? (
          <div style={{ marginBottom: 16 }}>
            <Alert
              message="Sinh viên đã có hợp đồng hiệu lực"
              description={`Hợp đồng hiện tại: ${activeContract.contractId}`}
              type="warning"
              showIcon
            />
            <Button
              type="primary"
              style={{ marginTop: 8 }}
              onClick={() =>
                window.location.href = `/contracts/${activeContract.contractId}`
              }
            >
              Xem hợp đồng hiện có
            </Button>
          </div>
        ) : (
          contractError && (
            <Alert
              message="Lỗi"
              description={contractError}
              type="error"
              showIcon
              style={{ marginBottom: 16 }}
            />
          )
        )}

        <Form layout="vertical" form={assignForm} onFinish={handleAssignRoom}>
          <Form.Item
            label="Chọn phòng"
            name="roomId"
            rules={[{ required: true, message: "Vui lòng chọn phòng!" }]}
          >
            <Select placeholder="Chọn phòng">
              {availableRooms.map((room) => (
                <Select.Option key={room.roomId} value={room.roomId}>
                  {room.roomName}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="Ngày bắt đầu"
            name="startDate"
            rules={[{ required: true, message: "Vui lòng chọn ngày bắt đầu!" }]}
          >
            <DatePicker format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item
            label="Ngày kết thúc"
            name="endDate"
            rules={[{ required: true, message: "Vui lòng chọn ngày kết thúc!" }]}
          >
            <DatePicker format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item label="Ghi chú" name="note">
            <Input.TextArea />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Xác nhận
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default StudentManagement;
