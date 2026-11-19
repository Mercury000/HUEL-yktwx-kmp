import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;

/** Generate static, deliberately non-payment fixtures using the upstream encoders. */
class GenerateFixtures {
    static void write(BitMatrix matrix, Path path) throws Exception {
        BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xffffff);
            }
        }
        ImageIO.write(image, "png", path.toFile());
    }
    public static void main(String[] args) throws Exception {
        Path output = Path.of(args[0]);
        Files.createDirectories(output);
        write(new QRCodeWriter().encode("https://example.invalid/caixiaotong-migration", BarcodeFormat.QR_CODE,
            900, 900, Map.of(EncodeHintType.MARGIN, 4)), output.resolve("demo_qr.png"));
        write(new Code128Writer().encode("DEMO-000000000000", BarcodeFormat.CODE_128,
            1440, 330, Map.of(EncodeHintType.MARGIN, 12)), output.resolve("demo_barcode.png"));
        System.out.println("Generated non-payment QR and barcode fixtures");
    }
}
