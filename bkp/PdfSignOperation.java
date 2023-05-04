package com.coplan.coplanassinatura;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignature;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.security.*;
import com.itextpdf.text.pdf.security.MakeSignature.CryptoStandard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author murat.demir
 */
public class PdfSignOperation {

    private byte[] content = null;
    private X509Certificate x509Certificate;
    private PdfReader reader = null;
    private ByteArrayOutputStream baos = null;
    private PdfStamper stamper = null;
    private PdfSignatureAppearance sap = null;
    private PdfSignature dic = null;
    private HashMap<PdfName, Integer> exc = null;
    private ExternalDigest externalDigest = null;
    private PdfPKCS7 sgn = null;
    private InputStream data = null;
    private byte hash[] = null;
    private Calendar cal = null;
    private byte[] sh = null;
    private byte[] encodedSig = null;
    private byte[] paddedSig = null;
    private PdfDictionary dic2 = null;
    private static X509Certificate[] chain = null;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public PdfSignOperation(byte[] content, X509Certificate cert) {
        this.content = content;
        this.x509Certificate = cert;
    }

    public byte[] getHash() throws Exception {
        reader = new PdfReader(new ByteArrayInputStream(content));

        baos = new ByteArrayOutputStream();
        stamper = PdfStamper.createSignature(reader, baos, '\0');
        sap = stamper.getSignatureAppearance();

        sap.setReason("Test");
        sap.setLocation("On a server!");
        sap.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, "sig");
        sap.setCertificate(x509Certificate);

        dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        dic.setReason(sap.getReason());
        dic.setLocation(sap.getLocation());
        dic.setContact(sap.getContact());
        dic.setDate(new PdfDate(sap.getSignDate()));
        sap.setCryptoDictionary(dic);

        exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.CONTENTS, 8192 * 2 + 2);
        sap.preClose(exc);

        externalDigest = new ExternalDigest() {
            @Override
            public MessageDigest getMessageDigest(String hashAlgorithm)
                    throws GeneralSecurityException {
                return DigestAlgorithms.getMessageDigest(hashAlgorithm, null);
            }
        };

        chain = new X509Certificate[1];
        chain[0] = x509Certificate;

        sgn = new PdfPKCS7(null, chain, "SHA256", null, externalDigest, false);
        data = sap.getRangeStream();
        cal = Calendar.getInstance();
        hash = DigestAlgorithms.digest(data, externalDigest.getMessageDigest("SHA256"));
//        sh = sgn.getAuthenticatedAttributeBytes(hash, cal, null, null, CryptoStandard.CMS);
        sh = sgn.getAuthenticatedAttributeBytes(hash, getOscp(), null, CryptoStandard.CMS);
        sh = DigestAlgorithms.digest(new ByteArrayInputStream(sh), externalDigest.getMessageDigest("SHA256"));
        return sh;
    }
    public static byte[] getOscp() {
        byte[] ocsp = null;
        OcspClient ocspClient = new OcspClientBouncyCastle(new OCSPVerifier(null, null));

        if (chain.length >= 2) {
            ocsp = ocspClient.getEncoded((X509Certificate)chain[0], (X509Certificate)chain[1], null);
        }

        return ocsp;
    }
    public String complateToSignature(byte[] signedHash) throws Exception {
        sgn.setExternalDigest(signedHash, null, "RSA");
        encodedSig = sgn.getEncodedPKCS7();
        paddedSig = new byte[8192];

        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
        dic2 = new PdfDictionary();

        dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));
        sap.close(dic2);

        return Base64.encodeBytes(baos.toByteArray());
    }

}