package org.ui.utils;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {

    private static Map<String, BufferedImage> cache = new HashMap<>();

    /**
     * Load an image for a chess piece and scale it to desired size.
     *
     * @param pieceType The piece type name, e.g., "Pawn", "Bishop"
     * @param ownerId   The player ID, e.g., "1" or "2"
     * @param size      Desired width and height in pixels
     * @return Scaled BufferedImage
     */
    public static BufferedImage getPieceImage(String pieceType, int ownerId, int size) {
        String key = pieceType + "_" + ownerId + "_" + size;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        try {
            String fileName = pieceType.toLowerCase() + "_" + (ownerId == 1 ? "white" : "black") + ".svg";
            InputStream stream = ImageLoader.class.getClassLoader().getResourceAsStream("simple_assets/" + fileName);
            if (stream == null)
                return null;

            BufferedImage original = SvgToBufferedImageConverter.convert(stream, size, size);

            cache.put(key, original);
            return original;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
