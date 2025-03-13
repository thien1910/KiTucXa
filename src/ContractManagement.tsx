import React, { useState, useEffect } from 'react';
import './ContractManagement.css';

interface Contract {
  id: string;
  studentName: string;
  studentId: string;
  startDate: string;
  endDate: string;
  status: 'Pending' | 'Signed';
}

const ContractManagement: React.FC = () => {
  const [contracts, setContracts] = useState<Contract[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [newContract, setNewContract] = useState<Contract>({
    id: '',
    studentName: '',
    studentId: '',
    startDate: '',
    endDate: '',
    status: 'Pending',
  });

  useEffect(() => {
    // Fetch danh sách hợp đồng (giả lập)
    setContracts([
      { id: '001', studentName: 'Nguyen Van A', studentId: 'SV001', startDate: '2025-01-01', endDate: '2025-12-31', status: 'Signed' },
      { id: '002', studentName: 'Le Thi B', studentId: 'SV002', startDate: '2025-02-01', endDate: '2025-11-30', status: 'Pending' }
    ]);
  }, []);

  const handleCreateContract = () => {
    if (!newContract.studentName || !newContract.studentId || !newContract.startDate || !newContract.endDate) return;
    setContracts([...contracts, { ...newContract, id: (contracts.length + 1).toString() }]);
    setNewContract({ id: '', studentName: '', studentId: '', startDate: '', endDate: '', status: 'Pending' });
  };

  const handleSignContract = (id: string) => {
    setContracts(contracts.map(contract => contract.id === id ? { ...contract, status: 'Signed' } : contract));
  };

  const filteredContracts = contracts.filter(contract =>
    contract.id.includes(searchTerm) ||
    contract.studentName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    contract.studentId.includes(searchTerm)
  );

  return (
    <div className="contract-container">
      <h2>Quản lý Hợp đồng</h2>
      <input className="search-input" type="text" placeholder="Tìm kiếm hợp đồng..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
      <h3>Tạo hợp đồng mới</h3>
      <div className="contract-form">
        <input type="text" placeholder="Tên sinh viên" value={newContract.studentName} onChange={(e) => setNewContract({ ...newContract, studentName: e.target.value })} />
        <input type="text" placeholder="Mã sinh viên" value={newContract.studentId} onChange={(e) => setNewContract({ ...newContract, studentId: e.target.value })} />
        <input type="date" value={newContract.startDate} onChange={(e) => setNewContract({ ...newContract, startDate: e.target.value })} />
        <input type="date" value={newContract.endDate} onChange={(e) => setNewContract({ ...newContract, endDate: e.target.value })} />
        <button onClick={handleCreateContract}>Tạo hợp đồng</button>
      </div>
      <h3>Danh sách hợp đồng</h3>
      <table className="contract-table">
        <thead>
          <tr>
            <th>Mã HĐ</th>
            <th>Tên Sinh Viên</th>
            <th>Mã SV</th>
            <th>Ngày BĐ</th>
            <th>Ngày KT</th>
            <th>Trạng thái</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {filteredContracts.map(contract => (
            <tr key={contract.id} className={contract.status === 'Signed' ? 'signed' : 'pending'}>
              <td>{contract.id}</td>
              <td>{contract.studentName}</td>
              <td>{contract.studentId}</td>
              <td>{contract.startDate}</td>
              <td>{contract.endDate}</td>
              <td>{contract.status}</td>
              <td>
                {contract.status === 'Pending' && <button onClick={() => handleSignContract(contract.id)}>Ký hợp đồng</button>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ContractManagement;
