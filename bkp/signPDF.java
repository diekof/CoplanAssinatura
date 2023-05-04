package com.coplan.coplanassinatura;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

public class signPDF {


    public static void main(String[] args) throws DocumentException, IOException, GeneralSecurityException {

        PdfReader reader = null;
        PrivateKey pk = null;
        String alias = "C:\\temp\\pdftestenovo.crt";

        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
        }
        catch (KeyStoreException|java.security.NoSuchProviderException e4){
            e4.printStackTrace();
        }
        try {
            ks.load(null, null);
        }
        catch (NoSuchAlgorithmException|java.security.cert.CertificateException|IOException e4){
            e4.printStackTrace();
        }

        try {
            pk = (PrivateKey)ks.getKey(alias, "abcd".toCharArray());
        }

        catch (UnrecoverableKeyException|KeyStoreException|NoSuchAlgorithmException e3){
            e3.printStackTrace();
        }
        Certificate[] chain = null;
        //chain = ks.getCertificateChain(alias);
        String cert = "MIIGaTCCBFGgAwIBAgIIVircB9XQwxkwDQYJKoZIhvcNAQELBQAwgbQxCzAJBgNV\n" +
                "BAYTAkJSMQ8wDQYDVQQKDAZHb3YtQnIxSTBHBgNVBAsMQEF1dG9yaWRhZGUgQ2Vy\n" +
                "dGlmaWNhZG9yYSAxTml2ZWwgZG8gR292ZXJubyBGZWRlcmFsIGRvIEJyYXNpbCBI\n" +
                "T00xSTBHBgNVBAMMQEF1dG9yaWRhZGUgQ2VydGlmaWNhZG9yYSAyTml2ZWwgZG8g\n" +
                "R292ZXJubyBGZWRlcmFsIGRvIEJyYXNpbCBIT00wHhcNMjMwMzAzMTgzMDQ5WhcN\n" +
                "MjQwMzAyMTgzMDQ5WjATMREwDwYDVQQDDAhNZXUgTm9tZTCCASIwDQYJKoZIhvcN\n" +
                "AQEBBQADggEPADCCAQoCggEBALNeGYrZhfcUUVJ8lPqopfe8R9NWhdhynaAdg1a/\n" +
                "I4BpMaFP47i3rLuWb0QPivBmPlZeatyI13OOZwuXRBGGhxFiR4vZxWfNn2PcyHUt\n" +
                "t0zvz4/4KZSw5cK7BPZGKtRlL9+3gcAfbiGqx7cNxO6AcgHdUL/zc+Whd+BkmVzF\n" +
                "bcdQbP0RXJToN0IVEDaLJMfEy8+IonQ9/WIHDYnIP+0LWI3l7F8fdJl4G3iVgK5n\n" +
                "fM/heaLXtBlcxrdaWoaPMDJsQiwGPpART59wEfyq2jUsIYVAFDpgNSdcOclyHGtC\n" +
                "Mqyk+gwFGmmd1SBBjYdISk+An42yjDuhhVcJ1YK2dy8cIHsCAwEAAaOCAh0wggIZ\n" +
                "MIGuBgNVHREEgaYwgaOgOAYFYEwBAwGgLwQtMDEwMTE5ODAwMjMxMjcxMjEyMTAw\n" +
                "MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwoBcGBWBMAQMGoA4EDDAwMDAwMDAwMDAw\n" +
                "MKAeBgVgTAEDBaAVBBMwMDAwMDAwMDAwMDAwMDAwMDAwoBMGBWBMAQQDoAoECE1l\n" +
                "dSBOb21lgRlkZG9uaXpldGVjb3JyZWFAZ21haWwuY29tMAkGA1UdEwQCMAAwHwYD\n" +
                "VR0jBBgwFoAUSJhkNiHyZd6Vt0oT6iTZXFrDdMwwUgYDVR0gBEswSTBHBgZgTAMC\n" +
                "AQEwPTA7BggrBgEFBQcCARYvaHR0cDovL3JlcG8uaXRpLmJyL2RvY3MvRFBDYWMy\n" +
                "bml2ZWxHb3ZCckhPTS5wZGYwSgYDVR0fBEMwQTA/oD2gO4Y5aHR0cDovL3JlcG8u\n" +
                "aXRpLmJyL2xjci9ob20vcHVibGljL2FjZi9MQ1JhY2ZHb3ZCci1Ib20uY3JsMA4G\n" +
                "A1UdDwEB/wQEAwIHgDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwHQYD\n" +
                "VR0OBBYEFEb5Vrk5MNqOUZjNQoUPwobAxIYYMEwGCCsGAQUFBwEBBEAwPjA8Bggr\n" +
                "BgEFBQcwAoYwaHR0cDovL3JlcG8uaXRpLmJyL2RvY3MvQ2FkZWlhX0dvdkJyLWRl\n" +
                "ci1ob20ucDdiMA0GCSqGSIb3DQEBCwUAA4ICAQBBENHZsAtOHEm/CFV4nC+N6iwX\n" +
                "PDToVw7UZnTE6m4d718PLqjVMNPd47vwy4zYBvgwRIaVO17H8Nv8724ZaR6UJdMm\n" +
                "Jk6yhxPiOgJrhEEarjslT2xPvg/6b+yy62YMS2gp+5gQ5A5pzo359WDxJ3IkmRKJ\n" +
                "dVsZW2/DG3Uw4v0+8jbQC21NjyFLRQ07X0DqnDAq0Pr+QoWnowZRhzpof4RQtrRF\n" +
                "pRV0DG/hj27tiFcjdH613qr/vcUQwi/w4HieMnVrZsXxlsw3rCKMs52zmlxcQRnL\n" +
                "Ah2KAZmBJcZpzF9+ODoziIT+aRnf6RVoQPhFbcB6hwKjaov5GPPyRUNW95cCUOyM\n" +
                "ihzJV7rsQhXlIAcutVj+uM3A3YEn4bAZaqnHS3GgxtY2vCFraPvG8wvZZduunQWK\n" +
                "DrBZsALL95+02zzo4a2NoVXw07ksjikx37Dan/beffFGK/a9RGzrQNoo7usK9g2n\n" +
                "Huu0mIL4dFUYokmkZQWlEZH+FHPc0/FyGJ2MjhzXhbr/UwDrBPKIbuC9Nn9HgiM6\n" +
                "yiGeNGGrU9WZhTvkT2j+NmuWfVVNJ9DqexvtLqsDLNW1U7cuarwQntaGPt9a8KGC\n" +
                "QnDp/dnw3xtTCqw3bcn4WUcE5Y7LGKLyckU/Go2qTU7EZ1dznT7GU0KSvBebMYBX\n" +
                "oP6GAjwIMmLIdzqUPg=="; // the cert we get from client
        ByteArrayInputStream userCertificate = new ByteArrayInputStream(Base64.decodeBase64(cert));

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        chain = new Certificate[]{cf.generateCertificate(userCertificate)};


