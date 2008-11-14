package uk.ac.ucl.mssl.climatephysics.imaging;

/*
 * LMB: Adapted from code supplied by Sun Microsystems under the
 * following license:
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 * */
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;

import javax.media.jai.ImageLayout;

/**
 * Class implementing the RIF and CRIF interfaces for the Subsample
 * operator.  An instance of this class should be registered with the
 * OperationRegistry with operation name "Subsample" and in the case
 * of the RIF, product name "jaiexample".
 */
public class SqrtCRIF implements ContextualRenderedImageFactory {
    /** Default constructor. */
    public SqrtCRIF() {}

    /**
     * Creates a new instance of SubsampleOpImage in the rendered layer.
     * This method satisfies the implementation of RIF.
     *
     * @param paramBlock  The source image, the X and Y scale factors.
     * @param renderHints The hints possible including <code>ImageLayout</code>,
     *                    <code>TileCache</code>, and
     *                    <code>BorderExtender</code>.
     */
    public RenderedImage create(ParameterBlock paramBlock,
                                RenderingHints renderHints) {
    	System.out.println("rendered image create");
    	
        RenderedImage source = paramBlock.getRenderedSource(0);
        
        ImageLayout layout = new ImageLayout();
        ComponentSampleModel sourceModel = (ComponentSampleModel)source.getSampleModel();
        System.out.println(sourceModel);
        
        SampleModel doubleModel = new ComponentSampleModel(DataBuffer.TYPE_DOUBLE,sourceModel.getWidth(),sourceModel.getHeight(),sourceModel.getPixelStride(), sourceModel.getScanlineStride(), sourceModel.getBandOffsets());
        layout.setSampleModel(doubleModel);

        return new SqrtOpImage(source, layout);
        
    }

    /**
     * Creates a new instance of <code>SubsampleOpImage</code>
     * in the renderable layer.  This method satisfies the
     * implementation of CRIF.
     */
    public RenderedImage create(RenderContext renderContext,
                                ParameterBlock paramBlock) {
    	System.out.println("rendered image create 2");
        return paramBlock.getRenderedSource(0);
    }

    /**
     * Maps the output RenderContext into the RenderContext for the
     * indicated source.  This method satisfies the implementation of CRIF.
     *
     * @param sourceIndex     The index of the source image.
     * @param renderContext   The renderContext being applied to the operation.
     * @param paramBlock      The ParameterBlock containing the sources
     *                        and the translation factors.
     * @param image           The RenderableImageOp from which this method
     *                        was called.
     */
    public RenderContext mapRenderContext(int sourceIndex,
                                          RenderContext renderContext,
					  ParameterBlock paramBlock,
					  RenderableImage image) {
    	System.out.println("maprendercontext");

        RenderContext RC = (RenderContext)renderContext.clone();

        return RC;
    }

    /**
     * Gets the bounding box for the output of <code>SubsampleOpImage</code>.
     * This method satisfies the implementation of CRIF.
     */
    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
    	System.out.println("get bounds");
	        // Get the source.
	        RenderableImage source = paramBlock.getRenderableSource(0);
	        return new Rectangle2D.Float(source.getMinX(), source.getMinY(), source.getWidth(), source.getHeight());
	    }

    /**
     * Returns the appropriate instance of the specified property specified
     * by the "name" parameter.
     *
     * <p> The implementation in this class always returns null since
     * no properties are defined by default.
     *
     * @param paramBlock A <code>ParameterBlock</code> containing the
     *        sources and parameters of the operation.
     * @param name A <code>String</code> containing the desired property name.
     *
     * @return An <code>Object</code> representing the value of the
     *         named property.
     */
    public Object getProperty(ParameterBlock paramBlock,
                              String name) {
        return null;
    }

    /**
     * Returns the valid property names for the operation.  The
     * implementation in this class always returns <code>null</code>
     * since no properties are associated with the operation by
     * default.
     *
     * @return An array of <code>String</code>s.
     */
    public String[] getPropertyNames() {
        return null;
    }

    /** 
     * Returns true if successive renderings with the same arguments
     * may produce different results.  The implementation in this
     * class always returns <code>false</code> so as to enable caching
     * of renderings by default.  CRIFs that do implement dynamic
     * rendering behavior must override this method.
     *
     * @return <code>true</code> if the CRIF has dynamic behavior.
     */
    public boolean isDynamic() {
        return false;
    }
}




