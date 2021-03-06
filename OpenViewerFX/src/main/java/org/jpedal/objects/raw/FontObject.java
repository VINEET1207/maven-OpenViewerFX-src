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
 * FontObject.java
 * ---------------
 */
package org.jpedal.objects.raw;

import org.jpedal.fonts.StandardFonts;
import org.jpedal.utils.LogWriter;

public class FontObject extends PdfObject {

    //mapped onto Type1
    private static final int MMType1=1230852645;

    //mapped onto Type1
    private static final int Type1C=1077224796;

    private static final int ZaDb=707859506;

    private static final int ZapfDingbats=1889256112;

    private static final int Symbol=1026712197;

    private PdfObject CharProcs, CIDSet, CIDSystemInfo, CIDToGIDMap,
    DescendantFonts,
    FontDescriptor,FontFile,
    FontFile2, FontFile3, ToUnicode;

    int BaseEncoding=PdfDictionary.Unknown;

	int CIDToGIDMapAsConstant=PdfDictionary.Unknown;

    int FirstChar,LastChar=255,Flags,MissingWidth,DW=-1,StemV, Supplement;

    float Ascent,Descent;

    float[] Widths,FontBBox;

    double[] FontMatrix;

    byte[][] Differences;
    
    Object[] W,W2;

    private byte[] rawBaseFont,rawCharSet, rawCMapName,
    rawFontName,rawFontStretch,
    rawOrdering, rawRegistry;

    private String BaseFont,CharSet, CMapName,
    FontName,FontStretch, Ordering,Registry;
    private int[] DW2;

    public FontObject(final String ref) {
        super(ref);
    }

    public FontObject(final int ref, final int gen) {
       super(ref,gen);
    }


    public FontObject(final int type) {
    	super(type);
	}

    @Override
    public PdfObject getDictionary(final int id){

        switch(id){

            case PdfDictionary.CharProcs:
                return CharProcs;

            case PdfDictionary.CIDSet:
                return CIDSet;

            case PdfDictionary.CIDSystemInfo:
	        	return CIDSystemInfo;

	        case PdfDictionary.CIDToGIDMap:
	        	return CIDToGIDMap;

            case PdfDictionary.DescendantFonts:
                return DescendantFonts;

            case PdfDictionary.Encoding:
                return Encoding;

            case PdfDictionary.FontDescriptor:
                return FontDescriptor;

            case PdfDictionary.FontFile:
                return FontFile;

            case PdfDictionary.FontFile2:
                return FontFile2;

            case PdfDictionary.FontFile3:
                return FontFile3;

            case PdfDictionary.ToUnicode:
                return ToUnicode;

            default:
                return super.getDictionary(id);
        }
    }

    @Override
    public void setIntNumber(final int id, final int value){

        switch(id){

            case PdfDictionary.Ascent:
                Ascent=value;
                break;

            case PdfDictionary.Descent:
                Descent=value;
                break;

	        case PdfDictionary.DW:
	            DW=value;
	        break;

            case PdfDictionary.FirstChar:
                FirstChar=value;
            break;

            case PdfDictionary.Flags:
                Flags=value;
            break;

            case PdfDictionary.LastChar:
                LastChar=value;
            break;

            case PdfDictionary.MissingWidth:
                MissingWidth=value;
            break;

            case PdfDictionary.StemV:
                StemV=value;
            break;

            case PdfDictionary.Supplement:
            	Supplement=value;
            break;

            default:
            	super.setIntNumber(id, value);
        }
    }

    @Override
    public int getInt(final int id){

        switch(id){

	        case PdfDictionary.DW:
	            return DW;

            case PdfDictionary.FirstChar:
                return FirstChar;

            case PdfDictionary.Flags:
                return Flags;

            case PdfDictionary.LastChar:
                return LastChar;

            case PdfDictionary.MissingWidth:
                return MissingWidth;

            case PdfDictionary.StemV:
                return StemV;

            case PdfDictionary.Supplement:
                return Supplement;

            default:
            	return super.getInt(id);
        }
    }

    @Override
    public void setFloatNumber(final int id, final float value){

        switch(id){

            case PdfDictionary.Ascent:
                Ascent=value;
                break;

            case PdfDictionary.Descent:
                Descent=value;
                break;

            default:
                super.setFloatNumber(id, value);
        }
    }

    @Override
    public float getFloatNumber(final int id){

        switch(id){

            case PdfDictionary.Ascent:
                return Ascent;

            case PdfDictionary.Descent:
                return Descent;

            default:
                return super.getFloatNumber(id);
        }
    }

