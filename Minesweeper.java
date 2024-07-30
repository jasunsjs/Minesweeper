/** Title: Minesweeper
  * Author: Jason Sun
  * Description: minesweeper game
  * Date: 26 Jan 2022
  * Version 1.0
  */

// Import java packages
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Minesweeper{
    
    // Game Window properties
    static JFrame gameWindow;
    static GraphicsPanel canvas;
    
    // Initialize frame dimensions
    static final int WIDTH = 1200;
    static final int HEIGHT = 1000;
    
    // Declare grid dimensions
    static int gridWidth;
    static int initialGridX;
    static int initialGridY;
    static int finalGridX; 
    static int finalGridY;
    static int rows;
    static int columns;
    
    // Declare mouse event variables
    static int mouseClickX;
    static int mouseClickY;
    static int mouseMotionX;
    static int mouseMotionY;
    
    // Main menu dimensions
    static Color menuColor = Color.green;
    static int newGameX = 390;
    static int newGameY = 344;
    static int easyX = 390;
    static int easyY = 338;
    static int mediumX = 390;
    static int mediumY = 506;
    static int hardX = 390;
    static int hardY = 674;
    static int menuW = 420;
    static int menuH = 126;
    
    // Strings to be drawn
    static String newGameString = "New Game";
    static String easyString = "Easy";
    static String mediumString = "Medium";
    static String hardString = "Hard";
    static String menuString = "Menu";
    static String winString = "BIG W";
    static String loseString = "BIG L";
    
    // Variable for different menu/in-game options
    static int menuOption = 0;
    
    // Fonts
    static int menuFontSize = 72;
    static Font menuFont = new Font("Cambria", Font.BOLD, menuFontSize);
    static int inGameFontSize = 50;
    static Font inGameFont = new Font("Cambria", Font.PLAIN, inGameFontSize);
    static int cellNumberFontSize = 40;
    static Font cellNumberFont = new Font ("Cambria", Font.BOLD, cellNumberFontSize);
    
    // Mouse listeners
    static MyMouseListener mouseListener = new MyMouseListener();
    static MyMouseMotionListener mouseMotionListener = new MyMouseMotionListener();
    
    // Image and picture properties
    static BufferedImage gameBackground;
    static BufferedImage minesweeperCellFlag;
    static BufferedImage minesweeperMineIcon;
    static BufferedImage gameTitle;
    
    // Game properties
    static int neighbourMines = 0;
    static int totalMines = 0;
    static int flaggedMines = 0;
    static boolean win = false;
    static boolean lose = false;
    // Arrays required in game
    static int[][] neighbours = new int[24][20]; // Values representing numbered cells
    static int[][] mines = new int[24][20]; // 0 if no mine, 1 if mine
    static int[][] flag = new int[24][20]; // 0 if flagged uncorrectly, 1 if flagged correctly
    static boolean[][] isFlagged = new boolean[24][20]; // Whether or not cell is flagged
    static boolean[][] isRevealed = new boolean[24][20]; // Whether or not cell is revealed
    
    
    
// main method----------------------------------------------------------------------------------------------------------   
    public static void main(String[] args){
        
        // Game window
        gameWindow = new JFrame("Minesweeper");
        gameWindow.setSize(WIDTH,HEIGHT);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Graphics and mouse listeners
        canvas = new GraphicsPanel();
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseMotionListener);
        gameWindow.add(canvas);
        
        // load pictures from files
        try {                
            gameBackground = ImageIO.read(new File("assets/Minesweeper_Background.png"));
            minesweeperCellFlag = ImageIO.read(new File("assets/Minesweeper_Cell_Flag.png"));
            minesweeperMineIcon = ImageIO.read(new File("assets/Minesweeper_Mine_Icon.png"));
            gameTitle = ImageIO.read(new File("assets/Minesweeper_Title.png"));
        } catch (IOException ex){}
        
        gameWindow.setVisible(true);
        runGameLoop();
        
    } // main method end
    
// runGameLoop method---------------------------------------------------------------------------------------------------
    public static void runGameLoop(){
        while (true) {
            gameWindow.repaint();
            try  {Thread.sleep(20);} catch(Exception e){}
        }
    } // runGameLoop method end
    
