import React, { useEffect, useState } from "react";
import { Table, Input, Button, Modal, Form, message } from "antd";
import { EditOutlined, PlusOutlined, DeleteOutlined } from "@ant-design/icons";
import { Select } from "antd";
import "./ServiceManagement.css";

interface Service {
  key: string;
  serviceName: string;
  description: string;
  pricePerUnit: string;
  calculationUnit: string;
  status: string;
}

const getAuthToken = () => localStorage.getItem("token"); // Lấy token từ localStorage

const ServiceManagement: React.FC = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [editingService, setEditingService] = useState<Service | null>(null);
  const [form] = Form.useForm();

  // 🛠 Fetch danh sách dịch vụ từ API khi component mount (CÓ AUTHORIZE)
  useEffect(() => {
    const fetchServices = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/utility-services/list",
          {
            method: "GET",
            headers: {
              Authorization: `Bearer ${getAuthToken()}`, // Thêm token vào header
              "Content-Type": "application/json",
            },
          },
        );

        if (!response.ok) throw new Error("Lấy danh sách dịch vụ thất bại!");

        const data = await response.json();
        setServices(
          data.map((item: any) => ({ key: item.utilityServiceId, ...item })),
        );
      } catch (error) {
        console.error("Lỗi khi lấy danh sách dịch vụ:", error);
      }
    };

    fetchServices();
  }, []);

  const handleAdd = () => {
    setEditingService(null);
    setModalVisible(true);
    form.resetFields();
  };

  const handleEdit = (record: Service) => {
    setEditingService(record);
    setModalVisible(true);
    form.setFieldsValue(record);
  };

  // 🗑 Xóa dịch vụ có AUTHORIZATION
  const handleDelete = async (key: string) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/utility-services/delete/${key}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${getAuthToken()}`,
            "Content-Type": "application/json",
          },
        },
      );

      if (!response.ok) throw new Error("Xóa dịch vụ thất bại!");

      setServices((prev) => prev.filter((service) => service.key !== key));
      message.success("Xóa dịch vụ thành công!");
    } catch (error) {
      message.error("Lỗi khi xóa dịch vụ!");
      console.error(error);
    }
  };

  // 📌 Thêm / Cập nhật dịch vụ có AUTHORIZATION
  const handleSave = async (values: Omit<Service, "key">) => {
    try {
      const url = editingService
        ? `http://localhost:8080/api/v1/utility-services/update/${editingService.key}`
        : "http://localhost:8080/api/v1/utility-services/add";

      const method = editingService ? "PUT" : "POST";

      const response = await fetch(url, {
        method: method,
        headers: {
          Authorization: `Bearer ${getAuthToken()}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(values),
      });

      const data = await response.json(); // Nhận phản hồi từ API
      console.log("API Response:", data); // Debug phản hồi

      if (!response.ok) {
        throw new Error(data.message || "Thêm/Cập nhật dịch vụ thất bại!");
      }

      if (!editingService) {
        const newService: Service = {
          key: data.result.utilityServiceId,
          ...data.result,
        };
        setServices((prev) => [...prev, newService]);
      } else {
        setServices((prev) =>
          prev.map((service) =>
            service.key === editingService.key
              ? { ...service, ...values }
              : service,
          ),
        );
      }

      message.success(
        editingService ? "Cập nhật thành công!" : "Thêm thành công!",
      );
      setModalVisible(false);
      setEditingService(null);
      form.resetFields();
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : "Lỗi không xác định!";
      console.error("Lỗi khi lưu dịch vụ:", errorMessage);
      message.error(errorMessage);
    }
  };

  return (
    <div className="service-container">
      <h2>Quản lý dịch vụ</h2>

      <Button
        type="primary"
        icon={<PlusOutlined />}
        onClick={handleAdd}
        className="add-button"
      >
        Thêm dịch vụ
      </Button>
      <Table
        dataSource={services}
        className="service-table"
        columns={[
          {
            title: "Tên dịch vụ",
            dataIndex: "serviceName",
            key: "serviceName",
          },
          { title: "Mô tả", dataIndex: "description", key: "description" },
          {
            title: "Giá mỗi đơn vị",
            dataIndex: "pricePerUnit",
            key: "pricePerUnit",
          },
          {
            title: "Đơn vị tính",
            dataIndex: "calculationUnit",
            key: "calculationUnit",
          },
          { title: "Trạng thái", dataIndex: "status", key: "status" },
          {
            title: "Hành động",
            key: "action",
            render: (_: unknown, record: Service) => (
              <>
                <Button
                  icon={<EditOutlined />}
                  onClick={() => handleEdit(record)}
                  className="edit-button"
                />
                <Button
                  icon={<DeleteOutlined />}
                  onClick={() => handleDelete(record.key)}
                  className="delete-button"
                />
              </>
            ),
          },
        ]}
      />
      <Modal
        title={editingService ? "Chỉnh sửa dịch vụ" : "Thêm dịch vụ"}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
      >
        <Form form={form} onFinish={handleSave} layout="vertical">
          <Form.Item
            name="serviceName"
            label="Tên dịch vụ"
            rules={[{ required: true, message: "Vui lòng nhập tên dịch vụ" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="description"
            label="Mô tả"
            rules={[{ required: true, message: "Vui lòng nhập mô tả" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="pricePerUnit"
            label="Giá mỗi đơn vị"
            rules={[
              { required: true, message: "Vui lòng nhập giá" },
              {
                pattern: /^[1-9]\d*$/,
                message: "Giá phải là số nguyên dương",
              },
            ]}
          >
            <Input type="number" min="1" />
          </Form.Item>

          <Form.Item
            name="calculationUnit"
            label="Đơn vị tính"
            rules={[{ required: true, message: "Vui lòng nhập đơn vị" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="status"
            label="Trạng thái"
            rules={[{ required: true, message: "Vui lòng chọn trạng thái" }]}
            initialValue="Active" // Mặc định là "Active"
          >
            <Select>
              <Select.Option value="Active">Active</Select.Option>
              <Select.Option value="Inactive">Disable</Select.Option>
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

export default ServiceManagement;
