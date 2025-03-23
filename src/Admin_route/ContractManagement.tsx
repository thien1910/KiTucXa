import React, { useState, useEffect } from "react";
import "./ContractManagement.css";

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
        const response = await fetch(
          "http://localhost:8080/api/v1/contracts/list",
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

  // Hàm xóa hợp đồng
  const handleDelete = async (id: string) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa hợp đồng này? ❗")) {
      return;
    }

    const token = localStorage.getItem("token"); // Lấy token từ localStorage

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/contracts/delete/${id}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`, // Gửi token trong header
          },
        }
      );

      if (!response.ok) {
        throw new Error(`Xóa hợp đồng thất bại! (Mã lỗi: ${response.status})`);
      }

      // Xóa thành công, cập nhật danh sách trong state
      const updatedContracts = contracts.filter(
        (contract) => contract.contractId !== id
      );
      setContracts(updatedContracts);

      alert("✅ Hợp đồng đã được xóa thành công!");
    } catch (error) {
      console.error("Lỗi khi xóa hợp đồng:", error);
      alert(`❌ Xóa hợp đồng thất bại! Vui lòng thử lại sau.`);
    }
  };

  // Hàm mở form chỉnh sửa
  const handleEdit = (contract: Contract) => {
    setCurrentContract(contract);
    setFormData(contract);
    setIsEditing(true);
    setIsCreating(false);
  };

  // Hàm mở form tạo mới
  const handleCreate = () => {
    setFormData({
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
    setIsCreating(true);
    setIsEditing(false);
  };

  // Hàm xử lý thay đổi dữ liệu trong form
  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Hàm gọi API để cập nhật hợp đồng
  const updateContractApi = async (contract: Contract) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Bạn chưa đăng nhập hoặc token không hợp lệ!");
        return false;
      }

      const response = await fetch(
        `http://localhost:8080/api/v1/contracts/update/${contract.contractId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(contract),
        }
      );

      const result = await response.json();

      if (!response.ok) {
        throw new Error(`Lỗi: ${result.message || response.statusText}`);
      }

      return true;
    } catch (error) {
      console.error("Lỗi khi cập nhật hợp đồng:", error);
      return false;
    }
  };

  // Hàm gọi API để thêm hợp đồng mới
  const createContractApi = async (contractData: Contract) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        "http://localhost:8080/api/v1/contracts/add",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`, // Đính kèm token
          },
          body: JSON.stringify(contractData),
        }
      );

      const result = await response.json();

      if (!response.ok) {
        throw new Error(`Lỗi: ${result.message || response.statusText}`);
        console.error("Lỗi khi tạo hợp đồng:");
      }

      return true;
    } catch (error) {
      console.error("Lỗi khi thêm hợp đồng:", error);
      return false;
    }
  };

  // Hàm lưu chỉnh sửa
  const handleSave = async () => {
    if (isEditing && currentContract) {
      const updatedContracts = contracts.map((contract) =>
        contract.contractId === currentContract.contractId ? formData : contract
      );
      const isUpdated = await updateContractApi(formData);
      if (!isUpdated) {
        alert("❌ Cập nhật hợp đồng thất bại! Vui lòng thử lại sau.");
        return;
      }
      alert("✅ Cập nhật hợp đồng thành công!");
      setContracts(updatedContracts);
      setIsEditing(false);
      setCurrentContract(null);
    } else if (isCreating) {
      const newContract = await createContractApi(formData);
      if (!newContract) {
        alert("❌ Tạo hợp đồng mới thất bại! Vui lòng thử lại sau.");
        return;
      }
      alert("✅ Tạo hợp đồng mới thành công!");
      setContracts([...contracts, formData]);
      setIsCreating(false);
    }
  };

  // Hàm đóng form
  const handleCancel = () => {
    setIsEditing(false);
    setIsCreating(false);
    setCurrentContract(null);
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

      <button className="create-btn" onClick={handleCreate}>
        Tạo hợp đồng mới
      </button>

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
              <td>
                <button
                  className="edit-btn"
                  onClick={() => handleEdit(contract)}
                >
                  Sửa
                </button>
                <button
                  className="delete-btn"
                  onClick={() => handleDelete(contract.contractId)}
                >
                  Xóa
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Form chỉnh sửa hoặc tạo mới */}
      {(isEditing || isCreating) && (
        <div className="edit-form">
          <h3>{isEditing ? "Chỉnh sửa hợp đồng" : "Tạo hợp đồng mới"}</h3>

          {!isCreating && (
            <div className="form-group">
              <label>Mã hợp đồng:</label>
              <input
                type="text"
                name="contractId"
                value={formData.contractId}
                onChange={handleInputChange}
                disabled={isEditing} // Không cho phép sửa mã hợp đồng khi chỉnh sửa
              />
            </div>
          )}

          <div className="form-group">
            <label>Mã khách hàng:</label>
            <input
              type="text"
              name="userId"
              value={formData.userId}
              disabled={isEditing}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-group">
            <label>Mã phòng:</label>
            <input
              type="text"
              name="roomId"
              value={formData.roomId}
              disabled={isEditing}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-group">
            <label>Ngày bắt đầu:</label>
            <input
              type="date"
              name="startDate"
              value={formData.startDate}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-group">
            <label>Ngày kết thúc:</label>
            <input
              type="date"
              name="endDate"
              value={formData.endDate}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-group">
            <label>Giá thuê:</label>
            <input
              type="number"
              name="price"
              value={formData.price}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-group">
            <label>Trạng thái thanh toán:</label>
            <select
              name="depositStatus"
              value={formData.depositStatus}
              onChange={handleInputChange}
            >
              <option value="COMPLETED">Hoàn tất</option>
              <option value="INSUFFICIENT">Còn thiếu</option>
              <option value="UNPAID">Chưa thanh toán</option>
            </select>
          </div>

          <div className="form-group">
            <label>Trạng thái hợp đồng:</label>
            <select
              name="contractStatus"
              value={formData.contractStatus}
              onChange={handleInputChange}
            >
              <option value="Active">Đang hiệu lực</option>
              <option value="Inactive">Hết hiệu lực</option>
            </select>
          </div>

          <div className="form-group">
            <label>Ghi chú:</label>
            <textarea
              name="note"
              value={formData.note || ""}
              onChange={handleInputChange}
            />
          </div>

          <div className="form-actions">
            <button onClick={handleSave}>Lưu</button>
            <button onClick={handleCancel}>Hủy</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ContractManagement;
