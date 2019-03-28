import com.js.IOpticalCharacterRecognitionEngine;
import com.js.tesseract.TesseractOpticalCharacterRecognitionEngine;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestAnnotateScan {

    @Test
    public void annotateScan() throws IOException {

        File inputImageFile = new File("C:\\Users\\joris\\Downloads\\scan.png");
        BufferedImage bufferedImage = ImageIO.read(inputImageFile);
        Graphics2D g2 = bufferedImage.createGraphics();

        File dataDirectory = new File("C:\\Users\\joris\\Downloads\\tessdata");
        IOpticalCharacterRecognitionEngine opticalCharacterRecognitionEngine = new TesseractOpticalCharacterRecognitionEngine(dataDirectory, "eng");

        g2.setColor(Color.RED);
        for(IOpticalCharacterRecognitionEngine.OCRChunk chunk : opticalCharacterRecognitionEngine.doOCR(bufferedImage)){
            g2.drawRect(chunk.getLocation().x, chunk.getLocation().y, chunk.getLocation().width, chunk.getLocation().height);
        }

        ImageIO.write(bufferedImage, "PNG", new File(inputImageFile.getParent(), "copy.png"));
    }
}
