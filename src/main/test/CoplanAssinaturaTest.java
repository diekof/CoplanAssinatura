import com.coplan.coplanassinatura.CoplanAssinatura;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CoplanAssinaturaTest {

    @Test
    public void testeAssinaPdf() throws Exception {
        CoplanAssinatura.fontSize=4;
        CoplanAssinatura.isAssinarTodasPaginas=false;
        CoplanAssinatura.NomeAssinador = "Joao exemplo";
        CoplanAssinatura.txtReason = "CPF: 023.127.121-21";
        CoplanAssinatura.Localizacao = "Teste";
        CoplanAssinatura.UrlLogoGovbr   = "https://www.gov.br/++theme++padrao_govbr/img/govbr-colorido-b.png";
        CoplanAssinatura.textoPadraoAssinatura = "Documento Assinado Digitalmente";
        CoplanAssinatura.NumeroAssinatura = 1;
        CoplanAssinatura.geraAssinaturaDocumento("c:\\temp\\pdf_assiando_new.pdf","c:\\temp\\pdf_assiando_new2.pdf");
        CoplanAssinatura.getSha256Assinatura("c:\\temp\\pdf_assiando.pdf");
    }

}