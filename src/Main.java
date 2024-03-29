import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import java.util.List;


public class Main {
    private JPanel panelMain;
    private JPanel panelFirst;
    private JPanel panelSecond;

    private JPanel panelRanking;

    private JPanel panelcollectedObjects;
    JTextField textFieldName;

    private ArrayList<String> scoresArrayList;

    private JButton buttonEnterGame;

    private JButton buttonRanking;
    private JPanel panelTop;
    private Timer timer_enemy;
    private Timer timerRefresh;
    private Timer timer_checkHearts;
    private JLabel selectChar;

    private JLabel sword;
    private JLabel coin;
    private JLabel potion;
    private ArrayList<JLabel> hearts;
    private JLabel monster;

    private ArrayList<Skeleton> skeletons;

    private int characterOption;
    private JPanel playerName;
    private JPanel panelGold;
    private JButton enterName;
    private JLabel title;
    private JLabel arrow_left;
    private JLabel rankingText;
    private JLabel arrow_right;
    private JLabel mitra;

    private ArrayList<Integer> directions;

    private ArrayList<JLabel> enemies;
    private JLabel goldCountTxt;


    protected ArrayList<String> arrayCollectedObjects;
    protected Character player1;

    private Skeleton skeleton;
    String name;

    int c;

    public Main() {
        arrayCollectedObjects = new ArrayList<>();
        directions = new ArrayList<>();
        skeleton = new Skeleton("down");
        // Wall and floor textures

        // buttons
        buttonEnterGame = new JButton();
        buttonEnterGame.setText("Enter game");
        buttonEnterGame.setLayout(null);
        buttonEnterGame.setSize(new Dimension(100, 35));
        buttonEnterGame.addActionListener(new start());

        buttonRanking = new JButton();
        buttonRanking.setText("Ranking");
        buttonRanking.setLayout(null);
        buttonRanking.setSize(new Dimension(100, 35));
        buttonRanking.addActionListener(new viewRanking());
        // Gold label
        goldCountTxt = new JLabel();
        // panels
        mitra = new JLabel();
        potion = new JLabel();
        sword = new JLabel();
        rankingText = new JLabel();
        hearts = new ArrayList<>();

        panelMain = getPanelMain();
        panelFirst = getPanel();
        panelRanking = getPanel();
        panelSecond = getPanel();
        panelTop = getPanelTop();
        panelcollectedObjects = getPanelGridLayout(100, 30, 3);
        playerName = getPanelGridLayout(200, 20, 2);
        panelFirst.setFocusable(true);
        buttonEnterGame.setLocation(panelFirst.getWidth() / 2 - 120, panelFirst.getWidth() / 2);
        panelGold = getPanelGridLayout(200, 20, 2);

        panelMain.add(panelFirst);
        panelMain.addKeyListener(new panelMainListener());

        arrow_left = GameVisuals.getArrowLeft(false);
        arrow_right = GameVisuals.getArrowRight(false);
        arrow_left.setLocation(panelFirst.getWidth() / 2 - 75, panelFirst.getHeight() / 2);
        arrow_right.setLocation(panelFirst.getWidth() / 2, panelFirst.getHeight() / 2);
        panelFirst.add(arrow_left);
        panelFirst.add(arrow_right);


        panelGold.setBackground(Color.WHITE);
        panelGold.setVisible(true);

        panelcollectedObjects.setBackground(Color.WHITE);

        panelTop.add(panelGold, 0);

        //get name

        panelFirst.add(playerName);

        textFieldName = new JTextField(10);
        textFieldName.setBounds(50, 50, 150, 30);
        textFieldName.setText("Player1");
        playerName.add(textFieldName, 0);
        enterName = new JButton();
        enterName.setSize(40, 20);
        enterName.setText("Enter");
        playerName.setLocation(panelMain.getWidth() / 2 + 250, panelMain.getWidth() / 2 + 290);
        enterName.addActionListener(new nameEntered());

        playerName.add(enterName);
        enterName.setFocusable(true);
        //Visuals


        GameVisuals.placeTiles(panelFirst);
        GameVisuals.placeTiles(panelSecond);
        GameVisuals.placeWall(panelSecond, panelTop);
        buttonRanking.setLocation(panelFirst.getWidth() / 2 + 30, panelFirst.getWidth() / 2);
        panelFirst.add(buttonEnterGame, 0);
        panelFirst.add(buttonRanking, 0);


        characterOption = 0;
        selectChar = GameVisuals.getCharachter(characterOption, 100);
        selectChar.setLocation(panelFirst.getWidth() / 2 - 65, panelFirst.getHeight() / 2 - 80);

        panelFirst.add(selectChar, 0);
        topText();
        panelSecond.setFocusable(true);
        coin = GameVisuals.getVisual(20, "src/img/dungeon/dollar.png");
        player1 = initializeCharacheter(characterOption);


        panelTop.add(panelGold, 0);

        sword = GameVisuals.getVisual(20, "src/img/dungeon/sword.png");
        sword.setLocation(panelSecond.getWidth() / 2, panelSecond.getHeight() / 2);
        panelSecond.add(sword, 0);
        mitra = GameVisuals.getVisual(20, "src/img/dungeon/mitra.png");
        potion = GameVisuals.getVisual(20, "src/img/dungeon/potion.png");
        potion.setLocation(panelSecond.getWidth() / 3, panelSecond.getHeight() / 2);
        panelSecond.add(potion, 0);
        fillArrayHearts();
        enemies = new ArrayList<>();
        fillMonsterArrayJLabel("src/img/skeleton/skeleton_down.gif");
        skeletons = new ArrayList<>();


        fillSKeletonObjArray();
        randomDirections();
        scoresArrayList = new ArrayList<>();
        timer_checkHearts = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        timer_checkHearts.start();
        Timer timer_rand_directions = new Timer(100000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                randomDirections();
            }
        });
        timer_rand_directions.start();
        timer_enemy = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int direction, min, max, y, x;
                y = 0;
                x = 0;
                int enemySpeed = 3;

                for (int i = 0; i < enemies.toArray().length; i++) {


                    if (skeletons.get(i).getDirection().equals("up")) {
                        y = enemySpeed;
                        move_monstersY(y, i, "up");
                        if (enemies.get(i).getIcon() != GameVisuals.getVisual(70, "src/img/skeleton/skeleton_up.gif").getIcon()) {
                            changeIcon("src/img/skeleton/skeleton_up.gif", i);
                        }
                        if (enemies.get(i).getY() < panelTop.getHeight() + 32 + enemies.get(i).getHeight()) {
                            skeletons.get(i).setDirection("down");
                        }
                    } else if (skeletons.get(i).getDirection().equals("down")) {
                        y = enemySpeed;
                        move_monstersY(y, i, "down");
                        if (enemies.get(i).getIcon() != GameVisuals.getVisual(70, "src/img/skeleton/skeleton_down.gif").getIcon()) {
                            changeIcon("src/img/skeleton/skeleton_down.gif", i);
                        }
                        if (enemies.get(i).getY() > panelSecond.getHeight() - 32 - enemies.get(i).getHeight()) {
                            skeletons.get(i).setDirection("up");
                        }
                    } else if (skeletons.get(i).getDirection().equals("left")) {
                        x = enemySpeed;
                        move_monstersX(x, i, "left");
                        if (enemies.get(i).getIcon() != GameVisuals.getVisual(70, "src/img/skeleton/skeleton_left.gif").getIcon()) {
                            changeIcon("src/img/skeleton/skeleton_left.gif", i);
                        }
                        if (enemies.get(i).getX() < 32) {
                            skeletons.get(i).setDirection("down");
                        }
                    } else {
                        x = enemySpeed;
                        move_monstersX(x, i, "right");
                        if (enemies.get(i).getIcon() != GameVisuals.getVisual(70, "src/img/skeleton/skeleton_right.gif").getIcon()) {
                            changeIcon("src/img/skeleton/skeleton_right.gif", i);
                        }
                        if (enemies.get(i).getX() > panelSecond.getWidth() - 32) {
                            skeletons.get(i).setDirection("left");
                        }
                    }
                }
            }
        });
        timer_enemy.start();

        timerRefresh = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean collision;
                collision = checkCollision(player1, coin, selectChar, 1);
                getCoin(coin, collision);
                panelTop.add(panelGold, 0);
                checkForCollisionWithcollectable(sword, 1);
                checkForCollisionWithcollectable(potion, 2);
                checkForCollisionWithcollectable(mitra, 3);

                checkCollisionEnemy();
                checkGameOver();
                checkWinGame();
            }
        });
        timerRefresh.start();
    }

    private void writeFile() {
        String info = player1.toString();
        Path filepath = Paths.get("src/resources/scores.txt");
        try {
            Files.writeString(filepath, info, StandardOpenOption.APPEND);
            Files.writeString(filepath, System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.out.println("error whilst saving your score");
        }

    }

    private void checkWinGame() {
        if (player1.getGoldCoins() >= 25) {
            timer_enemy.stop();
            timerRefresh.stop();
            timer_checkHearts.stop();
            JOptionPane.showMessageDialog(null, "You won!");
            writeFile();
            getScores();
            addDataToDB(player1.getName(), player1.getLives(), player1.getGoldCoins());
        }
    }

    private void checkGameOver() {
        if (player1.getLives() <= 0) {
            timer_enemy.stop();
            timerRefresh.stop();
            timer_checkHearts.stop();
            JOptionPane.showMessageDialog(null, "Game Over");
            writeFile();
            getScores();
            addDataToDB(player1.getName(), player1.getLives(), player1.getGoldCoins());
        }
    }

    private void fillArrayHearts() {
        for (int i = 0; i < player1.getLives(); i++) {
            hearts.add(GameVisuals.heart(true));


        }
        for (int i = 0; i < hearts.toArray().length; i++) {
            panelTop.add(hearts.get(i), 0);
        }
    }

    private void removeHeart() {

        c = player1.getLives();
        hearts.get(c).setIcon(GameVisuals.heart(false).getIcon());

    }

    private void checkCollisionEnemy() {

        for (int i = 0; i < enemies.toArray().length; i++) {
            boolean colision = checkCollision(player1, enemies.get(i), selectChar, 0.35);
            if (colision) {

                if (player1 instanceof Wizard && player1.getObjects().contains("potion")) {
                    enemies.get(i).setVisible(false);
                    enemies.remove(i);
                    player1.getObjects().remove("potion");
                    panelTop.revalidate();
                    panelTop.repaint();
                } else if (player1 instanceof Warrior && player1.getObjects().contains("sword")) {
                    enemies.get(i).setVisible(false);
                    enemies.remove(i);
                    player1.getObjects().remove("sword");
                    panelTop.revalidate();
                    panelTop.repaint();
                } else if (player1 instanceof Priest && player1.getObjects().contains("mitra")) {
                    enemies.get(i).setVisible(false);
                    enemies.remove(i);
                    player1.getObjects().remove("mitra");
                    panelTop.revalidate();
                    panelTop.repaint();
                } else {
                    player1.setLives(player1.getLives() - 1);
                    player1.setPositionY(50);
                    player1.setPositionX(10);
                    selectChar.setLocation(player1.getPositionX(), player1.getPositionY());
                    removeHeart();
                }

            }
        }


    }
    private void addDataToDB(String name, int lives, int goldCoins){
        String db_url = "jdbc:mysql://localhost:3306/juego_de_rol";
        String user = "juego";
        String passwd= "juego1";
        String insertQy = "insert into ranking (name, lives, gold_coins) values (?, ?, ?)";

        try{
            Connection con = DriverManager.getConnection(db_url, user, passwd);
            PreparedStatement ps = con.prepareStatement(insertQy);

            ps.setString( 1,name);
            ps.setInt(2, lives);
            ps.setInt(3, goldCoins);
            int addrows = ps.executeUpdate();
            if (addrows>0){
                System.out.println("The database has been recorded");
            }
            ps.close();
            con.close();
      } catch (Exception e) {
            System.out.println("Error writing in DB");
            e.printStackTrace();
            System.out.println(e.getStackTrace());
        }


    }

    private void addCollectedObjectsToPanel(int index) {
        JLabel itemToPlace = new JLabel();
        if (index == 1) {
            itemToPlace = GameVisuals.getVisual(20, "src/img/dungeon/sword.png");
            System.out.println("true");
            panelcollectedObjects.add(itemToPlace, 0);
            panelTop.add(panelcollectedObjects, 0);
            panelMain.add(panelTop);

        } else if (index == 2) {
            itemToPlace = potion;
            GameVisuals.getVisual(20, "src/img/dungeon/potion.png");
            System.out.println("true");
            panelcollectedObjects.add(itemToPlace, 0);
            panelTop.add(panelcollectedObjects, 0);

        } else if (index == 3) {
            itemToPlace = GameVisuals.getVisual(20, "src/img/dungeon/mitra.png");
            panelcollectedObjects.add(itemToPlace, 0);
            panelTop.add(panelcollectedObjects, 0);
            panelMain.add(panelTop);

        }
        panelTop.revalidate();
        panelTop.repaint();
        panelcollectedObjects.add(itemToPlace, 0);
        panelTop.add(panelcollectedObjects, 0);
    }


    private void fillSKeletonObjArray() {
        for (int i = 0; i < enemies.toArray().length; i++) {
            skeletons.add(new Skeleton("down"));
        }
    }

    private void randomDirections() {
        int min = 0;
        int max = 4;
        String direction;
        for (int i = 0; i < enemies.toArray().length; i++) {


            int num = (int) ((Math.random() * (max - min)) + min);
            if (num == 1) {
                direction = "up";
            } else if (num == 2) {
                direction = "down";
            } else if (num == 3) {
                direction = "left";
            } else {
                direction = "right";
            }
            skeletons.get(i).setDirection(direction);
        }
    }

    private void organiseFilesbyGold() {

    }

    private void getScores() {

        int y = 0;


        String fileName = "src/resources/scores.txt";


        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                JLabel text = new JLabel();
                text.setSize(new Dimension(panelMain.getWidth(), 200));
                scoresArrayList.add(line);
                text.setText(line);
                panelRanking.setLayout(null);
                text.setLocation(80, y);
                y += 20;
                panelRanking.add(text, 0);
                /*panelRanking.revalidate();
                panelRanking.repaint();*/

            }

            //text.setText("Capullo");


        } catch (Exception e) {
            System.out.println("Scores not found");
        }

    }

    private void move_monstersX(int x, int i, String direction) {
        if (direction.equals("right")) {
            enemies.get(i).setLocation(enemies.get(i).getX() + x, enemies.get(i).getY());
            panelSecond.add(enemies.get(i), 0);
        } else {
            enemies.get(i).setLocation(enemies.get(i).getX() - x, enemies.get(i).getY());
            panelSecond.add(enemies.get(i), 0);
        }
    }

    private void move_monstersY(int y, int i, String direction) {

        if (direction.equals("down")) {
            enemies.get(i).setLocation(enemies.get(i).getX(), enemies.get(i).getY() + y);
            panelSecond.add(enemies.get(i), 0);
        } else {
            enemies.get(i).setLocation(enemies.get(i).getX(), enemies.get(i).getY() - y);
            panelSecond.add(enemies.get(i), 0);
        }
    }

    private void changeIcon(String icon, int i) {

        enemies.get(i).setIcon(GameVisuals.getVisual(70, icon).getIcon());

    }

    private void fillMonsterArrayJLabel(String icon) {
        for (int i = 0; i < 5; i++) {

            JLabel enemy = GameVisuals.getVisual(70, icon);
            enemies.add(enemy);
            int randX, randY, minY, maxY, minX, maxX, wallDimenison;

            wallDimenison = 32;
            // Y limits
            minY = panelTop.getHeight() + wallDimenison;
            maxY = panelSecond.getHeight() - enemy.getHeight() - wallDimenison;
            // X limits
            minX = 0 + wallDimenison;
            maxX = panelSecond.getWidth() - enemy.getWidth() - wallDimenison;
            randY = (int) ((Math.random() * (maxY - minY)) + minY);
            randX = (int) ((Math.random() * (maxX - minX)) + minX);
            enemies.get(i).setLocation(randX, randY);
            panelSecond.add(enemies.get(i), 0);

        }

    }

    private Character initializeCharacheter(int f_charOpt) {


        switch (characterOption) {
            case 0:
                Warrior warrior = new Warrior(name, 5, 10, 10, "down", arrayCollectedObjects, 20, 20, "warrior");
                return warrior;

            case 1:
                Wizard wizard = new Wizard(name, 10, 20, 5, "down", arrayCollectedObjects, 20, 20, "wizzard");
                return wizard;

            default:
                Priest priest = new Priest(name, 5, 50, 3, "down", arrayCollectedObjects, 20, 20, "priest");
                return priest;

        }
    }

    private JPanel getPanelTop() {
        panelTop = new JPanel();
        panelTop.setSize(new Dimension(700, 50));
        panelTop.setBackground(Color.BLUE);
        return panelTop;
    }

    private JPanel getPanelGridLayout(int width, int height, int collumns) {
        JPanel panelGridLayout = new JPanel();
        panelGridLayout.setSize(width, height);
        panelGridLayout.setLayout(new GridLayout(1, collumns));
// 200 20
        return panelGridLayout;
    }


    private JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setSize(new Dimension(704, 480));
        panel.setLayout(null);

        return panel;
    }


    private JPanel getPanelMain() {
        panelMain.setPreferredSize(new Dimension(704, 480));
        panelMain.setLayout(null);
        panelMain.setFocusable(true);
        return panelMain;
    }

    private boolean checkCollision(Character p1, JLabel labelToCheck, JLabel charLabel, double sensitivity) {
        int leftpointOfRangeX = p1.getPositionX();
        double rightpointOfRangeX = p1.getPositionX() + charLabel.getWidth() * sensitivity;
        double rightpointOfRangeY = p1.getPositionY() + charLabel.getHeight() * sensitivity;
        int leftpointOfRangeY = p1.getPositionY();
        int labelLeft = labelToCheck.getX();
        int labelRight = labelToCheck.getX() + labelToCheck.getWidth();
        int labelTop = labelToCheck.getY();
        int labelBottom = labelToCheck.getY() + labelToCheck.getHeight();
        boolean collision = false;
        if (leftpointOfRangeX < labelRight && rightpointOfRangeX > labelLeft &&
                leftpointOfRangeY < labelBottom && rightpointOfRangeY > labelTop) {
            collision = true;

        }
        return collision;
    }


    private void getCoin(JLabel f_coin, boolean collision) {


        if (collision) {
            int randX, randY, minY, maxY, minX, maxX, wallDimenison;

            wallDimenison = 32;
            // Y limits
            minY = panelTop.getHeight() + wallDimenison;
            maxY = panelSecond.getHeight() - f_coin.getHeight() - wallDimenison;
            // X limits
            minX = 0 + wallDimenison;
            maxX = panelSecond.getWidth() - f_coin.getWidth() - wallDimenison;
            randY = (int) ((Math.random() * (maxY - minY)) + minY);
            randX = (int) ((Math.random() * (maxX - minX)) + minX);


            f_coin.setLocation(randX, randY);
            panelSecond.add(f_coin, 0);
            player1.setGoldCoins(player1.getGoldCoins() + 1);
            goldCountTxt.setText(player1.getGoldCoins() + " coins");

        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setLocation(300, 200);
    }

    private class start implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            playGame();
            name = textFieldName.getText();
            System.out.println(name);
            player1.setName(name);
        }
    }

    private class viewRanking implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            panelFirst.setVisible(false);
            getScores();
            panelRanking.setBackground(Color.RED);
            panelMain.add(panelRanking, 0);

        }
    }

    private class nameEntered implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            name = textFieldName.getText();
            player1.setName(name);
            System.out.println(name);
            panelFirst.requestFocus();
            panelMain.requestFocus();

        }
    }

    private class panelMainListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT) {

                if (characterOption != 0) {
                    characterOption = characterOption - 1;
                }
                selectChar.setVisible(false);
                System.out.println(characterOption);
                selectChar = GameVisuals.getCharachter(characterOption, 100);
                selectChar.setLocation(panelFirst.getWidth() / 2 - 65, panelFirst.getHeight() / 2 - 80);
                panelFirst.add(selectChar, 0);
                changeArrow("left");

            }

            if (key == KeyEvent.VK_RIGHT) {
                if (characterOption != 2) {
                    characterOption = characterOption + 1;

                }
                selectChar.setVisible(false);
                System.out.println(characterOption);
                selectChar = GameVisuals.getCharachter(characterOption, 100);
                selectChar.setLocation(panelFirst.getWidth() / 2 - 65, panelFirst.getHeight() / 2 - 80);
                panelFirst.add(selectChar, 0);
                changeArrow("right");

            }
            if (key == KeyEvent.VK_ENTER) {
                player1 = initializeCharacheter(characterOption);
                playGame();

            }
        }
    }

    private void playGame() {
        panelFirst.setVisible(false);
        panelMain.add(panelTop);
        panelMain.add(panelSecond);
        //selectChar.setSize(50, 50);
        panelSecond.add(selectChar, 0);
        panelMain.setFocusable(false);
        panelSecond.addKeyListener(new panelSecondListener());

        System.out.println(name);
        getCoin(coin, true);
        goldCountTxt.setText(player1.getGoldCoins() + " coins");
        panelGold.add(coin, 0);
        panelGold.add(goldCountTxt, 0);
        panelSecond.add(coin, 0);
    }

    private void changeArrow(String direction) {

        TimerTask task = null;

        if (direction.equals("left")) {
            arrow_left.setIcon(GameVisuals.getArrowLeft(true).getIcon());

            // Fer Timer per posar a blanc després de 2seg
            task = new TimerTask() {
                public void run() {
                    arrow_left.setIcon(GameVisuals.getArrowLeft(false).getIcon());
                }
            };
        } else if (direction.equals("right")) {
            arrow_right.setIcon(GameVisuals.getArrowRight(true).getIcon());

            task = new TimerTask() {
                public void run() {
                    arrow_right.setIcon(GameVisuals.getArrowRight(false).getIcon());
                }
            };
        }

        java.util.Timer timer = new java.util.Timer("hola");
        timer.schedule(task, 1000);

    }

    private class panelSecondListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int x, y;
            int speed;
            boolean action, collision;
            x = selectChar.getX();
            y = selectChar.getY();
            speed = player1.getSpeed();
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT) {
                if (x > 32) {
                    x = x - speed;
                    System.out.println(x);
                }

                if (!player1.getDirection().equals("left")) {
                    selectChar.setIcon(GameVisuals.getIconMovingGIF(characterOption, 100, "LEFT"));
                    player1.setDirection("left");

                }

            }

            if (key == KeyEvent.VK_RIGHT) {
                if (x < panelSecond.getWidth() - 32 - selectChar.getWidth())
                    x = x + speed;
                System.out.println(x);
                System.out.println(goldCountTxt);
                if (!player1.getDirection().equals("right")) {
                    selectChar.setIcon(GameVisuals.getIconMovingGIF(characterOption, 100, "RIGHT"));
                    player1.setDirection("right");
                }

            }
            if (key == KeyEvent.VK_UP) {
                if (y > panelTop.getHeight() + 32)
                    y = y - speed;
                System.out.println(y);
                if (!player1.getDirection().equals("up")) {
                    selectChar.setIcon(GameVisuals.getIconMovingGIF(characterOption, 100, "UP"));
                    player1.setDirection("up");
                }

            }

            if (key == KeyEvent.VK_DOWN) {
                if (y < panelSecond.getHeight() - 32 - selectChar.getHeight()) {
                    y = y + speed;
                }
                System.out.println(y);
                if (!player1.getDirection().equals("down"))
                    selectChar.setIcon(GameVisuals.getIconMovingGIF(characterOption, 100, "DOWN"));
                player1.setDirection("down");
                collision = checkCollision(player1, coin, selectChar, 1);
                getCoin(coin, collision);
            }

            selectChar.setLocation(x, y);
            player1.setPositionX(x);
            player1.setPositionY(y);

        }
    }

    private void checkForCollisionWithcollectable(JLabel collectable, int item) {
        boolean collided;
        String item_name;
        collided = checkCollision(player1, collectable, selectChar, 1);


        if (collided) {
            if (item == 1) {
                item_name = "sword";

            } else if (item == 2) {
                item_name = "potion";

            } else if (item == 3) {
                item_name = "mitra";

            } else {
                item_name = "sword";
            }
            arrayCollectedObjects.add(item_name);
            player1.setObjects(arrayCollectedObjects);
            collectable.setLocation(1000, 1000);
            System.out.println(item_name);
            addCollectedObjectsToPanel(item);
        }
    }

    public void topText() {
        title = new JLabel();
        title.setText("Dungeon Game");
        title.setSize(new Dimension(300, 150));
        ImageIcon image_title = new ImageIcon("src/img/dungeon/title.png");
        Icon icon_title = new ImageIcon(image_title.getImage().getScaledInstance(title.getWidth(), title.getHeight(), Image.SCALE_DEFAULT));
        title.setIcon(icon_title);
        title.setForeground(Color.WHITE);
        title.setLocation(panelFirst.getWidth() / 2 - 155, panelFirst.getHeight() / 2 - 220);
        panelFirst.add(title, 0);

    }


}
