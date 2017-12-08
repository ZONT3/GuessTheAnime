package ru.zont.guesstheanime;

import android.content.Context;
import android.support.annotation.DrawableRes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

class Anime {

    @DrawableRes int image = R.mipmap.ic_launcher;

    private ArrayList<String[]> titles = new ArrayList<>();
    ArrayList<String[]> displayTitles = new ArrayList<>();
    String originalTitle = "";
    String originalRomTitle = "";

    int bayannost = 5; // lesser = more bayan

    Anime(int i, Context context) {
        parse(i, context);
    }

    boolean hasTitle(String s) {
        if (s.equalsIgnoreCase(originalRomTitle) || s.equalsIgnoreCase(originalTitle)) return true;
        for (String[] t : titles)
            if (t[0].equalsIgnoreCase(s)) return true;
        return false;
    }

    private void parse(int i, Context context) {
        NodeList titlesNode = getRoot(context).getElementsByTagName("title");
        Element title = (Element) titlesNode.item(i);

        bayannost = Integer.valueOf(title.getAttribute("score"));
        image = context.getResources().getIdentifier(title.getAttribute("image"), "drawable", context.getPackageName());
        originalRomTitle = title.getAttribute("name");
        originalTitle = title.getAttribute("oname");

        NodeList names = title.getElementsByTagName("name");
        for (int j = 0; j < names.getLength(); j++) {
            Element name = (Element) names.item(j);
            titles.add(new String[]{name.getAttribute("value"), name.getAttribute("lang")});
            if (name.hasAttribute("hidden")) {
                if (name.getAttribute("hidden").equals("false"))
                    displayTitles.add(new String[]{name.getAttribute("value"), name.getAttribute("lang")});
            } else displayTitles.add(new String[]{name.getAttribute("value"), name.getAttribute("lang")});
        }
    }
    //----------STATIC

    private static Element root = null;

    private static Element getRoot(Context context) {
        if (root == null) {
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(context.getResources().openRawResource(R.raw.animelist));
                root = doc.getDocumentElement();
            } catch (Exception e) {e.printStackTrace();}
        }
        return root;
    }

    static int getTotalCount(Context context) {
        return getRoot(context).getElementsByTagName("title").getLength();
    }
}
