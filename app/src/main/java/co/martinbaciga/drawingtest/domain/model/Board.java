package co.martinbaciga.drawingtest.domain.model;

public class Board
{
	private int backgroundColor;

	// Required default constructor for Firebase serialization / deserialization
	@SuppressWarnings("unused")
	private Board() {
	}

	public Board(int mBackgroundColor)
	{
		this.backgroundColor = mBackgroundColor;
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public void setBackgroundColor(int mBackgroundColor)
	{
		this.backgroundColor = mBackgroundColor;
	}
}
