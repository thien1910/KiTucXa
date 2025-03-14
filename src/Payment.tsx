import React, { useState } from "react";
import "./Payment.css";

interface Invoice {
  id: number;
  description: string;
  amount: number;
  status: "Chưa thanh toán" | "Đã thanh toán";
}

const Payment: React.FC = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([
    { id: 1, description: "Hóa đơn tháng 3", amount: 500000, status: "Chưa thanh toán" },
    { id: 2, description: "Hóa đơn tháng 2", amount: 480000, status: "Đã thanh toán" },
  ]);

  const handlePayment = (id: number) => {
    setInvoices((prev) =>
      prev.map((invoice) =>
        invoice.id === id ? { ...invoice, status: "Đã thanh toán" } : invoice
      )
    );
  };

  return (
    <div className="payment-container">
      <h2>Thanh toán hóa đơn</h2>
      <table className="payment-table">
        <thead>
          <tr>
            <th>Mô tả</th>
            <th>Số tiền</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {invoices.map((invoice) => (
            <tr key={invoice.id}>
              <td>{invoice.description}</td>
              <td>{invoice.amount.toLocaleString()} VND</td>
              <td className={invoice.status === "Chưa thanh toán" ? "unpaid" : "paid"}>
                {invoice.status}
              </td>
              <td>
                {invoice.status === "Chưa thanh toán" && (
                  <button onClick={() => handlePayment(invoice.id)}>Thanh toán</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Payment;
