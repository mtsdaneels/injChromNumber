# The injective chromatic number

This respository contains an algorithm to calculate the injective chromatic number of a given graph. It is used for research purposes for KU Leuven.

## Installation

This requires a working shell. On Windows an easy way to simulate this is by using Windows Subsystem for Linux (WSL).

The repository contains 2 .jar files ready to use.

# Usage
The input of the program is the graph6 notation (http://users.cecs.anu.edu.au/~bdm/data/formats.txt) of 1 or more graphs. The first .jar file: "injChromNumber_table.jar" can be used to get an overview in the form of a table for a large amount of graphs.
An example, making use of plantri (https://users.cecs.anu.edu.au/~bdm/plantri/) to generate the input graphs:

```
./plantri -g 12 | java -jar injChromNumber_table.jar
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

To check which graphs are in a specific entry of the table, u can use -d and -i. The graphs are printed in graph6 notation. An example to find the 11 graphs in the above table with maximum degree 6 and injective chromatic number 8:

```
./plantri -g 12 | java -jar injChromNumber_table.jar -d 6 -i 8
./plantri -g 12
7595 triangulations written to stdout; cpu=0.01 sec
K|eMIDpCWw_z
K|tJgsL@GN_F
K|tJgsL@GN_L
K~fIIsL@gC_^
K~fIgsHBgF_F
K|thgc\@gE_N
K|mmIDd@wE_M
K|mmIDd@wA_N
K|f]Ih`GHb_L
K|tIJCpEWW_z
K|tJHCpCW{_Z
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

To get the injective coloring of the graphs while using the least amount of colors for that graph, you can use -c after -d and -i. This prints an array with the colors used for the graphs that are printed. For example: 

 ```
./plantri -g 12 | java -jar injChromNumber_table.jar -d 6 -i 8 -c
./plantri -g 12
7595 triangulations written to stdout; cpu=0.02 sec
K|eMIDpCWw_z
[7, 1, 2, 3, 4, 5, 6, 4, 5, 8, 1, 2]
K|tJgsL@GN_F
[1, 2, 6, 3, 7, 4, 8, 5, 6, 3, 1, 2]
K|tJgsL@GN_L
[1, 2, 6, 3, 7, 4, 8, 5, 6, 3, 1, 2]
K~fIIsL@gC_^
[1, 7, 2, 3, 8, 4, 5, 6, 2, 3, 8, 1]
K~fIgsHBgF_F
[1, 2, 3, 7, 4, 8, 5, 6, 2, 3, 1, 7]
K|thgc\@gE_N
[1, 5, 2, 7, 3, 8, 4, 5, 6, 1, 3, 2]
K|mmIDd@wE_M
[7, 1, 2, 3, 4, 5, 6, 4, 8, 3, 2, 7]
K|mmIDd@wA_N
[7, 1, 2, 3, 4, 5, 6, 4, 8, 3, 2, 7]
K|f]Ih`GHb_L
[7, 1, 2, 3, 4, 5, 6, 8, 4, 5, 6, 7]
K|tIJCpEWW_z
[1, 7, 2, 5, 3, 4, 5, 6, 4, 8, 2, 1]
K|tJHCpCW{_Z
[1, 2, 7, 3, 5, 6, 4, 5, 6, 2, 8, 1]
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

The second file: 'injChromNumber_withoutTable.jar" can be used to print out graphs without getting the table. This can be usefull for pipeling. For example, if we want to know the chromatic number of the 11 graphs from above, we can use countg (https://pallini.di.uniroma1.it/).

```
./plantri -g 12 | java -jar injChromNumber_withoutTable.jar -d 6 -i 8 | ./countg --N
./plantri -g 12
>A ./countg --N
7595 triangulations written to stdout; cpu=0.02 sec
         11 graphs : chrom=4
 11 graphs altogether; cpu=0.00 sec
```
