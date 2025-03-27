import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./RoomDetail.css";

interface Room {
  roomId: string;
  userId: string;
  roomName: string;
  department: string;
  maximumOccupancy: number;
  currentOccupancy: number;
  roomType: string;
  roomPrice: number;
  roomStatus: string;
  note: string;
  createdAt?: string;
  updatedAt?: string;
}

interface UtilityService {
  utilityServiceId: string;
  serviceName: string;
  description: string;
  pricePerUnit: number;
  calculationUnit: string;
  status: string; // "Active" hoặc "Inactive"
  createdAt: string;
  updatedAt: string;
}

interface RoomService {
  roomServiceId: string;
  utilityServiceId: string;
  price: number;
}

// Interface cho thông tin sinh viên
interface Student {
  userId: string;
  fullName: string;
  studentCode: string;
}

const RoomDetail: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const [room, setRoom] = useState<Room | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Danh sách dịch vụ có sẵn (Utility Services)
  const [utilityServices, setUtilityServices] = useState<UtilityService[]>([]);
  // Danh sách dịch vụ đã thêm vào phòng (Room Services)
  const [roomServices, setRoomServices] = useState<RoomService[]>([]);

  // Danh sách sinh viên có hợp đồng còn hiệu lực
  const [activeStudents, setActiveStudents] = useState<Student[]>([]);

  // State cho modal thêm dịch vụ
  const [showModal, setShowModal] = useState<boolean>(false);
  const [selectedUtilityServiceId, setSelectedUtilityServiceId] =
    useState<string>("");
  const [selectedServiceDetail, setSelectedServiceDetail] =
    useState<UtilityService | null>(null);
  // Giá dịch vụ tự động lấy từ service detail
  const [price, setPrice] = useState<number>(0);
  const [messageText, setMessageText] = useState<string | null>(null);

  // Lấy roomId từ query string
  const queryParams = new URLSearchParams(location.search);
  const roomId = queryParams.get("roomId");

  // Lấy chi tiết phòng
  useEffect(() => {
    if (!roomId) {
      setError("Không có mã phòng.");
      setLoading(false);
      return;
    }
    const token = localStorage.getItem("token");
    const fetchRoomDetail = async () => {
      try {
        setLoading(true);
        const response = await fetch(
          `http://localhost:8080/api/v1/rooms/${roomId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setRoom(data);
        setError(null);
      } catch (err) {
        console.error("Error fetching room detail:", err);
        setError("Không thể tải thông tin phòng.");
      } finally {
        setLoading(false);
      }
    };

    fetchRoomDetail();
  }, [roomId]);

  // Lấy danh sách các dịch vụ (cho dropdown)
  useEffect(() => {
    const token = localStorage.getItem("token");
    const fetchUtilityServices = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/utility-services/list",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setUtilityServices(data);
      } catch (err) {
        console.error("Error fetching utility services:", err);
      }
    };

    fetchUtilityServices();
  }, []);

  // Lấy danh sách dịch vụ của phòng
  const fetchRoomServices = async () => {
    if (!roomId) return;
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/room-services/room/${roomId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const data = await response.json();
      setRoomServices(Array.isArray(data) ? data : [data]);
    } catch (err) {
      console.error("Error fetching room services:", err);
    }
  };

  useEffect(() => {
    if (roomId) {
      fetchRoomServices();
    }
  }, [roomId]);

  // Fetch danh sách hợp đồng của phòng để lấy thông tin sinh viên có hợp đồng còn hiệu lực
  useEffect(() => {
    if (!room) return; // Chờ cho đến khi thông tin phòng được tải
    const token = localStorage.getItem("token");
    const fetchActiveContracts = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/contracts/list",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const contracts = await response.json();
        // Lọc ra những hợp đồng của phòng hiện tại có trạng thái "Active"
        const activeContracts = contracts.filter((contract: any) => {
          return (
            contract.roomId === room.roomId &&
            contract.contractStatus === "Active"
          );
        });
        // Lấy thông tin sinh viên từ mỗi hợp đồng (giả sử API user trả về fullName và studentCode)
        const students: Student[] = await Promise.all(
          activeContracts.map(async (contract: any) => {
            const userResponse = await fetch(
              `http://localhost:8080/api/v1/user/manager/${contract.userId}`,
              {
                headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${token}`,
                },
              },
            );
            if (userResponse.ok) {
              const userData = await userResponse.json();
              return {
                userId: contract.userId,
                fullName:
                  userData.result?.fullName || userData.fullName || "N/A",
                studentCode:
                  userData.result?.studentCode || userData.studentCode || "N/A",
              };
            } else {
              return {
                userId: contract.userId,
                fullName: "N/A",
                studentCode: "N/A",
              };
            }
          }),
        );
        setActiveStudents(students);
      } catch (err) {
        console.error("Error fetching active contracts:", err);
      }
    };

    fetchActiveContracts();
  }, [room]);

  // Khi chọn dịch vụ từ dropdown, lấy chi tiết dịch vụ đó và tự động cập nhật giá
  useEffect(() => {
    if (!selectedUtilityServiceId) {
      setSelectedServiceDetail(null);
      setPrice(0);
      return;
    }
    const token = localStorage.getItem("token");
    const fetchServiceDetail = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/api/v1/utility-services/${selectedUtilityServiceId}`,
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setSelectedServiceDetail(data);
        // Tự động cập nhật giá từ pricePerUnit của dịch vụ
        setPrice(data.pricePerUnit);
      } catch (err) {
        console.error("Error fetching service detail:", err);
      }
    };

    fetchServiceDetail();
  }, [selectedUtilityServiceId]);

  // Hàm xóa dịch vụ khỏi phòng
  const handleDeleteService = async (roomServiceId: string) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/room-services/delete/${roomServiceId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      if (!response.ok) {
        throw new Error(`Lỗi khi xóa dịch vụ: ${response.status}`);
      }
      fetchRoomServices();
    } catch (err) {
      console.error("Error deleting service:", err);
    }
  };

  const handleBack = () => {
    navigate(-1);
  };

  // Xử lý thêm dịch vụ vào phòng
  const handleAddService = async () => {
    if (!selectedUtilityServiceId) {
      setMessageText("Vui lòng chọn dịch vụ.");
      return;
    }
    if (price <= 0) {
      setMessageText("Giá dịch vụ không hợp lệ.");
      return;
    }
    if (selectedServiceDetail && selectedServiceDetail.status !== "Active") {
      setMessageText("Dịch vụ này không khả dụng.");
      return;
    }

    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/room-services/add",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            roomId,
            utilityServiceId: selectedUtilityServiceId,
            price,
          }),
        },
      );

      if (!response.ok) {
        throw new Error(`Lỗi khi thêm dịch vụ: ${response.status}`);
      }

      setMessageText("Dịch vụ đã được thêm thành công!");
      setSelectedUtilityServiceId("");
      setPrice(0);
      setSelectedServiceDetail(null);
      setShowModal(false);
      fetchRoomServices();
    } catch (err) {
      console.error("Error adding service:", err);
      setMessageText("Không thể thêm dịch vụ. Vui lòng thử lại.");
    }
  };

  if (loading) {
    return <p className="loading">Đang tải thông tin phòng...</p>;
  }

  if (error) {
    return <p className="error">{error}</p>;
  }

  if (!room) {
    return <p>Không có dữ liệu phòng.</p>;
  }

  return (
    <div className="room-detail-container">
      <h1 className="title">Chi tiết phòng: {room.roomName}</h1>
      <div className="room-detail-card">
        <p>
          <strong>Mã phòng:</strong> {room.roomId}
        </p>
        <p>
          <strong>Loại phòng:</strong> {room.roomType}
        </p>
        <p>
          <strong>Giá phòng:</strong> {Number(room.roomPrice).toLocaleString()}{" "}
          VNĐ
        </p>
        <p>
          <strong>Số người:</strong> {room.currentOccupancy} /{" "}
          {room.maximumOccupancy}
        </p>
        <p>
          <strong>Khoa/Bộ phận:</strong> {room.department}
        </p>
        <p>
          <strong>Trạng thái:</strong>{" "}
          {room.roomStatus === "empty_room" ? "Còn trống" : "Đã đầy"}
        </p>
        <p>
          <strong>Ghi chú:</strong> {room.note}
        </p>
        {room.createdAt && (
          <p>
            <strong>Ngày tạo:</strong>{" "}
            {new Date(room.createdAt).toLocaleString()}
          </p>
        )}
        {room.updatedAt && (
          <p>
            <strong>Ngày cập nhật:</strong>{" "}
            {new Date(room.updatedAt).toLocaleString()}
          </p>
        )}
      </div>

      {/* Hiển thị danh sách sinh viên có hợp đồng còn hiệu lực */}
      <div className="active-students-container">
        <h2>Sinh viên có hợp đồng còn hiệu lực</h2>
        {activeStudents.length > 0 ? (
          <ul>
            {activeStudents.map((student) => (
              <li key={student.userId}>
                <span className="student-code">
                  Mã SV: {student.studentCode}
                </span>{" "}
                - <span className="student-name">{student.fullName}</span>
              </li>
            ))}
          </ul>
        ) : (
          <p>Chưa có sinh viên nào có hợp đồng còn hiệu lực cho phòng này.</p>
        )}
      </div>

      {/* Hiển thị danh sách dịch vụ có trong phòng */}
      <div className="room-services-container">
        <h2>Dịch vụ có trong phòng</h2>
        {roomServices.length > 0 ? (
          <ul>
            {roomServices.map((service) => {
              const serviceInfo = utilityServices.find(
                (s) => s.utilityServiceId === service.utilityServiceId,
              );
              return (
                <li key={service.roomServiceId}>
                  <strong>
                    {serviceInfo
                      ? serviceInfo.serviceName
                      : "Tên dịch vụ không xác định"}
                  </strong>{" "}
                  - {Number(service.price).toLocaleString()} VNĐ
                  <button
                    onClick={() => handleDeleteService(service.roomServiceId)}
                    className="delete-btn"
                  >
                    Xóa
                  </button>
                </li>
              );
            })}
          </ul>
        ) : (
          <p>Chưa có dịch vụ nào được thêm vào phòng.</p>
        )}
      </div>

      {/* Nút mở modal thêm dịch vụ */}
      <button
        onClick={() => {
          setShowModal(true);
          setMessageText(null);
        }}
        className="open-modal-button"
      >
        Thêm dịch vụ vào phòng
      </button>

      {/* Modal thêm dịch vụ */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>Thêm dịch vụ vào phòng</h2>
            <label>
              Chọn dịch vụ:
              <select
                value={selectedUtilityServiceId}
                onChange={(e) => {
                  const value = e.target.value;
                  const selected = utilityServices.find(
                    (s) => s.utilityServiceId === value,
                  );
                  if (selected && selected.status !== "Active") {
                    setMessageText("Dịch vụ này không khả dụng.");
                    return;
                  }
                  // Kiểm tra xem dịch vụ đã được thêm chưa
                  const exists = roomServices.some(
                    (rs) => rs.utilityServiceId === value,
                  );
                  if (exists) {
                    setMessageText("Dịch vụ này đã được thêm vào phòng.");
                    return;
                  }
                  setSelectedUtilityServiceId(value);
                  setMessageText(null);
                }}
                className="select-service"
              >
                <option value="">Chọn dịch vụ</option>
                {utilityServices.map((service) => (
                  <option
                    key={service.utilityServiceId}
                    value={service.utilityServiceId}
                    disabled={
                      service.status !== "Active" ||
                      roomServices.some(
                        (rs) =>
                          rs.utilityServiceId === service.utilityServiceId,
                      )
                    }
                  >
                    {service.serviceName} -{" "}
                    {Number(service.pricePerUnit).toLocaleString()} VNĐ{" "}
                    {service.status !== "Active"
                      ? "(Inactive)"
                      : roomServices.some(
                            (rs) =>
                              rs.utilityServiceId === service.utilityServiceId,
                          )
                        ? "(Đã thêm)"
                        : ""}
                  </option>
                ))}
              </select>
            </label>
            {selectedServiceDetail && (
              <div className="service-detail">
                <p>
                  <strong>Mô tả:</strong> {selectedServiceDetail.description}
                </p>
                <p>
                  <strong>Đơn giá:</strong>{" "}
                  {Number(selectedServiceDetail.pricePerUnit).toLocaleString()}{" "}
                  VNĐ/ {selectedServiceDetail.calculationUnit}
                </p>
              </div>
            )}
            <p>
              <strong>Giá dịch vụ:</strong>{" "}
              {price > 0
                ? Number(price).toLocaleString() + " VNĐ"
                : "Chưa xác định"}
            </p>
            <div className="modal-buttons">
              <button onClick={handleAddService} className="add-service-button">
                Thêm dịch vụ
              </button>
              <button
                onClick={() => setShowModal(false)}
                className="close-modal-button"
              >
                Đóng
              </button>
            </div>
            {messageText && <p className="message">{messageText}</p>}
          </div>
        </div>
      )}

      <button onClick={handleBack} className="back-button">
        Quay lại
      </button>
    </div>
  );
};

export default RoomDetail;
