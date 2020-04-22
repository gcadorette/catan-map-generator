package Board;

import Board.Resources.Resource;

import java.util.HashSet;
import java.util.Set;

import static Board.Resources.Resource.*;
import static Board.Vertex.*;

public class Port
{

    public static Port P0 = new Port(w_1_1S, Desert);
    public static Port P1 = new Port(p00N, Desert);
    public static Port P2 = new Port(p10N, Wool);
    public static Port P3 = new Port(w1_1S, Wool);
    public static Port P4 = new Port(p31N, Desert);
    public static Port P5 = new Port(w30S, Desert);
    public static Port P6 = new Port(w41S, Desert);
    public static Port P7 = new Port(w53N, Desert);
    public static Port P8 = new Port(w54N, Brick);
    public static Port P9 = new Port(p43S, Brick);
    public static Port P10 = new Port(w45N, Lumber);
    public static Port P11 = new Port(p34S, Lumber);
    public static Port P12 = new Port(p24S, Desert);
    public static Port P13 = new Port(w25N, Desert);
    public static Port P14 = new Port(w14N, Grain);
    public static Port P15 = new Port(p02S, Grain);
    public static Port P16 = new Port(p02N, Ore);
    public static Port P17 = new Port(w_10S, Ore);

    public static Set<Port> ALL_PORTS;


    private Vertex _vertex;
    private Resource _resource;

    private Port(Vertex vertex, Resource resource)
    {
        _vertex = vertex;
        _resource = resource;
    }

    public Vertex getVertex()
    {
        return _vertex;
    }

    public Resource getResource()
    {
        return _resource;
    }

    public int getQuantity()
    {
        // Desert is a flag for typeless ports
        return _resource == Desert? 3 : 2;
    }

    static
    {
        ALL_PORTS = new HashSet<>();

        ALL_PORTS.add(P0);
        ALL_PORTS.add(P1);
        ALL_PORTS.add(P2);
        ALL_PORTS.add(P3);
        ALL_PORTS.add(P4);
        ALL_PORTS.add(P5);
        ALL_PORTS.add(P6);
        ALL_PORTS.add(P7);
        ALL_PORTS.add(P8);
        ALL_PORTS.add(P9);
        ALL_PORTS.add(P10);
        ALL_PORTS.add(P11);
        ALL_PORTS.add(P12);
        ALL_PORTS.add(P13);
        ALL_PORTS.add(P14);
        ALL_PORTS.add(P15);
        ALL_PORTS.add(P16);
        ALL_PORTS.add(P17);
    }
}
