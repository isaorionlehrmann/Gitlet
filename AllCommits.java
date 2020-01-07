package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;

/** AllCommits Class to store commits.
 *  @author Isa Orion Lehrmann
 */
public class AllCommits implements Serializable {

    /** Commits Folder. */
    static final File ALLCOMMITS = Utils.join
            (Main.getMainFolder(), "commitFolder");

    /** Creates a AllCommits object for one Hashmap. */
    public AllCommits() {
        commitMap = new HashMap<>();
    }

    /** Get allcommits Hashmap.
     * @return HashMap<String, Commit>*/
    public HashMap<String, Commit> getCommitMap() {
        return this.commitMap;
    }

    /** Hashmap to retrieve Commit from SHA-1. */
    private HashMap<String, Commit> commitMap;
}
