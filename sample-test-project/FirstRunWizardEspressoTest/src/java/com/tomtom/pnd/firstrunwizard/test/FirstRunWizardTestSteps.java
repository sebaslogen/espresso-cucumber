package com.tomtom.pnd.firstrunwizard.test;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.tomtom.espresso.test.CucumberHelperTestSteps.checkViewWithIdIsCompletelyDisplayed;
import static com.tomtom.espresso.test.CucumberHelperTestSteps.checkViewWithTextIsCompletelyDisplayedOnce;
import static com.tomtom.espresso.test.CucumberHelperTestSteps.takeScreenshotOnFail;
import static com.tomtom.pnd.firstrunwizard.test.NavInputFieldMatchers.withNormalState;
import static com.tomtom.pnd.firstrunwizard.test.NavInputFieldMatchers.withWarningState;
import static org.hamcrest.Matchers.allOf;

import com.tomtom.pnd.firstrunwizard.FirstRunWizardActivity;
import com.tomtom.pnd.firstrunwizard.R;
import com.tomtom.pnd.settingslib.PndSettings;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.support.test.espresso.EspressoException;
import android.support.test.espresso.NoMatchingViewException;
import android.test.ActivityInstrumentationTestCase2;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.AssertionFailedError;

/**
 * Test steps for the host activity.
 */
public class FirstRunWizardTestSteps extends ActivityInstrumentationTestCase2<FirstRunWizardActivity> {
    private static final String TAG = "FirstRunWizardTestSteps";

    public static class MissingValuesException extends RuntimeException implements EspressoException {
        private static final long serialVersionUID = -1247022787790935324L;

        MissingValuesException(final String message) {
            super(message);
        }
    }

    // Holds the activity under test
    private FirstRunWizardActivity mActivity;
    private Application mApplication;
    private final ActivityLifecycleCallbacks mActivityLifecycleCallback;
    private CountDownLatch mDoneSignal;

    private static final String SCREEN_NAME_LANGUAGE_SELECTION = "language selection";
    private static final String SCREEN_NAME_COUNTRY_SELECTION = "country selection";
    private static final String SCREEN_NAME_VEHICLE_TYPE_SELECTION = "vehicle type selection";

    private static final String TOGGLE_EU_EXPLOSIVE = "eu explosive";
    private static final String TOGGLE_EU_HARMFUL = "eu harmful to water";
    private static final String TOGGLE_EU_GENERAL = "eu general";
    private static final int CLOSE_ACTIVITY_WAIT = 1000;

    private static HashMap<String, Integer> INPUT_FIELD_MAP = new HashMap<String, Integer>();
    static {
        INPUT_FIELD_MAP.put("vehicle length", Integer.valueOf(R.id.navui_vehicleProfileLengthInput));
        INPUT_FIELD_MAP.put("vehicle width", Integer.valueOf(R.id.navui_vehicleProfileWidthInput));
        INPUT_FIELD_MAP.put("vehicle height", Integer.valueOf(R.id.navui_vehicleProfileHeightInput));
        INPUT_FIELD_MAP.put("vehicle gross weight", Integer.valueOf(R.id.navui_vehicleProfileWeightInput));
        INPUT_FIELD_MAP.put("vehicle axle weight", Integer.valueOf(R.id.navui_vehicleProfileAxleWeightInput));
        INPUT_FIELD_MAP.put("vehicle max speed", Integer.valueOf(R.id.navui_vehicleProfileMaxSpeedInput));
    }

    private Map<String, String> mVehicleProfileValues;

