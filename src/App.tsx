import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginForm from "./LoginForm";
import Dashboard from "./Dashboard";
import "./Login.css";
import AccountManagement from "./AccountManagement";
import UserProfile from "./UserProfile";
import StudentManagement from "./StudentManagement";
import ContractManagement from "./ContractManagement";
import InvoiceManagement from "./InvoiceManagement";
import RoomManagement from "./RoomManagement";
import PaymentPage from "./PaymentPage";
import ServiceManagement from "./ServiceManagement"; // Import ServiceManagement

const App: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginForm />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/account-management" element={<AccountManagement />} />
        <Route path="/user-profile" element={<UserProfile />} />
        <Route path="/student-mangement" element={<StudentManagement />} />
        <Route path="/contract-management" element={<ContractManagement />} />
        <Route path="/invoice-management" element={<InvoiceManagement />} />
        <Route path="/room-management" element={<RoomManagement />} />
        <Route path="/payment" element={<PaymentPage />} />
        <Route path="/service-management" element={<ServiceManagement />} /> {/* Add this line */}
      </Routes>
    </Router>
  );
};

export default App;