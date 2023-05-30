package com.coplan.coplanassinatura;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationRubberStamp;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

import javax.imageio.ImageIO;

public class CoplanAssinatura {

    public static String NomeAssinador  = "Gextec - Gextec Tecnologia";
    public static String Localizacao    = "Central";
    public static String txtReason      = "CPF";
    public static Boolean isCarimbo     = true;
    public static String UrlLogoGovbr   = "https://www.gov.br/++theme++padrao_govbr/img/govbr-colorido-b.png";
    public static Boolean isAssinarTodasPaginas = false;
    public static int fontSize = 4;
    public static String textoPadraoAssinatura = "Documento Assinado Digitalmente";
    public static PDFont fontNegrito = PDType1Font.HELVETICA_BOLD;
    public static int NumeroAssinatura = 0;
    public static int tipoAssinaura =1; //1 - govbr; 2 - manual
    
    public static void geraAssinaturaDocumento(String caminhoPdf, String caminhoAssinado) throws IOException{

        FileInputStream signedFileInputStream = new FileInputStream(new File(caminhoPdf));
        
        //Criar um dicionário de assinatura:
        PDDocument pdfDocument = PDDocument.load(new File(caminhoPdf));

        //Criar um dicionário de assinatura:
        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName(NomeAssinador);
        signature.setLocation(Localizacao);
        signature.setReason(txtReason);
        signature.setSignDate(Calendar.getInstance());
        
        if(isCarimbo){

            if(isAssinarTodasPaginas){
                for (PDPage pagina : pdfDocument.getPages()) {
                    desenhaCarimbo(pdfDocument,pagina);
                    //pagina.getAnnotations().add(desenhaCarimbo(pdfDocument,pagina));
                }
            }else{
                int numeroPagina = pdfDocument.getNumberOfPages()-1;
                PDPage pagina = pdfDocument.getPage(numeroPagina);
                desenhaCarimbo(pdfDocument,pagina);
            }

        }
        
        //Adicionar o dicionário de assinatura ao documento:
        pdfDocument.addSignature(signature);
        FileOutputStream  fos = new FileOutputStream(new File(caminhoAssinado));
        pdfDocument.save(fos);
        pdfDocument.close();

    }

    private static void desenhaCarimbo(PDDocument pdfDocument, PDPage pagina) throws IOException {

        float larguraPagina = pagina.getMediaBox().getWidth();
        float alturaPagina = pagina.getMediaBox().getHeight();

        float larguraCarimbo = 50; // largura do carimbo em pontos
        float alturaCarimbo = 50; // altura do carimbo em pontos

        float x = 50+larguraCarimbo;//(larguraPagina - larguraCarimbo - 50)*NumeroAssinatura; // 20 pontos de margem direita
        if (NumeroAssinatura>0){
            x = x+(110*NumeroAssinatura);
        }
         
        float y = 20; // 20 pontos de margem inferior

        PDRectangle retanguloDoCarimbo = new PDRectangle(larguraCarimbo,alturaCarimbo);
        retanguloDoCarimbo.setLowerLeftX(x);
        retanguloDoCarimbo.setLowerLeftY(y);

        PDAnnotationRubberStamp carimbo = new PDAnnotationRubberStamp();
        carimbo.setName(NomeAssinador);
        carimbo.setRectangle(retanguloDoCarimbo);

        if(tipoAssinaura==1) {

            //desenhando a logo.
            URL url = new URL(UrlLogoGovbr);
            BufferedImage img = ImageIO.read(url);
            File file = new File("govbr.png");
            ImageIO.write(img, "png", file);
            PDImageXObject simboloGovBR = PDImageXObject.createFromFile(file.getAbsolutePath(), pdfDocument);

            if (!simboloGovBR.isEmpty()) {

                SimpleDateFormat format_ = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss - zzz");

                PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, pagina, PDPageContentStream.AppendMode.APPEND, true, true);
                contentStream.drawImage(simboloGovBR, x - 45, y + 10, 30, 11);

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, fontSize - 2);
                contentStream.newLineAtOffset(x - 10, y + 20);
                contentStream.showText(textoPadraoAssinatura);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, fontSize - 1);
                contentStream.newLineAtOffset(x - 10, y + 10);
                contentStream.showText(txtReason);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(fontNegrito, fontSize);
                contentStream.newLineAtOffset(x - 10, y + 15);
                contentStream.showText(NomeAssinador);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, fontSize - 1);
                contentStream.newLineAtOffset(x - 10, y + 5);
                contentStream.showText("Data: " + format_.format(Calendar.getInstance().getTime()));
                contentStream.endText();

                contentStream.close();
            }
        }else{
            SimpleDateFormat format_ = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss - zzz");

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, pagina, PDPageContentStream.AppendMode.APPEND, true, true);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize - 2);
            contentStream.newLineAtOffset(x - 10, y + 20);
            contentStream.showText(textoPadraoAssinatura);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize - 1);
            contentStream.newLineAtOffset(x - 10, y + 10);
            contentStream.showText(txtReason);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(fontNegrito, fontSize);
            contentStream.newLineAtOffset(x - 10, y + 15);
            contentStream.showText(NomeAssinador);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, fontSize - 1);
            contentStream.newLineAtOffset(x - 10, y + 5);
            contentStream.showText("Data: " + format_.format(Calendar.getInstance().getTime()));
            contentStream.endText();

            contentStream.close();
        }
    }
    
    public static String getSha256Assinatura(String caminho) throws IOException, EncoderException {

        //Carregar o arquivo PDF e criar um objeto PDDocument:
        File pdfFile = new File(caminho);
        PDDocument pdfDocument = PDDocument.load(pdfFile);
        //Obter a última assinatura adicionada ao documento:
        PDSignature signature = pdfDocument.getLastSignatureDictionary();
        //Obter o intervalo de bytes do documento que será usado no cálculo do hash:
        int[] byteRangeArray = signature.getByteRange();
        long byteRangeOffset = byteRangeArray[0];
        long byteRangeLength = byteRangeArray[2] - byteRangeArray[1];
        //Ler os bytes do documento correspondentes ao intervalo definido:
        RandomAccessFile raf = new RandomAccessFile(pdfFile, "r");
        byte[] bytes = new byte[(int)byteRangeLength];
        raf.seek(byteRangeOffset);
        raf.readFully(bytes);
        raf.close();


        org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
        String sha256hex = new String(base64.encode(org.apache.commons.codec.digest.DigestUtils.sha256(bytes)));

        System.out.println(sha256hex);
        return sha256hex;

    }
    
    public static void assinarDocumento(String caminhoAssinado,String hexadecimal) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        FileInputStream fis = new FileInputStream(new File(caminhoAssinado));
        byte[] content = IOUtils.toByteArray(fis);
        fis.close();

        PDDocument document = PDDocument.load(new File(caminhoAssinado));
        PDSignature signature = document.getLastSignatureDictionary();
        List<PDSignatureField> signatureField = document.getSignatureFields();
        
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hex = digest.digest(hexadecimal.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        signature.setContents(hex);
        for (PDSignatureField s : signatureField) {
            
            s.setValue(signature);
            //s.setValue(hex.toString());
        }

        FileOutputStream fos = new FileOutputStream(caminhoAssinado);
        document.saveIncremental(fos);
        document.close();

    }

}
