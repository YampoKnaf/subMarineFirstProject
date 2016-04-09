package com.yampoknaf.firsthomeworksubi;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GameParameters myGameParameters;
    TextView choooseDifficultyLable;

    public String difficultiesLabelString;
    private String chooseDifficulty;
    public static final String KEY_WIDTH_OF_BOARD = "widthOfBoard";
    public static final String KEY_HEIGHT_OF_BOARD = "heightOfBoard";
    public static final String KEY_ALL_SHIPS_OF_BOARD = "allShipsInformation";
    public static final String KEY_BUNDLE_TO_CURRENT_DIFFICULTY = "difficultyVar";
    public static final String KEY_BUNDLE_TO_CREATE_BOARD = "informationForBoard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        difficultiesLabelString = getString(R.string.difficult_lbl);
        chooseDifficulty = getString(R.string.choose_difficult);

        myGameParameters = new GameParameters();

        choooseDifficultyLable = (TextView)findViewById(R.id.lblDifficulty);
        updateLabelOfDifficulty(myGameParameters.getCurrDifficulty());

        Button difficultyButton = (Button)findViewById(R.id.btnChooseDifficulty);
        difficultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                myDialogBuilder.setTitle(chooseDifficulty);
                myDialogBuilder.setItems(GameParameters.getStringOfDifficulties(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myGameParameters.setCurrDifficulty(GameParameters.AvaliableDifficulties.values()[which]);
                        updateLabelOfDifficulty(myGameParameters.getCurrDifficulty());
                    }
                });
                myDialogBuilder.create().show();
            }
        });

        Button playButton = (Button)findViewById(R.id.btnStartGame);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this , GameProccess.class);
                Bundle bundleToDeliver = new Bundle();
                bundleToDeliver.putInt(KEY_WIDTH_OF_BOARD , myGameParameters.getWideSizeOfBoard());
                bundleToDeliver.putInt(KEY_HEIGHT_OF_BOARD , myGameParameters.getHieghtSizeOfBoard());
                bundleToDeliver.putIntArray(KEY_ALL_SHIPS_OF_BOARD, myGameParameters.getNumberAndSizeOfShipsInGame());
                bundleToDeliver.putInt(KEY_BUNDLE_TO_CURRENT_DIFFICULTY , myGameParameters.getCurrDifficulty().getValue());
                newIntent.putExtra(KEY_BUNDLE_TO_CREATE_BOARD , bundleToDeliver);
                startActivity(newIntent);
            }
        });

        try{
            Intent intent = getIntent();
            if(intent != null){
                Bundle bundle = intent.getBundleExtra(KEY_BUNDLE_TO_CREATE_BOARD);
                if(bundle.getBoolean(WinLose.KEY_PLAY_AT_ONCE) == true){
                    myGameParameters.setCurrDifficulty(GameParameters.AvaliableDifficulties.values()[bundle.getInt(KEY_BUNDLE_TO_CURRENT_DIFFICULTY)]);
                    playButton.performClick();
                }

            }
        }catch(Exception e){

        }
    }

    private void updateLabelOfDifficulty(GameParameters.AvaliableDifficulties difficult){
        choooseDifficultyLable.setText(difficultiesLabelString + " " + difficult.toString());
    }

    @Override
    public void onStop(){
        super.onStop();
        System.gc();
    }
}
