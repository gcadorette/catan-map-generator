package Players;

import Board.Board;
import Board.Edge;
import Board.Port;
import Board.EdgeOrientation;
import Board.JSONBoardParser;
import Board.Resources.DevelopmentCard;
import Board.Resources.Resource;
import Game.GameAction.ActionType;
import Game.GameAction.TradeOffer;
import Game.GameState;
import Board.Vertex;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import Board.Coordinate;
import Board.VertexOrientation;
import Board.Tile;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import util.Counter;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static Board.Resources.DevelopmentCard.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntelligentAgentTest
{
    private IntelligentAgent _ia;
    private Player _p1;
    private Player _p2;
    private Player _p3;
    private IntelligentAgent _ia2;
    private IntelligentAgent _ia3;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() throws FileNotFoundException
    {
        String filename = "src" + File.separator + "test" + File.separator +
                          "resources" + File.separator + "boards" + File.separator + "boardConfiguration.json";
        Board board = new Board(JSONBoardParser.parseFile(filename));

        _ia = new IntelligentAgent();
        _p1 = new Player(_ia, 0);
        _ia2 = mock(IntelligentAgent.class);
        _ia3 = mock(IntelligentAgent.class);

        _p2 = new Player(_ia2, 2);
        _p3 = new Player(_ia3, 3);
        List<Player> players = new LinkedList<Player>();
        players.add(_p1);
        players.add(_p2);
        players.add(_p3);
        Map<Player, Counter> _points = new HashMap<>();
        _points.put(_p1, new Counter());
        _points.put(_p2, new Counter());
        _points.put(_p3, new Counter());

        GameState gameState = new GameState(players, board, _points);

        _p1.setGameState(gameState);
        _p2.setGameState(gameState);
        _p3.setGameState(gameState);
    }

    @Test
    public void canPlaceSettlement()
    {
        Set<Vertex> vertices = new HashSet<Vertex>();
        Vertex v1 = new Vertex(new Coordinate(2,1), VertexOrientation.NORTH);
        Vertex v2 = new Vertex(new Coordinate(3,2), VertexOrientation.SOUTH);
        Vertex v3 = new Vertex(new Coordinate(3, 4), VertexOrientation.NORTH);
        Vertex v4 = new Vertex(new Coordinate(2, 2), VertexOrientation.NORTH);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        vertices.add(v4);
        assertEquals(v1, _ia.placeSettlement(vertices));
    }

    @Test
    public void canPlaceSettlement2()
    {
        Set<Vertex> vertices = new HashSet<Vertex>();
        Vertex v1 = new Vertex(new Coordinate(1,2), VertexOrientation.SOUTH);
        Vertex v2 = new Vertex(new Coordinate(1, 0), VertexOrientation.SOUTH);
        Vertex v3 = new Vertex(new Coordinate(3, 2), VertexOrientation.NORTH);

        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
        assertEquals(v2, _ia.placeSettlement(vertices));
    }

    @Test
    public void canChoseRoad()
    {
        ArrayList<Resource> res = new ArrayList<Resource>();
        res.add(Resource.Brick);
        res.add(Resource.Lumber);
        assertEquals(ActionType.PLACE_ROAD, _ia.makeMove(res, new ArrayList<DevelopmentCard>()));
    }

    @Test
    public void canChoseSettlement()
    {
        ArrayList<Resource> res = new ArrayList<Resource>();
        res.add(Resource.Brick);
        res.add(Resource.Lumber);
        res.add(Resource.Wool);
        res.add(Resource.Grain);
        ActionType action = _ia.makeMove(res, new ArrayList<DevelopmentCard>());
        //We might chose to build a road since the list of choices isn't ordered.
        assertTrue(action == ActionType.PLACE_SETTLEMENT || action == ActionType.PLACE_ROAD);
    }

    @Test
    public void canUpgradeToCity()
    {
        Set<Vertex> vertices = new HashSet<Vertex>();
        Vertex v1 = new Vertex(new Coordinate(1,3), VertexOrientation.SOUTH);
        vertices.add(v1);
        _ia.placeSettlement(vertices);
        ArrayList<Resource> res = new ArrayList<Resource>();
        res.add(Resource.Grain);
        res.add(Resource.Grain);
        res.add(Resource.Ore);
        res.add(Resource.Ore);
        res.add(Resource.Ore);
        assertEquals(ActionType.UPGRADE_TO_CITY, _ia.makeMove(res, new ArrayList<DevelopmentCard>()));
    }

    @Test
    public void canBuyDevCard()
    {
        ArrayList<Resource> res = new ArrayList<Resource>();
        res.add(Resource.Grain);
        res.add(Resource.Ore);
        res.add(Resource.Wool);
        assertEquals(ActionType.BUY_DEVELOPMENT_CARD, _ia.makeMove(res, new LinkedList<DevelopmentCard>()));
    }

    @Test
    public void canBuyWithStrategy()
    {
        _p1.placeFirstSettlement();
        ArrayList<Resource> res = new ArrayList<Resource>();
        res.add(Resource.Lumber);
        assertEquals(ActionType.END_TURN, _ia.makeMove(res, new ArrayList<DevelopmentCard>()));
        res.add(Resource.Brick);
        res.add(Resource.Wool);
        res.add(Resource.Grain);
        assertEquals(ActionType.PLACE_ROAD, _ia.makeMove(res,  new ArrayList<DevelopmentCard>()));
    }

    @Test
    public void canPlayDevCard()
    {
        ArrayList<Resource> res = new ArrayList<Resource>();
        ArrayList<DevelopmentCard> dev = new ArrayList<DevelopmentCard>();
        dev.add(Knight);
        assertEquals(ActionType.PLAY_DEVELOPMENT_CARD, _ia.makeMove(res, dev));
    }

    @Test
    public void canMoveRobber()
    {
        Board board = _ia._gameState.getBoard();
        Set<Vertex> vertices = _ia._gameState.getBoard().getAllVertices();
        Vertex v1 = new Vertex(new Coordinate(2,4), VertexOrientation.NORTH);
        Vertex v2 = new Vertex(new Coordinate(3, 1), VertexOrientation.SOUTH);
        when(_ia2.placeSettlement(anySet())).thenReturn(v1);
        when(_ia3.placeSettlement(anySet())).thenReturn(v2);
        vertices.forEach(v -> board.getNeighborsEdges(v).forEach(e -> {
            _p2.addRoad(e);
            _p3.addRoad(e);
        }));
        _p2.placeSettlement();
        _p3.placeSettlement();
        Set<Tile> tiles = _ia._gameState.getBoard().getTiles();
        Tile chosenTile = tiles.stream().filter(t -> t.getPosition().equals(new Coordinate(2, 4))).findFirst().orElse(null);
        assertEquals(chosenTile, _ia.moveRobber(tiles));
    }

    @Test
    public void canSplitCards()
    {
        List<Resource> res = new ArrayList<Resource>();
        res.add(Resource.Grain);
        res.add(Resource.Ore);
        _p1.placeFirstSettlement();
        _ia.makeMove(res, new ArrayList<DevelopmentCard>());
        //We nearly have enough to buy a development card
        res.add(Resource.Grain);
        res.add(Resource.Ore);
        List<Resource> expected = new ArrayList<Resource>();
        res.add(Resource.Ore);
        res.add(Resource.Wool);
        res.add(Resource.Ore);
        expected.add(Resource.Grain);
        expected.add(Resource.Ore);
        expected.add(Resource.Ore);
        expected.add(Resource.Wool);
        assertTrue(expected.containsAll(_ia.splitCards(res, 3)));
    }
    @Test
    public void testChoseTarget()
    {
        Board board = _ia._gameState.getBoard();
        Set<Vertex> vertices = _ia._gameState.getBoard().getAllVertices();
        Vertex v1 = new Vertex(new Coordinate(2,4), VertexOrientation.NORTH);
        Vertex v2 = new Vertex(new Coordinate(3, 1), VertexOrientation.SOUTH);
        when(_ia2.placeSettlement(anySet())).thenReturn(v1);
        when(_ia3.placeSettlement(anySet())).thenReturn(v2);
        vertices.forEach(v -> board.getNeighborsEdges(v).forEach(e -> {
            _p2.addRoad(e);
            _p3.addRoad(e);
        }));
        _p2.placeSettlement();
        _p3.placeSettlement();
        _p2.upgradeToCity();

        List<Player> players = new LinkedList<>();
        players.add(_p2);
        players.add(_p3);

        assertEquals(_p2, _ia.selectTarget(players));
    }

    @Test
    public void testChoseRoadSimple()
    {
        Set<Edge> edges = new HashSet<Edge>();
        Edge e1 = new Edge(new Coordinate(2,3), EdgeOrientation.NORTH_EAST);
        Edge e2 = new Edge(new Coordinate(4,2), EdgeOrientation.NORTH_WEST);
        Edge e3 = new Edge(new Coordinate(0,1), EdgeOrientation.EAST);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        assertEquals(e3,_ia.placeRoad(edges));
    }

    @Test
    public void testChoseRoadWithDefinedRoad()
    {
        Set<Edge> edges = new HashSet<Edge>();
        Edge e1 = new Edge(Coordinate.p13, EdgeOrientation.NORTH_EAST);
        Edge e2 = new Edge(Coordinate.p12, EdgeOrientation.EAST);
        Edge e3 = new Edge(Coordinate.p22, EdgeOrientation.NORTH_EAST);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        Edge e4 = new Edge(Coordinate.p13, EdgeOrientation.EAST);
        when(_ia2.placeRoad(anySet())).thenReturn(e4);
        _p2.placeRoad();
        assertEquals(e2, _ia.placeRoad(edges));
        edges.remove(e2);
        Edge expected = new Edge(Coordinate.p23, EdgeOrientation.NORTH_EAST);
        edges.add(expected);
        assertEquals(expected, _ia.placeRoad(edges));
    }

    @Test
    public void testChosenRoadBeingCut()
    {
        Set<Edge> edges = new HashSet<Edge>();
        Edge e1 = new Edge(Coordinate.p11, EdgeOrientation.EAST);
        Edge e2 = new Edge(Coordinate.p22, EdgeOrientation.NORTH_WEST);
        Edge e3 = new Edge(Coordinate.p32, EdgeOrientation.NORTH_WEST);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        Edge e4 = new Edge(Coordinate.p21, EdgeOrientation.NORTH_WEST);
        when(_ia2.placeRoad(anySet())).thenReturn(e4);
        _p2.placeRoad();

        assertEquals(e3, _ia.placeRoad(edges));
        edges.remove(e3);
        Edge expected = new Edge(Coordinate.p32, EdgeOrientation.NORTH_EAST);
        Edge toBeRemoved = new Edge(Coordinate.p21, EdgeOrientation.EAST);
        edges.add(expected);
        edges.add(toBeRemoved);

        when(_ia3.placeRoad(anySet())).thenReturn(toBeRemoved);
        _p3.placeRoad();
        Edge chosenEdge = _ia.placeRoad(edges);
        assertTrue(chosenEdge.equals(e1) || chosenEdge.equals(expected));
        //I was really not expected two edges to be as close as they are to the _wantedVertex lmao
    }

    @Test
    public void testCanSendTrade()
    {
        Set<Edge> edges = new HashSet<Edge>();
        edges.add(new Edge(new Coordinate(2, 2), EdgeOrientation.NORTH_WEST));
        _ia.placeRoad(edges);

        List<Resource> resources = new LinkedList<>();
        resources.add(Resource.Wool);
        resources.add(Resource.Grain);
        resources.add(Resource.Brick);
        resources.add(Resource.Brick);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(resources, new LinkedList<DevelopmentCard>()));

    }

    @Test
    public void testCantSendTwoPlayerTrades()
    {
        _p1.placeFirstSettlement();
        Set<Edge> edges = new HashSet<Edge>();
        edges.add(new Edge(new Coordinate(2, 2), EdgeOrientation.NORTH_WEST));
        _p1.placeRoad();

        List<Resource> resources = new LinkedList<>();
        resources.add(Resource.Wool);
        resources.add(Resource.Grain);
        resources.add(Resource.Brick);
        resources.add(Resource.Brick);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(resources, new LinkedList<DevelopmentCard>()));
        assertEquals(ActionType.END_TURN, _ia.makeMove(resources, new LinkedList<DevelopmentCard>()));
    }

    @Test
    public void canTradeWithBankTest()
    {
        _p1.placeFirstSettlement();
        List<Resource> res = new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(ActionType.TRADE_TO_BANK, _ia.makeMove(res, new LinkedList<>()));
    }

    @Test
    public void testDevelopmentCardKnight()
    {
        Set<Vertex> vertices = new HashSet<Vertex>();
        Vertex v1 = new Vertex(new Coordinate(2,4), VertexOrientation.NORTH);
        vertices.add(v1);
        _ia.placeSettlement(vertices);
        Tile chosenTile = _ia._gameState.getBoard().getTiles().stream().filter(t -> t.getPosition().equals(new Coordinate(2,4))).findFirst().get();
        chosenTile.setActive(false);

        List<DevelopmentCard> devCards = new LinkedList<>();
        devCards.add(Monopoly);
        devCards.add(Road);
        devCards.add(VictoryPoint);
        devCards.add(Knight);

        assertEquals(Knight, _ia.playDevelopmentCard(devCards, new LinkedList<Resource>()));
    }

    @Test
    public void testDevelopmentCardRoad()
    {
        Set<Edge> edges = new HashSet<Edge>();
        Edge e1 = new Edge(Coordinate.p13, EdgeOrientation.NORTH_EAST);
        Edge e2 = new Edge(Coordinate.p12, EdgeOrientation.EAST);
        Edge e3 = new Edge(Coordinate.p22, EdgeOrientation.NORTH_EAST);
        edges.add(e1);
        edges.add(e2);
        edges.add(e3);

        Edge e4 = new Edge(Coordinate.p13, EdgeOrientation.EAST);
        when(_ia2.placeRoad(anySet())).thenReturn(e4);
        _p2.placeRoad();
        _ia.placeRoad(edges);

        List<DevelopmentCard> devCards = new LinkedList<DevelopmentCard>();
        devCards.add(Knight);
        devCards.add(VictoryPoint);
        devCards.add(Road);
        assertEquals(Road, _ia.playDevelopmentCard(devCards, new LinkedList<Resource>()));
    }

    @Test
    public void testMonoPolyAndYearsOfPlentyCard()
    {
        _p1.placeFirstSettlement();

        List<Resource> resources = new LinkedList<>();
        resources.add(Resource.Wool);
        resources.add(Resource.Wool);
        resources.add(Resource.Ore);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(resources, new LinkedList<DevelopmentCard>()));
        List<DevelopmentCard> devCards = new LinkedList<DevelopmentCard>();
        devCards.add(Knight);
        devCards.add(VictoryPoint);
        devCards.add(Road);
        devCards.add(YearsOfPlenty);
        assertEquals(YearsOfPlenty, _ia.playDevelopmentCard(devCards, resources));
    }

    @Test
    public void testPlayYearsOfPlenty()
    {
        _p1.placeFirstSettlement();

        List<Resource> resources = new LinkedList<>();
        resources.add(Resource.Grain);
        resources.add(Resource.Ore);
        resources.add(Resource.Ore);
        resources.add(Resource.Lumber);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(resources, new LinkedList<DevelopmentCard>()));
        assertEquals(new AbstractMap.SimpleEntry<>(Resource.Ore, Resource.Grain), _ia.playYearsOfPlentyCard(resources));
    }

    @Test
    public void testPlayMonopoly()
    {
        _p1.placeFirstSettlement();

        List<Resource> resources = new LinkedList<>();
        resources.add(Resource.Grain);
        resources.add(Resource.Grain);
        resources.add(Resource.Ore);
        resources.add(Resource.Lumber);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(resources, new LinkedList<DevelopmentCard>()));
        assertEquals(Resource.Ore, _ia.playMonopolyCard(resources));
    }

    @Test
    public void exchangeBetweenPlayer()
    {
        _p1.placeFirstSettlement();
        List<Resource> res = new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Ore);
        res.add(Resource.Ore);
        res.add(Resource.Ore);
        res.add(Resource.Grain);

        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(new TradeOffer(Resource.Brick, Resource.Grain), _ia.createTradeOffer(res));
    }

    @Test
    public void declineExchangeBetweenPlayer()
    {
        _p1.placeFirstSettlement();
        List<Resource> res = new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Ore);
        res.add(Resource.Ore);
        res.add(Resource.Ore);
        res.add(Resource.Grain);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(ActionType.END_TURN, _ia.makeMove(res, new LinkedList<>()));
        assertFalse(_ia.receiveTradeOffer(new TradeOffer(Resource.Lumber, Resource.Brick), res));
    }


    @Test
    public void acceptExchangeBetweenPlayer()
    {
        _p1.placeFirstSettlement();
        List<Resource> res= new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Wool);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(ActionType.END_TURN, _ia.makeMove(res, new LinkedList<>()));
        assertTrue(_ia.receiveTradeOffer(new TradeOffer(Resource.Lumber, Resource.Wool), res));
    }

    @Test
    public void tradeToBankVanilla()
    {
        _p1.placeFirstSettlement();
        List<Resource> res = new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(new AbstractMap.SimpleEntry<Resource, Resource>(Resource.Wool, Resource.Lumber),
                _ia.tradeToBank(res));
    }

    @Test
    public void tradeToBankDesertPort()
    {
        _p1.placeFirstSettlement();
        _p1.addVertex(Port.P0.getVertex());
        List<Resource> res = new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(new AbstractMap.SimpleEntry<Resource, Resource>(Resource.Wool, Resource.Lumber),
                _ia.tradeToBank(res));
    }

    @Test
    public void tradeToBankResourcePort()
    {
        _p1.placeFirstSettlement();
        _p1.addVertex(Port.P2.getVertex());
        List<Resource> res = new LinkedList<>();
        res.add(Resource.Brick);
        res.add(Resource.Wool);
        res.add(Resource.Wool);
        assertEquals(ActionType.TRADE_TO_PLAYER, _ia.makeMove(res, new LinkedList<>()));
        assertEquals(new AbstractMap.SimpleEntry<Resource, Resource>(Resource.Wool, Resource.Lumber),
                _ia.tradeToBank(res));
    }




}
