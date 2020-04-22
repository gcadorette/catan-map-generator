package Board;

import Board.Resources.Resource;

import static Board.Coordinate.*;
import static Board.Resources.Resource.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BoardGenerator
{

    private static ArrayList<Coordinate> _possibleCoordinates;
    private static ArrayList<Resource> _possibleResources;
    private static ArrayList<Integer> _possibleDiceValue;

    public static BoardConfiguration generateRandomMap()
    {
        ArrayList<Coordinate> coordinates = new ArrayList<>(_possibleCoordinates);
        ArrayList<Resource> resources = new ArrayList<>(_possibleResources);
        ArrayList<Integer> diceValues = new ArrayList<>(_possibleDiceValue);

        Collections.shuffle(coordinates);
        Collections.shuffle(resources);
        Collections.shuffle(diceValues);

        Set<Tile> tiles = new HashSet<>();
        while (!coordinates.isEmpty())
        {
            Coordinate coordinate = coordinates.remove(coordinates.size() - 1);
            Resource resource = resources.remove(resources.size() - 1);
            Integer diceValue = resource == Desert ? 0 : diceValues.remove(diceValues.size() - 1);

            tiles.add(new Tile(coordinate, diceValue, resource));
        }


        return new BoardConfiguration(tiles);
    }

    static {
        _possibleCoordinates = new ArrayList<>();
        _possibleResources = new ArrayList<>();
        _possibleDiceValue = new ArrayList<>();


        _possibleCoordinates.add(p00);
        _possibleCoordinates.add(p10);
        _possibleCoordinates.add(p20);
        _possibleCoordinates.add(p01);
        _possibleCoordinates.add(p11);
        _possibleCoordinates.add(p21);
        _possibleCoordinates.add(p31);
        _possibleCoordinates.add(p02);
        _possibleCoordinates.add(p12);
        _possibleCoordinates.add(p22);
        _possibleCoordinates.add(p32);
        _possibleCoordinates.add(p42);
        _possibleCoordinates.add(p13);
        _possibleCoordinates.add(p23);
        _possibleCoordinates.add(p33);
        _possibleCoordinates.add(p43);
        _possibleCoordinates.add(p24);
        _possibleCoordinates.add(p34);
        _possibleCoordinates.add(p44);



        _possibleResources.add(Brick);
        _possibleResources.add(Brick);
        _possibleResources.add(Brick);

        _possibleResources.add(Lumber);
        _possibleResources.add(Lumber);
        _possibleResources.add(Lumber);
        _possibleResources.add(Lumber);

        _possibleResources.add(Wool);
        _possibleResources.add(Wool);
        _possibleResources.add(Wool);
        _possibleResources.add(Wool);

        _possibleResources.add(Grain);
        _possibleResources.add(Grain);
        _possibleResources.add(Grain);
        _possibleResources.add(Grain);

        _possibleResources.add(Ore);
        _possibleResources.add(Ore);
        _possibleResources.add(Ore);

        _possibleResources.add(Desert);



        _possibleDiceValue.add(2);
        _possibleDiceValue.add(3);
        _possibleDiceValue.add(3);
        _possibleDiceValue.add(4);
        _possibleDiceValue.add(4);
        _possibleDiceValue.add(5);
        _possibleDiceValue.add(5);
        _possibleDiceValue.add(6);
        _possibleDiceValue.add(6);
        _possibleDiceValue.add(8);
        _possibleDiceValue.add(8);
        _possibleDiceValue.add(9);
        _possibleDiceValue.add(9);
        _possibleDiceValue.add(10);
        _possibleDiceValue.add(10);
        _possibleDiceValue.add(11);
        _possibleDiceValue.add(11);
        _possibleDiceValue.add(12);
    }
}
