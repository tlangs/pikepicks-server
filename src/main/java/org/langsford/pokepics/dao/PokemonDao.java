package org.langsford.pokepics.dao;

import org.json.JSONArray;
import org.json.JSONObject;
import org.langsford.pokepics.dto.Pokemon;
import org.langsford.pokepics.enumeration.Region;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * Created by trevyn on 6/28/15.
 */
@Component
public class PokemonDao {

    private List<Integer> allIds = new ArrayList<>();
    private Map<Integer, Pokemon> pokeMap = new HashMap<>();
    private Pokemon missingNo;
    private Random random = new Random();

    /**
     * Constructs the DAO with all the required components. Fills pokeMap with pokemon and instantiates missingNo.
     * Also sets up allIds for use later.
     * @throws IOException
     */
    public PokemonDao() throws IOException{
        for (Region r : Region.values()) {
            this.allIds.addAll(r.getIds());
        }

        JSONObject response = getJSON("http://pokeapi.co/api/v1/pokedex/1");
        JSONArray a = response.getJSONArray("pokemon");
        for (int i = 0; i < a.length(); i++) {
            String resourceURI = a.getJSONObject(i).getString("resource_uri");
            String[] elements = resourceURI.split("/");
            Integer nationalId = Integer.parseInt(elements[elements.length - 1]);
            String name = a.getJSONObject(i).getString("name");
            if (this.allIds.contains(nationalId)) {
                Pokemon poke = new Pokemon(nationalId, name, resourceURI);

                pokeMap.put(poke.getNationalId(), poke);
            }
        }

        missingNo = new Pokemon(-1, "MissingNo", null, ImageIO.read(PokemonDao.class.getResourceAsStream("/Missingno_RB.png")));

    }

    /**
     * Find a pokemon by its National ID. If the pokemon does not exist or can't be found, MissingNo is returned.
     * @param nationalId National ID of the pokemon to find
     * @return The pokemon belonging to the given National ID or MissingNo
     */
    public Pokemon findPokemon(Integer nationalId) {
        Pokemon poke = pokeMap.get(nationalId);
        if (poke == null) {
            return missingNo;
        }
        if (poke.getSprite() == null) {
            initSprite(poke);
        }
        return poke;
    }

    public Pokemon findRandomPokemon(Region[] regions) {
        List<Integer> availableIds;
        if (regions == null || regions.length == 0) {
            availableIds = allIds;
        }
        else {
            availableIds = new ArrayList<>();
            for (Region r : regions) {
                availableIds.addAll(r.getIds());
            }
        }
        return findPokemon(availableIds.get(random.nextInt(availableIds.size())));
    }

    /**
     * Initialize the sprite of a pokemon. If the pokemon's sprite cannot be found, MissingNo's sprite is used.
     * @param pokemon Pokemon to fill sprite for.
     */
    private void initSprite(Pokemon pokemon) {
        try {
            JSONObject pokemonEntry = getJSON("http://pokeapi.co/" + pokemon.getResourceURI());
            JSONArray spritesArray = pokemonEntry.getJSONArray("sprites");
            JSONObject spriteInfo = getJSON("http://pokeapi.co/" +
                    spritesArray.getJSONObject(0).getString("resource_uri"));
            BufferedImage image = ImageIO.read(new URL("http://pokeapi.co/" + spriteInfo.getString("image")));
            pokemon.setSprite(image);
        } catch (IOException e) {
            System.out.println(String.format("Could not find sprite for %s [id = %d]. Using MissingNo",
                    pokemon.getName(), pokemon.getNationalId()));
            pokemon.setSprite(missingNo.getSprite());
        }
    }

    private static JSONObject getJSON(String url) throws IOException {
        URL urlToGet = new URL(url);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(urlToGet.openStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        return new JSONObject(builder.toString());
    }

    public Map<Integer, Pokemon> getPokeMap() {
        return pokeMap;
    }


}
