package com.springrain.erp;
/**
 *
 * @author Chen Yi <njchenyi@gmail.com>
 */
import java.io.File;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.springrain.erp.common.utils.pdf.PdfUtil;
 
public class Barcodes {
 
    /** The resulting PDF. */
    private static final String RESULT = "d:/barcodes.pdf";
 
    /**
     * Generates a PDF file with different types of barcodes.
     * 
     * @param args
     *            no arguments needed here
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException,
            DocumentException {
        PdfUtil.createBarCodePdf("2",new File(RESULT),"6957599306331","Insdsadsadsad","New");
    }
    
 
}