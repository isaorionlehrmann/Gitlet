package gitlet;

import java.io.Serializable;

/** Class to store the currBranchName.
 *  @author Isa Orion Lehrmann
 */
public class CurrBranchName implements Serializable {

    /** Creates a currBranchName.
     * @param name the name. */
    public CurrBranchName(String name) {
        _name = name;
    }

    /** Get currBranchName.
     * @return String */
    public String getCurrBranchName() {
        return this._name;
    }

    /** name of current Branch. */
    private String _name;
}
