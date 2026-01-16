package org.example.demo_datn.Service.Photo;

import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ImageProcessingService {
    public BufferedImage readImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new AppException(ErrorCode.FILE_INVALID);
            }
            return image;
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_INVALID);
        }
    }

    public ProcessedImage resizeAndCompressJpeg(BufferedImage original, int maxWidth, float quality) {
        BufferedImage resized = ImageUtil.resize(original, maxWidth);

        // JPEG không chơi alpha. Nếu ảnh PNG có trong suốt -> convert sang RGB.
        BufferedImage rgbImage = toRgb(resized);

        byte[] jpegBytes = compressJpeg(rgbImage, quality);

        return new ProcessedImage(
                jpegBytes,
                rgbImage.getWidth(),
                rgbImage.getHeight()
        );
    }

    private BufferedImage toRgb(BufferedImage input) {
        if (input.getType() == BufferedImage.TYPE_INT_RGB) return input;

        BufferedImage rgb = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.drawImage(input, 0, 0, Color.WHITE, null); // nền trắng nếu ảnh có alpha
        g.dispose();
        return rgb;
    }

    private byte[] compressJpeg(BufferedImage image, float quality) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                throw new RuntimeException("No JPEG writers available");
            }

            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // 0.0f -> nát, 1.0f -> nét
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(image, null, null), param);
                writer.dispose();
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("JPEG compression failed", e);
        }
    }

    // record Java 16+; nếu Java thấp hơn thì dùng class thường
    public record ProcessedImage(byte[] bytes, int width, int height) {}
}