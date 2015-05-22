package com.tomtom.espresso.test;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;

import com.tomtom.espresso.test.actions.SpoonScreenshotAction;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;

import android.support.test.espresso.EspressoException;
import android.support.test.espresso.NoMatchingViewException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Class containing generic Cucumber test step definitions using Espresso test Instrumentation
 */
public class CucumberHelperTestSteps {

    public static final int RETRY_WAIT = 500;
    private static Scenario scenario;

    @Before
    public static void before(final Scenario scenario) {
        CucumberHelperTestSteps.scenario = scenario;
    }

    public CucumberHelperTestSteps() {
    }

    public static class ScreenshotException extends RuntimeException implements EspressoException {
        private static final long serialVersionUID = -1247022787790657324L;

        ScreenshotException(final String message) {
            super(message);
        }
    }

    public static Scenario getScenario() {
        return CucumberHelperTestSteps.scenario;
    }

    /**
     * Take a screenshot when the test scenario has failed
     */
    public static void takeScreenshotOnFail() {
        if ((scenario != null) && (scenario.isFailed())) {
            takeScreenshot("failed");
        }
    }

    /**
     * Take a screenshot of the current activity and embed it in the HTML report
     * @param tag Name of the screenshot to include in the file name
     */
    public static void takeScreenshot(final String tag) {
        if (scenario == null) {
            throw new ScreenshotException("Error taking screenshot: I'm missing a valid test scenario to attach the screenshot to");
        }
        SpoonScreenshotAction.perform(tag);
        final File screenshot = SpoonScreenshotAction.getLastScreenshot();
        if (screenshot == null) {
            throw new ScreenshotException("Screenshot was not taken correctly, check for failures in screenshot library");
        }
        FileInputStream screenshotStream = null;
        try {
            screenshotStream = new FileInputStream(screenshot);
            final byte fileContent[] = new byte[(int)screenshot.length()];
            screenshotStream.read(fileContent); // Read data from input image file into an array of bytes
            scenario.embed(fileContent, "image/png"); // Embed the screenshot in the report under current test step
        }
        catch (final IOException ioe) {
            throw new ScreenshotException("Exception while reading file " + ioe);
        }
        finally {
            try { // close the streams using close method
                if (screenshotStream != null) {
                    screenshotStream.close();
                }
            }
            catch (final IOException ioe) {
                throw new ScreenshotException("Error while closing stream: " + ioe);
            }
        }
    }

    @Given("^I take a screeshot$")
    public void i_take_a_screenshot() {
        takeScreenshot("screeshot");
    }

    /**
     * Try to press a button with some text or load button if it's in adapter
     * This doesn't guarantee the button is visible to the user, for example:
     * user needs to scroll down to press button
     *
     * @param buttonText
     *            Text of the button to press
     */
    public static void pressButtonWithTextOnce(final String buttonText) {
        try {
            onView(withText(buttonText)).perform(click());
        } catch (final junit.framework.AssertionFailedError e) {
            // When item to click has to be asynchronously loaded in UI from adapter
            onData(hasToString(equalToIgnoringCase(buttonText))).perform(click());
        } catch (final NoMatchingViewException e) {
            // When item to click has to be asynchronously loaded in UI from adapter
            onData(hasToString(equalToIgnoringCase(buttonText))).perform(click());
        } catch (final RuntimeException e) {
            // When item to click has to be asynchronously loaded in UI from adapter
            onData(hasToString(equalToIgnoringCase(buttonText))).perform(click());
        }
    }

    /**
     * Try to press a button with some text and retry for 10 seconds in case it's
     * asynchronously loaded This doesn't guarantee the button is visible
     * to the user, for example: user needs to scroll down to press button
     *
     * @param buttonText
     *            Text of the button to press
     */
    public static void pressButtonWithText(final String buttonText) {
        int retries = 20;
        do {
            try {
                i_see_button_enabled(buttonText, "enabled"); // Only click on enabled buttons
                pressButtonWithTextOnce(buttonText);
                return;
            } catch (final junit.framework.AssertionFailedError e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            } catch (final NoMatchingViewException e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            } catch (final RuntimeException e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            }
        } while (--retries > 0);
        i_see_button_enabled(buttonText, "enabled"); // Only click on enabled buttons
        pressButtonWithTextOnce(buttonText);
    }

