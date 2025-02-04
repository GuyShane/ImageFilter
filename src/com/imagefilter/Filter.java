package com.imagefilter;

import android.graphics.Bitmap;

public abstract class Filter {
	
	public Bitmap applyFilter(Bitmap source, int windowSize) {
		
		int width=source.getWidth();
		int height=source.getHeight();
		
		int []sourcePixels= new int[width*height];
		int []destPixels=new int[width*height];
		
		source.getPixels(sourcePixels, 0, width, 0, 0,
				width, height);

		for (int i=0;i<width*height;i++) {
			try {
				destPixels[i]=filterCalculation(getWindow(sourcePixels, i, width, windowSize));
			} catch (Exception e) {
				destPixels[i]=sourcePixels[i];
			}
		}

		return Bitmap.createBitmap(destPixels, width, height, source.getConfig());
	}
	
	public abstract int filterCalculation(int []window);
	
	private int getRow(int i, int width) {
		return i/width;
	}
	
	private int getCol(int i, int width) {
		return i%width;
	}
	
	private int getI(int row, int col, int width) {
		return row*width+col;
	}
	
	private int []getWindow(int []source, int i, int imageWidth, int windowSize) {
		int r=getRow(i,imageWidth);
		int c=getCol(i,imageWidth);
		int offset=windowSize/2;
		int []window=new int[windowSize*windowSize];
		for (int x=0;x<windowSize;x++){
			for (int y=0;y<windowSize;y++){
				window[getI(x,y,windowSize)]=source[getI((r+x)-offset,(c+y)-offset,imageWidth)];
			}
		}
		return window;
	}
}
