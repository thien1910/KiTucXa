"use client";

import React, { useState, useEffect } from "react";
import { Modal } from "antd";
// import { useRouter } from "next/navigation"
// import "../styles/Management.css";
import { useNavigate } from "react-router-dom";

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

const RoomManagement: React.FC = () => {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // State cho Modal thêm phòng mới
  const [addModalVisible, setAddModalVisible] = useState<boolean>(false);

  // State form thêm phòng mới
  const [newRoom, setNewRoom] = useState<
    Omit<Room, "roomId" | "createdAt" | "updatedAt">
  >({
    userId: "2b68ba14-fef7-4dd8-8147-088f27e68be3", // Default userId
    roomName: "",
    roomType: "Single",
    roomPrice: 0,
    maximumOccupancy: 1,
    currentOccupancy: 0,
    roomStatus: "empty_room",
    department: "",
    note: "",
  });

  const router = useNavigate(); // Khai báo router từ useNavigate

  // State cho form sửa phòng
  const [editingRoom, setEditingRoom] = useState<Room | null>(null);
  const token = localStorage.getItem("token");
  // const router = useRouter()

  // Hàm lấy danh sách phòng từ API
  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await fetch("http://localhost:8080/api/v1/rooms/list", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      setRooms(data);
      setError(null);
    } catch (err) {
      console.error("Error fetching rooms:", err);
      setError("Không thể tải danh sách phòng. Vui lòng thử lại sau.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRooms();
  }, []);

  // Hàm thêm phòng qua API
  const addRoom = async (
    roomData: Omit<Room, "roomId" | "createdAt" | "updatedAt">,
  ) => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/rooms/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(roomData),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      return data;
    } catch (err) {
      console.error("Error adding room:", err);
      throw err;
    }
  };

  // Xử lý thêm phòng
  const handleAddRoom = async () => {
    if (
      !newRoom.roomName ||
      newRoom.roomPrice <= 0 ||
      newRoom.maximumOccupancy <= 0 ||
      !newRoom.department
    ) {
      alert(
        "Vui lòng điền đầy đủ thông tin bắt buộc: Tên phòng, Giá phòng, Số lượng tối đa, Khoa/Bộ phận.",
      );
      return;
    }

    try {
      await addRoom(newRoom);
      fetchRooms();
      // Reset form thêm phòng
      setNewRoom({
        userId: "2b68ba14-fef7-4dd8-8147-088f27e68be3",
        roomName: "",
        roomType: "Single",
        roomPrice: 0,
        maximumOccupancy: 1,
        currentOccupancy: 0,
        roomStatus: "empty_room",
        department: "",
        note: "",
      });
      alert("Phòng đã được thêm thành công!");
      setAddModalVisible(false);
    } catch (err) {
      alert("Không thể thêm phòng. Vui lòng thử lại sau.");
    }
  };

  // Hàm cập nhật phòng
  const handleUpdateRoom = async () => {
    if (!editingRoom) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/rooms/update/${editingRoom.roomId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(editingRoom),
        },
      );

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      fetchRooms();
      setEditingRoom(null);
      alert("Phòng đã được cập nhật thành công!");
    } catch (err) {
      console.error("Error updating room:", err);
      alert("Không thể cập nhật phòng. Vui lòng thử lại sau.");
    }
  };

  // Hàm xóa phòng
  const handleDeleteRoom = async (roomId: string) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa phòng này không?")) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/rooms/delete/${roomId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      fetchRooms();
      alert("Phòng đã được xóa thành công!");
    } catch (err) {
      console.error("Error deleting room:", err);
      alert("Không thể xóa phòng. Vui lòng thử lại sau.");
    }
  };

  // Hàm lấy thông tin phòng theo ID
  const fetchRoomById = async (roomId: string) => {
    try {
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
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      return data;
    } catch (err) {
      console.error("Error fetching room:", err);
      throw err;
    }
  };

  // Xử lý chuyển sang form sửa phòng
  const handleEditRoom = async (room: Room) => {
    try {
      const latestRoomData = await fetchRoomById(room.roomId);
      setEditingRoom(latestRoomData);
    } catch (err) {
      console.error("Error fetching room details:", err);
      alert("Không thể tải thông tin phòng. Vui lòng thử lại sau.");
      setEditingRoom({ ...room });
    }
  };

  // Các hàm trợ giúp hiển thị trạng thái phòng
  const getRoomStatusDisplay = (status: string) => {
    return status === "empty_room" ? "Còn trống" : "Đã đầy";
  };

  const getStatusBadgeClass = (status: string) => {
    return status === "empty_room" ? "available" : "occupied";
  };

  return (
    <div className="room-management-container">
      <div className="title-wrapper">
        <h1 className="room-management-title">Quản lý phòng</h1>
      </div>

      {/* Nút mở Modal thêm phòng mới */}
      <button className="add-button" onClick={() => setAddModalVisible(true)}>
        Thêm phòng mới
      </button>

      {/* Modal thêm phòng mới */}
      <Modal
        visible={addModalVisible}
        title="Thêm phòng mới"
        onCancel={() => setAddModalVisible(false)}
        footer={null}
      >
        <div className="form-card">
          <div className="form-group">
            <input
              type="text"
              placeholder="Tên phòng *"
              value={newRoom.roomName}
              onChange={(e) =>
                setNewRoom({ ...newRoom, roomName: e.target.value })
              }
            />
            <select
              value={newRoom.roomType}
              onChange={(e) => {
                const type = e.target.value;
                // Cập nhật occupancy dựa trên loại phòng
                const occupancy =
                  type === "Single"
                    ? 1
                    : type === "Double"
                      ? 2
                      : type === "Quad"
                        ? 4
                        : 1;
                setNewRoom({
                  ...newRoom,
                  roomType: type,
                  maximumOccupancy: occupancy,
                });
              }}
            >
              <option value="Single">Single</option>
              <option value="Double">Double</option>
              <option value="Quad">Quad</option>
            </select>
            <input
              type="number"
              placeholder="Giá phòng (VNĐ) *"
              value={newRoom.roomPrice || ""}
              onChange={(e) =>
                setNewRoom({ ...newRoom, roomPrice: Number(e.target.value) })
              }
            />
            {/* Ẩn ô nhập số lượng tối đa vì đã tự động cập nhật */}
            {/* <input
        type="number"
        placeholder="Số lượng tối đa *"
        value={newRoom.maximumOccupancy || ""}
        onChange={(e) => setNewRoom({ ...newRoom, maximumOccupancy: Number(e.target.value) })}
      /> */}
            <input
              type="text"
              placeholder="Khoa/Bộ phận *"
              value={newRoom.department}
              onChange={(e) =>
                setNewRoom({ ...newRoom, department: e.target.value })
              }
            />
            <input
              type="text"
              placeholder="Ghi chú"
              value={newRoom.note}
              onChange={(e) => setNewRoom({ ...newRoom, note: e.target.value })}
            />
            <button className="add-button" onClick={handleAddRoom}>
              Thêm phòng
            </button>
          </div>
        </div>
      </Modal>

      {/* Danh sách phòng */}
      <div className="room-list-card">
        <h2>Danh sách phòng</h2>
        {loading ? (
          <p>Đang tải dữ liệu...</p>
        ) : error ? (
          <p className="error-message">{error}</p>
        ) : (
          <table className="room-table">
            <thead>
              <tr>
                <th>Tên phòng</th>
                <th>Loại</th>
                <th>Giá (VNĐ)</th>
                <th>Số người</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              {rooms.map((room) => (
                <tr key={room.roomId}>
                  <td>{room.roomName}</td>
                  <td>{room.roomType}</td>
                  <td>{Number(room.roomPrice).toLocaleString()}</td>
                  <td>{`${room.currentOccupancy}/${room.maximumOccupancy}`}</td>
                  <td>
                    <span
                      className={`status-badge ${getStatusBadgeClass(
                        room.roomStatus,
                      )}`}
                    >
                      {getRoomStatusDisplay(room.roomStatus)}
                    </span>
                  </td>
                  <td className="action-column">
                    <button
                      className="edit-button"
                      onClick={() => handleEditRoom(room)}
                    >
                      Sửa
                    </button>
                    <button
                      className="delete-button"
                      onClick={() => handleDeleteRoom(room.roomId)}
                    >
                      Xóa
                    </button>
                    <button
                      className="detail-button"
                      onClick={() =>
                        router(`/room-details?roomId=${room.roomId}`)
                      }
                    >
                      Chi tiết phòng
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Form sửa phòng */}
      {editingRoom && (
        <div className="form-card edit-form bg-white p-6 rounded-lg shadow-md max-w-md mx-auto">
          <h2 className="text-2xl font-bold mb-4 text-center">
            Sửa thông tin phòng
          </h2>
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Tên phòng:
              </label>
              <input
                type="text"
                value={editingRoom.roomName}
                onChange={(e) =>
                  setEditingRoom({ ...editingRoom, roomName: e.target.value })
                }
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="mb-3 block text-sm font-medium text-gray-700">
                Loại phòng:
              </label>
              <select
                value={editingRoom.roomType}
                onChange={(e) => {
                  const newType = e.target.value;
                  const occupancy =
                    newType === "Single"
                      ? 1
                      : newType === "Double"
                        ? 2
                        : newType === "Quad"
                          ? 4
                          : editingRoom.maximumOccupancy;
                  setEditingRoom({
                    ...editingRoom,
                    roomType: newType,
                    maximumOccupancy: occupancy,
                  });
                }}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="Single">Single</option>
                <option value="Double">Double</option>
                <option value="Quad">Quad</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Giá phòng (VNĐ):
              </label>
              <input
                type="number"
                value={editingRoom.roomPrice || ""}
                onChange={(e) =>
                  setEditingRoom({
                    ...editingRoom,
                    roomPrice: Number(e.target.value),
                  })
                }
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Số lượng tối đa:
              </label>
              <input
                type="number"
                value={editingRoom.maximumOccupancy || ""}
                readOnly
                className="mt-1 block w-full px-3 py-2 border border-gray-300 bg-gray-100 rounded-md shadow-sm focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Khoa/Bộ phận:
              </label>
              <input
                type="text"
                value={editingRoom.department}
                onChange={(e) =>
                  setEditingRoom({ ...editingRoom, department: e.target.value })
                }
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Ghi chú:
              </label>
              <input
                type="text"
                value={editingRoom.note}
                onChange={(e) =>
                  setEditingRoom({ ...editingRoom, note: e.target.value })
                }
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div className="flex justify-end space-x-4">
              <button
                className="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                onClick={handleUpdateRoom}
              >
                Cập nhật
              </button>
              <button
                className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-500"
                onClick={() => setEditingRoom(null)}
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default RoomManagement;
