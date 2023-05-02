package org.springframework.security.crypto.password;

public interface PasswordEncoder {
    String encode(CharSequence var1);

    boolean matches(CharSequence var1, String var2);
    default boolean upgradeEncoding(String encodedPwd){return false;}
}