    @Override
    public void setDictionary(final int id, final PdfObject value){

    	value.setID(id);

        //flag embedded data
        if(id==PdfDictionary.FontFile || id==PdfDictionary.FontFile2 || id==PdfDictionary.FontFile3) {
            hasStream = true;
        }


        switch(id){

            case PdfDictionary.CharProcs:
    		    CharProcs=value;
    		    break;

            case PdfDictionary.CIDSet:
                CIDSet=value;
                break;

            case PdfDictionary.CIDSystemInfo:
	        	CIDSystemInfo=value;
	        break;

	        case PdfDictionary.CIDToGIDMap:
	        	CIDToGIDMap=value;
            break;

            case PdfDictionary.DescendantFonts:
                DescendantFonts=value;
            break;

            case PdfDictionary.Encoding:
                Encoding=value;
            break;

            case PdfDictionary.FontDescriptor:
                FontDescriptor=value;
            break;

            case PdfDictionary.FontFile:
                FontFile=value;
            break;

            case PdfDictionary.FontFile2:
                FontFile2=value;
            break;

            case PdfDictionary.FontFile3:
                FontFile3=value;
            break;

            case PdfDictionary.ToUnicode:
            	ToUnicode=value;
            break;

            default:
            	super.setDictionary(id, value);
        }
    }


    @Override
    public int setConstant(final int pdfKeyType, final int keyStart, final int keyLength, final byte[] raw) {

        int PDFvalue =PdfDictionary.Unknown;

        int id=0,x=0,next;

        try{

            //convert token to unique key which we can lookup

            for(int i2=keyLength-1;i2>-1;i2--){

            	next=raw[keyStart+i2];

            	next -= 48;

                id += ((next)<<x);

                x += 8;
            }

            switch(id){

                case StandardFonts.CIDTYPE0:
                    PDFvalue =StandardFonts.CIDTYPE0;
                break;

                case PdfDictionary.CIDFontType0C:
                    PDFvalue =PdfDictionary.CIDFontType0C;
                break;

                case StandardFonts.CIDTYPE2:
                    PDFvalue =StandardFonts.CIDTYPE2;
                break;

                case PdfDictionary.CMap:
                    PDFvalue =PdfDictionary.CMap;
                break;

                case PdfDictionary.Encoding:
                    PDFvalue =PdfDictionary.Encoding;
                break;

                case PdfDictionary.Identity_H:
                    PDFvalue =PdfDictionary.Identity_H;
                break;

                case PdfDictionary.Identity_V:
                    PDFvalue =PdfDictionary.Identity_V;
                break;

                case PdfDictionary.MacExpertEncoding:
                    PDFvalue =StandardFonts.MACEXPERT;
                break;

                case PdfDictionary.MacRomanEncoding:
                    PDFvalue =StandardFonts.MAC;
                break;

                case PdfDictionary.PDFDocEncoding:
                    PDFvalue =StandardFonts.PDF;
                break;

                case MMType1:
                    PDFvalue =StandardFonts.TYPE1;
                break;

                case PdfDictionary.StandardEncoding:
                    PDFvalue =StandardFonts.STD;
                break;

                case StandardFonts.TYPE0:
                    PDFvalue =StandardFonts.TYPE0;
                break;

                case StandardFonts.TYPE1:
                    PDFvalue =StandardFonts.TYPE1;
                break;

                case Type1C:
                    PDFvalue =StandardFonts.TYPE1;
                break;

                case StandardFonts.TYPE3:
                    PDFvalue =StandardFonts.TYPE3;
                break;

                case StandardFonts.TRUETYPE:
                    PDFvalue =StandardFonts.TRUETYPE;
                break;

                case PdfDictionary.WinAnsiEncoding:
                    PDFvalue =StandardFonts.WIN;
                break;

                default:

                	if(pdfKeyType==PdfDictionary.Encoding){
                		PDFvalue=CIDEncodings.getConstant(id);

//                		if(PDFvalue==PdfDictionary.Unknown){
//
//                			byte[] bytes=new byte[keyLength];
//
//                          System.arraycopy(raw,keyStart,bytes,0,keyLength);
//
//                			unknownValue=new String(bytes);
//                		}

                		if(debug && PDFvalue==PdfDictionary.Unknown){
                			System.out.println("Value not in PdfCIDEncodings");

                           	 final byte[] bytes=new byte[keyLength];

                               System.arraycopy(raw,keyStart,bytes,0,keyLength);
                               System.out.println("Add to CIDEncodings and as String");
                               System.out.println("key="+new String(bytes)+ ' ' +id+" not implemented in setConstant in PdfFont Object");

                               System.out.println("final public static int CMAP_"+new String(bytes)+ '=' +id+ ';');

                		}
                	}else {
                        PDFvalue = super.setConstant(pdfKeyType, id);
                    }

                    if(PDFvalue==-1 && debug){

                        	 final byte[] bytes=new byte[keyLength];

                            System.arraycopy(raw,keyStart,bytes,0,keyLength);
                            System.out.println("key="+new String(bytes)+ ' ' +id+" not implemented in setConstant in PdfFont Object");

                            System.out.println("final public static int "+new String(bytes)+ '=' +id+ ';');

                        }

                    break;

            }

        }catch(final Exception e){
            LogWriter.writeLog("Exception: " + e.getMessage());
        }

        //System.out.println(pdfKeyType+"="+PDFvalue);
        switch(pdfKeyType){

        	case PdfDictionary.BaseEncoding:
        		BaseEncoding=PDFvalue;
        		break;

			case PdfDictionary.CIDToGIDMap:
				CIDToGIDMapAsConstant = PDFvalue;
				break;

	        case PdfDictionary.Encoding:
	        	generalType=PDFvalue;
	            break;

//            case PdfDictionary.Subtype:
//                subtype=PDFvalue;
//                //System.out.println("value set to "+subtype);
//                break;
            case PdfDictionary.ToUnicode:
                generalType=PDFvalue;
                break;
            default:
            	super.setConstant(pdfKeyType, PDFvalue);
        }

        return PDFvalue;
    }

