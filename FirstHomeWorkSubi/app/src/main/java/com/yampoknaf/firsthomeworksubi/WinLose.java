package com.yampoknaf.firsthomeworksubi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WinLose extends AppCompatActivity {


    private Bundle bundleWithBoardInformation;
    public static final String KEY_PLAY_AT_ONCE = "playAtOnceAfterGoToNextActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_lose);

        Intent intent = getIntent();
        bundleWithBoardInformation = intent.getBundleExtra(MainActivity.KEY_BUNDLE_TO_CREATE_BOARD);
        Button btnPlayAgain = (Button)findViewById(R.id.btnStartAgain);
        btnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundleWithBoardInformation.putBoolean(KEY_PLAY_AT_ONCE , true);
                nextActivity();
            }
        });

        Button btnGoToMenu = (Button)findViewById(R.id.btnGoToMenu);
        btnGoToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        ((TextView)findViewById(R.id.lblDifficultyWinLose)).setText(getString(R.string.difficult_lbl) + " " +
                GameParameters.AvaliableDifficulties.values()[bundleWithBoardInformation.getInt(MainActivity.KEY_BUNDLE_TO_CURRENT_DIFFICULTY)].toString());
        RelativeLayout relativeLayOut = (RelativeLayout)findViewById(R.id.winLoseOverAllLayer);
        if(GameManager.EndState.getGameState(bundleWithBoardInformation.getInt(GameProccess.KEY_WIN_LOSE)) == GameManager.EndState.WIN){
            relativeLayOut.setBackgroundResource(R.drawable.winner);
        }else{
            relativeLayOut.setBackgroundResource(R.drawable.you_lost);
        }
    }

    private void nextActivity(){
        Intent intent = new Intent(WinLose.this , MainActivity.class);
        intent.putExtra(MainActivity.KEY_BUNDLE_TO_CREATE_BOARD , bundleWithBoardInformation);
        startActivity(intent);
        finish();
    }


    @Override
    public void onStop(){
        super.onStop();
        System.gc();
    }
}
