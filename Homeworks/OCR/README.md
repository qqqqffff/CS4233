# Introduction

You will implement a program to read OCR-style numerals and output a
string with the corresponding digits.

You will implement the code using Test'Driven Development. You
will keep a TODO list with the tests you write, numbering each. The starting code is provided as an IntelliJ
project. If you are using another IDE you can copy the **src** directory and other files into an appropriate place.

## Description

You need to create a way to turn three lines of characters into a string
with digits that correspond to the digits the three lines represent.
There are just three characters used in the input lines: '|', '_', and the blank
character. The rendering of the ten digits, 0-9, are shown on the following lines.

      _     _  _       _   _  _   _   _
     | | |  _| _| |_| |_  |_   | |_| |_|
     |_| | |_  _|   |  _| |_|  | |_|   |

You will translate the three lines that represent the three lines of
characters in the OCR characters to a string with the translated
characters in the correct order. For example, if you have the following
three lines as input:

<pre>
     _ 
|_|  _|
  | |_ 
</pre>

you would output "42". There are no spaces between the output digits,
regardless of how many spaces might be between digits in the OCR input. So if you had an input string of 

<pre>
     _ 
|_|  _|
  | |_ 
</pre>

You would receive three strings (from top to bottom, where 'b' means a blank):

* top: "bbbbb_b"  (5 spaces followed by _ and a space)
* middle: "|\_|bb\_|"
* bottom: "bb\|b\|_b"


If the input is invalid; for example if the strings are not the same
length, an input is a null, or the strings do not translate to OCR
digits, you would thrown an OCRException runtime exception. 

**NOTE:** that an empty string is valid; you might receive three strings that are empty
(not null) or have just spaces in them. Also, more than one column of
spaces between the OCR digits is valid, but there must be at least one
column of spaces between digits. All input strings must, however, be the same length.
If there ae n spaces between two digits in one string, there must be n spaces between 
the same two digits in all strings.


You will use Test-Driven Development to develop your solution. You will
follow the cycle of Red-Green-Refactor based upon a TODO list that you
will keep. There is a TODO.md in your starting code. You will add your
tasks there. Please number each task in the order that you implement
them. **In markdown, just make a list where the line is started with a number.**

1. task 1
2. task 2
5. task 3 (you don't have to type in the proper sequence.)
Remember the tasks are something you should be able to do in a few
minutes, certainly no more than an hour.

You should have tests for valid and invalid inputs. You should also have
code coverage in your src directory of at least 92%. This does not include enumerations or interfaces.

## Starting code

There is a class, OCRTranslator that has a single method, `translate()` that takes three
Strings as input representing the three lines of an OCR character
string. This method returns the translated corresponding to the OCR
characters, or throws an OCRException for the cases previously specified.

You may create additonal classes or other private methods in
OCRTranslator to help you solve this problem. You **may not** modify the
signature of `translate()`. There are some additinal instructions in the 




My estimate of the time it will take you to complete this assignment is
3-6 hours. You will probably spend more time writing tests than you do
the implementation code.

*Make sure that you allow enough time to get the assignment completed,
cleaned up, checked, and submitted. Remember that if you are late
submitting the work, you will receive a zero for this assignment.*