    @Override
    public int getParameterConstant(final int key) {

        final int def;

        switch(key){


            case PdfDictionary.BaseEncoding:

            	//special cases first
            	if(key==PdfDictionary.BaseEncoding && Encoding!=null && Encoding.isZapfDingbats) {
                    return StandardFonts.ZAPF;
                } else if(key==PdfDictionary.BaseEncoding && Encoding!=null && Encoding.isSymbol) {
                    return StandardFonts.SYMBOL;
                } else {
                    return BaseEncoding;
                }

			case PdfDictionary.CIDToGIDMap:
				return CIDToGIDMapAsConstant;
        }

        //check general values
        def=super.getParameterConstant(key);

        return def;
    }

    @Override
    public PdfArrayIterator getMixedArray(final int id) {

    	switch(id){

            case PdfDictionary.Differences:
                return new PdfArrayIterator(Differences);

            default:
            	return super.getMixedArray(id);

        }
	}

    @Override
    public byte[][] getByteArray(final int id){

    	switch(id){

    	case PdfDictionary.Differences:
	        return Differences;

        default:
        	return super.getByteArray(id);

    	}

    }

    @Override
    public double[] getDoubleArray(final int id) {

        switch(id){

            case PdfDictionary.FontMatrix:
                return deepCopy(FontMatrix);

            default:
            	return super.getDoubleArray(id);
        }

    }

    @Override
    public void setDoubleArray(final int id, final double[] value) {

        switch(id){

            case PdfDictionary.FontMatrix:
                FontMatrix=value;
            break;

            default:
            	super.setDoubleArray(id, value);
        }

    }

    @Override
    public void setMixedArray(final int id, final byte[][] value) {

        switch(id){

            case PdfDictionary.Differences:
                Differences=value;
            break;

            default:
            	super.setMixedArray(id, value);

        }

    }

    @Override
    public void setIntArray(final int id, final int[] value) {

        switch(id){

            case PdfDictionary.DW2:
                DW2=value;
                break;

            default:
                super.setIntArray(id, value);
        }
    }

    @Override
    public int[] getIntArray(final int id) {

        switch(id){

            case PdfDictionary.DW2:
                return deepCopy(DW2);

            default:
                return super.getIntArray(id);
        }
    }

    @Override
    public float[] getFloatArray(final int id) {

        switch(id){

        	case PdfDictionary.FontBBox:
        		return deepCopy(FontBBox);

            case PdfDictionary.Widths:
                return deepCopy(Widths);

            default:
                return super.getFloatArray(id);
        }
    }

    @Override
    public void setFloatArray(final int id, final float[] value) {

        switch(id){

	        case PdfDictionary.FontBBox:
	            FontBBox=value;
	        break;

            case PdfDictionary.Widths:
                Widths=value;
            break;

            default:
            	super.setFloatArray(id, value);
        }
    }

    @Override
    public void setName(final int id, final byte[] value) {

        switch(id){

            case PdfDictionary.BaseFont:
                rawBaseFont=value;

                //track if font called ZapfDingbats and flag
                final int checksum = PdfDictionary.generateChecksum(0, value.length, value);

                isZapfDingbats=(checksum==ZapfDingbats || checksum==ZaDb);
                isSymbol=(checksum==Symbol);

                //store in both as we can't guarantee creation order
                if(Encoding!=null){
                	Encoding.isZapfDingbats=isZapfDingbats;
                	Encoding.isSymbol=isSymbol;
                }

              break;

            case PdfDictionary.CMapName:
                rawCMapName=value;
            break;

            case PdfDictionary.FontName:
                rawFontName=value;
            break;

            case PdfDictionary.FontStretch:
                rawFontStretch=value;
            break;


            default:
                super.setName(id,value);

        }

    }

