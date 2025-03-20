import React, { useState, useEffect } from "react";
import "./Dashboard.css";
import AccountManagement from "./AccountManagement";
import UserProfile from "./UserProfile";
import StudentManagement from "./StudentManagement";
import ContractManagement from "./ContractManagement";

const Dashboard = () => {
  const [view, setView] = useState<string>("dashboard");
  const [userFullName, setUserFullName] = useState<string>("");

  useEffect(() => {
    // Lấy thông tin người dùng từ localStorage hoặc API
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
    <div className="dashboard-container full-screen">
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="logo">🏠</div>
          <h2>Hệ thống quản lý</h2>
          <p>{userFullName}</p>
        </div>
        <nav>
          <ul>
            <li onClick={() => setView("dashboard")}>Dashboard</li>
            <li onClick={() => setView("accountManagement")}>Quản lí tài khoản</li>
            <li onClick={() => setView("userProfile")}>Quản lí thông tin cá nhân</li>
            <li onClick={() => setView("studentManagement")}>Quản lí sinh viên</li>
            <li onClick={() => setView("contractManagement")}>Quản lí hợp đồng</li>
            <li>Quản lý hóa đơn</li>
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
              </div>
            </section>
          </>
        )}
      </main>
    </div>
  );
};

export default Dashboard;
