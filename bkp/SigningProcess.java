package com.coplan.coplanassinatura;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.CertificateUtil;
import com.itextpdf.text.pdf.security.CrlClient;
import com.itextpdf.text.pdf.security.CrlClientOnline;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.OcspClient;
import com.itextpdf.text.pdf.security.OcspClientBouncyCastle;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import com.itextpdf.text.pdf.security.TSAClient;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

//import sun.security.mscapi.SunMSCAPI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public class SigningProcess {
    static KeyStore ks;
//    static SunMSCAPI providerMSCAPI;
    static X509Certificate[] certificateChain = null;
    static Key privateKey = null;
    static String alias;
    static HashMap returnCertificates;

//    public static HashMap returnCertificates() {
//        HashMap map = new HashMap();
//        try {
////            providerMSCAPI = new SunMSCAPI();
////            Security.addProvider(providerMSCAPI);
////            ks = KeyStore.getInstance("Windows-MY");
//            ks = KeyStore.getInstance("Windows-MY", "SunMSCAPI");
//            ks.load(null, null);
//            Field spiField = KeyStore.class.getDeclaredField("keyStoreSpi");
//            spiField.setAccessible(true);
//            KeyStoreSpi spi = (KeyStoreSpi) spiField.get(ks);
//            Field entriesField = spi.getClass().getSuperclass().getDeclaredField("entries");
//            entriesField.setAccessible(true);
//            Collection entries = (Collection) entriesField.get(spi);
//            for (Object entry : entries) {
//                alias = (String) invokeGetter(entry, "getAlias");
//                //                System.out.println("alias :" + alias);
//                privateKey = (Key) invokeGetter(entry, "getPrivateKey");
//                certificateChain = (X509Certificate[]) invokeGetter(entry, "getCertificateChain");
//                //                System.out.println(alias + ": " + privateKey + "CERTIFICATES -----------"+Arrays.toString(certificateChain));
//            }
//            map.put("privateKey", privateKey);
//            map.put("certificateChain", certificateChain);
//
//        } catch (KeyStoreException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (IOException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (NoSuchAlgorithmException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (CertificateException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (NoSuchFieldException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (SecurityException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (IllegalArgumentException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (IllegalAccessException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (NoSuchMethodException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (InvocationTargetException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (NoSuchProviderException e) {
//            throw new RuntimeException(e);
//        }
//        return map;
//    }
//
//    private static Object invokeGetter(Object instance, String methodName)
//            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        Method getAlias = instance.getClass().getDeclaredMethod(methodName);
//        getAlias.setAccessible(true);
//        return getAlias.invoke(instance);
//    }
//
//    public static String sign(String base64, HashMap map) {
//        String base64string = null;
//        try {
//            System.out.println("map :" + map);
//            // Getting a set of the entries
//            Set set = map.entrySet();
//            System.out.println("set :" + set);
//            // Get an iterator
//            Iterator it = set.iterator();
//            // Display elements
//            while (it.hasNext()) {
//                Entry me = (Entry) it.next();
//                String key = (String) me.getKey();
//                if ("privateKey".equalsIgnoreCase(key)) {
//                    privateKey = (PrivateKey) me.getValue();
//                }
//                if ("certificateChain".equalsIgnoreCase(key)) {
//                    certificateChain = (X509Certificate[]) me.getValue();
//                }
//            }
//
//            OcspClient ocspClient = new OcspClientBouncyCastle();
//            TSAClient tsaClient = null;
//            for (int i = 0; i < certificateChain.length; i++) {
//                X509Certificate cert = (X509Certificate) certificateChain[i];
//                String tsaUrl = CertificateUtil.getTSAURL(cert);
//                if (tsaUrl != null) {
//                    tsaClient = new TSAClientBouncyCastle(tsaUrl);
//                    break;
//                }
//            }
//            List<CrlClient> crlList = new ArrayList<CrlClient>();
//            crlList.add(new CrlClientOnline(certificateChain));
//
//            String property = System.getProperty("java.io.tmpdir");
////            ByteArrayInputStream userCertificate = new ByteArrayInputStream(Base64.decodeBase64(base64));
////            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] FileByte = Base64.decodeBase64(base64);
//            writeByteArraysToFile(property + "_unsigned.pdf", FileByte);
//
//            // Creating the reader and the stamper
//            PdfReader reader = new PdfReader(property + "_unsigned.pdf");
//            FileOutputStream os = new FileOutputStream(property + "_signed.pdf");
//            PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
//            // Creating the appearance
//            PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
//            //            appearance.setReason(reason);
//            //            appearance.setLocation(location);
//            appearance.setAcro6Layers(false);
//            appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig1");
//            // Creating the signature
////            ExternalSignature pks = new PrivateKeySignature((PrivateKey) privateKey, DigestAlgorithms.SHA256, ks.getName());
//            ExternalDigest digest = new BouncyCastleDigest();
//            MakeSignature.signDetached(appearance, digest, null, certificateChain, crlList, ocspClient, tsaClient, 0,
//                    MakeSignature.CryptoStandard.CMS);
//
//            InputStream docStream = new FileInputStream(property + "_signed.pdf");
//            byte[] encodeBase64 = Base64.encodeBase64(IOUtils.toByteArray(docStream));
//            base64string = new String(encodeBase64);
//        } catch (IOException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (DocumentException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        } catch (GeneralSecurityException ex) {
//            System.out.println("Exception :" + ex.getLocalizedMessage());
//        }
//        return base64string;
//    }
//
//    public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {
//        File file = new File(fileName);
//        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
//        writer.write(content);
//        writer.flush();
//        writer.close();
//
//    }
//
//    public static void main(String[] args) {
//        returnCertificates = returnCertificates();
//        System.out.println("returnCertificates :" + returnCertificates.get("privateKey"));
//        String base64 = "MIIGaTCCBFGgAwIBAgIIVircB9XQwxkwDQYJKoZIhvcNAQELBQAwgbQxCzAJBgNV\n" +
//                "BAYTAkJSMQ8wDQYDVQQKDAZHb3YtQnIxSTBHBgNVBAsMQEF1dG9yaWRhZGUgQ2Vy\n" +
//                "dGlmaWNhZG9yYSAxTml2ZWwgZG8gR292ZXJubyBGZWRlcmFsIGRvIEJyYXNpbCBI\n" +
//                "T00xSTBHBgNVBAMMQEF1dG9yaWRhZGUgQ2VydGlmaWNhZG9yYSAyTml2ZWwgZG8g\n" +
//                "R292ZXJubyBGZWRlcmFsIGRvIEJyYXNpbCBIT00wHhcNMjMwMzAzMTgzMDQ5WhcN\n" +
//                "MjQwMzAyMTgzMDQ5WjATMREwDwYDVQQDDAhNZXUgTm9tZTCCASIwDQYJKoZIhvcN\n" +
//                "AQEBBQADggEPADCCAQoCggEBALNeGYrZhfcUUVJ8lPqopfe8R9NWhdhynaAdg1a/\n" +
//                "I4BpMaFP47i3rLuWb0QPivBmPlZeatyI13OOZwuXRBGGhxFiR4vZxWfNn2PcyHUt\n" +
//                "t0zvz4/4KZSw5cK7BPZGKtRlL9+3gcAfbiGqx7cNxO6AcgHdUL/zc+Whd+BkmVzF\n" +
//                "bcdQbP0RXJToN0IVEDaLJMfEy8+IonQ9/WIHDYnIP+0LWI3l7F8fdJl4G3iVgK5n\n" +
//                "fM/heaLXtBlcxrdaWoaPMDJsQiwGPpART59wEfyq2jUsIYVAFDpgNSdcOclyHGtC\n" +
//                "Mqyk+gwFGmmd1SBBjYdISk+An42yjDuhhVcJ1YK2dy8cIHsCAwEAAaOCAh0wggIZ\n" +
//                "MIGuBgNVHREEgaYwgaOgOAYFYEwBAwGgLwQtMDEwMTE5ODAwMjMxMjcxMjEyMTAw\n" +
//                "MDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwoBcGBWBMAQMGoA4EDDAwMDAwMDAwMDAw\n" +
//                "MKAeBgVgTAEDBaAVBBMwMDAwMDAwMDAwMDAwMDAwMDAwoBMGBWBMAQQDoAoECE1l\n" +
//                "dSBOb21lgRlkZG9uaXpldGVjb3JyZWFAZ21haWwuY29tMAkGA1UdEwQCMAAwHwYD\n" +
//                "VR0jBBgwFoAUSJhkNiHyZd6Vt0oT6iTZXFrDdMwwUgYDVR0gBEswSTBHBgZgTAMC\n" +
//                "AQEwPTA7BggrBgEFBQcCARYvaHR0cDovL3JlcG8uaXRpLmJyL2RvY3MvRFBDYWMy\n" +
//                "bml2ZWxHb3ZCckhPTS5wZGYwSgYDVR0fBEMwQTA/oD2gO4Y5aHR0cDovL3JlcG8u\n" +
//                "aXRpLmJyL2xjci9ob20vcHVibGljL2FjZi9MQ1JhY2ZHb3ZCci1Ib20uY3JsMA4G\n" +
//                "A1UdDwEB/wQEAwIHgDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwHQYD\n" +
//                "VR0OBBYEFEb5Vrk5MNqOUZjNQoUPwobAxIYYMEwGCCsGAQUFBwEBBEAwPjA8Bggr\n" +
//                "BgEFBQcwAoYwaHR0cDovL3JlcG8uaXRpLmJyL2RvY3MvQ2FkZWlhX0dvdkJyLWRl\n" +
//                "ci1ob20ucDdiMA0GCSqGSIb3DQEBCwUAA4ICAQBBENHZsAtOHEm/CFV4nC+N6iwX\n" +
//                "PDToVw7UZnTE6m4d718PLqjVMNPd47vwy4zYBvgwRIaVO17H8Nv8724ZaR6UJdMm\n" +
//                "Jk6yhxPiOgJrhEEarjslT2xPvg/6b+yy62YMS2gp+5gQ5A5pzo359WDxJ3IkmRKJ\n" +
//                "dVsZW2/DG3Uw4v0+8jbQC21NjyFLRQ07X0DqnDAq0Pr+QoWnowZRhzpof4RQtrRF\n" +
//                "pRV0DG/hj27tiFcjdH613qr/vcUQwi/w4HieMnVrZsXxlsw3rCKMs52zmlxcQRnL\n" +
//                "Ah2KAZmBJcZpzF9+ODoziIT+aRnf6RVoQPhFbcB6hwKjaov5GPPyRUNW95cCUOyM\n" +
//                "ihzJV7rsQhXlIAcutVj+uM3A3YEn4bAZaqnHS3GgxtY2vCFraPvG8wvZZduunQWK\n" +
//                "DrBZsALL95+02zzo4a2NoVXw07ksjikx37Dan/beffFGK/a9RGzrQNoo7usK9g2n\n" +
//                "Huu0mIL4dFUYokmkZQWlEZH+FHPc0/FyGJ2MjhzXhbr/UwDrBPKIbuC9Nn9HgiM6\n" +
//                "yiGeNGGrU9WZhTvkT2j+NmuWfVVNJ9DqexvtLqsDLNW1U7cuarwQntaGPt9a8KGC\n" +
//                "QnDp/dnw3xtTCqw3bcn4WUcE5Y7LGKLyckU/Go2qTU7EZ1dznT7GU0KSvBebMYBX\n" +
//                "oP6GAjwIMmLIdzqUPg==";
//        String sign = sign(base64, returnCertificates);
//        System.out.println("sign :" + sign);
//    }

}