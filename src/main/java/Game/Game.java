package Game;

import Board.Board;
import Board.Resources.Building;
import Board.Resources.DevelopmentCard;
import Board.Resources.Resource;
import Board.Resources.Road;
import Board.Tile;
import Board.Vertex;
import Game.GameAction.ActionType;
import Game.GameAction.GameAction;
import Game.GameAction.TradeOffer;
import GameObjects.Cards;
import GameObjects.developmentCards.DevelopmentCardDeck;
import Players.Player;
import util.Counter;

import java.util.*;

public class Game
{
    private static final int MIN_AMT_PLAYERS = 3;
    private static final int MAX_AMT_PLAYERS = 4;
    private static final int MAX_POINTS = 10;

    private Board _board;
    private ArrayList<ArrayList<Cards>> cardsOfPlayers;

    private List<Player> _players;
    private Player _currentPlayer;

    private Tile _robbedTile;
    private DevelopmentCardDeck _developmentCardDeck;

    private Map<Player, Counter> _points;
    private Map<Player, Counter> _largestArmy;
    private Player _largestArmyHolder;
    private Dice _dice;

    public Game(Board board, List<Player> players)
    {
        _players = players;
        _board = board;
        _dice = new Dice();
        _robbedTile = _board.getDesert();
        _developmentCardDeck = new DevelopmentCardDeck();

        _points = new HashMap<>();
        _largestArmy = new HashMap<>();
        GameState gameState = new GameState(_players, _board, _points);
        _players.forEach(player -> player.setGameState(gameState));

        for (Player player : _players)
        {
            _points.put(player, new Counter());
            _largestArmy.put(player, new Counter());
        }
    }

    public float start()
    {
        setGame();
        _currentPlayer = _players.get(0);
        int amtOfTurns = 0;
        while (!isGameFinished() && amtOfTurns < 500)
        {
            amtOfTurns++;
            int roll = _dice.roll();
            if (roll == 7)
            {
                _players.stream().filter(player -> player.numberOfCards() > 7).forEach(Player::splitCards);
                moveRobber();
            }
            else
            {
                _players.forEach(player -> player.computeDiceRoll(roll));
            }

            ActionType action = _currentPlayer.makeMove();
            int count = 0;
            while (action != ActionType.END_TURN && count < 15)
            {
                switch (action)
                {
                    case BUY_DEVELOPMENT_CARD:
                        _currentPlayer.pay(DevelopmentCard.getCost());
                        giveDevelopmentCard();
                        break;
                    case PLAY_DEVELOPMENT_CARD:
                        DevelopmentCard playedCard = _currentPlayer.playDevelopmentCard();
                        playDevelopmentCard(playedCard);
                        break;
                    case PLACE_SETTLEMENT:
                        _currentPlayer.pay(Building.getSettlementCost());
                        _currentPlayer.placeSettlement();
                        _points.get(_currentPlayer).increment();
                        break;
                    case PLACE_ROAD:
                        _currentPlayer.pay(Road.getCost());
                        placeRoad();
                        break;

                    case UPGRADE_TO_CITY:
                        _currentPlayer.pay(Building.getCityCost());
                        _currentPlayer.upgradeToCity();
                        _points.get(_currentPlayer).increment();
                        break;
                    case TRADE_TO_BANK:
                        _currentPlayer.tradeToBank();
                        break;
                    case TRADE_TO_PLAYER:
                        tradeLoop();
                        break;
                }

                action = _currentPlayer.makeMove();
            }

            _currentPlayer = nextPlayer();
        }
        float sumOfPts = 0;
        for(Player p : _players)
        {
            int pts =  _points.get(p).get();
            sumOfPts += _points.get(p).get() < 10 ? pts : 0;
        }
        float ratio = 10 / sumOfPts;
        return ratio * 3 * amtOfTurns;
    }

    private void tradeLoop()
    {
        TradeOffer tradeOffer = _currentPlayer.createTradeOffer();
        Player otherPlayer = nextPlayer();
        boolean tradeAccepted = false;
        while (_currentPlayer != otherPlayer && !tradeAccepted)
        {
            if (otherPlayer.receiveTradeOffer(tradeOffer))
            {
                tradeAccepted = true;
                _currentPlayer.tradeAccepted(tradeOffer);
                otherPlayer.acceptTrade(tradeOffer);
            }

            otherPlayer = nextPlayer(otherPlayer);
        }
    }

