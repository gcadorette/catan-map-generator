package Board;

import java.util.*;

public class Coordinate implements Comparable<Coordinate>
{
    public static final Coordinate p00 = new Coordinate(0, 0);
    public static final Coordinate p10 = new Coordinate(1, 0);
    public static final Coordinate p20 = new Coordinate(2, 0);
    public static final Coordinate p01 = new Coordinate(0, 1);
    public static final Coordinate p11 = new Coordinate(1, 1);
    public static final Coordinate p21 = new Coordinate(2, 1);
    public static final Coordinate p31 = new Coordinate(3, 1);
    public static final Coordinate p02 = new Coordinate(0, 2);
    public static final Coordinate p12 = new Coordinate(1, 2);
    public static final Coordinate p22 = new Coordinate(2, 2);
    public static final Coordinate p32 = new Coordinate(3, 2);
    public static final Coordinate p42 = new Coordinate(4, 2);
    public static final Coordinate p13 = new Coordinate(1, 3);
    public static final Coordinate p23 = new Coordinate(2, 3);
    public static final Coordinate p33 = new Coordinate(3, 3);
    public static final Coordinate p43 = new Coordinate(4, 3);
    public static final Coordinate p24 = new Coordinate(2, 4);
    public static final Coordinate p34 = new Coordinate(3, 4);
    public static final Coordinate p44 = new Coordinate(4, 4);


    public static final Coordinate w_12 = new Coordinate(-1, 2);
    public static final Coordinate w_11 = new Coordinate(-1, 1);
    public static final Coordinate w_10 = new Coordinate(-1, 0);
    public static final Coordinate w_1_1 = new Coordinate(-1, -1);
    public static final Coordinate w0_1 = new Coordinate(0, -1);
    public static final Coordinate w1_1 = new Coordinate(1, -1);
    public static final Coordinate w2_1 = new Coordinate(2, -1);
    public static final Coordinate w30 = new Coordinate(3, 0);
    public static final Coordinate w41 = new Coordinate(4, 1);
    public static final Coordinate w52 = new Coordinate(5, 2);
    public static final Coordinate w53 = new Coordinate(5, 3);
    public static final Coordinate w54 = new Coordinate(5, 4);
    public static final Coordinate w55 = new Coordinate(5, 5);
    public static final Coordinate w45 = new Coordinate(4, 5);
    public static final Coordinate w35 = new Coordinate(3, 5);
    public static final Coordinate w25 = new Coordinate(2, 5);
    public static final Coordinate w14 = new Coordinate(1, 4);
    public static final Coordinate w03 = new Coordinate(0, 3);


    public static final Coordinate NULL_POSITION = new Coordinate(100, 100);

    private final int _x, _y;
    private static final List<Coordinate> allCoordinates;

    public Coordinate(int x, int y)
    {
        _x = x;
        _y = y;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public Coordinate getNW()
    {
        return allCoordinates.stream().filter(coordinate -> coordinate._x == _x - 1 && coordinate._y == _y - 1).findFirst().orElse(NULL_POSITION);
    }

    public Coordinate getNE()
    {
        return allCoordinates.stream().filter(coordinate -> coordinate._x == _x && coordinate._y == _y - 1).findFirst().orElse(NULL_POSITION);
    }

    public Coordinate getE()
    {
        return allCoordinates.stream().filter(coordinate -> coordinate._x == _x + 1 && coordinate._y == _y).findFirst().orElse(NULL_POSITION);
    }

    public Coordinate getSE()
    {
        return allCoordinates.stream().filter(coordinate -> coordinate._x == _x + 1 && coordinate._y == _y + 1).findFirst().orElse(NULL_POSITION);
    }

    public Coordinate getSW()
    {
        return allCoordinates.stream().filter(coordinate -> coordinate._x == _x && coordinate._y == _y + 1).findFirst().orElse(NULL_POSITION);
    }

    public Coordinate getW()
    {
        return allCoordinates.stream().filter(coordinate -> coordinate._x == _x - 1 && coordinate._y == _y).findFirst().orElse(NULL_POSITION);
    }
    

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return _x == that._x &&
               _y == that._y;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(_x, _y);
    }

    @Override
    public int compareTo(Coordinate o)
    {
        if (_y < o._y)
            return -1;
        if (_y > o._y)
            return 1;
        return _x < o._x ? -1 : 1;
    }
    @Override
    public String toString()
    {
        return "(" + _x + "," + _y + ")";
    }

    static
    {
        allCoordinates = new ArrayList<>();
        allCoordinates.add(pos(0, 0));
        allCoordinates.add(pos(1, 0));
        allCoordinates.add(pos(2, 0));
        allCoordinates.add(pos(0, 1));
        allCoordinates.add(pos(1, 1));
        allCoordinates.add(pos(2, 1));
        allCoordinates.add(pos(3, 1));
        allCoordinates.add(pos(0, 2));
        allCoordinates.add(pos(1, 2));
        allCoordinates.add(pos(2, 2));
        allCoordinates.add(pos(3, 2));
        allCoordinates.add(pos(4, 2));
        allCoordinates.add(pos(1, 3));
        allCoordinates.add(pos(2, 3));
        allCoordinates.add(pos(3, 3));
        allCoordinates.add(pos(4, 3));
        allCoordinates.add(pos(2, 4));
        allCoordinates.add(pos(3, 4));
        allCoordinates.add(pos(4, 4));
    }

    private static Coordinate pos(int x, int y)
    {
        return new Coordinate(x, y);
    }
}
