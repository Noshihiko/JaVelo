package ch.epfl.javelo.gui;

import java.nio.file.Path;

import static java.lang.Math.pow;

public final class TileManager {
    public final static int OFFSET_TILES_ZOOM_LEVEL = 4;

    public TileManager(Path path, String serverName) {

    }

    public Image imageForTileAt(TileId tileId) {
        path tileId

    }





        public record TileId(int zoomLevel, int x, int y) {

            public static boolean isValid(int zoomLevel, int x, int y) {
                return (x>=0 && x<= pow(zoomLevel, OFFSET_TILES_ZOOM_LEVEL) && y>=0 && y<= pow(zoomLevel, OFFSET_TILES_ZOOM_LEVEL));
            }
        }
}
