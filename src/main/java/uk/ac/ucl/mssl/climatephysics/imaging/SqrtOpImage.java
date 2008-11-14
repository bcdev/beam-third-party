package uk.ac.ucl.mssl.climatephysics.imaging;

import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.ImageLayout;
import javax.media.jai.PointOpImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFormatTag;

@SuppressWarnings("unchecked")
public class SqrtOpImage extends PointOpImage {

	public SqrtOpImage(RenderedImage source, 
			ImageLayout layout) {
		super(source, layout, null, true);
	}

	protected void computeRect(Raster[] sources,
			WritableRaster dest,
			Rectangle destRect) {
		
		//Raster source = sources[0];
		Rectangle srcRect = mapDestRect(destRect, 0);
		// RasterAccessor is a convenient way to represent any given 
		// Raster in a usable format.  It has very little overhead if
		// the underlying Raster is in a common format (PixelSequential
		// for this release) and allows generic code to process
		// a Raster with an exotic format.  Essentially, it allows the
		// common case to processed quickly and the rare case to be
		// processed easily.

		// This "best case" formatTag is used to create a pair of 
		// RasterAccessors for processing the source and dest rasters

		RasterFormatTag[] formatTags = getFormatTags();
		RasterAccessor srcAccessor = 
			new RasterAccessor(sources[0], srcRect, formatTags[0],
					getSourceImage(0).getColorModel());
		RasterAccessor dstAccessor = 
			new RasterAccessor(dest, destRect, formatTags[1],
					getColorModel());
		// Depending on the base dataType of the RasterAccessors,
		// either the byteLoop or intLoop method is called.  The two
		// functions are virtually the same, except for the data type
		// of the underlying arrays.
		switch (dstAccessor.getDataType()) {
		case DataBuffer.TYPE_DOUBLE:
			doubleLoop(srcAccessor,dstAccessor);
			break;
		default:
			String className = this.getClass().getName();
		throw new RuntimeException(className + 
				" does not implement computeRect" + 
				" for data type" + dstAccessor.getDataType());
		}

		// If the RasterAccessor object set up a temporary buffer for the 
		// op to write to, tell the RasterAccessor to write that data
		// to the raster now that we're done with it.
		//if (dstAccessor.needsClamping()) {
			dstAccessor.clampDataArrays();
			dstAccessor.copyDataToRaster();
		//}
	}	
	

	
	/**
	 * Computes an area of a given double-based destination Raster using
	 * a source RasterAccessor and a destination RasterAccesor.
	 */
	private void doubleLoop(RasterAccessor src, RasterAccessor dst) {
				
		int dwidth = dst.getWidth();
		int dheight = dst.getHeight();
		int dnumBands = dst.getNumBands();

		double dstDataArrays[][] = dst.getDoubleDataArrays();
		int dstBandOffsets[] = dst.getBandOffsets();
		int dstPixelStride = dst.getPixelStride();
		int dstScanlineStride = dst.getScanlineStride();

		double srcDataArrays[][] = src.getDoubleDataArrays();
		int srcBandOffsets[] = src.getBandOffsets();
		int srcPixelStride = src.getPixelStride();
		int srcScanlineStride = src.getScanlineStride();

		for (int k = 0; k < dnumBands; k++)  {
			double dstData[] = dstDataArrays[k];
			double srcData[] = srcDataArrays[k];
			int srcScanlineOffset = srcBandOffsets[k];
			int dstScanlineOffset = dstBandOffsets[k];
			for (int j = 0; j < dheight; j++)  {
				int srcPixelOffset = srcScanlineOffset;
				int dstPixelOffset = dstScanlineOffset;
				srcScanlineOffset += srcScanlineStride;
				dstScanlineOffset += dstScanlineStride;

				for (int i = 0; i < dwidth; i++)  {
					double pixel = srcData[srcPixelOffset];
					dstData[dstPixelOffset] = java.lang.Math.sqrt(pixel);
					srcPixelOffset += srcPixelStride;
					dstPixelOffset += dstPixelStride;
				} 
			}
		}
	}

}