        try {
            reader = new PdfReader("C:\\Des\\Tomcat 9.0\\webapps\\central.war\\PublicTempStorage\\voucher-905_821600f69367423b9d5755987f93aae95884-910a-42d9-9763-690bf66f9a55.pdf");
            BufferedReader br = null;
        }
        catch (IOException e5){
            e5.printStackTrace();
        }
        String signedFileNameWithPath = "C:\\temp\\assinatura\\pdf-destino-2022.pdf";
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(signedFileNameWithPath);
        }
        catch (FileNotFoundException e5){
            e5.printStackTrace();
        }
        PdfStamper stamper = null;


        try {
            stamper = PdfStamper.createSignature(reader, os, '\0', null, true);
        }
        catch (DocumentException|IOException e5) {
            e5.printStackTrace();
        }


//        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
//        Integer pageNumber = 2;
//        Rectangle rect=new Rectangle(50,100,220,140);
//        appearance.setAcro6Layers(false);
//        appearance.setLayer4Text(PdfSignatureAppearance.questionMark);
//        appearance.setVisibleSignature(rect,pageNumber, "sig2");

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature(new Rectangle(100, 250, 288, 426), 1, "Signature");
        appearance.setSignDate(Calendar.getInstance());
        appearance.setLayer4Text(PdfSignatureAppearance.questionMark);
        appearance.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
        appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.DESCRIPTION);

        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, new PdfName("adbe.pkcs7.detached"));
        dic.setReason(appearance.getReason());
        dic.setLocation(appearance.getLocation());
        dic.setContact(appearance.getContact());
        dic.setDate(new PdfDate(appearance.getSignDate()));
        appearance.setCryptoDictionary(dic);


        int estimatedSize = 8192;
        HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.CONTENTS, new Integer((estimatedSize << 1) + 2));
