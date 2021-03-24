package org.jokergames.jfql.encryption;

public class NoneEncryption extends Encryption {

    public NoneEncryption() {
        super("None", null, new Protocol() {
            @Override
            public String encrypt(String output, String key) {
                return output;
            }

            @Override
            public String decrypt(String input, String key) {
                return input;
            }
        });
    }
}
