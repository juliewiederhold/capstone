package edu.washington.akpuri.capstone;

import java.util.ArrayList;

/**
 * Created by Julie on 4/8/15.
 */
public class SingletonQuickText {
    private static SingletonQuickText instance = null;
    private static ArrayList<String> allQuickTexts;
    private static boolean isFirstViewOfQuickTexts;
    //Empty Constructor, it's a singleton
    protected SingletonQuickText() {
    }

    public static SingletonQuickText getInstance() {
        if (instance == null) {
            instance = new SingletonQuickText();
            allQuickTexts = new ArrayList<>();
            isFirstViewOfQuickTexts = true;
        }
        return instance;
    }

    public boolean hasQuickTexts() {return (allQuickTexts.size() != 0);}

    public ArrayList<String> getAllQuickTexts() {
        return allQuickTexts;
    }

    public void addToAllQuickTexts(String quickText){allQuickTexts.add(quickText);}

    public void removeFromAllQuickTexts(int index){allQuickTexts.remove(index);}

    public void setAllQuickTexts(ArrayList<String> quickTexts) {
        this.allQuickTexts = quickTexts;
    }

    public void setIsFirstViewOfQuickTexts(boolean view){
        isFirstViewOfQuickTexts = view;
    }

    public boolean getIsFirstViewOfQuickTexts(){return isFirstViewOfQuickTexts;}

}