// Graphics and drawings------------------------------------------------------------------------------------------------
    static class GraphicsPanel extends JPanel{
        public GraphicsPanel(){
            setFocusable(true);
            requestFocusInWindow();
        }
        
        public void paintComponent(Graphics g){ 
            super.paintComponent(g);
            
            // Draw background picture
            g.drawImage(gameBackground, 0, 0, this);
            
            // Draw game title
            if (menuOption == 0 || menuOption == 1)
                g.drawImage(gameTitle, 85, 90, this);
            
            // Draw game main menu
            g.setColor(menuColor);
            g.setFont(menuFont);
            if (menuOption == 0){
                g.fillRect(newGameX, newGameY, menuW, menuH);
                g.setColor(Color.black);
                g.drawString(newGameString, newGameX + 37, newGameY + 87);
            }
            
            // Draw difficulty options
            else if (menuOption == 1){
                g.fillRect(easyX, easyY, menuW, menuH);
                g.fillRect(mediumX, mediumY, menuW, menuH);
                g.fillRect(hardX, hardY, menuW, menuH);
                // Draw difficulty strings
                g.setColor(Color.black);
                g.drawString(easyString, easyX + 37, easyY + 87);
                g.drawString(mediumString, mediumX + 37, mediumY + 87);
                g.drawString(hardString, hardX + 37, hardY + 87);
            } // menuOption 1 end
            
            // Draw grid
            else if (menuOption == 2){
                g.setColor(Color.black);
                for (int x = initialGridX; x <= finalGridX; x += gridWidth)
                    g.drawLine(x, initialGridY, x, finalGridY);
                for (int y = initialGridY; y <= finalGridY; y += gridWidth)
                    g.drawLine(initialGridX, y, finalGridX, y);
                
                // Draw in return to menu option
                g.setColor(menuColor);
                g.fillRect(710, finalGridY + gridWidth/2, 190, 80);
                g.setColor(Color.black);
                g.setFont(inGameFont);
                g.drawString(menuString, 745, finalGridY + 80);
                
                // Display total number of mines
                g.drawImage(minesweeperMineIcon, 310, finalGridY + gridWidth/2, this);
                g.setColor(Color.black);
                g.drawString(String.valueOf(totalMines), 410, finalGridY + gridWidth/2 + 57);
                
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        // If mouse left clicks to reveal cell
                        if (isRevealed[i][j] == true){
                            g.setColor(Color.white);
                            g.fillRect(initialGridX+1 + i*gridWidth, initialGridY+1 + j*gridWidth, gridWidth-1, gridWidth-1);
                            // If revealed cell does not contain mine and is not 0
                            if (mines[i][j] == 0 && neighbours[i][j] != 0){
                                g.fillRect(initialGridX+1 + i*gridWidth, initialGridY+1 + j*gridWidth, gridWidth-1, gridWidth-1);
                                g.setColor(Color.black);
                                g.setFont(cellNumberFont);
                                g.drawString(String.valueOf(neighbours[i][j]), initialGridX+1 + i*gridWidth + 9, initialGridY+1 + j*gridWidth + 34);
                            }
                            // If revealed cell is mine cell
                            else if (mines[i][j] == 1){
                                g.setColor(Color.red);
                                g.fillRect(initialGridX+1 + i*gridWidth, initialGridY+1 + j*gridWidth, gridWidth-1, gridWidth-1);
                                g.setColor(Color.black);
                                g.fillOval(initialGridX+1 + i*gridWidth + 5, initialGridY+1 + j*gridWidth + 5, gridWidth-11, gridWidth-11);
                            }
                        }
                        
                        // If mouse right clicks to flag
                        else if (isFlagged[i][j] == true && lose == false)
                            g.drawImage(minesweeperCellFlag, initialGridX+1 + i*gridWidth, initialGridY+1 + j*gridWidth, this);
                        
                        // If mouse hovers over cell
                        else if (mouseMotionX > (initialGridX + i*gridWidth) && mouseMotionX < (initialGridX + i*gridWidth + gridWidth)
                                     && mouseMotionY > (initialGridY + j*gridWidth) && mouseMotionY < (initialGridY + j*gridWidth + gridWidth)){
                            g.setColor(Color.lightGray);
                            g.fillRect(initialGridX+1 + i*gridWidth, initialGridY+1 + j*gridWidth, gridWidth-1, gridWidth-1);
                        }
                    } // For loop end (columns)
                } // For loop end (rows)
            } // menuOption 2 end
            
            g.setFont(inGameFont);
            g.setColor(Color.black); 
            // If user loses game
            if (lose == true && win == false){
                g.drawString(loseString, 545, finalGridY + gridWidth + gridWidth/2);
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        if (mines[i][j] == 1) // Only reveal mines
                            isRevealed[i][j] = true;
                    }
                }
            }
            
            // If user wins game
            else if (win == true)
                g.drawString(winString, 530, finalGridY + gridWidth + gridWidth/2);
            
        } // paintComponent method end
    } // GraphicsPanel class end
    
