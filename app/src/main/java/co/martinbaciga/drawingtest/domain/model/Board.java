package co.martinbaciga.drawingtest.domain.model;

public class Board
{
	private int backgroundColor;
	private String backgroundImageUrl;

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

	public String getBackgroundImageUrl()
	{
		return backgroundImageUrl;
	}

	public void setBackgroundImageUrl(String backgroundImageUrl)
	{
		this.backgroundImageUrl = backgroundImageUrl;
	}
}
