package com.tomtom.espresso.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import cucumber.api.android.CucumberInstrumentationCore;

/**
 * A simple extension of the {@link android.support.test.runner.AndroidJUnitRunner} utilizing the {@link cucumber.api.android.CucumberInstrumentationCore}.
 */
public class EspressoInstrumentation extends android.support.test.runner.AndroidJUnitRunner {

    private static final String TAG = "EspressoInstrumentation";
    private static final String ANIMATION_PERMISSION = "android.permission.SET_ANIMATION_SCALE";

    /**
     * The {@link cucumber.api.android.CucumberInstrumentationCore} which will run the actual logic using this {@link android.support.test.runner.AndroidJUnitRunner}
     * implementation.
     */
    private CucumberInstrumentationCore cucumberInstrumentationCore = new CucumberInstrumentationCore(this);


    @Override
    public void onCreate(final Bundle bundle) {
        cucumberInstrumentationCore.create(bundle);
        super.onCreate(bundle);
    }

    @Override
    public void onStart() {
        cucumberInstrumentationCore.start();
    }

    private void disableAnimation() {
        final int permStatus = getContext().checkCallingOrSelfPermission(ANIMATION_PERMISSION);
        if (permStatus == PackageManager.PERMISSION_GRANTED) {
            if (reflectivelyDisableAnimation()) {
                Log.i(TAG, "All animations disabled.");
            } else {
                Log.i(TAG, "Could not disable animations.");
            }
        } else {
            Log.i(TAG, "Cannot disable animations due to lack of permission.");
        }
    }

    private boolean reflectivelyDisableAnimation() {
        try {
            final Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            final Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
            final Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            final Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            final Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            final Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales",
              float[].class);
            final Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            final IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            final Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            for (int i = 0; i < currentScales.length; i++) {
                currentScales[i] = 0.0f;
            }
            setAnimationScales.invoke(windowManagerObj, currentScales);
            return true;
        } catch (final ClassNotFoundException cnfe) {
            Log.w(TAG, "Cannot disable animations reflectively.", cnfe);
        } catch (final NoSuchMethodException mnfe) {
            Log.w(TAG, "Cannot disable animations reflectively.", mnfe);
        } catch (final SecurityException se) {
            Log.w(TAG, "Cannot disable animations reflectively.", se);
        } catch (final InvocationTargetException ite) {
            Log.w(TAG, "Cannot disable animations reflectively.", ite);
        } catch (final IllegalAccessException iae) {
            Log.w(TAG, "Cannot disable animations reflectively.", iae);
        } catch (final RuntimeException re) {
            Log.w(TAG, "Cannot disable animations reflectively.", re);
        }
        return false;
    }

}