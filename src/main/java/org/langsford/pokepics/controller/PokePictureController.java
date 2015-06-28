package org.langsford.pokepics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.langsford.pokepics.enumeration.Region;
import org.langsford.pokepics.service.PokePictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by trevyn on 1/11/15.
 */
@Controller
@RequestMapping("/")
public class PokePictureController {

    @Autowired
    private ObjectMapper objectMapper;
    private PokePictureService pokePictureService;
    private Map<String, Integer> nameIdMap;



    @Autowired
    public PokePictureController(PokePictureService pokePictureService) {
        this.pokePictureService = pokePictureService;
        Map<Integer, String> idNameMap = pokePictureService.getPokemonIdNames();
        this.nameIdMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : idNameMap.entrySet()) {
            this.nameIdMap.put(entry.getValue(), entry.getKey());
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView pokePage() {
        ModelAndView mav = new ModelAndView("poke");
        mav.addObject("pokemonList", pokePictureService.getAvailablePokemon());
        return mav;
    }

    @RequestMapping(value = "/fileupload", method = RequestMethod.POST, produces = "application/zip")
    public
    @ResponseBody
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
    public byte[] bulkUpload(@RequestParam String bulkInput, HttpServletResponse response)
            throws IOException {
        byte[] output;
        List<MemoryFile> files = new ArrayList<>();
        List inputs = objectMapper.readValue(bulkInput, List.class);
        int cnt = 0;
        for (Object input : inputs) {
            Map map = (Map) input;
            String text = map.get("text") != null ? (String) map.get("text") : "";
            String pokemon = map.get("pokemon") != null ? (String) map.get("pokemon") : "";
            Integer nationalId = nameIdMap.get(pokemon.toLowerCase());
            String regionsString = map.get("regions") != null ? (String) map.get("regions") : "";
            List<String> regionStrings = Arrays.asList(regionsString.split(","))
                    .stream().map(String::toUpperCase).collect(Collectors.toList());
            List<Region> regions = new ArrayList<>();
            for (String s : regionStrings) {
                try {
                    regions.add(Region.valueOf(s));
                } catch(IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            byte[] barray = pokeImage(text, nationalId, regions.toArray(new Region[regions.size()]));

            MemoryFile mfile = new MemoryFile();
            mfile.contents = barray;
            mfile.fileName = (text.equals("") ? cnt++ : text) + ".png";
            files.add(mfile);
        }
        output = createZipByteArray(files);
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
                    zipEntry = new ZipEntry(Collections.frequency(filenames, memoryFile.fileName)
                            + "-" +  memoryFile.fileName);
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
    public byte[] pokeImage(@RequestParam(value = "text", required = true, defaultValue = "") String text,
                            @RequestParam(value = "nationalId", required = false) Integer nationalId,
                            @RequestParam(value = "regions", required = false) Region[] regions) {
        BufferedImage returnImage;

        if (nationalId != null) {
            returnImage = pokePictureService.makeImageForPokemon(nationalId, text);
        } else {
            returnImage = pokePictureService.makeRandomImage(text, regions);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(returnImage, "png", outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex) {
        return ex.getMessage();
    }

}