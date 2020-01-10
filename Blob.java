package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;

/** Class to store all Blobs.
 *  @author Isa Orion Lehrmann
 */
public class Blob implements Serializable {

    /** To create folder to hold blobs. */
    static final File BLOB = Utils.join(Main.getMainFolder(), "blobFolder");

    /** Creates a Blob object for one Hashmap. */
    public Blob() {
        blobMap = new HashMap<>();
    }

    /** Get blob Hashmap.
     * @return HashMap */
    public HashMap<String, byte[]> getBlobMap() {
        return this.blobMap;
    }

    /** return the file as a string to be able to read.
     * @param sHA1 */
    public String fileAsString(String sHA1) {
        return new String(getBlobMap().get(sHA1));
    }

    /** Hashmap to retrieve BLOB from a SHA-1 value. */
    private HashMap<String, byte[]> blobMap;
}
