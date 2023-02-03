package ocr;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OCRTest {

    /**
     * Author: Apollinaris Rowe
     * All test numbers can be referred to their description of functionality in TODO.md
     */

    OCRTranslator translator = new OCRTranslator();

    @Test
    void test0() {
        String top = "b_b";
        String mid = "|b|";
        String bot = "|_|";
        assertEquals("0", translator.translate(top, mid, bot));
    }

    @Test
    void test1(){
        String top = "bb|";
        String mid = "bb|";
        String bot = "bb|";
        assertEquals("1", translator.translate(top, mid, bot));
    }

    @Test
    void test2(){
        String top = "b_b";
        String mid = "b_|";
        String bot = "|_b";
        assertEquals("2", translator.translate(top, mid, bot));
    }

    @Test
    void test3(){
        String top = "b_b";
        String mid = "b_|";
        String bot = "b_|";
        assertEquals("3", translator.translate(top, mid, bot));
    }

    @Test
    void test4(){
        String top = "bbb";
        String mid = "|_|";
        String bot = "bb|";
        assertEquals("4", translator.translate(top, mid, bot));
    }

    @Test
    void test5(){
        String top = "b_b";
        String mid = "|_b";
        String bot = "b_|";
        assertEquals("5", translator.translate(top, mid, bot));
    }

    @Test
    void test6(){
        String top = "b_b";
        String mid = "|_b";
        String bot = "|_|";
        assertEquals("6", translator.translate(top, mid, bot));
    }

    @Test
    void test7(){
        String top = "b_b";
        String mid = "bb|";
        String bot = "bb|";
        assertEquals("7", translator.translate(top, mid, bot));
    }

    @Test
    void test8(){
        String top = "b_b";
        String mid = "|_|";
        String bot = "|_|";
        assertEquals("8", translator.translate(top, mid, bot));
    }

    @Test
    void test9(){
        String top = "b_b";
        String mid = "|_|";
        String bot = "bb|";
        assertEquals("9", translator.translate(top, mid, bot));
    }

    @Test
    void test10(){
        int amtNumbers = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        StringBuilder value = new StringBuilder();

        for(int i = 0; i < amtNumbers; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));
            value.append(numb);
            String space = i + 1 == amtNumbers ? "" : "b";
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        assertEquals(value.toString(), translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test11(){
        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        for(int i = 0; i < 2; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));

            top.append(translator.acceptedTops.get(numb)).append("bb");
            mid.append(translator.acceptedMiddles.get(numb)).append("b");
            bot.append(translator.acceptedBottoms.get(numb)).append("b");
        }

        assertThrows(OCRException.class, () -> translator.translate(top.toString(), mid.toString(), bot.toString()),
                "Lines 1 and 2 are not the same length");
    }

    @Test
    void test12(){
        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        top.append("bb|");
        mid.append("bb");
        bot.append("bb|");

        assertThrows(OCRException.class, () -> translator.translate(top.toString(), mid.toString(), bot.toString()),
                "Lines 1 and 2 are not the same length");

        mid.append("|");

        assertEquals("1", translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test13(){
        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        top.append("bb|");
        mid.append("bb|");
        bot.append("bb");

        assertThrows(OCRException.class, () -> translator.translate(top.toString(), mid.toString(), bot.toString()),
                "Lines 1 and 3 are not the same length");

        bot.append("|");

        assertEquals("1", translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test14(){
        int amtNumbers = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        StringBuilder value = new StringBuilder();

        for(int i = 0; i < amtNumbers; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));
            value.append(numb);
            String space = i + 1 == amtNumbers ? "" : "b";
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        char temp = mid.charAt(mid.length() - 1);
        mid.deleteCharAt(mid.length() - 1);

        assertThrows(OCRException.class, () -> translator.translate(top.toString(), mid.toString(), bot.toString()),
                "Lines 1 and 2 are not the same length");

        mid.append(temp);

        assertEquals(value.toString(), translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test15(){
        int amtNumbers = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        StringBuilder value = new StringBuilder();

        for(int i = 0; i < amtNumbers; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));
            value.append(numb);
            String space = i + 1 == amtNumbers ? "" : "b";
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        char temp = bot.charAt(bot.length() - 1);
        bot.deleteCharAt(bot.length() - 1);

        assertThrows(OCRException.class, () -> translator.translate(top.toString(), mid.toString(), bot.toString()),
                "Lines 1 and 3 are not the same length");

        bot.append(temp);

        assertEquals(value.toString(), translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test16(){
        int amtNumbers = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        StringBuilder value = new StringBuilder();

        char[] spaces = new char[Math.abs(new Random().nextInt() % 9 + 2)];
        Arrays.fill(spaces, 'b');

        for(int i = 0; i < amtNumbers; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));
            value.append(numb);


            String space = i + 1 == amtNumbers ? "" : String.valueOf(spaces);
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        assertEquals(value.toString(), translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test17(){
        int amtNumbers = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        StringBuilder value = new StringBuilder();

        char[] spaces = new char[Math.abs(new Random().nextInt() % 9 + 2)];
        Arrays.fill(spaces, 'b');
        top.append(String.valueOf(spaces));
        mid.append(String.valueOf(spaces));
        bot.append(String.valueOf(spaces));


        for(int i = 0; i < amtNumbers; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));
            value.append(numb);

            String space = i + 1 == amtNumbers ? "" : "b";
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        assertEquals(value.toString(), translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test18(){
        int amtNumbers = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        StringBuilder value = new StringBuilder();

        for(int i = 0; i < amtNumbers; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));
            value.append(numb);

            char[] spaces = new char[Math.abs(new Random().nextInt() % 9 + 2)];
            Arrays.fill(spaces, 'b');
            String space = i + 1 == amtNumbers ? "" : String.valueOf(spaces);
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        assertEquals(value.toString(), translator.translate(top.toString(), mid.toString(), bot.toString()));
    }

    @Test
    void test19(){
        String top = "___";
        String mid = "___";
        String bot = "___";

        assertThrows(OCRException.class, () -> translator.translate(top, mid, bot),
                "Invalid OCR String");
    }

    @Test
    void test20(){
        String errorTop = "___";
        String errorMid = "___";
        String errorBot = "___";

        int amtNumbersBefore = Math.abs((new Random().nextInt() % 4)) + 2;

        StringBuilder top = new StringBuilder();
        StringBuilder mid = new StringBuilder();
        StringBuilder bot = new StringBuilder();

        for(int i = 0; i < amtNumbersBefore; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));

            String space = "b";
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        top.append(errorTop);
        mid.append(errorMid);
        bot.append(errorBot);

        int amtNumbersAfter = Math.abs((new Random().nextInt() % 4)) + 2;
        for(int i = 0; i < amtNumbersAfter; i++) {
            String numb = String.valueOf(Math.abs(new Random().nextInt() % 10));

            String space = i + 1 == amtNumbersAfter ? "" : "b";
            top.append(translator.acceptedTops.get(numb)).append(space);
            mid.append(translator.acceptedMiddles.get(numb)).append(space);
            bot.append(translator.acceptedBottoms.get(numb)).append(space);
        }

        assertThrows(OCRException.class, () -> translator.translate(top.toString(), mid.toString(), bot.toString()),
                "Invalid OCR String");
    }

    @Test
    void test21(){
        String top = null;
        String mid = "|_|";
        String bot = "|_|";

        assertThrows(OCRException.class, () -> translator.translate(top, mid, bot),
                "Line 1 is null");
    }

    @Test
    void test22(){
        String top = "b_b";
        String mid = null;
        String bot = "|_|";

        assertThrows(OCRException.class, () -> translator.translate(top, mid, bot),
                "Line 2 is null");
    }

    @Test
    void test23(){
        String top = "b_b";
        String mid = "|_|";
        String bot = null;

        assertThrows(OCRException.class, () -> translator.translate(top, mid, bot),
                "Line 3 is null");
    }

    @Test
    void test24(){
        String top = "zzz";
        String mid = "zzz";
        String bot = "zzz";

        assertThrows(OCRException.class, () -> translator.translate(top, mid, bot),
                "Invalid OCR String");
    }

    @Test
    void test25(){
        String top = "bbb";
        String mid = "bbb";
        String bot = "bbb";

        assertThrows(OCRException.class, () -> translator.translate(top, mid, bot),
                "Invalid OCR String");
    }
}
