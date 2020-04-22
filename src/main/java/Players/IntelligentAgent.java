package Players;

import Board.*;
import Board.Resources.Building;
import Board.Resources.DevelopmentCard;
import Board.Resources.Resource;
import Board.Resources.Road;
import Game.GameAction.ActionType;
import Game.GameAction.TradeOffer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntelligentAgent extends Agent
{
    private final Map<ActionType, Resource[]> _requiredResources;
    //TODO: Implement more complex resources
    //For now, we only plan one greedy step at a time.
    private ActionType _strategy;
    //Each OwnedResourceTile represent a single income of a certain resource.
    //So if we have a city, we will have two entries with the same <resource, nbr> pair.
    private List<Tile> _ownedResourcesTiles;
    private List<Edge> _ownedEdges;
    private Vertex _wantedVertex;
    private LinkedList<Edge> _desiredPath;
    private int _amtOfSettlement;
    private int _amtOfPts = 0;
    private int _amtOfRoads = 0;
    private int _amtOfCities = 0;
    private List<Vertex> _cities;
    private List<TradeOffer> _tradesSoFar;
    private TradeOffer _trade;
    private Map.Entry<Resource, Resource> _tradeBank;

    private static final int NUM_MAX_SETTLEMENT = 5;
    private static final int NUM_MAX_CITY = 4;
    private static final int NUM_MAX_ROAD = 15;

    public IntelligentAgent()
    {
        super();
        _ownedResourcesTiles = new ArrayList<Tile>();
        _requiredResources = new HashMap<ActionType, Resource[]>();
        _wantedVertex = null;
        _amtOfSettlement = 0;
        _desiredPath = new LinkedList<Edge>();
        _ownedEdges = new LinkedList<Edge>();
        _amtOfPts = 0;
        _cities = new LinkedList<Vertex>();
        _tradesSoFar = new LinkedList<TradeOffer>();
        _trade = null;
        _tradeBank = null;

        _requiredResources.put(ActionType.PLACE_ROAD, Road.getCost());
        _requiredResources.put(ActionType.PLACE_SETTLEMENT, Building.getSettlementCost());
        _requiredResources.put(ActionType.UPGRADE_TO_CITY, Building.getCityCost());
        _requiredResources.put(ActionType.BUY_DEVELOPMENT_CARD, DevelopmentCard.getCost());
    }

    /*
    The AI tries to choose the best decision according to some weights
    (the values are really empirical, it should probably be adjusted lmao).
    After it chose the best action, it tries to trade if the action can't be completed.
    The action that we can't complete is stored in the _strategy var which will be used by
    other parts of the AI.
     */
    @Override
    public ActionType makeMove(List<Resource> resources, List<DevelopmentCard> developmentCards)
    {
        if (_ownedEdges.size() > 0 && _gameState.ownerOf(_ownedEdges.get(0)) != null)
            _amtOfPts = _gameState.pointsOf(_gameState.ownerOf(_ownedEdges.get(0)));

        Map<Resource, Long> currResources = resources.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        ActionType bestAction = ActionType.PLACE_ROAD;
        float bestWeight = 0;
        ActionType bestStrategy = null;
        int actionDiff = _requiredResources.get(ActionType.PLACE_ROAD).length;
        for (Map.Entry<ActionType, Resource[]> entry : _requiredResources.entrySet())
        {
            int diff = getDifferenceOfCards(currResources, Arrays.asList(entry.getValue())).size();
            float weight = diff < entry.getValue().length ? 6 / (diff + 1) : 1;
            switch (entry.getKey())
            {
                case PLACE_SETTLEMENT:
                    long amtOfValidRoads = _ownedEdges.stream().filter(e ->
                    {
                        return _gameState.getBoard().getNeighborsVertices(e).stream().filter(v -> _gameState.ownerOf(v) != null).count() == 0;
                    }).count();
                    weight += amtOfValidRoads > 0 ? 5 : -20; //-20 because we just can't do a settlement :/
                    weight += _amtOfPts; //The more points we have, the more points we want to score hihi
                    weight += NUM_MAX_SETTLEMENT == _amtOfSettlement ? -20 : 0;
                    break;
                case UPGRADE_TO_CITY:
                    weight += _amtOfSettlement > 0 ? 5 : -20; //Same here :///
                    weight += _amtOfPts; //Same here
                    weight += NUM_MAX_CITY == _amtOfCities ? -20 : 0;
                    break;
                case PLACE_ROAD:
                    weight += NUM_MAX_ROAD == _amtOfRoads ? -20 : 0;
                    break;
                case BUY_DEVELOPMENT_CARD:
                    weight -= developmentCards.size() * 3.5f;
                    break;
                case PLAY_DEVELOPMENT_CARD:
                    weight += developmentCards.size() * 3;
                    break;
                //The two other possibilities are kind of default and will depend on the amtOfCards we need.
            }
            if (weight > bestWeight)
            {
                bestAction = entry.getKey();
                bestWeight = weight;
                actionDiff = diff;
            }
        }
        ActionType chosenAction = bestAction;
        _strategy = chosenAction;
        //Once we chose then best thing we could *buy*, let's see what's the best action we could do.
        if (actionDiff >= 1)
        {
            chosenAction = ActionType.END_TURN; //If we can't trade, we can't make make the chosen action

            if (actionDiff < _requiredResources.get(_strategy).length)
            {
                Map<Resource, Long> needed = Arrays.asList(_requiredResources.get(bestAction)).stream()
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                Long amtThatCouldBeTraded = currResources.entrySet().stream()
                        .filter(x -> !needed.containsKey(x.getKey()) || needed.get(x.getKey()) < x.getValue())
                        .collect(Collectors.counting());
                if (amtThatCouldBeTraded > 0)
                {
                    TradeOffer trade = chooseBestTrade(resources);
                    if (!_tradesSoFar.contains(trade))
                    {
                        _trade = trade;
                        _tradesSoFar.add(_trade);
                        chosenAction = ActionType.TRADE_TO_PLAYER;
                    }
                }
                if (chosenAction == ActionType.END_TURN)
                {
                    _tradeBank = this.choseTradeToBankOrPort(resources);
                    if (_tradeBank != null)
                        chosenAction = ActionType.TRADE_TO_BANK;
                }
            }
            else
            {
                if (!developmentCards.isEmpty())
                {
                    chosenAction = ActionType.PLAY_DEVELOPMENT_CARD;
                }
            }
        }
        if (chosenAction == ActionType.END_TURN)
            _tradesSoFar.clear();
        return chosenAction;
    }

    private List<Resource> getDifferenceOfCards(Map<Resource, Long> cards, List<Resource> resourcesNeeded)
    {
        Map<Resource, Long> neededResources = resourcesNeeded.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Resource> lackingResource = new ArrayList<>();
        for (Map.Entry<Resource, Long> currRessource : neededResources.entrySet())
        {
            int currDiff = 0;
            if (cards.containsKey(currRessource.getKey()))
            {
                currDiff = (int) (currRessource.getValue() - cards.get(currRessource.getKey()));
            }
            else
            {
                currDiff = currRessource.getValue().intValue();
            }
            for (int i = 0; i < currDiff; i++)
            {
                lackingResource.add(currRessource.getKey());
            }
        }
        return lackingResource;
    }

    @Override
    public List<Resource> splitCards(List<Resource> cards, int amountToDiscard)
    {
        //We make sure to remove cards that are not part of the strategy we
        //have in 'mind' for now
        List<Resource> cardsToRemove = new ArrayList<>();
        if (_strategy != null)
        {
            List<Resource> resourceOfStrat = new LinkedList<Resource>(Arrays.asList(_requiredResources.get(_strategy)));
            for (Resource card : cards)
            {
                if (!resourceOfStrat.contains(card))
                {
                    cardsToRemove.add(card);
                    amountToDiscard--;
                    if (amountToDiscard == 0)
                    {
                        return cardsToRemove;
                    }
                }
                else
                {
                    resourceOfStrat.remove(card);
                }
            }
            for (Resource card : cardsToRemove)
            {
                cards.remove(card);
            }
        }
        //But if we still have cards to remove after that, we remove random cards.
        Random rand = new Random();
        for (int i = 0; i < amountToDiscard; i++)
        {
            int index = rand.nextInt(cards.size());
            cardsToRemove.add(cards.get(index));
            cards.remove(index);

        }
        return cardsToRemove;
    }

    @Override
    public Tile moveRobber(Set<Tile> tiles)
    {
        float biggestWeight = 0;
        Tile bestTile = tiles.isEmpty() ? null : (Tile) tiles.toArray()[0];
        for (Tile tile : tiles)
        {
            float currNbr = 1 / (float) Math.abs(7.0 - (float) tile.getNbr());
            float toMax = 0.f;
            for (Vertex v : _gameState.getBoard().getNeighborsVertices(tile))
            {
                Player ownerOfVertex = _gameState.ownerOf(v);
                float currPts = 0.f;
                if (ownerOfVertex != null)
                {
                    currPts = _gameState.ownerOf(v).getId() == this.getId() ? -1 * currNbr : _gameState.pointsOf(_gameState.ownerOf(v));
                    toMax += currPts * 3 + currNbr * 2;
                }
            }
            if (toMax > biggestWeight)
            {
                biggestWeight = toMax;
                bestTile = tile;
            }
        }
        return bestTile;
    }

    @Override
    public Player selectTarget(List<Player> potentialTarget)
    {
        return potentialTarget.stream().max(Player::compareTo).get();
    }

    @Override
    public DevelopmentCard playDevelopmentCard(List<DevelopmentCard> developmentCards, List<Resource> cards)
    {

        if (developmentCards.isEmpty())
        {
            return null;
        }
        if (developmentCards.size() == 1)
        {
            return developmentCards.get(0);
        }
        float maxWeight = 0;
        DevelopmentCard bestAction = developmentCards.get(0);
        for (DevelopmentCard card : developmentCards)
        {
            float weight = 0;
            if (card.equals(DevelopmentCard.Knight))
            {
                Tile blockedTile = _ownedResourcesTiles.stream().filter(t -> !t.isActive()).findFirst().orElse(null);
                if (blockedTile != null)
                {
                    weight = 1 / Math.abs(7 - blockedTile.getNbr()) * 2;
                    final int[] resourcesCount = {0};
                    _gameState.getBoard().getNeighborsVertices(blockedTile).stream()
                            .filter(v -> _gameState.ownerOf(v) != null && _gameState.ownerOf(v).getId() == this._id).forEach(v ->
                    {
                        resourcesCount[0]++;
                        if (_cities.contains(v))
                            resourcesCount[0]++;
                    });
                    weight += resourcesCount[0];
                }
            }
            else if (card.equals(DevelopmentCard.Road))
            {
                if (_desiredPath != null && _desiredPath.size() > 2)
                {
                    weight = _amtOfPts / 2.f + _desiredPath.size() / 2.f;
                }
            }
            /*
            We treat Monopoly the same as Years of plenty since the "economy" of the map is not
            really implemented. To implement them differently, we would need to have a way to
            know fuzzily the cards that the other players have. So we would need to:
            - Know which die is rolled at each turn
            - Fuzzily remember who picked which card
            - Fuzzily remember when someone spends some resources.
            (By fuzzily, I mean that we don't directly have the information. It would be implemented with
            a particle filter-kind of thing. Knowing directly the exact values would be wayy too overpowered for
            our agents.)
             */
            else if (card.equals(DevelopmentCard.Monopoly) || card.equals(DevelopmentCard.YearsOfPlenty))
            {
                if (_strategy != null)
                {
                    Map<Resource, Long> currCards = cards.stream()
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                    int amtNeeded = this.getDifferenceOfCards(currCards, Arrays.asList(_requiredResources.get(_strategy))).size();
                    weight = (_amtOfPts + 1) / 3.f * (amtNeeded / 3.f);
                }
            }
            else if(card.equals(DevelopmentCard.VictoryPoint))
            {
                weight = (10 - _amtOfPts)/3.f;
            }
            if (weight > maxWeight)
            {
                maxWeight = weight;
                bestAction = card;
            }
        }
        return bestAction;
    }

    private LinkedList<Edge> pathFindToVertex(Vertex objective, Edge begin)
    {
        Board board = _gameState.getBoard();
        LinkedList<Edge> path = new LinkedList<Edge>();
        LinkedList<Edge> visited = new LinkedList<Edge>();
        //It will be a really rudimentory Djikstra
        PriorityQueue<LinkedList<Edge>> paths = new PriorityQueue<>(new ListLengthComparator());
        path.add(begin);
        paths.add(path);
        Edge currElem = begin;
        if (_gameState.ownerOf(currElem) != null)
        {
            return null;
        }
        while (!board.getNeighborsEdges(objective).contains(currElem))
        {
            LinkedList<Edge> currBest = paths.poll();
            for (Edge neighbor : Edge.getNeighbors(currBest.getLast()))
            {
                if (neighbor.getPosition().equals(Coordinate.NULL_POSITION) || visited.contains(neighbor))
                    continue;
                if (_gameState.ownerOf(neighbor) != null)
                {
                    visited.add(neighbor);
                    continue;
                }
                path = new LinkedList<Edge>(currBest);
                path.add(neighbor);
                paths.add(path);
                visited.add(neighbor);
            }
            if (paths.peek() != null)
                currElem = paths.peek().getLast();
            else
                return null;
        }
        path = paths.poll();
        return path;
    }

    @Override
    public Edge placeRoad(Set<Edge> freeEdgesAvailable)
    {
        if (freeEdgesAvailable.toArray().length == 0)
            return null;
        _amtOfRoads++;
        Board board = _gameState.getBoard();
        //We'll take into consideration that the goal is to product another city.
        if (_wantedVertex != null)
        {
            Set<Vertex> availableVertices = new HashSet<Vertex>(Vertex.getNeighbors(_wantedVertex));
            Set<Vertex> takenVertices = availableVertices.stream().filter(v -> _gameState.ownerOf(v) != null)
                    .collect(Collectors.toSet());
            if (takenVertices.size() == 3 || _gameState.ownerOf(_wantedVertex) != null)
            {
                _wantedVertex = null;
                _desiredPath = null;
            }
            if (_desiredPath != null && !_desiredPath.isEmpty())
            {
                LinkedList<Edge> path = _desiredPath.stream().filter(e -> _gameState.ownerOf(e) != null)
                        .collect(Collectors.toCollection(LinkedList::new));
                if (path.size() > 0)
                    _desiredPath = null;
                else
                {
                    //Simplest case
                    Edge chosenEdge = _desiredPath.getFirst();
                    _desiredPath.removeFirst();
                    _ownedEdges.add(chosenEdge);
                    return chosenEdge; //We assume that we *can* use this path
                }
            }
            if (_wantedVertex != null)
            {
                //Here, we know that we COULD still have _wantedTile. We still have thing to check tho
                int count = (int) Vertex.getNeighbors(_wantedVertex).stream().filter(v -> _gameState.ownerOf(v) != null).count();

                if (count != 0)
                {
                    _wantedVertex = null;
                }
                else
                {
                    LinkedList<Edge> bestPath = new LinkedList<Edge>();
                    int bestLength = Integer.MAX_VALUE;
                    for (Edge edge : freeEdgesAvailable)
                    {

                        LinkedList<Edge> currPath = pathFindToVertex(_wantedVertex, edge);
                        if (currPath != null && currPath.size() < bestLength)
                        {
                            bestPath = currPath;
                            bestLength = currPath.size();
                        }

                    }
                    if (bestPath != null && !bestPath.isEmpty())
                    {
                        Edge chosenEdge = bestPath.getFirst();
                        bestPath.removeFirst();
                        _desiredPath = bestPath;

                        _ownedEdges.add(chosenEdge);
                        return chosenEdge;
                    }
                    else
                        _wantedVertex = null; //We can't pathfind to the wanted vertex
                }
            }
        }
        //Since _wantedVertex CAN become null during the execution, we just validate that it is indeed null
        if (_wantedVertex == null)
        {
            /*
            Ok so now we have to:
            1- Find the best tile to connect ourselves to
            2- Pathfind our way to there.
             */
            Set<Vertex> potentialDestination = new HashSet<Vertex>();
            for (Edge edge : freeEdgesAvailable)
            {
                //We'll take into considerations the neighbors as well as the neighbors' neighbors.
                for (Vertex v : board.getNeighborsVertices(edge))
                {
                    potentialDestination.add(v);
                    potentialDestination.addAll(Vertex.getNeighbors(v));
                }
            }
            //We'll remove the vertices that will not be valid
            potentialDestination = potentialDestination.stream()
                    .filter(v ->
                    {
                        List<Vertex> neighbors = Vertex.getNeighbors(v);
                        return neighbors.stream()
                                       .filter(neighbor -> _gameState.ownerOf(neighbor) == null).count() == neighbors.size()
                               && _gameState.ownerOf(v) == null;
                    }).collect(Collectors.toCollection(HashSet::new));
            if (potentialDestination != null && !potentialDestination.isEmpty())
                _wantedVertex = this.chooseVertex(potentialDestination);
            if (_wantedVertex != null)
            {
                LinkedList<Edge> bestPath = new LinkedList<Edge>();
                int bestLength = Integer.MAX_VALUE;
                for (Edge edge : freeEdgesAvailable)
                {
                    LinkedList<Edge> currPath = pathFindToVertex(_wantedVertex, edge);
                    if (currPath != null && currPath.size() < bestLength)
                    {
                        bestPath = currPath;
                        bestLength = currPath.size();
                    }

                }
                if(bestPath != null && !bestPath.isEmpty())
                {
                    Edge chosenEdge = bestPath.getFirst();
                    bestPath.removeFirst();
                    _desiredPath = bestPath;

                    _ownedEdges.add(chosenEdge);
                    return chosenEdge;
                }
            }

        }

        //In this case, we just chose an arbitrary edge since there is no good goal to have
        return (Edge)freeEdgesAvailable.toArray()[0];
    }

    private Vertex chooseVertex(Set<Vertex> freeVertexAvailable)
    {
        Vertex chosenVertex = null;
        float maxWeight = 0;
        for (Vertex vertex : freeVertexAvailable)
        {
            Set<Tile> tiles = _gameState.getBoard().getNeighborsTiles(vertex).stream()
                    .filter(tile -> tile.getResource() != Resource.Desert).collect(Collectors.toCollection(HashSet::new));

            float ratioNewResources = 0;
            int amtOfResources = tiles.size();
            float diceNumbers = 0.f;
            long amtOfDiffResources = tiles.stream().map(t -> t.getResource()).distinct().count();
            for (Tile tile : tiles)
            {
                List<Integer> diceNbrs = _ownedResourcesTiles.stream().filter(t -> t.getResource() == tile.getResource())
                        .map(elem -> elem.getNbr()).collect(Collectors.toCollection(ArrayList::new));
                ratioNewResources += diceNbrs != null ? 1 / (diceNbrs.size() + 1) : 1;
                diceNumbers += 1.f / Math.abs(tile.getNbr() - 7);
            }
            //Totally arbitrary
            //We take into consideration that:
            //The amtOfResources we gain from building the settlement is the most important
            //Followed by the quality of the dice numbers
            //Followed by the amt of new resources it gives us.
            float weight = amtOfResources * 4 + diceNumbers * 4 + amtOfDiffResources * 2 + ratioNewResources;
            if (weight > maxWeight)
            {
                chosenVertex = vertex;
                maxWeight = weight;
            }
        }
        if(chosenVertex != null)
            _gameState.getBoard().getNeighborsTiles(chosenVertex).stream().forEach(tile -> _ownedResourcesTiles.add(tile));
        return chosenVertex;
    }


    @Override
    public Vertex placeSettlement(Set<Vertex> freeVertexAvailable)
    {
        _amtOfSettlement++;
        return chooseVertex(freeVertexAvailable);
    }

    @Override
    public Vertex upgradeCity(Set<Vertex> vertices)
    {
        _amtOfSettlement--;
        _amtOfCities++;
        Vertex newCity = this.chooseVertex(vertices);
        _cities.add(newCity);
        return newCity;
    }

    private Resource choseBestResourceToAsk(List<Resource> cards, Map<Resource, Long> needed)
    {
        if (_strategy != null)
        {
            Map<Resource, Long> currCards = cards.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


            List<Resource> lacking = this.getDifferenceOfCards(currCards, Arrays.asList(_requiredResources.get(_strategy)));
            float biggestWeight = 0;
            Resource bestResourceToAsk = null;
            //This way if we need a single resource many times, we have more chance to actually ask it
            Map<Resource, Float> weights = new HashMap<Resource, Float>();
            for (Resource curr : lacking)
            {
                if (!weights.containsKey(curr))
                    weights.put(curr, 0.f);
                float weight = weights.get(curr);
                for (Tile tile : _ownedResourcesTiles)
                {
                    if (tile.getResource().equals(curr))
                        weight += Math.abs(7 - tile.getNbr()) / 5.f;

                }
                if (weight < 0.01)
                    weight = 10; //Since we don't have this resource, we would really want to trade it.
                weights.put(curr, weight);
                if (weight > biggestWeight)
                {
                    biggestWeight = weight;
                    bestResourceToAsk = curr;
                }
            }
            return bestResourceToAsk;
        }
        return null;
    }

    private TradeOffer chooseBestTrade(List<Resource> cards)
    {
        if (_strategy != null)
        {
            Map<Resource, Long> needed = Arrays.asList(_requiredResources.get(_strategy)).stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Resource bestResourceToAsk = choseBestResourceToAsk(cards, needed);
            float biggestWeight = 0;
            Resource bestResourceToOffer = null;
            Map<Resource, Long> currResources = cards.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            List<Map.Entry<Resource, Long>> unWantedResources = currResources.entrySet().stream()
                    .filter(x -> !needed.containsKey(x.getKey()) || needed.get(x.getKey()) < x.getValue())
                    .collect(Collectors.toList());
            for (Map.Entry<Resource, Long> currRes : unWantedResources)
            {
                float weight = 0;
                for (Tile curr : _ownedResourcesTiles)
                {
                    if (curr.getResource().equals(currRes.getKey()))
                        weight += (1.f / Math.abs(7 - curr.getNbr())) * currRes.getValue() / 2.f; //We want to offer a resource that we have too much of
                }
                if (weight < 0.01f)
                {
                    weight = 0.2f; //If we don't have a way to generate this resource, we don't want it to have a big weight.
                }
                if (biggestWeight < weight)
                {
                    biggestWeight = weight;
                    bestResourceToOffer = currRes.getKey();
                }
            }
            if (bestResourceToAsk == null || bestResourceToOffer == null)
                return null;
            return new TradeOffer(bestResourceToOffer, bestResourceToAsk);
        }
        return null;
    }

    @Override
    public TradeOffer createTradeOffer(List<Resource> cards)
    {
        return _trade != null ? _trade : chooseBestTrade(cards);
    }

    @Override
    public boolean receiveTradeOffer(TradeOffer tradeOffer, List<Resource> cards)
    {
        if(tradeOffer.demandingResource() == null || tradeOffer.givingResource() == null)
            return false;
        makeMove(cards, new LinkedList<DevelopmentCard>()); //To get the latest _strategy
        if (_strategy != null)
        {
            Map<Resource, Long> neededResources = Arrays.asList(_requiredResources.get(_strategy)).stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Map<Resource, Long> currResources = cards.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            List<Resource> lackingResources = getDifferenceOfCards(currResources, Arrays.asList(_requiredResources.get(_strategy)));

            if (lackingResources.contains(tradeOffer.givingResource()))
            {
                if (!neededResources.containsKey(tradeOffer.demandingResource()))
                    return true;
                if (neededResources.containsKey(tradeOffer.demandingResource()) && currResources.containsKey(tradeOffer.demandingResource()) &&
                    currResources.get(tradeOffer.demandingResource()) > neededResources.get(tradeOffer.demandingResource()))
                    return true;
            }
        }
        return false;
    }

    @Override
    public Resource playMonopolyCard(List<Resource> cards)
    {
        if (_strategy != null)
        {
            Map<Resource, Long> needed = Arrays.asList(_requiredResources.get(_strategy)).stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            return choseBestResourceToAsk(cards, needed);
        }
        return null;
    }

    @Override
    public Map.Entry<Resource, Resource> playYearsOfPlentyCard(List<Resource> cards)
    {
        if (_strategy != null)
        {
            Map<Resource, Long> needed = Arrays.asList(_requiredResources.get(_strategy)).stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            Resource firstResource = choseBestResourceToAsk(cards, needed);
            if (firstResource != null)
            {
                cards.add(firstResource);
                Resource secondResource = choseBestResourceToAsk(cards, needed);
                return new AbstractMap.SimpleEntry<Resource, Resource>(firstResource, secondResource);
            }
        }
        return null;
    }


    private Map.Entry<Resource, Resource> choseTradeToBankOrPort(List<Resource> cards)
    {
        if (_strategy != null)
        {
            Map<Resource, Long> needed = Arrays.asList(_requiredResources.get(_strategy)).stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Map<Resource, Long> currResources = cards.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


            List<Resource> portResources = new LinkedList<Resource>();
            if(_ownedEdges != null && !_ownedEdges.isEmpty())
                _gameState.getPortsOf(_gameState.ownerOf(_ownedEdges.get(0))).forEach(p -> portResources.add(p.getResource()));
            else
                return null;
            int tradeCost = 4;
            for (Resource resource : portResources)
            {
                if (resource.equals(Resource.Desert))
                {
                    tradeCost = 3;
                    break;
                }
            }
            Map<Resource, Long> non_needed = new HashMap<Resource, Long>();
            for (Map.Entry<Resource, Long> entry : currResources.entrySet())
            {
                if (!needed.containsKey(entry.getKey()))
                    non_needed.put(entry.getKey(), entry.getValue());
                else if (needed.get(entry.getKey()) < entry.getValue())
                    non_needed.put(entry.getKey(), entry.getValue() - needed.get(entry.getKey()));
            }
            float bestWeight = 0.f;
            Resource bestResource = null;
            //To be GIVEN
            for (Map.Entry<Resource, Long> entry : non_needed.entrySet())
            {
                float weight = 0.f;
                boolean gotPort = portResources.contains(entry.getKey()) && entry.getValue() >= 2;
                if (gotPort || entry.getValue() >= tradeCost)
                {
                    for (Tile curr : _ownedResourcesTiles)
                    {
                        if (curr.getResource().equals(entry.getKey()))
                            weight = (1.f / Math.abs(7 - curr.getNbr())) * entry.getValue() / 2.f; //We want to offer a resource that we have too much of
                    }
                    weight += gotPort ? 2 : 0;
                }
                if (bestWeight < weight)
                {
                    bestResource = entry.getKey();
                    bestWeight = weight;
                }
            }
            //Resource to request
            List<Resource> lacking = this.getDifferenceOfCards(currResources, Arrays.asList(_requiredResources.get(_strategy)));
            float biggestWeight = 0;
            Resource bestResourceToAsk = null;
            for (Resource curr : lacking)
            {
                float weight = 0;
                for (Tile tile : _ownedResourcesTiles)
                {
                    if (tile.getResource().equals(curr))
                        weight = tile.getNbr() / 5.f;

                }
                if (weight < 0.01)
                    weight = 10; //Since we don't have this resource, we would really want to trade it.
                if (biggestWeight < weight)
                {
                    biggestWeight = weight;
                    bestResourceToAsk = curr;
                }
            }
            if (bestResource == null || bestResourceToAsk == null)
                return null;
            return new AbstractMap.SimpleEntry<Resource, Resource>(bestResource, bestResourceToAsk);
        }
        return null;
    }

    @Override
    public Map.Entry<Resource, Resource> tradeToBank(List<Resource> cards)
    {
        return _tradeBank != null ? _tradeBank : choseTradeToBankOrPort(cards);
    }

    public class ListLengthComparator implements Comparator<List<Edge>>
    {
        @Override
        public int compare(List<Edge> o1, List<Edge> o2)
        {
            if (o1.size() == o2.size())
                return 0;
            return o1.size() > o2.size() ? 1 : -1;
        }
    }

}
