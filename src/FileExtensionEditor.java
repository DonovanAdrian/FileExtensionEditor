import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
Copyright Donovan Adrian 2020, FEE ver 1.2

Welcome to the source code! This program is a formal attempt to create a program
that is highly usable while also utilizing global variables. The main goal was to
maximize readability and fixability by separating functions into meaningful chunks.
The best way to view this program (In my opinion) is in an IDE where you can
willfully collapse blocks of code as needed. This is especially helpful for debugging
in which you can collapse blocks of code that you know are valid and correct.
A program map or data tree is not available for this, but can be created upon request.
Additional sources that were utilized in the making of this program are detailed
below. Special thanks to everyone in the following forums and websites!

Sources:
https://www.rgagnon.com/javadetails/java-0370.html
https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
https://mkyong.com/swing/java-swing-joptionpane-showinputdialog-example/
https://mkyong.com/java/how-to-create-directory-in-java/
https://stackoverflow.com/questions/16433915/how-to-copy-file-from-one-location-to-another-location
https://knpcode.com/java-programs/how-to-make-file-folder-hidden-java/
https://knpcode.com/java-programs/renaming-a-file-using-java-program/
https://mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
https://www.geeksforgeeks.org/split-string-java-examples/
https://stackoverflow.com/questions/15875295/open-a-folder-in-explorer-using-java
https://stackoverflow.com/questions/8916064/how-to-add-a-progress-bar
https://stackoverflow.com/questions/31253896/need-to-add-text-to-jframe/31254778
https://examples.javacodegeeks.com/desktop-java/swing/jframe/center-jframe-jwindow-jdialog-on-screen/
https://stackoverflow.com/questions/2885173/how-do-i-create-a-file-and-write-to-it-in-java
https://www.programiz.com/java-programming/examples/current-working-directory
https://stackoverflow.com/questions/24104313/how-do-i-make-a-delay-in-java
https://stackoverflow.com/questions/26678539/how-can-i-move-a-file-to-a-non-empty-directory/26679752

And last, but most certainly NOT least:
https://www.jetbrains.com/help/idea/packaging-a-module-into-a-jar-file.html
Because I don't know this IDE like the palm of my hand... lol

------------------------------------------------------------------------------------------------------------
ToDo:
-Work With Replacement Of Files That May Already Exist/Appending
    -If Exist, Combine Or Replace?

Extra Things ToDo:
-Change the extension chooser into a list or a list of radios
    -Find a way to select multiple items in a list
    -Maybe This????
        https://stackoverflow.com/questions/15687201/how-to-add-items-to-jlist-from-another-window
-Work with parallelism...
    https://mkyong.com/java8/java-8-parallel-streams-examples/
        -What may need to be done is a completely separate function that uses a predetermined # of files (100? 128? 1000?)
        and splits up processing accordingly. This should help speed up processing so long as it doesn't trip over itself.
            -Make it so that it reads from a list, from unreadFiles to readFiles. Since we are only dealing with fileNames,
            fileSize shouldn't matter.
------------------------------------------------------------------------------------------------------------
 */

public class FileExtensionEditor {
    private static ProgressWindow prgWin = new ProgressWindow();
    private static JFileChooser fileChooser;
    private static File inputFolder;
    private static File backupFolder;
    private static String OS = "";
    private static boolean initialDisplayBool = true;
    private static boolean validDirBool = false;
    private static boolean backupExists = true;
    private static ArrayList<File> validFiles;
    private static ArrayList<String> validFileNames;
    private static ArrayList<File> backedUpFiles;
    private static ArrayList<String> backedUpFileNames;
    private static ArrayList<File> filesToEdit;
    private static ArrayList<File> completedFiles;
    private static ArrayList<File> backupFailedFiles;
    private static ArrayList<File> renameFailedFiles;
    private static ArrayList<File> restoreFailedFiles;
    private static ArrayList<File> deleteFailedFiles;
    private static ArrayList<String> existingExtensions;
    private static ArrayList<String> usedExtensions;
    private static String usedExtensionsStr;
    private static String existingExtensionsStr;
    private static int userChoice = 0;
    private static int finalUserChoice = 0;
    private static int validDirectory = 0;
    private static int ignoredDirs = 0;
    private static int ignoredFiles = 0;
    private static int foundExtensions = 0;
    private static String userInput = "";
    private static String wbListExtension = "";
    private static boolean userConfirmBool = true;
    private static boolean userExtensionInputBool = true;

    //---------------------------------------------------------------------------------**************
    //Main Function
    //---------------------------------------------------------------------------------**************

