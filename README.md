# Requirements
 - JDK 23+
 - maven 3.9+

# How to run
```
$ mvn clean package
$ java -jar aoc-2024/target/aoc-2024-1.0-SNAPSHOT.jar /path/to/your/inputs/folder
```

# Input folder structure

AOC runners expect the puzzle input to be in a directory with a specific layout. Unless overriden, the input for day X of year 20YZ should be in the following location:
```
/path/to/your/inputs/folder/Aoc20YZ/DayX.txt
```

[example-inputs](https://github.com/gagarski/aoc-all/tree/master/example-inputs) in this repo is a good example of inputs folder and contains example inputs (currently only for year 2024).
