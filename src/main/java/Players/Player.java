package Players;

import Board.Edge;
import Board.Port;
import Board.Resources.Building;
import Board.Resources.DevelopmentCard;
import Board.Resources.Resource;
import Board.Tile;
import Board.Vertex;
import Game.GameAction.ActionType;
import Game.GameAction.TradeOffer;
import Game.GameState;
import GameObjects.Cards;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class Player implements Comparable<Player>
{
    private static final int NUM_MAX_SETTLEMENT = 5;
    private static final int NUM_MAX_CITY = 4;
    private static final int NUM_MAX_ROAD = 15;

    private Agent _agent;
    private GameState _gameState;
    private Cards _cards;
    private int _id;

    private Map<Integer, Map<Tile, Integer>> _rollToResources;

    private Set<Edge> _roads;
    private Set<Vertex> _vertices;
    private List<Building> _buildings;

    private List<DevelopmentCard> _developmentCards;
    private int _knightsNumber;

    public Player(Agent agent, int id)
    {
        _agent = agent;
        _cards = new Cards();
        _roads = new HashSet<>();
        _vertices = new HashSet<>();
        _rollToResources = new HashMap<>();
        initRolls();
        _id = id;

        _developmentCards = new ArrayList<>();
        _knightsNumber = 0;
    }

    public void setGameState(GameState gameState)
    {
        _gameState = gameState;
        _agent.setGameState(_gameState);
    }

    public ActionType makeMove()
    {
        return _agent.makeMove(_cards.asList(), _developmentCards);
    }

    public void addCards(Resource... newCards)
    {
        for (Resource card : newCards)
        {
            if(card == null)
                return;
            addCards(card, 1);
        }
    }

    public void addCards(Resource resource, int quantity)
    {
        _cards.add(resource, quantity);
    }

    public void computeDiceRoll(int diceRoll)
    {
        Map<Tile, Integer> tileToNumber = _rollToResources.get(diceRoll);
        tileToNumber.forEach((tile, integer) ->
        {
            if (tile.isActive())
            {
                for (int i = 0; i < integer; i++)
                    addCards(tile.getResource());

            }
        });
    }

    public void splitCards()
    {
        _agent.splitCards(_cards.asList(), _cards.count() / 2);
    }

    public int numberOfCards()
    {
        return _cards.count();
    }

    public int numberOfCards(Resource resource)
    {
        return _cards.getNumber(resource);
    }

    public Optional<Resource> stealCard()
    {
        if (numberOfCards() == 0)
            return Optional.empty();

        List<Resource> resources = _cards.asList();
        Collections.shuffle(resources);
        Resource stolenCard = resources.get(0);
        _cards.remove(stolenCard);
        return Optional.of(stolenCard);
    }

    public void addToResourcesMap(int diceValue, Tile tile)
    {
        if (diceValue != 0)
        {
            Map<Tile, Integer> tileToNumber = _rollToResources.get(diceValue);
            if (tileToNumber.isEmpty())
            {
                tileToNumber.put(tile, 1);
            }
            else
            {
//            tileToNumber.put(tile, tileToNumber.get(tile) + 1);
                tileToNumber.computeIfPresent(tile, (t, i) -> i + 1); //Is this better?
            }
        }
    }

    public Set<Tile> getTouchedTiles()
    {
        Set<Tile> touchedTiles = new HashSet<>();
        _rollToResources.values().stream().map(Map::keySet).forEach(touchedTiles::addAll);

        return touchedTiles;
    }

    public void addRoad(Edge road)
    {
        if (road != null)
            _roads.add(road);
    }

    public void addVertex(Vertex vertex)
    {
        _vertices.add(vertex);
    }

    public Set<Edge> getRoads()
    {
        return _roads;
    }

    private void initRolls()
    {
        for (int i = 2; i <= 12; i++)
            _rollToResources.put(i, new HashMap<>());
    }

    public Set<Vertex> getVertices()
    {
        return _vertices;
    }

    public void pay(Resource... cards)
    {
        Cards temp = new Cards(_cards);
        boolean contains = true;
        for (Resource resource : cards)
            temp.remove(resource);
        if (contains)
        {
            _cards = temp;
        }
        else
        {
            System.out.println("AAAAAAAAAAAAAA");
        }
    }

    public void pay(Resource resource, int quantity)
    {
        _cards.remove(resource, quantity);
    }

    public void addDevelopmentCard(DevelopmentCard developmentCard)
    {
        if (developmentCard != null)
            _developmentCards.add(developmentCard);
    }

    public void addKnight()
    {
        _knightsNumber++;
    }

    public int getKnightsNumber()
    {
        return _knightsNumber;
    }

    public Tile moveRobber()
    {
        // TODO: filtrer la tile qui contient deja le robber
        return _agent.moveRobber(_gameState.getBoard().getTiles());
    }

    public Player selectTarget(List<Player> potentialTarget)
    {
        return _agent.selectTarget(potentialTarget);
    }

    public DevelopmentCard playDevelopmentCard()
    {
        DevelopmentCard chosenCard = _agent.playDevelopmentCard(Collections.unmodifiableList(_developmentCards),
                Collections.unmodifiableList(_cards.asList()));
        if (chosenCard == null)
        {
            System.out.println("NULL CARD CHOSEN");
        }
        _developmentCards.remove(chosenCard);
        return chosenCard;
    }

    public Resource playMonopolyCard()
    {
        return _agent.playMonopolyCard(_cards.asList());
    }

    public Map.Entry<Resource, Resource> playYearsOfPlentyCard()
    {
        return _agent.playYearsOfPlentyCard(_cards.asList());
    }

    public void placeRoad()
    {
        Set<Edge> freeEdgesAvailable = new HashSet<>();

        for (Edge currentEdge : _roads)
        {
            Set<Edge> neighbors = Edge.getNeighbors(currentEdge);
            neighbors.stream().filter(edge -> _gameState.ownerOf(edge) == null).forEach(freeEdgesAvailable::add);
        }
        for (Vertex currentVertex : _vertices)
        {
            Set<Edge> neighbors = _gameState.getBoard().getNeighborsEdges(currentVertex);
            neighbors.stream().filter(edge -> _gameState.ownerOf(edge) == null).forEach(freeEdgesAvailable::add);
        }

        addRoad(_agent.placeRoad(freeEdgesAvailable));
    }

    public void placeSettlement()
    {
        Set<Vertex> freeVertexAvailable = new HashSet<>();

        for (Edge edge : _roads)
        {
            _gameState.getBoard().getNeighborsVertices(edge).stream().filter(vertex -> _gameState.ownerOf(vertex) == null).forEach(freeVertexAvailable::add);
        }

        Vertex vertex = _agent.placeSettlement(freeVertexAvailable);
        if (vertex != null)
        {
            addVertex(vertex);
            _gameState.getBoard().getNeighborsTiles(vertex).forEach(tile -> addToResourcesMap(tile.getNbr(), tile));
        }
    }

    public void upgradeToCity()
    {
        _agent.upgradeCity(Collections.unmodifiableSet(_vertices));
    }

    public Vertex placeFirstSettlement()
    {
        Set<Vertex> allVertices = Sets.newHashSet(Vertex.ALL_VERTICES);
        Set<Vertex> freeVertice = allVertices.stream().filter(vertex -> _gameState.ownerOf(vertex) == null).collect(Collectors.toSet());
        Vertex choosenVertex = _agent.placeSettlement(freeVertice);
        _gameState.getBoard().getNeighborsTiles(choosenVertex).forEach(tile -> addToResourcesMap(tile.getNbr(), tile));
        addVertex(choosenVertex);

        this.placeRoad();
        return choosenVertex;

    }

    public int getId()
    {
        return _id;
    }

    public void tradeToBank()
    {
        Map.Entry<Resource, Resource> exchange = _agent.tradeToBank(_cards.asList());
        Resource toPay = exchange.getKey();
        List<Port> availablePorts = _gameState.getPortsOf(this);

        int quantityToPay = 4;

        if (availablePorts.stream().anyMatch(port -> port.getResource() == Resource.Desert))
            quantityToPay = 3;

        if (availablePorts.stream().anyMatch(port -> port.getResource() == toPay))
            quantityToPay = 2;


        pay(toPay, quantityToPay);
        addCards(exchange.getValue());
    }

    @Override
    public int compareTo(Player p)
    {
        return Integer.compare(_gameState.pointsOf(this), _gameState.pointsOf(p));
    }

    public TradeOffer createTradeOffer()
    {
        return _agent.createTradeOffer(_cards.asList());
    }

    // The tradeOffer was accepted by the other player
    public void tradeAccepted(TradeOffer tradeOffer)
    {
        addCards(tradeOffer.demandingResource());
        pay(tradeOffer.givingResource());
    }

    // You accept the tradeOffer from the other player
    public void acceptTrade(TradeOffer tradeOffer)
    {
        addCards(tradeOffer.givingResource());
        pay(tradeOffer.demandingResource());
    }

    public boolean receiveTradeOffer(TradeOffer tradeOffer)
    {
        return _agent.receiveTradeOffer(tradeOffer, _cards.asList());
    }
}
