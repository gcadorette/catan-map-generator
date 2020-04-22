package Board;

import Board.Resources.Resource;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static Board.EdgeOrientation.*;

public class Board
{
    private static final int CATAN_WIDTH = 5;
    private Set<Tile> _tiles;

    public Board(BoardConfiguration boardConfiguration)
    {
        _tiles = boardConfiguration.getTiles();
    }

    public Set<Tile> getTiles()
    {
        return _tiles;
    }

    public Tile getDesert()
    {
        return _tiles.stream().filter(tile -> tile.getResource() == Resource.Desert).findFirst().get();
    }

    public Set<Edge> getNeighborsEdges(Tile tile)
    {
        Set<Edge> edges = new HashSet<Edge>();

        Coordinate currPos = tile.getPosition();
        edges.add(new Edge(currPos, NORTH_WEST));
        edges.add(new Edge(currPos, NORTH_EAST));
        edges.add(new Edge(currPos, EAST));
        edges.add(new Edge(currPos.getSE(), NORTH_WEST));
        edges.add(new Edge(currPos.getSW(), NORTH_EAST));
        edges.add(new Edge(currPos.getW(), EAST));

        return edges;
    }

    public Set<Vertex> getAllVertices()
    {
        Set<Vertex> vertices = new HashSet<>();
        for (Tile tile : _tiles)
        {
            vertices.add(new Vertex(tile.getPosition(), VertexOrientation.NORTH));
            vertices.add(new Vertex(tile.getPosition(), VertexOrientation.SOUTH));
        }
        return vertices;
    }

    public Set<Vertex> getNeighborsVertices(Tile tile)
    {
        Set<Vertex> vertices = new HashSet<Vertex>();
        Coordinate tilePos = tile.getPosition();
        Set<Vertex> allVertices = getAllVertices();

        allVertices.stream().filter(v -> v.getPosition().equals(tilePos.getNW())
                                         && v.getOrientation() == VertexOrientation.SOUTH).findFirst().ifPresent(vertices::add);
        allVertices.stream().filter(v -> v.getPosition().equals(tilePos)
                                         && v.getOrientation() == VertexOrientation.NORTH).findFirst().ifPresent(vertices::add);
        allVertices.stream().filter(v -> v.getPosition().equals(tilePos.getNE())
                                         && v.getOrientation() == VertexOrientation.SOUTH).findFirst().ifPresent(vertices::add);
        allVertices.stream().filter(v -> v.getPosition().equals(tilePos.getSE())
                                         && v.getOrientation() == VertexOrientation.NORTH).findFirst().ifPresent(vertices::add);
        allVertices.stream().filter(v -> v.getPosition().equals(tilePos)
                                         && v.getOrientation() == VertexOrientation.SOUTH).findFirst().ifPresent(vertices::add);
        allVertices.stream().filter(v -> v.getPosition().equals(tilePos.getSW())
                                         && v.getOrientation() == VertexOrientation.NORTH).findFirst().ifPresent(vertices::add);
        return vertices;

    }

    public Set<Tile> getNeighborsTiles(Vertex vertex)
    {
        Set<Tile> tiles = new HashSet<>();
        Coordinate vertexPosition = vertex.getPosition();
        if (vertex.getOrientation() == VertexOrientation.NORTH)
        {
            _tiles.stream().filter(tile -> tile.getPosition().equals(vertexPosition)).findFirst().ifPresent(tiles::add);
            _tiles.stream().filter(tile -> tile.getPosition().equals(vertexPosition.getNW())).findFirst().ifPresent(tiles::add);
            _tiles.stream().filter(tile -> tile.getPosition().equals(vertexPosition.getNE())).findFirst().ifPresent(tiles::add);
        }
        else if (vertex.getOrientation() == VertexOrientation.SOUTH)
        {
            _tiles.stream().filter(tile -> tile.getPosition().equals(vertexPosition)).findFirst().ifPresent(tiles::add);
            _tiles.stream().filter(tile -> tile.getPosition().equals(vertexPosition.getSW())).findFirst().ifPresent(tiles::add);
            _tiles.stream().filter(tile -> tile.getPosition().equals(vertexPosition.getSE())).findFirst().ifPresent(tiles::add);
        }
        return tiles;
    }

