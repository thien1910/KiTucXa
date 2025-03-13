import React, { useState, useEffect } from 'react';
import './StudentManagement.css';

interface Student {
  id: string;
  name: string;
  class: string;
  room?: string;
}

const StudentManagement: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedStudent, setSelectedStudent] = useState<Student | null>(null);
  const [room, setRoom] = useState('');

  useEffect(() => {
    // Giả lập dữ liệu sinh viên
    setStudents([
      { id: 'SV001', name: 'Nguyen Van A', class: 'IT01', room: '101' },
      { id: 'SV002', name: 'Tran Thi B', class: 'IT02' },
      { id: 'SV003', name: 'Le Van C', class: 'IT03', room: '102' },
    ]);
  }, []);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const handleAssignRoom = () => {
    if (selectedStudent && room) {
      setStudents((prev) =>
        prev.map((s) =>
          s.id === selectedStudent.id ? { ...s, room } : s
        )
      );
      setSelectedStudent(null);
      setRoom('');
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
            <th>Lớp</th>
            <th>Phòng</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {students
            .filter((s) =>
              s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
              s.id.includes(searchTerm)
            )
            .map((student) => (
              <tr key={student.id}>
                <td>{student.id}</td>
                <td>{student.name}</td>
                <td>{student.class}</td>
                <td>{student.room || 'Chưa có'}</td>
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
          <h3>Xếp phòng cho {selectedStudent.name}</h3>
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
