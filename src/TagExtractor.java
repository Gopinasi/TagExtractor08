import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TagExtractor extends JFrame {
    private JTextArea textArea;
    private JButton loadFileButton, loadStopWordsButton, saveFileButton, quitButton;
    private File selectedFile, stopWordsFile;
    private Map<String, Integer> wordFrequencyMap;
    private Set<String> stopWords;

    public TagExtractor() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        wordFrequencyMap = new HashMap<>();
        stopWords = new TreeSet<>();

        // Initialize GUI components
        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        loadFileButton = new JButton("Load Text File");
        loadStopWordsButton = new JButton("Load Stop Words File");
        saveFileButton = new JButton("Save Tags to File");
        quitButton = new JButton("Quit");

        JPanel panel = new JPanel();
        panel.add(loadFileButton);
        panel.add(loadStopWordsButton);
        panel.add(saveFileButton);
        panel.add(quitButton);

        // Add components to frame
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        loadFileButton.addActionListener(e -> loadTextFile());
        loadStopWordsButton.addActionListener(e -> loadStopWordsFile());
        saveFileButton.addActionListener(e -> saveTagsToFile());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void loadTextFile() {
        JFileChooser fileChooser = new JFileChooser();
        java.io.File workingDirectory = new java.io.File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            processFile(selectedFile);
        }
    }

    private void loadStopWordsFile() {
        JFileChooser fileChooser = new JFileChooser();
        java.io.File workingDirectory = new java.io.File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            stopWordsFile = fileChooser.getSelectedFile();
            loadStopWords(stopWordsFile);
            processFile(selectedFile);
        }
    }

    private void loadStopWords(File file) {
        stopWords.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error!");
        }
    }

    private void processFile(File file) {
        if (stopWords.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Got it! Choose stop words file to continue.");
            return;
        }
        wordFrequencyMap.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
            displayWordFrequencies();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error!");
        }
    }

    private void processLine(String line) {
        // Remove non-letter characters and convert to lowercase
        String cleanedLine = line.replaceAll("[^a-zA-Z\\s]", "").toLowerCase();
        String[] words = cleanedLine.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty() && !stopWords.contains(word)) {
                wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
            }
        }
    }

    private void displayWordFrequencies() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        textArea.setText(selectedFile.getName() + "\n\n" + sb.toString());
    }

    private void saveTagsToFile() {
        JFileChooser fileChooser = new JFileChooser();
        java.io.File workingDirectory = new java.io.File(System.getProperty("user.dir"));
        fileChooser.setCurrentDirectory(workingDirectory);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile))) {
                for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
                    writer.write(entry.getKey() + ": " + entry.getValue());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(this, "Done!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractor gui = new TagExtractor();
            gui.setVisible(true);
        });
    }
}