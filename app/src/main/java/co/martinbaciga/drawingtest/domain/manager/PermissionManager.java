package co.martinbaciga.drawingtest.domain.manager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager
{
	public static final int REQUEST_WRITE_STORAGE = 112;

	public static boolean checkWriteStoragePermissions(Activity activity)
	{
		boolean hasPermission = (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
		if (!hasPermission) {
			ActivityCompat.requestPermissions(activity,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					REQUEST_WRITE_STORAGE);
		}
		return hasPermission;
	}
}
