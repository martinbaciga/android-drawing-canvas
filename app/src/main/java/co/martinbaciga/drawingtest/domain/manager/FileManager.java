package co.martinbaciga.drawingtest.domain.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager
{
	public static Uri saveBitmap(Context context, Bitmap bitmap)
	{
		String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DrawingCanvas";
		File dir = new File(file_path);

		if(!dir.exists())
		{
			dir.mkdirs();
		}

		String name = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).concat(".png");
		File file = new File(dir, name);

		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
			return FileProvider.getUriForFile(context,
					context.getApplicationContext().getPackageName() + ".provider", file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
