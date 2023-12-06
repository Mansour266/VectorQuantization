import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ImageCompressionGUI extends JFrame {
    private static  JTextField inputImagePath;
    private static JLabel imageNameLabel;

    public ImageCompressionGUI() {
        // Set up the JFrame
        setTitle("Vector Quantization");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Set icon image
        ImageIcon icon = new ImageIcon("myLogo.png");
        setIconImage(icon.getImage());

        // Set background color
        getContentPane().setBackground(new Color(4, 3, 3));

        // Set minimum size
        setMinimumSize(new Dimension(900, 600));

        // Set layout manager
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // The header label
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel headerLabel = new JLabel("Vector Quantization");
        headerLabel.setForeground(new Color(212, 175, 55));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 60));
        add(headerLabel, gbc);

// The image input label and field
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel imageInputLabel = new JLabel("Image Path:");
        imageInputLabel.setForeground(Color.white);
        imageInputLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(imageInputLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER; // Change anchor to CENTER
        gbc.gridwidth = 2;
        inputImagePath = new JTextField();
        inputImagePath.setFont(new Font("Arial", Font.PLAIN, 20));
        inputImagePath.setPreferredSize(new Dimension(400, 40));
        add(inputImagePath, gbc);


        // Set font size for buttons
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        // The compress and decompress buttons
        gbc.gridy = 3;
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton compressButton = new JButton("Compress");
        compressButton.setFont(buttonFont);
        add(compressButton, gbc);


        // Create components for image selection
        imageNameLabel = new JLabel();
        JButton chooseImageButton = new JButton("Choose Image");

        // Set font size for the label and button
        Font labelAndButtonFont = new Font("Arial", Font.PLAIN, 18);
        imageNameLabel.setFont(labelAndButtonFont);
        chooseImageButton.setFont(labelAndButtonFont);

        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        add(imageNameLabel, gbc);

        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(chooseImageButton, gbc);
        // Add action listener to the "Choose Image" button
        chooseImageButton.addActionListener(e -> chooseImage());

        // Add action listeners to the buttons
        compressButton.addActionListener(e -> {
            String inputName = inputImagePath.getText();

            if (inputName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a Input image path for compression.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Here we specify the code vector count, vector length and vector width
            VectorQuantization.compress(inputName,
                    "output.jpg",
                    32,
                    2,
                    2);
            JOptionPane.showMessageDialog(null, "Image compressed successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });


        // window listener to handle the close operation
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "Exit",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void chooseImage() {
        JFileChooser imageDialog = new JFileChooser(".");
        int option = imageDialog.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            // Added to store the selected image
            File inputImage = imageDialog.getSelectedFile();
            imageNameLabel.setText(inputImage.getName());

            // Set the text of the inputImagePath to the selected image path
            inputImagePath.setText(inputImage.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImageCompressionGUI().setVisible(true));
    }
}