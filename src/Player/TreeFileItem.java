package Player;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * @author Alexander Bolte - Bolte Consulting (2010 - 2014).
 *
 *         This class shall be a simple implementation of a TreeItem for
 *         displaying a file system tree.
 *
 *         The idea for this class is taken from the Oracle API docs found at
 *         http
 *         ://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html.
 *
 *         Basically the file sytsem will only be inspected once. If it changes
 *         during runtime the whole tree would have to be rebuild. Event
 *         handling is not provided in this implementation.
 */
public class TreeFileItem extends TreeItem<File> {

    /**
     * Calling the constructor of super class in oder to create a new
     * TreeItem<File>.
     *
     * @param f
     *            an object of type File from which a tree should be build or
     *            which children should be gotten.
     */
    public TreeFileItem(File f) {
        super(f);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.TreeItem#getChildren()
     */
    @Override
    public ObservableList<TreeItem<File>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;

            /*
             * First getChildren() call, so we actually go off and determine the
             * children of the File contained in this TreeItem.
             */
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.TreeItem#isLeaf()
     */
    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            File f = getValue();
            isLeaf = f.isFile();
        }

        return isLeaf;
    }

    /**
     * Returning a collection of type ObservableList containing TreeItems, which
     * represent all children available in handed TreeItem.
     *
     * @param TreeItem
     *            the root node from which children a collection of TreeItem
     *            should be created.
     * @return an ObservableList<TreeItem<File>> containing TreeItems, which
     *         represent all children available in handed TreeItem. If the
     *         handed TreeItem is a leaf, an empty list is returned.
     */
    private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
        File f = TreeItem.getValue();
        if (f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                ObservableList<TreeItem<File>> children = FXCollections
                        .observableArrayList();

                for (File childFile : files) {
                    children.add(new TreeFileItem(childFile));
                }

                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }

    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isLeaf;
}