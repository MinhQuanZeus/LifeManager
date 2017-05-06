package zeus.minhquan.lifemanager.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by QuanT on 5/2/2017.
 */

public class StringUtil {
    public static String MD5(String string) {
        if (string == null)
            return null;

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] inputBytes = string.getBytes();
            byte[] hashBytes = digest.digest(inputBytes);
            return byteArrayToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) { }

        return null;
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }

    public static String getDuplicateRecordName(String recordName){
        int index = 0;
        String a = "";
        for (int i = 0; i < recordName.length(); i++){
            if(recordName.charAt(i) == '.'){
                String sub = recordName.substring(i - 6,i);
                if(sub.charAt(0) == '(' && sub.charAt(2) == ')'){
                    index = Integer.parseInt(String.valueOf(sub.charAt(1))) + 1;
                    a = recordName.substring(0,i-6) + "(" +index +")BNN.3gp";
                    break;
                } else {
                    a = recordName.substring(0,i-3) + "(1)BNN.3gp";
                }
            }
        }
        return a;
    }
}
