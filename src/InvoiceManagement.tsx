import React, { useState, useEffect } from "react";
import { Table, Input, Button, Form, Modal, Select, message } from "antd";
import "./InvoiceManagement.css";
import { EditOutlined, DeleteOutlined } from "@ant-design/icons";

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
}
const token = localStorage.getItem("token");

const InvoiceManagement: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [form] = Form.useForm();

  useEffect(() => {
    const fetchInvoices = async () => {
      setLoading(true);
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/bills/list",
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");
        console.log(response);
        const data = await response.json();

        // Chuyển đổi dữ liệu từ API sang định dạng phù hợp với UI
        const mappedInvoices: Invoice[] = data.map((item: any) => ({
          id: item.billId,
          contractId: item.contractId,
          totalAmount: item.sumPrice,
          paymentDate: item.paymentDate,
          paymentMethod: item.paymentMethod,
          status: item.billStatus === "PAID" ? "paid" : "unpaid",
          createdAt: item.createdAt.split("T")[0], // Lấy phần ngày từ timestamp
        }));

        setInvoices(mappedInvoices);
      } catch (error) {
        console.error("Lỗi:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchInvoices();
    fetchContracts();
  }, []);

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
        }
      );

      if (!response.ok) throw new Error("Lỗi khi lấy danh sách hợp đồng");

      const data = await response.json();
      // console.log("Dữ liệu hợp đồng từ API:", data);

      setContracts(
        data.map((contract: any) => ({
          id: contract.contractId,
          employeeName: contract.userId,
          employeeCode: "N/A",
          department: contract.roomId,
          contractType: contract.contractStatus,
          startDate: new Date(contract.startDate).toLocaleDateString(),
          endDate: new Date(contract.endDate).toLocaleDateString(),
          salary: contract.price,
          status: contract.depositStatus,
        }))
      );
    } catch (error) {
      console.error("Lỗi:", error);
    }
  };

  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const response = await fetch("http://localhost:8080/api/v1/bills/list", {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

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

  const handleSearch = (value: string) => {
    setSearchQuery(value.toLowerCase());
  };

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
        fetchInvoices(); // Gọi lại API để lấy dữ liệu mới nhất
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
        }
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
      note: "", // Nếu có ghi chú thì cập nhật thêm
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
        }
      );
  
      const result = await response.json();
      console.log("Kết quả từ API:", result);
  
      // Kiểm tra xem phản hồi có tồn tại và hợp lệ không
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
              : invoice
          )
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

  const filteredInvoices = invoices.filter(
    (invoice) =>
      invoice.id.toLowerCase().includes(searchQuery) ||
      invoice.contractId.toLowerCase().includes(searchQuery)
  );

  return (
    <div className="invoice-management">
      <h1>Quản lý Hóa đơn</h1>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: 16,
          alignItems: "center",
        }}
      >
        <Search
          placeholder="Nhập mã hóa đơn hoặc tên sinh viên"
          onSearch={handleSearch}
          enterButton="Tìm kiếm"
          style={{ marginBottom: 0 }}
        />
      </div>
      <Button
        type="primary"
        onClick={() => {
          setIsModalOpen(true);
          setIsEditMode(false);
        }}
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
            title: "Ngày thanh toán",
            dataIndex: "paymentDate",
            key: "paymentDate",
          },
          {
            title: "Phương thức thanh toán",
            dataIndex: "paymentMethod",
            key: "paymentMethod",
          },
          { title: "Tổng tiền", dataIndex: "totalAmount", key: "totalAmount" },
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
            title: "Hành động",
            key: "actions",
            render: (record: Invoice) => (
              <div style={{ display: "flex", gap: "8px" }}>
                <Button
                  className="edit-button"
                  icon={<EditOutlined />}
                  onClick={() => handleEditInvoice(record)}
                ></Button>
                <Button
                  danger
                  icon={<DeleteOutlined />}
                  onClick={() => handleDeleteInvoice(record.id)}
                ></Button>
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
              <Option value="BANK_TRANSFER">Chuyển khoản</Option>
              <Option value="CASH">Tiền mặt</Option>
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
