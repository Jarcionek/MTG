package mtg;

import java.io.File;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.tree.TreeNode;

/**
 * @author Jaroslaw Pawlak
 */
public class CardTreeNode implements TreeNode {
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
        this.children = this.file.listFiles();
        if (this.children == null) {
            this.children = new File[0];
        }
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
        CardTreeNode ftn = (CardTreeNode) node;
        for (int i = 0; i < this.children.length; i++) {
            if (ftn.file.equals(this.children[i])) {
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
