import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.libjcsi.textcomponents.MenuBox;
import java.util.Properties;
import java.util.ArrayList;
import java.util.*;

public class LastManStanding {

  // console screen dimensions -- make static final
  private static final int SCREEN_WIDTH = 80;
  private static final int SCREEN_HEIGHT = 24;
  private static final int SPLASH_DEPTH = 6;

  public static void main(String[] args) {
    //ArrayList<Object> LastManStanding = new ArrayList<Object>();
    ArrayList<Item> items = new ArrayList<Item>();
    ArrayList<Creature> creatures = new ArrayList<Creature>();
    ArrayList<Cell> cells = new ArrayList<Cell>();
    ArrayList<Cell> creatureCells = new ArrayList<Cell>();
    ArrayList<String> deathLog = new ArrayList<String>();

    int turnNumber = 1;
    int numRows = 10;
    int numCols = 10;
    int numCreatures = 5;

    int testRuns = 0;


    Properties text = new Properties();
		text.setProperty("fontSize","12");
		//text.setProperty("font", "Courier New");
    text.setProperty("font", "Ariel");
		ConsoleSystemInterface csi = null;
		try{
			csi = new WSwingConsoleInterface("Last Man Standing", text);
		} catch (ExceptionInInitializerError eiie) {
			System.out.println("*** Error: Swing Console Box cannot be initialized. Exiting...");
			eiie.printStackTrace();
			System.exit(-1);
		}

          //bad command line argument checking
          if (args.length > 0) {
            numCreatures = Integer.parseInt(args[0]);
            numRows = Integer.parseInt(args[1]);
            numCols = Integer.parseInt(args[2]);
          }



          /* display is SCREEN_WIDTH x 24 MAX */
          /* our grid space is limited to that - Splash display */


          int maxRows = 0;
          int maxCols = 0;

          // TODO - TOP_LEFT, BOTTOM_RIGHT, etc...
          // see what we have left
          maxRows = SCREEN_HEIGHT - SPLASH_DEPTH;
          maxCols = (SCREEN_WIDTH-20)/2; // keep it a square

          // set numRows to at most ... maxRows
          numRows = (numRows < maxRows) ? numRows : maxRows;
          numCols = (numCols < maxCols) ? numCols : maxCols;

          /* TODO - DO WE NEED THESE EMPTY Cells ? */
          // we need to create our grid (dungeon) which we took
          // as command line args AND modified if too large
          for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
              // add a blank cell that will be displayed as " "
              cells.add(new Cell(r, c, " "));
            }
          }
          //System.out.println("Elements of ArrayList of Cell Type: " + cells);

          /* test --- add some creatures */
          /* TODO - make creatures based on game Level */
          cells.set(XYCoordinate.xyCoordToList(0,0, numRows), new Cell(0,0, new Goblin("Goblin01", 100, 90, 20, 10, true, "G")));
          cells.set(XYCoordinate.xyCoordToList(0,1, numRows), new Cell(0,1, new Goblin("Goblin022", 100, 30, 70, 10, true, "G")));
          cells.set(XYCoordinate.xyCoordToList(3,8, numRows), new Cell(3,8, new Goblin("Goblin03", 100, 60, 89, 10, true, "G")));
          cells.set(XYCoordinate.xyCoordToList(1,4, numRows), new Cell(1,4, new Human("Human01", 100, 60, 35, 10, true, "H")));
          cells.set(XYCoordinate.xyCoordToList(2,2, numRows), new Cell(2,2, new Zombie("Zombie01", 100, 60, 56, 10, true, "Z")));
          cells.set(XYCoordinate.xyCoordToList(5,2, numRows), new Cell(5,2, new Zombie("Zombie02", 100, 60, 11, 10, true, "Z")));


          /**************/
          /* USER INPUT */
          /**************/
          int selection = -1;
          int level = 1;
          int skillPoints = 100;
          int strength = 0;
          int agility = 0;
          int luck = 0;
          Creature.Movement cm = Creature.Movement.UP; // for storing movement of user character
          CharKey dir;

          String agilityQ = new String("");
          String name;
          boolean bot = false;
          Scanner scanner;

          /* CSI splash screen */
          displaySplash(csi, 0);
          //fadeSplash(csi);
          //displayCharacterCreation(csi, 8, skillPoints);

          int y = 8;
          String qLine1 = new String("What is your Characters name ? ");
          String qLine2 = new String(String.format("Assign %d Skill points to Agility, Strength & Luck.", skillPoints));
          String qLine3 = new String("    ...Agility : ");
          String qLine4 = new String("    ...Strength : ");
          String qLine5 = new String("    ...Luck : ");
          String qLine6 = new String("Do you wish to control your character [y/n] ? ");
          String startMessage = new String ("Hit Enter to start.");

          // character creation menu
          csi.print(0, y++, qLine1, CSIColor.LAVENDER_PINK);
          csi.refresh();
          name = askQuestionStr(csi, qLine1, 8);

          csi.print(0 ,y++, qLine2, CSIColor.LAVENDER_PINK);
          csi.refresh();


          /* create user character from user input */
          csi.print(0 ,y++, qLine3, CSIColor.LAVENDER_PINK);
          csi.refresh();
          agility = askQuestionInt(csi, qLine3, 10);
          skillPoints -= agility;

          csi.print(0 ,y++, qLine4, CSIColor.LAVENDER_PINK);
          csi.refresh();
          strength = askQuestionInt(csi, qLine4, 11);
          skillPoints -= strength;

          csi.print(0 ,y++, qLine5, CSIColor.LAVENDER_PINK);
          csi.print(qLine5.length(), 12, Integer.toString(skillPoints), CSIColor.WHITE);
          csi.refresh();

          csi.print(0 ,14, qLine6, CSIColor.LAVENDER_PINK);
          csi.refresh();
          String answer = askQuestionStr(csi, qLine6, 14);
          if (answer.toUpperCase().equals("Y")) {
            bot = false;
          } else {
            bot = true;
          }

          /* create the User Character */
          cells.set(XYCoordinate.xyCoordToList(6,9, numRows), new Cell(6,9, new Human(name, 100, strength, agility, luck, bot, "@")));
          //cells.set(XYCoordinate.xyCoordToList(6,9, numRows), new Cell(6,9, new Human(name, 100, strength, agility, luck, false, "H")));

          // print the Start Game Message and wait for user input
          csi.print((int)(SCREEN_WIDTH-startMessage.length()) / 2, 18, startMessage, CSIColor.BRIGHT_PINK);
          csi.refresh();
          CharKey tempWait = csi.inkey();

          // clear the screen in preperation to display the grid
          csi.cls();
          displaySplash(csi, 0);
          csi.refresh();

          /*******************/
          /* CORE LOGIC LOOP */
          /*******************/
          XYCoordinate xy = new XYCoordinate();
          int listIndex;
          char c;

          // setup grid outline
          displayGridOutline(csi, numRows, numCols, cells);

          do {
            //System.out.println("CORE START");
            displayGrid(numRows, numCols, cells);
            displayGrid(csi, numRows, numCols, turnNumber, cells);

            // make sure we dump old entries
            creatureCells.clear();

            // copy out our Creature Cells ONLY and then we will sort them by AGILITY and use this to determine which Cells move first
            // create an ArrayList of ONLY those cells with creatures
            for (Cell cellElement : cells) {
              if (!cellElement.getIsEmpty()) {
                if (cellElement.getCreature().getIsAlive()) {
                    //System.out.println("Adding ALIVE cell for main loop " + cellElement.toString());
                  creatureCells.add(cellElement);
                }
              }
            }

            // now have ArrayList of ONLY Creatures who are still ALIVE. No empty Cells

            // now sort these cells by AGILITY
            Collections.sort(creatureCells, new CellChainedComparator(new CellAgilityComparator(), new CellXYComparator()));

            //System.out.println(creatureCells.toString());

            /* now we have things in the right order for MOVEMENT Phase (agility first) */
            /* move through the List of creatures and make moves and attacks if required */
            for (Cell cellElement : creatureCells) {

              if (cellElement.getCreature().getIsAlive()) {
                // This Creatue is Alive - Is it a PLayer Controlled Creature ?
                if (!cellElement.getCreature().getIsBot()) {
                  // ask user for a Move.
                  System.out.printf("\n%s (Agility: %d, Strength: %d, Luck: %s). Your Health is %d",
                    cellElement.getCreature().getName(), cellElement.getCreature().getAgility(), cellElement.getCreature().getStrength(),
                    cellElement.getCreature().getLuck(), cellElement.getCreature().getHealth());

                  do { // discover a valid move for this cell

                			//csi.cls();
                			//csi.print(x,y, '@', CSIColor.WHITE);
                			//csi.print(x,y, 'H', CSIColor.CYAN);
                			//csi.print(x,y, "HELP", CSIColor.CYAN);
                			//csi.print(x+1,y+1, "HELP", CSIColor.CYAN);

                			dir = csi.inkey();
                			if(dir.isUpArrow()){
                				cm = Creature.Movement.UP;
                			}
                			if(dir.isDownArrow()){
                				cm = Creature.Movement.DOWN;
                			}
                			if(dir.isLeftArrow()){
                				cm = Creature.Movement.LEFT;
                			}
                			if(dir.isRightArrow()){
                				cm = Creature.Movement.RIGHT;
                			}
                			if(dir.code == CharKey.H){
                				cm = Creature.Movement.HOLD;
                			}

                    xy = cellElement.attemptManualMove(cm);
                    // make sure it is valid / in bounds
                  } while (xy.getX() < 0 || xy.getY() < 0 || xy.getX() >= numRows || xy.getY() >= numCols); // what about walls !?

                } else {

                  do { // discover a valid move for this cell
                    xy = cellElement.attemptMove();
                    //System.out.println("Cell: " + cellElement.getXPos() + "|" + cellElement.getYPos() + " looking to move to " + xy.getX() + "|" + xy.getY());

                    // make sure it is valid / in bounds
                  } while (xy.getX() < 0 || xy.getY() < 0 || xy.getX() >= numRows || xy.getY() >= numCols); // what about walls !?

                }
                //System.out.println("MOVED\n");

                // get the cell/index in the ArrayList where this Element wishes to move.
                listIndex = XYCoordinate.xyCoordToList(xy.getX(),xy.getY(), numRows);

                // MUST RESOLVE CONFLICTS

                /* see what is in the new Cell. */
                // Is it an EMPTY Cell ?
                if (cells.get(listIndex).getIsEmpty()){
                  //System.out.printf("Cell @ (%d,%d) moving to is Empty\n", xy.getX(),xy.getY());

                  // it's just an empty cell so we can swap it
                  cells.get(listIndex).setXPos(cellElement.getXPos());
                  cells.get(listIndex).setYPos(cellElement.getYPos());

                  // update the current Cells XY coordinates
                  cellElement.setXPos(xy.getX());
                  cellElement.setYPos(xy.getY());

                } else if (cells.get(listIndex).getCreature().getIsAlive()) {
                    if ( cells.get(listIndex).getXPos() == cellElement.getXPos() && cells.get(listIndex).getYPos() == cellElement.getYPos() ) {
                        //System.out.printf("%d|%d vs %d|%d\n", cells.get(listIndex).getXPos(), cells.get(listIndex).getYPos(), cellElement.getXPos(),  cellElement.getYPos());
                        //System.out.printf("Looks like this Cell did not move\n");
                    } else {
                      /************************
                      / RESOLVE CONFLICT/
                      /************************/
                      while (cellElement.getCreature().getIsAlive() && cells.get(listIndex).getCreature().getIsAlive()) {
                        cellElement.getCreature().attack(cells.get(listIndex).getCreature());
                      }
                      // add any deaths to our DeathLog array List
                      if (!cellElement.getCreature().getIsAlive()) {
                        deathLog.add(String.format("%s Died on turn %d at the hands of %s",
                          cellElement.getCreature().getName(), turnNumber, cells.get(listIndex).getCreature().getName()));
                      }
                      if (!cells.get(listIndex).getCreature().getIsAlive() ) {
                        deathLog.add(String.format("%s Died on turn %d at the hands of %s",
                          cells.get(listIndex).getCreature().getName(), turnNumber, cellElement.getCreature().getName()));
                      }
                    }
                } else {
                  // IF WE ARE HERE IT MEANS WE ARE FIGHTING WITH SOMEONE LATER IN THE ARRAYLIST WHO DIED EARLIER IN LOOP
                  // SO JUST IGNORE
                }
              }

            }

              // sort our arrrayList back by XY so we can easily display it
              Collections.sort(cells, new CellChainedComparator(new CellXYComparator()));

              turnNumber++;

              // clear the console
              //??

            } while (creatureCells.size() > 1); ////****************************************???/

            // final display @ End Game
            displayGrid(numRows, numCols, cells);

            for (String element : deathLog) {
              System.out.println(element);
            }

            System.out.printf("\n...in %d Turns.", turnNumber);

            // print off the winner if there was one.
            if (creatureCells.size() > 0) {
              System.out.println(creatureCells.get(0).getCreature().toString());
              System.out.println(creatureCells.get(0).getCreature().getName());
              displayWinner(csi, creatureCells.get(0).getCreature().getName());
            } else {
              System.out.println("Everybody died.");
            }
  }

  /* just a method to run through an arraylist we assume maps out a grid */
  public static void displayGrid(int numRows, int numCols, ArrayList<Cell> cells) {

    for (int r = 0; r < numRows; r++) {
      System.out.printf("\n");
      for (int c = 0; c < numCols; c++) {
        if (!cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getIsEmpty()) {
          if (cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getCreature().getIsAlive()) {
            System.out.printf("|%s|", cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getDisplay());
          } else {
              System.out.printf("| |"); // DEAD
          }

        } else {
          System.out.printf("|%s|", cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getDisplay());
        }
      }
    }
    System.out.printf("\n");

  }

  public static Creature.Movement charToCharacterMove (char c) {
    Creature.Movement cm = Creature.Movement.UP;
    switch(c) {
      case 'U':
        cm = Creature.Movement.UP;
        break;

      case 'D':
        cm = Creature.Movement.DOWN;
        System.out.println("DOWN");
        break;

      case 'L':
        cm = Creature.Movement.LEFT;
      break;

      case 'R':
        cm = Creature.Movement.RIGHT;
      break;

      case 'H':
        cm = Creature.Movement.HOLD;
      break;
    }
    return cm;
  }

  public static void displaySplash(ConsoleSystemInterface csi, int depth) {
    // positioning for banner
    String line1 = new String("ooooo        ooo        ooooo  .oooooo..o ");
    String line2 = new String("`888'        `88.       .888' d8P'    `Y8 ");
    String line3 = new String(" 888          888b     d'888  Y88bo.      ");
    String line4 = new String(" 888          8 Y88. .P  888   `\"Y8888o. ");
    String line5 = new String(" 888          8  `888'   888       `\"Y88b");
    String line6 = new String(" 888       o  8    Y     888  oo     .d8P ");
    String line7 = new String("o888ooooood8 o8o        o888o 8\"\"88888P'");
    String title = new String ("Last Man Standing");
    String stitle = new String ("2016");

    // title
    csi.print((int)(SCREEN_WIDTH-line1.length()) / 2, depth, line1, CSIColor.DARK_RED);
    csi.print((int)(SCREEN_WIDTH-line2.length()) / 2 ,depth+1, line2, CSIColor.RED);
    csi.print((int)(SCREEN_WIDTH-line3.length()) / 2, depth+2, line3, CSIColor.YELLOW);
    csi.print((int)(SCREEN_WIDTH-line4.length()) / 2, depth+3, line4, CSIColor.GREEN);
    csi.print((int)(SCREEN_WIDTH-line5.length()) / 2, depth+4, line5, CSIColor.BLUE);
    csi.print((int)(SCREEN_WIDTH-line6.length()) / 2, depth+5, line6, CSIColor.DARK_BLUE);
    csi.print((int)(SCREEN_WIDTH-line7.length()) / 2, depth+6, line7, CSIColor.PURPLE);
    csi.print((int)(SCREEN_WIDTH-title.length()) / 2, depth+23, title, CSIColor.GRAY);
    csi.print((int)(SCREEN_WIDTH-stitle.length()) / 2, depth+SCREEN_HEIGHT, stitle, CSIColor.WHITE);
    csi.refresh();

    //CharKey dir = csi.inkey();
    /*for (int i = 0; i < 25; i++) {
      csi.cls();
      y+= i;
      csi.print((int)(SCREEN_WIDTH-line1.length()) / 2, y, line1, CSIColor.DARK_RED);
      csi.print((int)(SCREEN_WIDTH-line2.length()) / 2 ,y+1, line2, CSIColor.RED);
      csi.print((int)(SCREEN_WIDTH-line3.length()) / 2, y+2, line3, CSIColor.YELLOW);
      csi.print((int)(SCREEN_WIDTH-line4.length()) / 2, y+3, line4, CSIColor.GREEN);
      csi.print((int)(SCREEN_WIDTH-line5.length()) / 2, y+4, line5, CSIColor.BLUE);
      csi.print((int)(SCREEN_WIDTH-line6.length()) / 2, y+5, line6, CSIColor.DARK_BLUE);
      csi.print((int)(SCREEN_WIDTH-line7.length()) / 2, y+6, line7, CSIColor.PURPLE);
      csi.print((int)(SCREEN_WIDTH-title.length()) / 2, y+23, title, CSIColor.GRAY);
      csi.print((int)(SCREEN_WIDTH-stitle.length()) / 2, y+SCREEN_HEIGHT, stitle, CSIColor.WHITE);
      csi.refresh();
      try {
        Thread.sleep(550);
      } catch (InterruptedException e) {
        //System.out.println(e);
      }
    }
*/
    }

    public static void displayWinner(ConsoleSystemInterface csi, String name) {
      // title
      String line1 = new String("Winner");
      String line2 = new String(name);

      csi.print((int)(SCREEN_WIDTH-line1.length()) / 2, SCREEN_HEIGHT-4, "Winner", CSIColor.DARK_RED);
      csi.print((int)(SCREEN_WIDTH-line2.length()) / 2, SCREEN_HEIGHT-3, name, CSIColor.WHITE);

      csi.refresh();
      }

    public static void displayCharacterCreation(ConsoleSystemInterface csi, int depth, int skillPoints) {

      int y = depth;
      String line1 = new String("What is your Characters name ? ");
      String line2 = new String(String.format("Assign %d Skill points to Agility, Strength & Luck.", skillPoints));
      String line3 = new String("    ...Agility :");
      String line4 = new String("    ...Strength :");
      String line5 = new String("    ...Luck :");

      // character creation menu
      csi.print(0, y++, line1, CSIColor.GRAY);
      csi.print(0 ,y++, line2, CSIColor.GRAY);
      csi.print(0 ,y++, line3, CSIColor.GRAY);
      csi.print(0 ,y++, line4, CSIColor.GRAY);
      csi.print(0 ,y++, line5, CSIColor.GRAY);

      csi.refresh();
    }

    public static void displayGridOutline(ConsoleSystemInterface csi, int numRows, int numCols, ArrayList<Cell> cells) {

      /*****************************************/
      // 2d Array - x is rows y is Collections
      // CSI.print (x - Col, y Row)
      /*****************************************/

      // where do we want to position the grid ?
      int topSide = SPLASH_DEPTH + 2;
      int leftSide = (SCREEN_WIDTH / 2) - (numCols / 2);

      System.out.println(leftSide + " " + topSide);
      // 35 + 7

      /* first - create grid outline */
      for (int r = leftSide-1; r <= leftSide+numCols; r++) {
        //csi.print(r, topSide, "#", CSIColor.BROWN);
        for (int c = topSide-1; c <= topSide+numRows; c++) {
        csi.print(r, c, "#", CSIColor.BROWN);
        }
      }
      csi.refresh();

      /*
            System.out.println(leftSide + (leftSide+numCols));
            for (int r = leftSide; r < leftSide+numCols; r++) {
              for (int c = 1; c < numRows; c++) {
              csi.print(r, topSide+c, " ", CSIColor.BROWN);
              }
            }*/
    }
    public static void displayGrid(ConsoleSystemInterface csi, int numRows, int numCols, int turn, ArrayList<Cell> cells) {

      // where do we want to position the grid ?
      int topSide = SPLASH_DEPTH + 2;
      int leftSide = (SCREEN_WIDTH / 2) - (numCols / 2);

      /*****************************************/
      // 2d Array - x is rows y is Collections
      // CSI.print (x - Col, y Row)
      /*****************************************/


      for (Cell cellElement : cells) {
        if (!cellElement.getIsEmpty()) {
          if (!cellElement.getCreature().getIsBot()) {
            displayUserStats(csi, cellElement.getCreature().getName(), turn, cellElement.getCreature().getHealth(),
            cellElement.getCreature().getAgility(), cellElement.getCreature().getStrength(), cellElement.getCreature().getLuck());
          }
          if (cellElement.getCreature().getIsAlive()) {
            csi.print(cellElement.getYPos()+leftSide, cellElement.getXPos()+topSide, cellElement.getDisplay(), CSIColor.PINK);
          } else {
            csi.print(cellElement.getYPos()+leftSide, cellElement.getXPos()+topSide, "x", CSIColor.GRAY);
          }
        } else {
          csi.print(cellElement.getYPos()+leftSide, cellElement.getXPos()+topSide, ".", CSIColor.GRAY);
        }
      }
      csi.refresh();




      /*for (int r = 0; r < numRows; r++) {

        for (int c = 0; c < numCols; c++) {
          if (!cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getIsEmpty()) {
            if (cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getCreature().getIsAlive()) {
              System.out.printf("|%s|", cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getDisplay());
              csi.print(r, topSide+c, cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getDisplay(), CSIColor.PINK);
            } else {
                System.out.printf("| |"); // DEAD
            }

          } else {
            System.out.printf("|%s|", cells.get(XYCoordinate.xyCoordToList(r,c, numRows)).getDisplay());
          }
        }
      }
      System.out.printf("\n");*/

    }

    public static String askQuestionStr(ConsoleSystemInterface csi, String q, int SCREEN_HEIGHT) {

      //csi.print(0, SCREEN_HEIGHT, q, CSIColor.GRAY);
      csi.locateCaret(q.length(), SCREEN_HEIGHT);

      String answer = csi.input();
      csi.refresh();
      return answer;
    }

    public static int askQuestionInt(ConsoleSystemInterface csi, String q, int SCREEN_HEIGHT) {

      //csi.print(0, SCREEN_HEIGHT, q, CSIColor.GRAY);
      csi.locateCaret(q.length(), SCREEN_HEIGHT);

      String answer = csi.input();
      csi.refresh();
      return Integer.parseInt(answer);
    }

    public static void displayUserStats(ConsoleSystemInterface csi, String name, int turn, int health, int agility, int strength, int luck) {
      String status1 = new String(String.format("Hero   : %s", name));
      String status2 = new String(String.format("Turn   : %d", turn));
      String status3 = new String(String.format("Health : %d", health));
      String status4 = new String(String.format("A %d: S : %d, L : %d", agility, strength, luck));

      // position ... bottom LEFT
      csi.print(5, SCREEN_HEIGHT-10, status4, CSIColor.YELLOW);
      csi.print(5, SCREEN_HEIGHT-11, status3, CSIColor.YELLOW);
      csi.print(5, SCREEN_HEIGHT-12, status2, CSIColor.YELLOW);
      csi.print(5, SCREEN_HEIGHT-13, status1, CSIColor.YELLOW);

    }




}
