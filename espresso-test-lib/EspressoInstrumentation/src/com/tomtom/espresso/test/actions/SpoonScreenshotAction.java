package com.tomtom.espresso.test.actions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import com.squareup.spoon.Spoon;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import java.io.File;

public final class SpoonScreenshotAction implements ViewAction {
    private final String tag;
    private final String testClass;
    private final String testMethod;
    private static File lastScreenshot;

    /**
     * Initialize with information required to take a screenshot
     * @param tag Name of the screenshot to include in the file name
     * @param testClass Name of the class taking the screenshot (required by Spoon library)
     * @param testMethod Name of the method taking the screenshot
     */
    public SpoonScreenshotAction(final String tag, final String testClass, final String testMethod) {
        this.tag = tag;
        this.testClass = testClass;
        this.testMethod = testMethod;
    }

    @Override
    public Matcher<View> getConstraints() {
        return Matchers.anything();
    }

    @Override
    public String getDescription() {
        return "Taking a screenshot using spoon.";
    }

    @Override
    public void perform(final UiController uiController, final View view) {
        lastScreenshot = Spoon.screenshot(getActivity(view), tag, testClass, testMethod);
    }

    public static File getLastScreenshot() {
        return lastScreenshot;
    }

    /**
     * Get the activity from the context of the view
     * @param view View from which the activity will be inferred
     * @return Activity that contains the given view
     */
    private static Activity getActivity(final View view) {
        Context context = view.getContext();
        while (!(context instanceof Activity)) {
            if (context instanceof ContextWrapper) {
                context = ((ContextWrapper) context).getBaseContext();
            } else {
                throw new IllegalStateException("Got a context of class " + context.getClass() + " and I don't know how to get the Activity from it");
            }
        }
        return (Activity) context;
    }

    /**
     * Espresso action to be take a screenshot of the current activity
     * This must be called directly from the test method
     * @param tag Name of the screenshot to include in the file name
     */
    public static void perform(final String tag) {
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        final String testClass = trace[3].getClassName();
        final String testMethod = trace[3].getMethodName();
        onView(isRoot()).perform(new SpoonScreenshotAction(tag, testClass, testMethod));
    }
}
