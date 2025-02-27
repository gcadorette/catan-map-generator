/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Game;

import Board.Board;
import Board.BoardConfiguration;
import Board.BoardExporter;
import Board.BoardGenerator;
import Players.IntelligentAgent;
import Players.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class App
{
    static BoardConfiguration bestBoard;
    static float bestWeight;
    static List<Thread> threads = new LinkedList<>();
    private static ReentrantLock lock;
    private static volatile AtomicInteger counter = new AtomicInteger(0);

    public static String start(int playerCount, int amt_of_generated_maps, int games_per_maps)
    {
        lock = new ReentrantLock();
        bestBoard = null;
        bestWeight = Float.MAX_VALUE;
        final int AMT_OF_REAL_CORES = Runtime.getRuntime().availableProcessors();
        final long start50 = System.currentTimeMillis();
        for (int k = 0; k < amt_of_generated_maps / AMT_OF_REAL_CORES; k++)
        {
            for (int i = 0; i < AMT_OF_REAL_CORES; i++)
            {
                testMap(games_per_maps, playerCount);
            }
            for (Thread t : threads)
            {
                try
                {
                    t.join();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        for (int i = 0; i < amt_of_generated_maps - (amt_of_generated_maps / AMT_OF_REAL_CORES) * AMT_OF_REAL_CORES; i++)
        {
            testMap(games_per_maps, playerCount);
        }
        for (Thread t : threads)
        {
            try
            {
                t.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        final long endTime50 = System.currentTimeMillis();
        System.out.println("TOTAL:  " + (endTime50 - start50) + "\n\n");
        System.out.println("The best board with a weight of " + bestWeight);
        System.out.println(new Board(bestBoard));
        return BoardExporter.drawBoard(bestBoard);
    }

    private static void testMap(int amt_of_games, int playerCount)
    {
        Runnable runnable = () ->
        {
            Game game;

            BoardConfiguration currBoardConfig = BoardGenerator.generateRandomMap();
            Board currBoard = new Board(currBoardConfig);
            float weight = 0;
            for (int j = 0; j < amt_of_games; j++)
            {
                List<Player> players = new LinkedList<Player>();
                for (int i = 0; i < playerCount; i++)
                {
                    Player p = new Player(new IntelligentAgent(), i);
                    players.add(p);
                }
                game = new Game(currBoard, players);
                weight += game.start();
            }
            weight = weight / amt_of_games;
            System.out.println(counter.getAndIncrement() + " Average weight of this map : " + weight);
            lock.lock();
            try
            {
                if (weight < bestWeight)
                {
                    bestWeight = weight;
                    bestBoard = currBoardConfig;
                }
            } finally
            {
                lock.unlock();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        threads.add(thread);
    }
}
