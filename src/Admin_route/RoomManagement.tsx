"use client"

import type React from "react"
import { useState, useEffect } from "react"
import "./RoomManagement.css"

interface Room {
  roomId: string
  userId: string
  roomName: string
  department: string
  maximumOccupancy: number
  currentOccupancy: number
  roomType: string
  roomPrice: number
  roomStatus: string
  note: string
  createdAt?: string
  updatedAt?: string
}

interface Student {
  student_id: string
  full_name: string
}

interface Staff {
  user_id: string
  full_name: string
}

interface UtilityService {
  utility_service_id: string
  service_name: string
}

const RoomManagement: React.FC = () => {
  const [rooms, setRooms] = useState<Room[]>([])
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string | null>(null)

  const [newRoom, setNewRoom] = useState<Omit<Room, "roomId" | "createdAt" | "updatedAt">>({
    userId: "0bfdfa55-534f-4b26-9b7f-d05489fdfba3", // Default userId
    roomName: "",
    roomType: "Single",
    roomPrice: 0,
    maximumOccupancy: 1,
    currentOccupancy: 0,
    roomStatus: "empty_room",
    department: "",
    note: "",
  })

  const students: Student[] = [
    { student_id: "SV001", full_name: "Nguyễn Văn A" },
    { student_id: "SV002", full_name: "Trần Thị B" },
  ]

  const staff: Staff[] = [
    { user_id: "NV001", full_name: "Lê Văn C" },
    { user_id: "NV002", full_name: "Phạm Thị D" },
  ]

  const services: UtilityService[] = [
    { utility_service_id: "1", service_name: "Điện" },
    { utility_service_id: "2", service_name: "Nước" },
    { utility_service_id: "3", service_name: "Wifi" },
  ]

  const [editingRoom, setEditingRoom] = useState<Room | null>(null)
  const [selectedStudents, setSelectedStudents] = useState<{ [key: string]: string }>({})
  const [selectedStaff, setSelectedStaff] = useState<{ [key: string]: string }>({})
  const [selectedStatuses, setSelectedStatuses] = useState<{ [key: string]: string }>({})
  const [selectedServiceIds, setSelectedServiceIds] = useState<string[]>([])
  const token =localStorage.getItem("token")
  // Fetch rooms from API
  const fetchRooms = async () => {
    try {
      setLoading(true)
      const response = await fetch("http://localhost:8080/api/v1/rooms/list", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        }
      })

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`)
      }

      const data = await response.json()
      setRooms(data)
      setError(null)
    } catch (err) {
      console.error("Error fetching rooms:", err)
      setError("Không thể tải danh sách phòng. Vui lòng thử lại sau.")
    } finally {
      setLoading(false)
    }
  }

  // Add a new room via API
  const addRoom = async (roomData: Omit<Room, "roomId" | "createdAt" | "updatedAt">) => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/rooms/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(roomData),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`)
      }

      const data = await response.json()
      return data
    } catch (err) {
      console.error("Error adding room:", err)
      throw err
    }
  }

  // Load rooms when component mounts
  useEffect(() => {
    fetchRooms()
  }, [])

  const handleAddRoom = async () => {
    if (!newRoom.roomName || newRoom.roomPrice <= 0 || newRoom.maximumOccupancy <= 0 || !newRoom.department) {
      alert("Vui lòng điền đầy đủ thông tin bắt buộc: Tên phòng, Giá phòng, Số lượng tối đa, Khoa/Bộ phận.")
      return
    }

    try {
      // Convert number values to strings for API
      const roomToAdd = {
        ...newRoom,
      }

      await addRoom(roomToAdd)

      // Refresh the room list
      fetchRooms()

      // Reset the form
      setNewRoom({
        userId: "0bfdfa55-534f-4b26-9b7f-d05489fdfba3",
        roomName: "",
        roomType: "Single",
        roomPrice: 0,
        maximumOccupancy: 1,
        currentOccupancy: 0,
        roomStatus: "empty_room",
        department: "",
        note: "",
      })

      alert("Phòng đã được thêm thành công!")
    } catch (err) {
      alert("Không thể thêm phòng. Vui lòng thử lại sau.")
    }
  }

  const handleUpdateRoom = async () => {
    if (!editingRoom) return

    try {
      const response = await fetch(`http://localhost:8080/api/v1/rooms/update/${editingRoom.roomId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(editingRoom),
      })

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`)
      }

      // Refresh the room list
      fetchRooms()
      setEditingRoom(null)
      alert("Phòng đã được cập nhật thành công!")
    } catch (err) {
      console.error("Error updating room:", err)
      alert("Không thể cập nhật phòng. Vui lòng thử lại sau.")
    }
  }

  const handleDeleteRoom = async (roomId: string) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa phòng này không?")) {
      return
    }

    try {
      const response = await fetch(`http://localhost:8080/api/v1/rooms/delete/${roomId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`)
      }

      // Refresh the room list
      fetchRooms()
      alert("Phòng đã được xóa thành công!")
    } catch (err) {
      console.error("Error deleting room:", err)
      alert("Không thể xóa phòng. Vui lòng thử lại sau.")
    }
  }

  // Fetch a single room by ID
  const fetchRoomById = async (roomId: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/v1/rooms/${roomId}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      })

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`)
      }

      const data = await response.json()
      return data
    } catch (err) {
      console.error("Error fetching room:", err)
      throw err
    }
  }

  const handleAssignStudent = (roomId: string) => {
    const studentId = selectedStudents[roomId]
    if (!studentId) return

    // In a real application, you would implement an API call to assign a student
    alert("Chức năng xếp sinh viên qua API chưa được triển khai.")

    setSelectedStudents((prev) => {
      const newSelected = { ...prev }
      delete newSelected[roomId]
      return newSelected
    })
  }

  const handleAssignStaff = (roomId: string) => {
    const staffId = selectedStaff[roomId]
    if (!staffId) return

    // In a real application, you would implement an API call to assign staff
    alert("Chức năng xếp nhân viên qua API chưa được triển khai.")

    setSelectedStaff((prev) => {
      const newSelected = { ...prev }
      delete newSelected[roomId]
      return newSelected
    })
  }

  const handleUpdateStatus = (roomId: string) => {
    const status = selectedStatuses[roomId]
    if (!status) return

    // In a real application, you would implement an API call to update status
    alert("Chức năng cập nhật trạng thái qua API chưa được triển khai.")

    setSelectedStatuses((prev) => {
      const newSelected = { ...prev }
      delete newSelected[roomId]
      return newSelected
    })
  }

  const handleServiceChange = (serviceId: string) => {
    setSelectedServiceIds((prev) =>
      prev.includes(serviceId) ? prev.filter((id) => id !== serviceId) : [...prev, serviceId],
    )
  }

  const handleManageServices = (roomId: string) => {
    // In a real application, you would implement an API call to update services
    alert("Chức năng cập nhật dịch vụ qua API chưa được triển khai.")
    setSelectedServiceIds([])
  }

  const handleEditRoom = async (room: Room) => {
    try {
      // Fetch the latest room data before editing
      const latestRoomData = await fetchRoomById(room.roomId)
      setEditingRoom(latestRoomData)
    } catch (err) {
      console.error("Error fetching room details:", err)
      alert("Không thể tải thông tin phòng. Vui lòng thử lại sau.")
      // Fall back to using the data we already have
      setEditingRoom({ ...room })
    }
  }

  // Helper function to map API room status to display text
  const getRoomStatusDisplay = (status: string) => {
    return status === "empty_room" ? "Còn trống" : "Đã đầy"
  }

  // Helper function to get status badge class
  const getStatusBadgeClass = (status: string) => {
    return status === "empty_room" ? "available" : "occupied"
  }

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
            value={newRoom.roomName}
            onChange={(e) => setNewRoom({ ...newRoom, roomName: e.target.value })}
          />
          <select value={newRoom.roomType} onChange={(e) => setNewRoom({ ...newRoom, roomType: e.target.value })} className="">
            <option value="Single">Single</option>
            <option value="Double">Double</option>
            <option value="VIP">VIP</option>
          </select>
          <input
            type="number"
            placeholder="Giá phòng (VNĐ) *"
            value={newRoom.roomPrice || ""}
            onChange={(e) => setNewRoom({ ...newRoom, roomPrice: Number(e.target.value) })}
          />
          <input
            type="number"
            placeholder="Số lượng tối đa *"
            value={newRoom.maximumOccupancy || ""}
            onChange={(e) => setNewRoom({ ...newRoom, maximumOccupancy: Number(e.target.value) })}
          />
          <input
            type="text"
            placeholder="Khoa/Bộ phận *"
            value={newRoom.department}
            onChange={(e) => setNewRoom({ ...newRoom, department: e.target.value })}
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
                    <span className={`status-badge ${getStatusBadgeClass(room.roomStatus)}`}>
                      {getRoomStatusDisplay(room.roomStatus)}
                    </span>
                  </td>
                  <td className="action-column">
                    <button className="edit-button" onClick={() => handleEditRoom(room)}>
                      Sửa
                    </button>
                    <button className="delete-button" onClick={() => handleDeleteRoom(room.roomId)}>
                      Xóa
                    </button>
                    <select
                      value={selectedStudents[room.roomId] || ""}
                      onChange={(e) =>
                        setSelectedStudents((prev) => ({
                          ...prev,
                          [room.roomId]: e.target.value,
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
                    <button className="assign-button" onClick={() => handleAssignStudent(room.roomId)}>
                      Xếp SV
                    </button>
                    <select
                      value={selectedStaff[room.roomId] || ""}
                      onChange={(e) =>
                        setSelectedStaff((prev) => ({
                          ...prev,
                          [room.roomId]: e.target.value,
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
                    <button className="assign-button" onClick={() => handleAssignStaff(room.roomId)}>
                      Xếp NV
                    </button>
                    <select
                      value={selectedStatuses[room.roomId] || ""}
                      onChange={(e) =>
                        setSelectedStatuses((prev) => ({
                          ...prev,
                          [room.roomId]: e.target.value,
                        }))
                      }
                    >
                      <option value="">Chọn trạng thái</option>
                      <option value="empty_room">Còn trống</option>
                      <option value="occupied_room">Đã đầy</option>
                    </select>
                    <button className="assign-button" onClick={() => handleUpdateStatus(room.roomId)}>
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
                    <button className="service-button" onClick={() => handleManageServices(room.roomId)}>
                      Cập nhật DV
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
        <h2 className="text-2xl font-bold mb-4 text-center">Sửa thông tin phòng</h2>
        <div className="space-y-4">
          {/* Tên phòng */}
          <div>
            <label className="block text-sm font-medium text-gray-700">Tên phòng:</label>
            <input
              type="text"
              value={editingRoom.roomName}
              onChange={(e) => setEditingRoom({ ...editingRoom, roomName: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
      
          {/* Loại phòng */}
          <div>
            <label className="mb-3 block text-sm font-medium text-gray-700">Loại phòng:</label>
            <select
              value={editingRoom.roomType}
              onChange={(e) => setEditingRoom({ ...editingRoom, roomType: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 select-option"
            >
              <option value="Single">Single</option>
              <option value="Double">Double</option>
              <option value="VIP">VIP</option>
            </select>
          </div>
      
          {/* Giá phòng */}
          <div>
            <label className="block text-sm font-medium text-gray-700">Giá phòng (VNĐ):</label>
            <input
              type="number"
              value={editingRoom.roomPrice || ""}
              onChange={(e) => setEditingRoom({ ...editingRoom, roomPrice: Number(e.target.value) })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
      
          {/* Số lượng tối đa */}
          <div>
            <label className="block text-sm font-medium text-gray-700">Số lượng tối đa:</label>
            <input
              type="number"
              value={editingRoom.maximumOccupancy || ""}
              onChange={(e) => setEditingRoom({ ...editingRoom, maximumOccupancy: Number(e.target.value) })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
      
          {/* Khoa/Bộ phận */}
          <div>
            <label className="block text-sm font-medium text-gray-700">Khoa/Bộ phận:</label>
            <input
              type="text"
              value={editingRoom.department}
              onChange={(e) => setEditingRoom({ ...editingRoom, department: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
      
          {/* Ghi chú */}
          <div>
            <label className="block text-sm font-medium text-gray-700">Ghi chú:</label>
            <input
              type="text"
              value={editingRoom.note}
              onChange={(e) => setEditingRoom({ ...editingRoom, note: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
      
          {/* Trạng thái phòng */}
          <div>
            <label className="block text-sm font-medium text-gray-700">Trạng thái phòng:</label>
            <select
              value={editingRoom.roomStatus}
              onChange={(e) => setEditingRoom({ ...editingRoom, roomStatus: e.target.value })}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="empty_room">Còn trống</option>
              <option value="occupied_room">Đã đầy</option>
            </select>
          </div>
      
          {/* Nút cập nhật và hủy */}
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
  )
}

export default RoomManagement