    private void setGame()
    {
        for (int i = 0; i < _players.size(); i++)
        {
            Player player = _players.get(i);
            player.placeFirstSettlement();
            _points.get(player).increment();
        }
        for (int i = _players.size() - 1; i >= 0; i--)
        {
            Player player = _players.get(i);
            Vertex choosenVertex = player.placeFirstSettlement();
            _points.get(player).increment();
            Set<Tile> adjacentTiles = _board.getNeighborsTiles(choosenVertex);
            adjacentTiles.forEach(tile -> player.addCards(tile.getResource()));
        }
    }

    private void giveDevelopmentCard()
    {
        DevelopmentCard card = _developmentCardDeck.pickCard();
        _currentPlayer.addDevelopmentCard(card);
    }

    private void playDevelopmentCard(DevelopmentCard developmentCard)
    {
        if(developmentCard == null)
            return;
        switch (developmentCard)
        {
            case Knight:
                moveRobber();
                _currentPlayer.addKnight();
                _largestArmy.get(_currentPlayer).increment();
                checkForBiggestArmy();
                break;
            case Monopoly:
                Resource resource = _currentPlayer.playMonopolyCard();
                if(resource != null)
                {
                    int total = 0;
                    for (Player player : _players)
                    {
                        int quantity = player.numberOfCards(resource);
                        if (quantity > 0)
                            player.pay(resource, quantity);
                        total += quantity;
                    }
                    _currentPlayer.addCards(resource, total);
                }
                break;
            case Road:
                placeRoad();
                placeRoad();
                break;
            case VictoryPoint:
                _points.get(_currentPlayer).increment();
                break;
            case YearsOfPlenty:
                Map.Entry<Resource, Resource> pair = _currentPlayer.playYearsOfPlentyCard();
                if(pair != null && pair.getKey() != null && pair.getValue() != null)
                    _currentPlayer.addCards(pair.getKey(), pair.getValue());
                break;
        }
    }

    private void checkForBiggestArmy()
    {
        if (_largestArmy.get(_currentPlayer).get() >= 5 && _largestArmyHolder != _currentPlayer)
        {
            if (_largestArmyHolder == null)
            {

                _largestArmyHolder = _currentPlayer;
                _points.get(_largestArmyHolder).increment();
                _points.get(_largestArmyHolder).increment();
            }
            else
            {
                if (_largestArmy.get(_currentPlayer).get() > _largestArmy.get(_largestArmyHolder).get())
                {
                    _points.get(_largestArmyHolder).decrement();
                    _points.get(_largestArmyHolder).decrement();

                    _points.get(_currentPlayer).increment();
                    _points.get(_currentPlayer).increment();

                    _largestArmyHolder = _currentPlayer;
                }
            }
        }
    }

    private void placeRoad()
    {
        _currentPlayer.placeRoad();
        checkForLongestRoad();
    }

    private void checkForLongestRoad()
    {
        // TODO: implement this
    }

    private void moveRobber()
    {
        _robbedTile.setActive(true);
        _robbedTile = _currentPlayer.moveRobber();
        _robbedTile.setActive(false);

        List<Player> potentialTarget = new ArrayList<>();
        for (Player player : _players)
        {
            if (player.getTouchedTiles().contains(_robbedTile))
                potentialTarget.add(player);
        }
        if (!potentialTarget.isEmpty())
        {
            Player targetedPlayer = _currentPlayer.selectTarget(potentialTarget);
            targetedPlayer.stealCard().ifPresent(card -> _currentPlayer.addCards(card));
        }
    }

    private Player nextPlayer()
    {
        return nextPlayer(_currentPlayer);
    }

    private Player nextPlayer(Player player)
    {
        int currentIndex = _players.indexOf(player);
        return _players.get((currentIndex + 1) % _players.size());
    }

    private void executeAction(Player player, GameAction gameAction)
    {

    }

    private boolean isGameFinished()
    {
        return _points.values().stream().anyMatch(value -> value.get() >= MAX_POINTS);
    }
}
