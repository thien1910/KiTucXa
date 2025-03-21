import React, { useState, useEffect } from "react";
import { Table, Input, Button, Modal, Form, Switch } from "antd";
import { EditOutlined, PlusOutlined } from "@ant-design/icons";
import "antd/dist/reset.css";
import "./AccountManagement.css";

interface Account {
  key: string;
  username: string;
  email: string;
  status: boolean;
}

const AccountManagement: React.FC = () => {
  const [accounts, setAccounts] = useState<Account[]>([
    { key: "1", username: "admin", email: "admin@example.com", status: true },
    { key: "2", username: "user1", email: "user1@example.com", status: false },
  ]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [editingAccount, setEditingAccount] = useState<Account | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    if (editingAccount) {
      form.setFieldsValue(editingAccount);
    } else {
      form.resetFields();
    }
  }, [editingAccount, form]);

  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const filteredAccounts = accounts.filter((acc) =>
    acc.username.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleEdit = (record: Account) => {
    setEditingAccount(record);
    setModalVisible(true);
  };

  const handleAdd = () => {
    setEditingAccount(null);
    setModalVisible(true);
  };

  const handleSave = (values: Omit<Account, "key">) => {
    if (editingAccount) {
      setAccounts((prev) =>
        prev.map((acc) => (acc.key === editingAccount.key ? { ...acc, ...values } : acc))
      );
    } else {
      const newAccount: Account = { key: Date.now().toString(), ...values };
      setAccounts((prev) => [...prev, newAccount]);
    }
    setModalVisible(false);
    setEditingAccount(null);
    form.resetFields();
  };

  const handleDisable = (key: string) => {
    setAccounts((prev) =>
      prev.map((acc) => (acc.key === key ? { ...acc, status: !acc.status } : acc))
    );
  };

  return (
    <div className="account-container">
      <h2>Quản lý tài khoản</h2>
      <Input
        placeholder="Tìm kiếm tài khoản"
        value={searchTerm}
        onChange={handleSearch}
        className="search-input"
      />
      <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd} className="add-button">
        Thêm tài khoản
      </Button>
      <Table
        dataSource={filteredAccounts}
        columns={[
          { title: "Tên đăng nhập", dataIndex: "username", key: "username" },
          { title: "Email", dataIndex: "email", key: "email" },
          {
            title: "Trạng thái",
            dataIndex: "status",
            key: "status",
            render: (status: boolean) => (status ? "Hoạt động" : "Vô hiệu hóa"),
          },
          {
            title: "Hành động",
            key: "action",
            render: (_: unknown, record: Account) => (
              <>
                <Button icon={<EditOutlined />} onClick={() => handleEdit(record)} className="edit-button" />
                <Button onClick={() => handleDisable(record.key)} className="disable-button">
                  {record.status ? "Vô hiệu hóa" : "Kích hoạt"}
                </Button>
              </>
            ),
          },
        ]}
        className="account-table"
      />
      <Modal
        title={editingAccount ? "Chỉnh sửa tài khoản" : "Thêm tài khoản"}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
      >
        <Form
          form={form}
          onFinish={handleSave}
          initialValues={editingAccount || { username: "", email: "", status: true }}
        >
          
          <Form.Item name="username" label="Tên đăng nhập" rules={[{ required: true, message: "Vui lòng nhập tên đăng nhập" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="email" label="Email" rules={[{ required: true, message: "Vui lòng nhập email" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="status" label="Trạng thái" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">Lưu</Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AccountManagement;
