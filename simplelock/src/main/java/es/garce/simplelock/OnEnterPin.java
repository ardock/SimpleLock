package es.garce.simplelock;

import android.os.Bundle;

/**
 * This interface is used by the lock activity to communicate with the caller.
 *
 * <p>Created by Gonzalo Garce on 17/06/2016.</p>
 */
public interface OnEnterPin {
    /**
     * This will be fired when the pin is not managed by SimpleLock and user hits done button.
     * @param pinCode The input pin.
     * @return True if the pin was correct, false otherwise.
     */
    boolean onEnterPin(String pinCode);
    /**
     * This is called when the pin is managed by SimpleLock and it was correct.
     * @param simpleLockActivity The activity reference if user wants to finish it.
     */
    void onCorrectPin(SimpleLockActivity simpleLockActivity);
    /**
     * This is called when the pin is not correct.
     * @param simpleLockActivity The activity reference if user wants to finish it.
     */
    void onWrongPin(SimpleLockActivity simpleLockActivity);
    /**
     * Called when user tries to go back from this activity. Listener must override the back
     * behaviour of the lock activity. Usually useful to prevent access when lock was called in
     * {@link android.app.Activity#onCreate(Bundle) onCreate}
     * @param simpleLockActivity The activity reference if user wants to finish it.
     */
    void onBack(SimpleLockActivity simpleLockActivity);
}
