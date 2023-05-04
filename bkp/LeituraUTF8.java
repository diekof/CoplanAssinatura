package com.coplan.coplanassinatura;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LeituraUTF8 {

    public static void main(String[] args)  {
        try {
            // abertura do arquivo
            java.io.BufferedReader myBuffer = new java.io.BufferedReader(new java.io.InputStreamReader(new java.io.FileInputStream("C:\\Des\\Tomcat 9.0\\webapps\\central.war\\PublicTempStorage\\voucher-905_821600f69367423b9d5755987f93fbf053e8-ea25-403a-a6c6-e6f0d1136698.pdf"), "UTF-8"));

            // loop que lê e imprime todas as linhas do arquivo
            String linha = myBuffer.readLine();
            StringBuilder stringBuilder = new StringBuilder(100);
            stringBuilder.append(myBuffer.readLine());
            while (linha != null) {
                linha = myBuffer.readLine();
                if(linha != null){
                    stringBuilder.append(linha);
                }
            }
//            System.out.println(stringBuilder);
            String AV27Content = stringBuilder.toString();
            org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
            String result = new String(toHexString(getSHA(AV27Content)));
            System.out.println(result);
            myBuffer.close();

            String base64String = geraSha256ToBase64();
            System.out.println(base64String);
//            validaDocumento();
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }
    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);
        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));
        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static String geraSha256ToBase64() throws NoSuchAlgorithmException, IOException {
        System.out.println("teste");
        byte[] buffer= new byte[8192];
        int count;
        java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.FileInputStream("C:\\Des\\Tomcat 9.0\\webapps\\central.war\\PublicTempStorage\\voucher-905_821600f69367423b9d5755987f93fbf053e8-ea25-403a-a6c6-e6f0d1136698.pdf"));
        while ((count = bis.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }
        bis.close();

        byte[] hash = digest.digest();
        System.out.println(new String(toHexString(hash)));
        org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
        System.out.println(new String(base64.encodeAsString(hash)));
        return new String(base64.encodeAsString(hash));
    }

//    public static void validaDocumento() throws UnirestException {
//        Unirest.setTimeouts(0, 0);
//        HttpResponse<String> response = Unirest.post("https://verificador.staging.iti.br/report")
//                .header("Cookie", "STICKY=f4c4fd23d1c00a69ab661bda41b11bff")
//                .field("signature_files", new File("C:\\Des\\Tomcat 9.0\\webapps\\central.war\\PublicTempStorage\\voucher-905_821600f69367423b9d5755987f931709c181-a678-4b97-9e83-bd33f826fcf4.pdf"))
//                .field("detached_files", new File("C:\\Des\\Tomcat 9.0\\webapps\\central.war\\PublicTempStorage\\CHAVE_DOCUMENTO.p7s"))
//                .field("verify_incremental_updates", "false")
//                .asString();
//
//        JSONObject resp=new JSONObject(response.getBody());
//        System.out.println(resp.getJSONObject("report").get("generalStatus"));
//
//    }
}
