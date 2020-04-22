package Board;

public enum EdgeOrientation
{
    NORTH_WEST(1),
    NORTH_EAST(2),
    EAST(3);
    private int _val;
    EdgeOrientation(int val) { _val = val; }

    public int get_val()
    {
        return _val;
    }
}
