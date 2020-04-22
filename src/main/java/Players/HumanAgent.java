package Players;

import Board.Edge;
import Board.Resources.DevelopmentCard;
import Board.Resources.Resource;
import Board.Tile;
import Board.Vertex;
import Game.GameAction.ActionType;
import Game.GameAction.TradeOffer;
import Game.GameState;
import com.google.common.collect.Lists;

import java.util.*;

import static Board.Resources.Resource.*;

public class HumanAgent extends Agent
{
    private Scanner _scanner;
    public HumanAgent()
    {
        _scanner = new Scanner(System.in);
    }


    @Override
    public ActionType makeMove(List<Resource> cards, List<DevelopmentCard> developmentCards)
    {
        showCards(cards);
        System.out.println("Make a move");
        System.out.println("A: End turn");
        System.out.println("B: Buy development card");
        System.out.println("C: Play development card");
        System.out.println("D: Place settlement");
        System.out.println("E: Place road");
        System.out.println("F: Upgrade settlement to city");
        System.out.println("G: Make a trade with the bank");
        System.out.println("H: Make a trade with another player");

        String answer = _scanner.next();
        switch (answer)
        {
            case "A":
                return ActionType.END_TURN;
            case "B":
                return ActionType.BUY_DEVELOPMENT_CARD;
            case "C":
                return ActionType.PLAY_DEVELOPMENT_CARD;
            case "D":
                return ActionType.PLACE_SETTLEMENT;
            case "E":
                return ActionType.PLACE_ROAD;
            case "F":
                return ActionType.UPGRADE_TO_CITY;
            case "G":
                return ActionType.TRADE_TO_BANK;
            case "H":
                return ActionType.TRADE_TO_PLAYER;
            default:
                return null;
        }
    }

    @Override
    public List<Resource> splitCards(List<Resource> cards, int amountToDiscard)
    {
        List<Resource> resources = new ArrayList<>(cards);
        List<Resource> resourcesToGiveUp = new ArrayList<>();
        System.out.println("You have too many cards! You must give up " + amountToDiscard + " cards.");
        for (int i = 0; i < amountToDiscard; i++)
        {
            showCards(resources);

            System.out.println("Select resource to give up");
            String answer = _scanner.next();
            switch (answer)
            {
                case "B":
                    resourcesToGiveUp.add(Brick);
                    resources.remove(Brick);
                    break;
                case "L":
                    resourcesToGiveUp.add(Lumber);
                    resources.remove(Lumber);
                    break;
                case "W":
                    resourcesToGiveUp.add(Wool);
                    resources.remove(Wool);
                    break;
                case "G":
                    resourcesToGiveUp.add(Grain);
                    resources.remove(Grain);
                    break;
                case "O":
                    resourcesToGiveUp.add(Ore);
                    resources.remove(Ore);
                    break;
            }
        }
        return resourcesToGiveUp;
    }

    private void showCards(List<Resource> resources)
    {
        System.out.println("You have:");
        long brick = countNumberOf(resources, Brick);
        long lumber = countNumberOf(resources, Lumber);
        long wool = countNumberOf(resources, Wool);
        long grain = countNumberOf(resources, Grain);
        long ore = countNumberOf(resources, Ore);

        System.out.println(brick + " (B)ricks");
        System.out.println(lumber + " (L)umber");
        System.out.println(wool + " (W)ool");
        System.out.println(grain + " (G)rain");
        System.out.println(ore + " (O)re");
    }

    private long countNumberOf(List<Resource> cards, Resource resource)
    {
        return cards.stream().filter(res -> res == resource).count();
    }

    @Override
    public Tile moveRobber(Set<Tile> tiles)
    {
        return tiles.stream().findFirst().get();
    }

    @Override
    public Player selectTarget(List<Player> potentialTarget)
    {
        return potentialTarget.get(0);
    }

    @Override
    public DevelopmentCard playDevelopmentCard(List<DevelopmentCard> developmentCards, List<Resource> cards)
    {
        return developmentCards.get(0);
    }

    @Override
    public Edge placeRoad(Set<Edge> freeEdgesAvailable)
    {
        List<Edge> edges = Lists.newArrayList(freeEdgesAvailable);

        System.out.println("Choose an edge");
        for (int i = 0; i < edges.size(); i++)
        {
            System.out.println(i + " : " + edges.get(i).toString());
        }
        int answer = _scanner.nextInt();

        return edges.get(answer);
    }

