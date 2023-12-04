public class Main {
    public static void main(String[] args) {
        String inputImagePath = "rgb.jpg";
        String outputImagePath = "output.jpg";
        int k = 16;
        VectorQuantization.compress(inputImagePath, outputImagePath, k);
    }
}
