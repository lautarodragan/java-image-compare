package com.github.lautarodragan;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.awt.Rectangle;

public class Main {

	private static LinkedList<Rectangle> rectangles = new LinkedList<Rectangle>();

	private static final int distanceThreshold = 15;
	private static final double colorThreshold = 0.3;

	public static void main(String[] args) {
		if (args.length < 3) {
			printUsage();
			return;
		}

		String input1 = args[0];
		String input2 = args[1];
		String output = args[2];

		System.out.println("Comparing images " + input1 + " and " + input2);
		System.out.println("Result to be written in " + output);

		BufferedImage imgInput1 = loadImage(input1);
		BufferedImage imgInput2 = loadImage(input2);
		BufferedImage imgOutput = loadImage(input1); // output = input1 + red rectangles
		BufferedImage differenceMap = loadImage(input1); // horror, should create blank image instead, but this is easier

		Color black = new Color(0, 0, 0);
		Color white = new Color(255, 255, 255);

		System.out.println("Finding differences...");

		for(int x = 0; x < Math.min(imgInput1.getWidth(), imgInput2.getWidth()); x++) {
			System.out.println((double)x / differenceMap.getWidth() * 100 + "%");
			for(int y = 0; y < Math.min(imgInput1.getHeight(), imgInput2.getHeight()); y++) {
				Color color1 = new Color(imgInput1.getRGB(x, y));
				Color color2 = new Color(imgInput2.getRGB(x, y));

				int differenceRed = Math.abs(color1.getRed() - color2.getRed());
				int differenceGreen = Math.abs(color1.getGreen() - color2.getGreen());
				int differenceBlue = Math.abs(color1.getBlue() - color2.getBlue());
				int difference = differenceRed + differenceGreen + differenceBlue;

				double relativeDifference = (double)difference / (256 * 3);

				if (relativeDifference > colorThreshold) {
					differenceMap.setRGB(x, y, black.getRGB());
					addToRectangles(x, y);
				} else {
					differenceMap.setRGB(x, y, white.getRGB());
				}
			}
		}

		drawRectangles(imgOutput, rectangles);

		saveImage(imgOutput, output);
		saveImage(differenceMap, "map." + output);

	}

	public static void saveImage(BufferedImage bufferedImage, String fileName) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName));
			ImageIO.write(bufferedImage, "png", bos);
			bos.close();
		} catch (IOException e) {
			System.err.println("ERROR: could not write to " + fileName);
		}
	}

	public static BufferedImage loadImage(String input) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(new File(input));
			return bufferedImage;
		} catch (IOException e) {
			System.err.println("ERROR: could not load " + input);
		}
		return null;

	}

	public static void addToRectangles(int x, int y) {
		// try to find an existing rectangle
		Rectangle rectangle = findRectangleNearby(x, y);

		// if none found, create a new one
		if (rectangle == null) {
			rectangles.add(new Rectangle(x, y, 1, 1));
		} else {
			// if we did find a rectangle close by, enlarge it in order for it to to contain the given (x, y)
			if (x > rectangle.x + rectangle.width) {
				rectangle.width += x - rectangle.x - rectangle.width + 1;
			} else if (x < rectangle.x) {
				rectangle.width += Math.abs(rectangle.x - x) + 1;
				rectangle.x = x;
			}
			if (y > rectangle.y + rectangle.height) {
				rectangle.height += y  - rectangle.y - rectangle.height + 1;
			} else if (y < rectangle.y) {
				rectangle.height += Math.abs(rectangle.y - y) + 1;
				rectangle.y = y;
			}
		}
	}

	public static Rectangle findRectangleNearby(int x, int y) {
		for (Rectangle r: rectangles) {
			if (x > r.x - distanceThreshold && y > r.y - distanceThreshold && x < r.x + r.width + distanceThreshold && y < r.y + r.height + distanceThreshold)
				return r;
		}
		return null;
	}

	public static void printUsage() {
		System.out.println("Image Comparer");
		System.out.println("Usage: compare image1.png image2.jpg output.png");
	}

	public static void drawRectangles(Graphics2D graph, LinkedList<Rectangle> rectangles) {
		for (Rectangle r : rectangles) {
			graph.setColor(Color.RED);
			graph.drawRect(r.x, r.y, r.width, r.height);
		}
	}

	public static void drawRectangles(BufferedImage bufferedImage, LinkedList<Rectangle> rectangles) {
		Graphics2D graph = bufferedImage.createGraphics();
		drawRectangles(graph, rectangles);
		graph.dispose();
	}

}
