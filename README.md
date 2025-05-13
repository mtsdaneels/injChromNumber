# The injective chromatic number

This respository contains an algorithm to calculate the injective chromatic number of a given graph. It is used for research purposes for KU Leuven.

## Installation

This requires a working shell and `make`. On Windows an easy way to simulate this is by using Windows Subsystem for Linux (WSL).
Navigate to the root directory and compile using `make` to create `injChromNumber.jar`, which can then be run.

# Usage
The input of the program is the graph6 notation (http://users.cecs.anu.edu.au/~bdm/data/formats.txt) of 1 or more graphs. The .jar file: "injChromNumber.jar" can be used to get an overview in the form of a table for a large amount of graphs.
An example, making use of plantri (https://users.cecs.anu.edu.au/~bdm/plantri/) to generate the input graphs:

```
./plantri -g 12 | java -jar injChromNumber.jar
./plantri -g 12
7595 triangulations written to stdout; cpu=0.02 sec
Total time : 0.14 sec
Amount of graphs: 7595
max degree:     0       1       2       3       4       5       6       7       8       9       10      11
inj = 1:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 2:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 3:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 4:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 5:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 6:        0       0       0       0       0       1       0       0       0       0       0       0
inj = 7:        0       0       0       0       0       0       1       0       0       0       0       0
inj = 8:        0       0       0       0       0       0       11      110     0       0       0       0
inj = 9:        0       0       0       0       0       0       39      556     819     0       0       0
inj = 10:       0       0       0       0       0       0       24      488     1497    1227    0       0
inj = 11:       0       0       0       0       0       0       1       70      465     900     772     0
inj = 12:       0       0       0       0       0       0       0       2       35      130     219     228
```

There is a possibility to apply filters for the received graphs. This can be achieved by adding "-f D-I" with D the maximum degree of the graphs and I the injective chromatic number of the graphs. The filter -f 6-10 will print out all the graphs with maximum degree 6 and injective chromatic number greater than or equal to 10. When you only want the graphs with maximum degree 6 and injective chromatic number exactly 10, you can use -f 6-10=. Multiple filters can be added at the same time. An example to find the graphs with maximum degree 6 and injective chromatic number > 10 AND the graphs with maximum degree 7 and injective chromatic number = 11:

```
./plantri -g 12 | java -jar injChromNumber.jar -f 6-11 6-7=
./plantri -g 12
7595 triangulations written to stdout; cpu=0.02 sec
--------------------
Filter: max degree = 6, inj = 7
K|nmIDF?wA_N
--------------------
Filter: max degree = 6, inj = 11
K|fIYDpCws_M
--------------------
Total time : 0.13 sec
Amount of graphs: 7595
max degree:     0       1       2       3       4       5       6       7       8       9       10      11
inj = 1:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 2:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 3:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 4:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 5:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 6:        0       0       0       0       0       1       0       0       0       0       0       0
inj = 7:        0       0       0       0       0       0       1       0       0       0       0       0
inj = 8:        0       0       0       0       0       0       11      110     0       0       0       0
inj = 9:        0       0       0       0       0       0       39      556     819     0       0       0
inj = 10:       0       0       0       0       0       0       24      488     1497    1227    0       0
inj = 11:       0       0       0       0       0       0       1       70      465     900     772     0
inj = 12:       0       0       0       0       0       0       0       2       35      130     219     228
```

To get the injective coloring of the graphs while using the least amount of colors for that graph, you can use -c after all the filters. This prints an array with the colors used for the graphs that are printed. For example: 

 ```
./plantri -g 12 | java -jar injChromNumber.jar -f 6-11 7-12 -c
./plantri -g 12
7595 triangulations written to stdout; cpu=0.01 sec
--------------------
Filter: max degree = 7, inj = 12
K|fIIDP[G`B|    [1, 8, 2, 9, 10, 3, 4, 5, 6, 7, 11, 12]
K|tIID`GX_bz    [1, 8, 2, 9, 3, 4, 5, 6, 7, 10, 11, 12]
--------------------
Filter: max degree = 6, inj = 11
K|fIYDpCws_M    [1, 7, 2, 8, 9, 3, 4, 5, 6, 10, 11, 1]
--------------------
Total time : 0.1 sec
Amount of graphs: 7595
max degree:     0       1       2       3       4       5       6       7       8       9       10      11
inj = 1:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 2:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 3:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 4:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 5:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 6:        0       0       0       0       0       1       0       0       0       0       0       0
inj = 7:        0       0       0       0       0       0       1       0       0       0       0       0
inj = 8:        0       0       0       0       0       0       11      110     0       0       0       0
inj = 9:        0       0       0       0       0       0       39      556     819     0       0       0
inj = 10:       0       0       0       0       0       0       24      488     1497    1227    0       0
inj = 11:       0       0       0       0       0       0       1       70      465     900     772     0
inj = 12:       0       0       0       0       0       0       0       2       35      130     219     228
```


The output is made such that only the graph6 notation of the graphs is output to stdout, meaning that it is possible to use pipes to another program. For example, if we want to know the chromatic number of the graphs from above, we can use countg (https://pallini.di.uniroma1.it/).

```
./plantri -g 12 | java -jar injChromNumber.jar -f 6-10 | ./countg --N
./plantri -g 12
>A ./countg --N
7595 triangulations written to stdout; cpu=0.01 sec
--------------------
Filter: max degree = 6, inj = 10
--------------------
Filter: max degree = 6, inj = 11
--------------------
Total time : 0.11 sec
Amount of graphs: 7595
max degree:     0       1       2       3       4       5       6       7       8       9       10      11
inj = 1:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 2:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 3:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 4:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 5:        0       0       0       0       0       0       0       0       0       0       0       0
inj = 6:        0       0       0       0       0       1       0       0       0       0       0       0
inj = 7:        0       0       0       0       0       0       1       0       0       0       0       0
inj = 8:        0       0       0       0       0       0       11      110     0       0       0       0
inj = 9:        0       0       0       0       0       0       39      556     819     0       0       0
inj = 10:       0       0       0       0       0       0       24      488     1497    1227    0       0
inj = 11:       0       0       0       0       0       0       1       70      465     900     772     0
inj = 12:       0       0       0       0       0       0       0       2       35      130     219     228
         25 graphs : chrom=4
 25 graphs altogether; cpu=0.00 sec
```
