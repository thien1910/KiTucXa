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
  // State để lưu tên phòng từ hợp đồng còn hiệu lực
  const [activeRoomName, setActiveRoomName] = useState<string>("Chưa có phòng");

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

  // Fetch thông tin người dùng
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
          // Nếu API trả về roomNameStudent thì dùng nó,
          // nhưng chúng ta sẽ ưu tiên cập nhật tên phòng theo hợp đồng Active
          roomNameStudent: userData.roomNameStudent,
          cccd: userData.cccd,
          status: userData.status,
          country: userData.country,
          roles: userData.roles,
          avatarUrl: userData.avatarUrl || "https://picsum.photos/200",
        };
        setUser(fetchedUser);

        // Gán giá trị ban đầu cho form chỉnh sửa
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

  // Fetch hợp đồng của người dùng để lấy tên phòng của hợp đồng còn hiệu lực
  useEffect(() => {
    const fetchActiveContract = async () => {
      try {
        const userId = localStorage.getItem("userId");
        if (!token || !userId) {
          console.error("Token hoặc userId không tồn tại trong localStorage.");
          return;
        }
        const response = await fetch(
          `http://localhost:8080/api/v1/contracts/user/${userId}`,
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
        // Lọc hợp đồng có trạng thái Active (còn hiệu lực)
        const activeContract = data.find(
          (item: any) => item.contractStatus === "Active",
        );
        if (activeContract) {
          // Nếu hợp đồng có đối tượng room, lấy roomName từ đó
          setActiveRoomName(
            activeContract.room
              ? activeContract.room.roomName
              : "Chưa có phòng",
          );
        } else {
          setActiveRoomName("Chưa có phòng");
        }
      } catch (error) {
        console.error("Lỗi khi gọi API hợp đồng:", error);
      }
    };

    fetchActiveContract();
  }, [token]);

  // Hàm lưu thông tin (không đổi mật khẩu)
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
        `http://localhost:8080/api/v1/user/student/${user.userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(updatedData),
        },
      );

      // Lấy phản hồi dưới dạng text trước
      const text = await response.text();
      console.log("Response text:", text);

      let result: any = {};
      if (text && text.trim() !== "") {
        try {
          result = JSON.parse(text);
        } catch (parseError) {
          console.error("Lỗi parse JSON:", parseError, "Text:", text);
        }
      }

      if (!response.ok) {
        throw new Error(result.message || "Lỗi khi cập nhật thông tin.");
      }

      // Cập nhật lại state user nếu thành công
      setUser({ ...user, ...updatedData });
      setIsEditingInfo(false);
      alert("Cập nhật thông tin thành công!");
    } catch (error) {
      console.error("Lỗi khi cập nhật thông tin:", error);
      alert("Cập nhật thông tin thất bại!");
    }
  };

  // Hàm xử lý đổi mật khẩu
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
      const text = await response.text();
      console.log("Response body:", text);

      let result: any = {};
      if (text && text.trim() !== "") {
        try {
          result = JSON.parse(text);
        } catch (parseError) {
          console.error("Lỗi parse JSON:", parseError, "Text:", text);
        }
      }

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
            {/* Hiển thị tên phòng từ hợp đồng còn hiệu lực */}
            <p className="user-status">Phòng: {activeRoomName}</p>
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
                <strong>Username:</strong> {user.userName}
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
                type="number"
                min="0"
                value={editedPhoneNumber}
                onChange={(e) => {
                  const val = e.target.value;
                  // Chỉ cho phép nhập các ký tự số (0-9)
                  if (/^\d*$/.test(val)) {
                    setEditedPhoneNumber(val);
                  }
                }}
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
                type="number"
                min="0"
                value={editedCccd}
                onChange={(e) => {
                  const val = e.target.value;
                  if (/^\d*$/.test(val)) {
                    setEditedCccd(val);
                  }
                }}
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
