# Espresso + Cucumber + Spoon

Library to test Android applications using Espresso test framework with cucumber BDD language and Spoon screenshots

The objective of this library is to enable UI testing with access to application internals through JUnit but expressed in a natural English language and supported by screenshots when needed

This library is created for use with Eclipse and/or with Ant, to use in Android Studio or with gradle I recommend using gradle versions management to fetch libraries from the maven repositories


## Espresso

Android test Instrumentation library created by Google to write concise, beautiful, and reliable Android UI tests

https://code.google.com/p/android-test-kit/wiki/Espresso

Key features:
+ Damn fast
+ Less flaky tests (thanks to automatic synchronization with UI thread)
+ Better code (clearner, shoter and more readable code) thanks to the use of hamcrest expressions
+ Less bolierplate code (thanks mainly to the two points above you need less code to get the job done)

#### Sample code
Sample to start an Activity, click a button and then check some text appears:
```java
getActivity();
onView(withText("Show text")).perform(click());
onView(withText("Expected text").check(matches(isDisplayed()));
```


## Cucumber

[BDD](http://en.wikipedia.org/wiki/Behavior-driven_development) or behavior-driven development allows developers and testers to write test code that is highly readable and can *turn the tests in the most up to date documentation of the application*.

Cucumber is a thin library that enables abstraction of Java code into BDD language.

#### Sample code
Example of Cucumber feature test (that could be designed in collaboration with product owner):
```java
Feature: First Run Wizard
  Test the first run wizard flow when user starts the device for the first time
  
  Scenario: Happy flow to complete the first run wizard
    Given I press "United Kingdom"
    Then I see text "Terms and Conditions"
```

And the Java code to support it: 
```java
@Given("^I press \"(.+)\"$")
public void i_press_buttonText(final String buttonText) {
    onView( withText( buttonText ) ).perform( click() );
}
 
@Given("^I see text \"(.+)\"$")
public void i_see_text(final String text) {
    onView( withText( text ) ).check( matches( isDisplayed() ) );
}
```

#### Links

https://cukes.info/ (Cucumber)

https://github.com/masterthought/cucumber-reporting (Improve cucumber reporting)

http://blog.czeczotka.com/2014/08/17/writing-cucumber-jvm-step-definitions/ (Advanced step definitions with regular expressions in Java-Cucumber)

http://35qk152ejao6mi5pan29erbr9.wpengine.netdna-cdn.com/wp-content/uploads/2011/08/Cucumber-Regular-Expressions-Cheat-Sheet.pdf


## Spoon

[Spoon is a library](https://github.com/square/spoon) from Square to distribute tests across multiple devices but also to take screenshots of the running application.

The use integrated in this library is to *take screenshots automatically when a test fails* and to enable screenshots on demand. In addition, test cases can include screenshots on demand for debugging or inspection purposes.