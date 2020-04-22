package Game;

import java.util.PrimitiveIterator;
import java.util.Random;
import java.util.stream.IntStream;

public class Dice
{
    private PrimitiveIterator.OfInt first;
    private PrimitiveIterator.OfInt second;


    public Integer roll()
    {
        if (first == null)
        {
            Random random = new Random();
            IntStream firstInts = random.ints(1, 7);
            first = firstInts.iterator();
            random = new Random();
            IntStream secondInts = random.ints(1, 7);
            second = secondInts.iterator();
        }
        return first.next() + second.next();
    }
}
