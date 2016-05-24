package co.martinbaciga.drawingtest.ui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class UiUtils
{
	public static Bitmap getBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		v.draw(c);
		return b;
	}
}
