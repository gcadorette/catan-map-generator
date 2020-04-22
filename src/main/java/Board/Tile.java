package Board;


import Board.Resources.Resource;

import java.util.Objects;

public class Tile implements Comparable<Tile>
{
    private Coordinate _position;
    private int _nbr;
    private Resource _resource;

    private boolean _isActive;

    public Tile(Coordinate position, int nbr, Resource resource)
    {
        _position = position;
        _nbr = nbr;
        _resource = resource;

        _isActive = true;
    }

    public Resource getResource()
    {
        return _resource;
    }

    public int getNbr()
    {
        return _nbr;
    }

    public Coordinate getPosition()
    {
        return _position;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return _nbr == tile._nbr &&
               _position.equals(tile._position) &&
               _resource == tile._resource;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(_position, _nbr, _resource);
    }

    @Override
    public int compareTo(Tile o)
    {
        return _position.compareTo(o.getPosition());
    }

    public boolean isActive()
    {
        return _isActive;
    }

    public void setActive(boolean active)
    {
        _isActive = active;
    }

    @Override
    public String toString()
    {
        return _nbr + " " + _resource;
    }
}
