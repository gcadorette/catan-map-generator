package Board.Resources;

public abstract class Construction
{
    private int coordX;
    private int coordY;
    private int position;
    public Construction(int coordX, int coordY, int position)
    {
        this.coordX = coordX;
        this.coordY = coordY;
        this.position = position;
    }
}
