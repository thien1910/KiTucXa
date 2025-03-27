import React, { useState, useEffect } from "react";
import { Table, Input, Button, Form, Modal, Select, message } from "antd";
import {
  EditOutlined,
  DeleteOutlined,
  PrinterOutlined,
} from "@ant-design/icons";
import "./InvoiceManagement.css";

const { Search } = Input;
const { Option } = Select;

interface Invoice {
  id: string;
  contractId: string;
  totalAmount: number;
  paymentDate: string;
  paymentMethod: string;
  status: "paid" | "unpaid";
  createdAt: string;
}

interface Contract {
  id: string;
  employeeName: string;
  employeeCode: string;
  department: string;
  contractType: string;
  startDate: string;
  endDate: string;
  salary: number;
  status: string;
  roomName?: string;
  customerName?: string;
}

const token = localStorage.getItem("token");

const InvoiceManagement: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState("");
  // Các state lọc bổ sung với giá trị mặc định rỗng
  const [statusFilter, setStatusFilter] = useState("");
  const [paymentMethodFilter, setPaymentMethodFilter] = useState("");
  const [roomNameFilter, setRoomNameFilter] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [form] = Form.useForm();

  // Fetch hóa đơn và hợp đồng khi component khởi tạo
  useEffect(() => {
    fetchInvoices();
    fetchContracts();
  }, []);

  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/v1/bills/list", {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu hóa đơn");
      const data = await response.json();
      const mappedInvoices: Invoice[] = data.map((item: any) => ({
        id: item.billId,
        contractId: item.contractId,
        totalAmount: item.sumPrice,
        paymentDate: item.paymentDate,
        paymentMethod: item.paymentMethod,
        status: item.billStatus === "PAID" ? "paid" : "unpaid",
        createdAt: item.createdAt.split("T")[0],
      }));
      setInvoices(mappedInvoices);
    } catch (error) {
      console.error("Lỗi:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchContracts = async () => {
    try {
      const response = await fetch(
        "http://localhost:8080/api/v1/contracts/list",
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      if (!response.ok) throw new Error("Lỗi khi lấy danh sách hợp đồng");
      const data = await response.json();
      // Enrich hợp đồng với thông tin roomName và customerName
      const enrichedContracts = await Promise.all(
        data.map(async (contract: any) => {
          let roomName = "N/A";
          try {
            const roomRes = await fetch(
              `http://localhost:8080/api/v1/rooms/${contract.roomId}`,
              {
                headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${token}`,
                },
              },
            );
            if (roomRes.ok) {
              const roomData = await roomRes.json();
              roomName =
                roomData.result?.roomName || roomData.roomName || "N/A";
            }
          } catch (error) {
            console.error("Lỗi khi lấy thông tin phòng:", error);
          }
          let customerName = "N/A";
          try {
            const userRes = await fetch(
              `http://localhost:8080/api/v1/user/manager/${contract.userId}`,
              {
                headers: {
                  "Content-Type": "application/json",
                  Authorization: `Bearer ${token}`,
                },
              },
            );
            if (userRes.ok) {
              const userData = await userRes.json();
              customerName =
                userData.result?.fullName || userData.fullName || "N/A";
            }
          } catch (error) {
            console.error("Lỗi khi lấy thông tin khách hàng:", error);
          }
          return {
            id: contract.contractId,
            employeeName: contract.userId,
            employeeCode: "N/A",
            department: contract.roomId,
            contractType: contract.contractStatus,
            startDate: new Date(contract.startDate).toLocaleDateString(),
            endDate: new Date(contract.endDate).toLocaleDateString(),
            salary: contract.price,
            status: contract.depositStatus,
            roomName,
            customerName,
          };
        }),
      );
      setContracts(enrichedContracts);
    } catch (error) {
      console.error("Lỗi:", error);
    }
  };

  // Hàm tìm kiếm theo searchQuery
  const handleSearch = (value: string) => {
    setSearchQuery(value.toLowerCase());
  };

  // Lọc các hóa đơn theo nhiều tiêu chí
  const filteredInvoices = invoices.filter((invoice) => {
    const matchesInvoiceQuery =
      invoice.id.toLowerCase().includes(searchQuery) ||
      invoice.contractId.toLowerCase().includes(searchQuery);

    const matchesStatus = statusFilter ? invoice.status === statusFilter : true;

    const matchesPaymentMethod = paymentMethodFilter
      ? invoice.paymentMethod.toLowerCase() ===
        paymentMethodFilter.toLowerCase()
      : true;

    const relatedContract = contracts.find((c) => c.id === invoice.contractId);
    const matchesRoomName = roomNameFilter
      ? (relatedContract?.roomName || "N/A").toLowerCase() ===
        roomNameFilter.toLowerCase()
      : true;

    return (
      matchesInvoiceQuery &&
      matchesStatus &&
      matchesPaymentMethod &&
      matchesRoomName
    );
  });

  // Lấy danh sách tên phòng duy nhất từ mảng contracts
  const uniqueRoomNames = Array.from(
    new Set(
      contracts.map((c) => c.roomName).filter((name) => name && name !== "N/A"),
    ),
  );

  const handleAddInvoice = async (values: any) => {
    try {
      const response = await fetch("http://localhost:8080/api/v1/bills/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          contractId: values.contractId,
          sumPrice: values.sumPrice,
          paymentDate: values.paymentDate,
          paymentMethod: values.paymentMethod,
          billStatus: values.billStatus,
          note: values.note,
        }),
      });
      const result = await response.json();
      if (result.code === 1000) {
        message.success("Thêm hóa đơn thành công!");
        fetchInvoices();
        setIsModalOpen(false);
        form.resetFields();
      } else {
        throw new Error("Lỗi khi thêm hóa đơn!");
      }
    } catch (error) {
      message.error("Thêm hóa đơn thất bại!");
      console.error("Lỗi:", error);
    }
  };

  const handleDeleteInvoice = async (id: string) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/bills/delete/${id}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      if (!response.ok) throw new Error("Lỗi khi xóa hóa đơn!");
      message.success("Xóa hóa đơn thành công!");
      setInvoices((prev) => prev.filter((invoice) => invoice.id !== id));
    } catch (error) {
      message.error("Xóa hóa đơn thất bại!");
      console.error("Lỗi:", error);
    }
  };

  const handleEditInvoice = (invoice: Invoice) => {
    setSelectedInvoice(invoice);
    setIsEditMode(true);
    setIsModalOpen(true);
    form.setFieldsValue({
      contractId: invoice.contractId,
      sumPrice: invoice.totalAmount,
      paymentDate: invoice.paymentDate,
      paymentMethod: invoice.paymentMethod,
      billStatus: invoice.status === "paid" ? "PAID" : "UNPAID",
      note: "",
    });
  };

  const handleUpdateInvoice = async (values: any) => {
    if (!selectedInvoice) return;
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/bills/update/${selectedInvoice.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            contractId: values.contractId,
            sumPrice: values.sumPrice,
            paymentDate: values.paymentDate,
            paymentMethod: values.paymentMethod,
            billStatus: values.billStatus,
            note: values.note,
          }),
        },
      );
      const result = await response.json();
      if (response.ok && result.billId) {
        message.success("Cập nhật hóa đơn thành công!");
        setInvoices((prev) =>
          prev.map((invoice) =>
            invoice.id === selectedInvoice.id
              ? {
                  ...invoice,
                  contractId: values.contractId,
                  totalAmount: values.sumPrice,
                  paymentDate: values.paymentDate,
                  paymentMethod: values.paymentMethod,
                  status: values.billStatus === "PAID" ? "paid" : "unpaid",
                }
              : invoice,
          ),
        );
        setIsModalOpen(false);
        form.resetFields();
        setSelectedInvoice(null);
        setIsEditMode(false);
      } else {
        throw new Error("Lỗi khi cập nhật hóa đơn!");
      }
    } catch (error) {
      message.error("Cập nhật hóa đơn thất bại!");
      console.error("Lỗi:", error);
    }
  };

  const handlePrintInvoice = (invoice: Invoice) => {
    const printContent = `
      <html>
        <head>
          <title>Hóa đơn ${invoice.id}</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 20px; }
            .invoice-container { max-width: 800px; margin: 0 auto; }
            h1 { text-align: center; }
            p { margin: 5px 0; }
          </style>
        </head>
        <body>
          <div class="invoice-container">
            <h1>Hóa đơn</h1>
            <p><strong>Mã hóa đơn:</strong> ${invoice.id}</p>
            <p><strong>Mã hợp đồng:</strong> ${invoice.contractId}</p>
            ${(() => {
              const relatedContract = contracts.find(
                (c) => c.id === invoice.contractId,
              );
              return `
                <p><strong>Tên phòng:</strong> ${relatedContract?.roomName || "N/A"}</p>
                <p><strong>Tên khách hàng:</strong> ${relatedContract?.customerName || "N/A"}</p>
              `;
            })()}
            <p><strong>Ngày thanh toán:</strong> ${invoice.paymentDate}</p>
            <p><strong>Phương thức thanh toán:</strong> ${invoice.paymentMethod}</p>
            <p><strong>Tổng tiền:</strong> ${invoice.totalAmount.toLocaleString()} VND</p>
            <p><strong>Trạng thái:</strong> ${invoice.status === "paid" ? "Đã thanh toán" : "Chưa thanh toán"}</p>
            <p><strong>Ngày tạo:</strong> ${invoice.createdAt}</p>
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

  return (
    <div className="invoice-management">
      <h1>Quản lý Hóa đơn</h1>
      <div
        className="search-filter"
        style={{
          marginBottom: 16,
          display: "flex",
          gap: "8px",
          flexWrap: "wrap",
        }}
      >
        <Search
          placeholder="Nhập mã hóa đơn hoặc mã hợp đồng"
          onSearch={handleSearch}
          enterButton="Tìm kiếm"
          style={{ width: 300 }}
        />
        <Select
          placeholder="Chọn trạng thái"
          allowClear
          style={{ width: 150 }}
          value={statusFilter}
          onChange={(value) => setStatusFilter(value)}
        >
          <Option value="">Tất cả</Option>
          <Option value="paid">Đã thanh toán</Option>
          <Option value="unpaid">Chưa thanh toán</Option>
        </Select>
        <Select
          placeholder="Phương thức thanh toán"
          allowClear
          style={{ width: 200 }}
          value={paymentMethodFilter}
          onChange={(value) => setPaymentMethodFilter(value)}
        >
          <Option value="">Tất cả</Option>
          <Option value="CASH">CASH</Option>
          <Option value="BANK_TRANSFER">BANK_TRANSFER</Option>
        </Select>
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
      </div>
      <Button
        type="primary"
        onClick={() => {
          setIsModalOpen(true);
          setIsEditMode(false);
        }}
        style={{ marginBottom: 16 }}
      >
        Thêm hóa đơn
      </Button>
      <Table
        dataSource={filteredInvoices}
        rowKey="id"
        loading={loading}
        columns={[
          { title: "Mã hóa đơn", dataIndex: "id", key: "id" },
          { title: "Mã hợp đồng", dataIndex: "contractId", key: "contractId" },
          {
            title: "Tên phòng",
            key: "roomName",
            render: (_: any, record: Invoice) => {
              const relatedContract = contracts.find(
                (c) => c.id === record.contractId,
              );
              return relatedContract?.roomName || "N/A";
            },
          },
          {
            title: "Trạng thái",
            dataIndex: "status",
            key: "status",
            render: (status) => (
              <span className={status === "paid" ? "paid" : "unpaid"}>
                {status === "paid" ? "Đã thanh toán" : "Chưa thanh toán"}
              </span>
            ),
          },
          {
            title: "Phương thức thanh toán",
            dataIndex: "paymentMethod",
            key: "paymentMethod",
          },
          {
            title: "Ngày thanh toán",
            dataIndex: "paymentDate",
            key: "paymentDate",
          },
          { title: "Tổng tiền", dataIndex: "totalAmount", key: "totalAmount" },
          {
            title: "Hành động",
            key: "actions",
            render: (record: Invoice) => (
              <div style={{ display: "flex", gap: "8px" }}>
                <Button
                  className="edit-button"
                  icon={<EditOutlined />}
                  onClick={() => handleEditInvoice(record)}
                />
                <Button
                  danger
                  icon={<DeleteOutlined />}
                  onClick={() => handleDeleteInvoice(record.id)}
                />
                <Button
                  icon={<PrinterOutlined />}
                  onClick={() => handlePrintInvoice(record)}
                />
              </div>
            ),
          },
        ]}
      />

      <Modal
        title={isEditMode ? "Sửa Hóa Đơn" : "Thêm Hóa Đơn"}
        open={isModalOpen}
        onCancel={() => {
          setIsModalOpen(false);
          setIsEditMode(false);
          form.resetFields();
        }}
        footer={null}
      >
        <Form
          layout="vertical"
          form={form}
          onFinish={isEditMode ? handleUpdateInvoice : handleAddInvoice}
        >
          <Form.Item
            label="Mã hợp đồng"
            name="contractId"
            rules={[{ required: true, message: "Vui lòng chọn mã hợp đồng!" }]}
          >
            <Select placeholder="Chọn mã hợp đồng">
              {contracts.map((contract) => (
                <Option key={contract.id} value={contract.id}>
                  {contract.id}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="Tổng tiền"
            name="sumPrice"
            rules={[{ required: true, message: "Vui lòng nhập tổng tiền!" }]}
          >
            <Input type="number" />
          </Form.Item>
          <Form.Item
            label="Ngày thanh toán"
            name="paymentDate"
            rules={[
              { required: true, message: "Vui lòng chọn ngày thanh toán!" },
            ]}
          >
            <Input type="date" />
          </Form.Item>
          <Form.Item
            label="Phương thức thanh toán"
            name="paymentMethod"
            rules={[
              {
                required: true,
                message: "Vui lòng chọn phương thức thanh toán!",
              },
            ]}
          >
            <Select>
              <Option value="BANK_TRANSFER">BANK_TRANSFER</Option>
              <Option value="CASH">CASH</Option>
            </Select>
          </Form.Item>
          <Form.Item
            label="Trạng thái"
            name="billStatus"
            rules={[{ required: true, message: "Vui lòng chọn trạng thái!" }]}
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
              {isEditMode ? "Cập nhật" : "Lưu"}
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default InvoiceManagement;
