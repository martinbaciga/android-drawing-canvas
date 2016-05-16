package co.martinbaciga.drawingtest.ui.util;

import android.graphics.Bitmap;
import android.view.View;

public class UiUtils
{
	public static Bitmap getBitmapFromView(View v)
	{
		v.setDrawingCacheEnabled(true);
		v.buildDrawingCache();
		return v.getDrawingCache();
	}
}
