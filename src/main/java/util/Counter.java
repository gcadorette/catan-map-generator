package util;

public class Counter
{
    private int _count;

    public Counter()
    {
        _count = 0;
    }

    public void increment()
    {
        _count++;
    }

    public void decrement()
    {
        _count--;
    }

    public int get()
    {
        return _count;
    }
}
