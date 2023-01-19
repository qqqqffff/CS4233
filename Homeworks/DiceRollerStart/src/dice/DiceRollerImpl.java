/*
 * Implementation of a DiceRoller.
 *
 * Copyright © 2023, Gary F. Pollice
 */
package dice;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DiceRollerImpl implements DiceRoller {
    static class Die{
        private int value = -1;
        private int numberOfSides;
        public Die(int numberOfSides){
            this.numberOfSides = numberOfSides;
        }
        public void roll(){
            this.value = Math.abs(new SecureRandom().nextInt()) % this.numberOfSides + 1;
        }
        public int getValue(){
            return this.value;
        }
        public void setNumberOfSides(int numberOfSides){
            this.numberOfSides = numberOfSides;
        }
        public int getNumberOfSides(){
            return this.numberOfSides;
        }
    }
    private final int numberOfSides;
    private final int numberOfDice;
    private final List<Die> dice;
    /**
     * Only/default constructor
     * @param numberOfSides number of sides
     * @param numberOfDice number of dice
     * @throws DiceException if the number of sides specified is less than or equal to 1
     * @throws DiceException if the number of dice specified is less than or equal to 0
     */
    public DiceRollerImpl(int numberOfSides, int numberOfDice) throws DiceException {
        if(numberOfSides <= 1) throw new DiceException("Invalid Number of Sides");
        if(numberOfDice <= 0) throw new DiceException("Invalid Number of Dice");

        this.numberOfSides = numberOfSides;
        this.numberOfDice = numberOfDice;
        this.dice = new ArrayList<>();

        for(int i = 1; i <= numberOfDice; i++){
            this.dice.add(new Die(numberOfSides));
        }
    }

    @Override
    public int roll() {
        AtomicInteger total = new AtomicInteger(0);

        this.dice.forEach(die -> {
            die.roll();
            total.set(total.get() + die.getValue());
        });

        return total.get();
    }

    @Override
    public int getDiceTotal() {
        AtomicInteger total = new AtomicInteger(0);

        this.dice.forEach(die -> total.set(total.get() + die.getValue()));

        return total.get() < 0 ? -1 : total.get();
    }

    @Override
    public int getDiceCount() {
        return this.numberOfDice;
    }

    @Override
    public int getDieValue(int dieNumber) throws DiceException {
        if(dieNumber > this.dice.size() || dieNumber < 1) throw new DiceException("Specified Dice Value Does not Exist");
        if(this.dice.get(dieNumber - 1).getValue() == -1) throw new DiceException("Dice was Never Rolled");

        return this.dice.get(dieNumber - 1).getValue();
    }
}
