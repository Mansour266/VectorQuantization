import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileCompressionGUI extends JFrame {
    private JTextField inputFileField, outputFileField;
    private JLabel fileNameLabel;
    private JButton chooseFileButton;

    private File inputFile; // Added to store the selected file

    public FileCompressionGUI() {
        Huffman huffman = new Huffman();
        // Set up the JFrame
        setTitle("Huffman Coding");
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
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel headerLabel = new JLabel("Huffman Coding");
        headerLabel.setForeground(new Color(212, 175, 55));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 60));
        add(headerLabel, gbc);

        // The file input label and field
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel fileInputLabel = new JLabel("File Input Name:");
        fileInputLabel.setForeground(Color.white);
        fileInputLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(fileInputLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        inputFileField = new JTextField();
        inputFileField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputFileField.setPreferredSize(new Dimension(400, 40));
        add(inputFileField, gbc);

        // The file output label and field
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel fileOutLabel = new JLabel("File Output Name:");
        fileOutLabel.setForeground(Color.white);
        fileOutLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(fileOutLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        outputFileField = new JTextField();
        outputFileField.setFont(new Font("Arial", Font.PLAIN, 20));
        outputFileField.setPreferredSize(new Dimension(400, 40));
        add(outputFileField, gbc);

        // Set font size for buttons
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        // The compress and decompress buttons
        gbc.gridy = 3;
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton compressButton = new JButton("Compress");
        compressButton.setFont(buttonFont);
        add(compressButton, gbc);

        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton decompressButton = new JButton("Decompress");
        decompressButton.setBackground(new Color(212, 175, 55));
        decompressButton.setForeground(Color.black);
        decompressButton.setFocusPainted(false);
        decompressButton.setFont(buttonFont);
        add(decompressButton, gbc);

        // Create components for file selection
        fileNameLabel = new JLabel();
        chooseFileButton = new JButton("Choose File");

        // Set font size for the label and button
        Font labelAndButtonFont = new Font("Arial", Font.PLAIN, 18);
        fileNameLabel.setFont(labelAndButtonFont);
        chooseFileButton.setFont(labelAndButtonFont);

        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        add(fileNameLabel, gbc);

        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(chooseFileButton, gbc);
        // Add action listener to the "Choose File" button
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        // Add action listeners to the buttons
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputFileName = inputFileField.getText();
                String outFileName = outputFileField.getText();

                if (inputFileName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Input file path for compression.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    huffman.compress(inputFileName, outFileName);
                    huffman.printCodes();
                    JOptionPane.showMessageDialog(null, "File compressed successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "File not found: " + inputFileName, "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred during compression.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        decompressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputFileName = inputFileField.getText();
                String outFileName = outputFileField.getText();

                if (inputFileName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Input file path for decompression.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    huffman.decompress(inputFileName, outFileName);
                    JOptionPane.showMessageDialog(null, "File decompressed successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "File not found: " + inputFileName, "Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "An error occurred during decompression.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
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

    private void chooseFile() {
        JFileChooser fileDialog = new JFileChooser(".");
        int option = fileDialog.showOpenDialog(this);

        if (option == JFileChooser.APPROVE_OPTION) {
            inputFile = fileDialog.getSelectedFile();
            fileNameLabel.setText(inputFile.getName());

            // Set the text of the inputFileField to the selected file path
            inputFileField.setText(inputFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileCompressionGUI().setVisible(true);
            }
        });
    }
}
