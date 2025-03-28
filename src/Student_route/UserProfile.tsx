import React, { useState, useEffect } from "react";
import "./UserProfile.css";

interface User {
  userId: string;
  fullName: string;
  email: string;
  role: string;
}

interface Contract {
  contractId: string;
  userId: string;
  roomName: string;
  startDate: string;
  endDate: string;
}

const UserProfile: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          console.error("Token không tồn tại trong localStorage.");
          return;
        }
        const response = await fetch("http://localhost:8080/api/v1/users", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`Lỗi HTTP! Trạng thái: ${response.status}`);
        }
        const data = await response.json();
        setUsers(data);
      } catch (error) {
        console.error("Lỗi khi gọi API người dùng:", error);
      }
    };

    const fetchContracts = async () => {
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          console.error("Token không tồn tại trong localStorage.");
          return;
        }
        const response = await fetch("http://localhost:8080/api/v1/contracts", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`Lỗi HTTP! Trạng thái: ${response.status}`);
        }
        const data = await response.json();
        setContracts(data);
      } catch (error) {
        console.error("Lỗi khi gọi API hợp đồng:", error);
      }
    };

    fetchUsers();
    fetchContracts();
  }, []);

  const handleEditClick = (user: User) => {
    setSelectedUser(user);
    setIsEditing(true);
    setIsChangingPassword(false);
  };

  const handlePasswordClick = (user: User) => {
    setSelectedUser(user);
    setIsEditing(false);
    setIsChangingPassword(true);
  };

  const handleSave = () => {
    setIsEditing(false);
    setIsChangingPassword(false);
  };

  const getActiveRoomName = (userId: string): string => {
    const currentDate = new Date();
    const activeContract = contracts.find((contract) => {
      const start = new Date(contract.startDate);
      const end = new Date(contract.endDate);
      return (
        contract.userId === userId &&
        currentDate >= start &&
        currentDate <= end
      );
    });
    return activeContract ? activeContract.roomName : "N/A";
  };

  return (
    <div className="user-profile-container">
      <h2>Quản lý thông tin cá nhân</h2>
      <div className="table-container">
        <table className="user-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Họ và tên</th>
              <th>Email</th>
              <th>Vai trò</th>
              {/* <th>Phòng (Hợp đồng hiệu lực)</th> */}
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.userId}>
                <td>{user.userId}</td>
                <td>{user.fullName}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
                {/* <td>{getActiveRoomName(user.userId)}</td> */}
                <td>
                  <button
                    className="edit-button"
                    onClick={() => handleEditClick(user)}
                  >
                    Chỉnh sửa
                  </button>
                  <button
                    className="password-button"
                    onClick={() => handlePasswordClick(user)}
                  >
                    Đổi mật khẩu
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {(isEditing || isChangingPassword) && selectedUser && (
        <div className="form-container">
          <h3>{isEditing ? "Chỉnh sửa thông tin" : "Đổi mật khẩu"}</h3>
          {isEditing ? (
            <>
              <div className="form-group">
                <label>Họ và tên</label>
                <input type="text" defaultValue={selectedUser.fullName} />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input type="email" defaultValue={selectedUser.email} />
              </div>
            </>
          ) : (
            <>
              <div className="form-group">
                <label>Mật khẩu cũ</label>
                <input type="password" />
              </div>
              <div className="form-group">
                <label>Mật khẩu mới</label>
                <input type="password" />
              </div>
              <div className="form-group">
                <label>Xác nhận mật khẩu mới</label>
                <input type="password" />
              </div>
            </>
          )}
          <button className="save-button" onClick={handleSave}>
            Lưu
          </button>
        </div>
      )}
    </div>
  );
};

export default UserProfile;
