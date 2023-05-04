package com.coplan.coplanassinatura;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

//import javax.annotation.Nullable;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MklTest {
    static Certificate[] chain;
    static byte[] toSign;
    static byte[] hash;

    static class MyExternalSignatureContainer implements ExternalSignatureContainer {
        protected byte[] sig;
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

    static class EmptyContainer implements ExternalSignatureContainer {
        public EmptyContainer() {
        }
        public byte[] sign(InputStream is) {
            try {
                ExternalDigest digest = getDigest();
                String hashAlgorithm = getHashAlgorithm();

                Certificate[] certs = {null};

                hash = DigestAlgorithms.digest(is, digest.getMessageDigest(hashAlgorithm));
                PdfPKCS7 sgn = getPkcs(certs);

                toSign = sgn.getAuthenticatedAttributeBytes(hash, getOscp(), null,
                        MakeSignature.CryptoStandard.CMS);

                return new byte[0];
            } catch (IOException | GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void modifySigningDictionary(PdfDictionary pdfDictionary) {
            pdfDictionary.put(PdfName.FILTER, PdfName.ADOBE_PPKMS);
            pdfDictionary.put(PdfName.SUBFILTER, PdfName.ADBE_PKCS7_DETACHED);
        }
    }

    public static String getHashAlgorithm() {
        return "SHA256";
    }

    public static byte[] getOscp() {
        byte[] ocsp = null;
        OcspClient ocspClient = new OcspClientBouncyCastle(new OCSPVerifier(null, null));

        if (chain.length >= 2) {
            ocsp = ocspClient.getEncoded((X509Certificate)chain[0], (X509Certificate)chain[1], null);
        }

        return ocsp;
    }

    public static PdfPKCS7 getPkcs() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        System.out.println(chain[0]);
        return new PdfPKCS7(null, chain, getHashAlgorithm(), null, getDigest(), false);
    }

    public static PdfPKCS7 getPkcs(Certificate[] certChain) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        //noinspection ConstantConditions
        return new PdfPKCS7(null, certChain, getHashAlgorithm(), null, getDigest(), false);
    }

    public static void emptySignature(String src, String dest, String fieldname) throws IOException, DocumentException, GeneralSecurityException {
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');

        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, fieldname);
        appearance.setReason("Nice");
        appearance.setLocation("Delhi");
        appearance.setSignDate(cal);

//        ExternalSignatureContainer external = new EmptyContainer();
        ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE,
                PdfName.ADBE_PKCS7_DETACHED);
        MakeSignature.signExternalContainer(appearance, external, 10192);

        os.close();
        reader.close();
    }

    public static void setChain() throws CertificateException {
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
    }

    private static ExternalDigest getDigest() {
        return new ExternalDigest() {
            public MessageDigest getMessageDigest(String hashAlgorithm)
                    throws GeneralSecurityException {
                return DigestAlgorithms.getMessageDigest(hashAlgorithm, null);
            }
        };
    }

    public static TSAClient getTsa() {
        return new TSAClientBouncyCastle("http://timestamp.digicert.com", null, null, 4096, "SHA-512");
    }

    public static void createSignature(String src, String dest, String fieldname, byte[] hash, byte[] signature) throws IOException, DocumentException, GeneralSecurityException {
//        PdfPKCS7 sgn = new PdfPKCS7(null, chain, null, "SHA1", null, false);
        PdfPKCS7 sgn = new PdfPKCS7(null, chain, "SHA256", null, null, false);
        sgn.setExternalDigest(signature, null, "RSA");

        byte[] encodedSig = sgn.getEncodedPKCS7(hash, getTsa(), getOscp(), null,
                MakeSignature.CryptoStandard.CMS);

        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        ExternalSignatureContainer external = new MyExternalSignatureContainer(encodedSig);
        MakeSignature.signDeferred(reader, fieldname, os, external);

        reader.close();
        os.close();
    }
    public static void main(String[] args) throws Exception {
        setChain();

        String src = "c:\\temp\\pdf_origem.pdf";
        String between = "c:\\temp\\sample_out_between.pdf";
        String dest = "c:\\temp\\pdf_assinado.pdf";
        String fieldName = "sign";

       emptySignature(src, between, fieldName);
//       System.out.println(Hex.encodeHexString(toSign));

        String signature = "123456";  // signed hash signature we get from client
        byte[] signatureBytes = Hex.decodeHex(signature.toCharArray());
//
//        createSignature(between, dest, fieldName, hash, signatureBytes);

//        String hashAlgorithm = getHashAlgorithm();
//        PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, getDigest(), false);
//        InputStream data = sap.getRangeStream();
//        byte hash[] = DigestAlgorithms.digest(data, externalDigest.getMessageDigest(hashAlgorithm));
//        Calendar cal = Calendar.getInstance();
//        byte[] ocsp = null;
//        if (chain.length >= 2 && ocspClient != null) {
//            ocsp = ocspClient.getEncoded((X509Certificate) chain[0], (X509Certificate) chain[1], null);
//        }
//        byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, cal, ocsp, crlBytes, sigtype);
//        byte[] extSignature = externalSignature.sign(sh);
//        sgn.setExternalDigest(extSignature, null, externalSignature.getEncryptionAlgorithm());

    }

}