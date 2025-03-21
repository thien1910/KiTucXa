// "use client"

// import type React from "react"
// import { useState, useEffect } from "react"
// import { Table, Input, Button, Modal, Form, Select } from "antd"
// import { EditOutlined, PlusOutlined } from "@ant-design/icons"
// import "antd/dist/reset.css"
// import "./AccountManagement.css"

// interface Account {
//   userId: string
//   userName: string
//   fullName: string | null
//   gender: "MALE" | "FEMALE" | null
//   roomNameStudent: string | null
//   cccd: string | null
//   phoneNumber: string | null
//   status: "Staying" | "Left" | "Disciplined" | null
//   country: string | null
//   roles: string[]
//   createdAt: string
//   updatedAt: string
// }

// interface ApiResponse {
//   code: number
//   result: Account[]
// }

// const AccountManagement: React.FC = () => {
//   const [accounts, setAccounts] = useState<Account[]>([])
//   const [searchTerm, setSearchTerm] = useState<string>("")
//   const [modalVisible, setModalVisible] = useState<boolean>(false)
//   const [editingAccount, setEditingAccount] = useState<Account | null>(null)
//   const [loading, setLoading] = useState<boolean>(true)
//   const [form] = Form.useForm()

//   const fetchAccounts = async () => {
//     try {
//       setLoading(true)
//       const token = localStorage.getItem('token');
//       // Replace with your actual API endpoint
//       const response = await fetch("http://localhost:8080/api/v1/user/list", {
//         method: "GET", // Hoặc "POST", "PUT", v.v. tùy vào API
//         headers: {
//           "Content-Type": "application/json",
//           Authorization: `Bearer ${token}`, // Thêm Bearer Token vào header
//         },
//       });
//       const data: ApiResponse = await response.json()

//       if (data.code === 1000) {
//         setAccounts(data.result)
//       } else {
//         console.error("Failed to fetch accounts:", data)
//       }
//     } catch (error) {
//       console.error("Error fetching accounts:", error)
//     } finally {
//       setLoading(false)
//     }
//   }

//   useEffect(() => {
//     fetchAccounts()
//   }, [])

//   useEffect(() => {
//     if (editingAccount) {
//       form.setFieldsValue({
//         ...editingAccount,
//         // Convert any null values to empty strings or appropriate defaults for form
//         fullName: editingAccount.fullName || "",
//         gender: editingAccount.gender || undefined,
//         roomNameStudent: editingAccount.roomNameStudent || "",
//         cccd: editingAccount.cccd || "",
//         phoneNumber: editingAccount.phoneNumber || "",
//         status: editingAccount.status || undefined,
//         country: editingAccount.country || "",
//       })
//     } else {
//       form.resetFields()
//     }
//   }, [editingAccount, form])

//   const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
//     setSearchTerm(e.target.value)
//   }

//   const filteredAccounts = accounts.filter((acc) => acc.userName.toLowerCase().includes(searchTerm.toLowerCase()))

//   const handleEdit = (record: Account) => {
//     setEditingAccount(record)
//     setModalVisible(true)
//   }

//   const handleAdd = () => {
//     setEditingAccount(null)
//     setModalVisible(true)
//   }

//   const handleSave = async (values: Partial<Account>) => {
//     try {
//       // For new account
//       if (!editingAccount) {
//         // Replace with your actual API endpoint for creating accounts
//         const response = await fetch("your-api-endpoint/create", {
//           method: "POST",
//           headers: {
//             "Content-Type": "application/json",
//           },
//           body: JSON.stringify(values),
//         })

//         const data = await response.json()
//         if (data.code === 1000) {
//           await fetchAccounts() // Refresh the list
//         }
//       } else {
//         // For updating existing account
//         // Replace with your actual API endpoint for updating accounts
//         const response = await fetch(`your-api-endpoint/update/${editingAccount.userId}`, {
//           method: "PUT",
//           headers: {
//             "Content-Type": "application/json",
//           },
//           body: JSON.stringify(values),
//         })

//         const data = await response.json()
//         if (data.code === 1000) {
//           await fetchAccounts() // Refresh the list
//         }
//       }
//     } catch (error) {
//       console.error("Error saving account:", error)
//     }

//     setModalVisible(false)
//     setEditingAccount(null)
//     form.resetFields()
//   }

//   const handleStatusChange = async (userId: string, newStatus: string) => {
//     try {
//       // Replace with your actual API endpoint for updating status
//       const response = await fetch(`your-api-endpoint/update-status/${userId}`, {
//         method: "PUT",
//         headers: {
//           "Content-Type": "application/json",
//         },
//         body: JSON.stringify({ status: newStatus }),
//       })

