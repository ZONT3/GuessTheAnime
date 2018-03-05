package ru.zont.guesstheanime;

import android.content.Context;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

class Opening {

    int id;
    int animeID;
    String song;
    long start = 0;
    long end = -1;
    int score;
    String resource;

    private Opening(int i, int j, String song) {
        id = Integer.valueOf(i+""+j);
        this.song = song;
    }

    // STATIC

    static private ArrayList<Opening> all = null;

    static ArrayList<Opening> getAll(Context context) {
        if (all==null) {
            all = new ArrayList<>();
            NodeList titles = Anime.getRoot(context).getElementsByTagName("title");
            for (int i=0; i<titles.getLength(); i++) {
                Element title = (Element) titles.item(i);
                for (int j=0; j<title.getElementsByTagName("opening").getLength(); j++) {
                    Element op = (Element) title.getElementsByTagName("opening").item(j);
                    Opening opening = new Opening(i, j, op.getAttribute("song"));
                    opening.animeID = i;
                    opening.resource = "http://dltngz.clan.su/gtaoa/"+title.getAttribute("image")+".mp3";
                    if (j>0) opening.resource = "http://dltngz.clan.su/gtaoa/"+title.getAttribute("image")+"_"+j+".mp3";
                    if (op.hasAttribute("url")) opening.resource = op.getAttribute("url");
                    if (op.hasAttribute("start")) opening.start = Long.parseLong(op.getAttribute("start"));
                    if (op.hasAttribute("end")) opening.end = Long.parseLong(op.getAttribute("end"));
                    if (op.hasAttribute("score")) opening.score = Integer.valueOf(op.getAttribute("score"));
                    else opening.score = Integer.valueOf(title.getAttribute("score"));
                    all.add(opening);
                }
            }
        }
        return all;
    }

    static Opening get(int id) {
        for (Opening op : all)
            if (op.id==id) return op;
        return null;
    }

    @Override
    public String toString() {
        return song;
    }
}
