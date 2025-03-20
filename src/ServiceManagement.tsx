import React, { useState } from "react";
import { Table, Input, Button, Modal, Form } from "antd";
import { EditOutlined, PlusOutlined, DeleteOutlined } from "@ant-design/icons";
import "./ServiceManagement.css";

interface Service {
  key: string;
  name: string;
  description: string;
}

const ServiceManagement: React.FC = () => {
  const [services, setServices] = useState<Service[]>([
    { key: "1", name: "Internet", description: "Dịch vụ internet tốc độ cao" },
    { key: "2", name: "Điện", description: "Dịch vụ cung cấp điện" },
  ]);
  const [modalVisible, setModalVisible] = useState<boolean>(false);
  const [editingService, setEditingService] = useState<Service | null>(null);
  const [form] = Form.useForm();

  const handleAdd = () => {
    setEditingService(null);
    setModalVisible(true);
  };

  const handleEdit = (record: Service) => {
    setEditingService(record);
    setModalVisible(true);
  };

  const handleDelete = (key: string) => {
    setServices((prev) => prev.filter((service) => service.key !== key));
  };

  const handleSave = (values: Omit<Service, "key">) => {
    if (editingService) {
      setServices((prev) =>
        prev.map((service) =>
          service.key === editingService.key ? { ...service, ...values } : service
        )
      );
    } else {
      const newService: Service = { key: Date.now().toString(), ...values };
      setServices((prev) => [...prev, newService]);
    }
    setModalVisible(false);
    setEditingService(null);
    form.resetFields();
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
          { title: "Tên dịch vụ", dataIndex: "name", key: "name" },
          { title: "Mô tả", dataIndex: "description", key: "description" },
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
        <Form
          form={form}
          onFinish={handleSave}
          initialValues={editingService || { name: "", description: "" }}
        >
          <Form.Item name="name" label="Tên dịch vụ" rules={[{ required: true, message: "Vui lòng nhập tên dịch vụ" }]}>
            <Input />
          </Form.Item>
          <Form.Item name="description" label="Mô tả" rules={[{ required: true, message: "Vui lòng nhập mô tả" }]}>
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