//       const data = await response.json()
//       if (data.code === 1000) {
//         await fetchAccounts() // Refresh the list
//       }
//     } catch (error) {
//       console.error("Error updating status:", error)
//     }
//   }

//   const getStatusTag = (status: string | null) => {
//     let color = ""
//     switch (status) {
//       case "Staying":
//         color = "#52c41a" // green
//         break
//       case "Left":
//         color = "#faad14" // orange
//         break
//       case "Disciplined":
//         color = "#f5222d" // red
//         break
//       default:
//         color = "#d9d9d9" // gray
//         break
//     }
//     return <span style={{ color }}>{status || "Chưa xác định"}</span>
//   }

//   return (
//     <div className="account-container">
//       <h2>Quản lý tài khoản</h2>
//       <Input placeholder="Tìm kiếm tài khoản" value={searchTerm} onChange={handleSearch} className="search-input" />
//       <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd} className="add-button">
//         Thêm tài khoản
//       </Button>
//       <Table
//         loading={loading}
//         dataSource={filteredAccounts}
//         rowKey="userId"
//         columns={[
//           { title: "Tên đăng nhập", dataIndex: "userName", key: "userName" },
//           { title: "Họ và tên", dataIndex: "fullName", key: "fullName" },
//           { title: "Phòng", dataIndex: "roomNameStudent", key: "roomNameStudent" },
//           { title: "Số điện thoại", dataIndex: "phoneNumber", key: "phoneNumber" },
//           {
//             title: "Trạng thái",
//             dataIndex: "status",
//             key: "status",
//             render: (status: string | null) => getStatusTag(status),
//           },
//           {
//             title: "Vai trò",
//             dataIndex: "roles",
//             key: "roles",
//             render: (roles: string[]) => roles.join(", "),
//           },
//           {
//             title: "Hành động",
//             key: "action",
//             render: (_: unknown, record: Account) => (
//               <>
//                 <Button
//                   icon={<EditOutlined />}
//                   onClick={() => handleEdit(record)}
//                   className="edit-button"
//                   style={{ marginRight: 8 }}
//                 />
//                 {record.status !== "Staying" ? (
//                   <Button
//                     onClick={() => handleStatusChange(record.userId, "Staying")}
//                     style={{ backgroundColor: "#52c41a", color: "white" }}
//                   >
//                     Kích hoạt
//                   </Button>
//                 ) : (
//                   <Select
//                     defaultValue="Chọn trạng thái"
//                     style={{ width: 140 }}
//                     onChange={(value) => handleStatusChange(record.userId, value)}
//                   >
//                     <Select.Option value="Left">Đã rời đi</Select.Option>
//                     <Select.Option value="Disciplined">Kỷ luật</Select.Option>
//                   </Select>
//                 )}
//               </>
//             ),
//           },
//         ]}
//         className="account-table"
//       />
//       <Modal
//         title={editingAccount ? "Chỉnh sửa tài khoản" : "Thêm tài khoản"}
//         open={modalVisible}
//         onCancel={() => setModalVisible(false)}
//         footer={null}
//       >
//         <Form form={form} onFinish={handleSave} layout="vertical">
//           <Form.Item
//             name="userName"
//             label="Tên đăng nhập"
//             rules={[{ required: true, message: "Vui lòng nhập tên đăng nhập" }]}
//           >
//             <Input />
//           </Form.Item>

//           <Form.Item name="fullName" label="Họ và tên">
//             <Input />
//           </Form.Item>

//           <Form.Item name="gender" label="Giới tính">
//             <Select>
//               <Select.Option value="MALE">Nam</Select.Option>
//               <Select.Option value="FEMALE">Nữ</Select.Option>
//             </Select>
//           </Form.Item>

//           <Form.Item name="roomNameStudent" label="Phòng">
//             <Input />
//           </Form.Item>

//           <Form.Item name="cccd" label="CCCD">
//             <Input />
//           </Form.Item>

//           <Form.Item name="phoneNumber" label="Số điện thoại">
//             <Input />
//           </Form.Item>

//           <Form.Item name="country" label="Quốc gia">
//             <Input />
//           </Form.Item>

//           <Form.Item name="status" label="Trạng thái">
//             <Select>
//               <Select.Option value="Staying">Đang ở</Select.Option>
//               <Select.Option value="Left">Đã rời đi</Select.Option>
//               <Select.Option value="Disciplined">Kỷ luật</Select.Option>
//             </Select>
//           </Form.Item>

