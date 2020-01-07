package gitlet;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;

/** Class to for Commit objects.
 *  @author Isa Orion Lehrmann
 */
public class Commit implements Serializable {

    /** init a Commit object.
     *
     * @param message message
     * @param info Stage object
     */
    public Commit(String message, Stage info) {
        this(info);
        _message = message;
        if (info == null) {
            _id = Utils.sha1(_time.toString(), _message);
        } else {
            _id = Utils.sha1(_message, _time.toString(),
                    _fileMap.toString(), _parent1SHA);
        }
        String shortCutId = _id.substring(0, 8);
        LongIDShortcut forAddLIDS = Main.loadLongIDS();
        forAddLIDS.getIDMap().put(shortCutId, _id);
        Main.saveLongIDS(forAddLIDS);
    }

    /** helper function for initializing.
     * @param info Stage object */
    public Commit(Stage info) {
        _fileMap = new HashMap<>();
        if (info == null) {
            String date = "Wed Dec 31 16:00:00 1969 -0800";

            SimpleDateFormat dt = new SimpleDateFormat(
                    "EEE MMM d HH:mm:ss yyyy Z");
            Date d = null;
            try {
                d = dt.parse(date);
            } catch (ParseException p) {
                System.out.println(p.getMessage());
            }
            _time = d;
            _parent1 = null;
            _parent2 = null;
        } else {
            HashMap<String, String> infoAdd = info.getAddedMap();
            ArrayList<String> infoRemove = info.getRemoveArray();
            if (infoAdd.isEmpty() && infoRemove.isEmpty()) {
                System.out.println("No changes added to the commit.");
                System.exit(0);
            }
            _time = new Date();
            Commit currCommit = Main.loadCurrCommit();
            HashMap<String, String> currFileMap = currCommit.getMap();
            for (Map.Entry mapElement : currFileMap.entrySet()) {
                String key = (String) mapElement.getKey();
                if (!infoAdd.containsKey(key) && !infoRemove.contains(key)) {
                    String value = (String) mapElement.getValue();
                    _fileMap.put(key, value);
                }
            }
            for (Map.Entry mapElement2 : infoAdd.entrySet()) {
                String key = (String) mapElement2.getKey();
                String value = (String) mapElement2.getValue();
                _fileMap.put(key, value);
            }
            _parent1 = currCommit;
            _parent1SHA = currCommit.getID();
            _parent2 = null;
            _parent2SHA = null;
            infoAdd.clear();
            infoRemove.clear();
            Main.saveStagingArea(info);
        }
    }

    /** Commits SNAPSHOT with MESSAGE and parents PAR1 PAR2.
     * Only for merge commit.*/
    public Commit(Stage snapshot, String message, String par1, String par2) {
        this(message, snapshot );
        merged = true;
        parents = "Merge: " + par1 + " " + par2;

    }

    /** function to get
     * Hashmap for the file pointers.
     * @return HashMap<String, String>*/
    public HashMap<String, String> getMap() {
        return this._fileMap;
    }

    /** function to get the sha1.
     * @return String */
    public String getID() {
        return this._id;
    }

    /** function to get the message.
     * @return String */
    public String getMessage() {
        return this._message;
    }

    /** function to get the parent commit.
     * @return Commit */
    public Commit getParent1() {
        if (_parent1 == null) {
            _parent1 = Main.loadAllCommits().getCommitMap().get(_parent1SHA);
        }
        return _parent1;
    }

    /** returns the sha1 of the paretn1. */
    public String getParent1SHA1() {
        return _parent1SHA;
    }


    /** SHA-1 ID of this commit. */
    private String _id;

    /** Commit message of the commit. */
    private String _message;

    /** Time of the commit. */
    private Date _time;

    /** the commit of parent1. */
    private transient Commit _parent1;

    /** sha1 of the parent1. */
    private String _parent1SHA;

    /** the commit of parent2. */
    private transient Commit _parent2;

    /** sha1 of the parent2. */
    private String _parent2SHA;

    /** Map for file to SHA-1 value for this commit. */
    private HashMap<String, String> _fileMap;

    /**True if Commit is a merge.*/
    private boolean merged = false;

    /** Merge parent IDs.*/
    private String parents;


    @Override
    /** to string function for log. */
    public String toString() {
        StringBuilder forLog = new StringBuilder();
        forLog.append("=== \n");
        forLog.append("commit " + _id + " \n");
        if (merged) {
            forLog.append(parents + "\n");
        }
        SimpleDateFormat dt = new SimpleDateFormat(
                "EEE MMM d HH:mm:ss yyyy Z");
        String timeStamp = dt.format(_time);
        forLog.append("Date: " + timeStamp + " \n");
        forLog.append(_message + " \n");
        return forLog.toString();
    }
}
