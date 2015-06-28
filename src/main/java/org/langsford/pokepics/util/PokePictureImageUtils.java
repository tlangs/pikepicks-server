package org.langsford.pokepics.util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by trevyn on 6/28/15.
 */
public class PokePictureImageUtils {

    public static BufferedImage cropOutWhitespace(BufferedImage image) {
        int firstCol = firstColumnOfImage(image);
        int firstRow = firstRowOfImage(image);
        int lastCol = lastColumnOfImage(image);
        int lastRow = lastRowOfImage(image);

        return image.getSubimage(firstCol, firstRow, lastCol - firstCol, lastRow - firstRow);
    }

    public static int getBottomOfSprite(BufferedImage sprite) {
        int lineStart;
        for (lineStart = 0; lineStart < sprite.getHeight(); lineStart++) {
            boolean stop = false;
            for (int column = 0; column < sprite.getWidth(); column++) {
                if (sprite.getRGB(column, lineStart) != 0) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }
        int lineEnd;
        for (lineEnd = lineStart; lineEnd < sprite.getHeight(); lineEnd++) {
            java.util.List<Integer> rgbs = new ArrayList<>();
            for (int column = 0; column < sprite.getWidth(); column++) {
                rgbs.add(sprite.getRGB(column, lineEnd));
            }
            int min = Collections.min(rgbs);
            if (min == 0) {
                break;
            }
        }
        return lineEnd;
    }

    private static int firstRowOfImage(BufferedImage image) {
        int lineStart;
        for (lineStart = 0; lineStart < image.getHeight(); lineStart++) {
            boolean stop = false;
            for (int column = 0; column < image.getWidth(); column++) {
                if (image.getRGB(column, lineStart) != -1) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }
        return lineStart;
    }

    private static int lastRowOfImage(BufferedImage image) {
        int lineEnd;
        for (lineEnd = image.getHeight() - 1; lineEnd > 0; lineEnd--) {
            boolean stop = false;
            for (int column = 0; column < image.getWidth(); column++) {
                if (image.getRGB(column, lineEnd) != -1) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }
        return lineEnd + 1;
    }

    private static int firstColumnOfImage(BufferedImage image) {
        int columnStart;
        for (columnStart = 0; columnStart < image.getWidth(); columnStart++) {
            boolean stop = false;
            for (int row = 0; row < image.getHeight(); row++) {
                if (image.getRGB(columnStart, row) != -1) {
                    stop = true;
                    break;
                }
            }
            if (stop) break;
        }
        return columnStart;
    }

    private static int lastColumnOfImage(BufferedImage image) {
        int columnEnd;
        for (columnEnd = image.getWidth() - 1; columnEnd > 0; columnEnd--) {
            boolean stop = false;
            for (int row = 0; row < image.getHeight(); row++) {
                if (image.getRGB(columnEnd, row) != -1) {
                    stop = true;
                    break;
                }
            }
            if (stop) break;
        }
        return columnEnd + 1;
    }

    private static int getBottomOfText(BufferedImage image) {
        int lineStart;
        for (lineStart = 0; lineStart < image.getHeight(); lineStart++) {
            boolean stop = false;
            for (int column = 0; column < image.getWidth(); column++) {
                if (image.getRGB(column, lineStart) != -1) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }
        int lineEnd;
        for (lineEnd = lineStart; lineEnd < image.getHeight(); lineEnd++) {
            java.util.List<Integer> rgbs = new ArrayList<>();
            for (int column = 0; column < image.getWidth(); column++) {
                rgbs.add(image.getRGB(column, lineEnd));
            }
            int min = Collections.min(rgbs);
            if (min == -1) {
                break;
            }
        }
        for (lineStart = lineEnd + 1; lineStart < image.getHeight(); lineStart++) {
            boolean stop = false;
            for (int column = 0; column < image.getWidth(); column++) {
                if (image.getRGB(column, lineStart) != -1) {
                    stop = true;
                    break;
                }
            }
            if (stop) {
                break;
            }
        }
        for (lineEnd = lineStart; lineEnd < image.getHeight(); lineEnd++) {
            java.util.List<Integer> rgbs = new ArrayList<>();
            for (int column = 0; column < image.getWidth(); column++) {
                rgbs.add(image.getRGB(column, lineEnd));
            }
            int min = Collections.min(rgbs);
            if (min == -1) {
                break;
            }
        }

        return lineEnd;
    }
}
