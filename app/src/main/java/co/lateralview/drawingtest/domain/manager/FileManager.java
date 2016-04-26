package co.lateralview.drawingtest.domain.manager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager
{
	public static void saveBitmap(Bitmap bitmap)
	{
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DrawingCanvas";
		File dir = new File(file_path);

		if(!dir.exists())
		{
			dir.mkdirs();
		}

		String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File file = new File(dir, name);

		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
