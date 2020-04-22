package Board;

import java.util.*;
import java.util.stream.Collectors;

import static Board.Coordinate.*;
import static Board.VertexOrientation.*;

public class Vertex
{
    private Coordinate _position;
    private VertexOrientation _orientation;

    public Vertex(Coordinate position, VertexOrientation orientation)
    {
        _position = position;
        _orientation = orientation;
    }

    public Coordinate getPosition()
    {
        return _position;
    }

    public VertexOrientation getOrientation()
    {
        return _orientation;
    }

    public static List<Vertex> getNeighbors(Vertex vertex)
    {
        Coordinate pos = vertex.getPosition();
        if (vertex._orientation == NORTH)
        {
            return ALL_VERTICES.stream().filter(v -> v.getOrientation().equals(SOUTH) && (v.getPosition().equals(pos.getNW()) || v.getPosition().equals(pos.getNE()) || v.getPosition().equals(pos.getNE().getNW()))).collect(Collectors.toList());
        }
        return ALL_VERTICES.stream().filter(v -> v.getOrientation().equals(NORTH) && (v.getPosition().equals(pos.getSW()) || v.getPosition().equals(pos.getSE()) || v.getPosition().equals(pos.getSE().getSW()))).collect(Collectors.toList());
    }

    public static boolean areNeighbors(Vertex v1, Vertex v2)
    {
        return oneWayCheck(v1, v2) || oneWayCheck(v2, v1);
    }

    private static boolean oneWayCheck(Vertex v1, Vertex v2)
    {
        if (v1._position == v2._position)
        {
            return false;
        }

        if (v1._orientation == v2._orientation)
        {
            return false;
        }

        if (v1._orientation == NORTH)
        {
            return v2._position == v1._position.getNW() || v2._position == v1._position.getNE();
        }

        if (v1._orientation == SOUTH)
        {
            return v2._position == v1._position.getSW() || v2._position == v1._position.getSE();
        }

        return false;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return _position.equals(vertex._position) &&
               _orientation == vertex._orientation;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(_position, _orientation);
    }

    @Override
    public String toString() {
        return this._position + " " + this._orientation;
    }

    public static List<Vertex> ALL_VERTICES;

    public static Vertex p00N = new Vertex(p00, NORTH);
    public static Vertex p10N = new Vertex(p10, NORTH);
    public static Vertex p20N = new Vertex(p20, NORTH);
    public static Vertex p01N = new Vertex(p01, NORTH);
    public static Vertex p11N = new Vertex(p11, NORTH);
    public static Vertex p21N = new Vertex(p21, NORTH);
    public static Vertex p31N = new Vertex(p31, NORTH);
    public static Vertex p02N = new Vertex(p02, NORTH);
    public static Vertex p12N = new Vertex(p12, NORTH);
    public static Vertex p22N = new Vertex(p22, NORTH);
    public static Vertex p32N = new Vertex(p32, NORTH);
    public static Vertex p42N = new Vertex(p42, NORTH);
    public static Vertex p13N = new Vertex(p13, NORTH);
    public static Vertex p23N = new Vertex(p23, NORTH);
    public static Vertex p33N = new Vertex(p33, NORTH);
    public static Vertex p43N = new Vertex(p43, NORTH);
    public static Vertex p24N = new Vertex(p24, NORTH);
    public static Vertex p34N = new Vertex(p34, NORTH);
    public static Vertex p44N = new Vertex(p44, NORTH);

    public static Vertex p00S = new Vertex(p00, SOUTH);
    public static Vertex p10S = new Vertex(p10, SOUTH);
    public static Vertex p20S = new Vertex(p20, SOUTH);
    public static Vertex p01S = new Vertex(p01, SOUTH);
    public static Vertex p11S = new Vertex(p11, SOUTH);
    public static Vertex p21S = new Vertex(p21, SOUTH);
    public static Vertex p31S = new Vertex(p31, SOUTH);
    public static Vertex p02S = new Vertex(p02, SOUTH);
    public static Vertex p12S = new Vertex(p12, SOUTH);
    public static Vertex p22S = new Vertex(p22, SOUTH);
    public static Vertex p32S = new Vertex(p32, SOUTH);
    public static Vertex p42S = new Vertex(p42, SOUTH);
    public static Vertex p13S = new Vertex(p13, SOUTH);
    public static Vertex p23S = new Vertex(p23, SOUTH);
    public static Vertex p33S = new Vertex(p33, SOUTH);
    public static Vertex p43S = new Vertex(p43, SOUTH);
    public static Vertex p24S = new Vertex(p24, SOUTH);
    public static Vertex p34S = new Vertex(p34, SOUTH);
    public static Vertex p44S = new Vertex(p44, SOUTH);

