package org.acheron.authserver.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Slf4j
@Service
public class QrCodeService {
    public BufferedImage generateQrCode(String issuer,String email, String secret)  {
        try {

            String uri = String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", issuer, email, secret,issuer);
            BitMatrix matrix = new QRCodeWriter().encode(uri, BarcodeFormat.QR_CODE, 200, 200);

            return MatrixToImageWriter.toBufferedImage(matrix);
        }catch (WriterException e){
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
    }
}
