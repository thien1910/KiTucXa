import React, { useState, useEffect } from "react";
import "./ContractStudent.css";

// Định nghĩa interface cho dữ liệu hợp đồng
interface Contract {
  contractId: string;
  userId: string;
  roomId: string;
  startDate: string;
  endDate: string;
  price: number;
  depositStatus: string;
  contractStatus: string;
  note: string;
  createdAt: string;
  updatedAt: string;
}

let initialContracts: Contract[] = [];

const ContractManagement: React.FC = () => {
  const [contracts, setContracts] = useState<Contract[]>(initialContracts);
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [currentContract, setCurrentContract] = useState<Contract | null>(null);
  const [isCreating, setIsCreating] = useState<boolean>(false);

  useEffect(() => {
    const fetchContracts = async () => {
      try {
        const token = localStorage.getItem("token"); // 🔐 Lấy token từ localStorage
        const userId = localStorage.getItem("userId"); // 🔐 Lấy userId từ localStorage
        if (!token || !userId) {
          console.error("Token hoặc userId không tồn tại trong localStorage.");
          return;
        }
        const response = await fetch(
          `http://localhost:8080/api/v1/contracts/user/${userId}`, // Thay userId bằng ID thực tế
          {
            method: "GET",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        // 🔹 Chuyển đổi dữ liệu từ API
        const initialContracts = data.map((item: any) => ({
          contractId: item.contractId,
          userId: item.userId,
          roomId: item.roomId,
          employeeName: "N/A", // API không có dữ liệu này
          contractType: "Hợp đồng thuê",
          startDate: item.startDate
            ? new Date(item.startDate).toISOString()
            : null,
          endDate: item.endDate ? new Date(item.endDate).toISOString() : null,
          price: item.price || 0,
          depositStatus: item.depositStatus || "PENDING",
          contractStatus: item.contractStatus || "Unknown",
          note: item.note || "",
          createdAt: item.createdAt
            ? new Date(item.createdAt).toISOString()
            : null,
          updatedAt: item.updatedAt
            ? new Date(item.updatedAt).toISOString()
            : null,
        }));

        setContracts(initialContracts); // ✅ Cập nhật state với dữ liệu API
      } catch (error) {
        console.error("Lỗi khi gọi API:", error);
      }
    };

    fetchContracts();
  }, []);

  // State cho form chỉnh sửa và tạo mới
  const [formData, setFormData] = useState<Contract>({
    contractId: "",
    userId: "",
    roomId: "",
    startDate: "",
    endDate: "",
    price: 0,
    depositStatus: "",
    contractStatus: "",
    note: "",
    createdAt: "",
    updatedAt: "",
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  const getDepositStatusLabel = (status: string) => {
    switch (status) {
      case "COMPLETED":
        return "Hoàn tất";
      case "INSUFFICIENT":
        return "Còn thiếu";
      case "UNPAID":
        return "Chưa thanh toán";
      default:
        return "Không xác định";
    }
  };

  const getContractStatusLabel = (status: string) => {
    switch (status) {
      case "Active":
        return "Đang hiệu lực";
      case "Inactive":
        return "Hết hiệu lực";
      default:
        return "Không xác định";
    }
  };

  return (
    <div className="contract-management">
      <h2>Quản lý hợp đồng</h2>
      <div className="filters">
        <select defaultValue="10">
          <option value="10">Hiển thị 10</option>
          <option value="20">Hiển thị 20</option>
          <option value="50">Hiển thị 50</option>
        </select>
        <select>
          <option>Chọn trạng thái</option>
          <option>Đã thuê</option>
          <option>Chưa thuê</option>
        </select>
        <input type="text" placeholder="Tìm kiếm..." />
      </div>

      {/* Bảng danh sách hợp đồng */}
      <table>
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã HĐ</th>
            <th>Mã KH</th>
            <th>Mã phòng</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Giá thuê</th>
            <th>Thanh toán</th>
            <th>Trạng thái</th>
            <th>Cập nhật</th>
            <th>Ghi chú</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {contracts.map((contract, index) => (
            <tr key={contract.contractId}>
              <td>{index + 1}</td>
              <td>{contract.contractId}</td>
              <td>{contract.userId}</td>
              <td>{contract.roomId}</td>
              <td>{formatDate(contract.startDate)}</td>
              <td>{formatDate(contract.endDate)}</td>
              <td>{contract.price?.toLocaleString()} VNĐ</td>
              <td>{getDepositStatusLabel(contract.depositStatus)}</td>
              <td>{getContractStatusLabel(contract.contractStatus)}</td>
              <td>{contract.note}</td>
              <td>{formatDate(contract.updatedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ContractManagement;