package org.langsford.pokepics.controller;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.langsford.pokepics.dto.Pokemon;
import org.langsford.pokepics.dto.PokemonDisplay;
import org.langsford.pokepics.service.PokePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by trevyn on 1/11/15.
 */
@Controller
@RequestMapping("/")
public class PokePictureController {

    @Autowired
    private PokePictureService pokePictureService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView pokePage() {
        ModelAndView mav = new ModelAndView("poke");
        List<PokemonDisplay> pokeNames = new ArrayList<>();
        for (Map.Entry<Integer, Pokemon> entry : pokePictureService.getPokemonIdMap().entrySet()) {
            PokemonDisplay pd = new PokemonDisplay();
            pd.setName(WordUtils.capitalize(entry.getValue().getName(), ' ', '-'));
            pd.setValue(entry.getValue().getName());
            pokeNames.add(pd);
        }
        mav.addObject("pokemonList", pokeNames);
        return mav;
    }

    @RequestMapping(value="/fileupload", method= RequestMethod.POST, produces = "application/zip")
    public @ResponseBody
    byte[] handleFileUpload(@RequestParam("fileUpload") MultipartFile file,
                                                 HttpServletResponse response) {
        byte[] output = new byte[0];
        if (!file.isEmpty()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                output = bulkUpload(sb.toString(), response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fileName = file.getOriginalFilename().split("\\.")[0];
        response.setHeader("Content-Disposition", "filename=" + fileName + ".zip");
        return output;
    }

    @RequestMapping(value = "/bulkupload", method = RequestMethod.GET, produces = "application/zip")
    @ResponseBody
    public byte[] bulkUpload(@RequestParam String bulkInput, HttpServletResponse response) {
        List<MemoryFile> files = new ArrayList<>();
        byte[] output = new byte[0];
        try {
            JSONArray array = new JSONArray(URLDecoder.decode(bulkInput, "UTF-8"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.has("name") ? object.getString("name") : "";
                String pokemon = object.has("pokemon") ? object.getString("pokemon") : "";
                String regions = object.has("regions") ? object.getString("regions") : "";
                byte[] barray = this.pokeImage(name, pokemon, regions);
                MemoryFile mfile = new MemoryFile();
                mfile.contents = barray;
                mfile.fileName = (name.equals("") ? i : name) + ".png";
                files.add(mfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            output = createZipByteArray(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setHeader("Content-Disposition", "filename=bulk.zip");
        return output;
    }

    private static class MemoryFile {
        public String fileName;
        public byte[] contents;
    }

    private byte[] createZipByteArray(List<MemoryFile> memoryFiles) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        List<String> filenames = new ArrayList<>();
        try {
            for (MemoryFile memoryFile : memoryFiles) {
                ZipEntry zipEntry;
                if (!filenames.contains(memoryFile.fileName)) {
                    zipEntry = new ZipEntry(memoryFile.fileName);
                } else {
                    zipEntry = new ZipEntry(
                            Collections.frequency(filenames, memoryFile.fileName) + memoryFile.fileName
                    );
                }
                filenames.add(memoryFile.fileName);
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(memoryFile.contents);
                zipOutputStream.closeEntry();
            }
        } finally {
            zipOutputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }


    @RequestMapping(value = "/makepokemon", method = RequestMethod.GET, produces = "image/png")
    @ResponseBody
    public byte[] pokeImage(@RequestParam(value = "name", required = true, defaultValue = "") String name,
                            @RequestParam(value = "pokemon", required = true, defaultValue = "") String pokemon,
                            @RequestParam(value = "regions", required = true, defaultValue = "") String regions) {

        pokemon = pokemon.toLowerCase().trim();

        BufferedImage returnImage = pokePictureService.getSprite(name, pokemon, regions);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(returnImage, "png", outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }


}