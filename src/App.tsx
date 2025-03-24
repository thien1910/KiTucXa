import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginForm from "./LoginForm";
import Dashboard from "./Manager_route/Dashboard";
import "./Login.css";
import AccountManagement from "./Admin_route/AccountManagement";
import UserProfile from "./Student_route/UserProfile";
import StudentManagement from "./Manager_route/StudentManagement";
import ContractManagement from "./Manager_route/ContractManagement";
import InvoiceManagement from "./Manager_route/InvoiceManagement";
import RoomManagement from "./Manager_route/RoomManagement";
import PaymentPage from "./Student_route/PaymentPage";
import ServiceManagement from "./Manager_route/ServiceManagement"; // Import ServiceManagement
import GuestDashboard from "./Guest_route/GuestDashboard";
import StudentDashboard from "./Student_route/StudentDashboard";
import AdminDashboard from "./Admin_route/AdminDashboard";

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginForm />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/manager/dashboard" element={<Dashboard />} />

        <Route path="/account-management" element={<AccountManagement />} />
        <Route path="/user-profile" element={<UserProfile />} />
        <Route path="/student-mangement" element={<StudentManagement />} />
        <Route path="/contract-management" element={<ContractManagement />} />
        <Route path="/invoice-management" element={<InvoiceManagement />} />
        <Route path="/room-management" element={<RoomManagement />} />
        <Route path="/payment" element={<PaymentPage />} />
        <Route path="/service-management" element={<ServiceManagement />} /> {/* Add this line */}
        <Route path="/guestdashboard" element={<GuestDashboard/>} />
        <Route path="/student/dashboard" element={<StudentDashboard/>} />
        <Route path="/admin/dashboard" element={<AdminDashboard/>} />
      </Routes>
    </Router>
  );
};

export default App;