package org.langsford.pokepics.service;

import org.langsford.pokepics.dao.PokemonDao;
import org.langsford.pokepics.dto.Pokemon;
import org.langsford.pokepics.enumeration.Region;
import org.langsford.pokepics.util.PokePictureImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by trevyn on 2/8/15.
 */
@Service
public class PokePictureService {


    private PokemonDao pokemonDao;

    private Font font;
    private Map<Integer, String> pokemonIdNames = new HashMap<>();

    @Autowired
    public PokePictureService(PokemonDao pokemonDao) {
        this.pokemonDao = pokemonDao;
        for (Map.Entry<Integer, Pokemon> pokemonEntry : pokemonDao.getPokeMap().entrySet()) {
            pokemonIdNames.put(pokemonEntry.getKey(), pokemonEntry.getValue().getName());
        }
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT,
                    PokePictureService.class.getResourceAsStream("/fonts/PokemonSolid.ttf"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage makeImageForPokemon(Integer pokemonId, String text) {
        Pokemon pokemon = pokemonDao.findPokemon(pokemonId);
        return makeImage(pokemon.getSprite(), text);
    }

    public BufferedImage makeRandomImage(String text, Region[] regions) {

        Pokemon random = pokemonDao.findRandomPokemon(regions);

        return makeImage(random.getSprite(), text);
    }

    private BufferedImage makeImage(BufferedImage sprite, String name) {
        int bottomOfSprite = PokePictureImageUtils.getBottomOfSprite(sprite);
        BufferedImage image = new BufferedImage(
                (sprite.getWidth() * 3) + 50, (sprite.getHeight() * 5),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        float size = 120F;
        g.setFont(this.font.deriveFont(size));
        FontMetrics fontMetrics = g.getFontMetrics();
        while (fontMetrics.stringWidth(name) > sprite.getWidth() * 3 - 50) {
            size--;
            g.setFont(font.deriveFont(size));
            fontMetrics = g.getFontMetrics();
        }
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.drawImage(sprite, 25, 0, sprite.getWidth() * 3, sprite.getHeight() * 3, null);
        g.setColor(Color.BLACK);
        int rgb = image.getRGB(1, 1);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        int fontht = fontMetrics.getHeight();
        g.drawString(name, 50, (bottomOfSprite * 3) + fontht);
        g.dispose();

        return PokePictureImageUtils.cropOutWhitespace(image);
    }

    public Map<Integer, String> getPokemonIdNames() {
       return this.pokemonIdNames;
    }

    public Collection<Pokemon> getAvailablePokemon() {
        return pokemonDao.getPokeMap().values();
    }


}
