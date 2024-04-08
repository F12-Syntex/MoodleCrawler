package com.moodlescraper.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class CheckboxTree extends JPanel {
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    public CheckboxTree() {
        setLayout(new BorderLayout());

        rootNode = new DefaultMutableTreeNode("Root");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setCellRenderer(new CheckboxTreeCellRenderer());
        tree.addMouseListener(new NodeSelectionListener());

        JScrollPane scrollPane = new JScrollPane(tree);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addNode(DefaultMutableTreeNode node) {
        rootNode.add(node);
        treeModel.reload();
    }

    public void removeAllNodes() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    public void selectAll() {
        selectNodes(true);
    }

    public void deselectAll() {
        selectNodes(false);
    }

    private void selectNodes(boolean select) {
        int rowCount = treeModel.getChildCount(rootNode);
        for (int i = 0; i < rowCount; i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeModel.getChild(rootNode, i);
            TreePath path = new TreePath(treeModel.getPathToRoot(childNode));
            tree.getSelectionModel().addSelectionPath(path);
        }
    }

    private class CheckboxTreeCellRenderer extends DefaultTreeCellRenderer {
        private JCheckBox checkbox;

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            Component renderer = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (leaf && value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                checkbox = new JCheckBox(node.getUserObject().toString());
                checkbox.setSelected(selected);
                return checkbox;
            }

            return renderer;
        }
    }

    private class NodeSelectionListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int row = tree.getRowForLocation(e.getX(), e.getY());
            if (row != -1) {
                TreePath path = tree.getPathForRow(row);
                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.isLeaf()) {
                        boolean isSelected = tree.getSelectionModel().isPathSelected(path);
                        tree.getSelectionModel().removeSelectionPath(path);
                        if (!isSelected) {
                            tree.getSelectionModel().addSelectionPath(path);
                        }
                    }
                }
            }
        }
    }
}