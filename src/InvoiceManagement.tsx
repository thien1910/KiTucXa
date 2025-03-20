import React, { useState } from "react";
import { Table, Input, Button, Modal, Form, InputNumber, Switch } from "antd";
import { saveAs } from 'file-saver';
import * as XLSX from 'xlsx';
import "./InvoiceManagement.css";

const { Search } = Input;

interface Invoice {
  id: string;
  studentId: string;
  studentName: string;
  roomNumber: string;
  period: string;
  charges: { description: string; amount: number }[];
  totalAmount: number;
  status: "paid" | "unpaid";
  createdAt: string;
}

const initialInvoices: Invoice[] = [
  {
    id: "HD001",
    studentId: "SV001",
    studentName: "Nguyễn Văn A",
    roomNumber: "101",
    period: "Tháng 3/2025",
    charges: [{ description: "Tiền phòng", amount: 500000 }],
    totalAmount: 500000,
    status: "unpaid",
    createdAt: "2025-03-10"
  },
  {
    id: "HD002",
    studentId: "SV002",
    studentName: "Trần Thị B",
    roomNumber: "102",
    period: "Tháng 3/2025",
    charges: [{ description: "Tiền phòng", amount: 550000 }],
    totalAmount: 550000,
    status: "paid",
    createdAt: "2025-03-10"
  }
];

const InvoiceManagement: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>(initialInvoices);
  const [searchQuery, setSearchQuery] = useState("");
  const [showModal, setShowModal] = useState(false);
  const [form] = Form.useForm();

  const handleSearch = (value: string) => {
    setSearchQuery(value.toLowerCase());
  };

  const filteredInvoices = invoices.filter(
    (invoice) =>
      invoice.id.toLowerCase().includes(searchQuery) ||
      invoice.studentName.toLowerCase().includes(searchQuery)
  );

  const handleSaveInvoice = (values: any) => {
    const newInvoice: Invoice = {
      id: values.id,
      studentId: values.studentId,
      studentName: values.studentName,
      roomNumber: values.roomNumber,
      period: values.period,
      charges: [{ description: "Tiền phòng", amount: values.totalAmount }],
      totalAmount: values.totalAmount,
      status: values.status ? "paid" : "unpaid",
      createdAt: new Date().toISOString().split("T")[0]
    };
    setInvoices([...invoices, newInvoice]);
    setShowModal(false);
    form.resetFields();
  };

  const exportToExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(invoices);
  
    // Set column widths
    const columnWidths = [
      { wch: 10 }, // id
      { wch: 10 }, // studentId
      { wch: 20 }, // studentName
      { wch: 10 }, // roomNumber
      { wch: 15 }, // period
      { wch: 30 }, // charges
      { wch: 15 }, // totalAmount
      { wch: 10 }, // status
      { wch: 15 }  // createdAt
    ];
    worksheet['!cols'] = columnWidths;
  
    // Apply styles to header row
    const headerStyle = {
      font: { bold: true },
      alignment: { horizontal: 'center', vertical: 'center' },
      fill: { fgColor: { rgb: 'FFFF00' } }
    };
  
    if (worksheet['!ref']) {
      const range = XLSX.utils.decode_range(worksheet['!ref']);
      for (let C = range.s.c; C <= range.e.c; ++C) {
        const cellAddress = XLSX.utils.encode_cell({ c: C, r: 0 });
        if (!worksheet[cellAddress]) continue;
        worksheet[cellAddress].s = headerStyle;
      }
    }
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Invoices");
    const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(data, "invoices.xlsx");
  };

  return (
    <div className="invoice-management">
      <h1>Quản lý Hóa đơn</h1>
      <Button type="primary" className="create-btn" onClick={() => setShowModal(true)}>
        Tạo hóa đơn mới
      </Button>
      <Button type="default" className="export-btn" onClick={exportToExcel} style={{ marginLeft: 8 }}>
        Xuất Excel
      </Button>
      <Search
        placeholder="Nhập mã hóa đơn hoặc tên sinh viên"
        onSearch={handleSearch}
        enterButton="Tìm kiếm"
        style={{ marginBottom: 16 }}
      />
      <Table
        dataSource={filteredInvoices}
        rowKey="id"
        columns={[
          { title: "Mã hóa đơn", dataIndex: "id", key: "id" },
          { title: "Sinh viên", dataIndex: "studentName", key: "studentName" },
          { title: "Phòng", dataIndex: "roomNumber", key: "roomNumber" },
          { title: "Kỳ thanh toán", dataIndex: "period", key: "period" },
          { title: "Tổng tiền", dataIndex: "totalAmount", key: "totalAmount" },
          {
            title: "Trạng thái",
            dataIndex: "status",
            key: "status",
            render: (status) => (
              <span className={status === "paid" ? "paid" : "unpaid"}>
                {status === "paid" ? "Đã thanh toán" : "Chưa thanh toán"}
              </span>
            )
          }
        ]}
      />
      <Modal
        title="Tạo hóa đơn mới"
        open={showModal}
        onCancel={() => setShowModal(false)}
        footer={null}
      >
        <Form form={form} onFinish={handleSaveInvoice} layout="vertical">
          <Form.Item name="id" label="Mã hóa đơn" rules={[{ required: true, message: "Vui lòng nhập mã hóa đơn" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="studentId" label="Mã sinh viên" rules={[{ required: true, message: "Vui lòng nhập mã sinh viên" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="studentName" label="Tên sinh viên" rules={[{ required: true, message: "Vui lòng nhập tên sinh viên" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="roomNumber" label="Số phòng" rules={[{ required: true, message: "Vui lòng nhập số phòng" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="period" label="Kỳ thanh toán" rules={[{ required: true, message: "Vui lòng nhập kỳ thanh toán" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="totalAmount" label="Tổng tiền" rules={[{ required: true, message: "Vui lòng nhập tổng tiền" }]}>
            <InputNumber style={{ width: "100%" }} />
          </Form.Item>
          <Form.Item name="status" label="Trạng thái" valuePropName="checked">
            <Switch checkedChildren="Đã thanh toán" unCheckedChildren="Chưa thanh toán" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">Lưu</Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default InvoiceManagement;