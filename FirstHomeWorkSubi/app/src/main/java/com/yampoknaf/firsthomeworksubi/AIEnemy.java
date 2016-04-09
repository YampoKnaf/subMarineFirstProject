package com.yampoknaf.firsthomeworksubi;
import android.graphics.Point;

import java.util.Random;

public class AIEnemy{
	private Point lastSuccessHit;
	private Point lastHit;
	
	private Point lastSuccessDir;
	private Point lastDir;
	private boolean goodDir = false;
	
	private boolean isAllShipDroped = false;
	private boolean isEverHit = false;
	
	private boolean[][] allTries;
	private Ship[][] playerBoard;
	private Random rand;
	private boolean secountHit = false;

	private static final int NUMBER_OF_POSSIBLE_DIRECTION = 4;
	
	public AIEnemy(Ship[][] playerBoard)
	{
		this.playerBoard = playerBoard;
		allTries = new boolean[playerBoard.length][playerBoard[0].length];
		rand = new Random();
	}
	
	public Point play()
	{
		Point pos = new Point();
		if(!isEverHit || isAllShipDroped)
		{
			pos.y = rand.nextInt(1000) % playerBoard.length;
			pos.x = rand.nextInt(1000) % playerBoard[0].length;
			while(allTries[pos.y][pos.x])
			{
				pos.y = rand.nextInt(1000) % playerBoard.length;
				pos.x = rand.nextInt(1000) % playerBoard[0].length;
			}
		}
		else if(!isAllShipDroped && !goodDir)
		{
			Point max = new Point(lastSuccessHit.x == playerBoard[0].length? -1 : 1 , lastSuccessHit.y == playerBoard.length? -1 : 1);
			Point min = new Point(lastSuccessHit.x == 0 ? 1 : -1 , lastSuccessHit.y == 0 ? 1 : -1);
			int counter =0;
			do
			{
				lastDir = getDir(min, max);
				pos = new Point(lastDir.x + lastSuccessHit.x , lastDir.y + lastSuccessHit.y);
				if(pos.x >= allTries[0].length || pos.y >= allTries.length || pos.x < 0 || pos.y < 0 || counter == NUMBER_OF_POSSIBLE_DIRECTION){
					isAllShipDroped = true;
					return play();
				}
				counter++;
			}while(allTries[pos.y][pos.x]);
		}
		else if(!isAllShipDroped && goodDir)
		{
			pos = new Point(lastSuccessHit.x + lastSuccessDir.x , lastSuccessHit.y + lastSuccessDir.y);
			if((pos.x >= allTries[0].length || pos.y >= allTries.length || pos.x < 0 || pos.y < 0) || allTries[pos.y][pos.x])
			{
				pos = new Point(lastSuccessHit.x - lastSuccessDir.x , lastSuccessHit.y - lastSuccessDir.y);
				while(allTries[pos.y][pos.x])
				{
					if(pos.x >= allTries[0].length || pos.y >= allTries.length || pos.x < 0 || pos.y < 0) {
						pos = new Point(pos.x - lastSuccessDir.x, pos.y - lastSuccessDir.y);
					}else{
						isAllShipDroped = true;
						return play();
					}
				}
			}	
		}
		allTries[pos.y][pos.x] = true;
		lastHit = pos;
		return pos;
	}
	
	public void setResult(GameManager.BombResult res)
	{
		switch(res)
		{
			case HIT:
				goodDir = false;
				if(secountHit)
					goodDir = true;
				secountHit = true;
				isAllShipDroped = false;
				isEverHit = true;
				lastSuccessHit = lastHit;
				lastSuccessDir = lastDir;
				break;
			case MISS:
				goodDir = false;
				secountHit = false;
				break;
			case DROWN_SHIP:
				secountHit = false;
				isEverHit = true;
				goodDir = false;
				isAllShipDroped = true;
				break;
		}


	}
	
	private Point getDir(Point min , Point max)
	{
		Point dir = new Point();
		if(rand.nextInt(1000) % 2 == 0)
		{
			dir.x = rand.nextInt(1000) % 2 == 0 ? max.x : min.x;
			dir.y = 0;
		}
		else
		{
			dir.x = 0;
			dir.y = rand.nextInt(1000) % 2 == 0 ? max.y : min.y;
		}
		return dir;
	}

	public void endLevel(){
		rand = null;
		playerBoard = null;
		allTries = null;
	}
}