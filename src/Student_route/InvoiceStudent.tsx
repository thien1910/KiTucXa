import React, { useState, useEffect } from "react";
import { Table, Input, Button } from "antd";
import { useNavigate } from "react-router-dom";
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
const userId = localStorage.getItem("userId");
const API_URL = `http://localhost:8080/api/v1/bills/user/${userId}`;

const InvoiceStudent: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState("");
  const navigate = useNavigate();

  // Gọi API lấy danh sách hóa đơn
  useEffect(() => {
    const fetchInvoices = async () => {
      setLoading(true);
      try {
        const response = await fetch(API_URL, {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });
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
          createdAt: item.createdAt.split("T")[0],
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

  // Hàm tìm kiếm
  const handleSearch = (value: string) => {
    setSearchQuery(value.toLowerCase());
  };

  // Lọc theo searchQuery
  const filteredInvoices = invoices.filter(
    (invoice) =>
      invoice.id.toLowerCase().includes(searchQuery) ||
      invoice.contractId.toLowerCase().includes(searchQuery)
  );

  // Khi bấm nút "Thanh toán", chuyển sang trang PaymentPage
  const goToPaymentPage = (invoice: Invoice) => {
    navigate("/student/payment", { state: { invoice } });
  };

  // Hàm in hóa đơn với giao diện tương tự in hợp đồng
  const handlePrintInvoice = (invoice: Invoice) => {
    const newWindow = window.open("", "_blank", "width=800,height=600");
    if (!newWindow) return;

    const invoiceHtml = `
      <html>
        <head>
          <title>Hóa đơn - ${invoice.id}</title>
          <style>
            body {
              font-family: Arial, sans-serif;
              margin: 20px;
            }
            h1, h2, h3 {
              text-align: center;
            }
            .invoice-info {
              margin: 20px 0;
            }
            .label {
              font-weight: bold;
            }
            .field {
              margin-bottom: 10px;
            }
          </style>
        </head>
        <body>
          <h1>HÓA ĐƠN THANH TOÁN</h1>
          <h3>Mã hóa đơn: ${invoice.id}</h3>
          <div class="invoice-info">
            <div class="field">
              <span class="label">Mã hợp đồng:</span> ${invoice.contractId}
            </div>
            <div class="field">
              <span class="label">Ngày thanh toán:</span> ${invoice.paymentDate}
            </div>
            <div class="field">
              <span class="label">Phương thức thanh toán:</span> ${invoice.paymentMethod}
            </div>
            <div class="field">
              <span class="label">Tổng tiền:</span> ${invoice.totalAmount.toLocaleString()} VND
            </div>
            <div class="field">
              <span class="label">Trạng thái:</span> ${invoice.status === "paid" ? "Đã thanh toán" : "Chưa thanh toán"}
            </div>
            <div class="field">
              <span class="label">Ngày tạo:</span> ${invoice.createdAt}
            </div>
          </div>
          <p>Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi.</p>
          <script>
            window.print();
          </script>
        </body>
      </html>
    `;

    newWindow.document.open();
    newWindow.document.write(invoiceHtml);
    newWindow.document.close();
  };

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
          {
            title: "Tổng tiền",
            dataIndex: "totalAmount",
            key: "totalAmount",
            render: (amount) => `${amount.toLocaleString()} VND`,
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
            title: "Thao tác",
            key: "action",
            render: (_, record: Invoice) => (
              <>
                {record.status === "unpaid" ? (
                  <>
                    <Button type="primary" onClick={() => goToPaymentPage(record)}>
                      Thanh toán
                    </Button>
                    <Button
                      style={{ marginLeft: 8 }}
                      onClick={() => handlePrintInvoice(record)}
                    >
                      In hóa đơn
                    </Button>
                  </>
                ) : (
                  <>
                    <span style={{ color: "green", fontWeight: "bold" }}>
                      Đã thanh toán
                    </span>
                    <Button
                      style={{ marginLeft: 8 }}
                      onClick={() => handlePrintInvoice(record)}
                    >
                      In hóa đơn
                    </Button>
                  </>
                )}
              </>
            ),
          },
        ]}
      />
    </div>
  );
};

export default InvoiceStudent;
