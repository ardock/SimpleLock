package es.garce.simplelock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;

/**
 * <p>This Activity is used to display a pin pad where user can enter a pin code to save this and
 * require it in the future.<br>
 * You must call {@linkplain SimpleLockNewActivity#newPin(Context, OnNewPin, int, String)} to launch an instance
 * of this activity. A listener must be provided to receive Simple Lock callbacks.</p>
 *
 * <p>Created by Gonzalo Garce on 17/06/2016.</p>
 */
public class SimpleLockNewActivity extends Activity implements View.OnClickListener {

    /**
     * This error code indicates that the New Pin Activity was canceled.
     */
    public static final int CANCELED = 0;
    /**
     * This error code indicates that Simple Lock tried to save the pin but there wasn't a valid key
     */
    public static final int NO_KEY = 1;

    private static final String KEY_KEY = "KEY_KEY";
    private static final String KEY_REPEAT = "KEY_REPEAT";

    private TextView tvDesc;

    private LinearLayout layoutPin;
    private int indexCircles = 0;

    private Button btnNumber[] = new Button[SimpleLock.BUTTON_IDS.length];
    private ImageView btnDel;
    private ImageView btnOk;

    private static OnNewPin mListener = null;
    private String pinCode = "";

    //Settings
    private String key = null;
    private int repeat = 1;
    private int localRepeat = 0;
    private String firstPin = null;

    //Preference
    private static int maxLength;
    private static int minLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        if (getIntent().getExtras() != null) {
            repeat = getIntent().getExtras().getInt(KEY_REPEAT, repeat);
            key = getIntent().getExtras().getString(KEY_KEY);
        }

        //Load preference
        maxLength = SimpleLock.getMaxLength(this);
        minLength = SimpleLock.getMinLength(this);

        //Set description
        tvDesc = (TextView) findViewById(R.id.es_garce_simplelock_description);
        tvDesc.setText(getResources().getString(R.string.es_garce_simplelock_new_pin));

        //Layout that will contain circles
        layoutPin = (LinearLayout) findViewById(R.id.es_garce_simplelock_layout_pin);

        //Load buttons and set listeners
        for (int i = 0; i < btnNumber.length; i++) {
            btnNumber[i] = (Button) findViewById(SimpleLock.BUTTON_IDS[i]);
            btnNumber[i].setOnClickListener(this);
        }
        btnDel = (ImageView) findViewById(R.id.es_garce_simplelock_btn_del);
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDel();
            }
        });
        btnDel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickLongDel();
                return true;
            }
        });
        btnOk = (ImageView) findViewById(R.id.es_garce_simplelock_btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOk();
            }
        });
        btnOk.setEnabled(false);
    }

    /**
     * Starts a LockActivity for create a new pin.
     * @param context Context to create the intent.
     * @param listener Listener that will receive the lock callbacks.
     * @param repeats Number of times the user must repeat the pin code to ensure it's ok. 1 default.
     *                If repeats < 0 then default would be applied.
     * @param key The key of the pin to be saved on preference if you want SimpleLock to manage the
     *            persistence. If null you must save the pin by your way when returned.
     */
    public static void newPin(Context context, OnNewPin listener, int repeats, @Nullable String key) {
        Intent intent = new Intent(context, SimpleLockNewActivity.class);
        if (repeats >= 0)
            intent.putExtra(KEY_REPEAT, repeats);
        if (key != null)
            intent.putExtra(KEY_KEY, key);
        mListener = listener;
        context.startActivity(intent);
    }

    /**
     * Sets text of the lock activity.
     * @param description Text to show.
     */
    public void setDescription (String description) {
        tvDesc.setText(description);
    }

    @Override
    public void onClick(View v) {
        if (pinCode.length() != maxLength) {
            addCircle();
            //Update pin
            Button btn = (Button) v;
            pinCode += btn.getText();
            //Enable Done
            btnOk.setEnabled(pinCode.length() > minLength);
        }
    }

    private void clickOk() {
        if (firstPin == null) {
            firstPin = pinCode;
            setDescription(getResources().getString(R.string.es_garce_simplelock_repeat_pin));
        }
        else {
            if(firstPin.equals(pinCode)) {
                localRepeat++;
                setDescription(getResources().getString(R.string.es_garce_simplelock_repeat_pin));
            }
            else {
                setDescription(getResources().getString(R.string.es_garce_simplelock_repeat_error));
            }
        }

        //If finished repeats then save
        if (localRepeat == repeat) {
            //Simple Lock manage pins
            if (mListener.onDone(this, pinCode)) {
                if (key == null)
                    mListener.onError(this, NO_KEY);
                else
                    try {
                        mListener.onSaved(this, SimpleLockPin.savePin(this, key, pinCode));
                    } catch (NoSuchAlgorithmException e) {
                        Toast.makeText(this, getResources().getString(
                                R.string.es_garce_simplelock_check_error), Toast.LENGTH_SHORT).show();
                    }
            }
        }

        //Always reset UI
        resetCircles();
        //Empty pin
        pinCode = "";
        //Diable Done
        btnOk.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        mListener.onError(this, CANCELED);
        super.onBackPressed();
    }

    private void clickDel() {
        if (pinCode.length() > 0) {
            removeCircle();
            //Update pin
            pinCode = pinCode.substring(0, pinCode.length() - 1);
            //Disable Done if needed
            btnOk.setEnabled(pinCode.length() > minLength);
        }
    }

    private void clickLongDel() {
        if (pinCode.length() > 0) {
            resetCircles();
            //Empty pin
            pinCode = "";
            //Diable Done
            btnOk.setEnabled(false);
        }
    }

    @SuppressWarnings("deprecation")
    private void addCircle () {
        ImageView img = new ImageView(this);
        img.setImageDrawable(getResources().getDrawable(R.drawable.lock_circle));
        layoutPin.addView(img, indexCircles);
        indexCircles++;
    }

    private void removeCircle () {
        //Remove view
        if (indexCircles > 0) {
            layoutPin.removeViewAt(--indexCircles);
        }
    }

    private void resetCircles () {
        layoutPin.removeViews(0, indexCircles);
        indexCircles = 0;
    }
}