    public FirstRunWizardTestSteps() {
        super(FirstRunWizardActivity.class);
        mActivityLifecycleCallback = new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityDestroyed(final Activity activity) {
                if (Log.D) Log.d(TAG, "onActivityDestroyed()");
                if (mDoneSignal != null) {
                    mDoneSignal.countDown();
                }
                if (mApplication != null) {
                    mApplication.unregisterActivityLifecycleCallbacks(this);
                }
            }

            @Override
            public void onActivityStopped(final Activity activity) {
            }

            @Override
            public void onActivityStarted(final Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
            }

            @Override
            public void onActivityResumed(final Activity activity) {
            }

            @Override
            public void onActivityPaused(final Activity activity) {
            }

            @Override
            public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
            }
        };
    }

    /**
     * Modify the private variable of FirstRunWizardActivity to control the value. This should
     * really be a mocked interface, but for one variable this works ok.
     * @param hasVehicleProfileSupport Enable Vehicle profile flow if true
     * @return True when successful
     */
    private final boolean setVehicleProfileSupport(final boolean hasVehicleProfileSupport) {
        try {
            final Field field = FirstRunWizardActivity.class.getDeclaredField("mSupportsVehicleProfiles");
            field.setAccessible(true);
            field.set(mActivity, hasVehicleProfileSupport);
            return true;
        } catch (final NoSuchFieldException e) {
            fail("Could not find field mSupportsVehicleProfiles in FirstRunWizardActivity");
        } catch (final IllegalArgumentException e) {
            fail("Illegal argument for mSupportsVehicleProfiles field in FirstRunWizardActivity");
        } catch (final IllegalAccessException e) {
            fail("Not allowed to change mSupportsVehicleProfiles field in FirstRunWizardActivity");
        }

        return false;
    }

    private void scroll(final String scrollDirection) {
        if ("up".equals(scrollDirection)) {
            onView(withId(R.id.navui_upButton)).perform(click());
        } else {
            onView(withId(R.id.navui_downButton)).perform(click());
        }
    }

    @Before
    public void before() {
        PndSettings.setUseMockMode(true); // Disable writing to the actual settings
    }

    @After
    public void after() {
        takeScreenshotOnFail();
        if (mActivity != null) { // Close activity after each test scenario
            if (!mActivity.isFinishing()) {
                mActivity.finish();
            }
            try {
                if (mDoneSignal != null) {
                    if (!mDoneSignal.await(10, TimeUnit.SECONDS)) {
                        if (Log.D) Log.d(TAG, "FirstRunWizard activity didn't finish properly in the given time");
                    }
                }
            } catch (final InterruptedException ex) {
                if (Log.D) Log.d(TAG, "FirstRunWizard activity didn't finish properly in the given time");
            }
        }
        mDoneSignal = null;
    }

    @Given("^I enable storing First Run Wizard changes into settings$")
    public void i_enable_store_of_settings() {
        PndSettings.setUseMockMode(false);
    }

    @Given("^I start First Run Wizard$")
    public void i_start_first_run_wizard() {
        mActivity = getActivity();
        setVehicleProfileSupport(false); // By default vehicle profile selection is disable in PND
        mDoneSignal = new CountDownLatch(1);
        if (mActivity != null) {
            if (mApplication == null) {
                mApplication = mActivity.getApplication();
            }
            if (mApplication != null) {
                mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallback);
            }
        }
    }

    @Given("^I see the \"(.+)\" screen$")
    public void i_see_screenName_screen(final String screenName) {
        if (SCREEN_NAME_LANGUAGE_SELECTION.equals(screenName)) {
            checkViewWithIdIsCompletelyDisplayed(R.id.language_selector);
        } else if (SCREEN_NAME_COUNTRY_SELECTION.equals(screenName)) {
            checkViewWithIdIsCompletelyDisplayed(R.id.country_selector);
        } else if (SCREEN_NAME_VEHICLE_TYPE_SELECTION.equals(screenName)) {
            checkViewWithIdIsCompletelyDisplayed(R.id.vehicletype_list);
        } else {
            throw new IllegalArgumentException("Screen name not recognized:" + screenName);
        }
    }

    @Given("^I select \"(.+)\" language$")
    public void i_select_languageName_language(final String languageName) {
        onView(withId(R.id.btnAllLanguages)).perform(click());
        i_scroll_to_item_with_text("down", languageName);
        onView(withText(languageName)).perform(click());
    }

    @Given("^tested activity is closed$")
    public void tested_activity_is_closed() {
        if (!mActivity.isFinishing()) {
            throw new IllegalArgumentException("Application is still running");
        }
    }

    @Given("^I enable vehicle profile support$")
    public void enable_vehicle_profile_support() {
        setVehicleProfileSupport(true);
    }

    @Given("^I scroll (up|down) to item with text \"(.+)\"$")
    public void i_scroll_to_item_with_text(final String scrollDirection, final String text) {
        int maxScroll = 10; // Scroll until item is completely visible or maxScroll
        while (maxScroll-- > 0) {
            try {
                checkViewWithTextIsCompletelyDisplayedOnce(text);
                return; // Item is fully scrolled into view
            } catch (final AssertionFailedError e) {
                scroll(scrollDirection);
            } catch(final NoMatchingViewException e) {
                scroll(scrollDirection);
            } catch(final RuntimeException e) {
                scroll(scrollDirection);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Given("^I press \"(.+)\" toggle$")
    public void i_press_toggle(final String resourceName) {
        final int id = extractToggleResourceIdFromText(resourceName);
        onView(allOf(withId(R.id.navui_toggleButton), isDescendantOfA(withId(id)))).perform(click());
    }

    @SuppressWarnings("unchecked")
    @Given("^I see \"(.+)\" toggle (enabled|disabled)$")
    public void i_press_toggle(final String resourceName, final String enabled) {
        final int id = extractToggleResourceIdFromText(resourceName);
        if ("enabled".equalsIgnoreCase(enabled)) {
            onView(allOf(withId(R.id.navui_toggleButton), isDescendantOfA(withId(id)))).check(matches(isChecked()));
        } else {
            onView(allOf(withId(R.id.navui_toggleButton), isDescendantOfA(withId(id)))).check(matches(isNotChecked()));
        }
    }

    @Given("^I see \"(.+)\" hazmat icon$")
    public void i_see_hazmat_icon(final String iconName) {
        if (TOGGLE_EU_EXPLOSIVE.equalsIgnoreCase(iconName)) {
            onView(withId(R.id.navui_vehicleProfileHazmatEuExplosiveIcon)).check(matches(isCompletelyDisplayed()));
        }
    }

    private int extractToggleResourceIdFromText(final String resourceName) {
        String resourceId = "";
        if (TOGGLE_EU_EXPLOSIVE.equalsIgnoreCase(resourceName)) {
            resourceId = "navui_hazmatEuExplosiveToggle";
        } else if (TOGGLE_EU_HARMFUL.equalsIgnoreCase(resourceName)) {
            resourceId = "navui_hazmatEuHarmfulToWaterToggle";
        } else if (TOGGLE_EU_GENERAL.equalsIgnoreCase(resourceName)) {
            resourceId = "navui_hazmatEuGeneralToggle";
        }
        return mActivity.getResources().getIdentifier(resourceId, "id", mActivity.getPackageName());
    }

    @Given("^I enter text \"(.+)\" into input field \"(.+)\"$")
    public void enter_text_into_input_field(final String text, final String inputField) {
        final Integer inputFieldResourceId = INPUT_FIELD_MAP.get(inputField);

        if (inputFieldResourceId != null) {
            onView(withId(inputFieldResourceId.intValue())).perform(scrollTo(), clearText(), typeText(text));
        } else {
            throw new IllegalArgumentException("Inputfield with name " + inputField + " not found!");
        }
    }

    @Given("^I hide the keyboard$")
    public void hide_keyboard() {
        closeSoftKeyboard();
    }

    @Given("^I check input field \"(.+)\" contains the text \"(.+)\"$")
    public void check_input_field_text(final String inputField, final String text) {
        final Integer inputFieldResourceId = INPUT_FIELD_MAP.get(inputField);

        if (inputFieldResourceId != null) {
            onView(withId(inputFieldResourceId.intValue())).check(matches(withText(text)));
        } else {
            throw new IllegalArgumentException("Inputfield with name " + inputField + " not found!");
        }
    }

    @Given("^I check that the input field \"(.+)\" is in normal state$")
    public void check_input_field_state_normal(final String inputField) {
        final Integer inputFieldResourceId = INPUT_FIELD_MAP.get(inputField);

        if (inputFieldResourceId != null) {
            onView(withId(inputFieldResourceId.intValue())).check(matches(withNormalState()));
        } else {
            throw new IllegalArgumentException("Input field with name " + inputField + " not found!");
        }
    }

    @Given("^I check that the input field \"(.+)\" is in warning state$")
    public void check_input_field_state_warning(final String inputField) {
        final Integer inputFieldResourceId = INPUT_FIELD_MAP.get(inputField);

        if (inputFieldResourceId != null) {
            onView(withId(inputFieldResourceId.intValue())).check(matches(withWarningState()));
        } else {
            throw new IllegalArgumentException("Input field with name " + inputField + " not found!");
        }
    }

    @Given("^these vehicle profile values$")
    public void vehicle_profile_values_list(final Map<String, String> values) {
        mVehicleProfileValues = values;
    }

    @Given("^I enter the above values into the input fields$")
    public void enter_stored_values() {
        if (mVehicleProfileValues != null) {
            for (final String inputName : mVehicleProfileValues.keySet()) {
                enter_text_into_input_field(mVehicleProfileValues.get(inputName), inputName);
            }
        } else {
            throw new MissingValuesException("No vehicle profile values were set, supply them first");
        }
    }

    @Given("^I enter above values into the input fields they should be in warning state$")
    public void check_values_for_warning() {
        if (mVehicleProfileValues != null) {
            for (final String inputName : mVehicleProfileValues.keySet()) {
                enter_text_into_input_field(mVehicleProfileValues.get(inputName), inputName);
                check_input_field_state_warning(inputName);
            }
        } else {
            throw new MissingValuesException("No vehicle profile values were set, supply them first");
        }
    }

    @Given("^I expect all input fields to have the above values$")
    public void check_input_field_values() {
        if (mVehicleProfileValues != null) {
            for (final String inputName : mVehicleProfileValues.keySet()) {
                check_input_field_text(inputName, mVehicleProfileValues.get(inputName));
            }
        } else {
            throw new MissingValuesException("No vehicle profile values were set, supply them first");
        }
    }

    @Given("^there is a vehicle profile stored with the following settings:$")
    public void check_stored_vehicle_profile(final Map<String, String> values) {
        final VehicleProfile profile = mActivity.getVehicleProfile();

        for (final Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getKey().equals("type")) {
                if (!profile.getVehicleType().name().equals(entry.getValue())) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ profile.getVehicleType().name());
                }
                continue;
            }
            if (entry.getKey().equals("length")) {
                if (!profile.getLength().getValue().equals(Long.valueOf(entry.getValue()))) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ String.valueOf(profile.getLength().getValue()));
                }
                continue;
            }
            if (entry.getKey().equals("width")) {
                if (!profile.getWidth().getValue().equals(Long.valueOf(entry.getValue()))) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ String.valueOf(profile.getWidth().getValue()));
                }
                continue;
            }
            if (entry.getKey().equals("height")) {
                if (!profile.getHeight().getValue().equals(Long.valueOf(entry.getValue()))) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ String.valueOf(profile.getHeight().getValue()));
                }
                continue;
            }
            if (entry.getKey().equals("weight")) {
                if (!profile.getTotalWeight().getValue().equals(Long.valueOf(entry.getValue()))) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ String.valueOf(profile.getTotalWeight().getValue()));
                }
                continue;
            }
            if (entry.getKey().equals("axleweight")) {
                if (!profile.getAxleWeight().getValue().equals(Long.valueOf(entry.getValue()))) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ String.valueOf(profile.getAxleWeight().getValue()));
                }
                continue;
            }
            if (entry.getKey().equals("maxspeed")) {
                if (!profile.getMaxSpeed().getValue().equals(Long.valueOf(entry.getValue()))) {
                    throw new IllegalArgumentException("Value for "+ entry.getKey() + " does not match. Expected: "+
                            entry.getValue() + " Actual: "+ String.valueOf(profile.getMaxSpeed().getValue()));
                }
                continue;
            }

            throw new IllegalArgumentException("Entry "+ entry.getKey() + " is not handled");
        }
    }
}
