package uk.ac.ebi.eva.core.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class ConfiguredBCrypt extends BCryptPasswordEncoder {

    public static final int BCRYPTO_STRENGTH = 12;

    public ConfiguredBCrypt(){
        super(BCRYPTO_STRENGTH, new SecureRandom());
    }
}
