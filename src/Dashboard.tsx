import React, { useState } from "react";
import "./Dashboard.css";
import AccountManagement from "./AccountManagement";
import UserProfile from "./UserProfile";
import StudentManagement from "./StudentManagement";
import ContractManagement from "./ContractManagement";
import InvoiceManagement from "./InvoiceManagement";

const Dashboard = () => {
  const [view, setView] = useState<string>("dashboard");

  return (
    <div className="dashboard-container full-screen">
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="logo">🏠</div>
          <h2>Hệ thống quản lý</h2>
          <p>support@example.com</p>
        </div>
        <nav>
          <ul>
            <li onClick={() => setView("dashboard")}>Dashboard</li>
            <li onClick={() => setView("accountManagement")}>Quản lí tài khoản</li>
            <li onClick={() => setView("userProfile")}>Quản lí thông tin cá nhân</li>
            <li onClick={() => setView("studentManagement")}>Quản lí sinh viên</li>
            <li onClick={() => setView("contractManagement")}>Quản lí hợp đồng</li>
            <li onClick={() => setView("invoiceManagement")}>Quản lý hóa đơn</li>
            <li>Quản lý dịch vụ</li>
            <li>Quản lí phòng</li>
            <li>Thanh toán</li>
            <li>Xem thống kê</li>
          </ul>
        </nav>
      </aside>
      <main className="main-content full-width">
        {view === "accountManagement" ? (
          <AccountManagement />
        ) : view === "userProfile" ? (
          <UserProfile />
        ) : view === "studentManagement" ? (
          <StudentManagement />
        ) : view === "contractManagement" ? (
          <ContractManagement />
        ) : view === "invoiceManagement" ? (
          <InvoiceManagement />
        ) : (
          <>
            <header className="dashboard-header">
              <h1>Chào mừng đến với hệ thống quản lý ký túc xá</h1>
              <p>Quản lý sinh viên, hợp đồng, thanh toán và nhiều hơn nữa.</p>
            </header>

            <section className="quick-access">
              <h2>🔗 Truy cập nhanh</h2>
              <div className="quick-links">
                <button onClick={() => setView("studentManagement")}>Quản lý sinh viên</button>
                <button onClick={() => setView("contractManagement")}>Quản lý hợp đồng</button>
                <button onClick={() => setView("accountManagement")}>Quản lý tài khoản</button>
                <button onClick={() => setView("userProfile")}>Thông tin cá nhân</button>
                <button onClick={() => setView("serviceManagement")}>Quản lý dịch vụ</button>
              </div>
            </section>

            <section className="dashboard-sections">
              <div className="dashboard-box">
                <h2>📘 Hướng dẫn sử dụng</h2>
                <ul>
                  <li>👤 <b>Quản lý tài khoản:</b> Tạo, sửa và phân quyền tài khoản.</li>
                  <li>🏠 <b>Quản lý sinh viên:</b> Xem danh sách, tìm kiếm và cập nhật thông tin.</li>
                  <li>📜 <b>Quản lý hợp đồng:</b> Ký hợp đồng mới, gia hạn hoặc chấm dứt.</li>
                  <li>💳 <b>Quản lý thanh toán:</b> Kiểm tra hóa đơn và xác nhận thanh toán.</li>
                </ul>
              </div>
              <div className="dashboard-box">
                <h2>⚙️ Cập nhật hệ thống</h2>
                <ul>
                  <li>🚀 Phiên bản mới nhất: <b>v1.2.0</b></li>
                  <li>✅ Cải thiện giao diện người dùng.</li>
                  <li>⚡ Tối ưu tốc độ tải trang.</li>
                  <li>🔒 Bổ sung tính năng bảo mật.</li>
                </ul>
              </div>
            </section>

            <section className="dashboard-sections">
              <div className="dashboard-box">
                <h2>📌 Thông báo quan trọng</h2>
                <ul>
                  <li>📢 Hạn đóng tiền kỳ II: <b>15/04/2025</b>.</li>
                  <li>🔧 Bảo trì hệ thống nước: <b>20/03/2025</b>.</li>
                  <li>📋 Kiểm tra phòng cuối kỳ: <b>05/05/2025</b>.</li>
                </ul>
              </div>
              <div className="dashboard-box">
                <h2>📆 Sự kiện sắp diễn ra</h2>
                <ul>
                  <li>🎉 Liên hoan chào đón tân sinh viên: <b>01/04/2025</b>.</li>
                  <li>🧹 Tổng vệ sinh ký túc xá: <b>10/04/2025</b>.</li>
                  <li>🏅 Giải bóng đá nội bộ: <b>20/04/2025</b>.</li>
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
