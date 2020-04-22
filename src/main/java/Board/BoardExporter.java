package Board;

import Board.Resources.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static Board.Resources.Resource.*;

public class BoardExporter
{
    private static final String IMAGE_FILE = "src" + File.separator + "main" + File.separator +
            "images" + File.separator;// + "static" + File.separator + "images" + File.separator;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    private static String randomAlphaNumeric(int count)
    {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0)
        {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private static Map<Resource, Color> resourceColorMap;

    public static void main(String[] args)
    {
        drawBoard(BoardGenerator.generateRandomMap());
    }

    public static String drawBoard(BoardConfiguration boardConfiguration)
    {
        Set<Tile> tiles = boardConfiguration.getTiles();

        BufferedImage image = new BufferedImage(600, 600, 1);
        Graphics2D graphics = image.createGraphics();

        graphics.translate(300, 300);
        addWater(graphics);
        for (Tile tile : tiles)
        {
            addTile(graphics, tile);
        }

        addPorts(graphics);

        try
        {
            return saveImage(image);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }


//        JLabel picLabel = new JLabel(new ImageIcon(image));
//
//        JPanel jPanel = new JPanel();
//        jPanel.add(picLabel);
//
//        JFrame f = new JFrame();
//        f.setSize(new Dimension(image.getWidth(), image.getHeight()));
//        f.setBackground(Color.WHITE);
//        f.add(jPanel);
//        f.setVisible(true);
    }

    private static String saveImage(BufferedImage image) throws IOException
    {
        String generated = randomAlphaNumeric(10);
        String filename = IMAGE_FILE + generated + ".png";
        File file = new File(filename);
        file.mkdirs();
        ImageIO.write(image, "png", file);
        return generated;
    }

    private static void addWater(Graphics2D graphics2D)
    {
        Polygon polygon = getPolygon(280, 0);
        graphics2D.setPaint(Color.BLUE);
        graphics2D.fillPolygon(polygon);
    }

    private static void addTile(Graphics2D graphics2D, Tile tile)
    {
        Coordinate position = tile.getPosition();

        int sideLength = 50;
        double apothem = (Math.sqrt(3) / 2) * sideLength;
        double y = (position.getY() - 2) * (sideLength * 1.5);
        double x = (position.getX() - 2) * apothem * 2.0;
        switch(position.getY())
        {
            case 0:
                x += (position.getY() + 2 ) * apothem;
                break;
            case 1:
                x += (position.getY() + 1 ) * apothem/2.0;
                break;
            case 2:
                x += (position.getY());
                break;
            case 3:
                x += (position.getY() - 1 ) * -apothem/2.0;
                break;
            case 4:
                x += (position.getY() - 2 ) * -apothem;
                break;
        }

        graphics2D.translate(x, y);
        Polygon polygon = getPolygon(sideLength, 30);
        graphics2D.setPaint(resourceColorMap.get(tile.getResource()));
        graphics2D.fillPolygon(polygon);
        graphics2D.drawPolygon(polygon);
        if (tile.getNbr() != 0)
        {
            graphics2D.setPaint(Color.BLACK);
            graphics2D.drawString("" + tile.getNbr(), 0, 0);
        }

        graphics2D.translate(-x, -y);
    }

    private static void addPorts(Graphics2D graphics2D)
    {
        graphics2D.setPaint(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(2));

        graphics2D.drawString("3 : 1", -130, -210);
        graphics2D.drawLine(-120, -210, -2 * 43 , -200);
        graphics2D.drawLine(-120, -210, -3 * 43 , -175);

        graphics2D.drawString("Wool 2 : 1", 30, -210);
        graphics2D.drawLine(30, -210, 0 , -200);
        graphics2D.drawLine(30, -210, 43 , -175);

        graphics2D.drawString("3 : 1", 150, -150);
        graphics2D.drawLine(150, -150, 3 * 43 , -125);
        graphics2D.drawLine(150, -150, 4 * 43 , -100);

        graphics2D.drawString("3 : 1", 250, 0);
        graphics2D.drawLine(240, 0, 5 * 43 , -25);
        graphics2D.drawLine(240, 0, 5 * 43 , 25);

        graphics2D.drawString("Brick 2 : 1", 135, 160);
        graphics2D.drawLine(150, 150, 3 * 43 , 125);
        graphics2D.drawLine(150, 150, 4 * 43 , 100);

        graphics2D.drawString("Lumber 2 : 1", 30, 220);
        graphics2D.drawLine(30, 210, 0 , 200);
        graphics2D.drawLine(30, 210, 43 , 175);

        graphics2D.drawString("3 : 1", -130, 220);
        graphics2D.drawLine(-120, 210, -2 * 43 , 200);
        graphics2D.drawLine(-120, 210, -3 * 43 , 175);

        graphics2D.drawString("Grain 2 : 1", -240, 60);
        graphics2D.drawLine(-200, 75, -4 * 43 , 50);
        graphics2D.drawLine(-200, 75, -4 * 43 , 100);

        graphics2D.drawString("Ore 2 : 1", -230, -50);
        graphics2D.drawLine(-200, -75, -4 * 43 , -50);
        graphics2D.drawLine(-200, -75, -4 * 43 , -100);
    }


    private static Polygon getPolygon(int r, int init)
    {
        int[] x = new int[6];
        int[] y = new int[6];
        for (int i = 0, theta = init; i <= 5; i++, theta += 60)
        {
            x[i] = (int)(r * Math.cos(Math.toRadians(theta)));
            y[i] = (int)(r * Math.sin(Math.toRadians(theta)));
        }
        return new Polygon(x, y, 6);
    }

    static
    {
        resourceColorMap = new HashMap<>();
        resourceColorMap.put(Brick, new Color(255,69,0));
        resourceColorMap.put(Lumber, new Color(0,100,0));
        resourceColorMap.put(Wool, Color.WHITE);
        resourceColorMap.put(Grain, Color.YELLOW);
        resourceColorMap.put(Ore, Color.GRAY);
        resourceColorMap.put(Desert, new Color(210,180,140));
    }
}