//           <Form.Item name="roles" label="Vai trò">
//             <Select mode="multiple">
//               <Select.Option value="STUDENT">Sinh viên</Select.Option>
//               <Select.Option value="MANAGER">Quản lý</Select.Option>
//               <Select.Option value="GUEST">Khách</Select.Option>
//             </Select>
//           </Form.Item>

//           <Form.Item>
//             <Button type="primary" htmlType="submit">
//               Lưu
//             </Button>
//           </Form.Item>
//         </Form>
//       </Modal>
//     </div>
//   )
// }

// export default AccountManagement

"use client";

import type React from "react";
import { useState, useEffect } from "react";
import { Table, Input, Button, Modal, Form, Select, Popconfirm, message } from "antd";
import { EditOutlined, PlusOutlined, DeleteOutlined } from "@ant-design/icons";
import "antd/dist/reset.css";
import "./AccountManagement.css";

interface Account {
  userId: string;
  userName: string;
  passWord: string; // Thêm trường passWord
  fullName: string | null;
  gender: "MALE" | "FEMALE" | null;
  roomNameStudent: string | null;
  cccd: string | null;
  phoneNumber: string | null;
  status: "Staying" | "Left" | "Disciplined" | null;
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
      form.setFieldsValue({
        ...editingAccount,
        fullName: editingAccount.fullName || "",
        gender: editingAccount.gender || undefined,
        roomNameStudent: editingAccount.roomNameStudent || "",
        cccd: editingAccount.cccd || "",
        phoneNumber: editingAccount.phoneNumber || "",
        status: editingAccount.status || undefined,
        country: editingAccount.country || "",
      });
    } else {
      form.resetFields();
    }
  }, [editingAccount, form]);

  // Handle search
  const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
  };

  const filteredAccounts = accounts.filter((acc) =>
    acc.userName.toLowerCase().includes(searchTerm.toLowerCase())
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
    try {
      const token = localStorage.getItem("token");
      if (!editingAccount) {
        // Create new account
        const response = await fetch("http://localhost:8080/api/v1/user/add", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            userName: values.userName,
            passWord: values.passWord, // Thêm trường passWord
            fullName: values.fullName,
            gender: values.gender,
            roomNameStudent: values.roomNameStudent,
            cccd: values.cccd,
            phoneNumber: values.phoneNumber,
            status: values.status,
            country: values.country,
          }),
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
            body: JSON.stringify({
              userName: values.userName,
              passWord: values.passWord, // Thêm trường passWord
              fullName: values.fullName,
              gender: values.gender,
              roomNameStudent: values.roomNameStudent,
              cccd: values.cccd,
              phoneNumber: values.phoneNumber,
              status: values.status,
              country: values.country,
            }),
          }
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
      const response = await fetch(`http://localhost:8080/api/v1/user/${userId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      const data = await response.json();
      if (data.code === 1000) {
        message.success("Xóa tài khoản thành công");
        await fetchAccounts(); // Refresh the list
      } else {
        message.error("Xóa tài khoản thất bại");
      }
    } catch (error) {
      console.error("Error deleting account:", error);
      message.error("Đã xảy ra lỗi khi xóa tài khoản");
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
        placeholder="Tìm kiếm tài khoản"
        value={searchTerm}
        onChange={handleSearch}
        className="search-input"
      />
      <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd} className="add-button">
        Thêm tài khoản
      </Button>
      <Table
        loading={loading}
        dataSource={filteredAccounts}
        rowKey="userId"
        columns={[
          { title: "Tên đăng nhập", dataIndex: "userName", key: "userName" },
          { title: "Họ và tên", dataIndex: "fullName", key: "fullName" },
          { title: "Giới tính", dataIndex: "gender", key: "gender" },
          { title: "Phòng", dataIndex: "roomNameStudent", key: "roomNameStudent" },
          { title: "CCCD", dataIndex: "cccd", key: "cccd" },
          { title: "Số điện thoại", dataIndex: "phoneNumber", key: "phoneNumber" },
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
          <Form.Item name="fullName" label="Họ và tên">
            <Input />
          </Form.Item>
          <Form.Item name="gender" label="Giới tính">
            <Select>
              <Select.Option value="MALE">Nam</Select.Option>
              <Select.Option value="FEMALE">Nữ</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="roomNameStudent" label="Phòng">
            <Input />
          </Form.Item>
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
              <Select.Option value="GUEST">Khách</Select.Option>
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