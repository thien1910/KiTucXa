import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginForm from "./LoginForm";
import Dashboard from "./Dashboard";
import "./Login.css";
import AccountManagement from "./AccountManagement";
import UserProfile from "./UserProfile";
import StudentManagement from "./StudentManagement";
import ContractManagement from "./ContractManagement";
import GuestDashboard from "./Guest_route/GuestDashboard";

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
        <Route path="/guestdashboard" element={<GuestDashboard/>} />
      </Routes>
    </Router>
  );
};

export default App;
