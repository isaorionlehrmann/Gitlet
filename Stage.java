package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;

/** Staging area object.
 *  @author Isa Orion Lehrmann
 */
public class Stage implements Serializable {

    /** Creating file for the staging area. */
    static final File STAGING_AREA = Utils.join(Main.getMainFolder(),
            "stageFolder");

    /** Creates a Stage object. */
    public Stage() {
        _addedMap = new HashMap<>();
        _removeArray = new ArrayList<>();
    }

    /** add to the staging area hashMap.
     * @param fileName file name*/
    public void add(String fileName) {
        Commit currCommit = Main.loadCurrCommit();
        HashMap<String, String> fileMap = currCommit.getMap();
        File fileCWD = Utils.join(Main.getCWD(), fileName);
        if (!fileCWD.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        byte[] forAddToBlobMap = Utils.readContents(fileCWD);

        String forCheck = new String(forAddToBlobMap);
        String forAddToBlobMap2 = Utils.readContentsAsString(fileCWD);

        String fileSha1 = Utils.sha1(forAddToBlobMap);
        if (_removeArray.contains(fileName)) {
            _removeArray.remove(fileName);
        } else {
            if (fileMap.containsKey(fileName)
                    && fileMap.get(fileName).equals(fileSha1)) {
                _addedMap.remove(fileName);
            } else {
                if (_addedMap.containsKey(fileName)) {
                    _addedMap.replace(fileName, fileSha1);
                } else {
                    _addedMap.put(fileName, fileSha1);
                }
                Blob forBlobMap = Main.loadBlob();
                HashMap<String, byte[]> blobMap = forBlobMap.getBlobMap();
                blobMap.put(fileSha1, forAddToBlobMap);
                Main.saveBlob(forBlobMap);
            }
        }
    }

    /** Remove from staging area hashmap, and
     * add to removeArray, and WD, if needed.
     * @param fileName the name of file*/
    public void rm(String fileName) {
        Commit currCommit = Main.loadCurrCommit();
        HashMap<String, String> commitMapNameSha = currCommit.getMap();
        HashMap<String, String> addedMap1 = getAddedMap();
        if (!commitMapNameSha.containsKey(fileName)
                && !addedMap1.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (addedMap1.containsKey(fileName)) {
            addedMap1.remove(fileName);
        }
        if (commitMapNameSha.containsKey(fileName)) {
            ArrayList<String> removeArray1 = getRemoveArray();
            if (!removeArray1.contains(fileName)) {
                getRemoveArray().add(fileName);
            }
            File fileCWD = Utils.join(Main.getCWD(), fileName);
            if (fileCWD.exists()) {
                Utils.restrictedDelete(fileCWD);
            }
        }
    }

    /** Retrieving map in order to add to new commit.
     * @return HashMap */
    public HashMap<String, String> getAddedMap() {
        return _addedMap;
    }

    /** Retrieving remove array in order to remove for next commit.
     * @return ArrayList */
    public ArrayList<String> getRemoveArray() {
        return _removeArray;
    }

    /** map from file name to SHA-1 to be able to easily compare files. */
    private HashMap<String, String> _addedMap;

    /** array list for the files to be removed if in currCommit. */
    private ArrayList<String> _removeArray;
}
