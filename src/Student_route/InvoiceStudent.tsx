import React, { useState, useEffect } from "react";
import { Table, Input, Button } from "antd";
import "./InvoiceStudent.css";

const { Search } = Input;

interface Invoice {
  id: string;
  contractId: string;
  totalAmount: number;
  paymentDate: string;
  paymentMethod: string;
  status: "paid" | "unpaid";
  createdAt: string;
}

const token = localStorage.getItem("token");

const API_URL = `http://localhost:8080/api/v1/bills/user/${localStorage.getItem('userId')}`; // Thay bằng API thật

const InvoiceStudent: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState("");

  // Gọi API lấy danh sách hóa đơn
  useEffect(() => {
    const fetchInvoices = async () => {
      setLoading(true);
      try {
        const response = await fetch(
          `${API_URL}`,
          {
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log(response);
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");
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
  }, []);

  const handleSearch = (value: string) => {
    setSearchQuery(value.toLowerCase());
  };

  const filteredInvoices = invoices.filter(
    (invoice) =>
      invoice.id.toLowerCase().includes(searchQuery) ||
      invoice.contractId.toLowerCase().includes(searchQuery)
  );

  return (
    <div className="invoice-student">
      <h1>Quản lý Hóa đơn</h1>
      <Search
        placeholder="Nhập mã hóa đơn hoặc mã hợp đồng"
        onSearch={handleSearch}
        enterButton="Tìm kiếm"
        style={{ marginBottom: 16 }}
      />
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
        ]}
      />
    </div>
  );
};

export default InvoiceStudent;
