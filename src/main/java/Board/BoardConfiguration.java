package Board;

import Board.Resources.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static Board.Resources.Resource.*;

public class BoardConfiguration
{
    private Set<Tile> _tiles;

    public BoardConfiguration(Set<Tile> tiles)
    {
        _tiles = tiles;
    }


    public Set<Tile> getTiles()
    {
        return _tiles;
    }

    public boolean isValid()
    {
        List<Coordinate> coordinates = _tiles.stream().map(Tile::getPosition).collect(Collectors.toList());
        List<Resource> resources = _tiles.stream().map(Tile::getResource).collect(Collectors.toList());
        List<Integer> diceValues = _tiles.stream().map(Tile::getNbr).collect(Collectors.toList());

        if (coordinates.stream().distinct().count() != 19)
            return false;

        if (Collections.frequency(resources, Brick) != 3)
            return false;
        if (Collections.frequency(resources,Lumber) != 4)
            return false;
        if (Collections.frequency(resources, Wool) != 4)
            return false;
        if (Collections.frequency(resources, Grain) != 4)
            return false;
        if (Collections.frequency(resources, Ore) != 3)
            return false;

        if (Collections.frequency(diceValues, 2) != 1)
            return false;

        for (int i = 3; i <= 6; i++)
        {
            if (Collections.frequency(diceValues, i) != 2)
                return false;
        }
        for (int i = 8; i <= 11; i++)
        {
            if (Collections.frequency(diceValues, i) != 2)
                return false;
        }

        if (Collections.frequency(diceValues, 12) != 1)
            return false;

        return true;
    }
}
