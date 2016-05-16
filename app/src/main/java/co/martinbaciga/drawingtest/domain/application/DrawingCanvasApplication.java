package co.martinbaciga.drawingtest.domain.application;

import android.app.Application;

import com.firebase.client.Firebase;

import co.martinbaciga.drawingtest.infrastructure.FireBaseDBConstants;

public class DrawingCanvasApplication extends Application
{
	private static DrawingCanvasApplication sInstance;
	private static Firebase sFirebaseRef;

	@Override
	public void onCreate()
	{
		super.onCreate();
		sInstance = this;

		initializeServices();
	}

	public static DrawingCanvasApplication getInstance()
	{
		return sInstance;
	}

	private void initializeServices()
	{
		Firebase.setAndroidContext(this);
		sFirebaseRef = new Firebase(FireBaseDBConstants.FIREBASE_DB_URL);
	}

	public Firebase getFirebaseRef()
	{
		return sFirebaseRef;
	}
}