import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Login.css";

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(""); // State để hiển thị lỗi
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();
  
  if (isSubmitting) return; // Nếu đang xử lý request, không gửi tiếp
  setIsSubmitting(true);
  setError("");

  try {
    const response = await axios.post("http://localhost:8080/api/v1/user/login", {
      userName: email,
      passWord: password,
    });

    localStorage.setItem("token", response.data);
    console.log("Đăng nhập thành công:", response.data);
    navigate("/dashboard");
  } catch (error: any) {
    console.error("Lỗi đăng nhập:", error.response?.data || error.message);
    setError("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
  } finally {
    setIsSubmitting(false); // Cho phép submit lại sau khi xử lý xong
  }
};


 

  const handleGuestLogin = () => {
    console.log("Đăng nhập với tư cách khách");
    navigate("/dashboard");
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
          
          {error && <p className="error-message">{error}</p>} {/* Hiển thị lỗi */}

          <button className="guest-login" onClick={handleGuestLogin}>
            Tiếp tục với tư cách khách
          </button>

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
