import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./RoomDetail.css"; // Tạo file CSS nếu cần tùy chỉnh giao diện

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

const RoomDetail: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [room, setRoom] = useState<Room | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Lấy roomId từ query string
  const queryParams = new URLSearchParams(location.search);
  const roomId = queryParams.get("roomId");

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
        const response = await fetch(`http://localhost:8080/api/v1/rooms/${roomId}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
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

  const handleBack = () => {
    navigate(-1);
  };

  if (loading) {
    return <p>Đang tải thông tin phòng...</p>;
  }

  if (error) {
    return <p className="error">{error}</p>;
  }

  if (!room) {
    return <p>Không có dữ liệu phòng.</p>;
  }

  return (
    <div className="room-detail-container">
      <h1>Chi tiết phòng: {room.roomName}</h1>
      <div className="room-detail-card">
        <p>
          <strong>Mã phòng:</strong> {room.roomId}
        </p>
        <p>
          <strong>Loại phòng:</strong> {room.roomType}
        </p>
        <p>
          <strong>Giá phòng:</strong> {Number(room.roomPrice).toLocaleString()} VNĐ
        </p>
        <p>
          <strong>Số người:</strong> {room.currentOccupancy} / {room.maximumOccupancy}
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
            <strong>Ngày tạo:</strong> {new Date(room.createdAt).toLocaleString()}
          </p>
        )}
        {room.updatedAt && (
          <p>
            <strong>Ngày cập nhật:</strong> {new Date(room.updatedAt).toLocaleString()}
          </p>
        )}
      </div>
      <button onClick={handleBack} className="back-button">
        Quay lại
      </button>
    </div>
  );
};

export default RoomDetail;
