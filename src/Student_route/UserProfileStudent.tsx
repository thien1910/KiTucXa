import React, { useState, useEffect } from "react";
import "../styles/UserProfileStudent.css";

interface User {
  userId: string;
  fullName: string;
  userName: string;
  passWord: string;
  phoneNumber: string;
  gender: "MALE" | "FEMALE";
  roomNameStudent: string;
  cccd: string;
  status: string;
  country: string;
  roles: string[];
  avatarUrl?: string;
}

const UserProfileStudent: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);

  // State điều khiển hiển thị Modal
  const [isEditingInfo, setIsEditingInfo] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);

  // State chỉnh sửa thông tin
  const [editedFullName, setEditedFullName] = useState("");
  const [editedPhoneNumber, setEditedPhoneNumber] = useState("");
  const [editedGender, setEditedGender] = useState<"MALE" | "FEMALE">("MALE");
  const [editedCccd, setEditedCccd] = useState("");

  // State đổi mật khẩu
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/user/my-info",
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          },
        );

        if (!response.ok) {
          throw new Error("Lỗi khi lấy dữ liệu");
        }

        const userData = await response.json();
        const fetchedUser: User = {
          userId: userData.userId,
          fullName: userData.fullName,
          userName: userData.userName,
          passWord: userData.passWord,
          phoneNumber: userData.phoneNumber,
          gender: userData.gender as "MALE" | "FEMALE",
          roomNameStudent: userData.roomNameStudent,
          cccd: userData.cccd,
          status: userData.status,
          country: userData.country,
          roles: userData.roles,
          avatarUrl: userData.avatarUrl || "https://picsum.photos/200",
        };
        setUser(fetchedUser);

        // Gán giá trị ban đầu cho form
        setEditedFullName(fetchedUser.fullName);
        setEditedPhoneNumber(fetchedUser.phoneNumber);
        setEditedGender(fetchedUser.gender);
        setEditedCccd(fetchedUser.cccd);
      } catch (error) {
        console.error("Lỗi khi gọi API:", error);
      }
    };

    fetchUserData();
  }, [token]);

  // Lưu thông tin (không đổi mật khẩu)
  const handleSaveInfo = async () => {
    if (!user) return;

    const updatedData = {
      userId: user.userId,
      userName: user.userName,
      passWord: user.passWord, // Giữ nguyên mật khẩu cũ
      fullName: editedFullName,
      gender: editedGender,
      cccd: editedCccd,
      phoneNumber: editedPhoneNumber,
      status: user.status,
      country: user.country,
    };

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/user/${user.userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(updatedData),
        },
      );

      const result = await response.json();
      if (!response.ok) {
        throw new Error(result.message || "Lỗi khi cập nhật thông tin.");
      }

      // Cập nhật lại state user
      setUser({ ...user, ...updatedData });
      setIsEditingInfo(false);
      alert("Cập nhật thông tin thành công!");
    } catch (error) {
      console.error("Lỗi khi cập nhật thông tin:", error);
    }
  };

  // Đổi mật khẩu
  const handleChangePassword = async () => {
    if (!user) return;

    if (newPassword !== confirmPassword) {
      alert("Mật khẩu mới và xác nhận mật khẩu không khớp.");
      return;
    }

    const passwordData = {
      oldPassword,
      newPassword,
    };

    console.log("Sending request to change password with token:", token);

    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/user/change-password",
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(passwordData),
        },
      );

      console.log("Response status:", response.status);
      const result = await response.json();
      console.log("Response body:", result);

      if (!response.ok) {
        throw new Error(result.message || "Lỗi khi đổi mật khẩu.");
      }

      setIsEditingPassword(false);
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
      alert("Đổi mật khẩu thành công!");
    } catch (error) {
      console.error("Lỗi khi đổi mật khẩu:", error);
      alert("Đổi mật khẩu thất bại!");
    }
  };

  return (
    <div className="profile-page">
      {/* Cột bên trái */}
      <div className="profile-left">
        {user && (
          <>
            <img src={user.avatarUrl} alt="Avatar" className="avatar-img" />
            <h2 className="user-name">{user.fullName}</h2>
            <p className="user-role">{user.roles.join(", ")}</p>
            <p className="user-status">
              Phòng: {user.roomNameStudent || "Chưa có phòng"}
            </p>
          </>
        )}
      </div>

      {/* Cột bên phải */}
      <div className="profile-right">
        {user ? (
          <div className="profile-info">
            <h2>THÔNG TIN CÁ NHÂN</h2>
            <div className="info-row">
              <p>
                <strong>Email:</strong> {user.userName}
              </p>
              <p>
                <strong>Phone:</strong> {user.phoneNumber}
              </p>
              <p>
                <strong>Giới tính:</strong>{" "}
                {user.gender === "MALE" ? "Nam" : "Nữ"}
              </p>
              <p>
                <strong>CCCD:</strong> {user.cccd}
              </p>
              <p>
                <strong>Trạng thái:</strong> {user.status}
              </p>
              <p>
                <strong>Quốc gia:</strong> {user.country}
              </p>
              <p>
                <strong>ID:</strong> {user.userId}
              </p>
            </div>

            <div className="action-buttons">
              <button
                className="edit-info-btn"
                onClick={() => {
                  setIsEditingInfo(true);
                  setIsEditingPassword(false);
                }}
              >
                Chỉnh sửa
              </button>
              <button
                className="change-password-btn"
                onClick={() => {
                  setIsEditingPassword(true);
                  setIsEditingInfo(false);
                }}
              >
                Đổi mật khẩu
              </button>
            </div>
          </div>
        ) : (
          <p>Đang tải dữ liệu...</p>
        )}
      </div>

      {/* Modal Chỉnh sửa thông tin */}
      {isEditingInfo && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Chỉnh sửa thông tin</h3>
            <div className="form-group">
              <label>Họ và tên</label>
              <input
                type="text"
                value={editedFullName}
                onChange={(e) => setEditedFullName(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Số điện thoại</label>
              <input
                type="text"
                value={editedPhoneNumber}
                onChange={(e) => setEditedPhoneNumber(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Giới tính</label>
              <select
                value={editedGender}
                onChange={(e) =>
                  setEditedGender(e.target.value as "MALE" | "FEMALE")
                }
              >
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
              </select>
            </div>
            <div className="form-group">
              <label>CCCD</label>
              <input
                type="text"
                value={editedCccd}
                onChange={(e) => setEditedCccd(e.target.value)}
              />
            </div>

            <div className="modal-actions">
              <button className="save-btn" onClick={handleSaveInfo}>
                Lưu
              </button>
              <button
                className="cancel-btn"
                onClick={() => setIsEditingInfo(false)}
              >
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Đổi mật khẩu */}
      {isEditingPassword && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Đổi mật khẩu</h3>
            <div className="form-group">
              <label>Mật khẩu cũ</label>
              <input
                type="password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Mật khẩu mới</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label>Xác nhận mật khẩu mới</label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </div>

            <div className="modal-actions">
              <button className="save-btn" onClick={handleChangePassword}>
                Lưu
              </button>
              <button
                className="cancel-btn"
                onClick={() => setIsEditingPassword(false)}
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

export default UserProfileStudent;
