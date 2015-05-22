package com.tomtom.pnd.firstrunwizard.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Configuration class used to trigger Cucumber feature files
 * This class glues test step definitions from the test project and from the custom espresso test library
*/
@CucumberOptions(   features = "features",
                    glue = {"com.tomtom.pnd.firstrunwizard.test", "com.tomtom.espresso.test"},
                    format = {  "pretty",
                                "html:/data/data/com.tomtom.pnd.firstrunwizard/cucumber-reports/report.html",
                                "json:/data/data/com.tomtom.pnd.firstrunwizard/cucumber-reports/cucumber.json",
                                "junit:/data/data/com.tomtom.pnd.firstrunwizard/cucumber-reports/cucumber.xml"
                            }
                )
@RunWith(Cucumber.class)
public class RunCucumberTest {

}