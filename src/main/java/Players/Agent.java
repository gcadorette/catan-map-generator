package Players;

import Board.Edge;
import Board.Resources.DevelopmentCard;
import Board.Resources.Resource;
import Board.Tile;
import Board.Vertex;
import Game.Game;
import Game.GameAction.ActionType;
import Game.GameAction.TradeOffer;
import Game.GameState;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Agent
{
    protected GameState _gameState;
    protected int _id;
    public Agent()
    {
    }

    public abstract ActionType makeMove(List<Resource> resources, List<DevelopmentCard> developmentCards);

    public void setId(int id)
    {
        this._id = id;
    }
    public int getId()
    {
        return this._id;
    }

    public void setGameState(GameState gameState)
    {
        _gameState = gameState;
    }

    public abstract List<Resource> splitCards(List<Resource> cards, int amountToDiscard);

    public abstract Tile moveRobber(Set<Tile> tiles);

    public abstract Player selectTarget(List<Player> potentialTarget);

    public abstract DevelopmentCard playDevelopmentCard(List<DevelopmentCard> developmentCards, List<Resource> cards);

    public abstract Edge placeRoad(Set<Edge> freeEdgesAvailable);

    public abstract Vertex placeSettlement(Set<Vertex> freeVertexAvailable);

    public abstract Vertex upgradeCity(Set<Vertex> vertices);

    public abstract Map.Entry<Resource, Resource> tradeToBank(List<Resource> cards);

    public abstract TradeOffer createTradeOffer(List<Resource> cards);

    public abstract boolean receiveTradeOffer(TradeOffer tradeOffer, List<Resource> cards);

    public abstract Resource playMonopolyCard(List<Resource> cards);

    public abstract Map.Entry<Resource, Resource> playYearsOfPlentyCard(List<Resource> cards);
}
