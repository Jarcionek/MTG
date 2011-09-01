package deckCreator;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.tree.TreeNode;

/**
 * @author Jaroslaw Pawlak
 */
public class CardTreeNode implements TreeNode {
    /**
     * Lists Files which are directories and contain JPG files in any subdirectory
     */
    private static FileFilter ff = new FileFilter() {
        public boolean accept(File pathname) {
            if (!pathname.isDirectory()) {
                return false;
            }
            if (pathname.listFiles(fnf).length > 0) {
                return true;
            }
            for (File e : pathname.listFiles(ff)) {
                if (e.listFiles(fnf).length > 0) {
                    return true;
                }
            }
            return false;
        }
    };
    /**
     * Lists JPG files
     */
    private static FilenameFilter fnf = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            return name.contains(".")
                    && name.substring(name.lastIndexOf("."))
                    .toLowerCase().equals(".jpg");
        }
    };

    private File file;
    private File[] children;
    private TreeNode parent;

    private CardTreeNode() {}

    public CardTreeNode(File[] children) {
        this.file = null;
        this.parent = null;
        this.children = children;
    }

    public CardTreeNode(File file, TreeNode parent) {
        this.file = file;
        this.parent = parent;
        if (!file.isDirectory()) {
            this.children = new File[0];
        } else {
            int i = file.listFiles(ff).length + file.listFiles(fnf).length;
            this.children = new File[i];
            i = 0;
            for (File e : file.listFiles(ff)) {
                    this.children[i++] = e;
            }
            for (File e : file.listFiles(fnf)) {
                    this.children[i++] = e;
            }
        }
    }

    public File getFile() {
        return file;
    }

    /**
     * Counts all non-directory files among all children of this node (recursive)
     * @return non-directory files in this and subnodes or 0 if CardTreeNode
     * stores non-directory
     */
    public int countFiles() {
        int r = 0;
        for (int i = 0; i < getChildCount(); i++) {
            CardTreeNode x = (CardTreeNode) getChildAt(i);
            if (x.getFile().isDirectory()) {
                r += x.countFiles();
            } else {
                r += 1;
            }
        }
        return r;
    }

    @Override
    public String toString() {
        if (file == null) {
            return null;
        } else if (!file.getName().contains(".")) {
            return file.getName();
        } else {
            return file.getName().substring(0, file.getName().lastIndexOf("."));
        }
    }

    public TreeNode getChildAt(int childIndex) {
        return new CardTreeNode(this.children[childIndex], this);
    }

    public int getChildCount() {
        return this.children.length;
    }

    public TreeNode getParent() {
        return this.parent;
    }

    public int getIndex(TreeNode node) {
        CardTreeNode ctn = (CardTreeNode) node;
        for (int i = 0; i < this.children.length; i++) {
            if (ctn.file.equals(this.children[i])) {
                return i;
            }
        }
        return -1;
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return this.getChildCount() == 0;
    }

    public Enumeration children() {
        final int elementCount = this.children.length;
        return new Enumeration<File>() {
            int count = 0;

            public boolean hasMoreElements() {
                return this.count < elementCount;
            }

            public File nextElement() {
                if (this.count < elementCount) {
                    return CardTreeNode.this.children[this.count++];
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

}
