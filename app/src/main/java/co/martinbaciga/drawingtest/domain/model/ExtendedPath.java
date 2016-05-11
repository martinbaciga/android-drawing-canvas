package co.martinbaciga.drawingtest.domain.model;

import android.graphics.Path;

public class ExtendedPath
{
	private Path path;
	private String segmentId;

	public ExtendedPath(Path path, String segmentId)
	{
		this.path = path;
		this.segmentId = segmentId;
	}

	public Path getPath()
	{
		return path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}

	public String getSegmentId()
	{
		return segmentId;
	}

	public void setSegmentId(String segmentId)
	{
		this.segmentId = segmentId;
	}
}
