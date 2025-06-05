@CarManager
@CreateCar
Feature: Creating a car using a registered brandId

  Scenario Outline: Successfully when creating a new car with a registered brandId
    When I successfully create a new car with brand id "<brandId>" and <nRepair> time played with 10 hours
    Then the create status code must be 201
    And the id must be not null in the response
    And the brandId must be equals to "<brandId>"
    And there are <nRepair> in the response

    Examples:
      | brandId | nRepair |
      | sega        | 10          |
      | nintendo    | 20          |


  Scenario Outline: Unsuccessfully when creating a new car with a unregistered brandId
    When I unsuccessfully create a new car with brand id "<brandId>" and <nRepair> time played with <hours> hours
    Then the create status code must be <statusCode>
    And error message must contain "<message>"

    Examples:
      | brandId | nRepair | hours | statusCode | message             |
      | inv         | 17          | 10    | 404        | Brand not found |
      | nintendo    | 17          | 30    | 400        | Validation error    |
      | nintendo    | 0           | 10    | 400        | Validation error    |