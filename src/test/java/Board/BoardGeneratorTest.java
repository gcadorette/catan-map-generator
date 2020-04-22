package Board;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static Board.BoardGenerator.generateRandomMap;
import static org.junit.Assert.*;

public class BoardGeneratorTest
{

    @Test
    public void generatedBoardsAreAllDifferent()
    {
        List<Tile> tiles1 = Lists.newArrayList(generateRandomMap().getTiles());
        List<Tile> tiles2 = Lists.newArrayList(generateRandomMap().getTiles());
        List<Tile> tiles3 = Lists.newArrayList(generateRandomMap().getTiles());

        Collections.sort(tiles1);
        Collections.sort(tiles2);
        Collections.sort(tiles3);

        assertNotEquals(tiles1, tiles2);
        assertNotEquals(tiles1, tiles3);
        assertNotEquals(tiles2, tiles3);
    }

    @Test
    public void generatedBoardsAreValid()
    {
        for (int i = 0; i < 10; i ++)
            assertTrue(generateRandomMap().isValid());
    }
}