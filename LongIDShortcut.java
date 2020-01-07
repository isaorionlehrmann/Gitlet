package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;

/** Class for the shortcut ID.
 *  @author Isa Orion Lehrmann
 */
public class LongIDShortcut implements Serializable {

    /** to create a folder for IDShortcut. */
    static final File IDSHORTCUT = Utils.join(Main.getMainFolder(),
            "IDShortcutFolder");

    /** Creates a LongIDShortcut object for one Hashmap. */
    public LongIDShortcut() {
        idMap = new HashMap<>();
    }

    /** Get LongIDShortcut Hashmap.
     * @return HashMap*/
    public HashMap<String, String> getIDMap() {
        return this.idMap;
    }

    /** Hashmap to retrieve SHA-1 from Short version. */
    private HashMap<String, String> idMap;
}
