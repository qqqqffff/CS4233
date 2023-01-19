package dice;


public class DiceTester {
    public static void main(String[] args) throws DiceException {
        mainTest1();
    }
    public static void mainTest1() throws DiceException {
        DiceRollerImpl diceRoller = new DiceRollerImpl(6, 20);

        int rollValue = diceRoller.roll();

        assert rollValue >= 20 && rollValue <= 120;
    }
}
