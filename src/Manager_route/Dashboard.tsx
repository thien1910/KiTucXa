import React, { useState, useEffect } from "react";
import "../styles/Dashboard.css";
import AccountManagement from "../Admin_route/AccountManagement";
// import UserProfile from "../Student_route/UserProfile";
import StudentManagement from "./StudentManagement";
import ContractManagement from "./ContractManagement";
import InvoiceManagement from "./InvoiceManagement";
import RoomManagement from "./RoomManagement";
import PaymentPage from "../Student_route/PaymentPage";
import ServiceManagement from "./ServiceManagement"; // Import ServiceManagement
import { useNavigate } from "react-router-dom";

const Dashboard = () => {
  const [view, setView] = useState<string>("dashboard");
  const [userFullName, setUserFullName] = useState<string>("");
  const navigate = useNavigate();
  const fullName = localStorage.getItem("fullName");

  const handleLogout = () => {
    localStorage.removeItem("token"); // Xóa token
    localStorage.removeItem("user"); // Xóa thông tin user
    navigate("/"); // Điều hướng về trang đăng nhập
  };
  useEffect(() => {
    const userData = localStorage.getItem("user");
    if (userData) {
      try {
        const parsedUser = JSON.parse(userData);
        setUserFullName(parsedUser.fullName || "Người dùng");
      } catch (error) {
        console.error("Lỗi khi parse dữ liệu người dùng:", error);
      }
    }
  }, []);

  return (
    <div className="dashboard-container">
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="logo">
            <span style={{ fontSize: "40px" }}>🏠</span>
          </div>
          <h2>Ký túc xá KTX</h2>
          <h1>
            Xin chào
            <br />
            {fullName}!
          </h1>
        </div>
        <nav>
          <ul>
            <li onClick={() => setView("dashboard")}>Dashboard</li>
            {/* <li onClick={() => setView("accountManagement")}>Quản lý tài khoản</li> */}
            {/* <li onClick={() => setView("userProfile")}>Quản lý thông tin cá nhân</li> */}
            <li onClick={() => setView("studentManagement")}>
              Quản lý sinh viên
            </li>
            <li onClick={() => setView("contractManagement")}>
              Quản lý hợp đồng
            </li>
            <li onClick={() => setView("invoiceManagement")}>
              Quản lý hóa đơn
            </li>
            <li onClick={() => setView("serviceManagement")}>
              Quản lý dịch vụ
            </li>{" "}
            {/* Update this line */}
            <li onClick={() => setView("roomManagement")}>Quản lý phòng</li>
            <li>Xem thống kê</li>
            <li onClick={handleLogout} className="logout-button">
              🚪 Đăng xuất
            </li>
          </ul>
        </nav>
      </aside>
      <main className="main-content">
        {view === "accountManagement" ? (
          <AccountManagement />
        ) : view === "studentManagement" ? (
          <StudentManagement />
        ) : view === "contractManagement" ? (
          <ContractManagement />
        ) : view === "invoiceManagement" ? (
          <InvoiceManagement />
        ) : view === "serviceManagement" ? ( // Add this condition
          <ServiceManagement />
        ) : view === "roomManagement" ? (
          <RoomManagement />
        ) : view === "payment" ? (
          <PaymentPage />
        ) : (
          <>
            <header className="dashboard-header">
              <h1>Chào mừng đến với hệ thống quản lý ký túc xá</h1>
              <p>Quản lý sinh viên, hợp đồng, thanh toán và nhiều hơn nữa.</p>
            </header>

            {/* <section className="quick-access">
              <h2>🔗 Truy cập nhanh</h2>
              <div className="quick-links">
                <button onClick={() => setView("studentManagement")}>Quản lý sinh viên</button>
                <button onClick={() => setView("contractManagement")}>Quản lý hợp đồng</button>
                <button onClick={() => setView("accountManagement")}>Quản lý tài khoản</button>
              </div>
            </section> */}

            <section className="dashboard-sections">
              {/* Hướng dẫn sử dụng */}
              <div className="dashboard-box">
                <h2>📘 Hướng dẫn sử dụng</h2>
                <ul>
                  <li>
                    👤 <b>Quản lý tài khoản:</b> Tạo, sửa và phân quyền tài
                    khoản.
                  </li>
                  <li>
                    🏠 <b>Quản lý sinh viên:</b> Xem danh sách, tìm kiếm và cập
                    nhật thông tin.
                  </li>
                  <li>
                    📜 <b>Quản lý hợp đồng:</b> Ký hợp đồng mới, gia hạn hoặc
                    chấm dứt.
                  </li>
                  <li>
                    💳 <b>Quản lý thanh toán:</b> Kiểm tra hóa đơn và xác nhận
                    thanh toán.
                  </li>
                </ul>
              </div>

              {/* Cập nhật hệ thống */}
              <div className="dashboard-box">
                <h2>⚙️ Cập nhật hệ thống</h2>
                <ul>
                  <li>
                    🚀 Phiên bản mới nhất: <b>v1.2.0</b>
                  </li>
                  <li>✅ Cải thiện giao diện người dùng.</li>
                  <li>⚡ Tối ưu tốc độ tải trang.</li>
                  <li>🔒 Bổ sung tính năng bảo mật.</li>
                </ul>
              </div>
            </section>

            <section className="dashboard-sections">
              {/* Thông báo quan trọng */}
              <div className="dashboard-box">
                <h2>📌 Thông báo quan trọng</h2>
                <ul>
                  <li>
                    📢 Hạn đóng tiền kỳ II: <b>15/04/2025</b>.
                  </li>
                  <li>
                    🔧 Bảo trì hệ thống nước: <b>20/03/2025</b>.
                  </li>
                  <li>
                    📋 Kiểm tra phòng cuối kỳ: <b>05/05/2025</b>.
                  </li>
                </ul>
              </div>

              {/* Sự kiện sắp diễn ra */}
              <div className="dashboard-box">
                <h2>📆 Sự kiện sắp diễn ra</h2>
                <ul>
                  <li>
                    🎉 Liên hoan chào đón tân sinh viên: <b>01/04/2025</b>.
                  </li>
                  <li>
                    🧹 Tổng vệ sinh ký túc xá: <b>10/04/2025</b>.
                  </li>
                  <li>
                    🏅 Giải bóng đá nội bộ: <b>20/04/2025</b>.
                  </li>
                </ul>
              </div>
            </section>
          </>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
