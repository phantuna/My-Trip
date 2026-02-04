package org.example.demo_datn.service.Photo;
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

public class ImageUtil {

    // Resize giữ tỉ lệ
    private ImageUtil() {}

    /**
     * Đọc ảnh từ MultipartFile an toàn
     */
    public static BufferedImage readImage(MultipartFile file) {
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

    /**
     * Resize ảnh giữ nguyên tỉ lệ (Aspect Ratio)
     * Sử dụng thuật toán Bicubic để ảnh mượt hơn
     */
    public static BufferedImage resize(BufferedImage original, int maxWidth) {
        if (original.getWidth() <= maxWidth) {
            return original;
        }

        int newHeight = (int) ((double) maxWidth / original.getWidth() * original.getHeight());

        BufferedImage resized = new BufferedImage(
                maxWidth,
                newHeight,
                BufferedImage.TYPE_INT_RGB // Dùng RGB để tránh lỗi màu khi resize
        );

        Graphics2D g = resized.createGraphics();
        // Bật chế độ khử răng cưa và nội suy chất lượng cao
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(original, 0, 0, maxWidth, newHeight, Color.WHITE, null); // Vẽ nền trắng nếu ảnh gốc có chỗ trong suốt
        g.dispose();

        return resized;
    }

    /**
     * Chuyển đổi ảnh sang hệ màu RGB (Xử lý ảnh PNG trong suốt)
     * Nếu không có bước này, ảnh PNG trong suốt nén sang JPG sẽ bị nền đen xì.
     */
    private static BufferedImage toRgb(BufferedImage input) {
        // Nếu đã là RGB (không có kênh Alpha) thì trả về luôn
        if (input.getType() == BufferedImage.TYPE_INT_RGB) {
            return input;
        }

        // Tạo ảnh mới có nền trắng
        BufferedImage rgb = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        g.drawImage(input, 0, 0, Color.WHITE, null); // Vẽ đè lên nền trắng
        g.dispose();
        return rgb;
    }

    /**
     * Nén ảnh sang định dạng JPEG với chất lượng tùy chọn
     * @param quality: 0.0f (nén mạnh nhất) -> 1.0f (chất lượng cao nhất)
     */
    public static byte[] compressJpeg(BufferedImage image, float quality) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Bước quan trọng: Đảm bảo ảnh là RGB trước khi nén JPEG
            BufferedImage rgbImage = toRgb(image);

            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            if (!writers.hasNext()) {
                throw new RuntimeException("No JPEG writers available");
            }

            ImageWriter writer = writers.next();
            ImageWriteParam param = writer.getDefaultWriteParam();

            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
                writer.setOutput(ios);
                writer.write(null, new IIOImage(rgbImage, null, null), param);
                writer.dispose(); // Giải phóng resource của writer
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("JPEG compression failed", e);
        }
    }
}