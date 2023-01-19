# Design Decisions
# Apollinaris Rowe
ID: 724859020  
Mail: airowe@wpi.edu  
  
> Helper Class **Die**:
> Two Global Variables to hold the number of possible sides, and the value. *initialized to -1 to represent not being rolled.*
> Functions roll() to simulate a die roll *random value between 1 and the number of sides*, getValue() returns the current value
> of the die, setNumberOfSides() to set the number of sides and getNumberOfSides() to get the number of sides.

> For the constructor:  
> Start with edge case checks, if the number of sides is <= 1 then invalid die, or if the number of dice is <= 0.
> On pass, initialize final instance variables: numberOfSides and numberOfDice. Initialize a list of all the **Die** Objects

> roll() Method:  
> Start with a lambda safe total value, AtomicInteger, to represent the total value to be returned *initialized to 0*.
> Then Iterate over the list using the forEach() method. For each iteration on the list I first simulate a roll, then 
> add that integer to the total to be returned at the end of the function.

> getDiceTotal() Method:  
> Start with a lambda safe total value, AtomicInteger, to represent the total value to be returned *initialized to 0*.
> Then Iterate over the list of **dice** using the foreach() method. For each iteration add the value
> to the total and return it if the total is non-negative. *All the values in the map will be -1 if
the dice have not been rolled yet* 

> getDiceCount() Method:   
> return the instance variable **numberOfDice**

> getDieValue(**dieNumber**) Method:  
> Run edge case checking, if the **dieNumber** is greater than the size of the list, or less than 0 throw an error.
> If the specified dieNumber was never rolled *returned value is -1* then throw an error.
> Return the specified **dieNumber**.  **dieNumber**  *should be between 1 and the numberOfDice inclusive*