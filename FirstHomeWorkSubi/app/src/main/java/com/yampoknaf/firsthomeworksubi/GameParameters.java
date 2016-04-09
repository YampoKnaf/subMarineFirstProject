package com.yampoknaf.firsthomeworksubi;

/**
 * Created by Orleg on 22/03/2016.
 */
public class GameParameters {
    public enum  AvaliableDifficulties {
        EASY(0) , MEDIUM(1) , HARD(2);
        private final int value;

        private AvaliableDifficulties(int value){
            this.value = value;
        }

        public int getValue(){
            return value;
        }

        public String toString(){
            switch(value)
            {
                case 0:
                    return "Easy";
                case 1:
                    return "Medium";
                case 2:
                    return "Hard";
            }
            return "wrong index"; // will never happand
        }
    };

    private AvaliableDifficulties currDifficulty;
    private static final int[] sizeOfBoardWide = {3 , 4 , 5};
    private static final int[] sizeOfBoardHieght = {5 , 6 , 7};
    private static final int[][] numberOfShips = {{3 , 1} ,
                                                  {2, 2 , 1} ,
                                                  {1 , 4 , 1 , 1}};


    public GameParameters(){
        currDifficulty = AvaliableDifficulties.EASY;
    }

    public void setCurrDifficulty(AvaliableDifficulties diff){
        this.currDifficulty = diff;
    }

    public AvaliableDifficulties getCurrDifficulty(){
        return currDifficulty;
    }

    public int getWideSizeOfBoard(){
       return  sizeOfBoardWide[currDifficulty.getValue()];
    }

    public int getHieghtSizeOfBoard(){
        return  sizeOfBoardHieght[currDifficulty.getValue()];
    }

    public int[] getNumberAndSizeOfShipsInGame(){
        return numberOfShips[currDifficulty.getValue()];
    }

    public static String[] getStringOfDifficulties(){
        AvaliableDifficulties[] allEnums = AvaliableDifficulties.values();
        String[] strToReturn = new String[allEnums.length];
        for(int i = 0 ; i < strToReturn.length ; i++){
            strToReturn[i] = allEnums[i].toString();
        }
        return strToReturn;
    }
}
