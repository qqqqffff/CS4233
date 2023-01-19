/*
 * Interface for the dice rolling component.
 *
 * Copyright © 2023, Gary F. Pollice
 */
package dice;

public interface DiceRoller {
    /**
     * Roll the dice.
     * @return Return the total of all of the dice after rolling them all.
     */
    int roll();

    /**
     * Get the total of all of the dice.
     * @return the total of all dice or -1 if there have been no calls to roll().
     */
    int getDiceTotal();

    /**
     * @return the number of dice
     */
    int getDiceCount();

    /**
     * Get the value of the specified die.
     *
     * @param dieNumber the die to query (1 to getDiceCount())
     * @return the value on the selected die.
     * @throws DiceException if the die number is invalid or if the die was never rolled.
     */
    int getDieValue(int dieNumber) throws DiceException;
}