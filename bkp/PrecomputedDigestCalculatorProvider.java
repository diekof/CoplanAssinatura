package com.coplan.coplanassinatura;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class PrecomputedDigestCalculatorProvider implements DigestCalculatorProvider {
    private final byte[] digest;

    public PrecomputedDigestCalculatorProvider(byte[] digest) {
        this.digest = digest;
    }

    @Override
    public DigestCalculator get(final AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException {

        return new DigestCalculator() {
            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream();
            }

            @Override
            public byte[] getDigest() {
                return digest;
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return digestAlgorithmIdentifier;
            }
        };
    }

}
