import React, { useState, useEffect } from "react";
import QRCode from "react-qr-code";
import "./PaymentPageStudent.css";

interface Bill {
  bill_id: string;
  contract_id: string;
  sum_price: number;
  payment_date: string;
  payment_method: string;
  status: string;
  notes: string;
}

const PaymentPage: React.FC = () => {
  const [bills, setBills] = useState<Bill[]>([]);
  const [selectedBill, setSelectedBill] = useState<Bill | null>(null);
  const [paymentMethod, setPaymentMethod] = useState("");
  const [qrValue, setQrValue] = useState("");
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    fetchBills();
  }, []);

  useEffect(() => {
    if (selectedBill && paymentMethod) {
      setQrValue(
        `bill_id:${selectedBill.bill_id},sum_price:${selectedBill.sum_price},payment_method:${paymentMethod}`,
      );
    }
  }, [selectedBill, paymentMethod]);

  // 🟢 **Hàm lấy danh sách hóa đơn từ API (Chỉ lấy theo userID)**
  const fetchBills = async () => {
    setErrorMessage("");
    try {
      const token = localStorage.getItem("token");
      const userId = localStorage.getItem("userId");

      if (!token || !userId) {
        throw new Error("Bạn chưa đăng nhập. Vui lòng đăng nhập lại!");
      }

      const response = await fetch(
        `http://localhost:8080/api/v1/bills/user/${userId}`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (!response.ok) {
        throw new Error(
          `Lỗi ${response.status}: Không thể tải danh sách hóa đơn.`,
        );
      }

      const data = await response.json();

      // 🟢 **Chuẩn hóa dữ liệu**
      const formattedData: Bill[] = data.map((bill: any) => ({
        bill_id: bill.billId || "N/A",
        contract_id: bill.contractId || "N/A",
        sum_price: bill.sumPrice || 0,
        payment_date: bill.paymentDate || "Chưa xác định",
        payment_method: bill.paymentMethod || "Chưa xác định",
        status: bill.billStatus || "Chưa xác định",
        notes: bill.note || "",
      }));

      setBills(formattedData);
    } catch (error: any) {
      console.error("Lỗi tải danh sách hóa đơn:", error);
      setErrorMessage(error.message);
    }
  };

  // 🟢 **Hàm chọn hóa đơn để thanh toán**
  const handlePayment = (bill: Bill) => {
    setSelectedBill(bill);
  };

  // 🟢 **Hàm xử lý thanh toán**
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!selectedBill || !paymentMethod) {
      alert("Vui lòng chọn hóa đơn và phương thức thanh toán.");
      return;
    }

    setLoading(true);

    const paymentData = {
      billId: selectedBill.bill_id,
      paymentDate: new Date().toISOString().split("T")[0],
      amount: selectedBill.sum_price,
      paymentMethod: paymentMethod.toUpperCase(),
    };

    try {
      // 🔹 **Gửi yêu cầu thanh toán**
      const response = await fetch("http://localhost:8080/api/v1/Payment/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify(paymentData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        alert(`Lỗi thanh toán: ${errorData.message}`);
        return;
      }

      // 🔹 **Cập nhật trạng thái hóa đơn**
      const updateBillData = { billStatus: "Đã thanh toán" };

      const updateResponse = await fetch(
        `http://localhost:8080/api/v1/bills/update/${selectedBill.bill_id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
          body: JSON.stringify(updateBillData),
        },
      );

      if (updateResponse.ok) {
        alert("Thanh toán thành công! Hóa đơn đã được cập nhật.");
        setSelectedBill(null);
        setPaymentMethod("");
        fetchBills(); // Reload danh sách hóa đơn
      } else {
        alert(
          "Thanh toán thành công nhưng không thể cập nhật trạng thái hóa đơn.",
        );
      }
    } catch (error) {
      console.error("Lỗi khi thanh toán:", error);
      alert("Có lỗi xảy ra khi thanh toán.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="payment-page">
      <h1>Danh Sách Hóa Đơn</h1>

      {errorMessage && <p className="error-message">{errorMessage}</p>}

      <div className="bill-list">
        {bills.length > 0 ? (
          bills.map((bill) => (
            <div key={bill.bill_id} className="bill-item">
              <div className="bill-details">
                <p>
                  <strong>Mã hóa đơn:</strong> {bill.bill_id}
                </p>
                <p>
                  <strong>Tổng tiền:</strong> {bill.sum_price.toLocaleString()}{" "}
                  VND
                </p>
                <p>
                  <strong>Ngày thanh toán:</strong> {bill.payment_date}
                </p>
                <p>
                  <strong>Phương thức thanh toán:</strong> {bill.payment_method}
                </p>
                <p>
                  <strong>Trạng thái:</strong> {bill.status}
                </p>
                <p>
                  <strong>Ghi chú:</strong> {bill.notes}
                </p>
              </div>
              {bill.status !== "Đã thanh toán" && (
                <button
                  className="pay-button"
                  onClick={() => handlePayment(bill)}
                >
                  Thanh toán
                </button>
              )}
            </div>
          ))
        ) : (
          <p>Không có hóa đơn nào.</p>
        )}
      </div>

      {selectedBill && (
        <form className="payment-form" onSubmit={handleSubmit}>
          <h2>Thanh toán hóa đơn</h2>
          <div className="form-group">
            <label>Mã hóa đơn:</label>
            <p>{selectedBill.bill_id}</p>
          </div>
          <div className="form-group">
            <label>Tổng tiền:</label>
            <p>{selectedBill.sum_price.toLocaleString()} VND</p>
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
              <option value="credit_card">Thẻ tín dụng</option>
              <option value="momo">MoMo</option>
              <option value="zalo_pay">Zalo Pay</option>
              <option value="cash">Tiền mặt</option>
            </select>
          </div>

          {["bank_transfer", "credit_card", "momo", "zalo_pay"].includes(
            paymentMethod,
          ) && (
            <div className="form-group">
              <label>Mã QR:</label>
              <QRCode value={qrValue} />
            </div>
          )}

          <button type="submit" className="submit-button" disabled={loading}>
            {loading ? "Đang xử lý..." : "Thanh toán"}
          </button>
        </form>
      )}
    </div>
  );
};

export default PaymentPage;
