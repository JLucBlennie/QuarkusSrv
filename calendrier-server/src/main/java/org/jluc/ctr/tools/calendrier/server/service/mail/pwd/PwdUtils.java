package org.jluc.ctr.tools.calendrier.server.service.mail.pwd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;

public class PwdUtils {

    private Logger mLogger = Logger.getLogger(PwdUtils.class);
    private static PwdUtils mInstance;
    private static File mPwdFile = new File("src/main/resources/ctr.pwd");

    public static synchronized PwdUtils getInstance() {
        if (mInstance == null) {
            mInstance = new PwdUtils();
        }
        return mInstance;
    }

    private PwdUtils() {

    }

    public synchronized void writePwdFile(List<StructPwd> pwdList) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = new FileOutputStream(mPwdFile);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(pwdList);
            oos.flush();
        } catch (IOException e) {
            mLogger.error("Pb ecriture fichier de contact...", e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    mLogger.error("Pb ecriture fichier de contact...", e);
                }
            }
        }
    }

    public synchronized List<StructPwd> readPwdFile() {
        List<StructPwd> result = null;
        ObjectInputStream ois = null;

        try {
            InputStream fis = PwdUtils.class.getResourceAsStream("/ctr.pwd");
            ois = new ObjectInputStream(fis);
            result = (List<StructPwd>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            mLogger.error("Pb lecture fichier de contact...", e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    mLogger.error("Pb lecture fichier de contact...", e);
                }
            }
        }

        return result;
    }

    /**
     * Point d'entree pour creer le fichier au depart
     * 
     * @param args
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            String login = args[0];
            String pwd = args[1];

            List<StructPwd> list = new ArrayList<StructPwd>();
            StructPwd contact = new StructPwd();
            contact.setLogin(login);
            contact.setPwd(pwd);
            list.add(contact);
            PwdUtils.getInstance().writePwdFile(list);
        } else {
            System.out.println("Pas le bon nombre d'arguments : 2 (login, password)");
        }
    }
}
