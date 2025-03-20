import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Login.css";

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(""); // State để hiển thị lỗi
  const [userInfo, setUserInfo] = useState<any>(null); // State lưu thông tin người dùng
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSubmitting) return;
    setIsSubmitting(true);
    setError("");
  
    try {
      const response = await axios.post("http://localhost:8080/api/v1/user/login", {
        userName: email,
        passWord: password,
      });
  
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("user", JSON.stringify(response.data.user)); // Lưu thông tin người dùng
      console.log("Đăng nhập thành công:", response.data);
      navigate("/dashboard");
    } catch (error: any) {
      console.error("Lỗi đăng nhập:", error.response?.data || error.message);
      setError("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
    } finally {
      setIsSubmitting(false);
    }
  };
  

  return (
    <div className="login-form w3_form">
      <div className="login-title w3_title">
        <h1>Trang đăng nhập</h1>
      </div>
      <div className="login w3_login">
        <h2 className="login-header w3_header">Tài khoản</h2>
        <div className="w3l_grid">
          <form className="login-container" onSubmit={handleSubmit}>
            <input
              type="text"
              placeholder="Email"
              name="Email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Mật khẩu"
              name="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <input type="submit" value="Đăng nhập" />
          </form>

          {error && <p className="error-message">{error}</p>}

          <button className="guest-login" onClick={() => navigate("/dashboard")}>
            Tiếp tục với tư cách khách
          </button>

          {userInfo && (
            <div className="user-info">
              <h3>Xin chào, {userInfo.fullName}!</h3>
              <p>Email: {userInfo.email}</p>
            </div>
          )}

          <div className="bottom-text w3_bottom_text">
            <h4>
              <a href="#">Quên mật khẩu?</a>
            </h4>
          </div>
        </div>
      </div>
      <div className="footer-w3l">
        <p className="agile">
          &copy; 2025 Biểu mẫu đăng nhập. Bảo lưu mọi quyền | Thiết kế bởi 22ITSE-1.2
        </p>
      </div>
    </div>
  );
};

export default LoginForm;
