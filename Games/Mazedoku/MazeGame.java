import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.Map;
import java.io.FileWriter;
/**
 * Maze Game
 *
 * INFO1113 Assignment 1
 * 2018 Semester 2
 *
 * The Maze Game.
 * In this assignment you will be designing a maze game.
 * You will have a maze board and a player moving around the board.
 * The player can step left, right, up or down.
 * However, you need to complete the maze within a given number of steps.
 *
 * As in any maze, there are walls that you cannot move through. If you try to
 * move through a wall, you lose a life. You have a limited number of lives.
 * There is also gold on the board that you can collect if you move ontop of it.
 *
 * Please implement the methods provided, as some of the marks are allocated to
 * testing these methods directly.
 *
 * @author YOU :)
 * @date 23 August 2018
 *
 */
public class MazeGame {
    /* You can put variables that you need throughout the class up here.
     * You MUST INITIALISE ALL of these class variables in your initialiseGame
     * method.
     */

    // A sample variable to show you can put variables here.
    // You would initialise it in initialiseGame method.
    // e.g. Have the following line in the initialiseGame method.
    // sampleVariable = 1;
     static int no_of_lives;
	static int no_of_steps;
	static int am_of_gold;
	static int no_of_rows;
	static String[][] board;
	static int x_position;
	static int y_position;
    /**
     * Initialises the game from the given configuration file.
     * This includes the number of lives, the number of steps, the starting gold
     * and the board.
     *
     * If the configuration file name is "DEFAULT", load the default
     * game configuration.
     *
     * NOTE: Please also initialise all of your class variables.
     *
     * @args configFileName The name of the game configuration file to read from.
     * @throws IOException If there was an error reading in the game.
     *         For example, if the input file could not be found.
     */
    public static void initialiseGame(String configFileName) throws IOException {
        // TODO: Implement this method.
		if(configFileName.equals("DEFAULT")){
			no_of_lives=3;
			no_of_steps=20;
			am_of_gold=0;
			no_of_rows=4;
			board=new String[no_of_rows][10];
			String[] line1= {"#","@"," ","#","#"," "," ","&","4","#"};
			String[] line2= {"#","#"," "," ","#"," ","#","#"," ","#"};
			String[] line3= {"#","#","#"," "," ","3","#"," "," "," "};
			String[] line4= {"#","#","#","#","#","#","#"," "," ","#"};
			for(int i=0;i<10;i++){
				board[0][i]=line1[i];
				board[1][i]=line2[i];
				board[2][i]=line3[i];
				board[3][i]=line4[i];
			}
			x_position=7;
			y_position=0;
		}else{
		File mazedoku=new File(configFileName);
		Scanner content=new Scanner(mazedoku);
		String line1=content.nextLine();
		String[] split=line1.split(" ");
		no_of_lives=Integer.parseInt(split[0]);
		no_of_steps=Integer.parseInt(split[1]);
		am_of_gold=Integer.parseInt(split[2]);
		no_of_rows=Integer.parseInt(split[3]);
		int j=0;
		while(j<no_of_rows){
			String forwardline=content.nextLine();
			if(j==0){
				board= new String[no_of_rows][forwardline.length()];
			}
			for(int i=0;i<forwardline.length();i++){
				board[j][i]=Character.toString(forwardline.charAt(i));
			}
		j=j+1;
		}
		for(int i=0;i<no_of_rows;i++){
			for(int k=0;k<board[0].length;k++){
				if(board[i][k].equals("&")){
					x_position=k;
					y_position=i;
				}
			}
		}
		}
		
	}
				
				

    /**
     * Save the current board to the given file name.
     * Note: save it in the same format as you read it in.
     * That is:
     *
     * <number of lives> <number of steps> <amount of gold> <number of rows on the board>
     * <BOARD>
     *
     * @args toFileName The name of the file to save the game configuration to.
     * @throws IOException If there was an error writing the game to the file.
     */
    public static void saveGame(String toFileName) throws IOException {
        // TODO: Implement this method.
		File savedFile = new File(toFileName);
		PrintWriter writer=new PrintWriter(savedFile);
		writer.printf("%d %d %d %d\n",no_of_lives,no_of_steps,am_of_gold,no_of_rows);
		for(int i=0;i<board.length;i++){
			char[] line=new char[board[0].length];
		for(int j=0;j<board[0].length;j++){
			line[j]=board[i][j].charAt(0);
			}
		writer.println(line);
	}
	writer.close();
    }

    /**
     * Gets the current x position of the player.
     *
     * @return The players current x position.
     */
    public static int getCurrentXPosition() {
        // TODO: Implement this method.
        return x_position;
    }

    /**
     * Gets the current y position of the player.
     *
     * @return The players current y position.
     */
    public static int getCurrentYPosition() {
        // TODO: Implement this method.
        return y_position;
    }

