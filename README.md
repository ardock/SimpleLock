# Simplelock
A simple lock library for Android. Make easy to add pin codes to your application.

With this library:
- You can have your pin codes working in **1 minute**. Read [quick start](README.md#quick-start) in order to use the basics of the library
- Have a set of **cool configuration** parameters
- You will find all the classes and methods **well documented**
- **Customize** the UI to fit in your app: all UI defaults can be overwritten to make it look as you want
- You can save effort letting Simple Lock to **manage the pin codes persistence**
- **Security**: Simple Lock always hash the pins before saving

##Installation
Add these line to your dependencies section in your `build.gradle` file.
```
compile 'es.garce.simplelock:simplelock:1.0.1'
```

Example:
```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile 'es.garce.simplelock:simplelock:1.0.1'
}
```

##Quick Start
There are 3 important classes to know:
>SimpleLock: Used to configure and save your settings

>SimpleLockActivity: The Activity that will ask for the pin

>SimpleLockNewActivity: The Activity that will let you set your new pin

#####Setup your preferences with SimpleLock:
```
SimpleLock.configure(this, true, true, 1, 6, true);
```
This will save these values on application preference and will be the default values
- Show pin placeholder
- Show pin preview
- Minimun length of 1
- Maximum length of 6
- Shuffle buttons when asking for pin code

Prevent to overwrite user settings with your app default every time your app is launched
```
if (!SimpleLock.isInit(this))
    SimpleLock.configure(this, true, true, 1, 6, true);
```

#####Create a new pin:
```
        SimpleLockNewActivity.newPin(this, new OnNewPin() {
            @Override
            public boolean onDone(SimpleLockNewActivity simpleLockActivity, String pin) {
                return true;
            }

            @Override
            public void onError(SimpleLockNewActivity simpleLockActivity, int code) {
                Toast.makeText(MainActivity.this, "Error nuevo pin: "+code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSaved(SimpleLockNewActivity simpleLockActivity, String pin) {
                simpleLockActivity.finish();
                Toast.makeText(MainActivity.this, "Nuevo pin: "+pin, Toast.LENGTH_SHORT).show();
            }
        }, 1, "MAIN");
```
Listener interface implementation created inline. You can always implement the interface in your Activity and call the pin as follow:
```
        SimpleLockNewActivity.newPin(this, this, 1, "MAIN");
```
Or implement it on other class
```
        SimpleLockNewActivity.newPin(this, MyOnNewPinImplementation, 1, "MAIN");
```

#####Ask the user for a pin:
```
        SimpleLockActivity.requestPin(this, new OnEnterPin() {
            @Override
            public boolean onEnterPin(String pinCode) {
                return false;
            }

            @Override
            public void onCorrectPin(SimpleLockActivity simpleLockActivity) {
                simpleLockActivity.finish();
                Toast.makeText(MainActivity.this, "Pin correcto", Toast.LENGTH_SHORT).show();
                //You can launch your secret activity here!!
            }

            @Override
            public void onWrongPin(SimpleLockActivity simpleLockActivity) {
                Toast.makeText(MainActivity.this, "Pin incorrecto", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBack(SimpleLockActivity simpleLockActivity) {
                simpleLockActivity.finish();
            }
        }, "MAIN");
```
You are asking for the `MAIN` pin previously saved.

#####Et voilà! You have secured your application.