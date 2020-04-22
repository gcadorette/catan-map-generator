package Board.Resources;

public class Road extends Construction
{
    public Road(int coordX, int coordY, int position){
        super(coordX, coordY, position);
    }

    public static Resource[] getCost()
    {
        return new Resource[]{Resource.Lumber, Resource.Brick};
    }
}
