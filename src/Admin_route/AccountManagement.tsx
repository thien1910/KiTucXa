"use client";

import type React from "react";
import { useState, useEffect } from "react";
import {
  Table,
  Input,
  Button,
  Modal,
  Form,
  Select,
  Popconfirm,
  message,
} from "antd";
import { EditOutlined, PlusOutlined, DeleteOutlined } from "@ant-design/icons";
import "antd/dist/reset.css";
import "./AccountManagement.css";

interface Account {
  userId: string;
  userName: string;
  passWord: string;
  maSV: string | null;
  fullName: string | null;
  gender: "MALE" | "FEMALE" | null;
  roomNameStudent: string | null;
  cccd: string | null;
  phoneNumber: string | null;
  // Đã cập nhật để bao gồm trạng thái Disabled
  status: "Staying" | "Left" | "Disciplined" | "Disabled" | null;
  country: string | null;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

interface ApiResponse {
  code: number;
  result: Account[];
}

const AccountManagement: React.FC = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [editingAccount, setEditingAccount] = useState<Account | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [form] = Form.useForm();

  // Fetch accounts from API
  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/api/v1/user/list", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });
      const data: ApiResponse = await response.json();

      if (data.code === 1000) {
        setAccounts(data.result);
        console.log("Fetched accounts:", data.result); // Debug: Kiểm tra dữ liệu từ API
      } else {
        console.error("Failed to fetch accounts:", data);
      }
    } catch (error) {
      console.error("Error fetching accounts:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  // Set form values when editing an account
  useEffect(() => {
    if (editingAccount) {
      console.log("Editing account:", editingAccount); // Debug: Kiểm tra tài khoản đang chỉnh sửa
      form.setFieldsValue({
        ...editingAccount,
        fullName: editingAccount.fullName || "",
        gender: editingAccount.gender || undefined,
        roomNameStudent: editingAccount.roomNameStudent || "",
        cccd: editingAccount.cccd || "",
        phoneNumber: editingAccount.phoneNumber || "",
        status: editingAccount.status || undefined,
        country: editingAccount.country || "",
        maSV: editingAccount.maSV || "", // Đảm bảo maSV được set
      });
      console.log("Form values after setFieldsValue:", form.getFieldsValue()); // Debug: Kiểm tra giá trị form sau khi set
    } else {
      form.resetFields();
    }
  }, [editingAccount, form]);

  // Handle search
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const filteredAccounts = accounts.filter((acc) =>
    acc.userName.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  // Handle edit
  const handleEdit = (record: Account) => {
    setEditingAccount(record);
    setModalVisible(true);
  };

  // Handle add
  const handleAdd = () => {
    setEditingAccount(null);
    setModalVisible(true);
  };

  // Handle save (create or update)
  const handleSave = async (values: Partial<Account>) => {
    console.log("Form values before save:", values); // Debug: Kiểm tra giá trị form trước khi lưu

    try {
      const token = localStorage.getItem("token");
      const payload = {
        ...values,
        maSV: values.maSV || null, // Đảm bảo maSV được gửi lên
      };
      console.log("Payload before API call:", payload); // Debug: Kiểm tra payload trước khi gửi lên API

      if (!editingAccount) {
        // Create new account
        const response = await fetch("http://localhost:8080/api/v1/user/add", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(payload),
        });

        const data = await response.json();
        if (data.code === 1000) {
          message.success("Tạo tài khoản thành công");
          await fetchAccounts(); // Refresh the list
        } else {
          message.error("Tạo tài khoản thất bại");
        }
      } else {
        // Update existing account
        const response = await fetch(
          `http://localhost:8080/api/v1/user/${editingAccount.userId}`,
          {
            method: "PUT",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(payload),
          },
        );

        const data = await response.json();
        if (data.code === 1000) {
          message.success("Cập nhật tài khoản thành công");
          await fetchAccounts(); // Refresh the list
        } else {
          message.error("Cập nhật tài khoản thất bại");
        }
      }
    } catch (error) {
      console.error("Error saving account:", error);
      message.error("Đã xảy ra lỗi khi lưu tài khoản");
    }

    setModalVisible(false);
    setEditingAccount(null);
    form.resetFields();
  };

  // Handle delete
  const handleDelete = async (userId: string) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        `http://localhost:8080/api/v1/user/${userId}`,
        {
          method: "DELETE",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      // Log response trước khi parse JSON
      const text = await response.text();
      console.log("Response từ server:", text);

      // Nếu server trả về HTML, không parse JSON
      if (!response.ok) {
        throw new Error(`Lỗi API: ${response.status} - ${response.statusText}`);
      }

      // Nếu phản hồi không phải JSON hợp lệ
      let data;
      try {
        data = JSON.parse(text);
      } catch (error) {
        throw new Error("Lỗi parse JSON: Server không trả về JSON hợp lệ.");
      }

      // Xử lý dữ liệu JSON
      if (data.code === 1000) {
        message.success("Xóa tài khoản thành công");
        await fetchAccounts(); // Refresh danh sách
      } else {
        message.error("Xóa tài khoản thất bại");
      }
    } catch (error) {
      console.error("Error deleting account:", error);
      message.error("Đã xảy ra lỗi khi xóa tài khoản");
    }
  };

  // Handle change status từ dropdown
  const handleChangeStatus = async (record: Account, newStatus: string) => {
    try {
      const token = localStorage.getItem("token");
      const payload = { status: newStatus };
      const response = await fetch(
        `http://localhost:8080/api/v1/user/${record.userId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(payload),
        },
      );
      const data = await response.json();
      if (data.code === 1000) {
        message.success("Cập nhật trạng thái thành công");
        await fetchAccounts();
      } else {
        message.error("Cập nhật trạng thái thất bại");
      }
    } catch (error) {
      console.error("Error updating status:", error);
      message.error("Đã xảy ra lỗi khi cập nhật trạng thái");
    }
  };

  // Handle disable account (vô hiệu hóa tài khoản)
  // Hàm toggle cập nhật trạng thái dựa trên trạng thái "Disciplined"
  const handleToggleActivation = async (record: Account) => {
    if (record.status === "Disciplined") {
      // Nếu tài khoản đang bị kỷ luật, kích hoạt lại (chuyển về "Staying")
      await handleChangeStatus(record, "Staying");
    } else {
      // Ngược lại, chuyển sang "Disciplined" để vô hiệu hóa tài khoản
      await handleChangeStatus(record, "Disciplined");
    }
  };

  // Render status tag with color
  const getStatusTag = (status: string | null) => {
    let color = "";
    switch (status) {
      case "Staying":
        color = "#52c41a"; // green
        break;
      case "Left":
        color = "#faad14"; // orange
        break;
      case "Disciplined":
        color = "#f5222d"; // red
        break;
      case "Disabled":
        color = "#d9d9d9"; // gray
        break;
      default:
        color = "#d9d9d9"; // gray
        break;
    }
    return <span style={{ color }}>{status || "Chưa xác định"}</span>;
  };

  return (
    <div className="account-container">
      <h2>Quản lý tài khoản</h2>
      <Input
        placeholder="Tìm kiếm tài khoản bằng tên đăng nhập"
        value={searchTerm}
        onChange={handleSearch}
        className="search-input"
      />
      <Button
        type="primary"
        icon={<PlusOutlined />}
        onClick={handleAdd}
        className="add-button"
      >
        Thêm tài khoản
      </Button>
      <Table
        loading={loading}
        dataSource={filteredAccounts}
        rowKey="userId"
        columns={[
          { title: "ID", dataIndex: "userId", key: "userId" },
          { title: "Mã sinh viên", dataIndex: "maSV", key: "maSV" },
          { title: "Tên đăng nhập", dataIndex: "userName", key: "userName" },
          { title: "Họ và tên", dataIndex: "fullName", key: "fullName" },
          { title: "Giới tính", dataIndex: "gender", key: "gender" },
          // { title: "Phòng", dataIndex: "roomNameStudent", key: "roomNameStudent" },
          { title: "CCCD", dataIndex: "cccd", key: "cccd" },
          {
            title: "Số điện thoại",
            dataIndex: "phoneNumber",
            key: "phoneNumber",
          },
          { title: "Quốc gia", dataIndex: "country", key: "country" },
          {
            title: "Trạng thái",
            dataIndex: "status",
            key: "status",
            render: (status: string | null) => getStatusTag(status),
          },
          {
            title: "Vai trò",
            dataIndex: "roles",
            key: "roles",
            render: (roles: string[]) => roles.join(", "),
          },
          {
            title: "Hành động",
            key: "action",
            render: (_: unknown, record: Account) => (
              <>
                <Button
                  icon={<EditOutlined />}
                  onClick={() => handleEdit(record)}
                  className="edit-button"
                  style={{ marginRight: 8 }}
                />
                <Popconfirm
                  title="Bạn có chắc chắn muốn xóa tài khoản này?"
                  onConfirm={() => handleDelete(record.userId)}
                  okText="Xóa"
                  cancelText="Hủy"
                >
                  <Button danger icon={<DeleteOutlined />} />
                </Popconfirm>

                <Button
                  onClick={() => handleToggleActivation(record)}
                  style={{ marginLeft: 8 }}
                  type={record.status === "Disciplined" ? "primary" : "default"}
                  danger={record.status !== "Disciplined"}
                >
                  {record.status === "Disciplined"
                    ? "Kích hoạt"
                    : "Vô hiệu hóa"}
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
        <Form form={form} onFinish={handleSave} layout="vertical">
          <Form.Item
            name="userName"
            label="Tên đăng nhập"
            rules={[{ required: true, message: "Vui lòng nhập tên đăng nhập" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="passWord"
            label="Mật khẩu"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu" }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            name="maSV"
            label="Mã sinh viên"
            rules={[{ required: true, message: "Vui lòng nhập mã sinh viên" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item name="fullName" label="Họ và tên">
            <Input />
          </Form.Item>
          <Form.Item name="gender" label="Giới tính">
            <Select>
              <Select.Option value="MALE">Nam</Select.Option>
              <Select.Option value="FEMALE">Nữ</Select.Option>
            </Select>
          </Form.Item>
          {/* <Form.Item name="roomNameStudent" label="Phòng">
            <Input />
          </Form.Item> */}
          <Form.Item name="cccd" label="CCCD">
            <Input />
          </Form.Item>
          <Form.Item name="phoneNumber" label="Số điện thoại">
            <Input />
          </Form.Item>
          <Form.Item name="country" label="Quốc gia">
            <Input />
          </Form.Item>
          <Form.Item name="status" label="Trạng thái">
            <Select>
              <Select.Option value="Staying">Đang ở</Select.Option>
              <Select.Option value="Left">Đã rời đi</Select.Option>
              <Select.Option value="Disciplined">Kỷ luật</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="roles" label="Vai trò">
            <Select mode="multiple">
              <Select.Option value="STUDENT">Sinh viên</Select.Option>
              <Select.Option value="MANAGER">Quản lý</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">
              Lưu
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default AccountManagement;