// MyMouseListener class------------------------------------------------------------------------------------------------
    static class MyMouseListener implements MouseListener{
        public void mouseClicked(MouseEvent e){
            mouseClickX = e.getX();
            mouseClickY = e.getY();
            int buttonClicked = e.getButton(); // Determine whether right or left click
            
            // If click on New Game------------------------------------------------------------------------
            if (mouseClickX > newGameX && mouseClickX < (newGameX + menuW) && mouseClickY > newGameY 
                    && mouseClickY < (newGameY + menuH) && menuOption == 0){
                menuOption = 1;
                System.out.println("New Game!");
            } // Click on New Game end
            
            // Easy difficulty-----------------------------------------------------------------------------
            else if (mouseClickX > easyX && mouseClickX < (easyX + menuW) && mouseClickY > easyY 
                         && mouseClickY < (easyY + menuH) && menuOption == 1){
                // Grid dimensions for easy mode
                initialGridX = 226;
                initialGridY = 164;
                finalGridX = 974;
                finalGridY = 736;
                gridWidth = 44;
                rows = 17;
                columns = 13;
                menuOption = 2;
                
                // Generate mines in the field
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        int randomNum = (int)(100 * Math.random() + 1); // Random integer from 1 to 100
                        if (randomNum <= 15){ // Only approximately 15% of the cells are mines
                            mines[i][j] = 1; // Contains mine
                            totalMines++;
                        }
                        else
                            mines[i][j] = 0; // Does not contain mine
                        isRevealed[i][j] = false;
                    } // For loop end (column)
                } // For loop end (row)
                
                // Determine neighbouring mines of a cell
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        neighbourMines = 0; // Reset neighbour mines to 0
                        for (int m = 0; m < rows; m++){
                            for (int n = 0; n < columns; n++){
                                if (isNeighbour(i, j, m, n) == true)
                                    neighbourMines++;
                            }
                        }
                        neighbours[i][j] = neighbourMines;
                    } // For loop end (column)
                } // For loop end (row)
                
                // Set initial flag to zero
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        flag[i][j] = 0;
                    }
                }
            } // Easy difficulty end
            
            // Medium difficulty---------------------------------------------------------------------------
            else if (mouseClickX > mediumX && mouseClickX < (mediumX + menuW) && mouseClickY > mediumY 
                         && mouseClickY < (mediumY + menuH) && menuOption == 1){
                // Grid dimensions for medium mode
                initialGridX = 180;
                initialGridY = 114;
                finalGridX = 1020;
                finalGridY = 786;
                gridWidth = 42;
                rows = 20;
                columns = 16;
                menuOption = 2;
                
                // Generate mines in the field
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        int randomNum = (int)(100 * Math.random() + 1); // Random integer from 1 to 100
                        if (randomNum <= 15){ // Only approximately 15% of the cells are mines
                            mines[i][j] = 1; // Contains mine
                            totalMines++;
                        }
                        else
                            mines[i][j] = 0; // Does not contain mine
                        isRevealed[i][j] = false;
                    } // For loop end (column)
                } // For loop end (row)
                
                // Determine neighbouring mines of a cell
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        neighbourMines = 0; // Reset neighbour mines to 0
                        for (int m = 0; m < rows; m++){
                            for (int n = 0; n < columns; n++){
                                if (isNeighbour(i, j, m, n) == true)
                                    neighbourMines++;
                            }
                        }
                        neighbours[i][j] = neighbourMines;
                    } // For loop end (column)
                } // For loop end (row)
                
                // Set initial flag to zero
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        flag[i][j] = 0;
                    }
                }
            } // Medium difficulty end
            
            // Hard difficulty-----------------------------------------------------------------------------
            else if (mouseClickX > hardX && mouseClickX < (hardX + menuW) && mouseClickY > hardY 
                         && mouseClickY < (hardY + menuH) && menuOption == 1){
                // Grid dimensions for hard mode
                initialGridX = 120;
                initialGridY = 50;
                finalGridX = 1080;
                finalGridY = 850;
                gridWidth = 40;
                rows = 24;
                columns = 20;
                menuOption = 2;
                
                // Generate mines in the field
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        int randomNum = (int)(100 * Math.random() + 1); // Random integer from 1 to 100
                        if (randomNum <= 15){ // Only approximately 15% of the cells are mines
                            mines[i][j] = 1; // Contains mine
                            totalMines++;
                        }
                        else
                            mines[i][j] = 0; // Does not contain mine
                        isRevealed[i][j] = false;
                    } // For loop end (column)
                } // For loop end (row)
                
                // Determine neighbouring mines of a cell
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        neighbourMines = 0; // Reset neighbour mines to 0
                        for (int m = 0; m < rows; m++){
                            for (int n = 0; n < columns; n++){
                                if (isNeighbour(i, j, m, n) == true)
                                    neighbourMines++;
                            }
                        }
                        neighbours[i][j] = neighbourMines;
                    } // For loop end (column)
                } // For loop end (row)
                
                // Set initial flag to zero
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        flag[i][j] = 0;
                    }
                }
            } // Hard difficulty end
            
            // Return game to main menu--------------------------------------------------------------------
            else if (mouseClickX > 710 && mouseClickX < 900 && mouseClickY > (finalGridY + gridWidth/2) 
                         && mouseClickY < (finalGridY + gridWidth/2 + 80) && menuOption == 2){
                menuOption = 1;
                // Reset variables
                lose = false;
                win = false;
                totalMines = 0;
                flaggedMines = 0;
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        isFlagged[i][j] = false;
                    }
                }
            } // Return to main menu end
            
            // For left mouse button-----------------------------------------------------------------------
            else if (buttonClicked == 1){
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        if (mouseClickX > (initialGridX + i*gridWidth) && mouseClickX < (initialGridX + i*gridWidth + gridWidth) 
                                && mouseClickY > (initialGridY + j*gridWidth) && mouseClickY < (initialGridY + j*gridWidth + gridWidth)){
                            isRevealed[i][j] = true;
                            // If left clicks on mine
                            if (mines[i][j] == 1){
                                lose = true;
                            }
                        }
                    } // For loop end (column)
                } // For loop end (row)
            } // Left click end
            
            // For right mouse button----------------------------------------------------------------------
            else if (buttonClicked == 3){
                for (int i = 0; i < rows; i++){
                    for (int j = 0; j < columns; j++){
                        if (mouseClickX > (initialGridX + i*gridWidth) && mouseClickX < (initialGridX + i*gridWidth + gridWidth) 
                                && mouseClickY > (initialGridY + j*gridWidth) && mouseClickY < (initialGridY + j*gridWidth + gridWidth)){
                            isFlagged[i][j] = true;
                            // If flagged cell contains mine
                            if (mines[i][j] == 1){
                                // Check if mine cell already flagged
                                if (flag[i][j] == 0){
                                    flag[i][j] = 1;
                                    flaggedMines++;
                                    // Win condition
                                    if (flaggedMines == totalMines && lose == false){
                                        win = true;
                                    }
                                }
                            }
                            else
                                flag[i][j] = 0;
                        }
                    } // For loop end (column)
                } // For loop end (row) 
            } // Right click if statement end
            
        } // mouseClicked method end
        
        public void mousePressed(MouseEvent e){   // MUST be implemented even if not used!
        }
        public void mouseReleased(MouseEvent e){  // MUST be implemented even if not used!
        }
        public void mouseEntered(MouseEvent e){   // MUST be implemented even if not used!
        }
        public void mouseExited(MouseEvent e){    // MUST be implemented even if not used!
        }
    } // MyMouseListener class end
    
//----------------------------------------------------------------------------------------------------------------------
    static class MyMouseMotionListener implements MouseMotionListener{
        public void mouseMoved(MouseEvent e){
            mouseMotionX = e.getX();
            mouseMotionY = e.getY();
        } // mouseMoved method end
        
        public void mouseDragged(MouseEvent e){
        }
    } // MyMouseMotionListener class end
    
// isNeighbour method---------------------------------------------------------------------------------------------------
    public static boolean isNeighbour(int mX, int mY, int nX, int nY){ // Determines if neighbour cell contains mine
        if ((mX-nX) < 2 && (mX-nX) > -2 && (mY-nY) < 2 && (mY-nY) > -2 && mines[nX][nY] == 1)
            return true;
        return false;
    } // isNeighbour method end
    
    
} // Minesweeper class end
