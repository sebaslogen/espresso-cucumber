Feature: Vehicle profile screen
  Test the vehicle profile screen with all inputs
 
  @TestIdUserStory("FNK-51")
  Scenario: Enter some values in the vehicle profile and check if the are persisted when leaving the screen
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
    	| vehicle length | 1100 |
    	| vehicle width | 220 |
    	| vehicle height | 330 |
    	| vehicle gross weight | 4400 |
    	| vehicle axle weight | 500 |
    	| vehicle max speed | 66 |
    And I enter the above values into the input fields
    And I hide the keyboard
	And I press "Next"
	And I press the back button
    Given these vehicle profile values
    	| vehicle length | 11.00 |
    	| vehicle width | 2.20 |
    	| vehicle height | 3.30 |
    	| vehicle gross weight | 44.00 |
    	| vehicle axle weight | 5.00 |
    	| vehicle max speed | 66 |
	Then I expect all input fields to have the above values    	
	
  @TestIdUserStory("FNK-51")
  Scenario: Enter some values in the vehicle profile and check the warning state is triggered
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
    	| vehicle length | 9900 |
    	| vehicle width | 9900 |
    	| vehicle height | 9900 |
    	| vehicle gross weight | 9900 |
    	| vehicle axle weight | 9900 |
    	| vehicle max speed | 999 |
    When I enter above values into the input fields they should be in warning state
    
  @TestIdUserStory("FNK-51")
  Scenario: Enter some values in the vehicle profile and check the maximum is entered
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
	Given I enter text "9900" into input field "vehicle length"
	Then I check that the input field "vehicle length" is in warning state
	Given I enter text "2" into input field "vehicle width"
	Then I check input field "vehicle length" contains the text "18.75"

  @TestIdUserStory("FNK-51")
  Scenario: Check the default values for truck in the UK
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
    	| vehicle length | 18.00 |
    	| vehicle width | 2.55 |
    	| vehicle height | 4.00 |
    	| vehicle gross weight | 40.00 |
    	| vehicle axle weight | 10.00 |
    	| vehicle max speed | 60 |
    Then I expect all input fields to have the above values
    
  @TestIdUserStory("FNK-51")    
  Scenario: Check the default values for truck in the Netherlands (EU)
    Given I start First Run Wizard
    And I enable vehicle profile support
    Then I see the "language selection" screen
    When I select "Nederlands" language
    And I press "Volgende"
    Then I see the "country selection" screen
    And I scroll down to item with text "Nederland"
    When I press "Nederland"
    And I press "Volgende"
    Then I see the "vehicle type selection" screen
    And I scroll down to item with text "Vrachtwagen"
    And I press "Vrachtwagen"
    And I press "Volgende"
    Then I see text "Mijn voertuig"
    Given these vehicle profile values
    	| vehicle length | 18,75 |
    	| vehicle width | 2,55 |
    	| vehicle height | 4,00 |
    	| vehicle gross weight | 30,00 |
    	| vehicle axle weight | 9,00 |
    	| vehicle max speed | 80 |
    Then I expect all input fields to have the above values    
    
  @TestIdUserStory("FNK-130")
  Scenario: Check that entered values are retained when going back to vehicle selection and not actually selecting a new type
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
    	| vehicle length | 12.34 |
    	| vehicle width | 1.42 |
    When I enter the above values into the input fields
    And I press the back button
    Then I see the "vehicle type selection" screen
    When I scroll down to item with text "Taxi"
    And I press "Taxi"
    And I scroll down to item with text "Truck"
    And I press "Truck"
    And I press "Next"
    Then I see text "My Vehicle"
    Given these vehicle profile values
    	| vehicle length | 12.34 |
    	| vehicle width | 1.42 |
    	| vehicle height | 4.00 |
    	| vehicle gross weight | 40.00 |
    	| vehicle axle weight | 10.00 |
    	| vehicle max speed | 60 |
    Then I expect all input fields to have the above values
    