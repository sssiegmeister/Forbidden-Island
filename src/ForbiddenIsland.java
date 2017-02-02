// Assignment 9
// partner1-Siegmeister partner1-Samuel
// partner1-sss13gm31st3r
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;

//Represents a single square of the game area
class Cell {
    // represents absolute height of this cell, in feet
    double height;
    // In logical coordinates, with the origin at the top-left corner of the screen
    int x;
    int y;
    // the four adjacent cells to this one
    Cell left;
    Cell top;
    Cell right;
    Cell bottom;
    // reports whether this cell is flooded or not
    boolean isFlooded;
    // constructor to allow for OceanCell constructor
    Cell() {
        this.isFlooded = true;
    }
    // constructor for a Cell
    Cell(int x, int y, double height, boolean isFlooded) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.isFlooded = false;
    }
    //EFFECT: if the the given cell has not been
    //        set this.left to the given Cell
    //        and set left.right to this
    void setLeft(Cell left) {
        if (this.left == null) {
            this.left = left;
            this.left.setRight(this);
        }
    }
    //EFFECT: if the the given cell has not been set
    //        set this.right to the given Cell
    //        and set right.left to this
    void setRight(Cell right) {
        if (this.right == null) {
            this.right = right;
            this.right.setLeft(this);
        }
    }
    //EFFECT: if the the given cell has not been set
    //        set this.top to the given Cell
    //        and set top.bottom to this
    void setTop(Cell top) {
        if (this.top == null) {
            this.top = top;
            this.top.setBottom(this);
        }
    }
    //EFFECT: if the the given cell has not been set
    //        set this.bottom to the given Cell
    //        and set bottom.top to this
    void setBottom(Cell bottom) {
        if (this.bottom == null) {
            this.bottom = bottom;
            this.bottom.setTop(this);
        }
    }
    //does this cell have any adjacent cells that are flooded?
    boolean nextFlooded() {
        return (this.left.isFlooded || this.right.isFlooded ||
                this.top.isFlooded || this.bottom.isFlooded);
    }
    //EFFECT: floods all adjacent cells to this cell
    void floodFill() {
        if (this.isFlooded) {
            if (this.left.height < this.height && !this.left.isFlooded) {
                this.left.isFlooded = true;
                this.left.floodFill();
            }
            if (this.right.height < this.height && !this.right.isFlooded) {
                this.right.isFlooded = true;
                this.right.floodFill();
            }
            if (this.top.height < this.height && !this.top.isFlooded) {
                this.top.isFlooded = true;
                this.top.floodFill();
            }
            if (this.bottom.height < this.height && !this.bottom.isFlooded) {
                this.bottom.isFlooded = true;
                this.bottom.floodFill();
            }
        }
    }
    //EFFECT: draw this cell on the given background
    void drawCell(WorldScene background, int level) {
        background.placeImageXY(new RectangleImage(10, 10, OutlineMode.SOLID,
                this.chooseColor(level)),
                ((this.x + 1) * 10) - 5, ((this.y + 1) * 10) - 5);

    }
    
    // returns the color of a Cell
    Color chooseColor(int level) {
        if (this.isFlooded) {
            return new Color(0, 0, (int) Math.max(0.0, (128 - ((level - this.height) * 2))));
        }
        else if (this.height < level) {
            return new Color((int) ((level - this.height) * 5), 127 - level, 0);
        }
        else {
            return new Color(Math.min(255,
                    (int) ((this.height - level) * 
                            (255 / ForbiddenIslandWorld.MAX_HEIGHT))),
                             Math.min(255, 
                    (int) ((this.height - level) * 
                            (128 / ForbiddenIslandWorld.MAX_HEIGHT) + 127)),
                             Math.min(255, 
                    (int) ((this.height - level) * 
                            (255 / ForbiddenIslandWorld.MAX_HEIGHT))));
        }
    }
    // is this Cell the same as that Cell
    boolean sameCell(Cell that) {
        return (this.x == that.x) &&
               (this.y == that.y) &&
               (this.height == that.height);
    }
}
//Represents a single square of the ocean part of the game area
class OceanCell extends Cell {
    // constructor for an OceanCell
    OceanCell(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // returns the color of an OceanCell
    Color chooseColor(int level) {
        return new Color(0, 0, 128);
    }
}
//Represents an item in the world
class Item {
    Cell cell;
    IList<Cell> land;
    int x;
    int y;
    //constructor for an item
    Item(IList<Cell> land) {
        //the cells availible for this to spawn on
        this.land = land;
        //the cell that this will spawn on
        this.cell = this.setStart(this.land);
        //the x coordinate of this
        this.x = cell.x;
        //the y coordinate of this
        this.y = cell.y;
    }
    //set the spawn point of an item
    Cell setStart(IList<Cell> loc) {
        int r = new Random().nextInt(loc.length());
        for (int i = 0; i < r; i += 1) {
            loc = loc.asCons().rest;
        }
        return loc.asCons().first;
    }
    //EFFECT: draw this item on the given background
    void drawItem(WorldScene background) {
        background.placeImageXY(new FromFileImage("mountaindew.png"),
                ((this.x + 1) * 10) - 5, ((this.y + 1) * 10) - 5);
    }
    //can this item be collected right now? 
    // (is it both at the given position
    //  and ready to be collected?)
    boolean collectable(Item that, int l) {
        return this.cell.sameCell(that.cell);
    }
}
//class to represent the player
class Player extends Item {
    boolean aquatic;
    //constructor for a player
    Player(IList<Cell> land) {
        super(land);
        this.cell = this.setStart(this.land);
        this.x = cell.x;
        this.y = cell.y;
        this.aquatic = false;
    }
    void drawItem(WorldScene background) {
        if (this.aquatic) {
            background.placeImageXY(new FromFileImage("natduck.png"),
                    ((this.x + 1) * 10) - 5, ((this.y + 1) * 10) - 5);
        }
        else {
            background.placeImageXY(new FromFileImage("nattuck.png"),
                    ((this.x + 1) * 10) - 5, ((this.y + 1) * 10) - 5);
        }
    }
}
class PowerUp extends Item {
    PowerUp(IList<Cell> land) {
        super(land);
        this.cell = this.setStart(this.land);
        this.x = cell.x;
        this.y = cell.y;
    }
    void drawItem(WorldScene background) {
        background.placeImageXY(new FromFileImage("feather.png"),
                ((this.x + 1) * 10) - 5, ((this.y + 1) * 10) - 5);
    }
}
//class to represent a helicopter
class Helicopter extends Item {
    //constructor for a helicopter
    Helicopter(IList<Cell> land) {
        super(land);
        this.cell = this.setStart(this.land);
        this.x = cell.x;
        this.y = cell.y;
    }
    Cell setStart(IList<Cell> loc) {
        Cell result = this.land.asCons().first;
        for (Cell c : this.land) {
            if (c.height == ForbiddenIslandWorld.MAX_HEIGHT) {
                result = c;
            }
        }
        return result;
    }
    void drawItem(WorldScene background) {
        background.placeImageXY(new FromFileImage("helicopter.png"),
                ((this.x + 1) * 10) - 5, ((this.y + 1) * 10) - 5);
    }
    boolean collectable(Item that, int l) {
        return this.cell.sameCell(that.cell) && l <= 1;
    }
}
// represents the world
class ForbiddenIslandWorld extends World {
    // All the cells of the game, including the ocean
    IList<Cell> board;
    // the current height of the ocean
    int waterHeight;
    // random number generator
    Player player;
    // the items that need to be collected
    IList<Item> targets;
    // duck powerup
    PowerUp duck;
    // does the player have the duck powerup?
    boolean duckAvailible;
    // how much time left 
    int duckTimeLeft;
    // generates random number
    Random rand = new Random();
    // constant for size of island
    static final int ISLAND_SIZE = 64;
    // constant for maximum height of the world
    static final double MAX_HEIGHT = (double) ISLAND_SIZE;
    // Field to represent which island is used
    // 0 for mountain
    // 1 for random height
    // 2 for random terrain
    int mode;
    // number of ticks passed
    int ticks;
    // number of cells traversed
    int score;
    // has the game been won?
    boolean gameWon;
    // has the game been lost?
    boolean gameLost;
    // constructor for world
    ForbiddenIslandWorld(int mode) {
        this.mode = mode;
        if (this.mode == 0) {
            this.constructMountain();
        }
        else if (this.mode == 1) {
            this.constructRandom();
        }
        else if (this.mode == 2) {
            this.constructTerrain();
        }
    }
    // EFFECT: sets conditions for mountain mode
    void constructMountain() {
        ArrayList<ArrayList<Cell>> c = makeCells(allHeights());
        this.board = convert(c);
        this.player = new Player(convertLand(this.board));
        this.targets = new Cons<Item>(new Item(convertLand(this.board)),
                new Cons<Item>(new Item(convertLand(this.board)),
                        new Cons<Item>(new Item(convertLand(this.board)),
                                new Cons<Item>(new Item(convertLand(this.board)),
                                        new Cons<Item>(new Helicopter(convertLand(this.board)),
                                                new Empty<Item>())))));
        this.duck = new PowerUp(convertLand(this.board));
        this.duckAvailible = false;
        this.waterHeight = 0;
        this.ticks = 1;
        this.score = 0;
        this.gameWon = false;
        this.gameLost = false;
    }
    // EFFECT: sets conditions for random mode
    void constructRandom() {
        ArrayList<ArrayList<Cell>> c = makeCells(allHeightsRand());
        this.board = convert(c);
        this.player = new Player(convertLand(this.board));
        this.targets = new Cons<Item>(new Item(convertLand(this.board)),
                new Cons<Item>(new Item(convertLand(this.board)),
                        new Cons<Item>(new Item(convertLand(this.board)),
                                new Cons<Item>(new Item(convertLand(this.board)),
                                        new Cons<Item>(new Helicopter(convertLand(this.board)),
                                                new Empty<Item>())))));
        this.duck = new PowerUp(convertLand(this.board));
        this.duckAvailible = false;
        this.waterHeight = 0;
        this.ticks = 1;
        this.score = 0;
        this.gameWon = false;
        this.gameLost = false;
    }
    // EFFECT: sets conditions for terrain mode
    void constructTerrain() {
        ArrayList<ArrayList<Cell>> c = makeCellsRand(allHeightsZero());
        this.board = convert(c);
        this.player = new Player(convertLand(this.board));
        this.targets = new Cons<Item>(new Item(convertLand(this.board)),
                new Cons<Item>(new Item(convertLand(this.board)),
                        new Cons<Item>(new Item(convertLand(this.board)),
                                new Cons<Item>(new Item(convertLand(this.board)),
                                        new Cons<Item>(new Helicopter(convertLand(this.board)),
                                                new Empty<Item>())))));
        this.duck = new PowerUp(convertLand(this.board));
        this.duckAvailible = false;
        this.waterHeight = 0;
        this.ticks = 1;
        this.score = 0;
        this.gameWon = false;
        this.gameLost = false;
    }
    // make a list of all heights for a perfectly rectangular mountain
    ArrayList<ArrayList<Double>> allHeights() {
        ArrayList<ArrayList<Double>> alisty = new ArrayList<ArrayList<Double>>();
        for (int y = 0; y < ISLAND_SIZE + 1; y += 1) {
            ArrayList<Double> alistx = new ArrayList<Double>();
            for (int x = 0; x < ISLAND_SIZE + 1; x += 1) {
                alistx.add((double) MAX_HEIGHT - (Math.abs(x - ISLAND_SIZE / 2) +
                                                  Math.abs(y - ISLAND_SIZE / 2)));
            }
            alisty.add(alistx);
        }
        return alisty;
    }
    // make a list of all heights for a diamond island of random heights
    ArrayList<ArrayList<Double>> allHeightsRand() {
        ArrayList<ArrayList<Double>> alisty = new ArrayList<ArrayList<Double>>();
        for (int y = 0; y < ISLAND_SIZE + 1; y += 1) {
            ArrayList<Double> alistx = new ArrayList<Double>();
            for (int x = 0; x < ISLAND_SIZE + 1; x += 1) {
                alistx.add((double) this.rand.nextInt((int) MAX_HEIGHT));
            }
            alisty.add(alistx);
        }
        return alisty;
    }
    // make a list of all heights set as zero
    ArrayList<ArrayList<Double>> allHeightsZero() {
        ArrayList<ArrayList<Double>> alisty = new ArrayList<ArrayList<Double>>();
        for (int y = 0; y < ISLAND_SIZE + 1; y += 1) {
            ArrayList<Double> alistx = new ArrayList<Double>();
            for (int x = 0; x < ISLAND_SIZE + 1; x += 1) {
                if ((x == 0 && y == ISLAND_SIZE / 2) ||
                    (x == ISLAND_SIZE / 2 && y == 0) || 
                    (x == ISLAND_SIZE && y == ISLAND_SIZE / 2) ||
                    (x == ISLAND_SIZE / 2 && y == ISLAND_SIZE)) {
                    alistx.add(1.0);
                }
                else if (x == ISLAND_SIZE / 2 && y == ISLAND_SIZE / 2) {
                    alistx.add(MAX_HEIGHT);
                }
                else if (x == 0 || x == ISLAND_SIZE ||
                         y == 0 || (y == ISLAND_SIZE)) {
                    alistx.add(0.0);
                }
                else {
                    alistx.add(null);
                }
            }
            alisty.add(alistx);
        }
        setHeights(alisty, alisty.get(0).get(0),
                           alisty.get(0).get(ISLAND_SIZE / 2),
                           alisty.get(ISLAND_SIZE / 2).get(0),
                           alisty.get(ISLAND_SIZE / 2).get(ISLAND_SIZE / 2),
                           0, ISLAND_SIZE / 2, 0, ISLAND_SIZE / 2);
        setHeights(alisty, alisty.get(0).get(ISLAND_SIZE / 2),
                           alisty.get(0).get(ISLAND_SIZE),
                           alisty.get(ISLAND_SIZE / 2).get(ISLAND_SIZE / 2),
                           alisty.get(ISLAND_SIZE / 2).get(ISLAND_SIZE),
                           ISLAND_SIZE / 2, ISLAND_SIZE, 0, ISLAND_SIZE / 2);
        setHeights(alisty, alisty.get(ISLAND_SIZE / 2).get(0),
                           alisty.get(ISLAND_SIZE / 2).get(ISLAND_SIZE / 2),
                           alisty.get(ISLAND_SIZE).get(0),
                           alisty.get(ISLAND_SIZE).get(ISLAND_SIZE / 2),
                           0, ISLAND_SIZE / 2, ISLAND_SIZE / 2, ISLAND_SIZE);
        setHeights(alisty, alisty.get(ISLAND_SIZE / 2).get(ISLAND_SIZE / 2),
                           alisty.get(ISLAND_SIZE / 2).get(ISLAND_SIZE),
                           alisty.get(ISLAND_SIZE).get(ISLAND_SIZE / 2),
                           alisty.get(ISLAND_SIZE).get(ISLAND_SIZE),
                           ISLAND_SIZE / 2, ISLAND_SIZE, ISLAND_SIZE / 2, ISLAND_SIZE);
        return alisty;
    }
    // EFFECT: set the heights for a randomly generated terrain
    void setHeights(ArrayList<ArrayList<Double>> h,
            double tl, double tr, double bl, double br,
            int lowx, int highx, int lowy, int highy) {
        double t;
        if (h.get(lowy).get((lowx + highx) / 2) == null) {
            t = Math.min(MAX_HEIGHT, this.randomInt((highx - lowx) + (highy - lowy)) + 
                    (tl + tr) / 2);
            h.get(lowy).set((lowx + highx) / 2, t);
        }
        else {
            t = h.get(lowy).get((lowx + highx) / 2);
        }
        double b;
        if (h.get(highy).get((lowx + highx) / 2) == null) {
            b = Math.min(MAX_HEIGHT, this.randomInt((highx - lowx) + (highy - lowy)) + 
                    (bl + br) / 2);
            h.get(highy).set((lowx + highx) / 2, b);
        }
        else {
            b = h.get(highy).get((lowx + highx) / 2);
        }
        double l;
        if (h.get((lowy + highy) / 2).get(lowx) == null) {
            l = Math.min(MAX_HEIGHT, this.randomInt((highx - lowx) + (highy - lowy)) + 
                    (tl + bl) / 2);
            h.get((lowy + highy) / 2).set(lowx, l);
        }
        else {
            l = h.get((lowy + highy) / 2).get(lowx);
        }
        double r;
        if (h.get((lowy + highy) / 2).get(highx) == null) {
            r = Math.min(MAX_HEIGHT, this.randomInt((highx - lowx) + (highy - lowy)) + 
                    (tr + br) / 2);
            h.get((lowy + highy) / 2).set(highx, r);
        }
        else {
            r = h.get((lowy + highy) / 2).get(highx);
        }
        double m;
        if (h.get((lowy + highy) / 2).get((lowx + highx) / 2) == null) { 
            m = Math.min(MAX_HEIGHT, this.randomInt((highx - lowx) + (highy - lowy)) + 
                    (tl + tr + bl + br) / 4);
            h.get((lowy + highy) / 2).set((lowx + highx) / 2, m);
        }
        else {
            m = h.get((lowy + highy) / 2).get((lowx + highx) / 2);
        }
        if (highx - lowx > 1) {
            setHeights(h, tl, t, l, m,
                    lowx, (lowx + highx) / 2, lowy, (lowy + highy) / 2);
            setHeights(h, t, tr, m, r,
                    (lowx + highx) / 2, highx, lowy, (lowy + highy) / 2);
            setHeights(h, l, m, bl, b,
                    lowx, (lowx + highx) / 2, (lowy + highy) / 2, highy);
            setHeights(h, m, r, b, br,
                    (lowx + highx) / 2, highx, (lowy + highy) / 2, highy);
        }
    }
    // make a list of Cells in a diamond island formation
    ArrayList<ArrayList<Cell>> makeCells(ArrayList<ArrayList<Double>> h) {
        ArrayList<ArrayList<Cell>> alisty = new ArrayList<ArrayList<Cell>>();
        for (int y = 0; y < ISLAND_SIZE + 1; y += 1) {
            ArrayList<Cell> alistx = new ArrayList<Cell>();
            for (int x = 0; x < ISLAND_SIZE + 1; x += 1) {
                if ((Math.abs(x - ISLAND_SIZE / 2) +
                     Math.abs(y - ISLAND_SIZE / 2)) < (ISLAND_SIZE / 2)) {
                    alistx.add(new Cell(x, y, h.get(y).get(x), false));
                }
                else {
                    alistx.add(new OceanCell(x, y));
                }
            }
            alisty.add(alistx);
        }
        setNeibs(alisty);
        return alisty;
    }
    // make a list of Cells in a random formation
    ArrayList<ArrayList<Cell>> makeCellsRand(ArrayList<ArrayList<Double>> h) {
        ArrayList<ArrayList<Cell>> alisty = new ArrayList<ArrayList<Cell>>();
        for (int y = 0; y < ISLAND_SIZE + 1; y += 1) {
            ArrayList<Cell> alistx = new ArrayList<Cell>();
            for (int x = 0; x < ISLAND_SIZE + 1; x += 1) {
                if (h.get(y).get(x) > 0) {
                    alistx.add(new Cell(x, y, h.get(y).get(x), false));
                }
                else {
                    alistx.add(new OceanCell(x, y));
                }
            }
            alisty.add(alistx);
        }
        setNeibs(alisty);
        return alisty;
    }
    // EFFECT: set the neighbors for each Cell
    void setNeibs(ArrayList<ArrayList<Cell>> c) {
        for (Integer y = 0; y < ISLAND_SIZE + 1; y += 1) {
            for (Integer x = 0; x < ISLAND_SIZE + 1; x += 1) {
                if (y == 0 && x == 0) {
                    c.get(y).get(x).setTop(c.get(y).get(x));
                    c.get(y).get(x).setBottom(c.get(y + 1).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x));
                    c.get(y).get(x).setRight(c.get(y).get(x + 1));
                }
                else if (y == 0 && x == ISLAND_SIZE) {
                    c.get(y).get(x).setTop(c.get(y).get(x));
                    c.get(y).get(x).setBottom(c.get(y + 1).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x - 1));
                    c.get(y).get(x).setRight(c.get(y).get(x));
                }
                else if (y == ISLAND_SIZE && x == 0) {
                    c.get(y).get(x).setTop(c.get(y - 1).get(x));
                    c.get(y).get(x).setBottom(c.get(y).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x));
                    c.get(y).get(x).setRight(c.get(y).get(x + 1));
                }
                else if (y == ISLAND_SIZE && x == ISLAND_SIZE) {
                    c.get(y).get(x).setTop(c.get(y - 1).get(x));
                    c.get(y).get(x).setBottom(c.get(y).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x - 1));
                    c.get(y).get(x).setRight(c.get(y).get(x));
                }
                else if (y == 0) {
                    c.get(y).get(x).setTop(c.get(y).get(x));
                    c.get(y).get(x).setBottom(c.get(y + 1).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x - 1));
                    c.get(y).get(x).setRight(c.get(y).get(x + 1));
                }
                else if (x == 0) {
                    c.get(y).get(x).setTop(c.get(y - 1).get(x));
                    c.get(y).get(x).setBottom(c.get(y + 1).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x));
                    c.get(y).get(x).setRight(c.get(y).get(x + 1));
                }
                else if (y == ISLAND_SIZE) {
                    c.get(y).get(x).setTop(c.get(y - 1).get(x));
                    c.get(y).get(x).setBottom(c.get(y).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x - 1));
                    c.get(y).get(x).setRight(c.get(y).get(x + 1));
                }
                else if (x == ISLAND_SIZE) {
                    c.get(y).get(x).setTop(c.get(y - 1).get(x));
                    c.get(y).get(x).setBottom(c.get(y + 1).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x - 1));
                    c.get(y).get(x).setRight(c.get(y).get(x));
                }
                else {
                    c.get(y).get(x).setTop(c.get(y - 1).get(x));
                    c.get(y).get(x).setBottom(c.get(y + 1).get(x));
                    c.get(y).get(x).setLeft(c.get(y).get(x - 1));
                    c.get(y).get(x).setRight(c.get(y).get(x + 1));
                }
            }
        }
    }
    // convert the given ArrayList<ArrayList<Cell>> to an IList<Cell>
    IList<Cell> convert(ArrayList<ArrayList<Cell>> c) {
        IList<Cell> base = new Empty<Cell>();
        for (int y = 0; y < ISLAND_SIZE + 1; y += 1) {
            for (int x = 0; x < ISLAND_SIZE + 1; x += 1) {
                base = new Cons<Cell>(c.get(y).get(x), base);
            }
        }
        return base;
    }
    // convert the given ArrayList<ArrayList<Cell>> to an IList<Cell>
    // excluding the flooded cells
    IList<Cell> convertLand(IList<Cell> loc) {
        IList<Cell> base = new Empty<Cell>();
        for (Cell c : loc) {
            if (!c.isFlooded) {
                base = new Cons<Cell>(c, base);
            }
        }
        return base;
    }
    // helper method to generate a random number in the range -n to n
    int randomInt(int n) {
        return -n + (new Random().nextInt(2 * n + 1));
    }
    //EFFECT: update the world each tick
    public void onTick() {
        if (this.ticks % 10 == 0) {
            this.waterHeight += 1;
            if (player.aquatic) {
                this.duckTimeLeft -= 1;
                if (this.duckTimeLeft == 0) {
                    player.aquatic = false;
                }
            }
        }
        if (this.ticks % 50 == 0) {
            this.duck = new PowerUp(convertLand(this.board));
        }
        if (!gameWon && !gameLost) {
            this.ticks += 1;
        }
        for (Cell c : this.board) {
            if (c.nextFlooded() && this.waterHeight >= c.height) {
                c.isFlooded = true;
                c.left.floodFill();
                c.right.floodFill();
                c.bottom.floodFill();
                c.top.floodFill();
            }
        }
        if ((this.player.cell.isFlooded && !player.aquatic) || this.ticks >= 640) {
            this.gameLost = true;
        }
        else if (this.targets.length() == 0) {
            this.gameWon = true;
        }
    }
    //EFFECT: update the world with the appropriate keys
    public void onKeyEvent(String k) {
        if (k.equals("m")) {
            this.constructMountain();
        }
        else if (k.equals("r")) {
            this.constructRandom();
        }
        else if (k.equals("t")) {
            this.constructTerrain();
        }
        else if (this.targets.length() == 0) {
            this.gameWon = true;
        }
        else if (this.player.cell.isFlooded && !player.aquatic) {
            this.gameLost = true;
        }
        else if (k.equals("up") && (!this.player.cell.top.isFlooded || player.aquatic)) {
            moveHelper(this.player.cell.top, 0, -1);
        }
        else if (k.equals("down") && (!this.player.cell.bottom.isFlooded || player.aquatic)) {
            moveHelper(this.player.cell.bottom, 0, 1);
        }
        else if (k.equals("left") && (!this.player.cell.left.isFlooded || player.aquatic)) {
            moveHelper(this.player.cell.left, -1, 0);
        }
        else if (k.equals("right") && (!this.player.cell.right.isFlooded || player.aquatic)) {
            moveHelper(this.player.cell.right, 1, 0);
        }
        else if (k.equals("s") && this.duckAvailible) {
            player.aquatic = true;
            this.duckAvailible = false;
            this.duckTimeLeft = 5;
        }
    }
    //helper method to move the player
    public void moveHelper(Cell c, int x, int y) {
        this.player.cell = c;
        this.player.x += x;
        this.player.y += y;
        this.score += 1;
        IList<Item> t = new Empty<Item>();
        for (Item i : this.targets) {
            if (!i.collectable(player, this.targets.length())) {
                t = new Cons<Item>(i, t);
            }
            if (duck.collectable(player, 0)) {
                this.duckAvailible = true;
                this.duck = new PowerUp(convertLand(this.board));
            }
        }
        this.targets = t;
    }
    // displays the scene
    public WorldScene makeScene() {
        WorldScene bg = this.getEmptyScene();
        for (Cell c : this.board) {
            c.drawCell(bg, this.waterHeight);
        }
        for (Item i : this.targets) {
            i.drawItem(bg);
        }
        if (!this.duckAvailible && !player.aquatic) {
            duck.drawItem(bg);
        }
        player.drawItem(bg);
        if (this.gameWon) {
            bg.placeImageXY(new TextImage("You Win!", ISLAND_SIZE, Color.red),
                    (ISLAND_SIZE / 2) * 10, (ISLAND_SIZE / 2) * 10);
            bg.placeImageXY(new TextImage("Total steps taken: " +
                    Integer.toString(this.score), ISLAND_SIZE / 4, Color.red),
                    (ISLAND_SIZE / 2) * 10, ISLAND_SIZE * 7);
        }
        else if (this.gameLost) {
            bg.placeImageXY(new TextImage("You Drowned", ISLAND_SIZE, Color.red),
                    (ISLAND_SIZE / 2) * 10, (ISLAND_SIZE / 2) * 10);
        }
        else if (player.aquatic) {
            bg.placeImageXY(new TextImage(String.format("%02d:%02d", 
                    (ISLAND_SIZE - 1 - ticks / 10) % 3600 / 60, 
                    (ISLAND_SIZE - 1 - ticks / 10) % 60),
                    ISLAND_SIZE / 4, Color.red),
                    ISLAND_SIZE * 9, ISLAND_SIZE / 4);
            bg.placeImageXY(new TextImage(Integer.toString(duckTimeLeft),
                    ISLAND_SIZE / 4, Color.green),
                    ISLAND_SIZE * 9, ISLAND_SIZE / 2);
        }
        else {
            bg.placeImageXY(new TextImage(String.format("%02d:%02d", 
                    (ISLAND_SIZE - 1 - ticks / 10) % 3600 / 60, 
                    (ISLAND_SIZE - 1 - ticks / 10) % 60),
                    ISLAND_SIZE / 4, Color.red),
                    ISLAND_SIZE * 9, ISLAND_SIZE / 4);
        }
        return bg;
    }
}
//class to make an IList iterable
class IListIterator<T> implements Iterator<T> {
    IList<T> items;
    IListIterator(IList<T> items) {
        this.items = items;
    }
    public boolean hasNext() {
        return this.items.isCons();
    }
    public T next() {
        Cons<T> itemsAsCons = this.items.asCons();
        T answer = itemsAsCons.first;
        this.items = itemsAsCons.rest;
        return answer;
    }
    public void remove() {
        throw new UnsupportedOperationException("Don't do this!");
    }
}
// interface representing an IList
interface IList<T> extends Iterable<T> {
    boolean isCons();
    Cons<T> asCons();
    Iterator<T> iterator();
    int length();
}
// represents an empty list
class Empty<T> implements IList<T> {
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
    public boolean isCons() {
        return false;
    }
    public Cons<T> asCons() {
        return null;
    }
    public int length() {
        return 0;
    }
}
// represents a non empty list
class Cons<T> implements IList<T> {
    T first;
    IList<T> rest;
    // constructor for Cons<T>
    Cons(T first, IList<T> rest) {
        this.first = first;
        this.rest = rest;
    }
    public Iterator<T> iterator() {
        return new IListIterator<T>(this);
    }
    public boolean isCons() {
        return true;
    }
    public Cons<T> asCons() {
        return this;
    }
    public int length() {
        return 1 + this.rest.length();
    }
}
// examples class
class ExamplesWorld {
    ForbiddenIslandWorld world1;
    ForbiddenIslandWorld world2;
    ForbiddenIslandWorld world3;
    ArrayList<ArrayList<Cell>> alist1;
    ArrayList<ArrayList<Cell>> alist2;
    ArrayList<ArrayList<Cell>> alist3;
    void initTestConditions() {
        world1 = new ForbiddenIslandWorld(0);
        world2 = new ForbiddenIslandWorld(1);
        world3 = new ForbiddenIslandWorld(2);
        alist1 = world1.makeCells(world1.allHeights());
        alist2 = world2.makeCells(world2.allHeightsRand());
        alist3 = world3.makeCellsRand(world3.allHeightsZero());
    }
    // tester method
    void testConditions(Tester t) {
        this.initTestConditions();
        t.checkExpect(alist1.get(1).get(3).right.sameCell(alist1.get(1).get(4)), true);
        t.checkExpect(alist1.get(1).get(3).left.sameCell(alist1.get(1).get(2)), true);
        t.checkExpect(alist1.get(1).get(3).top.sameCell(alist1.get(0).get(3)), true);
        t.checkExpect(alist1.get(1).get(3).bottom.sameCell(alist1.get(2).get(3)), true);
        t.checkExpect(alist1.get(0).get(3).top.sameCell(alist1.get(0).get(3)), true);
        t.checkExpect(alist1.get(64).get(3).bottom.sameCell(alist1.get(64).get(3)), true);
        t.checkExpect(alist1.get(32).get(0).left.sameCell(alist1.get(32).get(0)), true);
        t.checkExpect(alist1.get(32).get(64).right.sameCell(alist1.get(32).get(64)), true);
        t.checkExpect(alist1.get(64).get(3).bottom.bottom.sameCell(alist1.get(64).get(3)), true);
        t.checkExpect(alist1.get(4).get(4).sameCell(alist1.get(8).get(8)), false);
        t.checkExpect(new Cell(4, 4, 30.0, false).sameCell(new Cell(4, 4, 30.0, false)), true);
        t.checkExpect(new Cell(4, 4, 30.0, false).sameCell(new Cell(1, 4, 30.0, false)), false);
        t.checkExpect(new Cell(4, 4, 30.0, false).sameCell(new Cell(4, 1, 30.0, false)), false);
        t.checkExpect(new Cell(4, 4, 30.0, false).sameCell(new Cell(4, 4, 32.0, false)), false);
        t.checkExpect(alist1.get(32).get(32).height, 64.0);
        t.checkExpect(alist1.get(32).get(32).chooseColor(0), Color.WHITE);
        t.checkExpect(alist1.get(0).get(0).chooseColor(0), new Color(0, 0, 128));
        t.checkExpect(alist2.get(1).get(3).right.sameCell(alist2.get(1).get(4)), true);
        t.checkExpect(alist2.get(1).get(3).left.sameCell(alist2.get(1).get(2)), true);
        t.checkExpect(alist2.get(1).get(3).top.sameCell(alist2.get(0).get(3)), true);
        t.checkExpect(alist2.get(1).get(3).bottom.sameCell(alist2.get(2).get(3)), true);
        t.checkExpect(alist2.get(0).get(3).top.sameCell(alist2.get(0).get(3)), true);
        t.checkExpect(alist2.get(64).get(3).bottom.sameCell(alist2.get(64).get(3)), true);
        t.checkExpect(alist2.get(32).get(0).left.sameCell(alist2.get(32).get(0)), true);
        t.checkExpect(alist2.get(32).get(64).right.sameCell(alist2.get(32).get(64)), true);
        t.checkExpect(alist2.get(64).get(3).bottom.bottom.sameCell(alist2.get(64).get(3)), true);
        t.checkNumRange(alist2.get(32).get(32).height, 0.0, 64.0);
        t.checkNumRange(alist2.get(43).get(24).height, 0.0, 64.0);
        t.checkExpect(alist3.get(1).get(3).right.sameCell(alist3.get(1).get(4)), true);
        t.checkExpect(alist3.get(1).get(3).left.sameCell(alist3.get(1).get(2)), true);
        t.checkExpect(alist3.get(1).get(3).top.sameCell(alist3.get(0).get(3)), true);
        t.checkExpect(alist3.get(1).get(3).bottom.sameCell(alist3.get(2).get(3)), true);
        t.checkExpect(alist3.get(0).get(3).top.sameCell(alist3.get(0).get(3)), true);
        t.checkExpect(alist3.get(64).get(3).bottom.sameCell(alist3.get(64).get(3)), true);
        t.checkExpect(alist3.get(32).get(0).left.sameCell(alist3.get(32).get(0)), true);
        t.checkExpect(alist3.get(32).get(64).right.sameCell(alist3.get(32).get(64)), true);
        t.checkExpect(alist3.get(64).get(3).bottom.bottom.sameCell(alist3.get(64).get(3)), true);
        t.checkExpect(alist3.get(4).get(4).sameCell(alist3.get(8).get(8)), false);
        t.checkExpect(alist1.get(1).get(1).isFlooded, true);
        t.checkExpect(alist1.get(32).get(32).isFlooded, false);
        t.checkExpect(alist1.get(30).get(10).isFlooded, false);
        for (Cell c: world1.board) {
            if (c.height == 64.0) {
                c.isFlooded = true;
                c.floodFill();
            }
        }
        for (Cell c: world1.board) {
            t.checkExpect(c.isFlooded, true);
        }
        this.initTestConditions();
        world1.player.y = 10;
        world1.onKeyEvent("up");
        t.checkExpect(world1.player.y, 9);
        world1.player.y = 10;
        world1.onKeyEvent("down");
        t.checkExpect(world1.player.y, 11);
        world1.player.x = 10;
        world1.onKeyEvent("left");
        t.checkExpect(world1.player.x, 9);
        world1.player.x = 10;
        world1.onKeyEvent("right");
        t.checkExpect(world1.player.x, 11);
        world1.player.y = 10;
        world1.player.x = 10;
        world1.moveHelper(world1.player.cell, 30, 5);
        t.checkExpect(world1.player.y, 15);
        t.checkExpect(world1.player.x, 40);
        this.initTestConditions();
        for (Cell c : world1.board) {
            if (c.height == 64.0) {
                world1.player.cell = c;
                world1.player.x = c.x;
                world1.player.y = c.y;
            }
        }
        t.checkExpect(world1.waterHeight, 0);
        for (int i = 0; i < 10; i += 1) {
            this.world1.onTick();
        }
        t.checkExpect(world1.waterHeight, 1);
        for (int i = 0; i < 590; i += 1) {
            this.world1.onTick();
        }
        t.checkExpect(world1.waterHeight, 60);
        for (Cell c : world1.board) {
            if (c.height > 60) {
                t.checkExpect(c.isFlooded, false);
            }
            else {
                t.checkExpect(c.isFlooded, true);
            }
        }
        t.checkExpect(world1.duckAvailible, false);
        for (Cell c : world1.board) {
            if (c.height == 64.0) {
                world1.duck.cell = c;
                world1.duck.x = c.x;
                world1.duck.y = c.y;
            }
        }
        world1.onKeyEvent("up");
        world1.onKeyEvent("down");
        t.checkExpect(world1.score, 2);
        t.checkExpect(world1.duckAvailible, true);
        t.checkExpect(world1.player.aquatic, false);
        world1.onKeyEvent("s");
        t.checkExpect(world1.player.aquatic, true);
        for (int i = 0; i < 10; i += 1) {
            world1.onKeyEvent("left");
        }
        t.checkExpect(world1.gameLost, false);
        for (int i = 0; i < 50; i += 1) {
            world1.onTick();
        }
        t.checkExpect(world1.gameLost, true);
        t.checkExpect(world1.score, 12);
    }
    // runs the game
    void testWorld(Tester t) {
        ForbiddenIslandWorld w = new ForbiddenIslandWorld(2);
        w.bigBang((ForbiddenIslandWorld.ISLAND_SIZE + 1) * 10, 
                (ForbiddenIslandWorld.ISLAND_SIZE + 1) * 10, .1);
    }
}