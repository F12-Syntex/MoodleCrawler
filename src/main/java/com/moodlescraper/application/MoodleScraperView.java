package com.moodlescraper.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.moodlescraper.crawler.MoodleCrawler;
import com.moodlescraper.module.MoodleCourse;
import com.moodlescraper.module.MoodleCourseData;
import com.moodlescraper.module.ResourceFile;
import com.moodlescraper.utils.FileNameUtils;

public class MoodleScraperView extends JFrame {

    private String SESSION_KEY;
    private JTextField urlField;
    private JTree courseTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    private JButton downloadButton;

    private File downloadDirectory;

    private JProgressBar progressBar;
    private JLabel currentDownloadLabel;

    private long totalBytesDownloaded = 1;
    private long downloadTimeinMs = 0;
    private long downloadedFiles = 0;

    private long filesToDownload = 0;

    private JLabel etaLabel;

    public MoodleScraperView(String SESSION_KEY) {
        this.SESSION_KEY = SESSION_KEY;

        this.downloadDirectory = new File("C:\\Users\\synte\\OneDrive - University of Kent\\Desktop\\moodle");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Moodle Scraper");
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set the look and feel to a dark theme
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.put("control", new Color(64, 64, 64));
            UIManager.put("info", new Color(128, 128, 128));
            UIManager.put("nimbusBase", new Color(18, 30, 49));
            UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
            UIManager.put("nimbusFocus", new Color(115, 164, 209));
            UIManager.put("nimbusGreen", new Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
            UIManager.put("nimbusOrange", new Color(191, 98, 4));
            UIManager.put("nimbusRed", new Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
            UIManager.put("text", new Color(230, 230, 230));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(18, 30, 49));

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(18, 30, 49));
        urlField = new JTextField(
                "https://moodle.kent.ac.uk/2023/course/index.php?categoryid=3&browse=courses&perpage=1000&page=0");
        urlField.setForeground(Color.WHITE);
        urlField.setBackground(new Color(64, 64, 64));
        urlField.setColumns(30); // Set the initial number of columns
        searchPanel.add(urlField, BorderLayout.CENTER); // Add the urlField to the center of the BorderLayout
        JButton searchButton = new JButton("Search");
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(64, 64, 64));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = urlField.getText();
                performSearch(url);
            }
        });
        searchPanel.add(searchButton, BorderLayout.EAST); // Add the searchButton to the east of the BorderLayout
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Add a component listener to the main panel to adjust the urlField's width
        mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int width = mainPanel.getWidth() - searchButton.getWidth() - 20; // Subtract the searchButton's width
                                                                                 // and some padding
                urlField.setColumns(width / urlField.getFontMetrics(urlField.getFont()).charWidth('W')); // Set the
                                                                                                         // number of
                                                                                                         // columns
                                                                                                         // based on the
                                                                                                         // panel width
            }
        });

        rootNode = new DefaultMutableTreeNode("Courses");
        treeModel = new DefaultTreeModel(rootNode);
        courseTree = new JTree(treeModel);
        courseTree.setBackground(new Color(18, 30, 49));
        courseTree.setForeground(Color.WHITE);
        courseTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(courseTree);
        scrollPane.getViewport().setBackground(new Color(18, 30, 49));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        downloadButton = new JButton("Download");
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setBackground(new Color(64, 64, 64));
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadSelectedCourses();
            }
        });

        // In the MoodleScraperView constructor
        JPanel downloadPanel = new JPanel(new GridBagLayout());
        downloadPanel.setBackground(new Color(40, 40, 40));

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setOpaque(false);

        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(0, 200, 0));
        progressBar.setBackground(Color.gray.darker());
        progressBar.setStringPainted(true);
        progressBar.setString("");

        currentDownloadLabel = new JLabel();
        currentDownloadLabel.setForeground(new Color(200, 200, 200));

        etaLabel = new JLabel();
        etaLabel.setForeground(new Color(200, 200, 200));

        progressPanel.add(currentDownloadLabel);
        progressPanel.add(progressBar);
        progressPanel.add(etaLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        downloadPanel.add(progressPanel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        downloadButton = new JButton("Download");
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setBackground(new Color(64, 64, 64));
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadSelectedCourses();
            }
        });
        downloadPanel.add(downloadButton, gbc);

        mainPanel.add(downloadPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void performSearch(String url) {
        MoodleCrawler crawler = new MoodleCrawler(SESSION_KEY, url);
        List<MoodleCourse> courses = crawler.scrape();

        rootNode.removeAllChildren();
        for (MoodleCourse course : courses) {
            DefaultMutableTreeNode courseNode = new DefaultMutableTreeNode(course);
            // courseNode.setUserObject(course);

            rootNode.add(courseNode);

        }
        treeModel.reload();
    }

    // Update the downloadSelectedCourses() method
    private void downloadSelectedCourses() {
        TreePath[] selectedPaths = courseTree.getSelectionPaths();
        if (selectedPaths == null) {
            return;
        }

        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        currentDownloadLabel.setText("Downloading...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int totalSections = selectedPaths.length * 3; // Assuming 3 sections per course
                int completedSections = 0;

                for (TreePath path : selectedPaths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.isLeaf()) {
                        MoodleCourse course = (MoodleCourse) node.getUserObject();

                        String name = FileNameUtils.makeFolderNameSafe(course.getCourseName());
                        System.out.println("Downloading course: " + name);
                        currentDownloadLabel.setText("Downloading course: " + name);

                        MoodleCourseData data = course.getMetaData();

                        totalSections += data.getPastpapers().size();
                        totalSections += data.getResourceFiles().size();

                        filesToDownload = data.getPastpapers().size() + data.getResourceFiles().size();

                        // Download name section
                        completedSections++;
                        updateProgress(completedSections, totalSections);
                        downloadSection("Name: " + data.getCourseName(), node);

                        // Download ID section
                        completedSections++;
                        updateProgress(completedSections, totalSections);
                        downloadSection("CourseId: " + data.getCourseId(), node);

                        // Download files section
                        completedSections++;
                        updateProgress(completedSections, totalSections);
                        completedSections += downloadFilesSection(data.getResourceFiles(), node, completedSections, totalSections);

                        // Download past papers section
                        completedSections++;
                        updateProgress(completedSections, totalSections);
                        completedSections += downloadPastpapersSection(data.getPastpapers(), node, completedSections, totalSections);

                        treeModel.reload(node);
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                currentDownloadLabel.setText("Download completed.");
            }
        };

        worker.execute();
    }

    // Add these methods to handle the download process
    private void downloadSection(String sectionName, DefaultMutableTreeNode node) {
        DefaultMutableTreeNode section = new DefaultMutableTreeNode(sectionName);
        node.add(section);
    }

    private int downloadFilesSection(List<ResourceFile> files, DefaultMutableTreeNode node, int completedSections,
            int totalSections) {
        DefaultMutableTreeNode filesSection = new DefaultMutableTreeNode("Files");
        for (ResourceFile file : files) {
            System.out.println("Downloading file: " + file.getName());
            currentDownloadLabel.setText("Downloading file: " + file.getName());
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
            filesSection.add(fileNode);

            try {
                downloadFileForFilesOrPastPaper(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            completedSections++;
            updateProgress(completedSections, totalSections);
        }
        node.add(filesSection);
        return completedSections;
    }

    public void downloadFileForFilesOrPastPaper(ResourceFile file) throws IOException{
        long startTime = System.currentTimeMillis();
        File downloadedFile = file.install(downloadDirectory);
        long endTime = System.currentTimeMillis();

        downloadTimeinMs += endTime - startTime;
        
        if(downloadedFile != null){
            totalBytesDownloaded += downloadedFile.length();
        }

        downloadedFiles++;
        filesToDownload--;

        etaLabel.setText("ETA: " + getEtaForNFile(filesToDownload));
    }

    private String getEtaToDownloadFile(long fileSize){
        long downloadSpeed = totalBytesDownloaded / downloadTimeinMs;
        long eta = fileSize / downloadSpeed;
        return prettyTimeFromMilli(eta);
    }

    private String getEtaForNFile(long files){
        long averageFileSize = totalBytesDownloaded / downloadedFiles;
        long downloadSpeed = totalBytesDownloaded / downloadTimeinMs;
        long eta = (averageFileSize * files) / downloadSpeed;
        return prettyTimeFromMilli(eta);
    }

    private String prettyTimeFromMilli(long milli){
        long seconds = milli / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%d hours, %d minutes, %d seconds", hours, minutes % 60, seconds % 60);
    }

    private int downloadPastpapersSection(List<ResourceFile> pastpapers, DefaultMutableTreeNode node,
            int completedSections, int totalSections) {
        DefaultMutableTreeNode pastpapersSection = new DefaultMutableTreeNode("Past Papers");
        for (ResourceFile pastpaper : pastpapers) {
            DefaultMutableTreeNode pastpaperNode = new DefaultMutableTreeNode(pastpaper.getName());
            pastpapersSection.add(pastpaperNode);

            try {
                downloadFileForFilesOrPastPaper(pastpaper);
            } catch (Exception e) {
                e.printStackTrace();
            }

            completedSections++;
            updateProgress(completedSections, totalSections);
        }

        node.add(pastpapersSection);
        return completedSections;
    }

    private void updateProgress(int completedSections, int totalSections) {
        int progress = (int) ((double) completedSections / totalSections * 100);
        progressBar.setValue(progress);
        progressBar.setString(progress + "%"); // Display the progress percentage on the progress bar
    }
}