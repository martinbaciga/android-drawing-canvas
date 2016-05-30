package co.martinbaciga.drawingtest.infrastructure.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import co.martinbaciga.drawingtest.domain.application.DrawingCanvasApplication;

public class SharedPreferencesManager
{
	public static final String DEFAULT_FILE_NAME = DrawingCanvasApplication.getInstance().getPackageName() + ".sharedPreferences";

	private static SharedPreferences getSharedPreferences(Context context)
	{
		return context.getSharedPreferences(DEFAULT_FILE_NAME, Context.MODE_PRIVATE);
	}

	public static void save(Context context, String key, boolean value)
	{
		getSharedPreferences(context).edit().putBoolean(key, value).apply();
	}

	public static void save(Context context, String key, String value)
	{
		getSharedPreferences(context).edit().putString(key, value).apply();
	}

	public static void save(Context context, String key, int value)
	{
		getSharedPreferences(context).edit().putInt(key, value).apply();
	}

	public static boolean getBoolean(Context context, String key)
	{
		return getSharedPreferences(context).getBoolean(key, false);
	}

	public static boolean getBoolean(Context context, String key, boolean defaultValue)
	{
		return getSharedPreferences(context).getBoolean(key, defaultValue);
	}

	public static String getString(Context context, String key)
	{
		return getString(context, key, "");
	}

	public static String getString(Context context, String key, String defaultValue)
	{
		return getSharedPreferences(context).getString(key, defaultValue);
	}

	public static int getInt(Context context, String key)
	{
		return getInt(context, key, -1);
	}

	public static int getInt(Context context, String key, int defaultValue)
	{
		return getSharedPreferences(context).getInt(key, defaultValue);
	}

	public static <T> void save(Context context, String key, T model)
	{
		String json = new Gson().toJson(model);
		getSharedPreferences(context).edit().putString(key, json).apply();
	}

	public static <T> T get(Context context, String key, Class<T> type)
	{
		String json = getString(context, key);
		return json != "" ? new Gson().fromJson(getString(context, key), type) : null;
	}

	public static void clear(Context context)
	{
		getSharedPreferences(context).edit().clear().apply();
	}

	public static void remove(Context context, String key)
	{
		getSharedPreferences(context).edit().remove(key).apply();
	}
}
