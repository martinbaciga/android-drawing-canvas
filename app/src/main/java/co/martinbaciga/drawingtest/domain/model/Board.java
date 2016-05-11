package co.martinbaciga.drawingtest.domain.model;

public class Board
{
	private int mBackgroundColor;

	// Required default constructor for Firebase serialization / deserialization
	@SuppressWarnings("unused")
	private Board() {
	}

	public Board(int mBackgroundColor)
	{
		this.mBackgroundColor = mBackgroundColor;
	}

	public int getBackgroundColor()
	{
		return mBackgroundColor;
	}

	public void setBackgroundColor(int mBackgroundColor)
	{
		this.mBackgroundColor = mBackgroundColor;
	}
}
