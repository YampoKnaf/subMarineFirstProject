package com.yampoknaf.firsthomeworksubi;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GameProccess extends AppCompatActivity {

    private GameManager gameManager;
    private int screenWidth;
    private int screenHeight;

    private static final float PART_OF_SIDE_OF_SCREEN_FOR_MARGIN_SHIP_DISPLAY = 0.025f;
    private static final float PART_OF_TOP_OR_BOTTOM_FOR_MARGIN_SHIP_DISPLAY = 0.0005f;
    private static final float MAX_SIZE_OF_WIDTH_PICTURE_ON_DISPLAY = 0.13332f;
    private static final float PART_OF_MARGIN_FOR_SHIP_DISPLAY = 0.01f;
    private static final float HEIGHT_OF_ITEMS_IN_SHIP_DESPLAY = 0.025f;
    private static final float PART_OF_MARGIN_FOR_SHIP_BUTTON = 0.5f;
    private static final int OFF_SET_OF_PLAYER_BOARD = 0;
    private static final int OFF_SET_OF_ENEMY_BOTTON = 10;
    public final static String KEY_WIN_LOSE = "winLoseKey";
    public final static String MULTIPLAY_DISPLAY_SHIP = " x ";
    public final static String ID_VIEW_DISPLAY_BAR = "viewDisplayBar";

    public String lblWhosTurnKey;
    public String playerTurn;
    public String computerTurn;

    private static int numberOfPixelToSide;
    private static int numberOfPixelToAndBottom;
    private LinearLayout overAllLayout;

    private ArrayList<MyImageButton> allButton = new ArrayList<>();
    private ImageView[][] playerImageMatrix;
    private TextView showWhosTurn;
    private GameParameters.AvaliableDifficulties difficulty;
    private AIEnemy enemy;

    private static int sizeOfplayerImage = 0;
    private static int sizeOfEnemyImage = 0;

    private int[] pictureToDisplayBar;

    public boolean initializePlayerOnce = false;
    public boolean initializeEnemyOnce = false;
    public static BitmapDrawable hitPlayer;
    public static BitmapDrawable hitEnemy;
    public static BitmapDrawable missTargetPlayer;
    public static BitmapDrawable missTargetEnemy;
    public static BitmapDrawable drownPlayerVertical;
    public static BitmapDrawable drownEnemyVertical;
    public static BitmapDrawable drownPlayerHorizontal;
    public static BitmapDrawable drownEnemyHorizontal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_proccess);

        lblWhosTurnKey = getString(R.string.who_is_there);
        playerTurn = getString(R.string.player_turn);
        computerTurn = getString(R.string.computer_turn);

        TypedArray shipsToDisplay = getResources().obtainTypedArray(R.array.ImageOfDisplay);
        pictureToDisplayBar = new int[shipsToDisplay.length()];
        for (int i = 0; i < shipsToDisplay.length(); i++) {
            pictureToDisplayBar[i] = shipsToDisplay.getResourceId(i, -1);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        overAllLayout = (LinearLayout) findViewById(R.id.gameProccessOverAllLayOut);

        Intent intent = getIntent();
        Bundle bundleWithBoardInformation = intent.getBundleExtra(MainActivity.KEY_BUNDLE_TO_CREATE_BOARD);
        final int widthOfBoard = bundleWithBoardInformation.getInt(MainActivity.KEY_WIDTH_OF_BOARD);
        final int heightOfBoard = bundleWithBoardInformation.getInt(MainActivity.KEY_HEIGHT_OF_BOARD);
        final int[] shipsInformation = bundleWithBoardInformation.getIntArray(MainActivity.KEY_ALL_SHIPS_OF_BOARD);
        difficulty = GameParameters.AvaliableDifficulties.values()[bundleWithBoardInformation.getInt(MainActivity.KEY_BUNDLE_TO_CURRENT_DIFFICULTY)];

        playerImageMatrix = new ImageView[widthOfBoard][heightOfBoard];

        Thread initializeGame = new Thread(new Runnable() {
            @Override
            public void run() {
                gameManager = new GameManager(shipsInformation, widthOfBoard, heightOfBoard);
                enemy = new AIEnemy(gameManager.getBoard(GameManager.MakeMove.PLAYER));
                final Ship[][] playerBoard = gameManager.getBoard(GameManager.MakeMove.PLAYER);
                final Ship[][] enemyBoard = gameManager.getBoard(GameManager.MakeMove.COMPUTER);
                GameProccess.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createTopShipDisplayBar(shipsInformation);
                        createEnemyBoard(enemyBoard);
                        createTurnLable();
                        setwhosTurn(true);
                        createPlayerBoard(playerBoard);
                    }
                });
            }
        });


        initializeGame.start();
    }

    private void createTopShipDisplayBar(int[] shipInformation) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams linearLayoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        numberOfPixelToSide = (int) (PART_OF_SIDE_OF_SCREEN_FOR_MARGIN_SHIP_DISPLAY * screenWidth);
        numberOfPixelToAndBottom = (int) (PART_OF_TOP_OR_BOTTOM_FOR_MARGIN_SHIP_DISPLAY * screenHeight);
        linearLayoutParam.setMargins(numberOfPixelToSide, numberOfPixelToAndBottom, numberOfPixelToSide, numberOfPixelToAndBottom);
        linearLayout.setLayoutParams(linearLayoutParam);

        final int sizeOfPictureWidth = (int) (MAX_SIZE_OF_WIDTH_PICTURE_ON_DISPLAY * screenWidth);
        final int sizeOfMarginOverAll = (int) (screenWidth * PART_OF_MARGIN_FOR_SHIP_DISPLAY);
        final int heightOfItems = (int) (screenHeight * HEIGHT_OF_ITEMS_IN_SHIP_DESPLAY);

        for (int i = 0; i < shipInformation.length; i++) {
            BitmapDrawable bitmapDrawable = getImageInCurrentSize(sizeOfPictureWidth, heightOfItems, pictureToDisplayBar[i]);
            ImageView pictureTemp = new ImageView(this);

            pictureTemp.setImageDrawable(bitmapDrawable);
            LinearLayout.LayoutParams tempParamImage = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            tempParamImage.setMargins(sizeOfMarginOverAll, sizeOfMarginOverAll, sizeOfMarginOverAll, sizeOfMarginOverAll);
            pictureTemp.setLayoutParams(tempParamImage);
            linearLayout.addView(pictureTemp);

            TextView textTemp = new TextView(this);
            textTemp.setTextColor(Color.WHITE);
            textTemp.setText(MULTIPLAY_DISPLAY_SHIP + shipInformation[i]);
            textTemp.setId(ID_VIEW_DISPLAY_BAR.hashCode()+ i + 1);
            linearLayout.addView(textTemp);
        }

        overAllLayout.addView(linearLayout);
    }

    public void setDownshipDisplayBarViewByOne(int shipSize){
        TextView toChange = (TextView)findViewById(ID_VIEW_DISPLAY_BAR.hashCode() + shipSize);
        String periousText =  toChange.getText().toString();
        int numberOfShip = Integer.parseInt(periousText.charAt(periousText.length() - 1) + "") - 1;
        toChange.setText(MULTIPLAY_DISPLAY_SHIP + numberOfShip);
    }


    /// only run on gui thread
    private void createEnemyBoard(Ship[][] enemyBoard) {
        //final int sizeOfMarginOverAll = (int)(screenWidth*PART_OF_MARGIN_FOR_SHIP_BUTTON);
        final int sizeOfButton = (int) ((screenWidth - (float) screenWidth * PART_OF_MARGIN_FOR_SHIP_BUTTON) / (float) enemyBoard[0].length + OFF_SET_OF_ENEMY_BOTTON);


        GridLayout gridLayout = new GridLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        gridLayout.setRowCount(enemyBoard.length);
        gridLayout.setColumnCount(enemyBoard[0].length);
        sizeOfEnemyImage = sizeOfButton;
        BitmapDrawable imageTemp = getImageInCurrentSize(sizeOfButton, sizeOfButton, R.drawable.water);

        ButtonOfEnemyClickListener listener = new ButtonOfEnemyClickListener();
        for (int i = 0; i < enemyBoard.length; i++) {
            for (int j = 0; j < enemyBoard[i].length; j++) {
                MyImageButton temp = new MyImageButton(getApplicationContext(), j, i);
                temp.setPadding(OFF_SET_OF_ENEMY_BOTTON, OFF_SET_OF_ENEMY_BOTTON, OFF_SET_OF_ENEMY_BOTTON, OFF_SET_OF_ENEMY_BOTTON);
                temp.setImageDrawable(imageTemp);
                temp.setOnClickListener(listener);
                Ship ship = enemyBoard[i][j];
                if (ship != null)
                    ship.addButtonToShip(temp);
                allButton.add(temp);
                gridLayout.addView(temp);
            }
        }

        layoutParams.setMarginStart((screenWidth - gridLayout.getWidth()) / 9);
        layoutParams.setMarginEnd((screenWidth - gridLayout.getWidth()) / 9);
        gridLayout.setLayoutParams(layoutParams);
        overAllLayout.addView(gridLayout);
    }

    //only run on gui thread
    private void createTurnLable() {
        TextView textView = showWhosTurn = new TextView(this);
        textView.setText(lblWhosTurnKey);
        textView.setId(lblWhosTurnKey.hashCode());
        textView.setTextColor(Color.RED);
        textView.setTypeface(null, Typeface.BOLD);
        int width = textView.getWidth();
        textView.setPadding((screenWidth - width) / 5, OFF_SET_OF_ENEMY_BOTTON * 4, 0, OFF_SET_OF_ENEMY_BOTTON * 4);
        overAllLayout.addView(textView);

    }

    private void setwhosTurn(final boolean playerTurn) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playerTurn)
                    GameProccess.this.showWhosTurn.setText(GameProccess.this.playerTurn);
                else {
                    GameProccess.this.showWhosTurn.setText(computerTurn);
                }
            }
        });
    }

    /// only run on gui thread
    private void createPlayerBoard(Ship[][] playerBoard) {
        final int sizeOfButton = (int) ((screenWidth / 4 - (float) screenWidth * PART_OF_MARGIN_FOR_SHIP_BUTTON) / (float) playerBoard.length);


        GridLayout gridLayout = new GridLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //layoutParams.setMargins(sizeOfMarginOverAll,sizeOfMarginOverAll,sizeOfMarginOverAll,sizeOfMarginOverAll); // margin of button
        gridLayout.setRowCount(playerBoard[0].length);
        gridLayout.setColumnCount(playerBoard.length);
        sizeOfplayerImage = sizeOfButton;
        BitmapDrawable imageTempWater = getImageInCurrentSize(sizeOfButton, sizeOfButton, R.drawable.water);
        BitmapDrawable imageTempSub = getImageInCurrentSize(sizeOfButton, sizeOfButton, R.drawable.sub_in_area);
        for (int i = 0; i < playerBoard[i].length; i++) {
            for (int j = 0; j < playerBoard.length; j++) {
                ImageView temp = new ImageView(getApplicationContext());
                temp.setImageDrawable(playerBoard[j][i] == null ? imageTempWater : imageTempSub);
                playerImageMatrix[i][j] = temp;
                Ship ship = gameManager.getShip(GameManager.MakeMove.COMPUTER, j, i);
                if (ship != null)
                    ship.addImageView(temp);
                gridLayout.addView(temp);
            }
        }

        layoutParams.setMargins(0, OFF_SET_OF_PLAYER_BOARD * 3, 0, 0);

        layoutParams.setMarginStart((int) ((screenWidth - gridLayout.getWidth()) / 3));
        layoutParams.setMarginEnd((int) ((screenWidth - gridLayout.getWidth()) / 3));
        gridLayout.setLayoutParams(layoutParams);
        overAllLayout.addView(gridLayout);
    }

    private BitmapDrawable getImageInCurrentSize(int sizeOfPictureWidth, int heightOfItems, int whatToDraw) {

        Bitmap bitMapOriginal = BitmapFactory.decodeResource(getResources(), whatToDraw);

        int orgWidth = bitMapOriginal.getWidth();
        int orgHeight = bitMapOriginal.getHeight();

        float scaleWidth = ((float) sizeOfPictureWidth) / orgWidth;
        float scaleHeight = ((float) heightOfItems) / orgHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizableBitMap = Bitmap.createBitmap(bitMapOriginal, 0, 0,
                orgWidth, orgHeight, matrix, true);

        return new BitmapDrawable(getResources(), resizableBitMap);
    }


    class ButtonOfEnemyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            MyImageButton button = (MyImageButton) v;
            int xIndex = button.getxIndex();
            int yIndex = button.getyIndex();
            GameManager.BombResult result = gameManager.makeMove(yIndex, xIndex, GameManager.MakeMove.PLAYER);
            initializeEnemyImage(sizeOfEnemyImage);
            switch (result) {
                case MISS:
                    button.setImageDrawable(missTargetEnemy);
                    //enemyActionBoard[yIndex][xIndex] = GameManager.BombResult.MISS;
                    break;
                case HIT:
                    button.setImageDrawable(hitEnemy);
                    //enemyActionBoard[yIndex][xIndex] = GameManager.BombResult.HIT;
                    break;
                case DROWN_SHIP:
                    drawDrownShipOnButton(xIndex, yIndex);
                    break;
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setwhosTurn(false);
                        setAllButtonStillPlayableToEnable(false);
                        Thread.sleep((int) (Math.random() * 3000));
                        if (enemy == null)
                            return;
                        Point p = enemy.play();
                        final int y = p.y;
                        final int x = p.x;
                        GameProccess.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enemy.setResult(doTheGameOfComputer(y, x));
                            }
                        });
                        setAllButtonStillPlayableToEnable(true);
                        setwhosTurn(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();

            button.setEnabled(false);
            allButton.remove(button);
        }
    }

    private GameManager.BombResult doTheGameOfComputer(int y, int x) {
        initializePlayerImage(sizeOfplayerImage);
        GameManager.BombResult result = gameManager.makeMove(y, x, GameManager.MakeMove.COMPUTER);

        switch (result) {
            case MISS:
                playerImageMatrix[x][y].setImageDrawable(missTargetPlayer);
                //enemyActionBoard[yIndex][xIndex] = GameManager.BombResult.MISS;
                break;
            case HIT:
                playerImageMatrix[x][y].setImageDrawable(hitPlayer);
                //enemyActionBoard[yIndex][xIndex] = GameManager.BombResult.HIT;
                break;
            case DROWN_SHIP:
                drawDrownShipOnImage(x, y);
                break;
        }
        return result;
    }

    private void drawDrownShipOnImage(int xIndex, int yIndex) {
        Ship ship = gameManager.getShip(GameManager.MakeMove.COMPUTER, yIndex, xIndex);
        int shipSize = 0;
        GameManager.MyDirection direction = ship.getDirectionPlaced();
        ArrayList<ImageView> allView = ship.getAllImageView();
        if ((shipSize = ship.getSizeOfShip()) == 1) {
            allView.get(0).setImageDrawable(getRandomZeroOrOne() == 0 ? drownPlayerHorizontal : drownPlayerVertical);
            checkIfGameIsEnded();
            return;
        }
        for (ImageView imgView : allView) {
            switch (direction) {
                case NORTH:
                case SOUTH:
                    imgView.setImageDrawable(drownPlayerHorizontal);
                    break;
                case EAST:
                case WEST:
                    imgView.setImageDrawable(drownPlayerVertical);
                    break;
            }
        }
        checkIfGameIsEnded();
    }

    private void setAllButtonStillPlayableToEnable(final boolean enable) {
        GameProccess.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (MyImageButton button : allButton)
                    button.setEnabled(enable);
            }
        });
    }

    private void drawDrownShipOnButton(int xIndex, int yIndex) {
        Ship ship = gameManager.getShip(GameManager.MakeMove.PLAYER, yIndex, xIndex);
        int shipSize = 0;
        setDownshipDisplayBarViewByOne(ship.getSizeOfShip());
        GameManager.MyDirection direction = ship.getDirectionPlaced();
        ArrayList<MyImageButton> allButton = ship.getAllButton();
        if ((shipSize = ship.getSizeOfShip()) == 1) {
            allButton.get(0).setImageDrawable(getRandomZeroOrOne() == 0 ? drownEnemyHorizontal : drownEnemyVertical);
            checkIfGameIsEnded();
            return;
        }
        for (MyImageButton button : allButton) {

            switch (direction) {
                case NORTH:
                case SOUTH:
                    button.setImageDrawable(drownEnemyVertical);
                    break;
                case EAST:
                case WEST:
                    button.setImageDrawable(drownEnemyHorizontal);
                    break;
            }
        }
        checkIfGameIsEnded();
    }

    public int getRandomZeroOrOne() {
        return ((int) (Math.random() * 10000)) % 2;
    }

    public void initializePlayerImage(int sizeOfImage) {
        if (!initializePlayerOnce) {
            drownPlayerHorizontal = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.ship_player_hit_horizontal);
            drownPlayerVertical = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.ship_player_hit_vertical);
            hitPlayer = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.sub_player_hit);
            missTargetPlayer = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.target_miss);

            initializePlayerOnce = true;
        }
    }

    public void initializeEnemyImage(int sizeOfImage) {
        if (!initializeEnemyOnce) {
            drownEnemyHorizontal = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.ship_enemy_hit_horizontal);
            drownEnemyVertical = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.ship_enemy_hit_vertical);
            hitEnemy = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.target_hit);
            missTargetEnemy = getImageInCurrentSize(sizeOfImage, sizeOfImage, R.drawable.target_miss);
            initializeEnemyOnce = true;
        }
    }

    public void checkIfGameIsEnded() {
        final GameManager.EndState endState;
        if ((endState = gameManager.gameEnded()) != null) {
            Thread goToWinLoseIntent = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAllButtonStillPlayableToEnable(false);
                        Thread.sleep(400);
                        Intent intent = new Intent(GameProccess.this, WinLose.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt(KEY_WIN_LOSE, endState.getValue());
                        bundle.putInt(MainActivity.KEY_BUNDLE_TO_CURRENT_DIFFICULTY, difficulty.getValue());
                        intent.putExtra(MainActivity.KEY_BUNDLE_TO_CREATE_BOARD, bundle);
                        startActivity(intent);
                        finish();// made lots of problem because of the memory image took

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            goToWinLoseIntent.start();
        }
    }

    @Override
    public void onDestroy() { // deal with memory lick problem of bitmap
        super.onDestroy();
        enemy.endLevel();
        enemy = null;
        gameManager.endLevel();
        gameManager = null;
        overAllLayout.destroyDrawingCache();
        allButton = null;
        playerImageMatrix = null;
        difficulty = null;
        showWhosTurn = null;
        hitPlayer = null;
        hitEnemy = null;
        missTargetEnemy = null;
        missTargetPlayer = null;
        drownPlayerVertical = null;
        drownEnemyVertical = null;
        drownPlayerHorizontal = null;
        drownEnemyHorizontal = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        System.gc();

    } // much memory lick

    class MyImageButton extends ImageButton {

        private int xIndex;
        private int yIndex;

        public MyImageButton(Context context, int xIndex, int yIndex) {
            super(context);
            this.xIndex = xIndex;
            this.yIndex = yIndex;
        }

        public int getxIndex() {
            return xIndex;
        }

        public int getyIndex() {
            return yIndex;
        }
    }
}
