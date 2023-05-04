import com.coplan.coplanassinatura.CoplanAssinatura;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CoplanAssinaturaTest {

    @Test
    public void testeAssinaPdf() throws Exception {

        CoplanAssinatura.geraAssinaturaDocumento("c:\\temp\\pdf_origem.pdf","c:\\temp\\pdf_assiando.pdf");
        CoplanAssinatura.getSha256Assinatura("c:\\temp\\pdf_assiando.pdf");
    }

}