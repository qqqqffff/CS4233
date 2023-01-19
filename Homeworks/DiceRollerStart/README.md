# Dice Roller Component Assignment

Whether doing simulations or building games of chance or other types of games, and in other applications, a need often arises for some way to obtain a random value within certain parameters. A common request is to "roll dice" and get the results of the roll. While many of these clients use standard six-sided cube dice with one to six dots on the cube surface, others require dice of different numbers of sides. Your crack marketing department has determined that your customers need a component that allows a client to configure a set of dice and roll them, getting random results.

They provided the following list of requirements:
* A roller can be configured with any number of dice, but there must be at least one.
* All dice used by a single roller will have the same number of sides ≥ 2. For example, a 2-sided die is the equivalent of a coin that one might flip.
* If there are `n` sides, then the number of dots on each surface go from 1 to `n`.
* When the client rolls, the dice are rolled independently and each produces an integer value from 1 to `n`. The sum of all the dice is returned. 
* The client configures the dice roller by indicating the number of dice and the number of sides. Entering invalid numbers for the number of dice and the number of sides causes the code to throw an exception with an appropriate error, indicating the problem.
* The client can query the roller for the current total of dots on the dice from the most recent roll. If the dice have never been rolled by the dice roller, return -1 to indicate the error.
* The client can query the value of any single die from the most recent roll by providing the die number. The dice are numbered from 1 to `d`, where `d` is the number of dice. If the dice have never been rolled by the dice roller or the client requests the value of a die number that is not in the range of 1..`d`, then throw an exception with an appropriate error, indicating the problem.

## Starting code
The project lead engineer has created starting code for you. There is an interface for the DiceRoller component and a skeleton implementation that must be filled in. 

## Your job
You must keep maintain the interface for the `DiceRoller`, but how you do this, and implement the solution is up to you. You may add helper classes that cooperate with the main `DiceRollerImpl` class. Certainly, you have to complete all the methods in the `DiceRollerImpl`.

The goal of this assignment is to see how well you can take a fairly simple problem and produce readable, intentional, and modifiable (and, of course correct) code.

## Submission
If you are using either Eclipse or IntelliJ, you can submit your whole project. Otherwise, you should submit a zipped archive file containing your complete `src` directory and the **DesignDecisions.md** file.