package com.plustech.print.fileexplorer.workers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Comment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.plustech.print.R;
import com.plustech.print.common.Common;
import com.plustech.print.fileexplorer.FileExplorerMain;
import com.plustech.print.fileexplorer.FileListEntry;
import com.plustech.print.fileexplorer.util.FileExplorerUtils;
import com.plustech.print.fileexplorer.util.FileListSorter;
import com.plustech.print.fileexplorer.util.PreferenceUtil;

public class Finder extends AsyncTask<File, Integer, List<FileListEntry>>
{
	
	private static final String TAG = Finder.class.getName();
	
	private FileExplorerMain caller;
	private ProgressDialog waitDialog;
	private PreferenceUtil prefs;
	
	private File currentDir;
	
	
	public Finder(FileExplorerMain caller) {
		
		this.caller = caller;
		prefs = new PreferenceUtil(this.caller);
	}

	@Override
	protected void onPostExecute(List<FileListEntry> result) {

		final List<FileListEntry> childFiles = result;
		Log.v(TAG, "Children for "+currentDir.getAbsolutePath()+" received");
		caller.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				if(waitDialog!=null && waitDialog.isShowing())
				{
					waitDialog.dismiss();
				}
				Log.v(TAG, "Children for "+currentDir.getAbsolutePath()+" passed to caller");
				caller.setCurrentDir(currentDir);
				caller.setNewChildren(childFiles );
			}
		});
	
	}
	@Override
	protected List<FileListEntry> doInBackground(File... params) {
		
		Thread waitForASec = new Thread() {
			
			@Override
			public void run() {
				
				waitDialog = new ProgressDialog(caller);
				waitDialog.setTitle("");
				waitDialog.setMessage(caller.getString(R.string.querying_filesys));
				waitDialog.setIndeterminate(true);
				
				try {
					Thread.sleep(100);
					if(this.isInterrupted())
					{
						return;
					}
					else
					{
						caller.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {

								if(waitDialog!=null)
									waitDialog.show();
							}
						});

					}
				} catch (InterruptedException e) {
					
					Log.e(TAG, "Progressbar waiting thread encountered exception ",e);
					e.printStackTrace();
				}

				
			}
		};
//		caller.runOnUiThread(waitForASec);
		
		currentDir = params[0];
		Log.v(TAG, "Received directory to list paths - "+currentDir.getAbsolutePath());
		
		String[] children = currentDir.list();
		List<FileListEntry> childFiles = new ArrayList<FileListEntry>();
		
		boolean showHidden = prefs.isShowHidden();
		boolean showSystem = prefs.isShowSystemFiles();
		
		Map<String, Long> dirSizes = FileExplorerUtils.getDirSizes(currentDir);
		
		for(String fileName : children)
		{
			File f = new File(currentDir.getAbsolutePath()+File.separator+fileName);
			
			if(!f.exists())
			{
				continue;
			}
			if(FileExplorerUtils.isProtected(f) && !showSystem)
			{
				continue;
			}
			if(f.isHidden() && !showHidden)
			{
				continue;
			}
			
			String fname = f.getName();
			
			FileListEntry child = new FileListEntry();
			child.setName(fname);
			child.setPath(f);
			if(f.isDirectory())
			{
				try
				{
					Long dirSize = dirSizes.get(f.getCanonicalPath());
					child.setSize(dirSize);
				}
				catch (Exception e) {

					Log.w(TAG, "Could not find size for "+child.getPath().getAbsolutePath());
					child.setSize(0);
				}
				child.setLastModified(new Date(f.lastModified()));
				childFiles.add(child);
			}
			else
			{
				child.setSize(f.length());
				Log.i("cmc", f.getName());
				
//				child.setLastModified(new Date(f.lastModified()));
//				childFiles.add(child);
				
				switch( Common.FILE_TYPE.valueOf(Common.getFileType(f.getName())) ) {
				case TYPE_PDF:
				case TYPE_DOC:
				case TYPE_DOCX:
				case TYPE_XLS:
				case TYPE_XLSX:
				case TYPE_TXT:
				case TYPE_IMAGE:
					child.setLastModified(new Date(f.lastModified()));
					childFiles.add(child);
					FileListSorter sorter = new FileListSorter(caller);
					Collections.sort(childFiles, sorter);
					break;
				default:
					break;
				}
			}
		}
		Log.v(TAG, "Will now interrupt thread waiting to show progress bar");
		if(waitForASec.isAlive())
		{
			try
			{
				waitForASec.interrupt();
			}
			catch (Exception e) {
				
				Log.e(TAG, "Error while interrupting thread",e);
			}
		}
		return childFiles;
	}
}
