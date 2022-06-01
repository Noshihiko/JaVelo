package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

/**
 * Gestionnaire de tuiles OSM: obtient les tuiles depuis un serveur et les stocke dans un cache memoire
 * et un cache disque
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */

public final class TileManager {
    private final Path path;
    private final String serverName;

    private final static int MAXIMUM_CAPACITY = 100;
    private final static float LOAD_FACTOR = (float) 0.75;
    private final static boolean ACCESS_ORDER = true;
    private final static String USER_NAME_REQUESTING = "User-Agent";
    private final static String PROJECT_NAME = "JaVelo";

    LinkedHashMap<TileId, Image> memoryCache = new LinkedHashMap<>(MAXIMUM_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);

    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
    }

    /**
     * prend en argument l'identité d'une tuile et retourne son image
     *
     * @param tileId l'identité de la tuile
     * @return l'image correspondante
     * @throws IOException si une erreure survient lors de la creation des input streams
     */

    public Image imageForTileAt(TileId tileId) throws IOException {
        //** si l'image se trouve dans le cache memoire alors il la retourne directement **
        if(memoryCache.containsKey(tileId)) {
            return memoryCache.get(tileId);
        }
        else {
        //** si la taille du cache-memoire est depassé on enlève l'element le moins utilisé **
            while (memoryCache.size() >= MAXIMUM_CAPACITY) {
                memoryCache.remove(memoryCache.keySet().iterator().next());
            }
        }
        //** ecriture du chemin d'accès pour acceder a l'image de la tuile voulue **
        Path access = path.resolve(String.valueOf(tileId.zoomLevel)).resolve(String.valueOf(tileId.x)).resolve((tileId.y)+".png");

        if (Files.exists(access)) {
        //** si l'image est dans l'hard disk alors on la recupère **
            try (FileInputStream inputStream = new FileInputStream(access.toFile())) {
                return creationImage(tileId, inputStream);
            }
        }
        //** si l'image n'est pas dans l'hard disk on va chercher sur le web **
        else {
            URL url = new URL(
                    "https://" +serverName + "/" +tileId.zoomLevel + "/" + tileId.x + "/" +tileId.y + ".png");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty(USER_NAME_REQUESTING, PROJECT_NAME);
            Files.createDirectories(access.getParent());
            try (InputStream inputStream = urlConnection.getInputStream()) {
                FileOutputStream outputStream = new FileOutputStream(access.toFile());
                inputStream.transferTo(outputStream);
            }
            try(FileInputStream inputStream = new FileInputStream(access.toFile())){
                return creationImage(tileId, inputStream);
            }
        }
    }

    /**
     * Enregistrement representant l'identité d'une tuile OSM
     *
     * @param zoomLevel niveau de zoom de la tuile
     * @param x l'index X de la tuile
     * @param y l'index Y de la tuile
     */

    public record TileId(int zoomLevel, int x, int y) {

        public static boolean isValid(int zoomLevel, int x, int y) {
            return (x>= 0 && y>= 0 && zoomLevel>=0);
        }
    }

    /**
     * Methode creant une nouvelle tuile OSM et l'enregistrant dans la memoire cache
     *
     * @param tileId identité de la tuile OSM
     * @param inputStream flot d'entrée
     * @return la tuile correspondant à l'identité de la tuile passée en paramètre
     */

    private Image creationImage(TileId tileId, InputStream inputStream) {
        Image image = new Image(inputStream);
        memoryCache.put(tileId, image);
        return image;
    }
}
