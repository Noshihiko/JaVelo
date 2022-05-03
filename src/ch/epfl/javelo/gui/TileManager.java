package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import static java.lang.Math.pow;

public final class TileManager {
    public final static int OFFSET_TILES_ZOOM_LEVEL = 4;
    public final static int INITIAL_CAPACITY = 100;
    public final static float LOAD_FACTOR = (float) 0.75;
    public final static boolean ACCESS_ORDER = true;
    public final int TILE_SIZE = 256;
    private final Path path;

    LinkedHashMap<TileId, Image> cacheMemoire = new LinkedHashMap<TileId, Image>(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);

    public TileManager(Path path, String serverName) {
        this.path = path;
    }

    public Image imageForTileAt(TileId tileId) throws IOException {
        //si l'image se trouve dans le cache memoire alors il la retourne directement
        if(cacheMemoire.containsKey(tileId)) {
            return cacheMemoire.get(tileId);
        }
        else {
        //Si la taille du cache-memoire est depassé on enlève l'element le moins utilisé
             while (cacheMemoire.size() >= 100) {
                    cacheMemoire.remove(cacheMemoire.keySet().iterator().next());
                }
            }
            //ecriture du chemin d'accès pour acceder a l'image de la tuile voulue
            Path access = path.resolve(String.valueOf(tileId.zoomLevel)).resolve(String.valueOf(tileId.x)).resolve((tileId.y)+".png");

            if (Files.exists(access)) {
                //si l'image est dans l'hard disk alors on la recupère
                try (FileInputStream i = new FileInputStream(access.toFile())) {
                    Image image = new Image(i);
                    cacheMemoire.put(tileId, image);
                    return image;
                }
            }
            //si l'image n'est pas dans l'hard disk on va chercher sur le web
            else {
                URL u = new URL(
                        "https://tile.openstreetmap.org/"+tileId.zoomLevel + "/" + tileId.x +"/" +tileId.y +".png");
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");
                Files.createDirectories(access.getParent());
                try (InputStream i = c.getInputStream()) {
                    FileOutputStream a = new FileOutputStream(access.toFile());
                    i.transferTo(a);
                    Image image = new Image(i);
                    cacheMemoire.put(tileId, image);
                    return image;
                }
                //catch ?
            }
        }

        public record TileId(int zoomLevel, int x, int y) {

            public static boolean isValid(int zoomLevel, int x, int y) {
                return (x>=0 && x<= pow(zoomLevel, OFFSET_TILES_ZOOM_LEVEL) && y>=0 && y<= pow(zoomLevel, OFFSET_TILES_ZOOM_LEVEL));
            }
        }
}
