package Board;

import com.google.common.collect.Sets;

import java.util.*;

import static Board.EdgeOrientation.*;

public class Edge
{
    private Coordinate _position;

    private EdgeOrientation _orientation;

    public Edge(Coordinate position, EdgeOrientation orientation)
    {
        _position = position;
        _orientation = orientation;
    }

    public static Set<Edge> getNeighbors(Edge edge)
    {
        Coordinate pos = edge._position;
        if (edge._orientation == NORTH_WEST)
        {
            return Sets.newHashSet(
                    new Edge(pos, NORTH_EAST),
                    new Edge(pos.getW(), NORTH_EAST),
                    new Edge(pos.getW(), EAST),
                    new Edge(pos.getNW(), EAST));
        }

        if (edge._orientation == NORTH_EAST)
        {
            return Sets.newHashSet(
                    new Edge(pos, NORTH_WEST),
                    new Edge(pos, EAST),
                    new Edge(pos.getE(), NORTH_WEST),
                    new Edge(pos.getNW(), EAST));
        }

        if (edge._orientation == EAST)
        {
            return Sets.newHashSet(
                    new Edge(pos, NORTH_EAST),
                    new Edge(pos.getE(), NORTH_WEST),
                    new Edge(pos.getSE(), NORTH_EAST),
                    new Edge(pos.getSE(), NORTH_WEST));
        }
        return new HashSet<>();
    }

    public static boolean areNeighbors(Edge e1, Edge e2)
    {
        return oneWayCheck(e1, e2) || oneWayCheck(e2, e1);
    }

    private static boolean oneWayCheck(Edge e1, Edge e2)
    {
        if (e1._position == e2._position)
        {
            return e1._orientation == NORTH_EAST || e2._orientation == NORTH_EAST;
        }

        if (e1._orientation == NORTH_WEST)
        {
            if (e2._position == e1._position.getW())
            {
                return e2._orientation == NORTH_EAST || e2._orientation == EAST;
            }

            if (e2._position == e1._position.getNW())
            {
                return e2._orientation == EAST;
            }
        }

        if (e1._orientation == NORTH_EAST)
        {
            if (e2._position == e1._position.getE())
            {
                return e2._orientation == NORTH_WEST;
            }

            if (e2._position == e1._position.getNW())
            {
                return e2._orientation == EAST;
            }
        }

        if (e1._orientation == EAST)
        {
            if (e2._position == e1._position.getE())
            {
                return e2._orientation == NORTH_WEST;
            }

            if (e2._position == e1._position.getSW())
            {
                return e2._orientation == NORTH_WEST || e2._orientation == NORTH_EAST;
            }
        }
        return false;
    }

    public Coordinate getPosition()
    {
        return _position;
    }

    public EdgeOrientation getOrientation()
    {
        return _orientation;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return _position.equals(edge._position) &&
               _orientation == edge._orientation;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(_position, _orientation);
    }

    @Override
    public String toString()
    {
        return _position.getX() + " " + _position.getY() + " " + _orientation;
    }
}

