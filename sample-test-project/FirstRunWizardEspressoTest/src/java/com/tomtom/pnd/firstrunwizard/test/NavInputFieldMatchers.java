package com.tomtom.pnd.firstrunwizard.test;

import com.tomtom.navui.controlport.NavInputField.Attributes;
import com.tomtom.navui.controlport.NavInputField.InputFieldMode;
import com.tomtom.navui.stockcontrolport.StockInputField;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

public final class NavInputFieldMatchers {

    public static Matcher<View> withNormalState() {
        return new BoundedMatcher<View, StockInputField>(StockInputField.class) {
            @Override
            public boolean matchesSafely(final StockInputField inputField) {
                final InputFieldMode inputMode = inputField.getModel().getEnum(Attributes.INPUT_MODE);

                return InputFieldMode.NORMAL.equals(inputMode);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("with normal state");
            }
        };
    }


    public static Matcher<View> withWarningState() {
        return new BoundedMatcher<View, StockInputField>(StockInputField.class) {
            @Override
            public boolean matchesSafely(final StockInputField inputField) {
                final InputFieldMode inputMode = inputField.getModel().getEnum(Attributes.INPUT_MODE);

                return InputFieldMode.WARN.equals(inputMode);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("with warning state");
            }
        };
    }
}