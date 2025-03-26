import React, { useState, useEffect } from "react";
import "./ContractStudent.css";

interface Contract {
  contractId: string;
  userId: string;
  roomName: string;
  userName: string; // Thêm thuộc tính này

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

  useEffect(() => {
    const fetchContracts = async () => {
      try {
        const token = localStorage.getItem("token");
        const userId = localStorage.getItem("userId");
        if (!token || !userId) {
          console.error("Token hoặc userId không tồn tại trong localStorage.");
          return;
        }
  
        const response = await fetch(
          `http://localhost:8080/api/v1/contracts/user/${userId}`,
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
        console.log("Dữ liệu từ API:", data); // Kiểm tra dữ liệu trả về
  
        const initialContracts = data.map((item: any) => ({
          contractId: item.contractId,
          userId: item.user ? item.user.userId : "Không có dữ liệu",
          userName: item.user ? item.user.fullName || "Không có dữ liệu" : "Không có dữ liệu", // Lấy tên khách hàng
          roomName: item.room ? item.room.roomName : "Không có dữ liệu",
          startDate: item.startDate ? new Date(item.startDate).toISOString() : "",
          endDate: item.endDate ? new Date(item.endDate).toISOString() : "",
          price: item.price || 0,
          depositStatus: item.depositStatus || "PENDING",
          contractStatus: item.contractStatus || "Unknown",
          note: item.note || "",
          createdAt: item.createdAt ? new Date(item.createdAt).toISOString() : "",
          updatedAt: item.updatedAt ? new Date(item.updatedAt).toISOString() : "",
        }));
        
        
  
        setContracts(initialContracts);
      } catch (error) {
        console.error("Lỗi khi gọi API:", error);
      }
    };
  
    fetchContracts();
  }, []);
  

  const formatDate = (dateString: string) => {
    if (!dateString) return "";
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

  // Hàm xử lý in hợp đồng
  const handlePrintContract = (contract: Contract) => {
    // Tạo 1 cửa sổ mới
    const newWindow = window.open("", "_blank", "width=800,height=600");
    if (!newWindow) return;

    // Tạo nội dung HTML cho hợp đồng
    const contractHtml = `
  <html>
    <head>
      <title>Hợp đồng - ${contract.contractId}</title>
      <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1, h2, h3 { text-align: center; }
        .contract-info { margin: 20px 0; }
        .label { font-weight: bold; }
        .field { margin-bottom: 10px; }
      </style>
    </head>
    <body>
      <h1>HỢP ĐỒNG THUÊ PHÒNG</h1>
      <h3>Mã hợp đồng: ${contract.contractId}</h3>
      <div class="contract-info">
        <div class="field">
          <span class="label">Khách hàng:</span> ${contract.userName}
        </div>
        <div class="field">
          <span class="label">Mã phòng:</span> ${contract.roomName}
        </div>
        <div class="field">
          <span class="label">Ngày bắt đầu:</span> ${formatDate(contract.startDate)}
        </div>
        <div class="field">
          <span class="label">Ngày kết thúc:</span> ${formatDate(contract.endDate)}
        </div>
        <div class="field">
          <span class="label">Giá thuê:</span> ${contract.price.toLocaleString()} VNĐ
        </div>
        <div class="field">
          <span class="label">Trạng thái cọc:</span> ${getDepositStatusLabel(contract.depositStatus)}
        </div>
        <div class="field">
          <span class="label">Trạng thái hợp đồng:</span> ${getContractStatusLabel(contract.contractStatus)}
        </div>
        <div class="field">
          <span class="label">Ghi chú:</span> ${contract.note}
        </div>
        <div class="field">
          <span class="label">Cập nhật lần cuối:</span> ${formatDate(contract.updatedAt)}
        </div>
      </div>

      <p>Người thuê cam kết tuân thủ mọi quy định...</p>
      <p>Chữ ký bên A (Chủ trọ)..............</p>
      <p>Chữ ký bên B (Người thuê)...........</p>

      <script>
        window.print();
      </script>
    </body>
  </html>
`;

    // Ghi nội dung vào cửa sổ
    newWindow.document.open();
    newWindow.document.write(contractHtml);
    newWindow.document.close();
  };

  return (
    <div className="contract-management">
      <h2>Danh sách hợp đồng</h2>
      {/* <div className="filters">
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
      </div> */}

      {/* Bảng danh sách hợp đồng */}
      <table>
      <thead>
  <tr>
    <th>STT</th>
    <th>Mã HĐ</th>
    <th>Khách hàng</th> {/* Thêm cột Tên khách hàng */}
    <th>Phòng</th>
    <th>Ngày bắt đầu</th>
    <th>Ngày kết thúc</th>
    <th>Giá thuê</th>
    <th>Thanh toán</th>
    <th>Trạng thái</th>
    <th>Ghi chú</th>
    <th>Cập nhật</th>
    <th>Thao tác</th>
  </tr>
</thead>
<tbody>
  {contracts.map((contract, index) => (
    <tr key={contract.contractId}>
      <td>{index + 1}</td>
      <td>{contract.contractId}</td>
      <td>{contract.userName}</td> {/* Hiển thị tên khách hàng */}
      <td>{contract.roomName}</td>
      <td>{formatDate(contract.startDate)}</td>
      <td>{formatDate(contract.endDate)}</td>
      <td>{contract.price?.toLocaleString()} VNĐ</td>
      <td>{getDepositStatusLabel(contract.depositStatus)}</td>
      <td>{getContractStatusLabel(contract.contractStatus)}</td>
      <td>{contract.note}</td>
      <td>{formatDate(contract.updatedAt)}</td>
      <td>
        <button
          onClick={() => handlePrintContract(contract)}
          className="print-button"
        >
          In hợp đồng
        </button>
      </td>
    </tr>
  ))}
</tbody>


      </table>
    </div>
  );
};

export default ContractManagement;