    /**
     * Gets the number of lives the player currently has.
     *
     * @return The number of lives the player currently has.
     */
    public static int numberOfLives() {
        // TODO: Implement this method.
		return no_of_lives;
    }

    /**
     * Gets the number of remaining steps that the player can use.
     *
     * @return The number of steps remaining in the game.
     */
    public static int numberOfStepsRemaining() {
        // TODO: Implement this method.
        return no_of_steps;
    }

    /**
     * Gets the amount of gold that the player has collected so far.
     *
     * @return The amount of gold the player has collected so far.
     */
    public static int amountOfGold() {
        // TODO: Implement this method.
        return am_of_gold;
    }


    /**
     * Checks to see if the player has completed the maze.
     * The player has completed the maze if they have reached the destination.
     *
     * @return True if the player has completed the maze.
     */
    public static boolean isMazeCompleted() {
        // TODO: Implement this method.
		Boolean iscompleted=false;
		for(int i=0;i<no_of_rows;i++){
			for(int j=0;j<board[i].length;j++){
				if(board[y_position][x_position].equals("@")){
					iscompleted=true;
				}
				}
			}
		return iscompleted;
		}

    /**
     * Checks to see if it is the end of the game.
     * It is the end of the game if one of the following conditions is true:
     *  - There are no remaining steps.
     *  - The player has no lives.
     *  - The player has completed the maze.
     *
     * @return True if any one of the conditions that end the game is true.
     */
    public static boolean isGameEnd() {
        // TODO: Implement this method.
		Boolean GameEnd=false;
		if(no_of_steps<=0){
			GameEnd=true;
		}else if(no_of_lives<=0){
			GameEnd=true;
		}else if(MazeGame.isMazeCompleted()){
			GameEnd=true;
		}
        return GameEnd;
    }

    /**
     * Checks if the coordinates (x, y) are valid.
     * That is, if they are on the board.
     *
     * @args x The x coordinate.
     * @args y The y coordinate.
     * @return True if the given coordinates are valid (on the board),
     *         otherwise, false (the coordinates are out of range).
     */
    public static boolean isValidCoordinates(int x, int y) {
        // TODO: Implement this method.
		Boolean isvalid=true;
		if((y>=board.length)||(x>=board[0].length)||x<0||y<0){
			isvalid=false;
		}
        return isvalid;
    }

    /**
     * Checks if a move to the given coordinates is valid.
     * A move is invalid if:
     *  - It is move to a coordinate off the board.
     *  - There is a wall at that coordinate.
     *  - The game is ended.
     *
     * @args x The x coordinate to move to.
     * @args y The y coordinate to move to.
     * @return True if the move is valid, otherwise false.
     */
    public static boolean canMoveTo(int x, int y) {
        // TODO: Implement this method.
		boolean canmove=false;
if(MazeGame.isGameEnd()!=true&&MazeGame.isValidCoordinates(x,y)&&board[y][x].equals("#")!=true){
				canmove=true;
			}	
        return canmove;
    }

    /**
     * Move the player to the given coordinates on the board.
     * After a successful move, it prints "Moved to (x, y)."
     * where (x, y) were the coordinates given.
     *
     * If there was gold at the position the player moved to,
     * the gold should be collected and the message "Plus n gold."
     * should also be printed, where n is the amount of gold collected.
     *
     * If it is an invalid move, a life is lost.
     * The method prints: "Invalid move. One life lost."
     *
     * @args x The x coordinate to move to.
     * @args y The y coordinate to move to.
     */
    public static void moveTo(int x, int y) {
        // TODO: Implement this method.
		if(MazeGame.canMoveTo(x,y)){
				System.out.printf("Moved to (%d, %d).\n",x,y);
			if(board[y][x].equals("@")){
				no_of_steps=no_of_steps-1;
				x_position=x;
				y_position=y;
				}else{
					if(Character.isDigit(board[y][x].charAt(0))){
					int gold=Integer.parseInt(board[y][x]);
					System.out.printf("Plus %d gold.\n", gold);
					am_of_gold=am_of_gold+gold;
				}
				board[y_position][x_position]=".";
				board[y][x]="&";
				x_position=x;
				y_position=y;
				no_of_steps=no_of_steps-1;
				}
		}else{
			System.out.println("Invalid move. One life lost.");
			no_of_lives=no_of_lives-1;
			no_of_steps=no_of_steps-1;
		}
    }


    /**
     * Prints out the help message.
     */
    public static void printHelp(){
        // TODO: Implement this method.
		System.out.println("Usage: You can type one of the following commands.");
		System.out.println("help         Print this help message.");
		System.out.println("board        Print the current board.");
		System.out.println("status       Print the current status.");
		System.out.println("left         Move the player 1 square to the left.");	
		System.out.println("right        Move the player 1 square to the right.");
		System.out.println("up           Move the player 1 square up.");
		System.out.println("down         Move the player 1 square down.");
		System.out.println("save <file>  Save the current game configuration to the given file.");
    }

