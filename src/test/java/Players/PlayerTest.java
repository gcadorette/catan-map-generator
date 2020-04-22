package Players;

import Board.Resources.Resource;
import org.junit.Before;
import org.junit.Test;

import static Board.Resources.Resource.*;
import static org.junit.Assert.*;

public class PlayerTest
{

    Player _player;

    @Before
    public void setup()
    {
        _player = new Player(null, 0);
    }

    @Test
    public void canAddCard()
    {
        _player.addCards(Brick, Brick, Brick, Grain, Grain, Wool);
        assertEquals(6, _player.numberOfCards());
    }

    @Test
    public void canPay()
    {

        _player.addCards(Brick, Brick, Brick, Grain, Grain, Wool);
        _player.pay(Brick, Grain, Wool);

        assertEquals(3, _player.numberOfCards());
    }
}