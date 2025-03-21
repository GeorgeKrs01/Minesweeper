import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Minesweeper {
    private class MineTile extends JButton {
        int r, c;

        public MineTile(int r, int c){
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JButton resetButton = new JButton("Reset");

    int mineCount = 10;
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0;
    boolean gameOver = false;


    Minesweeper(){
//        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper");
        textLabel.setOpaque(true);

        resetButton.setFocusable(false);
        resetButton.addActionListener(e -> resetGame());

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        textPanel.add(resetButton, BorderLayout.EAST);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols)); // 8 x 8
//        boardPanel.setBackground(Color.GREEN);
        frame.add(boardPanel);

        for (int r = 0; r < numRows; r++){
            for (int c = 0; c < numCols; c++){
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0,0,0,0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
//                tile.setText("1");
//                tile.setText("💣");
//                tile.setText("🚩");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        super.mousePressed(e);
                        MineTile tile = (MineTile) e.getSource();

                        //left click
                        if (e.getButton() == MouseEvent.BUTTON1){
                            if (tile.getText() == "") {
                                if(mineList.contains(tile)) {
                                    revealMines();
                                }
                                else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        //right click
                        else if (e.getButton() == MouseEvent.BUTTON3){
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("\uD83D\uDEA9");
                            }
                            else if (tile.getText() == "\uD83D\uDEA9"){
                                tile.setText("");
                            }
                        }
                    }
                });
                boardPanel.add(tile);
            }
        }

        frame.setVisible(true); //moved it in the end, to first load all the buttons

        setMines();
    }

    void resetGame() {
        gameOver = false;
        tilesClicked = 0;
        textLabel.setText("Minesweeper");

        boardPanel.removeAll(); // Clear old board
        boardPanel.revalidate();
        boardPanel.repaint();

        createBoard(); // Recreate board
    }

    void createBoard() {
        board = new MineTile[numRows][numCols]; // Reset board array
        mineList = new ArrayList<>();

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;
                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));

                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) return;
                        MineTile tile = (MineTile) e.getSource();
                        if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
                            if (tile.getText().equals("")) {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
                            if (tile.getText().equals("") && tile.isEnabled()) {
                                tile.setText("🚩");
                            } else if (tile.getText().equals("🚩")) {
                                tile.setText("");
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

        setMines(); // Reinitialize mines
    }


    void setMines() {
        mineList = new ArrayList<MineTile>();

//        mineList.add(board[2][2]);
//        mineList.add(board[2][3]);
//        mineList.add(board[5][6]);
//        mineList.add(board[3][4]);
//        mineList.add(board[1][1]);
        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows); // 0 - 7
            int c = random.nextInt(numCols); // 0 - 7

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        textLabel.setText("Game Over!");
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }


        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        //top 3
        minesFound += countMine(r-1, c-1);  //top left
        minesFound += countMine(r-1, c);       //top
        minesFound += countMine(r-1, c+1);   //top right

        //left and right
        minesFound += countMine(r, c-1);        //left
        minesFound += countMine(r, c+1);        //right

        //bottom 3
        minesFound += countMine(r+1, c-1);  //bottom left
        minesFound += countMine(r+1, c);        //bottom
        minesFound += countMine(r+1, c+1);  //bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        }
        else {
            tile.setText("");

            //top 3
            checkMine(r-1, c-1); //top left
            checkMine(r-1, c);      //top
            checkMine(r-1, c+1); //top right

            //left and right
            checkMine(r, c-1);  //left
            checkMine(r, c+1);  //right

            //bot 3
            checkMine(r+1, c-1);    //bot left
            checkMine(r+1, c);          //bot
            checkMine(r+1, c+1);    //bot right
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            textLabel.setText("Mines Cleared!");
        }
    }

    int countMine(int r, int c) {
        for (MineTile mine : mineList) {
            if (mine.r == r && mine.c == c) {
                return 1;
            }
        }
        return 0;
    }

}
