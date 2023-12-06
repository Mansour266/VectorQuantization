import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class VectorQuantization {
    VectorQuantization(){} //Default constructor
    public static void compress(String inputImagePath, String outputImagePath, int codeVectorCount, int vectorLength, int vectorWidth) {
        try {
            BufferedImage rgbImage = ImageIO.read(new File(inputImagePath));
            int width = rgbImage.getWidth();
            int height = rgbImage.getHeight();

            double[][] pixels = new double[width * height][3];
            int index = 0;

            for (int i = 0; i < width; i += vectorWidth) {
                for (int j = 0; j < height; j += vectorLength) {
                    double[] pixelSum = {0, 0, 0};
                    int count = 0;

                    for (int x = i; x < i + vectorWidth && x < width; x++) {
                        for (int y = j; y < j + vectorLength && y < height; y++) {
                            Color pixelColor = new Color(rgbImage.getRGB(x, y));
                            pixelSum[0] += pixelColor.getRed();
                            pixelSum[1] += pixelColor.getGreen();
                            pixelSum[2] += pixelColor.getBlue();
                            count++;
                        }
                    }

                    pixels[index][0] = pixelSum[0] / count;
                    pixels[index][1] = pixelSum[1] / count;
                    pixels[index][2] = pixelSum[2] / count;
                    index++;
                }
            }

            RealMatrix dataMatrix = MatrixUtils.createRealMatrix(pixels);

            // LBG Algorithm
            LBGAlgorithm lbgAlgorithm = new LBGAlgorithm(dataMatrix, codeVectorCount);
            RealMatrix compressedData = lbgAlgorithm.vectorQuantization();

            // Reconstruct the compressed image
            BufferedImage compressedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            index = 0;

            for (int i = 0; i < width; i += vectorWidth) {
                for (int j = 0; j < height; j += vectorLength) {
                    int compressedValue = (int) compressedData.getEntry(index++, 0);
                    Color clusterColor = new Color((int) lbgAlgorithm.codeVector.getEntry(compressedValue, 0),
                            (int) lbgAlgorithm.codeVector.getEntry(compressedValue, 1),
                            (int) lbgAlgorithm.codeVector.getEntry(compressedValue, 2));

                    // Set the cluster color in the specified region
                    for (int x = i; x < i + vectorWidth && x < width; x++) {
                        for (int y = j; y < j + vectorLength && y < height; y++) {
                            compressedImage.setRGB(x, y, clusterColor.getRGB());
                        }
                    }
                }
            }

            // Save the compressed image
            ImageIO.write(compressedImage, "jpg", new File(outputImagePath));



            // Convert the RealMatrix to 1D array of integers
            int[] compressedIntArray = new int[compressedData.getRowDimension()];
            for (int i = 0; i < compressedData.getRowDimension(); i++) {
                compressedIntArray[i] = (int) compressedData.getEntry(i, 0);
            }


            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("output.dat"))) {
                outputStream.writeObject(compressedIntArray);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Image compression completed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}