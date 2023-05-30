import com.coplan.coplanassinatura.CoplanAssinatura;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CoplanAssinaturaTest {

    @Test
    public void testeAssinaPdf() throws Exception {

        assinatura(0,"C:\\temp\\pdf_empty.pdf","c:\\temp\\pdf_assiando_govbr_all_page.pdf");
//        assinatura(1,"c:\\temp\\pdf_assiando_govbr.pdf","c:\\temp\\pdf_assiando_govbr_2.pdf");
//        assinatura(2,"c:\\temp\\pdf_assiando_govbr_2.pdf","c:\\temp\\pdf_assiando_govbr_3.pdf");
//        assinatura(3,"c:\\temp\\pdf_assiando_govbr_3.pdf","c:\\temp\\pdf_assiando_govbr_4.pdf");

//        CoplanAssinatura.fontSize=4;
//        CoplanAssinatura.isAssinarTodasPaginas=Boolean.TRUE;
//        CoplanAssinatura.NomeAssinador = "DIEGO DONIZETE CORREA DA SILVA";
//        CoplanAssinatura.txtReason = "CPF: 023.127.121-21";
//        CoplanAssinatura.Localizacao = "Teste";
//        CoplanAssinatura.UrlLogoGovbr   = "https://www.gov.br/++theme++padrao_govbr/img/govbr-colorido-b.png";
//        CoplanAssinatura.textoPadraoAssinatura = "Documento Assinado Digitalmente";
//        CoplanAssinatura.NumeroAssinatura = 0;
//        CoplanAssinatura.geraAssinaturaDocumento("C:\\temp\\govbr_template_assinatura_b69bfaed9d864167637cdb-ae7b-48d9-951a-ce377d30c3ea.pdf","c:\\temp\\pdf_assiando_govbr.pdf");
//        CoplanAssinatura.geraAssinaturaDocumento("c:\\temp\\pdf_assiando_govbr.pdf","c:\\temp\\pdf_assiando_govbr_2.pdf");
//        CoplanAssinatura.geraAssinaturaDocumento("c:\\temp\\pdf_assiando_govbr_2.pdf","c:\\temp\\pdf_assiando_govbr_3.pdf");
//        CoplanAssinatura.geraAssinaturaDocumento("c:\\temp\\pdf_assiando_govbr_3.pdf","c:\\temp\\pdf_assiando_govbr_4.pdf");
        //CoplanAssinatura.getSha256Assinatura("C:\\temp\\govbr_template_assinatura_b69bfaed9d864167637cdb-ae7b-48d9-951a-ce377d30c3ea.pdf");
    }

    public void assinatura(int num, String pdfOrigem, String pdfDestino) throws Exception {
        CoplanAssinatura.fontSize=4;
        CoplanAssinatura.isAssinarTodasPaginas=Boolean.TRUE;
        CoplanAssinatura.NomeAssinador = "DIEGO DONIZETE CORREA DA SILVA";
        CoplanAssinatura.txtReason = "CPF: 023.127.121-21";
        CoplanAssinatura.Localizacao = "Teste";
        CoplanAssinatura.UrlLogoGovbr   = "https://www.gov.br/++theme++padrao_govbr/img/govbr-colorido-b.png";
        CoplanAssinatura.textoPadraoAssinatura = "Documento Assinado Digitalmente";
        CoplanAssinatura.NumeroAssinatura = num;
        CoplanAssinatura.geraAssinaturaDocumento(pdfOrigem,pdfDestino);
    }

}