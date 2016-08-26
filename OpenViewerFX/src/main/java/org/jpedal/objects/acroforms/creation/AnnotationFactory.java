/*
 * ===========================================
 * Java Pdf Extraction Decoding Access Library
 * ===========================================
 *
 * Project Info:  http://www.idrsolutions.com
 * Help section for developers at http://www.idrsolutions.com/support/
 *
 * (C) Copyright 1997-2016 IDRsolutions and Contributors.
 *
 * This file is part of JPedal/JPDF2HTML5
 *
     This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


 *
 * ---------------
 * AnnotationFactory.java
 * ---------------
 */
package org.jpedal.objects.acroforms.creation;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.jpedal.color.DeviceCMYKColorSpace;
import org.jpedal.objects.GraphicsState;
import org.jpedal.objects.acroforms.FormRenderUtilsG2;
import static org.jpedal.objects.acroforms.creation.SwingFormFactory.curveInk;
import org.jpedal.objects.raw.FormObject;
import org.jpedal.objects.raw.PdfDictionary;
import org.jpedal.objects.raw.PdfObject;
import org.jpedal.render.DynamicVectorRenderer;
import org.jpedal.utils.LogWriter;

/**
 *
 */
public class AnnotationFactory {

    /**
     * Determine the type of annotation from the sub type value and call appropriate method
     * @param form :: PdfObject containing the annotation
     * @return BufferedImage of the annotation or null if formobject contains errors
     */
    public static BufferedImage getIcon(final PdfObject form){
        BufferedImage commentIcon = null;
        
        switch(form.getParameterConstant(PdfDictionary.Subtype)){
            case PdfDictionary.Text :
                commentIcon = getTextIcon(form);
                break;
            case PdfDictionary.Highlight :
                commentIcon = getHightlightIcon(form);
                break;
            case PdfDictionary.Square :
                commentIcon = getSquareIcon(form);
                break;
            case PdfDictionary.Underline :
                commentIcon = getUnderLineIcon(form);
                break;
            case PdfDictionary.StrickOut :
                commentIcon = getStrickOutIcon(form);
                break;
            case PdfDictionary.Caret :
                commentIcon = getCaretIcon(form);
                break;
            case PdfDictionary.FileAttachment :
                commentIcon = getFileAttachmentIcon();
                break;
            case PdfDictionary.Line :
                commentIcon = getLineIcon(form);
                break;
            case PdfDictionary.Polygon :
                commentIcon = getPolyIcon(form, false);
                break;
            case PdfDictionary.PolyLine :
                commentIcon = getPolyIcon(form, true);
                break;
            case PdfDictionary.Circle :
                commentIcon = getCircleIcon(form);
                break;
            case PdfDictionary.Squiggly:
                commentIcon = getSquigglyIcon(form);
                break;
            case PdfDictionary.Sound:
                commentIcon = getSoundIcon(form);
                break;
            case PdfDictionary.Ink:
                commentIcon = getInkIcon(form);
                break;
        }
        
        return commentIcon;
    }
    
    private static BufferedImage getInkIcon(PdfObject form){
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        if (quad != null) {

            Rectangle bounds = getFormBounds((FormObject) form, quad);
            final Object[] InkListArray = form.getObjectArray(PdfDictionary.InkList);
            final BufferedImage icon1 = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            scanInkListTree(InkListArray, form, icon1.getGraphics());
            return icon1;
        }
        return null;
    }
    
