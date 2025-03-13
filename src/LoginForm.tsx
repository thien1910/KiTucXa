import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Email:", email, "Mật khẩu:", password);
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
              type="email"
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
