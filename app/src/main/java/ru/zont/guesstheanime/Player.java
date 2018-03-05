package ru.zont.guesstheanime;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

class Player implements Serializable {
    private int version;

    private ArrayList<Integer> completed = new ArrayList<>();
    private ArrayList<Integer> completedOps = new ArrayList<>();
    private ArrayList<int[]> hintsPurchased = new ArrayList<>();
    private ArrayList<int[]> hintsPurchasedOps = new ArrayList<>();
    private int score = 40;

    private void upd(Context context) {
        File save = new File(context.getFilesDir(), "player.sv");

        try {
            if (!save.exists()) {
                FileOutputStream out = new FileOutputStream(save);
                ObjectOutputStream oout = new ObjectOutputStream(out);
                version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                oout.writeObject(this);
                oout.flush();
                oout.close();
            }

            FileInputStream in = new FileInputStream(save);
            ObjectInputStream oin = new ObjectInputStream(in);
            Player newpl = (Player) oin.readObject();
            oin.close();

            this.completed = newpl.completed;
            this.completedOps = newpl.completedOps;
            this.score = newpl.score;
            this.hintsPurchased = newpl.hintsPurchased;
            this.hintsPurchasedOps = newpl.hintsPurchasedOps;
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

    boolean isCompletedOps(int i, Context context) {
        if (i<0) return true;
        upd(context);
        return completedOps.contains(i);
    }

    void setCompletedOps(int i, Context context) {
        upd(context);
        if (!completedOps.contains(i))
            completedOps.add(i);
        save(context);
    }

    int opsCompletedCount(Context context) {
        upd(context);
        return completedOps.size();
    }

    int addScore(int add, Context context) {
        upd(context);
        score += add;
        save(context);
        return score;
    }

    void purchaseHint(int anime, int hint, Context context) {
        upd(context);
        hintsPurchased.add(new int[]{anime, hint});
        save(context);
    }

    boolean hintPurchased(int anime, int hint, Context context) {
        upd(context);
        for (int[] ints : hintsPurchased)
            if (ints[0]==anime&&ints[1]==hint)
                return true;
        return false;
    }

    void purchaseHintOps(int id, int hint, Context context) {
        upd(context);
        hintsPurchasedOps.add(new int[]{id, hint});
        save(context);
    }

    boolean hintPurchasedOps(int id, int hint, Context context) {
        upd(context);
        for (int[] ints : hintsPurchasedOps)
            if (ints[0]==id&&ints[1]==hint)
                return true;
        return false;
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

    void checkVer(int min, int opmin, int artmin, Context context) {
        upd(context);
        if (version<min)
            delete(context);
        else {
            if (version<opmin) completedOps = new ArrayList<>();
            if (version<artmin) completed = new ArrayList<>();
        }
        upd(context);
    }

    static boolean delete(Context context) {
        File save = new File(context.getFilesDir(), "player.sv");
        return save.delete();
    }
}
