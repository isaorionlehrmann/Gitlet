package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;


/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Isa Orion Lehrmann
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter a command.");
                System.exit(0);
            }
            new Main(args);
            return;
        } catch (GitletException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(0);
    }

    /** Main method for version control system.
     * takes in the arguments and manipulates.
     * directories.
     *
     * @param args String[]
     */
    Main(String[] args) {
        if (args[0].equals("init")) {
            if (args.length > 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            init();
            return;
        }
        if (!getMainFolder().isDirectory()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
        if (!checkArg0(args[0])) {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        if (args.length == 1) {
            if (args[0].equals("log")) {
                log();
            } else if (args[0].equals("global-log")) {
                globalLog();
            } else if (args[0].equals("status")) {
                status();
            } else {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        } else if (args.length == 2) {
            if (args[0].equals("add")) {
                add(args[1]);
            } else if (args[0].equals("commit")) {
                commit(args[1], 2);
            } else if (args[0].equals("rm")) {
                remove(args[1]);
            } else if (args[0].equals("find")) {
                find(args[1]);
            } else if (args[0].equals("checkout")) {
                branchCheckout(args[1]);
            } else if (args[0].equals("branch")) {
                branch(args[1]);
            } else if (args[0].equals("rm-branch")) {
                removeBranch(args[1]);
            } else if (args[0].equals("reset")) {
                reset(args[1]);
            } else if (args[0].equals("merge")) {
                merge(args[1]);
            } else {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        } else {
            main2(args);
        }
    }

    /** helper function for init, when length > 2.
     * @param args String Array */
    public void main2(String[] args) {
        if (args.length <= 4) {
            if (!args[0].equals("checkout")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            if (args.length == 3) {
                if (!args[1].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                fileCheckout(args[2], loadCurrCommit());
            } else {
                if (!args[2].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                fileCommitCheckout(args[1], args[3]);
            }
        } else if (args.length > 4) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** checking if the arg0 is a valid one.
     * @return Boolean
     * @param arg0 String */
    public Boolean checkArg0(String arg0) {
        return allArguments.contains(arg0);
    }


    /** setting up all the folders in .gitlet to store info. */
    public static void setupPersistence() {
        if (!Stage.STAGING_AREA.isDirectory()) {
            Stage.STAGING_AREA.mkdirs();
        }
        if (!AllBranches.BRANCHES.isDirectory()) {
            AllBranches.BRANCHES.mkdirs();
        }
        if (!Blob.BLOB.isDirectory()) {
            Blob.BLOB.mkdirs();
        }
        if (!AllCommits.ALLCOMMITS.isDirectory()) {
            AllCommits.ALLCOMMITS.mkdirs();
        }
        if (!LongIDShortcut.IDSHORTCUT.isDirectory()) {
            LongIDShortcut.IDSHORTCUT.mkdirs();
        }
    }

    /** To load the stage area object to manipulate it.
     * @return Stage */
    public static Stage loadStagingArea() {
        File stageAreaFile = Utils.join(Stage.STAGING_AREA,
                "stagingArea");
        return Utils.readObject(stageAreaFile, Stage.class);
    }

    /** Reserialize the staging area for later use.
     * @param stagingArea Stage */
    public static void saveStagingArea(Stage stagingArea) {
        Utils.writeObject(Utils.join(Stage.STAGING_AREA,
                "stagingArea"), stagingArea);
    }

    /** To load the Blob HashMap to add to it, and retrieve.
     * @return Blob*/
    public static Blob loadBlob() {
        return Utils.readObject(Utils.join(Blob.BLOB,
                "blobMap"), Blob.class);
    }

    /** Reserialize the Blob for later use.
     * @param blob Blob */
    public static void saveBlob(Blob blob) {
        Utils.writeObject(Utils.join(blob.BLOB,
                "blobMap"), blob);
    }

    /** To load the allCommits HashMap to add to it, and retrieve.
     * @return AllCommits*/
    public static AllCommits loadAllCommits() {
        File allCommitsFile = Utils.join(AllCommits.ALLCOMMITS,
                "commitMap");
        return Utils.readObject(allCommitsFile, AllCommits.class);
    }

    /** Reserialize the allCommits for later use.
     * @param allCommitsObject All Commits */
    public static void saveAllCommits(AllCommits allCommitsObject) {
        Utils.writeObject(Utils.join(allCommitsObject.ALLCOMMITS,
                "commitMap"), allCommitsObject);
    }

    /** To load the allCommits HashMap to add to it, and retrieve.
     * @return AllBranches */
    public static AllBranches loadAllBranches() {
        File allBranchesFile = Utils.join(AllBranches.BRANCHES,
                "branchMap");
        return Utils.readObject(allBranchesFile, AllBranches.class);
    }

    /** Reserialize the allCommits for later use.
     * @param allBranchesObject All branches */
    public static void saveAllBranches(AllBranches allBranchesObject) {
        Utils.writeObject(Utils.join(allBranchesObject.BRANCHES,
                "branchMap"), allBranchesObject);
    }

    /** To load current commit.
     * @return Commit */
    public static Commit loadCurrCommit() {
        File currCommitsFile = Utils.join(getMainFolder(),
                "currentCommit");
        return Utils.readObject(currCommitsFile, Commit.class);
    }

    /** To load current branch name.
     * @return CurrBranchName */
    public static CurrBranchName loadCurrBranchName() {
        File currBranchName = Utils.join(getMainFolder(),
                "currentBranch");
        return Utils.readObject(currBranchName, CurrBranchName.class);
    }

    /** To load the ID HashMap to add to it, and retrieve.
     * @return LongIDShortcut */
    public static LongIDShortcut loadLongIDS() {
        File idShortcutFile = Utils.join(LongIDShortcut.IDSHORTCUT,
                "IDMap");
        return Utils.readObject(idShortcutFile, LongIDShortcut.class);
    }

    /** Reserialize the ID Hashmap Object for later use.
     * @param longIDShortObject object*/
    public static void saveLongIDS(LongIDShortcut longIDShortObject) {
        Utils.writeObject(Utils.join(longIDShortObject.IDSHORTCUT,
                "IDMap"), longIDShortObject);
    }




    /** for initializing the .gitlet directory. */
    private void init() {
        if (getMainFolder().isDirectory()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            System.exit(0);
        }
        getMainFolder().mkdir();
        setupPersistence();
        Utils.writeObject(Utils.join(Stage.STAGING_AREA,
                "stagingArea"), new Stage());
        Utils.writeObject(Utils.join(Blob.BLOB,
                "blobMap"), new Blob());
        Utils.writeObject(Utils.join(AllCommits.ALLCOMMITS,
                "commitMap"), new AllCommits());
        Utils.writeObject(Utils.join(LongIDShortcut.IDSHORTCUT,
                "IDMap"), new LongIDShortcut());
        Blob checkBlobObject = Utils.readObject(Utils.join(Blob.BLOB,
                "blobMap"), Blob.class);
        HashMap<String, byte[]> checkBlob = checkBlobObject.getBlobMap();
        Utils.writeObject(Utils.join(AllBranches.BRANCHES,
                "branchMap"), new AllBranches());
        String message = "initial commit";
        String branchName = "master";
        commit(message, 1);
        Utils.writeObject(Utils.join(getMainFolder(),
                "currentBranch"), new CurrBranchName(branchName));
        new Branch(branchName, loadCurrCommit());
    }

    /** Add to the staging area.
     * @param fileName String */
    public void add(String fileName) {
        Stage stagingObject = loadStagingArea();
        stagingObject.add(fileName);
        saveStagingArea(stagingObject);
    }

    /** Remove from the staging area, commit, and WD.
     * @param fileName String */
    public void remove(String fileName) {
        Stage stagingObject = loadStagingArea();
        stagingObject.rm(fileName);
        saveStagingArea(stagingObject);
    }

    /** Deletes the branch with the given name, only removes pointer.
     * @param branchName String*/
    public void removeBranch(String branchName) {
        AllBranches allBranches = loadAllBranches();
        HashMap<String, Branch> branchMap = allBranches.getBranchMap();
        if (!branchMap.containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (loadCurrBranchName().getCurrBranchName().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branchMap.remove(branchName);
        saveAllBranches(allBranches);
    }


     /** Create a new commit, assign to currCommit, and add to branch.
      *
      * @param message message
      * @param key for help
      */
    public void commit(String message, Integer key) {
        if (message.length() == 0) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        Commit newCommit;
        if (key == 1) {
            newCommit = new Commit(message, null);
        } else {
            newCommit = new Commit(message, loadStagingArea());
        }
        Utils.writeObject(Utils.join(getMainFolder(),
                "currentCommit"), newCommit);
        AllCommits allCommitsToAdd = Main.loadAllCommits();
        allCommitsToAdd.getCommitMap().put(newCommit.getID(), newCommit);
        Main.saveAllCommits(allCommitsToAdd);
        if (newCommit.getParent1SHA1() == null) {
            return;
        }
        String currBranchNameUpdate = Main.loadCurrBranchName()
                .getCurrBranchName();
        AllBranches allBranchesToAdd = Main.loadAllBranches();
        allBranchesToAdd.getBranchMap().get(currBranchNameUpdate)
                .updateHeadCommit(newCommit);
        Main.saveAllBranches(allBranchesToAdd);
    }

    /** Creates a new branch with the given name,
     * points it to current head.
     * @param branchName String */
    public void branch(String branchName) {
        if (loadAllBranches().getBranchMap().containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
        }
        new Branch(branchName, loadCurrCommit());
    }

    /** Checkout to a specific branch. Replace WD files.
     * @param branchName String */
    public void branchCheckout(String branchName) {
        HashMap<String, Commit> allCommits =
                loadAllCommits().getCommitMap();
        HashMap<String, Branch> allBranches =
                loadAllBranches().getBranchMap();
        if (!allBranches.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Branch branchForCheckout = allBranches.get(branchName);
        Commit checkoutBranchHead =
                allCommits.get(branchForCheckout.getHead());
        HashMap<String, String> checkBranchHeadMap =
                checkoutBranchHead.getMap();
        String currBranchName = loadCurrBranchName().getCurrBranchName();
        Branch currBranch = allBranches.get(currBranchName);
        Commit currCommit = loadCurrCommit();
        HashMap<String, String> currCommitMap = currCommit.getMap();
        if (currBranchName.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        for (Map.Entry forError : checkBranchHeadMap.entrySet()) {
            String key = (String) forError.getKey();
            String value = (String) forError.getValue();
            if (!currCommitMap.containsKey(key)) {
                if (Utils.join(getCWD(), key).isFile()) {
                    System.out.println("There is an untracked "
                            + "file in the way; delete it or add it first.");
                    System.exit(0);
                }
            }
        }
        HashMap<String, byte[]> blobMap = loadBlob().getBlobMap();
        for (Map.Entry forWrite : checkBranchHeadMap.entrySet()) {
            String key = (String) forWrite.getKey();
            String value = (String) forWrite.getValue();
            Utils.writeContents(Utils.join(getCWD(), key), blobMap.get(value));
        }
        for (Map.Entry forDelete : currCommitMap.entrySet()) {
            String key = (String) forDelete.getKey();
            String value = (String) forDelete.getValue();
            if (!checkBranchHeadMap.containsKey(key)) {
                Utils.restrictedDelete(Utils.join(getCWD(), key));
            }
        }
        Utils.writeObject(Utils.join(getMainFolder(),
                "currentBranch"), new CurrBranchName(branchName));
        Utils.writeObject(Utils.join(getMainFolder(),
                "currentCommit"), checkoutBranchHead);
        Stage forClearStage = loadStagingArea();
        forClearStage.getAddedMap().clear();
        forClearStage.getRemoveArray().clear();
        saveStagingArea(forClearStage);
    }

    /** Overwrites WD file if it exists, from currCommit.
     *
     * @param fileName name of the file
     * @param currCommit current commit
     */
    public void fileCheckout(String fileName, Commit currCommit) {
        File fileCWD = Utils.join(getCWD(), fileName);
        HashMap<String, String> currCommitMap = currCommit.getMap();
        if (!currCommitMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        String currCommitSHA1 = currCommitMap.get(fileName);
        if (fileCWD.exists()) {
            byte[] byteArrayFile = Utils.readContents(fileCWD);
            String cwdSHA1 = Utils.sha1(byteArrayFile);
            String penis = new String(byteArrayFile);
            if (currCommitSHA1 == cwdSHA1) {
                return;
            }
        }
        byte[] commitBlob = loadBlob().getBlobMap().get(currCommitSHA1);
        String commitPenis = new String(commitBlob);
        Utils.writeContents(fileCWD, commitBlob);
    }

    /** Overwrites the wWD file if it exists, from the given Commit.
     *
     * @param commitID sha1 of commit
     * @param fileName file name
     */
    public void fileCommitCheckout(String commitID, String fileName) {
        if (commitID.length() > 8) {
            HashMap<String, Commit> allCommits =
                    loadAllCommits().getCommitMap();
            if (!allCommits.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            Commit commitForCheckout2 = allCommits.get(commitID);
            fileCheckout(fileName, commitForCheckout2);
        } else if (commitID.length() == 8) {
            HashMap<String, String> idMap = loadLongIDS().getIDMap();
            if (!idMap.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            String sha1CheckoutCommit = idMap.get(commitID);
            Commit commitForCheckout =
                    loadAllCommits().getCommitMap().get(sha1CheckoutCommit);
            fileCheckout(fileName, commitForCheckout);
        }
    }

    /** Checks out all the files of the given commit,
     * and moves current branch head here.
     * @param commitID String */
    public void reset(String commitID) {
        Commit commitForReset;
        if (commitID.length() == 6) {
            HashMap<String, String> idMap = loadLongIDS().getIDMap();
            if (!idMap.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            String sha1CheckoutCommit = idMap.get(commitID);
            commitForReset = loadAllCommits().getCommitMap()
                    .get(sha1CheckoutCommit);
        } else {
            HashMap<String, Commit> allCommits =
                    loadAllCommits().getCommitMap();
            if (!allCommits.containsKey(commitID)) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            commitForReset = allCommits.get(commitID);
        }
        AllBranches toManipulateOneBranch = loadAllBranches();
        HashMap<String, Branch> allBranches =
                toManipulateOneBranch.getBranchMap();
        HashMap<String, String> commitForResetMap = commitForReset.getMap();
        String currBranchName = loadCurrBranchName().getCurrBranchName();
        Branch currBranch = allBranches.get(currBranchName);
        Commit currCommit = loadCurrCommit();
        HashMap<String, String> currCommitMap = currCommit.getMap();
        for (Map.Entry forError : commitForResetMap.entrySet()) {
            String key = (String) forError.getKey();
            if (!currCommitMap.containsKey(key)) {
                if (Utils.join(getCWD(), key).isFile()) {
                    System.out.println("There is an untracked file "
                            + "in the way; delete it or add it first.");
                    System.exit(0);
                }
            }
        }
        HashMap<String, byte[]> blobMap = loadBlob().getBlobMap();
        for (Map.Entry forWrite : commitForResetMap.entrySet()) {
            String key = (String) forWrite.getKey();
            String value = (String) forWrite.getValue();
            Utils.writeContents(Utils.join(getCWD(), key),
                    blobMap.get(value));
        }
        for (Map.Entry forDelete : currCommitMap.entrySet()) {
            String key = (String) forDelete.getKey();
            if (!commitForResetMap.containsKey(key)) {
                Utils.restrictedDelete(Utils.join(getCWD(), key));
            }
        }
        currBranch.updateHeadCommit(commitForReset);
        saveAllBranches(toManipulateOneBranch);
        Utils.writeObject(Utils.join(getMainFolder(),
                "currentCommit"), commitForReset);
        Stage forClearStage = loadStagingArea();
        forClearStage.getAddedMap().clear();
        forClearStage.getRemoveArray().clear();
        saveStagingArea(forClearStage);
    }

    /** Merge Function.
     * @param mergeBranchName String */
    public void merge(String mergeBranchName) {
        loadAllBranches();
//        if (splitpoint(currentBranc, mergeBranchName) == mergeBranchName) {
//            System.out.println("Given branch is an ancestor of the current branch.");
//            return;
//        }
    }

    /** Display the logs of the current commit and history. */
    public void log() {
        Commit currCommit = loadCurrCommit();
        System.out.println(currCommit.toString());
        String sha1Parent = currCommit.getParent1SHA1();
        if (sha1Parent != null) {
            HashMap<String, Commit> allCommitsForLog
                    = loadAllCommits().getCommitMap();
            while (sha1Parent != null) {
                currCommit = allCommitsForLog.get(sha1Parent);
                System.out.println(currCommit.toString());
                sha1Parent = currCommit.getParent1SHA1();
            }
        }
    }

    /** Displays the logs of all Commits ever. */
    public void globalLog() {
        AllCommits forGlobalLog = loadAllCommits();
        HashMap<String, Commit> toIterate = forGlobalLog.getCommitMap();
        for (Map.Entry forLog : toIterate.entrySet()) {
            String key = (String) forLog.getKey();
            Commit value = (Commit) forLog.getValue();
            System.out.println(value.toString());
        }
    }

    /** Finds the commit IDs of all the commits with this message.
     * @param message String */
    public void find(String message) {
        AllCommits forFindMessage = loadAllCommits();
        HashMap<String, Commit> toIterate = forFindMessage.getCommitMap();
        Boolean check = false;
        for (Map.Entry forLog : toIterate.entrySet()) {
            String key = (String) forLog.getKey();
            Commit value = (Commit) forLog.getValue();
            if (value.getMessage().equals(message)) {
                check = true;
                System.out.println(value.getID());
            }
        }
        if (!check) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** For displaying Branches and staging area, maybe WD files too. */
    public void status() {
        ArrayList<String> listOfBranchNames = new ArrayList<>();
        HashMap<String, Branch> mapBranchesStatus
                = loadAllBranches().getBranchMap();
        for (Map.Entry branchForLog : mapBranchesStatus.entrySet()) {
            String key = (String) branchForLog.getKey();
            listOfBranchNames.add(key);
        }
        java.util.Collections.sort(listOfBranchNames);
        ArrayList<String> listOfStaged = new ArrayList<>();
        Stage stagingObject = loadStagingArea();
        HashMap<String, String> stageMap = stagingObject.getAddedMap();
        for (Map.Entry stageForLog : stageMap.entrySet()) {
            String key = (String) stageForLog.getKey();
            listOfStaged.add(key);
        }
        java.util.Collections.sort(listOfStaged);
        ArrayList<String> removeArray = stagingObject.getRemoveArray();
        java.util.Collections.sort(removeArray);
        StringBuilder forStatus = new StringBuilder();
        forStatus.append("=== Branches === \n");
        String currBranchName = loadCurrBranchName().getCurrBranchName();
        for (int i = 0; i < listOfBranchNames.size(); i += 1) {
            String branchNameIndex = listOfBranchNames.get(i);
            if (branchNameIndex.equals(currBranchName)) {
                forStatus.append("*");
            }
            forStatus.append(branchNameIndex + "\n");
        }
        forStatus.append("\n");
        forStatus.append("=== Staged Files === \n");
        for (int i2 = 0; i2 < listOfStaged.size(); i2 += 1) {
            String stagedIndex = listOfStaged.get(i2);
            forStatus.append(stagedIndex + "\n");
        }
        forStatus.append("\n");
        forStatus.append("=== Removed Files === \n");
        for (int i3 = 0; i3 < removeArray.size(); i3 += 1) {
            String removeIndex = removeArray.get(i3);
            forStatus.append(removeIndex + "\n");
        }
        forStatus.append("\n");
        ArrayList<String> modified = modified();
        forStatus.append("=== Modifications Not Staged For Commit "
                + "=== \n");
        for (int i4 = 0; i4 < modified.size(); i4 += 1) {
            String removeIndex = modified.get(i4);
            forStatus.append(removeIndex + "\n");
        }
        forStatus.append("\n");
        forStatus.append("=== Untracked Files === \n");
        ArrayList<String> untracked = untracked();
        for (int i5 = 0; i5 < untracked.size(); i5 += 1) {
            String removeIndex = untracked.get(i5);
            forStatus.append(removeIndex + "\n");
        }
        forStatus.append("\n");
        String resultantString = forStatus.toString();
        System.out.println(resultantString);
    }

    /** Find the files that are modified but not staged.
     * .txt (deleted) or .txt (modified)
     * @return ArrayList */
    public ArrayList<String> modified() {
        ArrayList<String> forReturn = new ArrayList<>();
        Commit currCommit = loadCurrCommit();
        HashMap<String, String> currCommitFiles = currCommit.getMap();
        Stage stageForCheck = loadStagingArea();
        HashMap<String, String> stagedHashMap = stageForCheck.getAddedMap();
        ArrayList<String> markedRemovalArray = stageForCheck.getRemoveArray();
        for (Map.Entry stageForLog : currCommitFiles.entrySet()) {
            String key = (String) stageForLog.getKey();
            String value = (String) stageForLog.getValue();
            if (!Utils.join(getCWD(), key).exists()) {
                if (!markedRemovalArray.contains(key)) {
                    forReturn.add(key + "(deleted)");
                }
            } else if (Utils.join(getCWD(), key).exists()) {
                String fileWDSHA1 = Utils.sha1(Utils.readContents
                        (Utils.join(getCWD(), key)));
                if (!value.equals(fileWDSHA1)) {
                    if (!stagedHashMap.containsKey(key)) {
                        forReturn.add(key + "(modified");
                    }
                }
            }
        }
        for (Map.Entry stageForLog1 : stagedHashMap.entrySet()) {
            String key = (String) stageForLog1.getKey();
            String value = (String) stageForLog1.getValue();
            if (!Utils.join(getCWD(), key).exists()) {
                forReturn.add(key + "(deleted)");
            }
            String fileWDSHA1 = Utils.sha1(Utils.readContents
                    (Utils.join(getCWD(), key)));
            if (!value.equals(fileWDSHA1)) {
                forReturn.add(key + "(modified");
            }
        }
        java.util.Collections.sort(forReturn);
        return forReturn;
    }

    /** Find the files that are untracked.
     * @return ArrayList */
    public ArrayList<String> untracked() {
        ArrayList<String> forReturn = new ArrayList<>();
        Commit currCommit = loadCurrCommit();
        HashMap<String, String> currCommitFiles = currCommit.getMap();
        Stage stageForCheck = loadStagingArea();
        HashMap<String, String> stagedHashMap = stageForCheck.getAddedMap();
        ArrayList<String> markedRemovalArray = stageForCheck.getRemoveArray();

        for (int i = 0; i < markedRemovalArray.size(); i += 1) {
            String currFile = markedRemovalArray.get(i);
            if (Utils.join(getCWD(), currFile).exists()) {
                forReturn.add(currFile);
            }
        }
        List<String> cwdFiles = Utils.plainFilenamesIn(getCWD());
        for (int i2 = 0; i2 < cwdFiles.size(); i2 += 1) {
            String currFile2 = cwdFiles.get(i2);
            if (!stagedHashMap.containsKey(currFile2)
                    && !currCommitFiles.containsKey(currFile2)) {
                forReturn.add(currFile2);
            }
        }
        java.util.Collections.sort(forReturn);
        return forReturn;
    }

    /** retrieve CWD File.
     * @return File */
    public static File getCWD() {
        return cWD;
    }

    /** retrieve MAIN_FOLDER.
     * @return File */
    public static File getMainFolder() {
        return mAINFOLDER;
    }


    /** Current Working Directory. */
    private static File cWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder to hold commits, stages, and branches. */
    private static File mAINFOLDER = Utils.join(getCWD(),
            ".gitlet");

    /** all possible commands list. */
    private List<String> allArguments = List.of("log", "global-log",
            "status", "add", "commit",
            "rm", "find", "checkout", "branch",
            "rm-branch", "reset", "merge");
}
