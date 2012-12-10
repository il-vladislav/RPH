package rph.labyrinth.players;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import rph.labyrinth.Path;
import rph.labyrinth.Player;

public class iliusvlaPlayer extends Player {
    

    @Override
    protected String getName() {
        return "iliusvlaPlayer";
    }
    public ArrayList<Point> coordinates;
    public static int pole[][];
    public static int finishx;
    public static int finishy;
    public static int startx;
    public static int starty;
    static Path smile = new Path(Color.YELLOW);
    private int[] dxArr = new int[]{-1, 0, 1};
    private int[] dyArr = new int[]{-1, 0, 1};
    public static int WIDTH;
    public static int HEIGHT;

    @Override
    protected Path findPath(int[][] map) {
        smile = new Path(Color.YELLOW);
        pole = map;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map.length; j++) {
                if (map[i][j] == 255) {
                    finishx = i;
                    finishy = j;
                    System.err.print(finishx);
                }
                if (map[i][j] == 16711680) {
                    startx = i;
                    starty = j;
                    smile.addCoordinate(i, j);
                }
            }
        }
        WIDTH = map.length;
        HEIGHT = WIDTH;
        fh();
        smile.addCoordinate(finishx, finishy);
        if(noroute) return null;
        return smile;
    }
    
    
    
    //Classic A*-algorithm. I used a http://code.google.com/p/a-star/source/browse/trunk/java/PathFinder.java 
    //work in Java, and some work in Python  for it
    boolean noroute = false;
    public void fh() {
        // Make all list's
        Table<Cell> cellList = new Table<Cell>(WIDTH, HEIGHT);
        Table blockList = new Table(WIDTH, HEIGHT);
        LinkedList<Cell> openList = new LinkedList<Cell>();
        LinkedList<Cell> closedList = new LinkedList<Cell>();
        LinkedList<Cell> tmpList = new LinkedList<Cell>();

        //Set block-cell's
        for (int i = 0; i < pole.length; i++) {
            for (int j = 0; j < pole.length; j++) {
                if (pole[i][j] == 0) {
                    blockList.add(new Cell(i, j, true));
                }
            }
        }


        // Set free cell's
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                cellList.add(new Cell(j, i, blockList.get(j, i).blocked));
            }
        }

        // Start and finish
        cellList.get(finishx, finishy).setAsStart();
        cellList.get(startx, starty).setAsFinish();
        Cell start = cellList.get(finishx, finishy);
        Cell finish = cellList.get(startx, starty); 
        // Let's start
        boolean found = false;   
        noroute = false;
        //1) Add start-cell to open list
        openList.push(start);

        //2) Repeat next:
        while (!found && !noroute) {
            //a) Search in open list cell with lowest price.
            Cell min = openList.getFirst();
            for (Cell cell : openList) {                
                if (cell.F < min.F) {//If you want another way - set it to <=
                    min = cell;
                }
            }

            //b)Set it to closed list and delit from openlist
            closedList.push(min);
            openList.remove(min);
            //System.out.println(openList);

            //c) If your path may pass diagonally - Uncomment this
            tmpList.clear();
//            tmpList.add(cellList.get(min.x - 1, min.y - 1));
            tmpList.add(cellList.get(min.x, min.y - 1));
//            tmpList.add(cellList.get(min.x + 1, min.y - 1));
            tmpList.add(cellList.get(min.x + 1, min.y));
//            tmpList.add(cellList.get(min.x + 1, min.y + 1));
            tmpList.add(cellList.get(min.x, min.y + 1));
//            tmpList.add(cellList.get(min.x - 1, min.y + 1));
            tmpList.add(cellList.get(min.x - 1, min.y));

            for (Cell neightbour : tmpList) {
                //If cell is blocked or in Block-list - ignore it.
                if (neightbour.blocked || closedList.contains(neightbour)) {
                    continue;
                }

                //Add cell to open-list. Counting H,F,G-cost
                if (!openList.contains(neightbour)) {
                    openList.add(neightbour);
                    neightbour.parent = min;
                    neightbour.H = neightbour.mandist(finish);
                    neightbour.G = neightbour.price(min);
                    neightbour.F = neightbour.H + neightbour.G;
                    continue;
                }

                // Check open cell to lower cost
                if (neightbour.G < min.G + neightbour.price(min)) {                    
                    neightbour.parent = min.parent;
                    neightbour.H = neightbour.mandist(finish);
                    neightbour.G = neightbour.price(min);
                    neightbour.F = neightbour.H + neightbour.G;
                }               
            }
            //Stop if:
            //Add finish cell to Open list.
            //Or Open list is empry. It's problem

            if (openList.contains(finish)) {
                found = true;
            }
            if (openList.isEmpty()) {
                noroute = true;
            }
        }

        //3) Save path. Move from finish to start.
        if (!noroute) {
            Cell rd = finish.parent;
            while (!rd.equals(start)) {
                rd.road = true;
                smile.addCoordinate(rd.x, rd.y);
                System.out.println(rd.x + " " + rd.y);
                rd = rd.parent;

                if (rd == null) {
                    break;
                }
            }        
        } else {
            System.out.println("NO ROUTE");
        }

    }
    
    
    
    class Cell {

    /**
    
     * Make cell with -x -y coordinates
    
     * @param blocked it is blocked(black square)
    
     */
    public Cell(int x, int y, boolean blocked) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
    }

    /**
    
     * Manhattan algorithm. It's classic!
    
     * @param finish finish cell
    
     * @return distance
    
     */
    public int mandist(Cell finish) {
        return 10 * (Math.abs(this.x - finish.x) + Math.abs(this.y - finish.y));
    }

    /**
    
     * Calculating costs of path to near cell
    
     * @param finish near cell     
    
     */
    public int price(Cell finish) {
        if (this.x == finish.x || this.y == finish.y) {
            return 10;
        } else {
            return 14;
        }
    }

    /**
    
     * Set cell to start-cell
    
     */
    public void setAsStart() {
        this.start = true;
    }

    /**
    
     * Set cell to finish-cell
    
     */
    public void setAsFinish() {
        this.finish = true;
    }

    public boolean equals(Cell second) {
        return (this.x == second.x) && (this.y == second.y);

    }
    public int x = -1;
    public int y = -1;
    public Cell parent = this;
    public boolean blocked = false;
    public boolean start = false;
    public boolean finish = false;
    public boolean road = false;
    public int F = 0;
    public int G = 0;
    public int H = 0;
}

class Table<T extends Cell> {

    public int width;
    public int height;
    private Cell[][] table;

    public Table(int width, int height) {
        this.width = width;
        this.height = height;
        this.table = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                table[i][j] = new Cell(0, 0, false);
            }
        }
    }

    /**
    
     * Add cell to table
    
     */
    public void add(Cell cell) {
        table[cell.x][cell.y] = cell;
    }

    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        if (x < width && x >= 0 && y < height && y >= 0) {
            return (T) table[x][y];
        }
        return (T) (new Cell(0, 0, true)); //It work in JAVA!! Profit.

    }
}


}