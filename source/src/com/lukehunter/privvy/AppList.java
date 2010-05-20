package com.lukehunter.privvy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleExpandableListAdapter;

// credit: http://mylifewithandroid.blogspot.com/2008/05/expandable-lists.html

public class AppList extends ExpandableListActivity
{
    static String permissions[];
	static String apps[][];
//// Shades of grey
//	  {
//		"lightgrey","#D3D3D3",
//		"dimgray","#696969",
//		"sgi gray 92","#EAEAEA"
//	  },
//// Shades of blue
//	  {
//		"dodgerblue 2","#1C86EE",
//		"steelblue 2","#5CACEE",
//		"powderblue","#B0E0E6"
//	  },
//// Shades of yellow
//	  {
//		"yellow 1","#FFFF00",
//		"gold 1","#FFD700",
//		"darkgoldenrod 1","	#FFB90F"
//	  },
//// Shades of red
//	  {
//		"indianred 1","#FF6A6A",
//		"firebrick 1","#FF3030",
//		"maroon","#800000"
//	  }
//    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        
        HashMap<String, List<String>> dbg = buildMasterList();
        
		SimpleExpandableListAdapter expListAdapter =
			new SimpleExpandableListAdapter(
				this,
				createGroupList("permissionName", buildPermissionList(dbg)),	// groupData describes the first-level entries
				R.layout.child_row,	// Layout for the first-level entries
				new String[] { "permissionName" },	// Key in the groupData maps to display
				new int[] { R.id.childname },		// Data under "colorName" key goes into this TextView
				createChildList("appName", buildAppGroups(dbg)),	// childData describes second-level entries
				R.layout.child_row,	// Layout for second-level entries
				new String[] { "appName" },	// Keys in childData maps to display
				new int[] { R.id.childname }	// Data under the keys above go into these TextViews
			);
		setListAdapter( expListAdapter );
    }
    
    private HashMap<String, List<String>> buildMasterList() {
    	HashMap<String, List<String>> result = new HashMap<String, List<String>>();
    	
    	List<PackageInfo> packages = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
    	
    	for (int i = 0; i < packages.size(); i++) {
    		String[] packagePermissions = packages.get(i).requestedPermissions;
    		Log.d("AppList", packages.get(i).packageName);
    		if (packagePermissions != null) {
	    		for (int j = 0; j < packagePermissions.length; j++) {
	    			if (!result.containsKey(packagePermissions[j])) {
	    				result.put(packagePermissions[j], new ArrayList<String>());
	    			}
	    			result.get(packagePermissions[j]).add(packages.get(i).packageName);
	    		}
    		}
    		else {
    			Log.d("AppList", packages.get(i).packageName + ": no permissions");
    		}
    	}
    	
    	return result;
    }
    
    private static String[] buildPermissionList(HashMap<String, List<String>> masterList) {
    	String[] result = new String[masterList.keySet().size()];
    	
    	Iterator<String> i = masterList.keySet().iterator();
    	int curIdx = 0;
    	
    	while (i.hasNext()) {
    		result[curIdx] = i.next();
    		curIdx++;
    	}
    	
    	return result;
    }
    
    private static String[][] buildAppGroups(HashMap<String, List<String>> masterList) {
    	String[][] result = new String[masterList.keySet().size()][];
    	Iterator<String> i = masterList.keySet().iterator();
    	int curIdx = 0;
    	
    	while (i.hasNext()) {
    		String curPerm = i.next();
    		result[curIdx] = new String[masterList.get(curPerm).size()];
    		for (int j = 0; j < masterList.get(curPerm).size(); j++) {
    			result[curIdx][j] = masterList.get(curPerm).get(j);
    		}
    		curIdx++;
    	}
    	
    	return result;
    }
    
    private List<String> findAppsWithPermission(PermissionGroupInfo permission) {
    	List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
    	List<PackageInfo> packagesWithPermission = new ArrayList<PackageInfo>();
    	List<String> result = new ArrayList<String>();
    	
    	for (int i = 0; i < packages.size(); i++) {
    		String[] packagePermissions = packages.get(i).requestedPermissions;
    		for (int j = 0; j < packagePermissions.length; j++) {
    			if (packagePermissions[j] == permission.name) {
    				packagesWithPermission.add(packages.get(i));
    				break;
    			}
    		}
    	}
    	
    	for (int i = 0; i < packagesWithPermission.size(); i++) {
    		result.add(packagesWithPermission.get(i).packageName);
    	}
    	
    	return result;
    }

/**
  * Creates the group list out of the colors[] array according to
  * the structure required by SimpleExpandableListAdapter. The resulting
  * List contains Maps. Each Map contains one entry with key "colorName" and
  * value of an entry in the colors[] array.
  */
	private static List createGroupList(String key, String[] groups) {
	  ArrayList result = new ArrayList();
	  for( int i = 0 ; i < groups.length ; ++i ) {
		HashMap m = new HashMap();
	    m.put( key,groups[i] );
		result.add( m );
	  }
	  return (List)result;
    }

/**
  * Creates the child list out of the shades[] array according to the
  * structure required by SimpleExpandableListAdapter. The resulting List
  * contains one list for each group. Each such second-level group contains
  * Maps. Each such Map contains two keys: "shadeName" is the name of the
  * shade and "rgb" is the RGB value for the shade.
  */
  private static List createChildList(String key, String[][] children) {
	ArrayList result = new ArrayList();
	for( int i = 0 ; i < children.length ; ++i ) {
// Second-level lists
	  ArrayList secList = new ArrayList();
	  for( int n = 0 ; n < children[i].length ; n += 2 ) {
	    HashMap child = new HashMap();
		child.put( key, children[i][n] );
		secList.add( child );
	  }
	  result.add( secList );
	}
	return result;
  }

}
