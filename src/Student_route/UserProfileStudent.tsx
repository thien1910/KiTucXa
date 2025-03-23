import React, { useState, useEffect } from "react";
import "./UserProfileStudent.css";

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

const UserProfileStudent = () => {
    const [user, setUser] = useState<User | null>(null);
    const [isEditing, setIsEditing] = useState(false);

    // Trạng thái để lưu giá trị chỉnh sửa
    const [editedFullName, setEditedFullName] = useState("");
    const [editedPhoneNumber, setEditedPhoneNumber] = useState("");
    const [editedGender, setEditedGender] = useState<"MALE" | "FEMALE">("MALE");
    const [editedCccd, setEditedCccd] = useState("");
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const token = localStorage.getItem("token");

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/v1/user/my-info", {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        "Authorization": `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error("Lỗi khi lấy dữ liệu");
                }

                const userData = await response.json();

                setUser({
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
                    avatarUrl: userData.avatarUrl || "https://via.placeholder.com/150",
                });

                // Gán giá trị ban đầu
                setEditedFullName(userData.fullName);
                setEditedPhoneNumber(userData.phoneNumber);
                setEditedGender(userData.gender as "MALE" | "FEMALE");
                setEditedCccd(userData.cccd);
            } catch (error) {
                console.error("Lỗi khi gọi API:", error);
            }
        };

        fetchUserData();
    }, [token]);

    const handleSave = async () => {
        if (!user) return;
    
        const updatedData = {
            userId: user.userId,
            userName: user.userName,
            passWord: newPassword || user.passWord, // Giữ nguyên mật khẩu cũ nếu không nhập mật khẩu mới
            fullName: editedFullName,
            gender: editedGender,
            cccd: editedCccd,
            phoneNumber: editedPhoneNumber,
            status: user.status,
            country: user.country,
        };
    
        console.log("Dữ liệu gửi lên server:", JSON.stringify(updatedData));
    
        try {
            const response = await fetch(`http://localhost:8080/api/v1/user/${user.userId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`,
                },
                body: JSON.stringify(updatedData),
            });
    
            const result = await response.json();
            console.log("Response từ server:", result);
    
            if (!response.ok) {
                throw new Error(result.message || "Lỗi khi cập nhật thông tin.");
            }
    
            setUser({ ...user, ...updatedData });
            setIsEditing(false);
            alert("Cập nhật thành công!");
        } catch (error) {
            console.error("Lỗi khi cập nhật thông tin:", error);
        }
    };
    

    return (
        <div className="profile-container">
            <h2>Hồ sơ cá nhân</h2>

            {user ? (
                <div className="profile-card">
                    <div className="profile-left">
                        <img src={user.avatarUrl} alt="Avatar" className="profile-avatar" />
                        <h3>{user.fullName}</h3>
                        <p><strong>Vai trò:</strong> {user.roles.join(", ")}</p>
                        <p><strong>ID:</strong> {user.userId}</p>
                    </div>

                    <div className="profile-right">
                        <p><strong>Tên đăng nhập:</strong> {user.userName}</p>
                        <p><strong>Họ và tên:</strong> {user.fullName}</p>
                        <p><strong>Số điện thoại:</strong> {user.phoneNumber}</p>
                        <p><strong>Giới tính:</strong> {user.gender === "MALE" ? "Nam" : "Nữ"}</p>
                        <p><strong>Phòng:</strong> {user.roomNameStudent}</p>
                        <p><strong>CCCD:</strong> {user.cccd}</p>
                        <p><strong>Trạng thái:</strong> {user.status}</p>
                        <p><strong>Quốc gia:</strong> {user.country}</p>

                        <div className="profile-actions">
                            <button className="edit-button" onClick={() => setIsEditing(true)}>Chỉnh sửa</button>
                        </div>
                    </div>
                </div>
            ) : (
                <p>Đang tải dữ liệu...</p>
            )}

            {isEditing && user && (
                <div className="form-container">
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
                            onChange={(e) => setEditedGender(e.target.value as "MALE" | "FEMALE")}
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
                    <button className="save-button" onClick={handleSave}>Lưu</button>
                </div>
            )}
        </div>
    );
};

export default UserProfileStudent;