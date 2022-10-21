package edu.info.ip.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ImageUtil {

    public static BufferedImage loadImage(String fileName) {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return img;
    }

    public static void saveImage(BufferedImage img, String fileName, String formatName) {

        try {
            ImageIO.write(img, formatName, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayImage(BufferedImage img, String description) {
        if (img == null)
            return;
        JFrame frame = new JFrame(description);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel imagePanel = new ImagePanel();
        imagePanel.setFitToScreen(false);
        imagePanel.setImage(img);

        frame.setContentPane(new JScrollPane(imagePanel));
        frame.pack();
        frame.setVisible(true);
    }

    public static BufferedImage generateRandomPixels(int width, int height) {
        BufferedImage outImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Random rand = new Random();

        for (int y = 0; y < outImg.getHeight(); y++)
            for (int x = 0; x < outImg.getWidth(); x++) {

//                int r = rand.nextInt(256);
//                outImg.getRaster().setSample(x,y,0, r); // R
//                outImg.getRaster().setSample(x,y,1, r); // G
//                outImg.getRaster().setSample(x,y,2, r); // B

                outImg.getRaster().setSample(x, y, 0, rand.nextInt(256)); // R
                outImg.getRaster().setSample(x, y, 1, rand.nextInt(256)); // G
                outImg.getRaster().setSample(x, y, 2, rand.nextInt(256)); // B
            }

        return outImg;
    }

    public static BufferedImage extractBand(BufferedImage inImg, char band) {

        BufferedImage outImg = new BufferedImage(inImg.getWidth(), inImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < outImg.getHeight(); y++)
            for (int x = 0; x < outImg.getWidth(); x++) {
                int pixel = inImg.getRGB(x, y);

                int alpha = (pixel & 0xff000000) >> 24;
                int red = (pixel & 0x00ff0000) >> 16;
                int green = (pixel & 0x0000ff00) >> 8;
                int blue = (pixel & 0x000000ff);

                // recompose
                //pixel = 0x00000000 | (alpha << 24) | (red << 16) | (green << 8) | blue;

//                if(y == 0)
//                    System.out.println(alpha + " " + red + " " + green + " " + blue);

                switch (band) {
                    case 'R' -> outImg.getRaster().setSample(x, y, 0, red);
                    case 'G' -> outImg.getRaster().setSample(x, y, 0, green);
                    case 'B' -> outImg.getRaster().setSample(x, y, 0, blue);
                }
            }
        return outImg;
    }

    public static BufferedImage extractBand2(BufferedImage inImg, int band) {

        BufferedImage outImg = new BufferedImage(inImg.getWidth(), inImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < outImg.getHeight(); y++)
            for (int x = 0; x < outImg.getWidth(); x++) {
                int val = inImg.getRaster().getSample(x, y, band);
                outImg.getRaster().setSample(x, y, 0, val);
            }

        return outImg;
    }

    public static BufferedImage grayLevelGenerator(int firstGrayLevel, int blockSize, int grayLevelStep, int imgHeight){

        int imgWidth = ((256 - firstGrayLevel)/grayLevelStep) * blockSize;

        BufferedImage outImg = new BufferedImage(imgWidth,imgHeight,BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < outImg.getHeight(); y++) {
            int grayLevel = firstGrayLevel;
            for (int x = 0; x < outImg.getWidth(); x+=blockSize){
                for (int xi = 0; xi < blockSize; xi++) {
                    outImg.getRaster().setSample(x+xi, y, 0, grayLevel);
                }
                grayLevel += grayLevelStep;
            }
        }
        return outImg;
    }

    public static BufferedImage pixelate(BufferedImage inImg, int blockSize){
        BufferedImage outImg = null;

        if(inImg.getWidth() % blockSize != 0 && inImg.getHeight() % blockSize != 0){
            System.out.println("wrong image size");
            return outImg;
        }

        outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(),inImg.getType());

        for (int band = 0; band < inImg.getRaster().getNumBands(); band++)
        for (int y = 0; y < outImg.getHeight(); y+=blockSize) {
            for (int x = 0; x < outImg.getWidth(); x+=blockSize) {

                // parcurgem blocul
                int grayLevelSum = 0;
                for (int yi = 0; yi < blockSize; yi++)
                    for (int xi = 0; xi < blockSize; xi++)
                        grayLevelSum += inImg.getRaster().getSample(x+xi,y+yi, band);

                int avgGrayLevel = grayLevelSum / (blockSize * blockSize);

                for (int yi = 0; yi < blockSize; yi++)
                    for (int xi = 0; xi < blockSize; xi++)
                        outImg.getRaster().setSample(x+xi, y+yi, band, avgGrayLevel);
            }
        }

        return outImg;
    }

    public static BufferedImage flipV(BufferedImage inImg){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getWidth(),inImg.getType());

        for (int y = 0; y < outImg.getHeight()/2; y++)
            for (int x = 0; x < outImg.getWidth(); x++) {
//                int pixel = inImg.getRGB(x,y);
                outImg.setRGB(x,y,inImg.getRGB(x,(inImg.getHeight()-1)-y));
                outImg.setRGB(x,(inImg.getHeight()-1)-y,inImg.getRGB(x,y));
            }

        return outImg;
    }

    public static BufferedImage flipH(BufferedImage inImg){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getWidth(),inImg.getType());

        for (int y = 0; y < outImg.getHeight(); y++)
            for (int x = 0; x < outImg.getWidth()/2; x++) {
//                int pixel = inImg.getRGB(x,y);
                outImg.setRGB(x,y,inImg.getRGB((inImg.getWidth()-1)-x,y));
                outImg.setRGB((inImg.getWidth()-1)-x,y,inImg.getRGB(x,y));
            }

        return outImg;
    }

    public static BufferedImage toGray(BufferedImage input){
        BufferedImage output;
        output = new BufferedImage(input.getWidth(),input.getHeight(),BufferedImage.TYPE_BYTE_GRAY);

        for(int y=0; y<input.getHeight(); y++)
            for(int x=0; x<input.getWidth(); x++){
                int r = input.getRaster().getSample(x, y, 0);
                int g = input.getRaster().getSample(x, y, 1);
                int b = input.getRaster().getSample(x, y, 2);
                output.getRaster().setSample(x, y, 0, (r+g+b)/3);
            }
        return output;
    }

    public enum GrayTransforms {GRAY_TRANSFORMS_GREEN, GRAY_TRANSFORMS_SQRT,
        GRAY_TRANSFORMS_AVG, GRAY_TRANSFORMS_USUAL, GRAY_TRANSFORMS_PAL}

    public static int constrain(int val, int min, int max){
        return val>max ? max : (val<min ? min : val);
    }
    public static int constrain(int val){
        return constrain(val,0,255);
    }

    public static BufferedImage colorToGray(BufferedImage inImg, GrayTransforms version){
        BufferedImage outImg = new
                BufferedImage(inImg.getWidth(),inImg.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
        if(inImg.getType() == BufferedImage.TYPE_BYTE_GRAY){
            inImg.copyData(outImg.getRaster());
            return outImg;
        }
        for (int y = 0; y < inImg.getHeight(); y++)
            for (int x = 0; x < inImg.getWidth(); x++) {
                int r = inImg.getRaster().getSample(x,y,0);
                int g = inImg.getRaster().getSample(x,y,1);
                int b = inImg.getRaster().getSample(x,y,2);
                int grayLevel = 0;
                switch (version){
                    case GRAY_TRANSFORMS_GREEN -> grayLevel = g;
                    case GRAY_TRANSFORMS_SQRT -> grayLevel =
                            constrain((int)Math.round(Math.sqrt(r*r + g*g + b*b)));
                    case GRAY_TRANSFORMS_AVG -> grayLevel =
                            constrain((int)Math.round((double)(r+g+b)/3));
                    case GRAY_TRANSFORMS_USUAL -> grayLevel =
                            constrain((int)Math.round((double)(3*r+2*g+4*b)/9));
                    case GRAY_TRANSFORMS_PAL -> grayLevel =
                            constrain((int)Math.round(0.299*r+0.587*g+0.114*b));
                }
                outImg.getRaster().setSample(x,y,0,grayLevel);
            }
        return outImg;
    }

    public static BufferedImage brightnessV1(BufferedImage inImg, int offset){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(), inImg.getType());

        for (int band = 0; band < inImg.getRaster().getNumBands(); band++)
            for (int y = 0; y < inImg.getHeight(); y++)
                for (int x = 0; x < inImg.getWidth(); x++) {
                    int inGrayLevel = inImg.getRaster().getSample(x,y,band);
                    int outGrayLevel = constrain(inGrayLevel + offset);
                    outImg.getRaster().setSample(x,y,band,outGrayLevel);
                }

        return outImg;
    }

    public static BufferedImage brightnessV2(BufferedImage inImg, int offset){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(), inImg.getType());

        short[] brightnessLUT = new short[256];

        for (int i = 0; i < 256; i++)
            brightnessLUT[i] = (short)constrain(i + offset);

        for (int band = 0; band < inImg.getRaster().getNumBands(); band++)
            for (int y = 0; y < inImg.getHeight(); y++)
                for (int x = 0; x < inImg.getWidth(); x++) {
                    int inGrayLevel = inImg.getRaster().getSample(x,y,band);
                    outImg.getRaster().setSample(x,y,band,brightnessLUT[inGrayLevel]);
                }

        return outImg;
    }

    public static BufferedImage brightnessV3(BufferedImage inImg, int offset){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(), inImg.getType());

        short[] brightnessLUT = new short[256];

        for (int i = 0; i < 256; i++) {
            brightnessLUT[i] = (short) constrain(i + offset);
            System.out.println(i + " " + brightnessLUT[i]);
        }

        ShortLookupTable shortLookupTable = new ShortLookupTable(0, brightnessLUT);
        LookupOp lookupOp = new LookupOp(shortLookupTable, null);
        lookupOp.filter(inImg,outImg);

        return outImg;
    }

    public static BufferedImage brightnessRGB(BufferedImage inImg, int offsetR, int offsetG, int offsetB){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(), inImg.getType());

        short[] rLUT = new short[256];
        short[] gLUT = new short[256];
        short[] bLUT = new short[256];

        short[][] rgbLUT = {rLUT, gLUT, bLUT};

        for (int i = 0; i < 256; i++) {
            rLUT[i] = (short) constrain(i + offsetR);
            gLUT[i] = (short) constrain(i + offsetG);
            bLUT[i] = (short) constrain(i + offsetB);
        }

        ShortLookupTable shortLookupTable = new ShortLookupTable(0,rgbLUT);
        LookupOp lookupOp = new LookupOp(shortLookupTable, null);
        lookupOp.filter(inImg,outImg);

        return outImg;
    }

    public static BufferedImage contrast(BufferedImage inImg, float scale){
        BufferedImage outImg = new BufferedImage(inImg.getWidth(),inImg.getHeight(), inImg.getType());

        short[] contrastLUT = new short[256];

        for (int i = 0; i < 256; i++) {
            contrastLUT[i] = (short) constrain(Math.round(scale * i));
            System.out.println(i + " " + contrastLUT[i]);
        }

        ShortLookupTable shortLookupTable = new ShortLookupTable(0, contrastLUT);
        LookupOp lookupOp = new LookupOp(shortLookupTable, null);
        lookupOp.filter(inImg,outImg);

        return outImg;
    }
}
