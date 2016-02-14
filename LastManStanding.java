import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import net.slashie.libjcsi.textcomponents.MenuBox;
import java.util.Properties;
import java.util.ArrayList;
import java.util.*;

public class LastManStanding {

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
		text.setProperty("font", "Courier New");
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
                  numRows = Integer.parseInt(args[0]);
                  numCols = Integer.parseInt(args[1]);
                  numCreatures = Integer.parseInt(args[2]);
          }


          // we need to create our grid (dungeon) which we took
          // as command line args or use a default
          for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
              // add a blank cell that will be displayed as " "
              cells.add(new Cell(r, c, " "));
            }
          }
          //System.out.println("Elements of ArrayList of Cell Type: " + cells);

          /* test --- add some creatures */
          cells.set(XYCoordinate.xyCoordToList(0,0, numRows), new Cell(0,0, new Goblin("Goblin1", 100, 90, 20, 10, true, "G")));
          cells.set(XYCoordinate.xyCoordToList(0,1, numRows), new Cell(0,1, new Goblin("Goblin2", 100, 30, 70, 10, true, "G")));
          cells.set(XYCoordinate.xyCoordToList(3,8, numRows), new Cell(3,8, new Goblin("Goblin3", 100, 60, 89, 10, true, "G")));
          cells.set(XYCoordinate.xyCoordToList(1,4, numRows), new Cell(1,4, new Human("Human1", 100, 60, 35, 10, true, "H")));
          cells.set(XYCoordinate.xyCoordToList(2,2, numRows), new Cell(2,2, new Zombie("Zombie1", 100, 60, 56, 10, true, "Z")));
          cells.set(XYCoordinate.xyCoordToList(5,2, numRows), new Cell(5,2, new Zombie("Zombie2", 100, 60, 11, 10, true, "Z")));


          /**************/
          /* USER INPUT */
          /**************/
          int selection = -1;
          int level = 1;
          int skillPoints = 100;
          int strength = 0;
          int agility = 0;
          int luck = 0;

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


          csi.print((int)(80-startMessage.length()) / 2, 18, startMessage, CSIColor.BRIGHT_PINK);
          csi.refresh();

          CharKey tempWait = csi.inkey();

          // clear the screen in preperation to display the grid
          csi.cls();
          displaySplash(csi, 0);
          csi.refresh();

          //cells.set(XYCoordinate.xyCoordToList(6,9, numRows), new Cell(6,9, new Human(name, 100, strength, agility, luck, false, "H")));
          cells.set(XYCoordinate.xyCoordToList(6,9, numRows), new Cell(6,9, new Human(name, 100, strength, agility, luck, bot, "@")));


          /*******************/
          /* CORE LOGIC LOOP */
          /*******************/
          XYCoordinate xy = new XYCoordinate();
          int listIndex;
          char c;

          do {
            //System.out.println("CORE START");
            displayGrid(numRows, numCols, cells);

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
                    System.out.printf("\nPlease make your move ['U' 'D' 'L' 'R' 'H'] : ");
                    scanner = new Scanner(System.in);
                    c = Character.toUpperCase(scanner.next(".").charAt(0));

                    //System.out.printf("YOU selected movement of %s\n", c);

                    xy = cellElement.attemptManualMove(charToCharacterMove(c));

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

            System.out.println("\noooooo   oooooo     oooo ooooo ooooo      ooo ooooo      ooo oooooooooooo ooooooooo.   ");
            System.out.println(" `888.    `888.     .8'  `888' `888b.     `8' `888b.     `8' `888'     `8 `888   `Y88. ");
            System.out.println("  `888.   .8888.   .8'    888   8 `88b.    8   8 `88b.    8   888          888   .d88' ");
            System.out.println("   `888  .8'`888. .8'     888   8   `88b.  8   8   `88b.  8   888oooo8     888ooo88P'  ");
            System.out.println("    `888.8'  `888.8'      888   8     `88b.8   8     `88b.8   888    \"     888`88b.    ");
            System.out.println("     `888'    `888'       888   8       `888   8       `888   888       o  888  `88b.  ");
            System.out.println("      `8'      `8'       o888o o8o        `8  o8o        `8  o888ooooood8 o888o  o888o ");

            System.out.printf("\n...in %d Turns.", turnNumber);

            // print off the winner if there was one.
            if (creatureCells.size() > 0) {
              System.out.println(creatureCells.get(0).getCreature().toString());
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
    int y = depth;
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
    csi.print((int)(80-line1.length()) / 2, y, line1, CSIColor.DARK_RED);
    csi.print((int)(80-line2.length()) / 2 ,y+1, line2, CSIColor.RED);
    csi.print((int)(80-line3.length()) / 2, y+2, line3, CSIColor.YELLOW);
    csi.print((int)(80-line4.length()) / 2, y+3, line4, CSIColor.GREEN);
    csi.print((int)(80-line5.length()) / 2, y+4, line5, CSIColor.BLUE);
    csi.print((int)(80-line6.length()) / 2, y+5, line6, CSIColor.DARK_BLUE);
    csi.print((int)(80-line7.length()) / 2, y+6, line7, CSIColor.PURPLE);
    csi.print((int)(80-title.length()) / 2, y+23, title, CSIColor.GRAY);
    csi.print((int)(80-stitle.length()) / 2, y+24, stitle, CSIColor.WHITE);
    csi.refresh();

    CharKey dir = csi.inkey();
    /*for (int i = 0; i < 25; i++) {
      csi.cls();
      y+= i;
      csi.print((int)(80-line1.length()) / 2, y, line1, CSIColor.DARK_RED);
      csi.print((int)(80-line2.length()) / 2 ,y+1, line2, CSIColor.RED);
      csi.print((int)(80-line3.length()) / 2, y+2, line3, CSIColor.YELLOW);
      csi.print((int)(80-line4.length()) / 2, y+3, line4, CSIColor.GREEN);
      csi.print((int)(80-line5.length()) / 2, y+4, line5, CSIColor.BLUE);
      csi.print((int)(80-line6.length()) / 2, y+5, line6, CSIColor.DARK_BLUE);
      csi.print((int)(80-line7.length()) / 2, y+6, line7, CSIColor.PURPLE);
      csi.print((int)(80-title.length()) / 2, y+23, title, CSIColor.GRAY);
      csi.print((int)(80-stitle.length()) / 2, y+24, stitle, CSIColor.WHITE);
      csi.refresh();
      try {
        Thread.sleep(550);
      } catch (InterruptedException e) {
        //System.out.println(e);
      }
    }
*/
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

    public static void displayGrid(ConsoleSystemInterface csi, int numRows, int numCols, ArrayList<Cell> cells) {

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

    public static String askQuestionStr(ConsoleSystemInterface csi, String q, int screenDepth) {

      //csi.print(0, screenDepth, q, CSIColor.GRAY);
      csi.locateCaret(q.length(), screenDepth);

      String answer = csi.input();
      csi.refresh();
      return answer;
    }

    public static int askQuestionInt(ConsoleSystemInterface csi, String q, int screenDepth) {

      //csi.print(0, screenDepth, q, CSIColor.GRAY);
      csi.locateCaret(q.length(), screenDepth);

      String answer = csi.input();
      csi.refresh();
      return Integer.parseInt(answer);
    }


}
