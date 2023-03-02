Name: Apollinaris Rowe

Test List using file EscapeGameTester

**Build Tests**

Notes:
- Hex coordinates are fully functional
- Jump attribute does not work exactly as described view MovementManager.java for more details
- My tests achieve 80% coverage without EscapeJsonConverter and the interfaces, I believe the coverage will be much higher
- For some tests I will simply just reference the corresponding diagrams that I have drawn
- Some tests will test the underlying game logic, like the capture and exit tests

| **#** | Test                                                               | Comments                             |
|:-----:|:-------------------------------------------------------------------|:-------------------------------------|
|   1   | Making Square Coordinates that are in and out of bounds            | coordinate testing                   |
|   2   | Testing Escape Game Initializer                                    | testing initialization               |
|   3   | Making Hex Coordinates that are in and out of bounds               | coordinate testing                   |
|   4   | Testing Omni Movement in Hex Coordinates with blocks               | not functional                       |
|   5   | Hex Move testing for general movement                              | not functional                       |
|   6   | Square coordinate exit move logic and tests                        | view square_exit_test.png            |
|   7   | Square coordinate infinite coordinate testing with large distances | limit testing path finding algorithm |
|   8   | Diagonal Movement Algorithm Testing                                | view square_diagonal_move_test.png   |
|   9   | Orthogonal Movement Algorithm Testing                              | view square_orthogonal_move_test.png |
|  10   | Capture Logic Testing                                              | view square_capture_test.png         |
|  11   | Linear Movement Algorithm Testing                                  | view square_linear_move_test.png     |
|  12   | Omni Movement Algorithm Testing                                    | view square_omni_move_test.png       |
|  13   | Hex Initialization Test                                            | testing initialization               |