//        appearance.preClose(exc);


        PrivateKeySignature privateKeySignature=null;
        try {
            privateKeySignature= new PrivateKeySignature(pk, "SHA-256", ks.getProvider().getName());
        }
        catch (NullPointerException e) {
        }
//        if(privateKeySignature!=null) {
            BouncyCastleDigest bouncyCastleDigest = new BouncyCastleDigest();
            try {
//                PdfSignatureAppearance sap, ExternalSignatureContainer externalSignatureContainer, int estimatedSize

//                com.itextpdf.text.pdf.security.ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE,
//                        PdfName.ADBE_PKCS7_DETACHED);
//                String signature = "123456";  // signed hash signature we get from client
//                byte[] signatureBytes = Hex.decodeHex(signature.toCharArray());
//                PdfPKCS7 sgn = new PdfPKCS7(pk, chain, "SHA256", null, null, false);
//                sgn.setExternalDigest(signatureBytes, null, "RSA");

//                InputStream fileAppearance = appearance.getRangeStream();
//                byte[] bytes1 = IOUtils.toByteArray(fileAppearance);
//                byte[] hash = DigestUtils.sha256(bytes1);
//                System.out.println(hash);
//                byte[] encodedSig = sgn.getEncodedPKCS7(hash, getTsa(), getOscp(chain), null, CryptoStandard.CMS);
//                byte[] encodedSig = sgn.getEncodedPKCS7(hash, tsc, ocsp, null, CryptoStandard.CMS);
//                ExternalSignatureContainer external = new MklTest.MyExternalSignatureContainer(encodedSig);
//                ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE,
//                        PdfName.ADBE_PKCS7_DETACHED);

                class MyExternalSignatureContainer implements ExternalSignatureContainer {
                    protected final byte[] sig;
                    public MyExternalSignatureContainer(byte[] sig) {
                        this.sig = sig;
                    }
                    public byte[] sign(InputStream is) {
                        return sig;
                    }

                    @Override
                    public void modifySigningDictionary(PdfDictionary signDic) {
                    }
                }
//                ExternalSignatureContainer external = new MyExternalSignatureContainer(encodedSig);

                ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE,
                        PdfName.ADBE_PKCS7_DETACHED);

                MakeSignature.signExternalContainer(appearance,external,10192);
//                appearance.preClose(exc);
//                MakeSignature.signDetached(appearance, (ExternalDigest)bouncyCastleDigest, (ExternalSignature)privateKeySignature, chain, null, null, null, 0, MakeSignature.CryptoStandard.CMS);
            }
            catch (IOException e1){
                e1.printStackTrace();
            }
            catch (DocumentException e1){
                e1.printStackTrace();
            }
            catch (SignatureException e1) {
                e1.printStackTrace();
            }
            catch (GeneralSecurityException e1){
                e1.printStackTrace();
            }
//        }

    }

    public static TSAClient getTsa() {
        return new TSAClientBouncyCastle("http://timestamp.digicert.com", null, null, 4096, "SHA-512");
    }
    public static byte[] getOscp(Certificate[] chain) {
        byte[] ocsp = null;
        OcspClient ocspClient = new OcspClientBouncyCastle(new OCSPVerifier(null, null));

        if (chain.length >= 2) {
            ocsp = ocspClient.getEncoded((X509Certificate)chain[0], (X509Certificate)chain[1], null);
        }

        return ocsp;
    }
}