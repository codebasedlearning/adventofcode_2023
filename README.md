# adventofcode_2023

All AoC 2023 solutions.

Characterise problems and solutions in terms of programming language features 
and algorithmic requirements, with a view to learning a new language.

All solutions involve reading data from a text file 'input.txt' and string manipulation.
More typical ingredients are (data) classes to model small pieces of data with 
some properties, and typical collections such as lists/arrays, sets and maps.
Tasks with 2D grid data use 2D arrays and, if modelled with classes, may also use 
indexes and operators.

## 2023

### Day 01 Trebuchet?!
- data 'eightwothree'
- extract numbers, string manipulation, maybe regular expressions
- easy

### Day 02 Cube Conundrum
- data 'Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green'
- classes, maps
- easy

### Day 03 Gear Ratios
- grid data '467..114..'
- (2d)arrays, classes, index-manipulations
- easy

### Day 04 Scratchcards
- data 'Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53'
- classes, sets, maps
- easy

### Day 05 If You Give A Seed A Fertilizer
- data 'seeds: 79 14 55 13 // seed-to-soil map: // 50 98 2 // 52 50 48'
- classes, maps, classes, enums, ranges
- easy

### Day 06 Wait For It
- data 'Time: 7 15 30 // Distance: 9 40 200'
- some math (roots)
- easy

### Day 07 Camel Cards
- data '32T3K 765' (cards)
- classes, maps
- easy

### Day 08 Haunted Wasteland
- data 'AAA = (BBB, BBB)'
- classes, maps, math (gcd,scm), long numbers
- medium (idea)

### Day 09 Mirage Maintenance
- data '0 3 6 9 12 15'
- maps
- easy

### Day 10 Pipe Maze
- grid data '-L|F7'
- (2d)arrays, index-manipulations
- medium (time-consuming, graph properties)

### Day 11 Cosmic Expansion
- grid data '...#......'
- (2d)arrays, index-manipulations
- easy

### Day 12 Hot Springs
- data '???.### 1,1,3'
- maps, algo (dynamic programming)
- medium/hard (idea)

### Day 13 Point of Incidence
- grid data '#.##..##.'
- maps, (2d)arrays, index-manipulations
- easy

### Day 14 Parabolic Reflector Dish
- grid data 'O....#....'
- maps, (2d)arrays, index-manipulations
- medium (idea)

### Day 15 Lens Library
- data 'rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7'
- maps, hashing
- easy

### Day 16 The Floor Will Be Lava
- grid data '.|...\....'
- sets, (2d)arrays, index-manipulations
- easy/medium (time-consuming)

### Day 17 Clumsy Crucible
- grid data '2413432311323'
- maps, queues, (2d)arrays, index-manipulations, math (graph)
- medium/hard (graph)

### Day 18 Lavaduct Lagoon
- data 'R 6 (#70c710)'
- maps, classes, math (graph)
- medium/hard (graph)

### Day 19 Aplenty
- data 'px{a<2006:qkq,m>2090:A,rfg} // {x=787,m=2655,a=1222,s=2876}'
- classes, maps, queues, ranges
- easy/medium (complexity)

### Day 20 Pulse Propagation
- data 'broadcaster -> a, b, c // %a -> b'
- maps, queues, classes, inheritance, long numbers, math (gcd,scm)
- hard (complexity)

### Day 21 Step Counter
- grid data '.....###.#.'
- sets, queues, (2d)arrays, index-manipulations, math (graph)
- easy/medium (part 1, graph), very hard (math, quadratic forms, indefinite grid)

### Day 22 Sand Slabs
- data '1,0,1~1,2,1'
- maps, (3d)arrays, index-manipulations
- easy

### Day 23 A Long Walk
- grid data '#.#####################'
- (2d)arrays, index-manipulations, math (graph)
- medium

### Day 24 Never Tell Me The Odds
- data '19, 13, 30 @ -2,  1, -2'
- math (2d, 3d)
- medium (2d), hard (3d, non-linear equations)

### Day 25 Snowverload
- data 'jqt: rhn xhk nvd'
- math (graph)
- medium/had (graph)
