import com.coplan.coplanassinatura.CoplanAssinatura;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CoplanAssinaturaTest {

    @Test
    public void testeAssinaPdf() throws Exception {
        CoplanAssinatura.fontSize=6;
        CoplanAssinatura.isAssinarTodasPaginas=true;
        CoplanAssinatura.NomeAssinador = "Diego Donizete Correa da Silva";
        CoplanAssinatura.txtReason = "CPF: 023.127.121-21";
        CoplanAssinatura.Localizacao = "Teste";
        CoplanAssinatura.geraAssinaturaDocumento("c:\\temp\\pdf_origem.pdf","c:\\temp\\pdf_assiando.pdf");
        CoplanAssinatura.getSha256Assinatura("c:\\temp\\pdf_assiando.pdf");
    }

}