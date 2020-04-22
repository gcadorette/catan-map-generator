package GameObjects;

public class Card
{
    private int count;

    public Card()
    {
        count = 0;
    }

    public void add(int amount)
    {
        count += amount;
    }

    public boolean remove(int amount)
    {
        if (amount > count)
            return false;
        count -= amount;
        return true;
    }

    public int getCount()
    {
        return count;
    }



}
