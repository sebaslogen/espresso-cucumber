Feature: First Run Wizard
  Test the first run wizard flow
 
  @TestIdUserStory("FNK-35")
  Scenario: Happy flow to complete the first run wizard
    Given I start First Run Wizard
    Then I see the "language selection" screen
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I scroll down to item with text "United Kingdom"
    When I press "United Kingdom"
    And I press "Next"
    Then I see text "Terms and Conditions"
    When I press "I Agree"
    Then I see text "Your Information & Privacy"
    When I press "No"
    Then I see text "Don't Send Information"
    When I press the back button 4 times
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I press "Next"
    Then I see text "Terms and Conditions"
    And I press "I Agree"
    Then I see text "Your Information & Privacy"
    And I press "No"
    And I enable storing First Run Wizard changes into settings
    When I press "Confirm"
    Then tested activity is closed

  @TestIdUserStory("FNK-35")
  Scenario: Happy flow to complete the first run wizard for a truck device
    Given I start First Run Wizard
    And I enable vehicle profile support
    Then I see the "language selection" screen
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I scroll down to item with text "United Kingdom"
    When I press "United Kingdom"
    And I press "Next"
    Then I see the "vehicle type selection" screen
    And I scroll down to item with text "Truck"
    And I press "Truck"
    And I press "Next"
    Then I see text "My Vehicle"
    And I press "Next"
    Then I see text "Terms and Conditions"
    When I press "I Agree"
    Then I see text "Your Information & Privacy"
    When I press "No"
    Then I see text "Don't Send Information"
    When I press the back button 6 times
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I press "Next"
    Then I see the "vehicle type selection" screen
    And I press "Next"
    Then I see text "My Vehicle"
    And I press "Next"
    Then I see text "Terms and Conditions"
    And I press "I Agree"
    Then I see text "Your Information & Privacy"
    And I press "No"
    And I enable storing First Run Wizard changes into settings
    When I press "Confirm"
    Then tested activity is closed

  @TestIdUserStory("FNK-33")
  Scenario: Only Truck device can see and change hazmat values in the vehicle profile
    Given I start First Run Wizard
    And I enable vehicle profile support
    Then I see the "language selection" screen
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I scroll down to item with text "United Kingdom"
    When I press "United Kingdom"
    And I press "Next"
    Then I see the "vehicle type selection" screen
    And I scroll down to item with text "Truck"
    And I press "Truck"
    And I press "Next"
    And I see text "My Vehicle"
    And I scroll down to item with text "HAZMAT"
    When I press "HAZMAT"
    Then I see text "Setup Hazardous Materials"
    When I press "EU explosive" toggle
    Then I see "EU explosive" toggle enabled
    And I see "EU harmful to water" toggle disabled
    And I see "EU general" toggle disabled
    And I see text "Explosive materials"
    And I see text "Goods harmful to water"
    And I see text "General hazardous materials"
    When I press the back button
    Then I see text "My Vehicle"
    And I see "EU Explosive" hazmat icon
    When I press the back button
    And I scroll up to item with text "Car"
    And I press "Car"
    And I press "Next"
    Then I don't see text "HAZMAT"

  @TestIdUserStory("FNK-51")
  Scenario: Enter vehicle profile screen and check that the vehicle type matches the one select in the vehicle type selection screen 
    Given I start First Run Wizard
    And I enable vehicle profile support
    Then I see the "language selection" screen
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I scroll down to item with text "United Kingdom"
    When I press "United Kingdom"
    And I press "Next"
    Then I see the "vehicle type selection" screen
    And I scroll down to item with text "Truck"
    And I press "Truck"
    And I press "Next"
    Then I see text "My Vehicle"
    And I see text "Truck"
    When I press the back button
    And I scroll up to item with text "Taxi"
    And I press "Taxi"
    And I press "Next"
    Then I see text "My Vehicle"
    And I see text "Taxi"

  @TestIdUserStory("FNK-51")
  Scenario: Enter vehicle profile screen and click vehicle type to change vehicle type. Check that the type changed 
    Given I start First Run Wizard
    And I enable vehicle profile support
    Then I see the "language selection" screen
    When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I scroll down to item with text "United Kingdom"
    When I press "United Kingdom"
    And I press "Next"
    Then I see the "vehicle type selection" screen
    And I scroll down to item with text "Truck"
    And I press "Truck"
    And I press "Next"
    Then I see text "My Vehicle"
    Then I press "Vehicle type"    
    And I see the "vehicle type selection" screen
    When I scroll up to item with text "Bus"
    And I press "Bus"
    Then I see text "My Vehicle"
    And I see text "Bus" 

  @TestIdUserStory("FNK-51")
  Scenario: At the end of the First Run Wizard the vehicle profile should be stored in the activity
    Given I start First Run Wizard
    And I enable vehicle profile support
    Then I see the "language selection" screen
	When I select "English (United Kingdom)" language
    And I press "Next"
    Then I see the "country selection" screen
    And I scroll down to item with text "United Kingdom"
    When I press "United Kingdom"
    And I press "Next"
    Then I see the "vehicle type selection" screen
    And I scroll down to item with text "Truck"
    And I press "Truck"
    And I press "Next"
    Then I see text "My Vehicle"
    Given these vehicle profile values
    	| vehicle length | 1565 |
    	| vehicle width | 253 |
    	| vehicle height | 310 |
    	| vehicle gross weight | 3500 |
    	| vehicle axle weight | 899 |
    	| vehicle max speed | 79 |
    And I enter the above values into the input fields
    And I hide the keyboard
    And I press "Next"
    Then I see text "Terms and Conditions"
    When I press "I Agree"
    Then I see text "Your Information & Privacy"
    When I press "No"
    Then I see text "Don't Send Information"
    When I press "Confirm"
    Then tested activity is closed
    And there is a vehicle profile stored with the following settings:
    	| type | TRUCK |
    	| length | 15650 |
    	| width | 2530 |
    	| height | 3100 |
    	| weight | 35000000 |
    	| axleweight | 8990000 |
    	| maxspeed | 35316 |