    public static float[] scanInkListTree(final Object[] InkListArray, final PdfObject form, final Graphics g) {
        
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        if (quad == null) {
            return null;
        }
         
        Rectangle bounds = getFormBounds((FormObject) form, quad);
        
        float minX = 0;
        float minY = 0;
        float maxX = 0;
        float maxY = 0;
        
        float[] vals = null;
        final Graphics2D g2 = (Graphics2D) g;
        //if specific DecodeParms for each filter, set othereise use global
        if(InkListArray !=null){
            
            final int count= InkListArray.length;
            
            float x;
            float y;
            
            boolean isFirstPoint = true;
            
            //If Graphics not set, don't draw anything.
            if(g!=null){
                final float[] underlineColor = form.getFloatArray(PdfDictionary.C);
                Color c1 = new Color(0);
                if(underlineColor!=null){
                    switch(underlineColor.length){
                        case 0:
                            //Should not happen. Do nothing. Annotation is transparent
                            break;
                        case 1:
                            //DeviceGrey colorspace
                            c1 = new Color(underlineColor[0],underlineColor[0],underlineColor[0],1.0f);
                            break;
                        case 3:
                            //DeviceRGB colorspace
                            c1 = new Color(underlineColor[0],underlineColor[1],underlineColor[2],1.0f);
                            break;
                        case 4:
                            //DeviceCMYK colorspace
                            final DeviceCMYKColorSpace cmyk = new DeviceCMYKColorSpace();
                            cmyk.setColor(underlineColor, 4);
                            c1 = new Color(cmyk.getColor().getRGB());
                            
                            break;
                        default:
                            break;
                    }
                }
                
                g2.setColor(new Color(0.0f,0.0f,0.0f,0.0f));
                g2.fillRect(0, 0, bounds.width, bounds.height);
                g2.setColor(c1);
                g2.setPaint(c1);
            }
            
            for(int i=0;i<count;i++){
                
                if(InkListArray[i] instanceof byte[]){
                    final byte[] decodeByteData= (byte[]) InkListArray[i];
                    
                    if(vals==null){
                        vals = new float[count];
                    }
                    
                    if(decodeByteData!=null){
                        final String val= new String(decodeByteData);
                        final float v = Float.parseFloat(val);
                        
                        switch(i%2){
                            case 0 :
                                if(isFirstPoint){
                                    minX = v;
                                    maxX = v;
                                }else{
                                    if(v<minX) {
                                        minX = v;
                                    }
                                    if(v>maxX) {
                                        maxX = v;
                                    }
                                }
                                x = (v - bounds.x);
                                vals[i] = x;
                                break;
                            case 1 :
                                if(isFirstPoint){
                                    minY = v;
                                    maxY = v;
                                    isFirstPoint = false;
                                }else{
                                    if(v<minY) {
                                        minY = v;
                                    }
                                    if(v>maxY) {
                                        maxY = v;
                                    }
                                }
                                y = bounds.height - (v - bounds.y);
                                vals[i] = y;
                                
                                //x = 0;
                                //y = 0;
                                
                                break;
                        }
                        //                        System.out.println("val="+val);
                        
                    }
                }else{
                    // System.out.println(">>");
                    final float[] r = scanInkListTree((Object[]) InkListArray[i], form, g);
                    if(isFirstPoint){
                        minX = r[0];
                        maxX = r[2];
                        minY = r[1];
                        maxY = r[3];
                        isFirstPoint = false;
                    }else{
                        if(r[0]<minX) {
                            minX = r[0];
                        }
                        if(r[2]>maxX) {
                            maxX = r[2];
                        }
                        if(r[1]<minY) {
                            minY = r[1];
                        }
                        if(r[3]>maxY) {
                            maxY = r[3];
                        }
                    }
                    // System.out.println("<<");
                }
            }
        }
        
        if(vals!=null){
            if(vals.length<6){ //Only use lines on ink
            for(int i=0; i<vals.length; i++){
                if(i%2==0){ //X coord
                    if(vals[i]<minX) {
                        minX = vals[i];
                    }
                    
                    if(vals[i]>maxX) {
                        maxX = vals[i];
                    }
                }else{ //Y coord
                    if(vals[i]<minY) {
                        minY = vals[i];
                    }
                    
                    if(vals[i]>maxY) {
                        maxY = vals[i];
                    }
                }
            }
            
            float xOffset = 0;
            float yOffset = 0;
            
            if(minX < 0) {
                xOffset = Math.abs(minX);
            }
            if(minY < 0) {
                yOffset = Math.abs(minY);
            }
            
            minX += xOffset;
            maxX += xOffset;
            minY += yOffset;
            maxY += yOffset;
            
            if(g2!=null){
                
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(1.52f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                for(int i=0; i<vals.length; i+=4){
                    final Line2D.Float line = new Line2D.Float(vals[0], vals[1], vals[2], vals[3]);
                    g2.draw(line);
                }
            }
            }else{ //Enough armguments so curve ink
                final float[] values = curveInk(vals);
            for(int i=0; i<values.length; i++){
                if(i%2==0){ //X coord
                    if(values[i]<minX) {
                        minX = values[i];
                    }
                    
                    if(values[i]>maxX) {
                        maxX = values[i];
                    }
                }else{ //Y coord
                    if(values[i]<minY) {
                        minY = values[i];
                    }
                    
                    if(values[i]>maxY) {
                        maxY = values[i];
                    }
                }
            }
            
            float xOffset = 0;
            float yOffset = 0;
            
            if(minX < 0) {
                xOffset = Math.abs(minX);
            }
            if(minY < 0) {
                yOffset = Math.abs(minY);
            }
            
            minX += xOffset;
            maxX += xOffset;
            minY += yOffset;
            maxY += yOffset;
            
            if(g2!=null){
                
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(1.52f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                for(int i=0; i<values.length; i+=8){
                    
                    final CubicCurve2D curve = new CubicCurve2D.Double(values[i], values[i+1], values[i+2], values[i+3]
                            , values[i+4], values[i+5], values[i+6], values[i+7]);
                    g2.draw(curve);
                }
            }
            }
        }
        
        return new float[]{minX, minY, maxX, maxY};
    }
    
    private static Color convertFloatArrayToColor(float[] values){
        Color c = new Color(0,0,0,0);
        if (values != null) {
            switch (values.length) {
                case 0:
                    //Should not happen. Do nothing. Annotation is transparent
                    break;
                case 1:
                    //DeviceGrey colorspace
                    c = new Color(values[0], values[0], values[0]);
                    break;
                case 3:
                    //DeviceRGB colorspace
                    c = new Color(values[0], values[1], values[2]);
                    break;
                case 4:
                    //DeviceCMYK colorspace
                    final DeviceCMYKColorSpace cmyk = new DeviceCMYKColorSpace();
                    cmyk.setColor(values, 4);
                    c = new Color(cmyk.getColor().getRGB());
                    c = new Color(c.getRed(), c.getGreen(), c.getBlue());

                    break;
                default:
                    break;
            }
        }
        return c;
    }
    
    private static Rectangle getFormBounds(FormObject form, float[] rect) {
        Rectangle bounds = (form).getBoundingRectangle();

        //Bounds is 0 so calculate based on rect areas
        if (bounds.getWidth() == 0 && bounds.getHeight() == 0) {
            for (int i = 0; i != rect.length; i++) {
                if (i % 2 == 0) {
                    if (bounds.x > rect[i]) {
                        bounds.x = (int) rect[i];
                    }
                    if (bounds.x + bounds.width < rect[i]) {
                        bounds.width = (int) (rect[i] - bounds.x);
                    }
                } else {
                    if (bounds.y > rect[i]) {
                        bounds.y = (int) rect[i];
                    }
                    if (bounds.y + bounds.height < rect[i]) {
                        bounds.height = (int) (rect[i] - bounds.y);
                    }
                }

            }
        }
        return bounds;
    }
    
    private static BufferedImage getStrickOutIcon(final PdfObject form){
        
        Color color = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));

        float[] quad = form.getFloatArray(PdfDictionary.QuadPoints);
        if (quad == null) {
            quad = form.getFloatArray(PdfDictionary.Rect);
        }

        Rectangle bounds = getFormBounds((FormObject)form, quad);
            
        final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics g = icon.getGraphics();
        
        if (quad.length >= 8) {
            for (int hi = 0; hi != quad.length; hi += 8) {
                final int x = (int) quad[hi] - bounds.x;
                int y = (int) quad[hi + 5] - bounds.y;
                //Adjust y for display
                y = (bounds.height - y) - (int) (quad[hi + 1] - quad[hi + 5]);
                final int width = (int) (quad[hi + 2] - quad[hi]);
                final int height = (int) (quad[hi + 1] - quad[hi + 5]);
                
                try {
                    g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                    g.fillRect(0, 0, width, height);
                    g.setColor(color);
                    g.fillRect(x, y + (height / 2), width, 1);
                } catch (final Exception e) {
                    LogWriter.writeLog("Exception: " + e.getMessage());
                }
            }
        }
        return icon;
    }
    
