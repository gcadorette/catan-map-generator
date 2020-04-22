package Game;

import Board.Board;
import Board.Edge;
import Board.Vertex;
import Board.Port;
import Players.Player;
import util.Counter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Board.Port.*;

public class GameState
{
    private List<Player> _players;
    private Board _board;
    private Map<Player, Counter> _points;

    public GameState(List<Player> players, Board board, Map<Player, Counter> points)
    {
        _players = players;
        _board = board;
        _points = points;
    }

    public Player ownerOf(Edge edge)
    {
        for (Player player : _players)
        {
            if (player.getRoads().contains(edge))
                return player;
        }
        return null;
    }

    public Player ownerOf(Vertex vertex)
    {
        for (Player player : _players)
        {
            if (player.getVertices().contains(vertex))
                return player;
        }
        return null;
    }

    public Board getBoard()
    {
        return _board;
    }

    public List<Port> getPortsOf(Player player)
    {
        List<Port> list = new ArrayList<>();
        for (Port port : ALL_PORTS)
        {
            if (player.getVertices().contains(port.getVertex()))
                list.add(port);
        }
        return list;
    }

    public int pointsOf(Player player)
    {
        if (player == null)
            return 0;
        return _points.get(player).get();
    }
}
