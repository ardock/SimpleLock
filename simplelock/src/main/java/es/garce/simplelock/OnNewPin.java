package es.garce.simplelock;

/**
 * This interface is used by the new pin lock activity to communicate with the caller.
 *
 * Created by Gonzalo Garce on 17/06/2016.
 */
public interface OnNewPin {
    /**
     * When the new pin was inserted correctly the listener calls this function. If you decide to return
     * True and let Simple Lock the pin persistence you must not finish the activity until
     * {@link OnNewPin#onSaved(SimpleLockNewActivity, String)} has been called.
     * @param simpleLockActivity The activity reference if user wants to terminate it.
     * @param pin The pin inserted by the user.
     * @return True if the pin should be saved in preferences by Simple Lock, false otherwise.
     * (See {@link SimpleLockPin} documentation for further information).
     */
    boolean onDone(SimpleLockNewActivity simpleLockActivity, String pin);
    /**
     * This can be called when user pin confirmation doesn't match.
     * @param simpleLockActivity The activity reference if user wants to terminate it.
     * @param code Error code. See {@link SimpleLockNewActivity} errors
     *             ({@link SimpleLockNewActivity#CANCELED CANCELED},
     *             {@link SimpleLockNewActivity#NO_KEY NO_KEY}, etc).
     */
    void onError(SimpleLockNewActivity simpleLockActivity, int code);
    /**
     * This is fired when Simple Lock manage the pin persistence and it has been saved. When this is
     * called is secure to finish the {@link SimpleLockNewActivity activity}.
     * @param simpleLockActivity The activity reference if user wants to terminate it.
     * @param pin The hash representation of the pin that has been saved.
     */
    void onSaved(SimpleLockNewActivity simpleLockActivity, String pin);
}