    /**
     * Test if some view is displayed and retry for 10 seconds in case it's asynchronously loaded
     *
     * @param id
     *            ID of the view item to test
     */
    public static void checkViewWithIdIsCompletelyDisplayed(final int id) {
        int retries = 20;
        do {
            try {
                onView(withId(id)).check(matches(isCompletelyDisplayed()));
                return;
            } catch (final junit.framework.AssertionFailedError e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            }
        } while (--retries > 0);
        onView(withId(id)).check(matches(isCompletelyDisplayed()));
    }

    /**
     * Test if a view with some text is displayed or try to load it from adapter
     *
     * @param text
     *            Text contained in the view to test
     */
    public static void checkViewWithTextIsCompletelyDisplayedOnce(final String text) {
        try {
            onView(withText(text)).check(matches(isCompletelyDisplayed()));
        } catch (final junit.framework.AssertionFailedError e) {
            // When item to check has to be asynchronously loaded in UI from adapter
            onData(hasToString(equalToIgnoringCase(text))).check(matches(isCompletelyDisplayed()));
        } catch (final NoMatchingViewException e) {
            // When item to check has to be asynchronously loaded in UI from adapter
            onData(hasToString(equalToIgnoringCase(text))).check(matches(isCompletelyDisplayed()));
        } catch (final RuntimeException e) {
            // When item to check has to be asynchronously loaded in UI from adapter
            onData(hasToString(equalToIgnoringCase(text))).check(matches(isCompletelyDisplayed()));
        }
    }

    /**
     * Test if some view is displayed and retry for 10 seconds in case it's asynchronously loaded
     *
     * @param text
     *            Text contained in the view to test
     */
    public static void checkViewWithTextIsCompletelyDisplayed(final String text) {
        int retries = 20;
        do {
            try {
                checkViewWithTextIsCompletelyDisplayedOnce(text);
                return;
            } catch (final junit.framework.AssertionFailedError e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            } catch (final NoMatchingViewException e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            } catch (final RuntimeException e) {
                try { // Retry every half a second during 10 seconds
                    Thread.sleep(RETRY_WAIT);
                } catch (final InterruptedException ex) {
                }
            }
        } while (--retries > 0);
        checkViewWithTextIsCompletelyDisplayedOnce(text);
    }

    @Given("^I press \"(.+)\"$")
    public static void i_press_buttonText(final String buttonText) {
        pressButtonWithText(buttonText);
    }

    @Given("^I press \"(.+)\" ([0-9]+) times$")
    public static void i_press_buttonText(final String buttonText, final int repetitions) {
        for (int i = 0; i < repetitions; i++) {
            pressButtonWithText(buttonText);
        }
    }

    @Given("^I see text \"(.+)\"$")
    public static void i_see_text(final String text) {
        checkViewWithTextIsCompletelyDisplayed(text);
    }

    @Given("^I don't see text \"(.+)\"$")
    public static void i_do_not_see_text(final String text) {
        onView(withText(text)).check(matches(not(isDisplayed())));
    }

    @Given("^text \"(.+)\" does(?: not|n't) exist$")
    public static void text_does_not_exist(final String text) {
        onView(withText(text)).check(doesNotExist());
    }

    @Given("^I press the back button$")
    public static void i_press_back() {
        pressBack();
    }

    @Given("^I press the back button ([0-9]+) times$")
    public static void i_press_back(final int repetitions) {
        for (int i = 0; i < repetitions; i++) {
            i_press_back();
        }
    }

    @Given("^I see \"(.+)\" button (enabled|disabled)$")
    public static void i_see_button_enabled(final String buttonText, final String enabled) {
        if ("enabled".equalsIgnoreCase(enabled)) {
            onView(withText(buttonText)).check(matches(isEnabled()));
        } else {
            onView(withText(buttonText)).check(matches(not(isEnabled())));
        }
    }

    @Given("^I wait for ([0-9]+) seconds?$")
    public static void i_wait_for_seconds(final int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (final InterruptedException e) {
        }
    }
}