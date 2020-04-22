package GameObjects;


import Board.Resources.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Board.Resources.Resource.*;

public class Cards
{
    private Map<Resource, Card> _cards;

    public Cards()
    {
        _cards = new HashMap<>();

        _cards.put(Brick, new Card());
        _cards.put(Lumber, new Card());
        _cards.put(Wool, new Card());
        _cards.put(Grain, new Card());
        _cards.put(Ore, new Card());
    }

    public Cards(Cards other)
    {
        _cards = new HashMap<Resource, Card>(other._cards);
    }

    public int count()
    {
        return _cards.values().stream().map(Card::getCount).reduce(0, Integer::sum);
    }


    public int getNumber(Resource resource)
    {
        if(!_cards.containsKey(resource))
            return 0;
        return _cards.get(resource).getCount();
    }

    public void add(Resource resource, int number)
    {
        _cards.get(resource).add(number);
    }

    public void add(Resource resource)
    {
        add(resource, 1);
    }

    public boolean remove(Resource resource, int number)
    {
        return _cards.get(resource).remove(number);
    }

    public boolean remove(Resource resource)
    {
        return remove(resource, 1);
    }

    public List<Resource> asList()
    {
        List<Resource> list = new ArrayList<>();

        for (Map.Entry<Resource, Card> entry : _cards.entrySet())
        {
            for (int i = 0; i < entry.getValue().getCount(); ++i)
                list.add(entry.getKey());
        }

        return list;
    }
}
