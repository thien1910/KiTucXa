package com.project.KiTucXa.service;

import com.google.zxing.WriterException;
import com.project.KiTucXa.Service.QRCodeGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Base64;

public class QRCodeGeneratorTest {

    @Test
    void testGenerateQRCode_Success() {
        String text = "Hello, QR Code!";
        try {
            String qrCode = QRCodeGenerator.generateQRCode(text);

            // Kiểm tra xem chuỗi không rỗng
            assertNotNull(qrCode, "QR code không được null");
            assertFalse(qrCode.isEmpty(), "QR code không được rỗng");

            // Kiểm tra xem chuỗi có đúng định dạng Base64 không
            assertTrue(qrCode.startsWith("data:image/png;base64,"), "QR code phải có prefix đúng");

            // Kiểm tra có phải Base64 hợp lệ không
            String base64Data = qrCode.replace("data:image/png;base64,", "");
            assertDoesNotThrow(() -> Base64.getDecoder().decode(base64Data), "Dữ liệu Base64 không hợp lệ");

        } catch (WriterException | IOException e) {
            fail("Test bị lỗi do exception: " + e.getMessage());
        }
    }

    @Test
    void testGenerateQRCode_EmptyText() {
        String text = "";
        assertThrows(WriterException.class, ()
                -> QRCodeGenerator.generateQRCode(text), "Nên ném lỗi khi input rỗng");
    }

    @Test
    void testGenerateQRCode_NullText() {
        assertThrows(NullPointerException.class, ()
                -> QRCodeGenerator.generateQRCode(null), "Nên ném lỗi khi input null");
    }
}