    private static BufferedImage getUnderLineIcon(final PdfObject form){
        
        Color color = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        
        float[] quad = form.getFloatArray(PdfDictionary.QuadPoints);
        if (quad == null) {
            quad = form.getFloatArray(PdfDictionary.Rect);
        }
        
        Rectangle bounds = getFormBounds((FormObject)form, quad);
            
        final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics g = icon.getGraphics();
        
        if (quad.length >= 8) {
            for (int hi = 0; hi != quad.length; hi += 8) {
                final int x = (int) quad[hi] - bounds.x;
                int y = (int) quad[hi + 5] - bounds.y;
                //Adjust y for display
                y = (bounds.height - y) - (int) (quad[hi + 1] - quad[hi + 5]);
                final int width = (int) (quad[hi + 2] - quad[hi]);
                final int height = (int) (quad[hi + 1] - quad[hi + 5]);

                try {
                    g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                    g.fillRect(x, y, width, height);
                    g.setColor(color);
                    g.fillRect(x, y + height - 1, width, 1);
                } catch (final Exception e) {
                    LogWriter.writeLog("Exception: " + e.getMessage());
                }
            }
        }
        
        return icon;
    }
    
    private static BufferedImage getSquigglyIcon(final PdfObject form){
        
        Color color = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        
        float[] quad = form.getFloatArray(PdfDictionary.QuadPoints);
        if (quad == null) {
            quad = form.getFloatArray(PdfDictionary.Rect);
        }
        
        Rectangle bounds = getFormBounds((FormObject)form, quad);
            
        final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics g = icon.getGraphics();
        
        if (quad.length >= 8) {
            for (int hi = 0; hi != quad.length; hi += 8) {
                final int x = (int) quad[hi] - bounds.x;
                int y = (int) quad[hi + 5] - bounds.y;
                //Adjust y for display
                y = (bounds.height - y) - (int) (quad[hi + 1] - quad[hi + 5]);
                final int width = (int) (quad[hi + 2] - quad[hi]);
                final int height = (int) (quad[hi + 1] - quad[hi + 5]);
                final int step = 6;
                final int bottom = y + height-1;
                final int top = bottom-(step/2);
                try {
                    g.setColor(color);
                    
                    for(int i=0; i<width; i+=step){
                        g.drawLine(x+i, bottom, x+i+(step/2), top);
                        g.drawLine(x+i+(step/2), top, x+i+step, bottom);
                    }
                } catch (final Exception e) {
                    LogWriter.writeLog("Exception: " + e.getMessage());
                }
            }
        }
        
        return icon;
    }
    
