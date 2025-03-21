import React, { useState, useEffect } from 'react';
import QRCode from 'react-qr-code';
import './PaymentPage.css';

interface Bill {
  bill_id: number;
  contract_id: number;
  sum_price: number;
  payment_date: string;
  payment_method: string;
  status: string;
  notes: string;
}

const PaymentPage: React.FC = () => {
  const [bills] = useState<Bill[]>([
    {
      bill_id: 1,
      contract_id: 101,
      sum_price: 500000,
      payment_date: '2025-03-01',
      payment_method: 'credit_card',
      status: 'paid',
      notes: 'Paid in full'
    },
    {
      bill_id: 2,
      contract_id: 102,
      sum_price: 300000,
      payment_date: '2025-03-05',
      payment_method: 'bank_transfer',
      status: 'unpaid',
      notes: 'Pending payment'
    }
  ]);
  const [selectedBill, setSelectedBill] = useState<Bill | null>(null);
  const [paymentMethod, setPaymentMethod] = useState('');
  const [qrValue, setQrValue] = useState('');
  const [recipientName, setRecipientName] = useState('');
  const [address, setAddress] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');

  useEffect(() => {
    if (selectedBill && paymentMethod) {
      setQrValue(`bill_id:${selectedBill.bill_id},sum_price:${selectedBill.sum_price},payment_method:${paymentMethod}`);
    }
  }, [selectedBill, paymentMethod]);

  const handlePayment = (bill: Bill) => {
    setSelectedBill(bill);
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // Handle payment submission
    console.log('Thanh toán hóa đơn:', selectedBill?.bill_id, paymentMethod);
    if (paymentMethod === 'cash_on_delivery') {
      console.log('Recipient Name:', recipientName);
      console.log('Address:', address);
      console.log('Phone Number:', phoneNumber);
    }
  };

  return (
    <div className="payment-page">
      <h1>Hóa đơn</h1>
      <div className="bill-list">
        {bills.map(bill => (
          <div key={bill.bill_id} className="bill-item">
            <div className="bill-details">
              <p><strong>Mã hóa đơn:</strong> {bill.bill_id}</p>
              <p><strong>Tổng tiền:</strong> {bill.sum_price.toLocaleString()} VND</p>
              <p><strong>Ngày thanh toán:</strong> {bill.payment_date}</p>
              <p><strong>Phương thức thanh toán:</strong> {bill.payment_method}</p>
              <p><strong>Trạng thái:</strong> {bill.status}</p>
              <p><strong>Ghi chú:</strong> {bill.notes}</p>
            </div>
            <button className="pay-button" onClick={() => handlePayment(bill)}>Thanh toán</button>
          </div>
        ))}
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
              onChange={e => setPaymentMethod(e.target.value)}
            >
              <option value="">Chọn phương thức</option>
              <option value="credit_card">Thẻ tín dụng</option>
              <option value="bank_transfer">Chuyển khoản ngân hàng</option>
              <option value="paypal">PayPal</option>
              <option value="momo">MoMo</option>
              <option value="zalo_pay">Zalo Pay</option>
              <option value="cash_on_delivery">Thanh toán khi nhận hàng</option>
            </select>
          </div>
          {paymentMethod === 'cash_on_delivery' && (
            <>
              <div className="form-group">
                <label htmlFor="recipientName">Tên người nhận:</label>
                <input
                  type="text"
                  id="recipientName"
                  value={recipientName}
                  onChange={e => setRecipientName(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="address">Địa chỉ:</label>
                <input
                  type="text"
                  id="address"
                  value={address}
                  onChange={e => setAddress(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="phoneNumber">Số điện thoại:</label>
                <input
                  type="text"
                  id="phoneNumber"
                  value={phoneNumber}
                  onChange={e => setPhoneNumber(e.target.value)}
                  required
                />
              </div>
            </>
          )}
          <div className="form-group">
            <label>Mã QR:</label>
            <QRCode value={qrValue} />
          </div>
          <button type="submit" className="submit-button">Thanh toán</button>
        </form>
      )}
    </div>
  );
};

export default PaymentPage;