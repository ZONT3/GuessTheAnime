package ru.zont.guesstheanime;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private ArrayList<Integer> completed = new ArrayList<>();
    private int score = 0;

    private void upd(Context context) {
        File save = new File(context.getFilesDir(), "player.sv");

        try {
            if (!save.exists()) {
                FileOutputStream out = new FileOutputStream(save);
                ObjectOutputStream oout = new ObjectOutputStream(out);
                oout.writeObject(this);
                oout.flush();
                oout.close();
            }

            FileInputStream in = new FileInputStream(save);
            ObjectInputStream oin = new ObjectInputStream(in);
            Player newpl = (Player) oin.readObject();
            oin.close();

            this.completed = newpl.completed;
            this.score = newpl.score;
        } catch (Exception e) {e.printStackTrace();}
    }

    boolean isCompleted(int i, Context context) {
        if (i<0) return true;
        upd(context);
        return completed.contains(i);
    }

    void setCompleted(int i, Context context) {
        upd(context);
        if (!completed.contains(i))
            completed.add(i);
        save(context);
    }

    int addScore(int add, Context context) {
        upd(context);
        score += add;
        save(context);
        return score;
    }

    private void save(Context context) {
        File save = new File(context.getFilesDir(), "player.sv");

        try {
            FileOutputStream out = new FileOutputStream(save);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(this);
            oout.flush();
            oout.close();
        } catch (Exception e) {e.printStackTrace();}
    }

    static boolean delete(Context context) {
        File save = new File(context.getFilesDir(), "player.sv");
        return save.delete();
    }
}
