import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;


public class VectorQuantization {
    String inputImageName;
    String outputImageName;
    int codevectorCount;
    VectorQuantization(String inputImageName, String outputImageName, int codevectorCount){
        this.inputImageName = inputImageName;
        this.outputImageName = outputImageName;
        this.codevectorCount = codevectorCount;
    }

    public static void compress(String inputImagePath, String outputImagePath, int codevectorCount) {
        try {

            BufferedImage bwImage = ImageIO.read(new File(inputImagePath));
            int width = bwImage.getWidth();
            int height = bwImage.getHeight();

            double[][] pixels = new double[width * height][1];
            int index = 0;

            //
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color pixelColor = new Color(bwImage.getRGB(i, j));
                    int grayValue = pixelColor.getRed();
                    pixels[index++][0] = grayValue;
                }
            }

            RealMatrix dataMatrix = MatrixUtils.createRealMatrix(pixels);

            // LBG Algorithm
            LBGAlgorithm lbgAlgorithm = new LBGAlgorithm(dataMatrix, codevectorCount);
            RealMatrix compressedData = lbgAlgorithm.vectorQuantization();

            // Reconstruct the compressed image
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            index = 0;

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int compressedValue = (int) compressedData.getEntry(index++, 0);
                    int rgbValue = new Color(compressedValue, compressedValue, compressedValue).getRGB();
                    compressedImage.setRGB(i, j, rgbValue);
                }
            }

            // Save the compressed image
            ImageIO.write(compressedImage, "jpg", new File(outputImagePath));



            // writing to a binary file
            try(ObjectOutputStream outputStream = new ObjectOutputStream((new FileOutputStream("output.dat")))){
                outputStream.writeObject(compressedImage);
            } catch (IOException e){
                e.printStackTrace();
            }


//            // reading from a binary file
//
//            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("output.dat"))) {
//                Object object = inputStream.readObject();
//
//                if (object instanceof BufferedImage) {
//                    BufferedImage compressedImageFromBinary = (BufferedImage) object;
//                    // Use the BufferedImage as needed
//                } else {
//                    // Handle the case where the object is not a BufferedImage
//                    System.err.println("Unexpected object type in the file");
//                }
//
//            } catch (IOException | ClassNotFoundException e) {
//                e.printStackTrace();
//            }



            System.out.println("Image compression completed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
