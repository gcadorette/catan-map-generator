package Board.Resources;

import static Board.Resources.Resource.*;

public class Building extends Construction
{
    private int points;
    private int amtOfResources;

    public Building(int coordX, int coordY, int position)
    {
        super(coordX, coordY, position);
        this.points = 1;
        this.amtOfResources = 1;
    }

    public int getPoints()
    {
        return points;
    }
    public int getAmtOfResources()
    {
        return amtOfResources;
    }
    public void upgrade(){
        points = 2;
        amtOfResources = 2;
    }

    public static Resource[] getSettlementCost()
    {
        return new Resource[]{Grain, Lumber, Wool, Brick};
    }

    public static Resource[] getCityCost()
    {
        return new Resource[]{Grain, Grain, Ore, Ore, Ore};
    }
}
