import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Login.css";

const LoginForm: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSubmitting) return;
    setIsSubmitting(true);
    setError("");

    try {
      const response = await axios.post("http://localhost:8080/api/v1/auth/token", {
        userName: email,
        passWord: password,
      });

      const { token, authenticated, roles, userId, fullName } = response.data.result;

      if (!authenticated) {
        setError("Xác thực thất bại!");
        return;
      }

      // Lưu thông tin vào localStorage
      localStorage.setItem("token", token);
      localStorage.setItem("userId", userId);
      localStorage.setItem("fullName", fullName);
      localStorage.setItem("roles", JSON.stringify(roles));

      // Chuyển hướng theo role
      if (roles.includes("ADMIN")) {
        navigate("/admin/dashboard");
      } else if (roles.includes("MANAGER")) {
        navigate("/manager/dashboard");
      } else if (roles.includes("STUDENT")) {
        navigate("/student/dashboard");
      }
    } catch (error: any) {
      console.error("Lỗi đăng nhập:", error.response?.data || error.message);
      setError("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="login-page">
      {/* Cột trái - Ảnh */}
      <div className="left-section">
        {/* Thay link ảnh theo ý bạn, ví dụ link Unsplash */}
        <img
          src="https://picsum.photos/1200"
          alt="Left Banner"
          className="left-image"
        />
      </div>

      {/* Cột phải - Form đăng nhập */}
      <div className="right-section">
        <div className="login-content">
          <h1>Đăng nhập</h1>
          <p className="subtitle">
            Đăng nhập vào hệ thống ký túc xá của chúng tôi
          </p>

          {/* Bạn có thể thêm tab nếu muốn, ví dụ: Login / Create Profile */}
          {/* <div className="tab-switch">
            <button className="active">Login</button>
            <button>Create Profile</button>
          </div> */}

          <form onSubmit={handleSubmit} className="login-form">
            <input
              type="text"
              placeholder="Enter Username"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Enter Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            {error && <p className="error-message">{error}</p>}

            {/* <div className="remember-forgot">
              <label className="remember-me">
                <input type="checkbox" />
                Remember Me
              </label>
              <a href="#forgot">Forgot Password?</a>
            </div> */}

            <button type="submit" className="login-btn" disabled={isSubmitting}>
              {isSubmitting ? "Đang đăng nhập..." : "Login"}
            </button>
          </form>

          {/* <button className="facebook-btn">Continue with Facebook</button> */}

          {/* Nút đăng nhập khách */}
          <button className="facebook-btn" onClick={() => navigate("/guestdashboard")}>
            Continue as Guest
          </button>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;
