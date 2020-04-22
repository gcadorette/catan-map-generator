package GameObjects.developmentCards;

import Board.Resources.DevelopmentCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static Board.Resources.DevelopmentCard.*;

public class DevelopmentCardDeck
{
    private List<DevelopmentCard> _developmentCards;

    public DevelopmentCardDeck()
    {
        initDeck();
    }

    public DevelopmentCard pickCard()
    {
        if (!_developmentCards.isEmpty())
            return _developmentCards.remove(0);
        return null;
    }

    private void initDeck()
    {
        _developmentCards = new ArrayList<>();

        for (int i = 0; i < 14; i++)
            _developmentCards.add(Knight);

        for (int i = 0; i < 5; i++)
            _developmentCards.add(VictoryPoint);

        for (int i = 0; i < 2; i++)
            _developmentCards.add(Road);

        for (int i = 0; i < 2; i++)
            _developmentCards.add(YearsOfPlenty);

        for (int i = 0; i < 2; i++)
            _developmentCards.add(Monopoly);
        Collections.shuffle(_developmentCards);
    }

}
