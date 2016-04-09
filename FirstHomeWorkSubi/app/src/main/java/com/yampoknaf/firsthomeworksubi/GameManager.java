package com.yampoknaf.firsthomeworksubi;



import java.util.ArrayList;
import java.util.Random;

public class GameManager {

    class MyRandom extends Random{

        public synchronized int nextInt() {
            return super.nextInt(35000);
        }
    }
    public enum MyDirection{

        NORTH(0) , WEST(1) , EAST(2) , SOUTH(3);

        private final int value;

        private MyDirection(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

        public static MyDirection getDirection(int value){
            switch(value){
                case 0:
                    return NORTH;
                case 1:
                    return WEST;
                case 2:
                    return EAST;
                case 3:
                    return SOUTH;
            }
            return null; //wrong value
        }

        public static MyDirection getNextDirection(MyDirection direction){
            switch(direction){
                case NORTH:
                    return SOUTH;
                case WEST:
                    return NORTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return EAST;
            }
            return null; //wrong value
        }
    }
    public enum MakeMove{PLAYER , COMPUTER};
    public enum BombResult{HIT , MISS , DROWN_SHIP , NO_ACTION};
    public enum EndState{
        WIN(0) , LOSE(1);
        private final int value;
        private EndState(int val){
            value = val;
        }

        public int getValue() {
            return value;
        }
        public static EndState getGameState(int val){
            return EndState.values()[val];
        }
    };

    private int shipCounterEnemy;
    private int shipCounterPlayer;
    private int shipCounterEnemyBombed;
    private int shipCounterPlayerBombed;
    private Ship[][] playerBoard = null;
    private Ship[][] enemyBoard = null;
    private final static int MAX_NUMBER_OF_FAILED_TO_BUILD_THE_BOARD = 100;

    public GameManager(int[] ships , int widthOfBoard , int heightOfBoard){
        int numberOfShips = 0;
        ArrayList<Ship> playerShips = new ArrayList<Ship>();
        ArrayList<Ship> enemyShips = new ArrayList<Ship>();
        for(int i = 1 ; i < ships.length + 1 ; i++){ // make the ship information into ship object
            numberOfShips +=  ships[i-1]*i;
            for(int j = 0 ; j < ships[i-1] ; j++){
                playerShips.add(new Ship(i));
                enemyShips.add(new Ship(i));
            }
        }
        shipCounterEnemy = shipCounterPlayer = numberOfShips; // numberOfShip part in game

        shipCounterEnemyBombed = shipCounterPlayerBombed = 0; // no ship has been bombed

        MyRandom rand = new MyRandom();

        boolean needToCreateNewBoards = true;
        while(needToCreateNewBoards) { // do until managed to place ship on board
            needToCreateNewBoards = false;
            initializeBoard(widthOfBoard, heightOfBoard); // create the board
            if(placeShipOnBoard(playerBoard , rand , playerShips)){ // try to put all the ship of the player inside his board
                if(!placeShipOnBoard(enemyBoard , rand , enemyShips)) // same as above but for enemy
                    needToCreateNewBoards = true;
            }
            else{
                needToCreateNewBoards = true;
            }
        }
    }

    private void initializeBoard(int widthOfBoard , int heightOfBoard){
        playerBoard = new Ship[heightOfBoard][widthOfBoard];
        enemyBoard = new Ship[heightOfBoard][widthOfBoard];
    }

