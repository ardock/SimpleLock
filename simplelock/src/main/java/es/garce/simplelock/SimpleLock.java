package es.garce.simplelock;

import android.content.Context;

/**
 * <p>This class contains the globals and settings of the Simple Lock.</p>
 *
 * <p>Created by Gonzalo Garce on 17/06/2016.</p>
 */
public class SimpleLock {

    public static class SimpleLockConfigurationError extends Exception {
        public SimpleLockConfigurationError(String message) {
            super(message);
        }
    }

    private static final String basePreference = "es.garce.simplelock";
    private static final String maxInputPreference = "input_length";
    private static final String placeholderPreference = "placeholder";
    private static final String previewPreference = "show_preview";
    private static final String minPreference = "min_length";
    private static final String shufflePreference = "shuffle";

    protected static final int[] BUTTON_IDS = {
            R.id.es_garce_simplelock_btn_0,
            R.id.es_garce_simplelock_btn_1,
            R.id.es_garce_simplelock_btn_2,
            R.id.es_garce_simplelock_btn_3,
            R.id.es_garce_simplelock_btn_4,
            R.id.es_garce_simplelock_btn_5,
            R.id.es_garce_simplelock_btn_6,
            R.id.es_garce_simplelock_btn_7,
            R.id.es_garce_simplelock_btn_8,
            R.id.es_garce_simplelock_btn_9
    };

    protected static boolean showPlaceholder = true;
    protected static boolean showPreview = true;

    protected static boolean shuffleButtons = false;

    protected static int maxLength = 4;
    protected static int minLength = 1;

    /**
     * Configure SimpleLock attributes. Usually run first time app is launched
     * @param context Context to get {@link android.content.SharedPreferences} instance.
     * @param showPlaceholder True if SimpleLock should show small circles  with the pin length as
     *                        a length hint. You can hide to make lock more secure.
     * @param showPreview If True a bigger circle will appear when user enters new number. If
     *                    placeholder is false they will appear still.
     * @param minLength The minimum length of the input. It must be bigger than 0. Done button only
     *                  will be enabled when minLength reached.
     * @param maxLength The maximum length numbers the user can input.
     *                  It must be bigger than {@linkplain SimpleLock#minLength}
     */
    public static void configure(Context context, boolean showPlaceholder, boolean showPreview,
                                 int minLength, int maxLength, boolean shuffleButtons)
                        throws SimpleLockConfigurationError {

        if (minLength < 1)
            throw new SimpleLockConfigurationError("minLength must be positive");

        if (maxLength <  minLength)
            throw new SimpleLockConfigurationError("maxLength must be bigger than minLength");

        setShowPlaceholder(context, showPlaceholder);
        setShowPreview(context, showPreview);
        setMaxLength(context, maxLength);
        setMinLength(context, minLength);
        setShuffleButtons(context, shuffleButtons);
    }

    /**
     * You can check if SimpleLock has been configured previously to prevent overwrite settings
     * every time you run your app.
     * @param context Context to get {@link android.content.SharedPreferences} instance.
     * @return  Returns true if the Lock has been init at least one time.
     */
    public static boolean isInit(Context context) {
        if (context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .contains(maxInputPreference))
            return true;

        if (context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .contains(placeholderPreference))
            return true;

        if (context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .contains(previewPreference))
            return true;

        if (context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .contains(minPreference))
            return true;

        if (context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .contains(shufflePreference))
            return true;

        return false;
    }

    public static boolean isShuffleButtons(Context context) {
        return context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .getBoolean(shufflePreference, shuffleButtons);
    }

    public static void setShuffleButtons(Context context, boolean shuffleButtons) {
        context.getSharedPreferences(basePreference, Context.MODE_PRIVATE).edit()
                .putBoolean(shufflePreference, shuffleButtons).apply();
    }

    public static boolean isShowPlaceholder(Context context) {
        return context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .getBoolean(placeholderPreference, showPlaceholder);
    }

    public static void setShowPlaceholder(Context context, boolean showPlaceholder) {
        context.getSharedPreferences(basePreference, Context.MODE_PRIVATE).edit()
                .putBoolean(placeholderPreference, showPlaceholder).apply();
    }

    public static boolean isShowPreview(Context context) {
        return context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .getBoolean(previewPreference, showPreview);
    }

    public static void setShowPreview(Context context, boolean showPreview) {
        context.getSharedPreferences(basePreference, Context.MODE_PRIVATE).edit()
                .putBoolean(previewPreference, showPreview).apply();
    }

    public static int getMinLength(Context context) {
        return context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .getInt(minPreference, minLength);
    }

    public static void setMinLength(Context context, int minLength) {
        context.getSharedPreferences(basePreference, Context.MODE_PRIVATE).edit()
                .putInt(minPreference, minLength).apply();
    }

    public static void setMaxLength(Context context, int maxLength) {
        context.getSharedPreferences(basePreference, Context.MODE_PRIVATE).edit()
                .putInt(maxInputPreference, maxLength).apply();
    }

    public static int getMaxLength(Context context) {
        return context.getSharedPreferences(basePreference, Context.MODE_PRIVATE)
                .getInt(maxInputPreference, maxLength);
    }
}
