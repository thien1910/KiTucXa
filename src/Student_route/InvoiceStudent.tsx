import React, { useState, useEffect } from "react";
import { Table, Input, Button, Modal } from "antd";
import QRCode from "react-qr-code";
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

interface Contract {
  contractId: string;
  roomName: string;
  customerName: string;
}

const token = localStorage.getItem("token");
const userId = localStorage.getItem("userId");
const API_URL = `http://localhost:8080/api/v1/bills/user/${userId}`;

const InvoiceStudent: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState("");
  // State để quản lý modal thanh toán
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [qrValue, setQrValue] = useState("");

  useEffect(() => {
    fetchInvoices();
    fetchContracts();
  }, []);

  // Fetch hóa đơn của sinh viên
  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const response = await fetch(API_URL, {
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

  // Fetch hợp đồng của sinh viên và lấy thông tin roomName, customerName từ dữ liệu JSON mẫu
  const fetchContracts = async () => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/contracts/user/${userId}`,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );
      if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu hợp đồng");
      const data = await response.json();
      const mappedContracts: Contract[] = data.map((item: any) => ({
        contractId: item.contractId,
        roomName: item.room?.roomName || "N/A", // Lấy roomName từ đối tượng room
        customerName: item.user?.fullName || "N/A", // Lấy fullName từ đối tượng user
      }));
      setContracts(mappedContracts);
    } catch (error) {
      console.error("Lỗi:", error);
    }
  };

  const handleSearch = (value: string) => {
    setSearchQuery(value.toLowerCase());
  };

  const filteredInvoices = invoices.filter(
    (invoice) =>
      invoice.id.toLowerCase().includes(searchQuery) ||
      invoice.contractId.toLowerCase().includes(searchQuery),
  );

  const openPaymentModal = (invoice: Invoice) => {
    setSelectedInvoice(invoice);
    setPaymentMethod("");
    setQrValue("");
    setShowPaymentModal(true);
  };

  useEffect(() => {
    if (selectedInvoice && paymentMethod) {
      setQrValue(
        `bill_id:${selectedInvoice.id},sum_price:${selectedInvoice.totalAmount},payment_method:${paymentMethod}`,
      );
    }
  }, [selectedInvoice, paymentMethod]);

  // Hàm in hóa đơn, bổ sung tên phòng và tên khách hàng
  const handlePrintInvoice = (invoice: Invoice) => {
    const newWindow = window.open("", "_blank", "width=800,height=600");
    if (!newWindow) return;
    const relatedContract = contracts.find(
      (c) => c.contractId === invoice.contractId,
    );
    const invoiceHtml = `
      <html>
        <head>
          <title>Hóa đơn - ${invoice.id}</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 20px; }
            h1, h2, h3 { text-align: center; }
            .invoice-info { margin: 20px 0; }
            .label { font-weight: bold; }
            .field { margin-bottom: 10px; }
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
              <span class="label">Tên phòng:</span> ${relatedContract?.roomName || "N/A"}
            </div>
            <div class="field">
              <span class="label">Tên khách hàng:</span> ${relatedContract?.customerName || "N/A"}
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

  const handlePaymentSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedInvoice || !paymentMethod) {
      alert("Vui lòng chọn hóa đơn và phương thức thanh toán.");
      return;
    }

    const billUpdateData = {
      billStatus: "PAID",
      paymentMethod: paymentMethod.toUpperCase(),
      paymentDate: new Date().toISOString().split("T")[0],
    };

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/bills/update/${selectedInvoice.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(billUpdateData),
        },
      );

      if (!response.ok) {
        let errorData;
        try {
          errorData = await response.json();
        } catch (e) {
          errorData = { message: "Lỗi không xác định" };
        }
        alert(`Lỗi cập nhật hóa đơn: ${errorData.message}`);
        return;
      }

      try {
        await response.json();
      } catch (e) {
        // Nếu không có body JSON thì bỏ qua
      }

      setInvoices((prevInvoices) =>
        prevInvoices.map((inv) =>
          inv.id === selectedInvoice.id
            ? {
                ...inv,
                status: "paid",
                paymentMethod: paymentMethod.toUpperCase(),
                paymentDate: new Date().toISOString().split("T")[0],
              }
            : inv,
        ),
      );

      setShowPaymentModal(false);
      alert("Thanh toán thành công! Hóa đơn đã được cập nhật trên server.");
    } catch (error) {
      console.error("Lỗi khi cập nhật hóa đơn:", error);
      alert("Có lỗi xảy ra khi cập nhật hóa đơn.");
    }
  };

  return (
    <div className="invoice-student">
      <h1>Danh sách hóa đơn</h1>
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
                (c) => c.contractId === record.contractId,
              );
              return relatedContract?.roomName || "N/A";
            },
          },
          {
            title: "Ngày tạo",
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
                    <Button
                      type="primary"
                      onClick={() => openPaymentModal(record)}
                    >
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
                  <div style={{ display: "flex", alignItems: "center" }}>
                    <span style={{ color: "green", fontWeight: "bold" }}>
                      Đã thanh toán
                    </span>
                    <Button
                      style={{ marginLeft: 8 }}
                      onClick={() => handlePrintInvoice(record)}
                    >
                      In hóa đơn
                    </Button>
                  </div>
                )}
              </>
            ),
          },
        ]}
      />

      <Modal
        visible={showPaymentModal}
        title="Thanh toán hóa đơn"
        onCancel={() => setShowPaymentModal(false)}
        footer={null}
        className="modal-payment"
        width="60vw"
        bodyStyle={{ maxHeight: "80vh", overflowY: "auto" }}
      >
        {selectedInvoice && (
          <form onSubmit={handlePaymentSubmit}>
            <div className="form-group">
              <label>Mã hóa đơn:</label>
              <p>{selectedInvoice.id}</p>
            </div>
            <div className="form-group">
              <label>Tổng tiền:</label>
              <p>{selectedInvoice.totalAmount.toLocaleString()} VND</p>
            </div>
            <div className="form-group">
              <label htmlFor="paymentMethod">Phương thức thanh toán:</label>
              <select
                id="paymentMethod"
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
                required
              >
                <option value="">Chọn phương thức</option>
                <option value="bank_transfer">Chuyển khoản ngân hàng</option>
                <option value="cash">Tiền mặt</option>
              </select>
            </div>
            {paymentMethod === "bank_transfer" && (
              <div className="form-group">
                <label>Mã QR:</label>
                <QRCode value={qrValue} size={128} />
              </div>
            )}
          </form>
        )}
      </Modal>
    </div>
  );
};

export default InvoiceStudent;