    private boolean placeShipOnBoard(Ship[][] board , Random rand , ArrayList<Ship> arrOfShip){
        int counter = 0; // have 100 try overall -> a tiny chance for it to go above 15
        for (int i = 0; i < arrOfShip.size(); i++) {
            if(!placeShipOnBoard(board , rand , arrOfShip.get(i))) { // enter specific ship into the board
                i--;
                counter++;
                if(counter > MAX_NUMBER_OF_FAILED_TO_BUILD_THE_BOARD){
                    return  false;
                }
            }
        }
        return true;
    }
    private boolean placeShipOnBoard(Ship[][] board , Random rand , Ship shipToPlace){
        int yIndex = rand.nextInt()%playerBoard.length;
        int xIndex = rand.nextInt()%playerBoard[0].length;
        MyDirection direction = MyDirection.getDirection(rand.nextInt()%MyDirection.values().length);
        return placeShipOnBoard(board , xIndex , yIndex , shipToPlace , direction); //place helper
    }
    private boolean placeShipOnBoard(Ship[][] boardToPlaceOn ,int xIndex , int yIndex , Ship shipToPlaceOn , MyDirection direction){
        int shipSize = shipToPlaceOn.getSizeOfShip();

        for(int i = 0 ; i < MyDirection.values().length ; i++ , direction = MyDirection.getNextDirection(direction)){
            switch (direction) { // check if have place in the matrix for the ship
                case NORTH:
                    if (yIndex - shipSize + 1 < 0)
                        continue;
                    break;
                case WEST:
                    if (xIndex - shipSize + 1 < 0)
                        continue;
                    break;
                case EAST:
                    if (xIndex + shipSize - 1 >= boardToPlaceOn[i].length)
                        continue;
                    break;
                case SOUTH:
                    if (yIndex + shipSize - 1 >= boardToPlaceOn.length)
                        continue;
                    break;
            }
            boolean flagToNextIteration = false;
            for(int j = 0 ; j < shipSize ; j++) {
                switch (direction) { // check that there are no ship in that place allready
                    case NORTH:
                        if (boardToPlaceOn[yIndex - j][xIndex] != null) {
                            flagToNextIteration = true;
                            break;
                        }
                        break;
                    case WEST:
                        if (boardToPlaceOn[yIndex][xIndex - j] != null) {
                            flagToNextIteration = true;
                            break;
                        }
                        break;
                    case EAST:
                        if (boardToPlaceOn[yIndex][xIndex + j] != null) {
                            flagToNextIteration = true;
                            break;
                        }
                        break;
                    case SOUTH:
                        if (boardToPlaceOn[yIndex + j][xIndex] != null) {
                            flagToNextIteration = true;
                            break;
                        }
                        break;
                }
            }

            if(flagToNextIteration == true)
                continue;

            for(int j = 0 ; j < shipSize ; j++) {
                switch (direction) { // place the ship
                    case NORTH:
                        boardToPlaceOn[yIndex - j][xIndex] = shipToPlaceOn;
                        break;
                    case WEST:
                        boardToPlaceOn[yIndex][xIndex - j] = shipToPlaceOn;
                        break;
                    case EAST:
                        boardToPlaceOn[yIndex][xIndex + j] = shipToPlaceOn;
                        break;
                    case SOUTH:
                        boardToPlaceOn[yIndex + j][xIndex] = shipToPlaceOn;
                        break;
                }
            }

            shipToPlaceOn.setDirectionPlaced(direction);
            return true;
        }
        return false;
    }

    public BombResult  makeMove(int yIndex , int xIndex , MakeMove whoMadMove){
        BombResult toReturn = null;
        switch(whoMadMove){
            case COMPUTER:
                toReturn = makeMove(playerBoard , yIndex , xIndex);
                if(toReturn != BombResult.MISS )
                    shipCounterPlayerBombed++;
                break;
            case PLAYER:
                toReturn = makeMove(enemyBoard , yIndex , xIndex);
                if(toReturn != BombResult.MISS )
                    shipCounterEnemyBombed++;
                break;
        }
        return toReturn;
    }
    private BombResult makeMove(Ship[][] board , int yIndex , int xIndex){
        Ship tempShip;
        if((tempShip = board[yIndex][xIndex]) == null)
            return BombResult.MISS;
        else{
            if(tempShip.doDemage()){
                return BombResult.DROWN_SHIP;
            }else{
                return BombResult.HIT;
            }
        }
    }

    public EndState gameEnded(){
        if(shipCounterEnemy == shipCounterEnemyBombed)
            return EndState.WIN;
        else if(shipCounterPlayer == shipCounterPlayerBombed)
            return EndState.LOSE;
        else{
            return null;
        }
    }

    public Ship[][] getBoard(MakeMove whosBoard){
        switch (whosBoard){
            case PLAYER:
                return playerBoard;
            case COMPUTER:
                return enemyBoard;
        }
        return null;
    }

    public Ship getShip(MakeMove whosBoard , int yIndex , int xIndex){
        switch (whosBoard){
            case PLAYER:
                return enemyBoard[yIndex][xIndex];
            case COMPUTER:
                return playerBoard[yIndex][xIndex];
        }
        return null;
    }

    public void endLevel(){
        enemyBoard = null;
        playerBoard = null;
    }
}

