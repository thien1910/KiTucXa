package com.project.KiTucXa.service;

import com.google.zxing.WriterException;
import com.project.KiTucXa.Service.QRCodeGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Pattern;

public class QRCodeGeneratorTest {

    @Test
    void testGenerateQRCode() {
        String testText = "Hello, QR Code!";
        try {
            String qrCodeData = QRCodeGenerator.generateQRCode(testText);

            // Kiểm tra chuỗi không rỗng
            assertNotNull(qrCodeData, "QR Code data should not be null");
            assertFalse(qrCodeData.isEmpty(), "QR Code data should not be empty");

            // Kiểm tra định dạng base64
            String base64Pattern = "^data:image/png;base64,[A-Za-z0-9+/=]+$";
            assertTrue(Pattern.matches(base64Pattern, qrCodeData), "QR Code data should be a valid Base64 image string");

            // Kiểm tra xem có đúng là dữ liệu ảnh không
            String base64String = qrCodeData.split(",")[1];
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            assertTrue(decodedBytes.length > 0, "Decoded bytes should not be empty");
        } catch (WriterException | IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