    public static Vertex w_11S = new Vertex(w_11, SOUTH);
    public static Vertex w_10S = new Vertex(w_10, SOUTH);
    public static Vertex w_1_1S = new Vertex(w_1_1, SOUTH);
    public static Vertex w0_1S = new Vertex(w0_1, SOUTH);
    public static Vertex w1_1S = new Vertex(w1_1, SOUTH);
    public static Vertex w2_1S = new Vertex(w2_1, SOUTH);
    public static Vertex w30S = new Vertex(w30, SOUTH);
    public static Vertex w41S = new Vertex(w41, SOUTH);

    public static Vertex w53N = new Vertex(w53, NORTH);
    public static Vertex w54N = new Vertex(w54, NORTH);
    public static Vertex w55N = new Vertex(w55, NORTH);
    public static Vertex w45N = new Vertex(w45, NORTH);
    public static Vertex w35N = new Vertex(w35, NORTH);
    public static Vertex w25N = new Vertex(w25, NORTH);
    public static Vertex w14N = new Vertex(w14, NORTH);
    public static Vertex w03N = new Vertex(w03, NORTH);


    static {

        ALL_VERTICES = new ArrayList<>();

        ALL_VERTICES.add(p00N);
        ALL_VERTICES.add(p10N);
        ALL_VERTICES.add(p20N);
        ALL_VERTICES.add(p01N);
        ALL_VERTICES.add(p11N);
        ALL_VERTICES.add(p21N);
        ALL_VERTICES.add(p31N);
        ALL_VERTICES.add(p02N);
        ALL_VERTICES.add(p12N);
        ALL_VERTICES.add(p22N);
        ALL_VERTICES.add(p32N);
        ALL_VERTICES.add(p42N);
        ALL_VERTICES.add(p13N);
        ALL_VERTICES.add(p23N);
        ALL_VERTICES.add(p33N);
        ALL_VERTICES.add(p43N);
        ALL_VERTICES.add(p24N);
        ALL_VERTICES.add(p34N);
        ALL_VERTICES.add(p44N);
        ALL_VERTICES.add(p00S);
        ALL_VERTICES.add(p10S);
        ALL_VERTICES.add(p20S);
        ALL_VERTICES.add(p01S);
        ALL_VERTICES.add(p11S);
        ALL_VERTICES.add(p21S);
        ALL_VERTICES.add(p31S);
        ALL_VERTICES.add(p02S);
        ALL_VERTICES.add(p12S);
        ALL_VERTICES.add(p22S);
        ALL_VERTICES.add(p32S);
        ALL_VERTICES.add(p42S);
        ALL_VERTICES.add(p13S);
        ALL_VERTICES.add(p23S);
        ALL_VERTICES.add(p33S);
        ALL_VERTICES.add(p43S);
        ALL_VERTICES.add(p24S);
        ALL_VERTICES.add(p34S);
        ALL_VERTICES.add(p44S);
        ALL_VERTICES.add(w_11S);
        ALL_VERTICES.add(w_10S);
        ALL_VERTICES.add(w_1_1S);
        ALL_VERTICES.add(w0_1S);
        ALL_VERTICES.add(w1_1S);
        ALL_VERTICES.add(w2_1S);
        ALL_VERTICES.add(w30S);
        ALL_VERTICES.add(w41S);
        ALL_VERTICES.add(w53N);
        ALL_VERTICES.add(w54N);
        ALL_VERTICES.add(w55N);
        ALL_VERTICES.add(w45N);
        ALL_VERTICES.add(w35N);
        ALL_VERTICES.add(w25N);
        ALL_VERTICES.add(w14N);
        ALL_VERTICES.add(w03N);
    }
}
