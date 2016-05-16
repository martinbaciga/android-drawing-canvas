package co.martinbaciga.drawingtest.ui.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class SystemUtils
{
	public static int convertDpToPixel(float dp, Context context)
	{
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}
}
