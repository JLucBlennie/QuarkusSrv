package org.jluc.ctr.tools.calendrier.server.service.mail.pwd;

import java.io.Serializable;

public class StructPwd implements Serializable {

    /**
     * Serial
     */
    private static final long serialVersionUID = -1958355632373016681L;

    private String mLogin = "";
    private String mPwd = "";

    public StructPwd() {
        super();
    }

    public String getLogin() {
        return decrypt(mLogin);
    }

    public void setLogin(String login) {
        this.mLogin = encrypt(login);
    }

    public String getPwd() {
        return decrypt(mPwd);
    }

    public void setPwd(String pwd) {
        this.mPwd = encrypt(pwd);
    }

    private String encrypt(String password) {
        String crypte = "";
        for (int i = 0; i < password.length(); i++) {
            int c = password.charAt(i) ^ 48;
            crypte = crypte + (char) c;
        }
        return crypte;
    }

    public String decrypt(String password) {
        String aCrypter = "";
        for (int i = 0; i < password.length(); i++) {
            int c = password.charAt(i) ^ 48;
            aCrypter = aCrypter + (char) c;
        }
        return aCrypter;
    }
}