    @Override
    public void setTextStreamValue(final int id, final byte[] value) {

        switch(id){

	        case PdfDictionary.CharSet:
	            rawCharSet=value;
	        break;

            case PdfDictionary.Ordering:
                rawOrdering=value;
            break;

            case PdfDictionary.Registry:
                rawRegistry=value;
            break;

            default:
                super.setTextStreamValue(id,value);

        }

    }

    @Override
    public String getName(final int id) {

        switch(id){

            case PdfDictionary.BaseFont:

            //setup first time
            if(BaseFont==null && rawBaseFont!=null) {
                BaseFont = new String(rawBaseFont);
            }

            return BaseFont;

            case PdfDictionary.CMapName:

                //setup first time
                if(CMapName==null && rawCMapName!=null) {
                    CMapName = new String(rawCMapName);
                }

                return CMapName;

            case PdfDictionary.FontName:

            //setup first time
            if(FontName==null && rawFontName!=null) {
                FontName = new String(rawFontName);
            }

            return FontName;

            case PdfDictionary.FontStretch:
            	//setup first time
                if(FontStretch==null && rawFontStretch!=null) {
                    FontStretch = new String(rawFontStretch);
                }

                return FontStretch;

            default:
                return super.getName(id);

        }
    }

    @Override
    public String getTextStreamValue(final int id) {

        switch(id){

	        case PdfDictionary.CharSet:

	            //setup first time
	            if(CharSet==null && rawCharSet!=null) {
                    CharSet = new String(rawCharSet);
                }

	            return CharSet;

            case PdfDictionary.Ordering:

                //setup first time
                if(Ordering==null && rawOrdering!=null) {
                    Ordering = new String(rawOrdering);
                }

                return Ordering;

            case PdfDictionary.Registry:

                //setup first time
                if(Registry==null && rawRegistry!=null) {
                    Registry = new String(rawRegistry);
                }

                return Registry;

            default:
                return super.getTextStreamValue(id);

        }
    }

    /**
     * unless you need special fucntions,
     * use getStringValue(int id) which is faster
     */
    @Override
    public String getStringValue(final int id, final int mode) {

        byte[] data=null;

        //get data
        switch(id){

            case PdfDictionary.BaseFont:
                data=rawBaseFont;
                break;

            case PdfDictionary.CMapName:
                data=rawCMapName;
                break;

            case PdfDictionary.FontName:
                data=rawFontName;
                break;

            case PdfDictionary.FontStretch:
                data=rawFontStretch;
                break;
        }

        //convert
        switch(mode){
            case PdfDictionary.STANDARD:

                //setup first time
                if(data!=null) {
                    return new String(data);
                } else {
                    return null;
                }


            case PdfDictionary.LOWERCASE:

                //setup first time
                if(data!=null) {
                    return new String(data);
                } else {
                    return null;
                }

            case PdfDictionary.REMOVEPOSTSCRIPTPREFIX:

                //setup first time
                if(data!=null){
                	final int len=data.length;
                    if(len>6 && data[6]=='+'){ //lose ABCDEF+ if present
                		final int length=len-7;
                		final byte[] newData=new byte[length];
                		System.arraycopy(data, 7, newData, 0, length);
                		return new String(newData);
                    }else if(len>7 && data[len-7]=='+'){ //lose +ABCDEF if present
                        final int length=len-7;
                        final byte[] newData=new byte[length];
                        System.arraycopy(data, 0, newData, 0, length);
                        return new String(newData);
                    }else {
                        return new String(data);
                    }
                }else {
                    return null;
                }

            default:
                throw new RuntimeException("Value not defined in getName(int,mode)");
        }
    }

    @Override
    public int getObjectType() {
		return PdfDictionary.Font;
	}


    @Override
    public boolean decompressStreamWhenRead() {
		return true;
	}
   
    @Override
    public Object[] getObjectArray(final int id) {
        
        switch(id){
            
            case PdfDictionary.W:
                return W;                
                
            case PdfDictionary.W2:
                return W2; 
                
            default:    
                return super.getObjectArray(id);
        }
        
    }
    
    @Override
    public void setObjectArray(final int id, final Object[] objectValues) {
        
        switch(id){
            
            case PdfDictionary.W:
                
                W= objectValues;
                break;
                
            case PdfDictionary.W2:
                
                W2= objectValues;
                break;    
                
            default:
                super.setObjectArray(id, objectValues);
        }
    }
}
