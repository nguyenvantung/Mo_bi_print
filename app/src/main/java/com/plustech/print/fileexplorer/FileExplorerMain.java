package com.plustech.print.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.plustech.print.R;
import com.plustech.print.common.Common;
import com.plustech.print.fileexplorer.util.FileActionsHelper;
import com.plustech.print.fileexplorer.util.FileExplorerUtils;
import com.plustech.print.fileexplorer.util.PreferenceUtil;
import com.plustech.print.fileexplorer.workers.FileMover;
import com.plustech.print.fileexplorer.workers.Finder;
import com.plustech.print.fileexplorer.workers.Trasher;

public class FileExplorerMain extends ListActivity {


	private ListView explorerListView;
	private File currentDir;
	private PreferenceUtil prefs;
	private List<FileListEntry> files;
	private FileListAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
    	
    	
//    	if(!new PreferenceUtil(this).isEulaAccepted())
//    	{
//    		EulaPopupBuilder.create(this).show();
//    	}
    	
        setContentView(R.layout.main);
        prefs = new PreferenceUtil(this);
		currentDir = prefs.getStartDir();
		
		listContents(currentDir);
		
        files = new ArrayList<FileListEntry>();
        
        explorerListView = (ListView)getListView();
        
        adapter = new FileListAdapter(this, files);
        explorerListView.setAdapter(adapter);

        explorerListView.setOnItemClickListener(new OnItemClickListener() {
        	
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            	
            	FileListEntry file = (FileListEntry)explorerListView.getAdapter().getItem(position);
            	Log.d("FileExplorerManager", "Select path: "+file.getPath().getAbsolutePath());
            	
                switch( Common.FILE_TYPE.valueOf(Common.getFileType(file.getPath().getName())) ) {
				case TYPE_PDF:
				case TYPE_DOC:
				case TYPE_DOCX:
				case TYPE_XLS:
				case TYPE_XLSX:
				case TYPE_TXT:
				case TYPE_IMAGE:
					Intent result = Common.buildIntent(file.getPath().getAbsolutePath());
	            	setResult(RESULT_OK, result);
	            	finish();
					break;
				default:
					select(file.getPath());
					break;
				}
	
            }

         });
        

        registerForContextMenu(explorerListView);
        

    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	
    	if(v.getId() == getListView().getId());
    	{
    		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    		File selected = files.get(info.position).getPath();
    		if(FileExplorerUtils.isProtected(selected))
    		{
    			return;
    		}
    		
    		int[] actions = FileActionsHelper.getContextMenuOptions(selected, this);
			if(actions.length>0)
    		{
    			menu.setHeaderTitle(selected.getName());
    			for(int i=0;i<actions.length;i++)
    			{
    				menu.add(Menu.NONE, actions[i], i, actions[i]);
    			}
    		}
    		
    	}
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
      AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
      int action = item.getItemId();
      FileListEntry selected = files.get(info.position);
      FileActionsHelper.doOperation(selected,action, this);
      
      return true;
    }
    void select(File file)
    {
    	if(FileExplorerUtils.isProtected(file))
    	{
    		new Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(getString(R.string.access_denied))
			.setMessage(getString(R.string.cant_open_dir, file.getName()))
			.show();
    	}
    	else if(file.isDirectory())
        {
       	 listContents(file );
        }
        else
        {
       	 openFile(file);
        }
    }
	private void openFile(File file) {
		if(FileExplorerUtils.isProtected(file) || file.isDirectory())
		{
			return;
		}
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
		intent.setDataAndType(uri,type==null?"*/*":type);
		startActivity((Intent.createChooser(intent, getString(R.string.open_using))));
	}
	
    public void listContents(File dir)
    {
    	if(!dir.isDirectory() || FileExplorerUtils.isProtected(dir))
    	{
    		return;
    	}
    	new Finder(this ).execute(dir);
	}

    @Override
    public void onBackPressed() {
    	
    	if(FileExplorerUtils.isRoot(currentDir))
    	{
    		finish();
    	}
    	else
    	{
    		listContents(currentDir.getParentFile() );
    	}
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.options_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

    	 if(FileExplorerUtils.canPaste(currentDir))
         {
    		 menu.findItem(R.id.menu_paste).setVisible(true);
         }
    	 else
    	 {
    		 menu.findItem(R.id.menu_paste).setVisible(false);
    	 }
    	return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	
    	switch (item.getItemId()) {
		case  R.id.menu_paste:
			confirmPaste();
			return true;

		case R.id.menu_refresh:
			refresh();
			return true;
		case R.id.menu_newfolder:
			confirmCreateFolder();
        	return true;
        	
		case R.id.menu_settings:
			Intent prefsIntent = new Intent(FileExplorerMain.this, SettingsActivity.class);
			startActivity(prefsIntent);
			return true;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
        
        return true;
    }
    
	private void confirmPaste() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.confirm));
		alert.setMessage(getString(R.string.confirm_paste_text, FileExplorerUtils.getFileToPaste().getName()));
		alert.setIcon(android.R.drawable.ic_dialog_alert);

		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		 
				dialog.dismiss();
				new FileMover(FileExplorerMain.this,FileExplorerUtils.getPasteMode()).execute(currentDir);
			}
		});

		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		   
			  dialog.dismiss();
		  }
		});

		alert.show();
		
		
	}
	private void confirmCreateFolder() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.create_folder));
		alert.setIcon(android.R.drawable.ic_dialog_info);
		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setHint(getString(R.string.enter_folder_name));
		input.setSingleLine();
		alert.setView(input);

		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  CharSequence newDir = input.getText();
		  if(FileExplorerUtils.mkDir(currentDir.getAbsolutePath(), newDir))
		  {
			  listContents(currentDir );
		  }
		  }
		});

		alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		   
			  dialog.dismiss();
		  }
		});

		alert.show();
		
	}


	void deletePath(File path)
	{
		new Trasher(this).execute(path);
	}
	public void setCurrentDir(File dir)
	{
		currentDir = dir;
	}
	
	public void setNewChildren(List<FileListEntry> children )
	{
		TextView emptyText = (TextView)findViewById(android.R.id.empty);
		if(emptyText!=null)
		{
			emptyText.setText(R.string.empty_folder);
		}
		files.clear();
		files.addAll(children);
		adapter.notifyDataSetChanged();	
	}

	public void refresh() {
		listContents(currentDir);
	}
	
	
}