// src/RoomManagement.tsx
import React, { useState } from "react";
import "./RoomManagement.css";

interface Room {
  room_id: string;
  room_name: string;
  room_type: "Single" | "Double" | "VIP";
  room_price: number;
  maximum_occupancy: number;
  current_occupancy: number;
  room_status: "Available" | "Occupied";
  department: string;
  note?: string;
  assigned_student_ids: string[];
  assigned_staff_id?: string;
  utility_service_ids: string[];
}

interface Student {
  student_id: string;
  full_name: string;
}

interface Staff {
  user_id: string;
  full_name: string;
}

interface UtilityService {
  utility_service_id: string;
  service_name: string;
}

const RoomManagement: React.FC = () => {
  const [rooms, setRooms] = useState<Room[]>([
    {
      room_id: "1",
      room_name: "P101",
      room_type: "Single",
      room_price: 1000000,
      maximum_occupancy: 1,
      current_occupancy: 0,
      room_status: "Available",
      department: "CNTT",
      assigned_student_ids: [],
      assigned_staff_id: "",
      utility_service_ids: ["1"],
    },
  ]);

  const students: Student[] = [
    { student_id: "SV001", full_name: "Nguyễn Văn A" },
    { student_id: "SV002", full_name: "Trần Thị B" },
  ];

  const staff: Staff[] = [
    { user_id: "NV001", full_name: "Lê Văn C" },
    { user_id: "NV002", full_name: "Phạm Thị D" },
  ];

  const services: UtilityService[] = [
    { utility_service_id: "1", service_name: "Điện" },
    { utility_service_id: "2", service_name: "Nước" },
    { utility_service_id: "3", service_name: "Wifi" },
  ];

  const [newRoom, setNewRoom] = useState<Omit<Room, "room_id">>({
    room_name: "",
    room_type: "Single",
    room_price: 0,
    maximum_occupancy: 1,
    current_occupancy: 0,
    room_status: "Available",
    department: "",
    note: "",
    assigned_student_ids: [],
    utility_service_ids: [],
  });

  const [editingRoom, setEditingRoom] = useState<Room | null>(null);
  const [selectedStudents, setSelectedStudents] = useState<{ [key: string]: string }>({});
  const [selectedStaff, setSelectedStaff] = useState<{ [key: string]: string }>({});
  const [selectedStatuses, setSelectedStatuses] = useState<{ [key: string]: "Available" | "Occupied" }>({}); // Trạng thái phòng
  const [selectedServiceIds, setSelectedServiceIds] = useState<string[]>([]);

  const handleAddRoom = () => {
    if (
      !newRoom.room_name ||
      newRoom.room_price <= 0 ||
      newRoom.maximum_occupancy <= 0 ||
      !newRoom.department
    ) {
      alert("Vui lòng điền đầy đủ thông tin bắt buộc: Tên phòng, Giá phòng, Số lượng tối đa, Khoa/Bộ phận.");
      return;
    }

    const newRoomWithId: Room = {
      ...newRoom,
      room_id: Date.now().toString(),
      assigned_student_ids: [...newRoom.assigned_student_ids],
      utility_service_ids: [...newRoom.utility_service_ids],
    };
    setRooms((prevRooms) => [...prevRooms, newRoomWithId]);
    setNewRoom({
      room_name: "",
      room_type: "Single",
      room_price: 0,
      maximum_occupancy: 1,
      current_occupancy: 0,
      room_status: "Available",
      department: "",
      note: "",
      assigned_student_ids: [],
      utility_service_ids: [],
    });
  };

  const handleUpdateRoom = () => {
    if (!editingRoom) return;
    setRooms((prevRooms) =>
      prevRooms.map((room) =>
        room.room_id === editingRoom.room_id
          ? {
              ...editingRoom,
              assigned_student_ids: [...editingRoom.assigned_student_ids],
              utility_service_ids: [...editingRoom.utility_service_ids],
            }
          : { ...room }
      )
    );
    setEditingRoom(null);
  };

  const handleDeleteRoom = (room_id: string) => {
    setRooms((prevRooms) => prevRooms.filter((room) => room.room_id !== room_id));
    setSelectedStudents((prev) => {
      const newSelected = { ...prev };
      delete newSelected[room_id];
      return newSelected;
    });
    setSelectedStaff((prev) => {
      const newSelected = { ...prev };
      delete newSelected[room_id];
      return newSelected;
    });
    setSelectedStatuses((prev) => {
      const newSelected = { ...prev };
      delete newSelected[room_id];
      return newSelected;
    });
  };

  const handleAssignStudent = (room_id: string) => {
    const studentId = selectedStudents[room_id];
    if (!studentId) return;
    setRooms((prevRooms) =>
      prevRooms.map((room) =>
        room.room_id === room_id
          ? {
              ...room,
              assigned_student_ids: [...room.assigned_student_ids, studentId],
              current_occupancy: room.current_occupancy + 1,
            }
          : { ...room }
      )
    );
    setSelectedStudents((prev) => {
      const newSelected = { ...prev };
      delete newSelected[room_id];
      return newSelected;
    });
  };

  const handleAssignStaff = (room_id: string) => {
    const staffId = selectedStaff[room_id];
    if (!staffId) return;
    setRooms((prevRooms) =>
      prevRooms.map((room) =>
        room.room_id === room_id ? { ...room, assigned_staff_id: staffId } : { ...room }
      )
    );
    setSelectedStaff((prev) => {
      const newSelected = { ...prev };
      delete newSelected[room_id];
      return newSelected;
    });
  };

  const handleUpdateStatus = (room_id: string) => {
    const status = selectedStatuses[room_id];
    if (!status) return;
    setRooms((prevRooms) =>
      prevRooms.map((room) =>
        room.room_id === room_id ? { ...room, room_status: status } : { ...room }
      )
    );
    setSelectedStatuses((prev) => {
      const newSelected = { ...prev };
      delete newSelected[room_id];
      return newSelected;
    });
  };

  const handleServiceChange = (serviceId: string) => {
    setSelectedServiceIds((prev) =>
      prev.includes(serviceId) ? prev.filter((id) => id !== serviceId) : [...prev, serviceId]
    );
  };

  const handleManageServices = (room_id: string) => {
    setRooms((prevRooms) =>
      prevRooms.map((room) =>
        room.room_id === room_id
          ? { ...room, utility_service_ids: [...selectedServiceIds] }
          : { ...room }
      )
    );
    setSelectedServiceIds([]);
  };

  const handleEditRoom = (room: Room) => {
    setEditingRoom({
      ...room,
      assigned_student_ids: [...room.assigned_student_ids],
      utility_service_ids: [...room.utility_service_ids],
    });
  };

  return (
    <div className="room-management-container">
      <h1 className="room-management-title">Quản lý phòng</h1>

      {/* Form thêm phòng */}
      <div className="form-card">
        <h2>Thêm phòng mới</h2>
        <div className="form-group">
          <input
            type="text"
            placeholder="Tên phòng *"
            value={newRoom.room_name}
            onChange={(e) => setNewRoom({ ...newRoom, room_name: e.target.value })}
          />
          <select
            value={newRoom.room_type}
            onChange={(e) => setNewRoom({ ...newRoom, room_type: e.target.value as "Single" | "Double" | "VIP" })}
          >
            <option value="Single">Single</option>
            <option value="Double">Double</option>
            <option value="VIP">VIP</option>
          </select>
          <input
            type="number"
            placeholder="Giá phòng (VNĐ) *"
            value={newRoom.room_price || ""}
            onChange={(e) => setNewRoom({ ...newRoom, room_price: Number(e.target.value) })}
          />
          <input
            type="number"
            placeholder="Số lượng tối đa *"
            value={newRoom.maximum_occupancy || ""}
            onChange={(e) => setNewRoom({ ...newRoom, maximum_occupancy: Number(e.target.value) })}
          />
          <input
            type="text"
            placeholder="Khoa/Bộ phận *"
            value={newRoom.department}
            onChange={(e) => setNewRoom({ ...newRoom, department: e.target.value })}
          />
          <button className="add-button" onClick={handleAddRoom}>
            Thêm phòng
          </button>
        </div>
      </div>

      {/* Danh sách phòng */}
      <div className="room-list-card">
        <h2>Danh sách phòng</h2>
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
              <tr key={room.room_id}>
                <td>{room.room_name}</td>
                <td>{room.room_type}</td>
                <td>{room.room_price.toLocaleString()}</td>
                <td>{`${room.current_occupancy}/${room.maximum_occupancy}`}</td>
                <td>
                  <span className={`status-badge ${room.room_status.toLowerCase()}`}>
                    {room.room_status === "Available" ? "Còn trống" : "Đã đầy"}
                  </span>
                </td>
                <td className="action-column">
                  <button className="edit-button" onClick={() => handleEditRoom(room)}>
                    Sửa
                  </button>
                  <button className="delete-button" onClick={() => handleDeleteRoom(room.room_id)}>
                    Xóa
                  </button>
                  <select
                    value={selectedStudents[room.room_id] || ""}
                    onChange={(e) =>
                      setSelectedStudents((prev) => ({
                        ...prev,
                        [room.room_id]: e.target.value,
                      }))
                    }
                  >
                    <option value="">Chọn SV</option>
                    {students.map((student) => (
                      <option key={student.student_id} value={student.student_id}>
                        {student.full_name}
                      </option>
                    ))}
                  </select>
                  <button className="assign-button" onClick={() => handleAssignStudent(room.room_id)}>
                    Xếp SV
                  </button>
                  <select
                    value={selectedStaff[room.room_id] || ""}
                    onChange={(e) =>
                      setSelectedStaff((prev) => ({
                        ...prev,
                        [room.room_id]: e.target.value,
                      }))
                    }
                  >
                    <option value="">Chọn NV</option>
                    {staff.map((s) => (
                      <option key={s.user_id} value={s.user_id}>
                        {s.full_name}
                      </option>
                    ))}
                  </select>
                  <button className="assign-button" onClick={() => handleAssignStaff(room.room_id)}>
                    Xếp NV
                  </button>
                  <select
                    value={selectedStatuses[room.room_id] || ""}
                    onChange={(e) =>
                      setSelectedStatuses((prev) => ({
                        ...prev,
                        [room.room_id]: e.target.value as "Available" | "Occupied",
                      }))
                    }
                  >
                    <option value="">Chọn trạng thái</option>
                    <option value="Available">Còn trống</option>
                    <option value="Occupied">Đã đầy</option>
                  </select>
                  <button className="assign-button" onClick={() => handleUpdateStatus(room.room_id)}>
                    Cập nhật trạng thái
                  </button>
                  <div className="service-checkboxes">
                    {services.map((service) => (
                      <label key={service.utility_service_id} className="checkbox-label">
                        <input
                          type="checkbox"
                          checked={selectedServiceIds.includes(service.utility_service_id)}
                          onChange={() => handleServiceChange(service.utility_service_id)}
                        />
                        {service.service_name}
                      </label>
                    ))}
                  </div>
                  <button className="service-button" onClick={() => handleManageServices(room.room_id)}>
                    Cập nhật DV
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Form sửa phòng */}
      {editingRoom && (
        <div className="form-card edit-form">
          <h2>Sửa thông tin phòng</h2>
          <div className="form-group">
            <input
              type="text"
              value={editingRoom.room_name}
              onChange={(e) => setEditingRoom({ ...editingRoom, room_name: e.target.value })}
            />
            <select
              value={editingRoom.room_type}
              onChange={(e) =>
                setEditingRoom({ ...editingRoom, room_type: e.target.value as "Single" | "Double" | "VIP" })
              }
            >
              <option value="Single">Single</option>
              <option value="Double">Double</option>
              <option value="VIP">VIP</option>
            </select>
            <input
              type="number"
              value={editingRoom.room_price || ""}
              onChange={(e) => setEditingRoom({ ...editingRoom, room_price: Number(e.target.value) })}
            />
            <input
              type="number"
              value={editingRoom.maximum_occupancy || ""}
              onChange={(e) => setEditingRoom({ ...editingRoom, maximum_occupancy: Number(e.target.value) })}
            />
            <input
              type="text"
              value={editingRoom.department}
              onChange={(e) => setEditingRoom({ ...editingRoom, department: e.target.value })}
            />
            <button className="update-button" onClick={handleUpdateRoom}>
              Cập nhật
            </button>
            <button className="cancel-button" onClick={() => setEditingRoom(null)}>
              Hủy
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default RoomManagement;