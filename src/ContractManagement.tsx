import React, { useState } from 'react';
import './ContractManagement.css';

// Định nghĩa interface cho dữ liệu hợp đồng
interface Contract {
  id: number;
  employeeName: string;
  employeeCode: string;
  department: string;
  contractType: string;
  startDate: string;
  endDate: string;
  salary: number;
  status: string;
}

// Dữ liệu mẫu (giả lập)
const initialContracts: Contract[] = [
  {
    id: 1,
    employeeName: 'Lê Xuân Hải 31',
    employeeCode: 'N/A',
    department: 'KI TƯC XÁ BÌNH TÂN MY TRINH',
    contractType: 'Đã thuê',
    startDate: '25/04/2024',
    endDate: '25/04/2025',
    salary: 99999,
    status: 'Đã thuê',
  },
  {
    id: 2,
    employeeName: 'khach 111',
    employeeCode: 'Nguyen VA',
    department: 'N/A',
    contractType: 'Đã thuê',
    startDate: '25/04/2024',
    endDate: '25/04/2025',
    salary: 99999,
    status: 'Đã thuê',
  },
  // Thêm dữ liệu mẫu khác nếu cần
];

const ContractManagement: React.FC = () => {
  const [contracts, setContracts] = useState<Contract[]>(initialContracts);
  const [isEditing, setIsEditing] = useState<boolean>(false);
  const [currentContract, setCurrentContract] = useState<Contract | null>(null);
  const [isCreating, setIsCreating] = useState<boolean>(false);

  // State cho form chỉnh sửa và tạo mới
  const [formData, setFormData] = useState<Contract>({
    id: 0,
    employeeName: '',
    employeeCode: '',
    department: '',
    contractType: '',
    startDate: '',
    endDate: '',
    salary: 0,
    status: '',
  });

  // Hàm xóa hợp đồng
  const handleDelete = (id: number) => {
    const updatedContracts = contracts.filter((contract) => contract.id !== id);
    setContracts(updatedContracts);
  };

  // Hàm mở form chỉnh sửa
  const handleEdit = (contract: Contract) => {
    setCurrentContract(contract);
    setFormData(contract);
    setIsEditing(true);
    setIsCreating(false);
  };

  // Hàm mở form tạo mới
  const handleCreate = () => {
    setFormData({
      id: contracts.length + 1,
      employeeName: '',
      employeeCode: '',
      department: '',
      contractType: '',
      startDate: '',
      endDate: '',
      salary: 0,
      status: '',
    });
    setIsCreating(true);
    setIsEditing(false);
  };

  // Hàm xử lý thay đổi dữ liệu trong form
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Hàm lưu chỉnh sửa
  const handleSave = () => {
    if (isEditing && currentContract) {
      const updatedContracts = contracts.map((contract) =>
        contract.id === currentContract.id ? formData : contract
      );
      setContracts(updatedContracts);
      setIsEditing(false);
      setCurrentContract(null);
    } else if (isCreating) {
      setContracts([...contracts, formData]);
      setIsCreating(false);
    }
  };

  // Hàm đóng form
  const handleCancel = () => {
    setIsEditing(false);
    setIsCreating(false);
    setCurrentContract(null);
  };

  return (
    <div className="contract-management">
      <h2>Quản lý hợp đồng</h2>
      <div className="filters">
        <select defaultValue="10">
          <option value="10">Hiển thị 10</option>
          <option value="20">Hiển thị 20</option>
          <option value="50">Hiển thị 50</option>
        </select>
        <select>
          <option>Chọn trạng thái</option>
          <option>Đã thuê</option>
          <option>Chưa thuê</option>
        </select>
        <input type="text" placeholder="Tìm kiếm..." />
      </div>

      <button className="create-btn" onClick={handleCreate}>
        Tạo hợp đồng mới
      </button>

      {/* Bảng danh sách hợp đồng */}
      <table>
        <thead>
          <tr>
            <th>STT</th>
            <th>Tên KH</th>
            <th>Tên NV</th>
            <th>Tên phòng</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Tiền cọc</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {contracts.map((contract, index) => (
            <tr key={contract.id}>
              <td>{index + 1}</td>
              <td>{contract.employeeName}</td>
              <td>{contract.employeeCode}</td>
              <td>{contract.department}</td>
              <td>{contract.startDate}</td>
              <td>{contract.endDate}</td>
              <td>{contract.salary}</td>
              <td>{contract.status}</td>
              <td>
                <button className="edit-btn" onClick={() => handleEdit(contract)}>
                  Sửa
                </button>
                <button className="delete-btn" onClick={() => handleDelete(contract.id)}>
                  Xóa
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* Form chỉnh sửa hoặc tạo mới */}
      {(isEditing || isCreating) && (
        <div className="edit-form">
          <h3>{isEditing ? 'Chỉnh sửa hợp đồng' : 'Tạo hợp đồng mới'}</h3>
          <div className="form-group">
            <label>Tên KH:</label>
            <input
              type="text"
              name="employeeName"
              value={formData.employeeName}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Tên NV:</label>
            <input
              type="text"
              name="employeeCode"
              value={formData.employeeCode}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Tên phòng:</label>
            <input
              type="text"
              name="department"
              value={formData.department}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Ngày bắt đầu:</label>
            <input
              type="text"
              name="startDate"
              value={formData.startDate}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Ngày kết thúc:</label>
            <input
              type="text"
              name="endDate"
              value={formData.endDate}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Tiền cọc:</label>
            <input
              type="number"
              name="salary"
              value={formData.salary}
              onChange={handleInputChange}
            />
          </div>
          <div className="form-group">
            <label>Trạng thái:</label>
            <select name="status" value={formData.status} onChange={handleInputChange}>
              <option value="Đã thuê">Đã thuê</option>
              <option value="Chưa thuê">Chưa thuê</option>
            </select>
          </div>
          <div className="form-actions">
            <button onClick={handleSave}>Lưu</button>
            <button onClick={handleCancel}>Hủy</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ContractManagement;