package Board;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import java.io.*;

public class JSONBoardParser
{
    private static final String JSON_FILE = "src" + File.separator + "main" + File.separator +
                                            "resources" + File.separator + "boards" + File.separator + "boardConfiguration.json";


    public static BoardConfiguration parseFile(String filename) throws FileNotFoundException
    {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filename));
        return gson.fromJson(reader, BoardConfiguration.class);
    }

    public static BoardConfiguration parseFile() throws FileNotFoundException
    {
        return parseFile(JSON_FILE);
    }

    public static void writeFile(BoardConfiguration boardConfiguration) throws IOException
    {
        File file = new File(JSON_FILE);
        file.createNewFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Writer writer = new FileWriter(file);

        gson.toJson(boardConfiguration, BoardConfiguration.class, gson.newJsonWriter(writer));
        writer.close();
    }
}
