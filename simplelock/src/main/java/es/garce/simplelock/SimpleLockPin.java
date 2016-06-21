package es.garce.simplelock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>This class is used to manage pin persistence. It makes use of hash functions to secure the
 * pin code and don't save it clear.</p>
 *
 * <p>Created by Gonzalo Garce on 17/06/2016.</p>
 */
public class SimpleLockPin {

    private static final String basePreference = "es.garce.simplelock_pin";

    /**
     * Gets a hashed pin from Shared Preference and checks if the input pin is correct.
     * @param context Context to get preference.
     * @param key The key of the pin to check. This should be set when user created a new pin.
     * @param pin The pin code to compare to.
     * @return True if the pin codes match false otherwise or the pin for the given key wasn't found.
     */
    protected static boolean checkPin(Context context, @NonNull String key, @NonNull String pin)
            throws NoSuchAlgorithmException {
        String prefPin = context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .getString(key, null);
        if (prefPin != null) {
            //hash input pin
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(pin.getBytes());
            //Convert to string to compare
            String hashed =  Base64.encodeToString(md.digest(), Base64.DEFAULT);
            return prefPin.equals(hashed);
        }
        return false;
    }

    /**
     * Save a pin on the shared preferences, the pin is hashed before save.
     * @param context Context to get preference.
     * @param key The key of the pin to save. If there are gonna be more than one pin in your application
     *            you must set a key for each specific pin. You should save the key and know when to use it.
     * @param pin The pin code to save.
     * @return The hashed pin that has been saved on preferences.
     */
    protected static String savePin(Context context, @NonNull String key, @NonNull String pin)
            throws NoSuchAlgorithmException {
        //hash pin
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(pin.getBytes());
        //Covert to string to save on preferences
        String hashed =  Base64.encodeToString(md.digest(), Base64.DEFAULT);
        context.getSharedPreferences(basePreference, Context.MODE_PRIVATE).edit()
                .putString(key, hashed).apply();
        return hashed;
    }

    /**
     * This method is provided as a helper for people who wants to manage the pin persistence by their
     * own. Using this you can save your pin hashed in the same way if you were letting the persistence
     * to Simple Lock.
     * @param pin The String representation of the pin to hash.
     * @return The SHA-1 hash of the pin.
     * @throws NoSuchAlgorithmException
     */
    public static String hashPin(@NonNull String pin) throws NoSuchAlgorithmException {
        //hash pin
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(pin.getBytes());
        //Covert to string to save on preferences
        return Base64.encodeToString(md.digest(), Base64.DEFAULT);
    }

}