    public Set<Edge> getNeighborsEdges(Vertex vertex)
    {
        Set<Edge> edges = new HashSet<>();
        Coordinate coord = vertex.getPosition();

        if (vertex.getOrientation() == VertexOrientation.NORTH)
        {
            edges.add(new Edge(coord.getNW(), EAST));
            edges.add(new Edge(coord, NORTH_WEST));
            edges.add(new Edge(coord, NORTH_EAST));

        }
        else if (vertex.getOrientation() == VertexOrientation.SOUTH)
        {
            edges.add(new Edge(coord.getSW(), NORTH_EAST));
            edges.add(new Edge(coord.getSW(), EAST));
            edges.add(new Edge(coord.getSE(), NORTH_WEST));
        }

        return edges;
    }

    public Set<Vertex> getNeighborsVertices(Edge edge)
    {
        Set<Vertex> vertices = new HashSet<>();
        Coordinate coord = edge.getPosition();

        if (edge.getOrientation() == NORTH_WEST)
        {
            Vertex.ALL_VERTICES.stream().filter(vertex -> vertex.getPosition().equals(coord) && vertex.getOrientation() == VertexOrientation.NORTH).forEach(vertices::add);
            Vertex.ALL_VERTICES.stream().filter(vertex -> vertex.getPosition().equals(coord.getNW()) && vertex.getOrientation() == VertexOrientation.SOUTH).forEach(vertices::add);
        }
        else if (edge.getOrientation() == NORTH_EAST)
        {
            Vertex.ALL_VERTICES.stream().filter(vertex -> vertex.getPosition().equals(coord) && vertex.getOrientation() == VertexOrientation.NORTH).forEach(vertices::add);
            Vertex.ALL_VERTICES.stream().filter(vertex -> vertex.getPosition().equals(coord.getNE()) && vertex.getOrientation() == VertexOrientation.SOUTH).forEach(vertices::add);
        }
        else if (edge.getOrientation() == EAST)
        {
            Vertex.ALL_VERTICES.stream().filter(vertex -> vertex.getPosition().equals(coord.getNE()) && vertex.getOrientation() == VertexOrientation.SOUTH).forEach(vertices::add);
            Vertex.ALL_VERTICES.stream().filter(vertex -> vertex.getPosition().equals(coord.getSE()) && vertex.getOrientation() == VertexOrientation.NORTH).forEach(vertices::add);
        }

        return vertices;
    }

    public String vertexValue(Vertex vertex)
    {
        List<Tile> adjacentTile = Lists.newArrayList(getNeighborsTiles(vertex));
        StringBuilder result = new StringBuilder();
        result.append(adjacentTile.remove(0));
        for (Tile tile : adjacentTile)
        {
            result.append(", ");
            result.append(tile.toString());
        }
        return result.toString();
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        ArrayList<Tile> tiles = new ArrayList<>(_tiles);
        tiles.sort(Tile::compareTo);

        int index = 0;
        int indexOfLine = index;
        for (int i = 0; i < (tiles.size() / CATAN_WIDTH) + 2; i++)
        {
            int amtOfElemPerLine = CATAN_WIDTH - Math.abs(2 - i);
            StringBuilder spacer = new StringBuilder();
            for (int k = 0; k < (CATAN_WIDTH - amtOfElemPerLine) * 4; k++)
            {
                spacer.append(" ");
            }
            str.append(spacer);
            for (int j = 0; j < amtOfElemPerLine; j++)
            {
                Tile tile = (Tile) tiles.toArray()[index];
                if (tile.getNbr() >= 10)
                    str.append(" ").append(tile.getResource().toString().charAt(0)).append("(").append(tile.getNbr()).append(") |");
                else
                    str.append(" ").append(tile.getResource().toString().charAt(0)).append("(0").append(tile.getNbr()).append(") |");
                index++;
            }
            index = indexOfLine;
            str.append("\n");
            str.append(spacer);
            for (int j = 0; j < amtOfElemPerLine; j++)
            {
                Tile tile = tiles.get(index);
                str.append(" ").append(tile.getPosition().toString()).append(" |");
                index++;
            }
            str.append("\n\n");
            indexOfLine = index;
        }
        return str.toString();
    }

}
