package Board;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static Board.JSONBoardParser.writeFile;
import static org.junit.Assert.assertEquals;

public class JSONBoardParserTest
{

    @Test
    public void canWriteAndRead() throws IOException, Exception
    {
        BoardConfiguration originalBoard = BoardGenerator.generateRandomMap();
        writeFile(originalBoard);
        BoardConfiguration parsedConfiguration = JSONBoardParser.parseFile();
        assertEquals(originalBoard.getTiles(), parsedConfiguration.getTiles());
    }
}