import React, { useState, useEffect } from "react";
import { Table, Input, Button, Form, Modal, Select, message } from "antd";
import "./ContractManagement.css";
import { EditOutlined, DeleteOutlined, PrinterOutlined } from "@ant-design/icons";

const { Search } = Input;
const { Option } = Select;

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
  roomName?: string;       // Tên phòng (lấy từ API room)
  customerName?: string;   // Tên khách hàng (lấy từ API user)
}

const ContractManagement: React.FC = () => {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [isEditing, setIsEditing] = useState(false);
  const [currentContract, setCurrentContract] = useState<Contract | null>(null);
  const [isCreating, setIsCreating] = useState(false);
  
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
    roomName: "",
    customerName: "",
  });
  
  // Các state dùng để lọc hợp đồng
  const [searchQuery, setSearchQuery] = useState("");
  const [roomNameFilter, setRoomNameFilter] = useState(""); // Nếu rỗng: tất cả
  const [statusFilter, setStatusFilter] = useState(""); // Nếu rỗng: tất cả
  
  // State cho Modal tạo hóa đơn
  const [isInvoiceModalOpen, setIsInvoiceModalOpen] = useState(false);
  const [invoiceForm] = Form.useForm();

  const token = localStorage.getItem("token");

  // Hàm định dạng ngày
  const formatDate = (dateString: string) => {
    if (!dateString) return "";
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  // Hàm chuyển đổi trạng thái thanh toán
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

  // Hàm chuyển đổi trạng thái hợp đồng
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

  // Fetch hợp đồng và enrich với tên phòng & tên khách hàng từ API phụ
  useEffect(() => {
    const fetchContracts = async () => {
      try {
        message.info("Đang tải danh sách hợp đồng...");
        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/api/v1/contracts/list", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();

        const enrichedContracts: Contract[] = await Promise.all(
          data.map(async (item: any) => {
            // Lấy thông tin phòng
            const roomRes = await fetch(`http://localhost:8080/api/v1/rooms/${item.roomId}`, {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
            });
            const roomData = roomRes.ok ? await roomRes.json() : {};
            const roomName = roomData.result?.roomName || roomData.roomName || "N/A";

            // Lấy thông tin user (khách hàng)
            const userRes = await fetch(`http://localhost:8080/api/v1/user/manager/${item.userId}`, {
              headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
              },
            });
            const userData = userRes.ok ? await userRes.json() : {};
            const fullName = userData.result?.fullName || userData.fullName || "N/A";

            return {
              contractId: item.contractId,
              userId: item.userId,
              roomId: item.roomId,
              startDate: item.startDate ? new Date(item.startDate).toISOString() : "",
              endDate: item.endDate ? new Date(item.endDate).toISOString() : "",
              price: item.price || 0,
              depositStatus: item.depositStatus || "PENDING",
              contractStatus: item.contractStatus || "Unknown",
              note: item.note || "",
              createdAt: item.createdAt ? new Date(item.createdAt).toISOString() : "",
              updatedAt: item.updatedAt ? new Date(item.updatedAt).toISOString() : "",
              roomName,      
              customerName: fullName,
              
            };
          })
        );
        setContracts(enrichedContracts);
        message.success("Danh sách hợp đồng được tải thành công!");
      } catch (error) {
        console.error("Lỗi khi gọi API:", error);
        message.error("Lỗi khi tải danh sách hợp đồng!");
      }
    };

    fetchContracts();
  }, []);

  // Xử lý tìm kiếm
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
  };

  // Xử lý tìm kiếm theo tên phòng
  const handleRoomFilter = (e: React.ChangeEvent<HTMLInputElement>) => {
    setRoomNameFilter(e.target.value);
  };

  // Xử lý lọc theo trạng thái hợp đồng (Active/Inactive)
  const handleStatusFilter = (value: string) => {
    setStatusFilter(value);
  };

  // Áp dụng các tiêu chí lọc
  const filteredContracts = contracts.filter((contract) => {
    const matchesSearchQuery =
      contract.contractId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      contract.userId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (contract.customerName || "").toLowerCase().includes(searchQuery.toLowerCase());

      const matchesRoomFilter = roomNameFilter
      ? (contract.roomName || "").toLowerCase().includes(roomNameFilter.toLowerCase())
      : true;
    
    const matchesStatusFilter = statusFilter
      ? contract.contractStatus === statusFilter
      : true;

    return matchesSearchQuery && matchesRoomFilter && matchesStatusFilter;
  });

  // Lấy danh sách tên phòng duy nhất từ mảng contracts để hiển thị dropdown
  

  // Các hàm xử lý khác (delete, edit, create, print, invoice...) giữ nguyên như cũ

  const handleDelete = async (id: string) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa hợp đồng này?")) return;
    try {
      message.info("Đang xóa hợp đồng...");
      const response = await fetch(`http://localhost:8080/api/v1/contracts/delete/${id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        throw new Error(`Xóa hợp đồng thất bại! (Mã lỗi: ${response.status})`);
      }
      setContracts(contracts.filter((c) => c.contractId !== id));
      message.success("Hợp đồng đã được xóa thành công!");
    } catch (error) {
      console.error("Lỗi khi xóa hợp đồng:", error);
      message.error("Xóa hợp đồng thất bại! Vui lòng thử lại sau.");
    }
  };

  const handleEdit = (contract: Contract) => {
    message.info("Mở form chỉnh sửa hợp đồng...");
    setCurrentContract(contract);
    setFormData(contract);
    setIsEditing(true);
    setIsCreating(false);
  };

  const handleCreate = () => {
    message.info("Mở form tạo hợp đồng mới...");
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
      roomName: "",
      customerName: "",
    });
    setIsCreating(true);
    setIsEditing(false);
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const updateContractApi = async (contract: Contract) => {
    try {
      message.info("Đang cập nhật hợp đồng...");
      const response = await fetch(`http://localhost:8080/api/v1/contracts/update/${contract.contractId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(contract),
      });
      const result = await response.json();
      if (!response.ok) {
        throw new Error(`Lỗi: ${result.message || response.statusText}`);
      }
      return true;
    } catch (error) {
      console.error("Lỗi khi cập nhật hợp đồng:", error);
      message.error("Cập nhật hợp đồng thất bại!");
      return false;
    }
  };

  const createContractApi = async (contractData: Contract) => {
    try {
      message.info("Đang tạo hợp đồng mới...");
      const response = await fetch("http://localhost:8080/api/v1/contracts/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(contractData),
      });
      const result = await response.json();
      if (!response.ok) {
        throw new Error(`Lỗi: ${result.message || response.statusText}`);
      }
      return true;
    } catch (error) {
      console.error("Lỗi khi thêm hợp đồng:", error);
      message.error("Tạo hợp đồng mới thất bại!");
      return false;
    }
  };

  const handleSave = async () => {
    if (isEditing && currentContract) {
      const updatedContracts = contracts.map((c) =>
        c.contractId === currentContract.contractId ? formData : c
      );
      const isUpdated = await updateContractApi(formData);
      if (!isUpdated) {
        alert("❌ Cập nhật hợp đồng thất bại! Vui lòng thử lại sau.");
        return;
      }
      message.success("Cập nhật hợp đồng thành công!");
      setContracts(updatedContracts);
      setIsEditing(false);
      setCurrentContract(null);
    } else if (isCreating) {
      const newContract = await createContractApi(formData);
      if (!newContract) {
        alert("❌ Tạo hợp đồng mới thất bại! Vui lòng thử lại sau.");
        return;
      }
      message.success("Tạo hợp đồng mới thành công!");
      setContracts([...contracts, formData]);
      setIsCreating(false);
    }
  };

  const handleCancel = () => {
    setIsEditing(false);
    setIsCreating(false);
    setCurrentContract(null);
    message.info("Đóng form hợp đồng.");
  };

  const handlePrint = (contract: Contract) => {
    message.info("Đang in hợp đồng...");
    const printContent = `
      <html>
        <head>
          <title>Hợp đồng ${contract.contractId}</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 20px; }
            .contract-container { max-width: 800px; margin: 0 auto; }
            h1 { text-align: center; }
            p { margin: 5px 0; }
          </style>
        </head>
        <body>
          <div class="contract-container">
            <h1>Hợp đồng thuê</h1>
            <p><strong>Mã hợp đồng:</strong> ${contract.contractId}</p>
            <p><strong>Mã khách hàng:</strong> ${contract.userId}</p>
            <p><strong>Mã phòng:</strong> ${contract.roomId}</p>
            <p><strong>Tên phòng:</strong> ${contract.roomName || "N/A"}</p>
            <p><strong>Tên khách hàng:</strong> ${contract.customerName || "N/A"}</p>
            <p><strong>Ngày bắt đầu:</strong> ${formatDate(contract.startDate)}</p>
            <p><strong>Ngày kết thúc:</strong> ${formatDate(contract.endDate)}</p>
            <p><strong>Giá thuê:</strong> ${contract.price.toLocaleString()} VNĐ</p>
            <p><strong>Thanh toán:</strong> ${getDepositStatusLabel(contract.depositStatus)}</p>
            <p><strong>Trạng thái hợp đồng:</strong> ${getContractStatusLabel(contract.contractStatus)}</p>
            <p><strong>Ghi chú:</strong> ${contract.note}</p>
          </div>
        </body>
      </html>
    `;
    const printWindow = window.open("", "_blank", "width=800,height=600");
    if (printWindow) {
      printWindow.document.open();
      printWindow.document.write(printContent);
      printWindow.document.close();
      printWindow.focus();
      printWindow.print();
    }
  };

  // Phần tạo hóa đơn
  const handleCreateInvoice = (contract: Contract) => {
    message.info("Mở form tạo hóa đơn...");
    invoiceForm.setFieldsValue({
      contractId: contract.contractId,
      sumPrice: contract.price,
    });
    setIsInvoiceModalOpen(true);
  };

  const handleInvoiceSubmit = async (values: any) => {
    try {
      console.log("Bắt đầu tạo hóa đơn...");
      message.info("Đang tạo hóa đơn...");
      const payload = {
        contractId: values.contractId,
        sumPrice: values.sumPrice,
        paymentDate: values.paymentDate,
        paymentMethod: values.paymentMethod,
        billStatus: values.billStatus,
        note: values.note || "",
      };
  
      const response = await fetch("http://localhost:8080/api/v1/bills/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });
      const result = await response.json();
      console.log("API Response:", result);
      if (result.code === 1000) {
        message.success("Tạo hóa đơn thành công!");
        setIsInvoiceModalOpen(false);
        invoiceForm.resetFields();
      } else {
        throw new Error("Lỗi khi tạo hóa đơn!");
      }
    } catch (error) {
      console.error("Lỗi:", error);
      message.error("Tạo hóa đơn thất bại!");
    }
  };
  
  const uniqueRoomNames = Array.from(
    new Set(contracts.map((c) => c.roomName ?? "").filter((name) => name !== "" && name !== "N/A"))
  );
  
  return (
    <div className="contract-management">
      <h2>Quản lý hợp đồng</h2>
      <div className="filters" style={{ marginBottom: 16, display: "flex", gap: "8px", flexWrap: "wrap" }}>
        {/* Ô tìm kiếm chung cho contractId, userId, customerName */}
        <Search 
          placeholder="Tìm kiếm hợp đồng..." 
          onChange={handleSearch} 
          style={{ width: 300 }} 
        />
        {/* Ô tìm kiếm theo tên phòng */}
        <Select
  placeholder="Tên phòng"
  allowClear
  style={{ width: 150 }}
  value={roomNameFilter}
  onChange={(value) => setRoomNameFilter(value)}
>
  <Option value="">Tất cả</Option>
  {uniqueRoomNames.map((name) => (
    <Option key={name} value={name}>
      {name}
    </Option>
  ))}
</Select>

        {/* Dropdown lọc theo trạng thái hợp đồng */}
        <Select 
          placeholder="Chọn trạng thái hợp đồng" 
          style={{ width: 200 }}
          value={statusFilter}
          onChange={handleStatusFilter}
          allowClear
        >
          <Option value="">Tất cả</Option>
          <Option value="Active">Đang hiệu lực</Option>
          <Option value="Inactive">Hết hiệu lực</Option>
        </Select>
      </div>

      <Button className="create-btn" onClick={handleCreate}>
        Tạo hợp đồng mới
      </Button>

      {/* Bảng danh sách hợp đồng */}
      <table>
        <thead>
          <tr>
            <th>STT</th>
            <th>Mã HĐ</th>
            <th>Mã KH</th>
            <th>Tên phòng</th>
            <th>Tên khách hàng</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Giá thuê</th>
            <th>Thanh toán</th>
            <th>Trạng thái</th>
            <th>Cập nhật gần nhất</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {filteredContracts.map((contract, index) => (
            <tr key={contract.contractId}>
              <td>{index + 1}</td>
              <td>{contract.contractId}</td>
              <td>{contract.userId}</td>
              <td>{contract.roomName || "N/A"}</td>
              <td>{contract.customerName || "N/A"}</td>
              <td>{formatDate(contract.startDate)}</td>
              <td>{formatDate(contract.endDate)}</td>
              <td>{contract.price?.toLocaleString()} VNĐ</td>
              <td>{getDepositStatusLabel(contract.depositStatus)}</td>
              <td>{getContractStatusLabel(contract.contractStatus)}</td>
              <td>{formatDate(contract.updatedAt)}</td>
              <td>
                <Button onClick={() => handleEdit(contract)} icon={<EditOutlined />}>
                  Sửa
                </Button>
                <Button onClick={() => handleDelete(contract.contractId)} danger icon={<DeleteOutlined />}>
                  Xóa
                </Button>
                <Button onClick={() => handlePrint(contract)} icon={<PrinterOutlined />}>
                  In hợp đồng
                </Button>
                <Button onClick={() => handleCreateInvoice(contract)}>
                  Tạo hóa đơn
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Form chỉnh sửa hoặc tạo mới hợp đồng */}
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
                disabled={isEditing}
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
            <Button type="primary" onClick={handleSave}>
              Lưu
            </Button>
            <Button onClick={handleCancel}>Hủy</Button>
          </div>
        </div>
      )}

      {/* Modal Tạo Hóa Đơn */}
      <Modal
        title="Tạo Hóa Đơn"
        open={isInvoiceModalOpen}
        onCancel={() => {
          setIsInvoiceModalOpen(false);
          invoiceForm.resetFields();
        }}
        footer={null}
      >
        <Form layout="vertical" form={invoiceForm} onFinish={handleInvoiceSubmit}>
          <Form.Item
            label="Mã hợp đồng"
            name="contractId"
            rules={[{ required: true, message: "Mã hợp đồng không được để trống" }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="Tổng tiền"
            name="sumPrice"
            rules={[{ required: true, message: "Tổng tiền không được để trống" }]}
          >
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="Ngày thanh toán"
            name="paymentDate"
            rules={[{ required: true, message: "Vui lòng chọn ngày thanh toán" }]}
          >
            <Input type="date" />
          </Form.Item>
          <Form.Item
            label="Phương thức thanh toán"
            name="paymentMethod"
            rules={[{ required: true, message: "Vui lòng chọn phương thức thanh toán" }]}
          >
            <Select>
              <Option value="BANK_TRANSFER">Chuyển khoản</Option>
              <Option value="CASH">Tiền mặt</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="Trạng thái"
            name="billStatus"
            rules={[{ required: true, message: "Vui lòng chọn trạng thái" }]}
          >
            <Select>
              <Option value="PAID">Đã thanh toán</Option>
              <Option value="UNPAID">Chưa thanh toán</Option>
            </Select>
          </Form.Item>
          <Form.Item label="Ghi chú" name="note">
            <Input.TextArea />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Xác nhận
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ContractManagement;
