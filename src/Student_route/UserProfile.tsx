import React, { useState } from "react";
import "./UserProfile.css";

interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

const UserProfile = () => {
  const [users, setUsers] = useState<User[]>([
    { id: 1, name: "Nguyễn Văn A", email: "a@example.com", role: "Admin" },
    { id: 2, name: "Trần Thị B", email: "b@example.com", role: "User" },
  ]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isChangingPassword, setIsChangingPassword] = useState(false);

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
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td>{user.role}</td>
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
                <input type="text" defaultValue={selectedUser.name} />
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
