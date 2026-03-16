@keypresses
Feature: Key Presses
  As a user
  I want to press keys on the keyboard
  So that I can verify what key was detected by the application

  Background:
    Given the user is on the Key Presses page

  Scenario: Press the ENTER key
    When the user presses the "ENTER" key
    Then the result should show "You entered: ENTER"

  Scenario: Press the TAB key
    When the user presses the "TAB" key
    Then the result should show "You entered: TAB"

  Scenario: Press the ESCAPE key
    When the user presses the "ESCAPE" key
    Then the result should show "You entered: ESCAPE"

  Scenario: Press the letter A
    When the user presses the "A" key
    Then the result should show "You entered: A"

  Scenario: Press the SPACE key
    When the user presses the "SPACE" key
    Then the result should show "You entered: SPACE"

  Scenario Outline: Press multiple keys and verify result
    When the user presses the "<key>" key
    Then the result should show "You entered: <expected>"

    Examples:
      | key    | expected |
      | UP     | UP       |
      | DOWN   | DOWN     |
      | LEFT   | LEFT     |
      | RIGHT  | RIGHT    |
