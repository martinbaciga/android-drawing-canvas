package co.martinbaciga.drawingtest.domain.model;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

public class ExtendedPath
{
	private Path path;
	private List<Point> points;
	private Paint paint;
	private String segmentId;

	public ExtendedPath(Path path, List<Point> points, String segmentId)
	{
		this.path = path;
		this.points = points;
		this.segmentId = segmentId;
	}

	public ExtendedPath(Path path, List<Point> points, Paint paint, String segmentId)
	{
		this.path = path;
		this.points = points;
		this.paint = paint;
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

	public List<Point> getPoints()
	{
		return points;
	}

	public void setPoints(List<Point> points)
	{
		this.points = points;
	}

	public Paint getPaint()
	{
		return paint;
	}

	public void setPaint(Paint paint)
	{
		this.paint = paint;
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