    public static void main(String[] args) {
        while(true) {
            if (initialDisplayBool) {
                System.out.println("Initializing...");
                init();

                System.out.println("Displaying Welcome Mat...");
                displayInitialWindow();
            }

            while (!validDirBool) {
                System.out.println("Displaying Directory Prompt...");
                promptDir();

                System.out.println("Probing Directory...");
                probeDir();
            }

            System.out.println("Displaying User Option Prompt...");
            promptUser();

            while(userExtensionInputBool) {
                System.out.println("Displaying User Input Prompts...");
                if (userChoice == 2) {
                    promptUserExtension(
                            "whitelist.\n For multiple extensions, separate each input with commas.",
                            false);
                    System.out.println("The following extensions will be changed: " + userInput);
                    if (userConfirmBool) {
                        System.out.println("Performing Whitelist File Operations");
                        whiteListOperations();
                        userExtensionInputBool = false;
                    }
                } else if (userChoice == 1) {
                    promptUserExtension(
                            "blacklist.\n For multiple extensions, separate each input with commas.",
                            false);
                    System.out.println("The following extensions will be ignored: " + userInput);
                    if (userConfirmBool) {
                        System.out.println("Performing Blacklist File Operations");
                        blackListOperations();
                        userExtensionInputBool = false;
                    }
                } else if (userChoice == 0) {
                    promptUserExtension("change all your files to.", true);
                    System.out.println("All files in directory will be changed to " + userInput);
                    if (userConfirmBool) {
                        System.out.println("Performing All File Operations");
                        allFileOperations();
                        userExtensionInputBool = false;
                    }
                } else {
                    System.out.println("Well... That wasn't supposed to happen! Exiting for your safety (and mine)...");
                    formalExit();
                }
            }

            if (userConfirmBool) {
                System.out.println("Opening Edited Directory...");
                openCompleteFolder();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie){
                    //Ignore
                }
                System.out.println("Displaying Final Window...");
                displayCompletionWindow();
            }

            deleteBackup();
            System.out.println("Resetting Backend Variables...");
            userConfirmBool = true;
            userExtensionInputBool = true;
            validDirBool = false;
            backupExists = true;
            validFiles = new ArrayList<>();
            validFileNames = new ArrayList<>();
            filesToEdit = new ArrayList<>();
            backedUpFiles = new ArrayList<>();
            backedUpFileNames = new ArrayList<>();
            completedFiles = new ArrayList<>();
            backupFailedFiles = new ArrayList<>();
            renameFailedFiles = new ArrayList<>();
            restoreFailedFiles = new ArrayList<>();
            deleteFailedFiles = new ArrayList<>();
            existingExtensions = new ArrayList<>();
            usedExtensions = new ArrayList<>();
            usedExtensionsStr = "";
            existingExtensionsStr = "";
            userChoice = 0;
            finalUserChoice = 0;
            validDirectory = 0;
            ignoredDirs = 0;
            ignoredFiles = 0;
            foundExtensions = 0;
            userInput = "";
            System.out.println("Clearing Backup Directory...");
            backupFolder = null;
            System.out.println("Clearing Input Directory...");
            inputFolder = null;
        }
    }

    //---------------------------------------------------------------------------------**************
    //Initialization
    //---------------------------------------------------------------------------------**************

    private static void init(){
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        OS = System.getProperty("os.name").toLowerCase();
        validFiles = new ArrayList<>();
        validFileNames = new ArrayList<>();
        filesToEdit = new ArrayList<>();
        backedUpFiles = new ArrayList<>();
        backedUpFileNames = new ArrayList<>();
        completedFiles = new ArrayList<>();
        backupFailedFiles = new ArrayList<>();
        renameFailedFiles = new ArrayList<>();
        restoreFailedFiles = new ArrayList<>();
        deleteFailedFiles = new ArrayList<>();
        existingExtensions = new ArrayList<>();
        usedExtensions = new ArrayList<>();
    }

    //---------------------------------------------------------------------------------**************
    //Welcome Mat
    //---------------------------------------------------------------------------------**************

    private static void displayInitialWindow(){
        if(initialDisplayBool){
            JOptionPane.showMessageDialog(null,
                    "Welcome to the File Extension Editor!\n\n" +
                             "In this program you can edit your files\n" +
                             "to match a specific file extension.\n" +
                             "To begin, click \"OK\"", "Welcome!", JOptionPane.PLAIN_MESSAGE);
            initialDisplayBool = false;
        }
    }

    //---------------------------------------------------------------------------------**************
    //Directory Selection, Dynamic True/False Prompt
    //---------------------------------------------------------------------------------**************

    private static void promptDir(){
        while (true){
            int returnVal = fileChooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputFolder = fileChooser.getSelectedFile();
                backupFolder = new File(inputFolder.getAbsolutePath() + "/feeBackup");

                System.out.println("The Following Directory Was Selected:\n"
                        + inputFolder.getAbsolutePath() + "\n");
                System.out.println("Backup Filepath Initialized:\n" +
                        backupFolder.getAbsolutePath() + "\n");
            }
            if (inputFolder == null) {
                if(!trueFalsePrompt("Do you wish to choose a directory to edit? If not,\n" +
                        "the program will close!", "Continue?")){
                    System.out.println("No File Chosen...");
                    if(trueFalsePrompt("Do you wish to exit?", "Exit?")){
                        System.out.println("User Chose To Exit.");
                        formalExit();
                    }
                }
            } else {
                break;
            }
        }
    }

    private static boolean trueFalsePrompt(String prompt, String title){
        while(true) {
            int userChoice = JOptionPane.showOptionDialog(null, prompt, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"No", "Yes"}, "default");
            if (userChoice == 1) {
                return true;
            } else if (userChoice != -1) {
                return false;
            }
        }
    }

    //---------------------------------------------------------------------------------**************
    //Directory Probing, Existing Extensions, File Name Validity
    //---------------------------------------------------------------------------------**************

    private static void probeDir(){
        try {
            for (final File fileEntry : inputFolder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    /*
                    This if/else block was meant to delete old backups, but since the folder is never hidden,
                    deleting the old backup shouldn't be necessary unless the user says so
                    (If an old backup exists, it should just throw an error when a new one is created)

                    if(fileEntry.getName().equals(".feeBackup")){
                        //System.out.println("An Old Backup Directory Has Been Found!");
                        if(deleteOldBackup(fileEntry)){
                            //System.out.println("Old Backup Directory Deleted!");
                        } else {
                            //System.out.println("Old Backup Directory Not Deleted");
                        }
                    } else {
                    */
                        ignoredDirs++;
                        //System.out.println("Ignoring Directory: " + fileEntry.getName());
                    //}
                } else if (!validityCheck(fileEntry.getName())) {
                    ignoredFiles++;
                    //System.out.println("Ignoring File: " + fileEntry.getName());
                } else {
                    validDirectory++;
                    validFiles.add(fileEntry);
                    validFileNames.add(fileEntry.getName());
                    spliceAddExtension(fileEntry.getName());
                    //System.out.println(fileEntry.getName());
                }
            }
            System.out.println("Found " + validDirectory + " valid files");
            System.out.println("Found " + foundExtensions + " valid extensions");
            System.out.println("Ignored " + ignoredFiles + " files");
            System.out.println("Ignored " + ignoredDirs + " directories");
            int totalFiles = validDirectory + ignoredFiles;
            System.out.println("Total Files: " + totalFiles);
            System.out.println();
        } catch (NullPointerException npe){
            System.out.println("The Input Folder Encountered An Error... Please Choose A Valid Folder Next Time!");
            JOptionPane.showMessageDialog(null,
                    inputFolder.getAbsolutePath() +
                            "\n\nThe directory you selected is not suitable. \n" +
                            "Please select another directory.\n" +
                            "Click \"OK\" to continue",
                    "Unsuitable Directory", JOptionPane.PLAIN_MESSAGE);
            inputFolder = null;
        }

        if(existingExtensions.size() != 0){
            for (int i = 0; i < existingExtensions.size(); i++){
                if(i == 0) {
                    existingExtensionsStr = existingExtensions.get(i);
                } else {
                    existingExtensionsStr = existingExtensions.get(i) + ", " + existingExtensionsStr;
                }
            }
        }

        if(validDirectory == 0){
            System.out.println("This is an unsuitable directory, please choose another directory");
            JOptionPane.showMessageDialog(null,
                    inputFolder.getAbsolutePath() +
                            "\n\nThe directory you selected is not suitable. \n" +
                            "Please select another directory.\n" +
                            "Click \"OK\" to continue",
                    "Unsuitable Directory", JOptionPane.PLAIN_MESSAGE);
            inputFolder = null;
        } else {
            validDirBool = true;
            createBackup();
        }
        System.out.println();
    }

    private static void spliceAddExtension(String fileName){
        boolean lastPeriod = false;
        String extensionCheck = "";

        for (int i = 0; i < fileName.length(); i++) {
            if(fileName.charAt(i) == '.'){
                if(i == 0) {
                }
                if(!lastPeriod) {
                    lastPeriod = true;
                } else {
                    lastPeriod = false;
                    extensionCheck = "";
                }
            }
            if(lastPeriod){
                if(fileName.charAt(i) != '.'){
                    extensionCheck = extensionCheck + fileName.charAt(i);
                }
            }
        }
        if(!existingExtensions.contains(extensionCheck)){
            if(!extensionCheck.equals("")) {
                existingExtensions.add(extensionCheck);
                foundExtensions++;
                //System.out.println("Added " + extensionCheck + " To List Of Extensions!");
            }
        }
    }

    private static boolean validityCheck(String fileName){
        boolean lastPeriod = false;
        boolean spaceAfterLast = false;
        int extensionFound = 0;
        String extensionCheck = "";

        for (int i = 0; i < fileName.length(); i++) {
            //check for file extensions that SHOULDN'T BE OVERRIDDEN. return false (.dll?)

            if(fileName.charAt(i) == '.'){
                extensionFound++;
                if(i == 0) {

                    return false;
                }
                if(!lastPeriod) {
                    lastPeriod = true;
                } else {
                    lastPeriod = false;
                    spaceAfterLast = false;
                    extensionCheck = "";
                }
            }
            if(lastPeriod){
                if(Character.isAlphabetic(fileName.charAt(i))){
                    extensionCheck = extensionCheck + fileName.charAt(i);
                    if(extensionCheck.contains("dll")){
                        return false;
                    }
                }
                if(Character.isSpaceChar(fileName.charAt(i))){
                    spaceAfterLast = true;
                }
            }
        }
        if(extensionFound == 0){
            return false;
        }
        if(spaceAfterLast){
            return false;
        }
        return true;
    }

    //---------------------------------------------------------------------------------**************
    //User Prompt - WhiteList/BlackList/All
    //---------------------------------------------------------------------------------**************

    private static void promptUser(){
        while(true) {
            userChoice = JOptionPane.showOptionDialog(null,
                    "Do you wish to whitelist files, blacklist files,\n" +
                            "or change all of your file extensions?",
                    "Choose An Option Below",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"All", "Blacklist", "Whitelist"},
                    null);
            if(userChoice != -1){
                System.out.println("User Chose Option #" + userChoice + "\n");
                if(foundExtensions == 1 && userChoice == 0) {
                    System.out.println("Break!");
                    break;
                } else if (foundExtensions == 1 && (userChoice == 1 || userChoice == 2)){
                    System.out.println("This Folder Only Has One Extension! The All Option Is Optimal!");
                    JOptionPane.showMessageDialog(null,
                            "This folder only has one extension, please choose the \"All\" option",
                            "Uh Oh!", JOptionPane.PLAIN_MESSAGE);
                }
            } else {
                if(!trueFalsePrompt("Do you wish to continue editing file extensions for the directory:\n" +
                        inputFolder.getAbsolutePath() + "\n\nDo you wish to continue?",
                        "Continue?")){
                    System.out.println("No Option Chosen...");
                    if(trueFalsePrompt("Do you wish to exit?", "Exit?")){
                        System.out.println("User Chose To Exit.");
                        formalExit();
                    }
                }
            }
        }
    }

    //---------------------------------------------------------------------------------**************
    //User Input - File Extensions, User Input Validity - File Extensions, Space Checker @ 0
    //---------------------------------------------------------------------------------**************

    private static void promptUserExtension(String extensionType, boolean confPrompt){
        String supplementalInputMsg = "The extension in this directory is:\n";
        if(existingExtensions.size() > 1){
            supplementalInputMsg = "The extensions in this directory are:\n";
        }

        while(true) {
            if(!confPrompt) {
                existingExtensions.addAll(usedExtensions);
                usedExtensions.clear();
            }
            userInput = "";
            userInput = JOptionPane.showInputDialog(null,
                    "Type a file extension that you would like to " + extensionType +
                            "\n\n" + supplementalInputMsg +
                            existingExtensionsStr,
                    "Input A File Extension",
                    JOptionPane.PLAIN_MESSAGE);
            if(userInput == null){
                if(!trueFalsePrompt("Do you wish to continue editing file extensions for the directory:\n" +
                                inputFolder.getAbsolutePath() + "\n\nDo you wish to continue?",
                        "Continue?")){
                    if(trueFalsePrompt("Do you wish to exit?", "Exit?")){
                        System.out.println("User Chose To Exit.");
                        formalExit();
                    } else {
                        userConfirmBool = false;
                    }

                }
            } else if (validInput(confPrompt)) {
                break;
            }
        }

        if(confPrompt){
            userConfirmBool = trueFalsePrompt(
                    inputFolder.getAbsolutePath() +
                            "\n\nThis will change ALL files in the above directory\n" +
                            "to the extension: " + userInput + "\nIs this correct?",
                    "Please Confirm Your Selection");
        } else {
            if(extensionType.contains("whitelist")) {
                userConfirmBool = trueFalsePrompt(
                        "All files with the following extension(s) will be " +
                                "whitelisted:\n" + userInput + "\n\nIs this correct?",
                        "Please Confirm Your Selection");
                promptWBListExtension(extensionType);
            } else {
                userConfirmBool = trueFalsePrompt(
                        "All files with the following extension(s) will be " +
                                "blacklisted:\n" + userInput + "\n\nIs this correct?",
                        "Please Confirm Your Selection");
                promptWBListExtension(extensionType);
            }
        }
    }

    private static void promptWBListExtension(String extensionType){
        while(true) {
            wbListExtension = JOptionPane.showInputDialog(null,
                    "Type the file extension that you would\n" +
                            "like to change the selected files to.",
                    "Input A File Extension",
                    JOptionPane.PLAIN_MESSAGE);
            if (wbListExtension == null) {
                if (!trueFalsePrompt("Do you wish to continue editing file extensions for the directory:\n" +
                                inputFolder.getAbsolutePath() + "\n\nDo you wish to continue?",
                        "Continue?")) {
                    System.out.println("No Input Detected...");
                    if(trueFalsePrompt("Do you wish to exit?", "Exit?")){
                        System.out.println("User Chose To Exit.");
                        formalExit();
                    } else {
                        userConfirmBool = false;
                    }
                }
            } else if (validInputExtension()) {
                break;
            }
        }


        String supplementalTFMsg;
        if(extensionType.contains("whitelist")) {
            supplementalTFMsg = "The files that contain the following extension:\n";
            if (usedExtensions.size() > 1) {
                supplementalTFMsg = "The files that contain the following extensions:\n";
            }
        } else {
            supplementalTFMsg = "The files that DO NOT contain the following extension:\n";
            if (usedExtensions.size() > 1) {
                supplementalTFMsg = "The files that DO NOT contain the following extensions:\n";
            }
        }

        if(usedExtensions.size() != 0){
            for (int i = 0; i < usedExtensions.size(); i++){
                if(i == 0) {
                    usedExtensionsStr = usedExtensions.get(i);
                } else {
                    usedExtensionsStr = usedExtensions.get(i) + ", " + usedExtensionsStr;
                }
            }
        }

        userConfirmBool = trueFalsePrompt(
                supplementalTFMsg + usedExtensionsStr +
                        "\n\nWill be changed to " + wbListExtension +
                        "\nIs this correct?",
                "Please Confirm Your Selection");
    }

    private static boolean validInputExtension(){
        char nextChar;
        String tempExt;

        if(userInput.equals("")){
            System.out.println("Empty Input Detected!");
            JOptionPane.showMessageDialog(null,
                    "The input was empty, please try again.",
                    "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        String[] wbListExtArr = wbListExtension.split(",");

        for (int a = 0; a < wbListExtArr.length; a++){
            tempExt = "";
            wbListExtArr[a] = wbListExtArr[a].trim();
            if(wbListExtArr[a].contains(".")){
                System.out.println(wbListExtArr[a]);
                for (int b = 0; b < wbListExtArr[a].length(); b++){
                    nextChar = wbListExtArr[a].charAt(b);
                    if(Character.isAlphabetic(nextChar) || Character.isDigit(nextChar)){
                        tempExt = tempExt + nextChar;
                    }
                }
                wbListExtArr[a] = tempExt;
                System.out.println(wbListExtArr[a]);
            }
        }

        if (wbListExtArr.length > 1){
            System.out.println("Please Only List One Extension To Change");
            JOptionPane.showMessageDialog(null,
                    "Please only list one extension to change.",
                    "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
            return false;
        }
        if (!existingExtensions.contains(wbListExtArr[0])) {
            System.out.println("This Extension Does Not Exist In The Directory. Do You Wish To Continue?");
            return(!trueFalsePrompt(
                    "This extension does not exist in this directory.\n" +
                            "Do you wish to use a new file extension?",
                    "Please Confirm Your Selection"));
        }
        return true;
    }

    private static boolean validInput(boolean allVars){
        char nextChar;
        String tempExt;

        if(userInput.equals("")){
            System.out.println("Empty Input Detected!");
            JOptionPane.showMessageDialog(null,
                    "The input was empty, please try again.",
                    "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
            return false;
        }

        String[] userInpExtArr = userInput.split(",");

        for (int a = 0; a < userInpExtArr.length; a++){
            tempExt = "";
            userInpExtArr[a] = userInpExtArr[a].trim();
            if(userInpExtArr[a].contains(".")){
                System.out.println(userInpExtArr[a]);
                for (int b = 0; b < userInpExtArr[a].length(); b++){
                    nextChar = userInpExtArr[a].charAt(b);
                    if(Character.isAlphabetic(nextChar) || Character.isDigit(nextChar)){
                        tempExt = tempExt + nextChar;
                    }
                }
                userInpExtArr[a] = tempExt;
                System.out.println(userInpExtArr[a]);
            }
        }

        if(allVars){
            if (userInpExtArr.length > 1){
                System.out.println("Please Only List One Extension To Change");
                JOptionPane.showMessageDialog(null,
                        "Please only list one extension to change.",
                        "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
            if (!existingExtensions.contains(userInpExtArr[0])) {
                System.out.println("This Extension Does Not Exist In The Directory. Do You Wish To Continue?");
                return(!trueFalsePrompt(
                        "This extension does not exist in this directory.\n" +
                                "Do you wish to use a new file extension?",
                        "Please Confirm Your Selection"));
            }
        } else {
            for (int i = 0; i < userInpExtArr.length; i++) {
                if (!existingExtensions.contains(userInpExtArr[i])) {
                    if(usedExtensions.contains(userInpExtArr[i])){
                        System.out.println("Please Refrain From Typing Extensions Twice");
                        JOptionPane.showMessageDialog(null,
                                "It appears that you typed one of the extensions more than once.\n" +
                                        "Please only type each extension once!",
                                "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
                        return false;
                    }
                    System.out.println("Extension " + userInpExtArr[i] + " Does Not Exist In This Directory!");
                    JOptionPane.showMessageDialog(null,
                            "This extension does not exist in the directory, please try again.",
                            "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
                    return false;
                } else {
                    usedExtensions.add(userInpExtArr[i]);
                    existingExtensions.remove(userInpExtArr[i]);
                }

            }
            if (existingExtensions.size() == 0) {
                System.out.println("You Selected All The File Extensions,\n" +
                        "Please Use The \"All Files\" Option If You Wish To Edit All Files!");
                JOptionPane.showMessageDialog(null,
                        "You selected all the file extensions!\n" +
                                "If you wish to edit all files, please use the \"All Files\" option",
                        "Input Not Valid!", JOptionPane.PLAIN_MESSAGE);
                return false;
            }
        }
        return true;
    }

    //---------------------------------------------------------------------------------**************
    //WhiteList, BlackList, All File Operations
    //---------------------------------------------------------------------------------**************

    private static void whiteListOperations(){
        prgWin.openWindow("Please Wait... Editing Files", filesToEdit.size());
        initializeFiles(0);
        for (int i = 0; i < filesToEdit.size(); i++){
            renameFile(filesToEdit.get(i), wbListExtension);
            prgWin.updateBar(i);
        }

        prgWin.closeWindow();

        if (renameFailedFiles.size() > 0) {
            if(trueFalsePrompt(renameFailedFiles.size() + " files failed to edit\n" +
                    "Would you like a list of these files saved?\n" +
                    "NOTE: It will save as a plain text document in\n" +
                    "the same directory", "Continue?")){
                failedRenameFileCreator("WhiteListFailedRenameFile.txt", renameFailedFiles);
            } else {
                //Do Nothing, Continue
            }
        }
    }

    private static void blackListOperations(){
        prgWin.openWindow("Please Wait... Editing Files", filesToEdit.size());
        initializeFiles(1);
        for (int i = 0; i < filesToEdit.size(); i++){
            renameFile(filesToEdit.get(i), wbListExtension);
            prgWin.updateBar(i);
        }

        prgWin.closeWindow();

        if (renameFailedFiles.size() > 0) {
            if(trueFalsePrompt(renameFailedFiles.size() + " files failed to edit\n" +
                    "Would you like a list of these files saved?\n" +
                    "NOTE: It will save as a plain text document in\n" +
                    "the same directory", "Continue?")){
                failedRenameFileCreator("BlackListFailedRenameFile.txt", renameFailedFiles);
            } else {
                //Do Nothing, Continue
            }
        }
    }

    private static void allFileOperations(){
        prgWin.openWindow("Please Wait... Editing Files", filesToEdit.size());
        initializeFiles(2);
        for (int i = 0; i < filesToEdit.size(); i++){
            renameFile(filesToEdit.get(i), userInput);
            prgWin.updateBar(i);
        }
        prgWin.closeWindow();

        if (renameFailedFiles.size() > 0) {
            if(trueFalsePrompt(renameFailedFiles.size() + " files failed to edit\n" +
                    "Would you like a list of these files saved?\n" +
                    "NOTE: It will save as a plain text document in\n" +
                    "the same directory", "Continue?")){
                failedRenameFileCreator("AllFileFailedRenameFile.txt", renameFailedFiles);
            } else {
                //Do Nothing, Continue
            }
        }
    }

    private static void failedRenameFileCreator(String fileName, ArrayList<File> filesToEdit){
        boolean tryAgain = true;
        int tryAgainOption = 0;
        while(tryAgain) {
            try {
                if(tryAgainOption == 0){//0, BufferedReader, First Resort
                    Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(fileName), "utf-8"));
                    writer.write("Writer Start - ");
                    for (String element : fileOutputBuilder("BufferedReader", filesToEdit)) {
                        writer.write(element);
                        writer.write(" - ");
                    }
                    writer.write("Writer End");
                    try {
                        writer.close();
                    } catch (Exception ex){
                        System.out.println("Writer Not Closed... Ignoring");
                    }
                    System.out.println("File Written With BufferedReader");
                } else if (tryAgainOption == 1){//1, Files Class, Second Resort
                    List<String> files = fileOutputBuilder("Files", filesToEdit);
                    Path file = Paths.get(fileName);
                    Files.write(file, files, StandardCharsets.UTF_8);
                    System.out.println("File Written With FilesClass");
                } else if (tryAgainOption == 2){//2, PrintWriter, Second To Last Resort
                    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
                    writer.println("PrintWriter Output Begin");
                    writer.println("***FAILED FILES***");
                    for (int i = 0; i < renameFailedFiles.size(); i++){
                        writer.println(renameFailedFiles.get(i).getName());
                    }
                    writer.close();
                    System.out.println("File Written With PrintWriter");
                } else {//3, FileOutputStream Class, Binary File, Last Resort
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dataOut = new DataOutputStream(baos);
                    for (String element : fileOutputBuilder("Byte", filesToEdit)) {
                        dataOut.writeUTF(element);
                    }
                    byte data[] = baos.toByteArray();
                    FileOutputStream fileOut = new FileOutputStream(fileName);
                    fileOut.write(data);
                    fileOut.close();
                    System.out.println("File Written With FileOutputStream");
                }

                try {
                    String fileTempStr = System.getProperty("user.dir");

                    File source = new File(fileTempStr + "/" + fileName);
                    System.out.println("Current Location: " + fileTempStr);
                    File destination = new File(inputFolder.getAbsolutePath() + "/" + fileName);
                    System.out.println("Destination: " + inputFolder.getAbsolutePath());

                    Path temp = Files.move(source.toPath(),
                                        destination.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Operation Succeeded");

                    if(temp != null){
                        System.out.println("FileRenameFailed Moved Successfully!");
                    } else {
                        System.out.println("FileRenameFailed Not Moved...");
                    }
                } catch (IOException ioe) {
                    System.out.println("Error Encountered When Moving File: " + ioe.getLocalizedMessage());
                    ioe.printStackTrace();
                }

                //Break Out Of While Loop; Successful Write
                tryAgain = false;
            } catch (IOException ioe) {
                System.out.println("Failed To Create Text File: \"" + ioe.getLocalizedMessage() + "\"\n");
                if(tryAgainOption > 3) {
                    if (trueFalsePrompt(
                            "Failed to create text file with error:\n\"" +
                                    ioe.getLocalizedMessage() + "\"\n" +
                                    "Would you like to try again?\n" +
                                    "NOTE: This may fail more than once!", "Continue?")) {
                        //Continue, change bool to break out
                        tryAgain = false;
                    } else {
                        //Try again
                        tryAgainOption = 0;
                    }
                }
                tryAgainOption++;
            }
        }
    }

    private static List<String> fileOutputBuilder(String outputType, ArrayList<File> files){
        String tempString = "***FAILED FILES***";
        List<String> tempList = new ArrayList<>();
        tempList.add(outputType + " Output Begin");
        tempList.add(tempString);
        for (int i = 0; i < files.size(); i++){
            tempList.add(files.get(i).getName());
        }
        return tempList;
    }

    private static void initializeFiles(int mode){
        for (int i = 0; i < validFiles.size(); i++){
            if (mode == 0){
                for (int a = 0; a < usedExtensions.size(); a++){
                    if(validFiles.get(i).getName().contains(usedExtensions.get(a))){
                        System.out.println("QUEUED MODE W: " + validFiles.get(i).getName());
                        filesToEdit.add(validFiles.get(i));
                        break;
                    }
                }
            } else if (mode == 1){
                boolean found = false;
                for (int b = 0; b < usedExtensions.size(); b++){
                    if(validFiles.get(i).getName().contains(usedExtensions.get(b))){
                        found = true;
                        break;
                    }
                }
                if(!found){
                    System.out.println("QUEUED MODE B: " + validFiles.get(i).getName());
                    filesToEdit.add(validFiles.get(i));
                }
            } else if (mode == 2){
                System.out.println("QUEUED MODE A: " + validFiles.get(i).getName());
                filesToEdit.add(validFiles.get(i));
            }
        }
    }

    private static void renameFile(File file, String newExtension){
        int tryAgain = 0;
        //System.out.println("Attempting To Rename File...");
        String tempName = getNameOnly(file.getName());
        //System.out.println("Temporarily Storing File...");

        while (true) {
            File newDir = new File(file.getParent() + "/" + tempName + "." + newExtension);
            if (!file.renameTo(newDir)) {
                if (tryAgain == 0) {
                    System.out.println("File Rename Failed... Trying Again...");
                    tryAgain++;
                } else {
                    System.out.println("File Rename Failed...");
                    renameFailedFiles.add(file);
                    break;
                }
            } else {
                completedFiles.add(newDir);
                break;
            }
        }
    }

    private static String getNameOnly(String fileName){//_____________________________________________________**********
        String tempName = "";
        String extQ = "";
        char nextChar;
        boolean extQBool = false;

        for (int i = 0; i < fileName.length(); i++){
            nextChar = fileName.charAt(i);

            if(nextChar == '.' && !extQBool){
                extQBool = true;
            } else if (nextChar == '.'){
                tempName = tempName + extQ;
                extQ = "";
            }

            if(!extQBool){
                tempName = tempName + nextChar;
            } else {
                extQ = extQ + nextChar;
            }
        }

        //System.out.println("Collected File Name: " + tempName);
        fileName = tempName;

        return fileName;
    }

    //---------------------------------------------------------------------------------**************
    //Backup Creation/Deletion, Formal Exit, OS Checkers
    //---------------------------------------------------------------------------------**************

    private static void createBackup(){
        int backupProgress = 0;
        int backupFail = 0;
        boolean tryAgain = false;
        File fileQueue;
        if(!backupFolder.exists()){
            if(backupFolder.mkdir()){
                System.out.println("Created Backup Folder!");

                //setFolderAsHidden();

                prgWin.openWindow("Please Wait... Backing Up Files", validFiles.size());

                for (int i = 0; i < validFiles.size(); i++) {
                    fileQueue = new File(backupFolder.getAbsolutePath() + "/" + validFiles.get(i).getName());
                    try {
                        Path temp = Files.copy(validFiles.get(i).toPath(),
                                fileQueue.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                        if(temp != null){
                            backupProgress++;
                            backedUpFiles.add(fileQueue);
                            backedUpFileNames.add(fileQueue.getName());
                            System.out.println("Backed Up File " + backupProgress + " Out Of " + validDirectory);
                            //System.out.println(validFiles.get(i).getName() + " Backed Up!");
                            if(tryAgain)
                                tryAgain = false;
                        } else {
                            if(tryAgain) {
                                backupFail++;
                                backupFailedFiles.add(validFiles.get(i));
                                System.out.println(validFiles.get(i).getName() + " Not Backed Up...");
                                tryAgain = false;
                            } else {
                                System.out.println("Backup Failed... Trying Again...");
                                i--;
                                tryAgain = true;
                            }
                        }
                    } catch (IOException ioe) {
                        System.out.println("File Backup Critically Failed For: " + ioe.getLocalizedMessage());
                    }

                    prgWin.updateBar(i);
                }

                prgWin.closeWindow();

                if(backupFail > 0) {
                    String supplementaryTxt = " file failed.";
                    if (backupFail > 1)
                        supplementaryTxt = " files failed.";
                    System.out.println("Backup for " + backupFail + supplementaryTxt);
                    if (trueFalsePrompt(
                            "File backup for " + backupFail + supplementaryTxt +
                            "\nWould you like to continue?" +
                            "\nNOTE: If you continue, this could result in file loss!",
                            "Continue?")){
                        //Continue, do nothing
                    } else {
                        //Choose a new directory, this will force the loop closed again
                        deleteBackup();
                        System.out.println("Resetting Backend Variables...");
                        userConfirmBool = true;
                        userExtensionInputBool = true;
                        validDirBool = false;
                        backupExists = true;
                        validFiles = new ArrayList<>();
                        validFileNames = new ArrayList<>();
                        filesToEdit = new ArrayList<>();
                        backedUpFiles = new ArrayList<>();
                        backedUpFileNames = new ArrayList<>();
                        completedFiles = new ArrayList<>();
                        backupFailedFiles = new ArrayList<>();
                        renameFailedFiles = new ArrayList<>();
                        restoreFailedFiles = new ArrayList<>();
                        deleteFailedFiles = new ArrayList<>();
                        existingExtensions = new ArrayList<>();
                        usedExtensions = new ArrayList<>();
                        usedExtensionsStr = "";
                        existingExtensionsStr = "";
                        userChoice = 0;
                        finalUserChoice = 0;
                        validDirectory = 0;
                        ignoredDirs = 0;
                        ignoredFiles = 0;
                        foundExtensions = 0;
                        userInput = "";
                        System.out.println("Clearing Backup Directory...");
                        backupFolder = null;
                        System.out.println("Clearing Input Directory...");
                        inputFolder = null;
                    }
                }

            } else {
                System.out.println("Failed To Create Backup Directory!");
                if(trueFalsePrompt(
                        "A backup folder could not be created!\n" +
                                "Would you like to continue?\n" +
                                "NOTE: If you continue, this could result in file loss!",
                        "Continue?")){
                    //Continue, change bool
                    backupExists = false;
                } else {
                    //Choose a new directory, this will force the loop closed again
                    deleteBackup();
                    System.out.println("Resetting Backend Variables...");
                    userConfirmBool = true;
                    userExtensionInputBool = true;
                    validDirBool = false;
                    backupExists = true;
                    validFiles = new ArrayList<>();
                    validFileNames = new ArrayList<>();
                    filesToEdit = new ArrayList<>();
                    backedUpFiles = new ArrayList<>();
                    backedUpFileNames = new ArrayList<>();
                    completedFiles = new ArrayList<>();
                    backupFailedFiles = new ArrayList<>();
                    renameFailedFiles = new ArrayList<>();
                    restoreFailedFiles = new ArrayList<>();
                    deleteFailedFiles = new ArrayList<>();
                    existingExtensions = new ArrayList<>();
                    usedExtensions = new ArrayList<>();
                    usedExtensionsStr = "";
                    existingExtensionsStr = "";
                    userChoice = 0;
                    finalUserChoice = 0;
                    validDirectory = 0;
                    ignoredDirs = 0;
                    ignoredFiles = 0;
                    foundExtensions = 0;
                    userInput = "";
                    System.out.println("Clearing Backup Directory...");
                    backupFolder = null;
                    System.out.println("Clearing Input Directory...");
                    inputFolder = null;
                }
            }
        }
    }

    //This function was scrapped for now. I'll keep it around though in case it proves to be useful later.
    private static void setFolderAsHidden(){

        if(isWindows()) {
            System.out.println("Attempting To Hide Windows Backup Folder...");
            try {
                // execute attrib command to set hide attribute
                Process p = Runtime.getRuntime().exec("attrib +H " + backupFolder.getPath());
                p.waitFor();
                if (backupFolder.isHidden()) {
                    System.out.println(backupFolder.getName() + " Is Now Hidden");
                } else {
                    System.out.println(backupFolder.getName() + " Is Not Set To Hidden");
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Hiding Backup Folder Failed: " + e.getLocalizedMessage());
            }
        } else if (isUnix() || isMac()){
            System.out.println("Attempting To Hide Mac/Unix Backup Folder...");
            File newBackupDir = new File(backupFolder.getParent() + "/." + backupFolder.getName());
            if(backupFolder.renameTo(newBackupDir)){
                System.out.println("Backup Folder Hidden Successfully");
                backupFolder = newBackupDir;
            } else {
                System.out.println("Hiding Backup Folder Failed...");
            }
        } else {
            System.out.println("This OS Is Not Supported For Hiding The Backup Folder");
        }
    }

    private static boolean isWindows(){
        //System.out.println("You are using Windows!");
        return(OS.contains("win"));
    }

    private static boolean isMac(){
        //System.out.println("You are on a Mac!");
        return(OS.contains("mac"));
    }

    private static boolean isUnix(){
        //System.out.println("You are using a distro of Unix!");
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    //While I created ways to "retry" in all the other file operation functions, this is the one that can easily be
    //done manually by the user and is also out of the way, so there is no need to create "retry" code here...
    private static void deleteBackup(){

        if(restoreFailedFiles.size() == 0) {
            try {//This is here because deleteBackup() is called in main
                System.out.println("Deleting Backup...");
                prgWin.openWindow("Please Wait... Deleting Backup", backedUpFiles.size());

                for (int i = 0; i < backedUpFiles.size(); i++) {
                    File newFile = new File(backupFolder.getAbsolutePath() + "/" + backedUpFiles.get(i).getName());
                    if (!newFile.delete()) {
                        System.out.println(backedUpFiles.get(i).getName() + " Not Deleted...");
                    }
                    prgWin.updateBar(i);
                }
                if (backupFolder.delete()) {
                    System.out.println("Backup Folder Deleted!");
                } else {
                    File tempBackupDir = new File(backupFolder.getParent() + "/" + backupFolder.getName());
                    if (backupFolder.renameTo(tempBackupDir)) {
                        if (backupFolder.delete()) {
                            System.out.println("Backup Folder Deleted!");
                        } else if (tempBackupDir.delete()) {
                            System.out.println("Backup Folder Deleted!");
                        } else {
                            System.out.println("Backup Folder NOT Deleted... Please Delete Manually!");
                        }
                    }
                }
                prgWin.closeWindow();
            } catch (NullPointerException npe) {
                System.out.println("Backup Folder Does Not Exist...");
            }
        } else {
            System.out.println("The Backup Will Not Be Deleted Due To Failed File Transfers");
        }
    }

    private static boolean deleteOldBackup(File oldBackup){
        File oldBackupDir = new File(oldBackup.getParent() + "/" + oldBackup.getName());
        if(oldBackup.delete()){
            return true;
        } else if (oldBackupDir.delete()){
            return true;
        }
        if(oldBackup.renameTo(oldBackupDir)){
            System.out.println("Old Backup Folder Unhidden!");
            if(oldBackup.delete() || oldBackupDir.delete()){
                return true;
            }
        } else {
            System.out.println("Old Backup Folder Not Unhidden...");
        }

        return false;
    }

    private static void formalExit(){
        try {
            if (prgWin.getIsVisible() == 1) {
                prgWin.closeWindow();
            }
        } catch (NullPointerException npe){
            //There is no progress window yet
        }
        try {//This is here in case formalExit() is called from anywhere
            if (backupFolder.exists()) {
                System.out.println("Backup Folder Exists...");
                deleteBackup();
                System.out.println("Exiting...");
                System.exit(0);
            } else {
                System.out.println("Backup Folder Does Not Exist...\nExiting Normally...");
                System.exit(0);
            }
        } catch (NullPointerException npe){
            System.out.println("Backup Folder Does Not Exist...\nExiting Normally...");
            System.exit(0);
        }
    }

    //---------------------------------------------------------------------------------**************
    //Open Complete Folder/Display Complete Window/Undo File Operations
    //---------------------------------------------------------------------------------**************

    private static void openCompleteFolder(){
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File(inputFolder.getAbsolutePath());
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException iae) {
            System.out.println("File Not Found");
        } catch (IOException ioe){
            System.out.println("IO Exception");
        }
    }

    private static void displayCompletionWindow(){
        while(true) {
            finalUserChoice = JOptionPane.showOptionDialog(null,
                    "Please take a moment to confirm that the files\n" +
                    "have been changed as per your request. If so,\n" +
                    "click the EXIT button below to exit. If not,\n" +
                    "click RETRY below to restore the previous files\n" +
                    "and start fresh. If the files are correct and\n" +
                    "you would like to edit more file extensions,\n" +
                    "click CONTINUE.",
                    "Choose An Option Below",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Continue", "Retry", "Exit"},
                    null);
            if(finalUserChoice != -1){
                break;
            } else {
                if(trueFalsePrompt("Do you wish to exit?", "Exit?")){
                    System.out.println("User Chose To Exit.");
                    formalExit();
                }
            }
        }

        if(finalUserChoice == 2){ //Exit
            System.out.println("Thank You For Using The File Extension Editor! Exiting...");
            formalExit();
        } else if (finalUserChoice == 1){ //Retry
            System.out.println("User Chose To Retry...");
            undoFileOperations();
        } else if (finalUserChoice == 0){ //Continue
            System.out.println("User Chose To Continue...");
        } else {
            System.out.println("Well.... That wasn't supposed to happen!! Exiting for your safety (and mine)....");
            formalExit();
        }
    }

    private static void undoFileOperations() {
        System.out.println("Restoring Old Files...");
        File fileQueue;
        int filesRestored = 0;
        boolean tryAgainRestore = false;
        if (backupExists) {
            prgWin.openWindow("Please Wait... Restoring Backup", backedUpFiles.size());
            for (int i = 0; i < backedUpFiles.size(); i++) {
                fileQueue = new File(inputFolder.getAbsolutePath() + "/" + backedUpFiles.get(i).getName());
                try {
                    Path temp = Files.copy(backedUpFiles.get(i).toPath(),
                            fileQueue.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    if (temp != null) {
                        //System.out.println(backedUpFiles.get(i).getName() + " Restored!");
                        filesRestored++;
                    } else {
                        if (!tryAgainRestore) {
                            System.out.println(backedUpFiles.get(i).getName() + " Not Restored... Trying Again...");
                            tryAgainRestore = true;
                            i--;
                        } else {
                            System.out.println(backedUpFiles.get(i).getName() + " Not Restored...");
                            restoreFailedFiles.add(backedUpFiles.get(i));
                            tryAgainRestore = false;
                        }
                    }
                } catch (IOException ioe) {
                    System.out.println("File Restore Critically Failed For: " + ioe.getLocalizedMessage());
                }
                prgWin.updateBar(i);
            }
            prgWin.closeWindow();

            if (restoreFailedFiles.size() > 0) {
                if (trueFalsePrompt("File restore for " + restoreFailedFiles.size() + " files failed...\n" +
                        "Would you like to try to move them to\n" +
                        "a separate folder?\n" +
                        "NOTE: This will create a folder called\n" +
                        "\"FileExtensionRecovery\"", "Continue?")) {
                    File feeRecovery = new File(inputFolder.getAbsolutePath() + "/FileExtensionRecovery");
                    int restoreProgress = 0;
                    int restoreFail = 0;
                    boolean tryAgain = false;
                    if (!feeRecovery.exists()) {
                        if (feeRecovery.mkdir()) {
                            prgWin.openWindow("Please Wait... Recovering Files", validFiles.size());

                            for (int i = 0; i < restoreFailedFiles.size(); i++) {
                                fileQueue = new File(feeRecovery.getAbsolutePath() + "/" + restoreFailedFiles.get(i).getName());
                                try {
                                    Path temp = Files.copy(restoreFailedFiles.get(i).toPath(),
                                            fileQueue.toPath(),
                                            StandardCopyOption.REPLACE_EXISTING);
                                    if (temp != null) {
                                        restoreProgress++;
                                        System.out.println("Restored File " + restoreProgress + " Out Of " + restoreFailedFiles.size());
                                        if (tryAgain)
                                            tryAgain = false;
                                    } else {
                                        if (tryAgain) {
                                            restoreFail++;
                                            System.out.println(validFiles.get(i).getName() + " Not Recovered...");
                                            tryAgain = false;
                                        } else {
                                            System.out.println("Recover Failed... Trying Again...");
                                            i--;
                                            tryAgain = true;
                                        }
                                    }
                                } catch (IOException ioe) {
                                    System.out.println("File Restore Critically Failed For: " + ioe.getLocalizedMessage());
                                }
                                prgWin.updateBar(i);
                            }

                            prgWin.closeWindow();

                            if (restoreFail > 0) {
                                String supplementaryTxt = " file!\n";
                                if (restoreFail > 1)
                                    supplementaryTxt = " files!\n";
                                JOptionPane.showMessageDialog(null,
                                        "The File Extension Editor failed to\n" +
                                                "recover " + restoreFail + supplementaryTxt +
                                                "The backup folder will not be deleted\n" +
                                                "so you can access your files.", "Restore Files Failed", JOptionPane.PLAIN_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                    "The File Extension Editor failed to\n" +
                                            "create a recovery folder...", "Restore Files Failed", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                } else {
                    //Continue, do nothing
                }
            }

            boolean tryAgainDelete = false;
            int filesDeleted = 0;
            System.out.println("Deleting Incomplete Files...");
            prgWin.openWindow("Please Wait... Deleting Edited Files", completedFiles.size());
            for (int i = 0; i < completedFiles.size(); i++) {
                if (!completedFiles.get(i).delete()) {
                    if (!tryAgainDelete) {
                        System.out.println("Incomplete File Delete Failed... Trying Again...");
                        tryAgainDelete = true;
                        i--;
                    } else {
                        System.out.println("Incomplete File " + i + " Not Deleted...");
                        deleteFailedFiles.add(completedFiles.get(i));
                        tryAgainDelete = false;
                    }
                } else {
                    filesDeleted++;
                }
                prgWin.updateBar(i);
            }
            prgWin.closeWindow();

            if (deleteFailedFiles.size() > 0) {
                if (trueFalsePrompt("File delete for " + deleteFailedFiles.size() + " failed...\n" +
                        "Would you like to have a list of these files saved?\n" +
                        "NOTE: It will save as a plain text document in\n" +
                        "the same directory", "Continue?")) {
                    failedRenameFileCreator("FileExtensionFailedDeletesFile.txt", deleteFailedFiles);
                } else {
                    //Continue, do nothing
                }
            }

            System.out.println(filesDeleted + " Files(s) Deleted!");
            System.out.println(filesRestored + " File(s) Restored!");
        } else {
            System.out.println("Backup Does Not Exist, Files Cannot Be Restored");
        }
    }
}