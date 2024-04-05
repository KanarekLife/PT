package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar <jar-file> <input-dir> <output-dir>");
            System.exit(1);
        }

        try (var stream = Files.list(Path.of(args[0]))) {
            Files.createDirectory(Path.of(args[1]));

            stream
                    .parallel()
                    .map(Main::LoadImage)
                    .filter(Objects::nonNull)
                    .map(img -> new Image(img.name(), ConvertToGrayScale(img.data())))
                    .forEach(img -> SaveImage(img, Path.of(args[1])));

        } catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
        }
    }

    private static Image LoadImage(Path path) {
        try {
            return new Image(path.getFileName().toString(), ImageIO.read(path.toFile()));
        } catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
            return null;
        }
    }

    private static BufferedImage ConvertToGrayScale(BufferedImage img) {
        var grayImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                var color = new Color(img.getRGB(x, y));
                var grayColor = (int) (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
                grayImg.setRGB(x, y, new Color(grayColor, grayColor, grayColor).getRGB());
            }
        }
        return grayImg;
    }

    private static void SaveImage(Image img, Path outputDir) {
        try {
            ImageIO.write(img.data(), "png", outputDir.resolve(img.name()).toFile());
            System.out.println("Image " + img.name() + " saved successfully.");
        } catch (IOException ex) {
            System.err.println("An error occurred: " + ex.getMessage());
        }
    }
}