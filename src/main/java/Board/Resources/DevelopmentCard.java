package Board.Resources;

import Players.Player;

public enum DevelopmentCard
{
    Knight,
    Monopoly,
    Road,
    VictoryPoint,
    YearsOfPlenty;


    public static Resource[] getCost()
    {
        return new Resource[] {Resource.Wool, Resource.Grain, Resource.Ore};
    }

}