    /**
     * Prints out the status message.
     */
    public static void printStatus() {
        // TODO: Implement this method.
		System.out.printf("Number of live(s): %d\n",no_of_lives);
		System.out.printf("Number of step(s) remaining: %d\n",no_of_steps);
		System.out.printf("Amount of gold: %d\n",am_of_gold);
		
		
    }

    /**
     * Prints out the board.
     */
    public static void printBoard() {
        // TODO: Implement this method.
		for(int i=0;i<no_of_rows;i++){
			for(int j=0;j<board[i].length;j++){
				System.out.print(board[i][j]);
			}
			System.out.println();
		}
		
		
		
		
		
    }

    /**
     * Performs the given action by calling the appropriate helper methods.
     * [For example, calling the printHelp() method if the action is "help".]
     *
     * The valid actions are "help", "board", "status", "left", "right",
     * "up", "down", and "save".
     * [Note: The actions are case insensitive.]
     * If it is not a valid action, an IllegalArgumentException should be thrown.
     *
     * @args action The action we are performing.
     * @throws IllegalArgumentException If the action given isn't one of the
     *         allowed actions.
     */
    public static void performAction(String action) throws IllegalArgumentException {
        // TODO: Implement this method.
		String true_action=action.toLowerCase();
		String[] strlist=action.split(" ");
		if(true_action.equals("help")){
			MazeGame.printHelp();
		}else if(true_action.equals("board")){
			MazeGame.printBoard();
		}else if(true_action.equals("status")){
			MazeGame.printStatus();
		}else if(true_action.equals("left")){
			MazeGame.moveTo(x_position-1,y_position);
		}else if(true_action.equals("right")){
			MazeGame.moveTo(x_position+1,y_position);
		}else if(true_action.equals("up")){
			MazeGame.moveTo(x_position,y_position-1);
		}else if(true_action.equals("down")){
			MazeGame.moveTo(x_position,y_position+1);
		}else if(true_action.startsWith("save")&&strlist.length==2){
			String[] command=action.split(" ");
			if(command.length==2){
			try{
				MazeGame.saveGame(command[1]);
			}catch(IOException e){
				System.out.printf("Error: Could not save the current game configuration to '%s'.\n", command[1]);
			}
		}
		}else{
			throw new IllegalArgumentException();
			}
	}
    /**
     * The main method of your program.
     *
     * @args args[0] The game configuration file from which to initialise the
     *       maze game. If it is DEFAULT, load the default configuration.
     */
    public static void main(String[] args) {
        // Run your program (reading in from args etc) from here.
		if(args.length<1){
			System.out.println("Error: Too few arguments given. Expected 1 argument, found 0.");
			System.out.println("Usage: MazeGame [<game configuration file>|DEFAULT]");
		}else if(args.length>1){
			System.out.printf("Error: Too many arguments given. Expected 1 argument, found %d.\n",args.length);
			System.out.println("Usage: MazeGame [<game configuration file>|DEFAULT]");
		}else{
			try{
		MazeGame maze=new MazeGame();
		maze.initialiseGame(args[0]);
		   }catch(IOException e){
			System.out.printf("Error: Could not load the game configuration from '%s'.\n", args[0]);
			}
		Scanner scan=new Scanner(System.in);
		while(scan.hasNextLine()){
			String scanned=scan.nextLine();
			try{
				if(scanned.equals("")){
					continue;
				}
				MazeGame.performAction(scanned);
			}catch(IllegalArgumentException e){
			System.out.printf("Error: Could not find command '%s'.\n", scanned);
			System.out.println("To find the list of valid commands, please type 'help'.");
			}
		if(MazeGame.isGameEnd()){
				if(MazeGame.isMazeCompleted()){
					System.out.println("Congratulations! You completed the maze!");
					System.out.println("Your final status is:");
					MazeGame.printStatus();
				}else if(MazeGame.no_of_lives==0&&MazeGame.no_of_steps==0){
					System.out.println("Oh no! You have no lives and no steps left.");
					System.out.println("Better luck next time!");
				}else if(MazeGame.no_of_lives==0){
					System.out.println("Oh no! You have no lives left.");
					System.out.println("Better luck next time!");
				}else if(MazeGame.no_of_steps==0){
					System.out.println("Oh no! You have no steps left.");
					System.out.println("Better luck next time!");
				}
		}
			
		}
		if(MazeGame.isGameEnd()!=true){
			System.out.println("You did not complete the game.");
			}
		}
	}
	}
