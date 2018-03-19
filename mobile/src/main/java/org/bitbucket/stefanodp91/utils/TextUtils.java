package org.bitbucket.stefanodp91.utils;

/**
 * Created by stefano on 26/03/2016.
 */
public class TextUtils {
    public static boolean isValid(String string) {
        boolean isValid = false;

        if (string != null && !string.isEmpty()) {
            isValid = true;
        }

        return isValid;
    }
}
