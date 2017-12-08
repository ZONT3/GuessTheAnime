package ru.zont.guesstheanime;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Anime {

    @DrawableRes int image = R.mipmap.ic_launcher;

    ArrayList<String[]> titles = new ArrayList<>();
    String originalTitle = "";
    String originalRomTitle = "";

    int bayannost = 5; // lesser = more bayan

    Anime(int i, Context context) {
        parse(i, context);
    }

    boolean hasTitle(String s) {
        return s.equals(originalRomTitle)
                || s.equals(originalTitle)
                || titles.contains(s);
    }

    private void parse(int i, Context context) {
        NodeList titlesNode = getRoot(context).getElementsByTagName("title");
        Element title = (Element) titlesNode.item(i);

        bayannost = Integer.valueOf(title.getAttribute("score"));
        image = context.getResources().getIdentifier(title.getAttribute("image"), "drawable", context.getPackageName());
        originalRomTitle = title.getAttribute("name");
        originalTitle = title.getAttribute("oname");

        NodeList names = title.getElementsByTagName("name");
        for (int j=0; j<names.getLength(); j++) {
            Element name = (Element) names.item(j);
            titles.add(new String[]{name.getAttribute("value"),name.getAttribute("lang")});
        }
    }

    //----------STATIC

    private static Element root = null;

    private static Element getRoot(Context context) {
        if (root == null) {
            Document doc = null;
            try {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(context.getResources().openRawResource(R.raw.animelist));
                root = doc.getDocumentElement();
            } catch (Exception e) {e.printStackTrace();}
        }
        return root;
    }
}
