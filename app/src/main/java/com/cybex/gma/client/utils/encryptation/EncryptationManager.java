package com.cybex.gma.client.utils.encryptation;

public class EncryptationManager {

    private static Encrypt mEncrypt;
    private static Params mParams;

    public static final class Params {

        String AESKey;
        String SHAKey;

        public String getAESKey() {
            return AESKey;
        }

        public Params setAESKey(String AESKey) {
            this.AESKey = AESKey;
            return this;
        }


        public String getSHAKey() {
            return SHAKey;
        }

        public void setSHAKey(String SHAKey) {
            this.SHAKey = SHAKey;
        }
    }

    public static Params config() {
        mParams = new Params();
        return mParams;
    }

    public static Encrypt getEncrypt() {

        if (mEncrypt == null) {
            mEncrypt = new Encrypt();
        }

        if (mParams != null) {
            mEncrypt.setAESKey(mParams.getAESKey());
            mEncrypt.setSHAKey(mParams.getSHAKey());
        }
        return mEncrypt;
    }
}
