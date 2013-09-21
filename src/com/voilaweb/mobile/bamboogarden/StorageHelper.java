package com.voilaweb.mobile.bamboogarden;

import android.content.Context;

import java.io.*;

public class StorageHelper {
	static public final boolean DO_PRESERVE = true;
	static public final boolean DO_NOT_PRESERVE = false;
	
	private Context mContext;
	private static StorageHelper mInstance = null;
	
	
	private StorageHelper(Context context) {
		mContext = context;
	}
	
	
	synchronized public void shelve(String name, Serializable obj) {
        try {
			FileOutputStream fos = mContext.openFileOutput("shelf." + name, Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(obj);
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	
	synchronized public Serializable unshelve(String name) {
		return unshelve(name, 0L);
	}
	
	
	synchronized public Serializable unshelve(String name, boolean preserve) {
		return unshelve(name, 0, preserve);
	}
	
	
	synchronized public Serializable unshelve(String name, long validFor) {
		return unshelve(name, validFor, false);
	}
	
	
	synchronized public Serializable unshelve(String name, long validFor, boolean preserve) {
		Serializable obj = null;
		try {
			String fileName = "shelf." + name;
			File file = mContext.getFileStreamPath(fileName);
			if(file.exists()) {
				boolean useFile = true;
				if(validFor != 0) {
					long updatedAtMillis = file.lastModified();
					if(System.currentTimeMillis() - updatedAtMillis  > validFor) {
						useFile = false; // invalidate
					}
				}
				if(useFile) {
					FileInputStream fis = mContext.openFileInput(fileName);
		    		ObjectInputStream is = new RefactoredObjectInputStream(fis);
		    		obj = (Serializable)is.readObject();
		    		is.close();	 
				}
				if(!preserve) {
					mContext.deleteFile(fileName);
				}
			}
		} catch (FileNotFoundException e) {
			// OK so the file did not exist...it's fine.
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	
	synchronized public void forget(String name) {
		try {
			String fileName = "shelf." + name;
			mContext.deleteFile(fileName);
		}
		catch(Exception e) {}
	}
	
	
	static public StorageHelper instance(Context context) {
		if(mInstance == null)
			mInstance = new StorageHelper(context);
		return mInstance;
	}


    private class RefactoredObjectInputStream extends ObjectInputStream {


        public RefactoredObjectInputStream() throws StreamCorruptedException, IOException {
            super();
        }


        public RefactoredObjectInputStream(InputStream input) throws StreamCorruptedException, IOException {
            super(input);
        }


        @Override
        protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
            ObjectStreamClass ret = super.readClassDescriptor();
            /*
            if(ret.getName().equals("com.voilaweb.mobile.wasgyf.AppMetaData")) {
                ret = ObjectStreamClass.lookup(com.voilaweb.mobile.wasgyf.model.AppMetaData.class);
            }
            */
            return ret;
        }
    }
}
