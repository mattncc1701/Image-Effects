import com.jhlabs.image.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;

class imProcess {

	public static void printUsage() {
		System.out.println("Usage: java imProcess -input <filename> [options]");
		System.out.println("-output <filename>");
		System.out.println("-brightness <float>");
		System.out.println("-edgedetect");
		System.out.println("-blur <float>");

		System.out.println("-contrast <float>");
		System.out.println("-saturation <float>");
		System.out.println("-sharpen <float>");
		System.out.println("-randomdither");
		System.out.println("-ordereddither");
		System.out.println("-mosaic <imagefolder>");
		System.exit(1);
	}

	public static void main(String[] args) {
		int i = 0;
		BufferedImage src = null, dst = null;
		BufferedImage tmp = null;	// used for swapping src and dst buffer
		int width, height;			// image width, height

		String arg;
		String outputfilename = "output.png";		// default output filename
		
		if (args.length < 2) {
			printUsage();
		}
		
		// parse command line options, and call approrpiate member functions
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];

			if (arg.equals("-input")) {

				String inputfile = args[i++];
				try {
					src = ImageIO.read(new File(inputfile));
				} catch (IOException e) {
				}
				width = src.getWidth();
				height = src.getHeight();
				dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				continue;

			} else if (arg.equals("-output")) {

				outputfilename = args[i++];
				System.out.println("Output file: " + outputfilename);
				continue;

			} else if (arg.equals("-brightness")) {

				float brightness = Float.parseFloat(args[i++]);
				System.out.println("Set brightness: " + brightness);
				Brighten(src, dst, brightness);

			} else if (arg.equals("-contrast")) {

				float contrast = Float.parseFloat(args[i++]);
				System.out.println("Set contrast: " + contrast);

				AdjustContrast(src, dst, contrast);

			} else if (arg.equals("-saturation")) {

				float saturation = Float.parseFloat(args[i++]);
				System.out.println("Set saturation: " + saturation);

				AdjustSaturation(src, dst, saturation);

			} else if (arg.equals("-randomdither")) {

				System.out.println("Generated random dithering");

				RandomDither(src, dst);

			} else if (arg.equals("-ordereddither")) {

				System.out.println("Generate ordered dithering");

				OrderedDither(src, dst);
			} else if (arg.equals("-blur")) {

				float radius = Float.parseFloat(args[i++]);
				System.out.println("Set blur radius: " + radius);
				
				Blur(src, dst, radius);

			} else if (arg.equals("-sharpen")) {

				float sharpness = Float.parseFloat(args[i++]);
				System.out.println("Set sharpness : " + sharpness);

				Sharpen(src, dst, sharpness);

			} else if (arg.equals("-edgedetect")) {

				System.out.println("Apply edge detector");

				EdgeDetect(src, dst);

			} else if (arg.equals("-mosaic")) {

				String mosaicfolder = args[i++];
				System.out.println("Base image folder: " + mosaicfolder);

				Mosaic(src, dst, mosaicfolder);

			} else {
				printUsage();
			}
			// swap src and dst to prepare for the next operation
			tmp = src; src = dst; dst = tmp;
		}
		if (i != args.length) {
			System.out.println("there are unused arguments");
		}
		// write the output image to disk file
		File outfile = new File(outputfilename);
		try {
			ImageIO.write(src, "png", outfile);
		} catch(IOException e) {
		}
	}

	// Change the brightness of an image
	// brightness is a scaling factor 
	// Use this function as an example. There is nothing you need to change here
	public static void Brighten(BufferedImage src, BufferedImage dst, float brightness) {

		int width = src.getWidth();
		int height = src.getHeight();

		// a buffer that stores the destination image pixels
		int[] pixels = new int[width * height];
	
		// get the pixels of the source image	
		src.getRGB(0, 0, width, height, pixels, 0, width);

		int i;
		int a, r, g, b;
		for(i = 0; i < width * height; i ++) {
			Color rgb = new Color(pixels[i]);
			a = rgb.getAlpha();
			r = rgb.getRed();
			g = rgb.getGreen();
			b = rgb.getBlue();
			r = PixelUtils.clamp((int)((float)r * brightness));//increase the red green and blue values of all pixels in our picture to brighten it
			g = PixelUtils.clamp((int)((float)g * brightness));
			b = PixelUtils.clamp((int)((float)b * brightness));

			pixels[i] = new Color(r, g, b, a).getRGB();
		}

		// write pixel values to the destination image
		dst.setRGB(0, 0, width, height, pixels, 0, width);

	}

	// change the contrast of an image
	// contrast = 0 gives a medium gray (0.5, 0.5, 0.5) image
	// constrat = 1 gives the original image
	public static void AdjustContrast(BufferedImage src, BufferedImage dst, float contrast) {
		int width = src.getWidth();
		int height = src.getHeight();

		// a buffer that stores the destination image pixels
		int[] pixels = new int[width * height];
	
		// get the pixels of the source image	
		src.getRGB(0, 0, width, height, pixels, 0, width);

		int i;
		int a, r, g, b;
		for(i = 0; i < width * height; i ++) {
			Color rgb = new Color(pixels[i]); 
			
			a = rgb.getAlpha();
			r = rgb.getRed();
			g = rgb.getGreen();
			b = rgb.getBlue();
			r = PixelUtils.clamp((int)((float)(r *contrast) + .5*(255-(contrast*255))));//Use the equation for contrast to adjust pictures contrast
			g = PixelUtils.clamp((int)((float)(g *contrast) + .5*(255-(contrast*255))));
			b = PixelUtils.clamp((int)((float)(b *contrast) + .5*(255-(contrast*255))));
			
			pixels[i] = new Color(r, g, b, a).getRGB();
		}

		// write pixel values to the destination image
		dst.setRGB(0, 0, width, height, pixels, 0, width);
	}

	// change the saturation of an image
	// saturation = 0 gives a gray scale version of the image
	// saturation = 1 gives the original image
	public static void AdjustSaturation(BufferedImage src, BufferedImage dst, float saturation) {
		int width = src.getWidth();
		int height = src.getHeight();

		// a buffer that stores the destination image pixels
		int[] pixels = new int[width * height];
	
		// get the pixels of the source image	
		src.getRGB(0, 0, width, height, pixels, 0, width);

		int i;
		int a, r, g, b;
		float L = 0;
		for(i = 0; i < width * height; i ++) {
			Color rgb = new Color(pixels[i]); 
			
			a = rgb.getAlpha();
			r = rgb.getRed();
			g = rgb.getGreen();
			b = rgb.getBlue();
			
			L = (float) (.3*r +.59*g +.11*b);
			r = PixelUtils.clamp((int)((float)r *saturation + L*(1-saturation)));//use the equation for saturation to adjust the pictures saturation
			g = PixelUtils.clamp((int)((float)g *saturation + L*(1-saturation)));
			b = PixelUtils.clamp((int)((float)b *saturation + L*(1-saturation)));
			
			pixels[i] = new Color(r, g, b, a).getRGB();
		}

		// write pixel values to the destination image
		dst.setRGB(0, 0, width, height, pixels, 0, width);
	}

	// blur an image
	// use the GaussianFilter from jhlabs to perform Gaussian blur
	// This function is a given, and there is nothing you need to change here
	public static void Blur(BufferedImage src, BufferedImage dst, float radius) {

		GaussianFilter filter = new GaussianFilter();
		filter.setRadius(radius);
		filter.filter(src, dst);
	}

	// sharpen an image
	// sharpness sets the amount of sharpening
	// use the ConvolveFilter from jhlabs and the sharpening matrix we covered in class to perform this operation
	public static void Sharpen(BufferedImage src, BufferedImage dst, float sharpness) {
		float[] kernelArray = {0, -sharpness, 0, -sharpness, 1+ 4*(sharpness), -sharpness, 0 , -sharpness, 0};//sharpen kernel
		ConvolveFilter filter = new ConvolveFilter(kernelArray);//sharpen the image using the kernel and applying the filter
		filter.filter(src, dst);
	}

	// detect edge features of an image
	// use the EdgeFilter from jhlabs
	// This function is a given, and there is nothing you need to change here
	public static void EdgeDetect(BufferedImage src, BufferedImage dst) {

		EdgeFilter filter = new EdgeFilter();
		filter.filter(src, dst);
	}

	// random dithering
	// compare each image pixel against a random threshold to quantize it to 0 or 1
	// ignore the color, and just use the luminance of a pixel to do the dithering
	// your output should be a binary (black-white) image
	public static void RandomDither(BufferedImage src, BufferedImage dst) {
		Random newRandom = new Random();
		float randomNumber = newRandom.nextFloat();
		
		int width = src.getWidth();
		int height = src.getHeight();

		// a buffer that stores the destination image pixels
		int[] pixels = new int[width * height];
	
		// get the pixels of the source image	
		src.getRGB(0, 0, width, height, pixels, 0, width);
		int a, r, g, b;
		float L = 0;
		for(int i = 0; i < width * height; i ++) {
			randomNumber = newRandom.nextFloat();
			Color rgb = new Color(pixels[i]); 
			a = rgb.getAlpha();
			r = rgb.getRed();
			g = rgb.getGreen();
			b = rgb.getBlue();
			L = (float) (.3*r +.59*g +.11*b);//convert image to gray scale
			if(L/255 > randomNumber){
				r = PixelUtils.clamp((int)((float)255));//if pixel gray scale value is greater than our random number make it white
				g = PixelUtils.clamp((int)((float)255));
				b = PixelUtils.clamp((int)((float)255));
			}
			else{
				r = PixelUtils.clamp((int)((float)0));//if pixel gray scale value is greater than our random number make it black
				g = PixelUtils.clamp((int)((float)0));
				b = PixelUtils.clamp((int)((float)0));	
			}
			pixels[i] = new Color(r, g, b, a).getRGB();
		}

		// write pixel values to the destination image
		dst.setRGB(0, 0, width, height, pixels, 0, width);

	}

	// ordered dithering
	// compare each image pixel against a pseudo-random threshold to quantize it to 0 or 1
	// in this case, the pseudo random number is given by a 4x4 Bayers matrix
	// ignore the color, and just use the luminance of a pixel to do the dithering
	// your output should be a binary (black-white) image
	public static void OrderedDither(BufferedImage src, BufferedImage dst) {
		final float[][] Bayers = {{15/16.f,  7/16.f,  13/16.f,   5/16.f},
								  {3/16.f,  11/16.f,   1/16.f,   9/16.f},
								  {12/16.f,  4/16.f,  14/16.f,   6/16.f},
								  { 0,      8/16.f,    2/16.f,  10/16.f} };
		int width = src.getWidth();//1 =0, 
		int height = src.getHeight();

		// a buffer that stores the destination image pixels
		int[] pixels = new int[width * height];
	
		// get the pixels of the source image	
		src.getRGB(0, 0, width, height, pixels, 0, width);
		int a, r, g, b;
		float e = 0;
		float L = 0;
		int x = 0, y = 0;

		for(int i = 0; i < width * height; i ++) {
			x = i%width;//find current x and y position based off of our one dimensional array pixels
			y = i/width;			
			Color rgb = new Color(pixels[i]); 
			a = rgb.getAlpha();
			r = rgb.getRed();
			g = rgb.getGreen();
			b = rgb.getBlue();
			e = Bayers[x%4][y%4];//current index in our array
			L = (float) (.3*r + .59*g +.11*b);//convert to gray scale
			if((float)L/255 > e){
				r = PixelUtils.clamp(255);//if pixel gray scale value is greater than our array number make it white
				b = PixelUtils.clamp(255);
				g = PixelUtils.clamp(255);
			}
			else{
				r = PixelUtils.clamp(0);//if pixel gray scale value is greater than our array number make it black
				b = PixelUtils.clamp(0);
				g = PixelUtils.clamp(0);	
			}

			pixels[i] = new Color(r, g, b, a).getRGB();
		}
		// write pixel values to the destination image
		dst.setRGB(0, 0, width, height, pixels, 0, width);
	}

	// generate image Mosaics
	// mosaicfolder specifies a subfolder containing a collection of images
	// to be used for producing Mosaic
	public static void Mosaic(BufferedImage src, BufferedImage dst, String mosaicfolder) {

		int width = src.getWidth();
		int height = src.getHeight();

		// load all mosaic images from the specified subfolder
		File folder = new File(mosaicfolder);
		File files[] = folder.listFiles();

		int i;
		int w = 0, h = 0;
		int num = files.length;

		// mpixels stores the pixels of each mosaic image read from a disk file
		int[][] mpixels = new int[num][];

		for (i = 0; i < files.length; i ++) {
			if (!files[i].isFile()) continue;
			BufferedImage mosaic = null;
			try {
				mosaic = ImageIO.read(files[i]);
			} catch (IOException e) {
			}
			if (w == 0) {
				w = mosaic.getWidth();
				h = mosaic.getHeight();
			} else {
				if (mosaic.getWidth() != w || mosaic.getHeight() != h) {
					System.out.println("mosaic images must be of the same size.");
					System.exit(1);
				}
			}
			mpixels[i] = new int[w*h];

			// get pixels from the buffered image
			mosaic.getRGB(0, 0, w, h, mpixels[i], 0, w);
		}
		System.out.println("" + num + " mosaic images (" + w + "," + h + ") loaded.");
		
		int[] pixels = new int[width * height];
		int[] pixelsFinal = new int[width * height];
		src.getRGB(0, 0, width, height, pixels, 0, width);

		int a, r, g, b;
		
		float closetValue = 0;//final value after using the equation to find the difference between images 
		//these are the different parts of the of my equation to find the closet mosaic image value
		float currentrd = 0;//denominator 
		float currentgd = 0;//denominator
		float currentbd = 0;//denominator
		float currentrn = 0;//numerator
		float currentgn = 0;//numerator
		float currentbn = 0;//numerator
		float alpharn = 0;//numerator 
		float alphagn = 0;//numerator
		float alphabn = 0;//numerator
		float alphard = 0;//denominator
		float alphagd = 0;//denominator
		float alphabd = 0;//denominator
		float finalAlphar = 0;
		float finalAlphag = 0;
		float finalAlphab = 0;
		int closetPictureIndex = 0;	//index into array of mosaic images 
		
		int rm, gm, bm;//red green and blue value of mosaic image 
		Random newRandom = new Random();//random number to add diversity to images

		int x = 0, y = 0;
		while(x < width || y < height){//go through every pixel in sample image
			closetValue  = 0;
			for(int u = 0; u < mpixels.length; u++){//go through every picture in mosaic folder
				currentrn = 0;
				currentgn = 0;
				currentbn = 0;
				currentrd = 0;
				currentgd = 0;
				currentbd = 0;
				alpharn = 0;
				alphagn = 0;
				alphabn = 0;
				alphard = 0;
				alphagd = 0;
				alphabd = 0;				
				for(int k = 0; k < w*h; k++){// go through pixels in sub images
					if((x + y*width + k%w + (k/w)*width) < width*height ){//make sure that we are in the bounds of the image
						Color rgb = new Color(pixels[x + y*width + k%w + (k/w)*width]);//get current pixel value of original image based off which sub image we are in
						Color rgbmPixels = new Color(mpixels[u][k]); //pixel of mosaic image we are comparing with real image
						rm = rgbmPixels.getRed();
						gm = rgbmPixels.getGreen();
						bm = rgbmPixels.getBlue();
						//these represent the summations of the different parts of the equations we have
						currentrn = currentrn + ((rgb.getRed()*rm));
						currentgn = currentgn + ((rgb.getGreen()*gm));
						currentbn = currentbn + ((rgb.getBlue()*bm));
						currentrd = currentrd + (rm*rm);
						currentgd = currentgd + (gm*gm);
						currentbd = currentbd + (bm*bm);
						alpharn = alpharn + ((rgb.getRed()*rm));
						alphagn = alphagn + ((rgb.getGreen()*gm));	
						alphabn = alphabn + ((rgb.getBlue()*bm));
						alphard = alphard + rm*rm;
						alphabd = alphabd + bm*bm;
						alphagd = alphagd + gm*gm;
					}//if	
				}//for
				if(u == 0){//if very first picture
					//calculate the value of similarity  between images
					currentbn = (float) -Math.pow(currentbn,2)/currentbd;
					currentgn = (float) -Math.pow(currentgn,2)/currentgd;
					currentrn = (float) -Math.pow(currentrn,2)/currentrd;
					closetValue = currentbn + currentgn + currentrn;
					closetPictureIndex = u;
					finalAlphar = alpharn/alphard;
					finalAlphag = alphagn/alphagd;
					finalAlphab = alphabn/alphabd;
				}
				else{//compare with current lowest picture d value
					//calculate the value of similarity  between images
					float randomNumber = newRandom.nextFloat();
					currentbn = (float) -Math.pow(currentbn,2)/currentbd;
					currentgn = (float) -Math.pow(currentgn,2)/currentgd;
					currentrn = (float) -Math.pow(currentrn,2)/currentrd;
					if(closetValue > ((randomNumber+1)*(currentbn + currentgn + currentrn))){ //if my current lowest value is greater than my new one than i must replace it
						closetValue = (randomNumber+1)*(currentbn + currentgn + currentrn);
						closetPictureIndex = u;
						finalAlphar = alpharn/alphard;
						finalAlphag = alphagn/alphagd;
						finalAlphab = alphabn/alphabd;	
					}
				}
			}//for
			for(int m = 0; m < w*h; m ++) {//go through the current part of the image we are on and replace the pixels with the new image mosaic pixels we calculated
				if((x + y*width + m%w + (m/w)*width) < width*height ){//stay in bounds of our image
					Color rgb = new Color(pixels[x + y*width + m%w + (m/w)*width]);	 
					Color rgbmPixels = new Color(mpixels[closetPictureIndex][m]); 

					a = rgb.getAlpha();
					r = rgb.getRed();
					g = rgb.getGreen();
					b = rgb.getBlue();
					rm = rgbmPixels.getRed();
					gm = rgbmPixels.getGreen();
					bm = rgbmPixels.getBlue();
					int rmf;
					int gmf;
					int bmf;
					rmf = PixelUtils.clamp((int)((float)rm*finalAlphar));//calculate red green and blue values of new pixel values with alpha scaling value
					bmf = PixelUtils.clamp((int)((float)bm*finalAlphab));
					gmf = PixelUtils.clamp((int)((float)gm*finalAlphag));
					pixelsFinal[x + y*width + m%w + (m/w)*width] = new Color(rmf, gmf, bmf, a).getRGB();//replace that pixel
				}//if
			}//for
			x = x + w;//keep track of current x and y value. My x and y values stay the same until i move to another sub-image
			if(x >= width && y < height){
				x = 0;
				y = y + h;
			}
		}
		
		dst.setRGB(0, 0, width, height, pixelsFinal, 0, width);
	}

}