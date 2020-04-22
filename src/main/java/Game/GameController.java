package Game;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class GameController
{
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletResponse response)
    {
        return "index.html";
    }

    @GetMapping("/generate")
    @ResponseBody
    public String generate(@RequestParam String amtOfPlayers, @RequestParam String amtOfMaps, @RequestParam String amtOfGames)
    {
        int amtOfPlayersI = Integer.parseInt(amtOfPlayers);
        int amtOfMapsI = Integer.parseInt(amtOfMaps);
        int amtOfGamesI = Integer.parseInt(amtOfGames);
        String generated = App.start(amtOfPlayersI , amtOfMapsI, amtOfGamesI);
        return generated;
    }


}

