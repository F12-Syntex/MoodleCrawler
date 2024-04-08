package com.moodlescraper.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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

    // Add these instance variables to the MoodleScraperView class
    private JProgressBar progressBar;
    private JLabel currentDownloadLabel;

    public MoodleScraperView(String SESSION_KEY) {
        this.SESSION_KEY = SESSION_KEY;

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

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(new Color(18, 30, 49));
        urlField = new JTextField(
                "https://moodle.kent.ac.uk/2023/course/index.php?categoryid=3&browse=courses&perpage=1000&page=0", 50);
        urlField.setForeground(Color.WHITE);
        urlField.setBackground(new Color(64, 64, 64));
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
        searchPanel.add(urlField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

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

        JPanel downloadPanel = new JPanel(new BorderLayout());
        downloadPanel.setBackground(new Color(40, 40, 40));
        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(0, 255, 127)); // Change the color of the progress bar to a bright green
        progressBar.setBackground(Color.gray.darker());
        currentDownloadLabel = new JLabel();
        currentDownloadLabel.setForeground(new Color(200, 200, 200));
        downloadPanel.add(progressBar, BorderLayout.CENTER);
        downloadPanel.add(currentDownloadLabel, BorderLayout.SOUTH);
        downloadPanel.add(downloadButton, BorderLayout.EAST);
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
                        downloadFilesSection(data.getResourceFiles(), node);

                        // Download past papers section
                        completedSections++;
                        updateProgress(completedSections, totalSections);
                        downloadPastpapersSection(data.getPastpapers(), node);

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

    private void downloadFilesSection(List<ResourceFile> files, DefaultMutableTreeNode node) {
        DefaultMutableTreeNode filesSection = new DefaultMutableTreeNode("Files");
        for (ResourceFile file : files) {
            System.out.println("Downloading file: " + file.getName());
            currentDownloadLabel.setText("Downloading file: " + file.getName());
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
            filesSection.add(fileNode);
        }
        node.add(filesSection);
    }

    private void downloadPastpapersSection(List<ResourceFile> pastpapers, DefaultMutableTreeNode node) {
        DefaultMutableTreeNode pastpapersSection = new DefaultMutableTreeNode("Past Papers");
        for (ResourceFile pastpaper : pastpapers) {
            DefaultMutableTreeNode pastpaperNode = new DefaultMutableTreeNode(pastpaper.getName());
            pastpapersSection.add(pastpaperNode);
        }
        node.add(pastpapersSection);
    }

    private void updateProgress(int completedSections, int totalSections) {
        int progress = (int) ((double) completedSections / totalSections * 100);
        progressBar.setValue(progress);
    }
}