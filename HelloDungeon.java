import net.slashie.libjcsi.CSIColor;
import net.slashie.libjcsi.CharKey;
import net.slashie.libjcsi.ConsoleSystemInterface;
import net.slashie.libjcsi.wswing.WSwingConsoleInterface;
import java.util.Properties;

public class HelloDungeon{

	public static void main(String[] args){

		Properties text = new Properties();
		text.setProperty("fontSize","12");
		text.setProperty("font", "Courier");
		ConsoleSystemInterface csi = null;
		try{
			csi = new WSwingConsoleInterface("My little Java Roguelike - Programming is fun", text);
		}
		catch (ExceptionInInitializerError eiie) {
			System.out.println("*** Error: Swing Console Box cannot be initialized. Exiting...");
			eiie.printStackTrace();
			System.exit(-1);
		}
		int x = 0;
		int y = 0;
		boolean stop = false;
		while(!stop){
			csi.cls();
			//csi.print(x,y, '@', CSIColor.WHITE);
			//csi.print(x,y, 'H', CSIColor.CYAN);
			csi.print(x,y, "HELP", CSIColor.CYAN);
			csi.print(x+1,y+1, "HELP", CSIColor.CYAN);

			CharKey dir = csi.inkey();
			if(dir.isUpArrow()&& (y-1 >= 0)){
				y--;
			}
			if(dir.isDownArrow() && (y+1 < 25)){
				y++;
			}
			if(dir.isLeftArrow() && (x-1 >= 0)){
				x--;
			}
			if(dir.isRightArrow() && (x+1 < 80)){
				x++;
			}
			if(dir.code == CharKey.Q){
				stop = true;
			}
//csi.cls();
        csi.print(1, 1, "Hello, Hello!", CSIColor.CYAN);
        csi.print(2, 3, "This is printed using the Java Console System Interface lib. (libjcsi)");
        csi.print(2, 4, "Swing Console Box Implementation", ConsoleSystemInterface.RED);

        csi.print(5, 6, "########", ConsoleSystemInterface.GRAY);
        csi.print(5, 7, "#......#", ConsoleSystemInterface.GRAY);
        csi.print(5, 8, "#......#", ConsoleSystemInterface.GRAY);
        csi.print(5, 9, "####/###", ConsoleSystemInterface.GRAY);

        csi.print(6, 7, "......", ConsoleSystemInterface.BLUE);
        csi.print(6, 8, "......", ConsoleSystemInterface.BLUE);

        csi.print(9, 9, "/", ConsoleSystemInterface.BROWN);

        csi.print(8, 8, "@", ConsoleSystemInterface.RED);
				csi.refresh();
		}
		System.exit(0);
	}
 }
