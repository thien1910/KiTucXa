import React, { useState } from "react";
import { Table, Input, Button, Modal, Form } from "antd";
import { EditOutlined, PlusOutlined, DeleteOutlined } from "@ant-design/icons";
import "./ServiceManagement.css";

interface Service {
  key: string;
  serviceName: string;
  description: string;
  pricePerUnit: string;
  calculationUnit: string;
  status: string;
}

const ServiceManagement: React.FC = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [editingService, setEditingService] = useState<Service | null>(null);
  const [form] = Form.useForm();

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

  const handleDelete = (key: string) => {
    setServices((prev) => prev.filter((service) => service.key !== key));
  };

  const handleSave = async (values: Omit<Service, "key">) => {
    try {
      if (editingService) {
        setServices((prev) =>
          prev.map((service) =>
            service.key === editingService.key ? { ...service, ...values } : service
          )
        );
      } else {
        const response = await fetch("http://localhost:8080/api/v1/utility-services/add", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(values),
        });

        if (!response.ok) {
          throw new Error("Thêm dịch vụ thất bại!");
        }

        const data = await response.json();
        const newService: Service = { key: data.id, ...data };
        setServices((prev) => [...prev, newService]);
      }
      setModalVisible(false);
      setEditingService(null);
      form.resetFields();
    } catch (error) {
      console.error("Lỗi khi thêm dịch vụ:", error);
    }
  };

  return (
    <div className="service-container">
      <h2>Quản lý dịch vụ</h2>
      <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd} className="add-button">
        Thêm dịch vụ
      </Button>
      <Table
        dataSource={services}
        columns={[
          { title: "Tên dịch vụ", dataIndex: "serviceName", key: "serviceName" },
          { title: "Mô tả", dataIndex: "description", key: "description" },
          { title: "Giá mỗi đơn vị", dataIndex: "pricePerUnit", key: "pricePerUnit" },
          { title: "Đơn vị tính", dataIndex: "calculationUnit", key: "calculationUnit" },
          { title: "Trạng thái", dataIndex: "status", key: "status" },
          {
            title: "Hành động",
            key: "action",
            render: (_: unknown, record: Service) => (
              <>
                <Button icon={<EditOutlined />} onClick={() => handleEdit(record)} className="edit-button" />
                <Button icon={<DeleteOutlined />} onClick={() => handleDelete(record.key)} className="delete-button" />
              </>
            ),
          },
        ]}
        className="service-table"
      />
      <Modal
        title={editingService ? "Chỉnh sửa dịch vụ" : "Thêm dịch vụ"}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
      >
        <Form form={form} onFinish={handleSave}>
          <Form.Item name="serviceName" label="Tên dịch vụ" rules={[{ required: true, message: "Vui lòng nhập tên dịch vụ" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Mô tả" rules={[{ required: true, message: "Vui lòng nhập mô tả" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="pricePerUnit" label="Giá mỗi đơn vị" rules={[{ required: true, message: "Vui lòng nhập giá" }]}>
            <Input type="number" />
          </Form.Item>
          <Form.Item name="calculationUnit" label="Đơn vị tính" rules={[{ required: true, message: "Vui lòng nhập đơn vị" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="status" label="Trạng thái" rules={[{ required: true, message: "Vui lòng chọn trạng thái" }]}>
            <Input />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">Lưu</Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default ServiceManagement;