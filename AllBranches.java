package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;

/** Class to store all branches.
 *  @author Isa Orion Lehrmann
 */
public class AllBranches implements Serializable {

    /** branches for the folder. */
    static final File BRANCHES = Utils.join(Main.
            getMainFolder(), "branchFolder");

    /** Creates a AllCommits object for one Hashmap. */
    public AllBranches() {
        branchMap = new HashMap<>();
    }

    /** Get allBranches Hashmap.
     * @return HashMap<String, Branch>*/
    public HashMap<String, Branch> getBranchMap() {
        return this.branchMap;
    }

    /** Hashmap to retrieve Branch from name. */
    private HashMap<String, Branch> branchMap;
}
