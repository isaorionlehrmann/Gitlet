package gitlet;

import java.io.Serializable;

/** Class for Branch object.
 *  @author Isa Orion Lehrmann
 */
public class Branch implements Serializable {

    /** Init for a Branch object.
     *
     * @param name name
     * @param newHead commit object
     */
    public Branch(String name, Commit newHead) {
        _name = name;
        _head = newHead;
        _headSHA1 = newHead.getID();
        AllBranches allBranchesToAdd = Main.loadAllBranches();
        allBranchesToAdd.getBranchMap().put(this.getName(), this);
        Main.saveAllBranches(allBranchesToAdd);
    }

    /** to return name. */
    public String getName() {
        return _name;
    }

    /** to return the sha1 of head. */
    public String getHead() {
        return _headSHA1;
    }

    /** to update the head commit.
     * @param forUpdate */
    public void updateHeadCommit(Commit forUpdate) {
        _head = forUpdate;
        _headSHA1 = forUpdate.getID();
    }

    /** named of the branch. */
    private String _name;

    /** the sha1 of the head commit. */
    private String _headSHA1;

    /** the head commit. */
    private transient Commit _head;
}
