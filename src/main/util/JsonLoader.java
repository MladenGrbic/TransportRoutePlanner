package main.util;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Uslužna klasa za učitavanje JSON fajlova.
 *
 * @author Mladen Grbić
 * @version 1.0
 */
public class JsonLoader {

    /**
     * Učitava JSON fajl i vraća ga kao JSONObject.
     *
     * @param filePath Putanja do JSON fajla.
     * @return JSONObject koji predstavlja sadržaj fajla.
     * @throws RuntimeException Ako dođe do greške pri učitavanju fajla.
     */
    public static JSONObject loadJson(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
            throw new RuntimeException("Greška prilikom učitavanja JSON fajla: " + filePath, e);
        }
    }
}