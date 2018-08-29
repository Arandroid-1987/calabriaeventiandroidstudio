package com.calabriaeventi.net.meteo;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MeteoParser {

    public String getMeteo(String comune, Date date) {
        if (comune.toLowerCase(Locale.ITALIAN).equals("calabria")) {
            return "";
        }
        String url = "http://www.ilmeteo.it/portale/meteo/previ.anas.php?";
        if (comune.contains("(")) {
            comune = new StringTokenizer(comune, "(").nextToken().trim();
            switch (comune) {
                case "Catanzaro Lido":
                    comune = "Catanzaro";
                    break;
                case "Gioiosa Jonica":
                    comune = "Gioiosa%20Ionica";
                    break;
                case "Le Castella di Isola Capo Rizzuto":
                    comune = "Isola%20di%20Capo%20Rizzuto";
                    break;
                case "Tassitano di Aprigliano":
                    comune = "Aprigliano";
                    break;
                default:
                    comune = comune.replaceAll(" ", "%20");
                    break;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM",
                Locale.ITALIAN);
        String data = sdf.format(date).toLowerCase(Locale.getDefault());
        url += "citta=" + comune + "&g=8";
        Document doc;
        InputStream input;
        try {
            input = new URL(url).openStream();
            doc = Jsoup.parse(input, "CP1252", url);
            Elements tr = doc.select("tr.dark");
            tr.addAll(doc.select("tr.light"));
            for (int i = 0; i < tr.size(); i++) {
                Element el = tr.get(i);
                Element a = el.select("a.active").first();
                if (a != null) {
                    String giorno = a.text().toLowerCase(Locale.getDefault())
                            .trim();
                    giorno = giorno.replace("\u00a0", " ");
                    StringTokenizer st = new StringTokenizer(giorno);
                    String giornoSettimana = st.nextToken();
                    String giornoMese = st.nextToken();
                    String mese = st.nextToken();
                    giornoMese = mettiZero(giornoMese);
                    giorno = giornoSettimana + " " + giornoMese + " " + mese;
                    if (giorno.equals(data)) {
                        Element td = el.select("td").get(1);
                        String src = td.select("img").first().attr("src");
                        src = convertToCool(src);
                        return src;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String mettiZero(String giornoMese) {
        if (giornoMese.length() == 1) {
            return "0" + giornoMese;
        }
        return giornoMese;
    }

    private String convertToCool(String src) {
        if (src.endsWith("s1.gif")) { // sole
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/113_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s1.png";
        }
        if (src.endsWith("s2.gif")) { // sole caldo
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/113_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s2.png";
        }
        if (src.endsWith("s3.gif")) { // parzialmente nuvoloso
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/116_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s3.png";
        }
        if (src.endsWith("s4.gif")) { // molto nuvoloso
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/119_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s4.png";
        }
        if (src.endsWith("s5.gif")) { // variabile con pioggie
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/176_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s5.png";
        }
        if (src.endsWith("s6.gif")) { // variabile con nevischio
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/179_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s6.png";
        }
        if (src.endsWith("s7.gif")) { // variabile con neve
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/179_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s7.png";
        }
        if (src.endsWith("s8.gif")) { // nuvoloso
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/122_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s8.png";
        }
        if (src.endsWith("s9.gif")) { // pioggia
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/266_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s9.png";
        }
        if (src.endsWith("s10.gif")) { // pioggia forte
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/359_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s10.png";
        }
        if (src.endsWith("s11.gif")) { // neve
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/227_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s11.png";
        }
        if (src.endsWith("s12.gif")) { // acquaneve
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/317_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s12.png";
        }
        if (src.endsWith("s13.gif")) { // temporale
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/386_night_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s13.png";
        }
        if (src.endsWith("s14.gif")) { // nebbia
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/248_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s14.png";
        }
        if (src.endsWith("s15.gif")) { // nebbia e sole
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/248_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s15.png";
        }
        if (src.endsWith("s16.gif")) { // temporale e schiarite
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/176_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s16.png";
        }
        if (src.endsWith("s17.gif")) { // grandine
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/359_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s17.png";
        }
        if (src.endsWith("s18.gif")) { // neve leggera
            // src =
            // "http://cdn.worldweatheronline.net/images/weather/small/227_day_sm.png";
            src = "http://www.arandroid.altervista.org/meteo/s18.png";
        }
        return src;
    }

}