    private static BufferedImage getSquareIcon(final PdfObject form){
        Color c = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        Color ic = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.IC));
        
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        if (quad != null) {
            
            Rectangle bounds = getFormBounds((FormObject)form, quad);
            
            final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics2D g = (Graphics2D)icon.getGraphics();
            g.setColor(ic);
            g.fillRect(0, 0, bounds.width, bounds.height);
            g.setColor(c);
            FormRenderUtilsG2.renderBorder(g, (FormObject)form, 0,0,icon.getWidth(), icon.getHeight());
            
            return icon;
        }
        //Return a small empty image as no highlight to make.
        return null;
    }
    
    private static BufferedImage getCircleIcon(final PdfObject form){
        Color c = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        Color ic = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.IC));
        
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        if (quad != null) {
            
            Rectangle bounds = getFormBounds((FormObject)form, quad);
            
            final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics2D g = (Graphics2D)icon.getGraphics();
            g.setColor(ic);
            g.fillOval(1, 1, bounds.width-2, bounds.height-2);
            g.setColor(c);
            g.drawOval(1, 1, bounds.width-2, bounds.height-2);
            
            return icon;
        }
        //Return a small empty image as no highlight to make.
        return null;
    }
    
    private static BufferedImage getLineIcon(final PdfObject form){
        Color c = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        float[] line = form.getFloatArray(PdfDictionary.L);
        if (quad != null && line != null) {
            
            Rectangle bounds = getFormBounds((FormObject)form, quad);
            
            final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics2D g = (Graphics2D)icon.getGraphics();
            g.setColor(c);
            g.drawLine((int)line[0]-bounds.x, (int)(bounds.height-(line[1]-bounds.y)), (int)line[2]-bounds.x, (int)(bounds.height-(line[3]-bounds.y)));
            
            return icon;
        }
        //Return a small empty image as no highlight to make.
        return null;
    }
    
    private static BufferedImage getPolyIcon(final PdfObject form, final boolean line){
        Color c = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        Color ic = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.IC));
        
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        float[] vertices = form.getFloatArray(PdfDictionary.Vertices);
        
        if (quad != null && vertices!=null) {
            
            Rectangle bounds = getFormBounds((FormObject)form, quad);
            
            final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics2D g = (Graphics2D)icon.getGraphics();
            g.setColor(c);
            float lastX = vertices[0];
            float lastY = vertices[1];
            Polygon poly = new Polygon();
            if (!line) {
                poly.addPoint((int) lastX - bounds.x, (int) (bounds.height - (lastY - bounds.y)));
            }
            for(int i=2; i!=vertices.length; i+=2){
                if(!line){
                    poly.addPoint((int)vertices[i]-bounds.x, (int)(bounds.height-(vertices[i+1]-bounds.y)));
                }
                g.drawLine((int)lastX-bounds.x, (int)(bounds.height-(lastY-bounds.y)), (int)vertices[i]-bounds.x, (int)(bounds.height-(vertices[i+1]-bounds.y)));
                lastX = vertices[i];
                lastY = vertices[i+1];
            }
            if(!line){
                g.drawLine((int)lastX-bounds.x, (int)(bounds.height-(lastY-bounds.y)), (int)vertices[0]-bounds.x, (int)(bounds.height-(vertices[1]-bounds.y)));
                g.setColor(ic);
                g.fillPolygon(poly);
            }
            return icon;
        }
        //Return a small empty image as no highlight to make.
        return null;
    }
    
    private static BufferedImage getCaretIcon(final PdfObject form){
        Color c = convertFloatArrayToColor(form.getFloatArray(PdfDictionary.C));
        float[] rd = form.getFloatArray(PdfDictionary.RD);
        
        float[] quad = form.getFloatArray(PdfDictionary.Rect);
        if (quad != null) {
            
            Rectangle bounds = getFormBounds((FormObject)form, quad);
            
            final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics2D g = (Graphics2D)icon.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setStroke(new BasicStroke(rd[1]));
            g.setColor(c);
            g.drawLine(0, bounds.height, bounds.width/2, 0);
            g.drawLine(bounds.width/2, 0, bounds.width, bounds.height);
            
            return icon;
        }
        //Return a small empty image as no highlight to make.
        return null;
    }
    
    private static BufferedImage getHightlightIcon(final PdfObject form){
        final float[] f = form.getFloatArray(PdfDictionary.C);
        Color c = new Color(0);
        if (f != null) {
            switch (f.length) {
                case 0:
                    //Should not happen. Do nothing. Annotation is transparent
                    break;
                case 1:
                    //DeviceGrey colorspace
                    c = new Color(f[0], f[0], f[0], 0.5f);
                    break;
                case 3:
                    //DeviceRGB colorspace
                    c = new Color(f[0], f[1], f[2], 0.5f);
                    break;
                case 4:
                    //DeviceCMYK colorspace
                    final DeviceCMYKColorSpace cmyk = new DeviceCMYKColorSpace();
                    cmyk.setColor(f, 4);
                    c = new Color(cmyk.getColor().getRGB());
                    c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0.5f);

                    break;
                default:
                    break;
            }
        }
        
        float[] quad = form.getFloatArray(PdfDictionary.QuadPoints);
        if (quad != null) {
            Rectangle bounds = ((FormObject)form).getBoundingRectangle();
            
            //Bounds is 0 so calculate based on quad areas
            if(bounds.getWidth()==0 && bounds.getHeight()==0){
                for(int i=0; i!=quad.length; i++){
                    if(i%2==0){
                        if(bounds.x>quad[i]){
                            bounds.x = (int)quad[i];
                        }
                        if(bounds.x+bounds.width<quad[i]){
                            bounds.width = (int)(quad[i]-bounds.x);
                        }
                    }else{
                        if(bounds.y>quad[i]){
                            bounds.y = (int)quad[i];
                        }
                        if(bounds.y+bounds.height<quad[i]){
                            bounds.height = (int)(quad[i]-bounds.y);
                        }
                    }
                    
                }
            }
            
            final BufferedImage icon = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
            final Graphics g = icon.getGraphics();
        
            if (quad.length >= 8) {
                for (int hi = 0; hi != quad.length; hi += 8) {
                    final int x = (int) quad[hi] - bounds.x;
                    int y = (int) quad[hi + 5] - bounds.y;
                    //Adjust y for display
                    y = (bounds.height - y) - (int) (quad[hi + 1] - quad[hi + 5]);
                    final int width = (int) (quad[hi + 2] - quad[hi]);
                    final int height = (int) (quad[hi + 1] - quad[hi + 5]);
                    final Rectangle rh = new Rectangle(x, y, width, height);
                    g.setColor(c);
                    g.fillRect(rh.x, rh.y, rh.width, rh.height);
                }
            }
            return icon;
        }
        //Return a small empty image as no highlight to make.
        return null;
    }
    
    private static BufferedImage getTextIcon(final PdfObject form){
        
        String name = form.getName(PdfDictionary.Name);
        final String iconFile;
        
        if(name==null) {
            name = "Note";
        }
        
        /* Name of the icon image to use for the icon of this annotation
         * - predefined icons are needed for names:-
         * Comment, Key, Note, Help, NewParagraph, Paragraph, Insert
         */
        if(name.equals("Comment")){
          iconFile = "/org/jpedal/objects/acroforms/res/comment.png";
        }else if(name.equals("Check")){
            iconFile = "/org/jpedal/objects/acroforms/res/Check.png";
        }else if(name.equals("Checkmark")){
            iconFile = "/org/jpedal/objects/acroforms/res/Checkmark.png";
        }else if(name.equals("Circle")){
            iconFile = "/org/jpedal/objects/acroforms/res/Circle.png";
        }else if(name.equals("Cross")){
            iconFile = "/org/jpedal/objects/acroforms/res/Cross.png";
        }else if(name.equals("CrossHairs")){
            iconFile = "/org/jpedal/objects/acroforms/res/CrossHairs.png";
        }else if(name.equals("Help")){
            iconFile = "/org/jpedal/objects/acroforms/res/Help.png";
        }else if(name.equals("Insert")){
            iconFile = "/org/jpedal/objects/acroforms/res/InsertText.png";
        }else if(name.equals("Key")){
            iconFile = "/org/jpedal/objects/acroforms/res/Key.png";
        }else if(name.equals("NewParagraph")){
            iconFile = "/org/jpedal/objects/acroforms/res/NewParagraph.png";
        }else if(name.equals("Paragraph")){
            iconFile = "/org/jpedal/objects/acroforms/res/Paragraph.png";
        }else if(name.equals("RightArrow")){
            iconFile = "/org/jpedal/objects/acroforms/res/RightArrow.png";
        }else if(name.equals("RightPointer")){
            iconFile = "/org/jpedal/objects/acroforms/res/RightPointer.png";
        }else if(name.equals("Star")){
            iconFile = "/org/jpedal/objects/acroforms/res/Star.png";
        }else if(name.equals("UpLeftArrow")){
            iconFile = "/org/jpedal/objects/acroforms/res/Up-LeftArrow.png";
        }else if(name.equals("UpArrow")){
            iconFile = "/org/jpedal/objects/acroforms/res/UpArrow.png";
        }else{ //Default option. Name = Note
            iconFile = "/org/jpedal/objects/acroforms/res/TextNote.png";
        }
        
        BufferedImage commentIcon = null;
        try {
            commentIcon = ImageIO.read(AnnotationFactory.class.getResource(iconFile));
        } catch (final IOException e){
            LogWriter.writeLog("Exception: " + e.getMessage());
        }
        
        //Set color of annotation
        float[] col = form.getFloatArray(PdfDictionary.C);
        
        if(col==null){//If not color set we should use white
            col = new float[]{1.0f,1.0f,1.0f};
        }
        
        final Color c = new Color(col[0], col[1], col[2]);
        final int rgb = c.getRGB();

        //Replace default color with specified color
        for(int x=0; x!=commentIcon.getWidth(); x++){
            for(int y=0; y!=commentIcon.getHeight(); y++){
                
               //Checks for yellow (R255,G255,B000) and replaces with color
                if(commentIcon.getRGB(x, y)==-256){
                    commentIcon.setRGB(x, y, rgb);
                }
            }
        }

        return commentIcon;
    }
    
    private static BufferedImage getFileAttachmentIcon(){
        
        final String iconFile = "/org/jpedal/objects/acroforms/res/FileAttachment.png";
        
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(AnnotationFactory.class.getResource(iconFile));
        } catch (final IOException e){
            LogWriter.writeLog("Exception: " + e.getMessage());
        }
        
        return icon;
    }
    
    private static BufferedImage getSoundIcon(final PdfObject form){
        
        String iconFile = "/org/jpedal/objects/acroforms/res/Speaker.png";
        
        String name = form.getName(PdfDictionary.Name);
        if(name!=null && name.equals("Mic")){
            iconFile = "/org/jpedal/objects/acroforms/res/Microphone.png";
        }
        
        BufferedImage icon = null;
        try {
            icon = ImageIO.read(AnnotationFactory.class.getResource(iconFile));
        } catch (final IOException e){
            LogWriter.writeLog("Exception: " + e.getMessage());
        }
        
        return icon;
    }
    
    /**
     * Method to create an icon to represent the annotation and render it.
     * @param form :: PdfObject to hold the annotation data
     * @param current :: DynamicVectorRender to draw the annotation
     * @param pageNumber :: Int value of the page number
     * @param rotation :: Int value of the page rotation
     */
    public static void renderFlattenedAnnotation(final PdfObject form, final DynamicVectorRenderer current, final int pageNumber, final int rotation) {

        final BufferedImage image=AnnotationFactory.getIcon(form);

        if (image != null) {
            final GraphicsState gs = new GraphicsState();

            /*
             * now draw the finished image of the form
             */
            final int iconHeight = image.getHeight();
            final int iconWidth = image.getWidth();

            final float[] rect = form.getFloatArray(PdfDictionary.Rect);

            //Some Text annotations can have incorrect sizes so correct to icon size
            if (form.getParameterConstant(PdfDictionary.Subtype) == PdfDictionary.Text) {
                rect[2] = rect[0] + iconWidth;
                rect[1] = rect[3] - iconHeight;
                form.setFloatArray(PdfDictionary.Rect, rect);
            }
        //4 needed as we upsample by a factor of 4
            //Factor out rotation as icon should not be rotated
            switch (rotation % 360) {
                case 0:
                    gs.CTM = new float[][]{{iconWidth, 0, 1}, {0, iconHeight, 1}, {0, 0, 0}};

                    gs.x = rect[0];
                    gs.y = rect[3] - iconHeight;

                    //draw onto image
                    gs.CTM[2][0] = rect[0];
                    gs.CTM[2][1] = rect[3] - iconHeight;
                    break;
                case 90:
                    gs.CTM = new float[][]{{0, iconWidth, 1}, {-iconHeight, 0, 1}, {0, 0, 0}};

                    gs.x = rect[0] + iconHeight;
                    gs.y = rect[3];

                    //draw onto image
                    gs.CTM[2][0] = rect[0] + iconHeight;
                    gs.CTM[2][1] = rect[3];
                    break;
                case 180:
                    gs.CTM = new float[][]{{-iconWidth, 0, 1}, {0, -iconHeight, 1}, {0, 0, 0}};

                    gs.x = rect[0];
                    gs.y = rect[3] + iconHeight;

                    //draw onto image
                    gs.CTM[2][0] = rect[0];
                    gs.CTM[2][1] = rect[3] + iconHeight;
                    break;
                case 270:
                    gs.CTM = new float[][]{{0, -iconWidth, 1}, {iconHeight, 0, 1}, {0, 0, 0}};

                    gs.x = rect[0] - iconHeight;
                    gs.y = rect[3];

                    //draw onto image
                    gs.CTM[2][0] = rect[0] - iconHeight;
                    gs.CTM[2][1] = rect[3];
                    break;
            }

            //Hard code blendMode for highlights to ensure correct output
            if (form.getParameterConstant(PdfDictionary.Subtype) == PdfDictionary.Highlight) {
                current.setGraphicsState(GraphicsState.STROKE, gs.getAlpha(GraphicsState.STROKE), PdfDictionary.Darken);
                current.setGraphicsState(GraphicsState.FILL, gs.getAlpha(GraphicsState.FILL), PdfDictionary.Darken);
            }

            current.drawImage(pageNumber, image, gs, false, form.getObjectRefAsString(), -1);

            if (form.getParameterConstant(PdfDictionary.Subtype) == PdfDictionary.Highlight) {
                current.setGraphicsState(GraphicsState.STROKE, gs.getAlpha(GraphicsState.STROKE), PdfDictionary.Normal);
                current.setGraphicsState(GraphicsState.FILL, gs.getAlpha(GraphicsState.FILL), PdfDictionary.Normal);
            }
        }
    }
}