    @Override
    public Vertex placeSettlement(Set<Vertex> freeVertexAvailable)
    {
        List<Vertex> vertices = Lists.newArrayList(freeVertexAvailable);

        System.out.println("Choose a location");
        for (int i = 0; i < vertices.size(); i++)
        {
            String vertexValue = _gameState.getBoard().vertexValue(vertices.get(i));
            System.out.println(i + " : " + vertexValue);
        }
        int answer = _scanner.nextInt();

        return vertices.get(answer);
    }

    @Override
    public Vertex upgradeCity(Set<Vertex> vertices)
    {
        return vertices.stream().findFirst().get();
    }

    @Override
    public Map.Entry<Resource, Resource> tradeToBank(List<Resource> cards)
    {
        System.out.println("Which resource do you wish to trade?");
        System.out.println("(B)ricks");
        System.out.println("(L)umber");
        System.out.println("(W)ool");
        System.out.println("(G)rain");
        System.out.println("(O)re");

        String answer = _scanner.next();
        Resource resource;
        switch (answer)
        {
            case "B":
                resource = Brick;
                break;
            case "L":
                resource = Lumber;
                break;
            case "W":
                resource = Wool;
                break;
            case "G":
                resource = Grain;
                break;
            case "O":
                resource = Ore;
                break;
            default:
                resource = Desert;
        }

        System.out.println("Which resource do you wish to buy?");
        System.out.println("(B)ricks");
        System.out.println("(L)umber");
        System.out.println("(W)ool");
        System.out.println("(G)rain");
        System.out.println("(O)re");

        answer = _scanner.next();
        Resource toBuy;
        switch (answer)
        {
            case "B":
                toBuy = Brick;
                break;
            case "L":
                toBuy = Lumber;
                break;
            case "W":
                toBuy = Wool;
                break;
            case "G":
                toBuy = Grain;
                break;
            case "O":
                toBuy = Ore;
                break;
            default:
                toBuy = Desert;
        }
        return new AbstractMap.SimpleEntry<>(resource, toBuy);
    }

    @Override
    public TradeOffer createTradeOffer(List<Resource> cards)
    {
        System.out.println("What resource are you looking for?");
        System.out.println("(B)ricks");
        System.out.println("(L)umber");
        System.out.println("(W)ool");
        System.out.println("(G)rain");
        System.out.println("(O)re");
        String answer = _scanner.next();
        Resource demanding;
        switch (answer)
        {
            case "B":
                demanding = Brick;
                break;
            case "L":
                demanding = Lumber;
                break;
            case "W":
                demanding = Wool;
                break;
            case "G":
                demanding = Grain;
                break;
            case "O":
                demanding = Ore;
                break;
            default:
                demanding = Desert;
        }

        System.out.println("What do you offer?");
        System.out.println("(B)ricks");
        System.out.println("(L)umber");
        System.out.println("(W)ool");
        System.out.println("(G)rain");
        System.out.println("(O)re");
        answer = _scanner.next();
        Resource offer;
        switch (answer)
        {
            case "B":
                offer = Brick;
                break;
            case "L":
                offer = Lumber;
                break;
            case "W":
                offer = Wool;
                break;
            case "G":
                offer = Grain;
                break;
            case "O":
                offer = Ore;
                break;
            default:
                offer = Desert;
        }

        return new TradeOffer(offer, demanding);
    }

    @Override
    public boolean receiveTradeOffer(TradeOffer tradeOffer, List<Resource> cards)
    {
        System.out.println("You received a trade offer!");

        System.out.println("Do you want " + tradeOffer.givingResource() + "in exchange of your " + tradeOffer.demandingResource());
        System.out.println("(A)ccept or (R)efuse");

        String answer = _scanner.next();
        if ("A".equals(answer))
        {
            return true;
        } else if ("R".equals(answer))
        {
            return false;
        }

        return false;
    }

    //TODO
    @Override
    public Resource playMonopolyCard(List<Resource> cards)
    {
        return null;
    }

    //TODO
    @Override
    public Map.Entry<Resource, Resource> playYearsOfPlentyCard(List<Resource> cardss)
    {
        return null;
    }
}
