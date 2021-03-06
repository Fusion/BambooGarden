package com.voilaweb.mobile.bamboogarden;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.containers.RootClass;

import java.io.*;
import java.nio.channels.FileChannel;


@RootClass.Candidate
public class RootHelper {


    private String APPPATH = "/data/data/com.wacom.bamboopaper/";
    private String DBPATH  = APPPATH + "databases/";
    private String FPATH   = APPPATH + "files/";


    public native int nativeCreateSymLink(String filePath, String linkPath);
    public native int nativeGetOwnerId(String filePath);
    public native int nativeChownerId(String filePath, int uid);
    static {
        System.load(System.getProperty("user.dir") + "/lib/libtinylib.so");
    }

    public RootHelper(RootClass.RootArgs args) {
        CLog.log("RootHelper invoked for BambooGarden app");
        if(args.args.length > 0) {
            if(args.args[0].equals("init")) {
                try {
                    initBamboo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if(args.args[0].equals("list")) {
                exportExistingBambooList();
            }
            else if(args.args[0].equals("create")) {
                if(args.args.length > 1) {
                    try {
                        createDatabase(args.args[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(args.args[0].equals("color")) {
                if(args.args.length > 2) {
                    updateBambooColor(args.args[1], args.args[2]);
                }
            }
            else if(args.args[0].equals("switch")) {
                if(args.args.length > 1) {
                    try {
                        switchBamboo(args.args[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if(args.args[0].equals("delete")) {
                if(args.args.length > 1) {
                    deleteDatabase(args.args[1]);
                }
            }
        }
    }


    private boolean initBamboo() throws IOException {
        CLog.log("RootHelper:initBamboo()");
        File defaultFile = new File(DBPATH + File.separator + "book");
        try {
            if(!isSymlink(defaultFile)) {
                File newFile = new File(DBPATH + File.separator + "default");
                if(!defaultFile.renameTo(newFile)) {
                    throw new IOException("Unable to rename book");
                }
                if(!createSymLink(newFile, defaultFile)) {
                    throw new IOException("Unable to link back to book");
                }
                File journalFile = new File(DBPATH + File.separator + "book-journal");
                File newJournalFile = new File(DBPATH + File.separator + "default-journal");
                if(!journalFile.renameTo(newJournalFile)) {
                    throw new IOException("Unable to rename book-journal");
                }
                if(!createSymLink(newJournalFile, journalFile)) {
                    throw new IOException("Unable to link back to book-journal");
                }

                setDbOwnership(DBPATH + File.separator + "book");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private boolean exportExistingBambooList() {
        CLog.log("RootHelper:exportExistingBambooList()");
        String activeFileName = getActiveFileName();
        File[] bambooFiles = new File(DBPATH).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return !filename.endsWith("-journal") && !filename.equals("book");
            }
        });
        System.out.println("OK");
        for(File bambooFile:bambooFiles) {
            String name = bambooFile.getName();
            int color = getBambooColor(bambooFile);
            System.out.println(name + "," + (activeFileName.equals(name) ? "true" : "false") + "," + color);
        }
        return true;
    }


    // TODO: Sanitize input!
    private boolean createDatabase(String name) throws IOException {
        String dbName = DBPATH + name;
        File dbFile = new File(dbName);
        // First check if already exists: it shouldn't
        if(dbFile.exists())
            return error("Database already exists.");

        /*
         * My original intent was to cleanly create a database as seen below.
         * At this point, though, even with the exact same schema, Bamboo
         * detects a discrepancy somwhere and freaks out.
         * So, instead, I have to go with the big guns i.e. copy files.
         *
         */

        copyFile(System.getProperty("user.dir") + "/files/book", dbName);
        copyFile(System.getProperty("user.dir") + "/files/book-journal", dbName + "-journal");

        /*
         * Leaving this here for reference...
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        if(db == null)
            return error("Unable to create database.");
        try {
            db.execSQL("CREATE TABLE book(id INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
                    + " current_page_index INTEGER NOT NULL, last_modified_date INTEGER NOT NULL,"
                    + " cover_color INTEGER NOT NULL,paper_type INTEGER NOT NULL,"
                    + " title TEXT NOT NULL DEFAULT 'My\nNotes')");
            db.execSQL("CREATE TABLE page(id INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
                    + " uri TEXT, index_in_book INTEGER NOT NULL, last_modified_date INTEGER NOT NULL,"
                    + " book_id INTEGER NOT NULL, FOREIGN KEY(book_id) REFERENCES book(id))");
            db.execSQL("CREATE INDEX page_index_in_book_idx ON page (index_in_book ASC)");
        }
        catch(SQLiteException ex) {
            db.close();
            return error(ex.toString());
        }
        db.close();
        */

        setDbOwnership(dbName);
        return true;
    }


    private boolean deleteDatabase(String name) {
        CLog.log("RootHelper:deleteDatabase()");
        String dbName = DBPATH + name;
        File dbFile = new File(dbName);
        if(dbFile.exists()) {
            dbFile.delete();
            File jFile = new File(dbName + "-journal");
            if(jFile.exists()) {
                jFile.delete();
            }
        }
        return true;
    }


    private boolean switchBamboo(String name) throws IOException {
        CLog.log("RootHelper:switchBamboo()");
        // delete symlinks recreate symlinks
        String dbName = DBPATH + "book";
        File dbFile = new File(dbName);
        if(dbFile.exists()) {
            dbFile.delete();
            File jFile = new File(dbName + "-journal");
            if(jFile.exists()) {
                jFile.delete();
            }
        }

        linkBamboo(name);

        return true;
    }


    private boolean updateBambooColor(String name, String colorValue) {
        return setBambooColor(new File(DBPATH + name), Integer.parseInt(colorValue));
    }


    private int getBambooColor(File bambooDb) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DBPATH + bambooDb.getName(), null, 0);
        Cursor cursor = db.rawQuery("SELECT cover_color FROM book", null);
        cursor.moveToFirst();
        int color = cursor.getInt(0);
        db.close();
        return color;
    }


    private boolean setBambooColor(File bambooDb, int color) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DBPATH + bambooDb.getName(), null, 0);
        db.execSQL("UPDATE book SET cover_color=" + color);
        db.close();
        return true;
    }


    private boolean copyFile(String sourcePath, String destPath) {
        FileChannel source = null;
        FileChannel dest   = null;
        try {
            source = new FileInputStream(new File(sourcePath)).getChannel();
            dest = new FileOutputStream(new File(destPath)).getChannel();
            dest.transferFrom(source, 0, source.size());
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if(source != null)
                try {
                    source.close();
                } catch (IOException e) {
                }
            if(dest != null)
                try {
                    dest.close();
                } catch (IOException e) {
                }
        }
        return true;
    }


    private boolean linkBamboo(String newDbname) throws IOException {
        File defaultFile = new File(DBPATH + File.separator + "book");
        try {
            File newFile = new File(DBPATH + File.separator + newDbname);
            if(!createSymLink(newFile, defaultFile)) {
                throw new IOException("Unable to link back to book");
            }
            File journalFile = new File(DBPATH + File.separator + "book-journal");
            File newJournalFile = new File(DBPATH + File.separator + newDbname + "-journal");
            if(!createSymLink(newJournalFile, journalFile)) {
                throw new IOException("Unable to link back to book-journal");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /* Quick adaption of Apache common.io.FileUtils's implementation for Java < 7 */
    protected boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File may not be null");
        }
        boolean symlink = false;

        File fileInCanonicalDir = getFileInCanonicalDir(file);
        if (fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile())) {
            symlink = false;
        } else {
            symlink = true;
        }

        return symlink;
    }


    private String getActiveFileName() {
        String activeFileName = "";
        File defaultFile = new File(DBPATH + File.separator + "book");
        try {
            activeFileName = getFileInCanonicalDir(defaultFile).getCanonicalFile().getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activeFileName;
    }


    protected File getFileInCanonicalDir(File file) throws IOException {
        File fileInCanonicalDir;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }
        return fileInCanonicalDir;
    }


    protected boolean createSymLink(File srcPath, File dstPath)  {
        return (0 == nativeCreateSymLink(srcPath.getAbsolutePath(), dstPath.getAbsolutePath()));
    }


    protected int getBambooOwnerId() throws IOException {
        return nativeGetOwnerId(DBPATH);
    }


    protected boolean setFileOwnerId(String filePath, int uid) {
        return (0 == nativeChownerId(filePath, uid));
    }


    protected void setDbOwnership(String dbName) throws IOException {
        int ownerId = getBambooOwnerId();
        if(ownerId < 0) throw new IOException("Unable to retrieve ownership");
        if(!setFileOwnerId(dbName, ownerId)) throw new IOException("Unable to change database's owner id");
        if(!setFileOwnerId(dbName + "-journal", ownerId)) throw new IOException("Unable to change journal's owner id");
    }

    private void test() {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(DBPATH + "book", null, 0);
            CLog.log("Got nice db object: " + db);
        }
        catch(SQLiteException ex) {
            CLog.error("Unable to open db!", ex);
        }
    }


    protected boolean error(String txt) {
        return false;
    }
}
