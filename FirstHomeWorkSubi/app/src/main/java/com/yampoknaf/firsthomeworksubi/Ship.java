package com.yampoknaf.firsthomeworksubi;


import android.widget.ImageView;

import java.util.ArrayList;

public class Ship {
    private int size;
    private int numberOfPartDemaged;
    private GameManager.MyDirection directionPlaced;
    private ArrayList<GameProccess.MyImageButton> allButton;
    private ArrayList<ImageView> allImageView;

    public Ship(int sizeOfShip){
        size = sizeOfShip;
        numberOfPartDemaged = 0;
        allButton = new ArrayList<>();
        allImageView = new ArrayList<>();
    }

    public boolean doDemage(){
        numberOfPartDemaged++;
        return shipHasBeenDestroyed();
    }

    public boolean shipHasBeenDestroyed(){
        return size == numberOfPartDemaged;
    }

    public int getSizeOfShip(){
        return size;
    }

    public void setDirectionPlaced(GameManager.MyDirection directionPlaced) {
        this.directionPlaced = directionPlaced;
    }

    public GameManager.MyDirection getDirectionPlaced() {
        return directionPlaced;
    }

    public void addButtonToShip(GameProccess.MyImageButton but){
        allButton.add(but);
    }

    public ArrayList<GameProccess.MyImageButton> getAllButton(){
        return allButton;
    }

    public void addImageView(ImageView imageView){
        allImageView.add(imageView);
    }

    public ArrayList<ImageView> getAllImageView() {
        return allImageView;
    }
}
