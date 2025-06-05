@CarManager
@ReadCar
Feature: Reading all cars and car by brand id

  Scenario Outline: Successfully when read cars by valid brandId
    Given  I successfully create a new car with brand id "<brandId>" and 5 time played with 10 hours
    When I successfully read car list with brand id equals to "<brandId>"
    Then the read status code must be 200
    And the response list must contain at least 1 elements
    And the response list must contain only cars for brand id "<brandId>"

    Examples:
      | brandId |
      | sega        |
      | nintendo    |

  Scenario: Successfully when read cars by invalid brandId
    When I successfully read car list with brand id equals to "invalid"
    Then the read status code must be 200
    And the response list must contain at least 0 elements