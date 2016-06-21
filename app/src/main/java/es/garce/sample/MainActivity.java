package es.garce.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import es.garce.simplelock.OnEnterPin;
import es.garce.simplelock.OnNewPin;
import es.garce.simplelock.SimpleLock;
import es.garce.simplelock.SimpleLockActivity;
import es.garce.simplelock.SimpleLockNewActivity;

public class MainActivity extends AppCompatActivity implements OnEnterPin, OnNewPin {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SimpleLock.configure(this, true, true, 1, 4, true);
        } catch (SimpleLock.SimpleLockConfigurationError simpleLockConfigurationError) {
            simpleLockConfigurationError.printStackTrace();
        }
    }

    public void newPin (View view) {
        SimpleLockNewActivity.newPin(this, this, -1, "MAIN");
    }

    public void secret (View view) {
        SimpleLockActivity.requestPin(this, this, "MAIN");
    }

    @Override
    public boolean onEnterPin(String pinCode) {
        return false;
    }

    @Override
    public void onCorrectPin(SimpleLockActivity simpleLockActivity) {
        Log.e("MAIN", "Correct");
        simpleLockActivity.finish();
    }

    @Override
    public void onWrongPin(SimpleLockActivity simpleLockActivity) {
        Log.e("MAIN", "Wrong");
    }

    @Override
    public void onBack(SimpleLockActivity simpleLockActivity) {
        Log.e("MAIN", "Back");
        simpleLockActivity.finish();
    }

    @Override
    public boolean onDone(SimpleLockNewActivity simpleLockActivity, String pin) {
        Log.e("MAIN", "Saved");
        //simpleLockActivity.finish();
        return true;
    }

    @Override
    public void onError(SimpleLockNewActivity simpleLockActivity, int code) {
        Log.e("MAIN", "Error new: "+code);
    }

    @Override
    public void onSaved(SimpleLockNewActivity simpleLockActivity, String pin) {
        simpleLockActivity.finish();
    }
}
