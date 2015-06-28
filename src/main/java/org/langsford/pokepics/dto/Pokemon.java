package org.langsford.pokepics.dto;

import java.awt.image.BufferedImage;

/**
 * Created by trevyn on 1/11/15.
 */
public class Pokemon {

    private int nationalId;
    private String name;
    private String resourceURI;
    private BufferedImage sprite;

    public Pokemon() {

    }

    public Pokemon(int nationalId, String name, String resourceURI) {
        this.nationalId = nationalId;
        this.name = name;
        this.resourceURI = resourceURI;
        this.sprite = null;
    }

    public Pokemon(int nationalId, String name, String resourceURI, BufferedImage sprite) {
        this.nationalId = nationalId;
        this.name = name;
        this.resourceURI = resourceURI;
        this.sprite = sprite;
    }

    public int getNationalId() {
        return nationalId;
    }

    public void setNationalId(int nationalId) {
        this.nationalId = nationalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceURI() {
        return resourceURI;
    }

    public void setResourceURI(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    public BufferedImage getSprite() {
        return sprite;
    }

    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }
}
