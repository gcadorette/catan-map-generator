package Board;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static Board.Coordinate.*;
import static Board.Resources.Resource.*;
import static org.junit.Assert.*;

public class BoardConfigurationTest
{

    private Set<Tile> _validTiles;

    @Before
    public void setup()
    {
        initTiles();
    }

    @Test
    public void validBoardIsValid()
    {
        BoardConfiguration validBoard = new BoardConfiguration(_validTiles);
        assertTrue(validBoard.isValid());
    }

    @Test
    public void emptyBoardIsNoValid()
    {
        assertFalse(new BoardConfiguration(new HashSet<>()).isValid());
    }

    @Test
    public void badNumberOfResource()
    {
        _validTiles.remove(0);
        _validTiles.add(new Tile(p00, 2, Wool));
        BoardConfiguration invalidBoard = new BoardConfiguration(_validTiles);
        assertFalse(invalidBoard.isValid());
    }

    @Test
    public void coordinatesNotUnique()
    {
        _validTiles.remove(0);
        _validTiles.add(new Tile(p22, 2, Brick));
        BoardConfiguration invalidBoard = new BoardConfiguration(_validTiles);
        assertFalse(invalidBoard.isValid());
    }

    @Test
    public void badNumberOfDiceValue()
    {
        _validTiles.remove(0);
        _validTiles.add(new Tile(p00, 3, Brick));
        BoardConfiguration invalidBoard = new BoardConfiguration(_validTiles);
        assertFalse(invalidBoard.isValid());
    }

    private void initTiles()
    {
        _validTiles = new HashSet<>();

        _validTiles.add(new Tile(p00, 2, Brick));
        _validTiles.add(new Tile(p10, 3, Brick));
        _validTiles.add(new Tile(p20, 3, Brick));
        _validTiles.add(new Tile(p01, 4, Lumber));
        _validTiles.add(new Tile(p11, 4, Lumber));
        _validTiles.add(new Tile(p21, 5, Lumber));
        _validTiles.add(new Tile(p31, 5, Lumber));
        _validTiles.add(new Tile(p02, 6, Wool));
        _validTiles.add(new Tile(p12, 6, Wool));
        _validTiles.add(new Tile(p22, 8, Wool));
        _validTiles.add(new Tile(p32, 8, Wool));
        _validTiles.add(new Tile(p42, 9, Grain));
        _validTiles.add(new Tile(p13, 9, Grain));
        _validTiles.add(new Tile(p23, 10, Grain));
        _validTiles.add(new Tile(p33, 10, Grain));
        _validTiles.add(new Tile(p43, 11, Ore));
        _validTiles.add(new Tile(p24, 11, Ore));
        _validTiles.add(new Tile(p34, 12, Ore));
        _validTiles.add(new Tile(p44, 0, Desert));

    }
}