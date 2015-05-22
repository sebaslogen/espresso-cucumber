# Espresso + Cucumber + Spoon

Library to test Android applications using Espresso **test framework with cucumber BDD language and Spoon screenshots**.

The objective of this library is to enable UI testing with access to application internals through JUnit while expressed in a natural English language and supported by screenshots when required.

This library is created for use with Eclipse and/or with Ant, to use in Android Studio or with gradle I recommend using gradle version management to fetch libraries from the maven repositories.

**Sample test execution report**
![test execution report image](https://raw.githubusercontent.com/neoranga55/espresso-cucumber/master/report-sample.png)


## Espresso

Android test Instrumentation library created by Google to write concise, beautiful, and reliable Android UI tests

**Key features**:
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

#### Links

https://code.google.com/p/android-test-kit/wiki/Espresso

https://code.google.com/p/android-test-kit/wiki/EspressoV2CheatSheet


## Cucumber

[BDD](http://en.wikipedia.org/wiki/Behavior-driven_development) or behavior-driven development allows developers and testers to write test code that is highly readable and can **turn the tests in the most up to date documentation of the application**.

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

https://github.com/cucumber/cucumber-jvm/

https://github.com/masterthought/cucumber-reporting (Improved cucumber reporting)

http://blog.czeczotka.com/2014/08/17/writing-cucumber-jvm-step-definitions/ (Advanced step definitions with regular expressions in Java-Cucumber)

http://35qk152ejao6mi5pan29erbr9.wpengine.netdna-cdn.com/wp-content/uploads/2011/08/Cucumber-Regular-Expressions-Cheat-Sheet.pdf


## Spoon

[Spoon is a library](https://github.com/square/spoon) from Square to distribute tests across multiple devices but also to take screenshots of the running application.

The use integrated in this library is to **take screenshots automatically when a test fails** and to enable screenshots on demand. In addition, test cases can include screenshots on demand for debugging or inspection purposes.


## Getting started

The code in this repository can be compiled and imported as a library in an Android test project to be used or modified.

A faster method to bootstrap a test application is to use the script in this repository to automatically create a test project for your application using a template

#### Bootstrapping a test project
Execute script espresso.py to generate a new test project for your application.
You need to supply the package name of your application and the initially tested Activity, see below:
```java
./espresso.py -h
usage: espresso.py [-h] [-d DESTINATION_PATH] [-p PROJECT_NAME] [-a ACTIVITY]
                   action element package
 
Create test project with Cucumber and Espresso from template    
For Espresso library examples and documentation visit: https://code.google.com/p/android-test-kit/wiki/EspressoSamples
 
positional arguments:
  action                The action to perform (e.g. 'generate' or just 'g')
  element               The element to perform the action (e.g. 'test' or just 't')
  package               The package of the target application to test (e.g. com.tomtom.pnd.firstrunwizard)
 
optional arguments:
  -h, --help            show this help message and exit
  -d DESTINATION_PATH, --destination-path DESTINATION_PATH
                        Path inside which the test project will be created and placed (e.g. .../MyAppProject/test)
  -p PROJECT_NAME, --project-name PROJECT_NAME
                        Name of the project to test (last part of package name is used by default)
  -a ACTIVITY, --default-activity ACTIVITY
                        Name of the main activity to test (test will instrument this activity as starting point, e.g. HomeActivity)
```

Example to generate a test from example application 'ShopDemoApp':
```java
cd espresso-test-lib
./espresso.py g t com.tomtom.pnd.shopdemo -p ShopDemoApp -a ShopDemoVideoActivity -d ../../pndapps/Apps/ShopDemoApp/test
```

## Running a test

The execution follows the same procedure as any [Android instrumentation test](http://developer.android.com/tools/testing/testing_android.html) with this basic two steps:

1. Compile and install test project (this should automatically compile the application the you're going to test)
2. Execute Android instrumentation from command line using ADB.
Example: `adb shell am instrument -r -w com.tomtom.pnd.firstrunwizard.test/com.tomtom.espresso.test.EspressoInstrumentation`

More information: http://www.kandroid.org/online-pdk/guide/instrumentation_testing.html


## Technical details of mixing Espresso and Cucumber

The integration happens in the **[EspressoInstrumentation](https://github.com/neoranga55/espresso-cucumber/blob/master/espresso-test-lib/EspressoInstrumentation/src/com/tomtom/espresso/test/EspressoInstrumentation.java) class**, this is where the **AndroidJUnitRunner** Instrumentation needs to be extended to add Cucumber features. By creating a **CucumberInstrumentationCore** object and providing the bundle to this object, Cucumber can start the tests and take control of the testing flow when the test instrumentation is started.

Finally, the configuration file that selects which test definition classes will be used and which feature files will be executed is defined in a class with the tag `@RunWith(Cucumber.class)`. There is a nice and simple example in the  **[RunCucumberTest](https://github.com/neoranga55/espresso-cucumber/blob/master/sample-test-project/FirstRunWizardEspressoTest/src/java/com/tomtom/pnd/firstrunwizard/test/RunCucumberTest.java)** class.

_Warning: The combination of Espresso and Cucumber could produce a horrible taste on human beings, please consider  applying it only to Androids._


## Licenses

This library was produced internally at TomTom but is released under Apache license 2.0

- Espresso: Apache 2.0 https://code.google.com/p/android-test-kit/
- Cucumber: https://github.com/cucumber/cucumber-jvm/blob/master/LICENCE
- Spoon: Apache 2.0 https://github.com/square/spoon/blob/master/LICENSE.txt
