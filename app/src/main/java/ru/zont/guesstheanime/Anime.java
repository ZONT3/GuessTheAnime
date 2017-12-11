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

    ArrayList<String[]> titles = new ArrayList<>();
    ArrayList<String[]> displayTitles = new ArrayList<>();
    private ArrayList<String[]> descriptions = new ArrayList<>();
    ArrayList<String> characters = new ArrayList<>();
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

    String getTitle(String lang, @SuppressWarnings("SameParameterValue") boolean display) {
        ArrayList<String[]> list = titles;
        if (display) list = displayTitles;
        String res = null;

        if (lang.equals("jp")) res = originalRomTitle;

        for (String[] str : list)
            if (str[1].equals(lang))
                return str[0];
        return res;
    }

    String getTitleLang(String title) {
        for (String[] str : titles)
            if (str[0].equalsIgnoreCase(title))
                return str[1];
        return "";
    }

    String getDescription(String lang) {
        for (String[] str : descriptions)
            if (str[1].equals(lang))
                return str[0];
        return null;
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

        NodeList descs = title.getElementsByTagName("description");
        for (int j=0; j<descs.getLength(); j++) {
            Element desc = (Element) descs.item(j);
            descriptions.add(new String[]{desc.getTextContent(), desc.getAttribute("lang")});
        }

        NodeList characters = title.getElementsByTagName("char");
        for (int j=0; j<characters.getLength(); j++)
            this.characters.add(characters.item(j).getTextContent());
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
