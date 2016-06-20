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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>This Activity is used to display a pin pad where user must enter a pin code in order to continue.<br>
 * You must call {@linkplain SimpleLockActivity#requestPin(Context, OnEnterPin, String)} to launch an instance
 * of this activity. A listener must be provided to receive lock callbacks.</p>
 *
 * <p>Created by Gonzalo Garce on 17/06/2016.</p>
 */
public class SimpleLockActivity extends Activity implements View.OnClickListener {

    private static final String KEY_KEY = "KEY_KEY";

    private TextView tvDesc;

    private LinearLayout layoutPin;
    private ImageView placeholder[];
    private int indexCircles = 0;

    private Button btnNumber[] = new Button[SimpleLock.BUTTON_IDS.length];
    private ImageView btnDel;
    private ImageView btnOk;

    private static OnEnterPin mListener = null;
    private String pinCode = "";

    //Preference
    private String key = null;
    private boolean showPreview = SimpleLock.showPreview;
    private static boolean showPlaceholder = SimpleLock.showPlaceholder;
    private static boolean shuffleButtons = SimpleLock.shuffleButtons;
    private static int maxLength = SimpleLock.maxLength;
    private static int minLength = SimpleLock.minLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        //Load preference
        showPreview = SimpleLock.isShowPreview(this);
        showPlaceholder = SimpleLock.isShowPlaceholder(this);
        shuffleButtons = SimpleLock.isShuffleButtons(this);
        maxLength = SimpleLock.getMaxLength(this);
        minLength = SimpleLock.getMinLength(this);

        if (getIntent().getExtras() != null)
            key = getIntent().getExtras().getString(KEY_KEY);

        //Set description
        tvDesc = (TextView) findViewById(R.id.es_garce_simplelock_description);
        tvDesc.setText(getResources().getString(R.string.es_garce_simplelock_enter_pin));

        //Layout that will contain circles
        layoutPin = (LinearLayout) findViewById(R.id.es_garce_simplelock_layout_pin);
        //If show placeholder circles create and add them to layout
        if (showPlaceholder) {
            placeholder = new ImageView[maxLength];
            for (int i = 0; i < placeholder.length; i++) {
                placeholder[i] = new ImageView(this);
                //noinspection deprecation
                placeholder[i].setImageDrawable(getResources().getDrawable(R.drawable.lock_circle_small));
                layoutPin.addView(placeholder[i]);
            }
        }

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

        //If shuffle enabled then reorder buttons
        if (shuffleButtons)
            shuffleButtons(btnNumber);
    }


    @Override
    public void onBackPressed() {
        if (mListener != null)
            mListener.onBack(this);
    }

    /**
     * Lanza una LockActivity que pide una clave para continuar.
     * @param context El contexto para crear el intent, normalmente la actividad invocadora.
     * @param listener Listener that will receive the lock callbacks.
     * @param key The key of the pin the lock must check. If null the pin check will be done by the
     *            invoker that will receive the input through {@linkplain OnEnterPin#onEnterPin(String)}
     */
    public static void requestPin(Context context, OnEnterPin listener, @Nullable String key) {
        Intent intent = new Intent(context, SimpleLockActivity.class);
        if (key != null)
            intent.putExtra(KEY_KEY, key);
        mListener = listener;
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (pinCode.length() != maxLength) {

            //Show preview
            if (showPreview)
                addCircle();

            //Update pin
            Button btn = (Button) v;
            pinCode += btn.getText();

            //Enable OK button
            if (pinCode.length() == minLength)
                btnOk.setEnabled(true);
        }
    }

    /**
     * Sets text of the lock activity.
     * @param description Text to show.
     */
    public void setDescription (String description) {
        tvDesc.setText(description);
    }

    private void clickOk() {
        //Simple Lock must manage the pin check
        if (key != null) {
            try {
                if (SimpleLockPin.checkPin(this, key, pinCode))
                    mListener.onCorrectPin(this);
                else
                    mListener.onWrongPin(this);
            } catch (NoSuchAlgorithmException e) {
                Toast.makeText(this, getResources().getString(
                        R.string.es_garce_simplelock_save_error), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //The check is managed by the application
            if (mListener.onEnterPin(pinCode))
                mListener.onCorrectPin(this);
            else
                mListener.onWrongPin(this);
        }
        pinCode = "";
        resetCircles();
        btnOk.setEnabled(false);
    }

    private void clickDel() {
        if (pinCode.length() > 0) {
            //Preview
            if (showPreview && indexCircles > 0)
                removeCircle();

            //Update pin
            pinCode = pinCode.substring(0, pinCode.length() - 1);

            //Disable OK button
            if (pinCode.length() < minLength)
                btnOk.setEnabled(false);
        }
    }

    private void clickLongDel() {
        if (pinCode.length() > 0) {
            //Preview
            if (showPreview)
                resetCircles();

            //Update pin
            pinCode = "";

            //Disable OK button
            btnOk.setEnabled(false);
        }
    }

    @SuppressWarnings("deprecation")
    private void addCircle () {
        //Hide placeholder circle
        if (showPlaceholder)
            placeholder[indexCircles].setVisibility(View.GONE);

        ImageView img = new ImageView(this);
        img.setImageDrawable(getResources().getDrawable(R.drawable.lock_circle));
        layoutPin.addView(img, indexCircles);
        indexCircles++;
    }

    private void removeCircle () {
        //Remove view
        if (indexCircles > 0) {
            layoutPin.removeViewAt(--indexCircles);
            //Hide placeholder circle
            if (showPlaceholder)
                placeholder[indexCircles].setVisibility(View.VISIBLE);
        }
    }

    private void resetCircles () {
        layoutPin.removeViews(0, indexCircles);
        if (showPlaceholder)
            for (int i = 0; i < indexCircles; i++) {
                placeholder[i].setVisibility(View.VISIBLE);
            }
        indexCircles = 0;
    }

    protected void shuffleButtons (Button buttons[]){
        List<Button> copy = new ArrayList<>(Arrays.asList(buttons));
        int i = 0;
        while (!copy.isEmpty()) {
            int rnd = (int) (Math.random()*copy.size());
            copy.get(rnd).setText(String.valueOf(i));
            copy.remove(rnd);
            i++;
        }

    }
}
