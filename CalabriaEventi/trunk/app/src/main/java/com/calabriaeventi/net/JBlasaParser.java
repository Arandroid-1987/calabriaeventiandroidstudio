package com.calabriaeventi.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.calabriaeventi.model.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.utils.DateUtils;

public class JBlasaParser {

    public LinkedList<Evento> read(String feed, GlobalState gs) {
        return readOther(feed, 0, 10, gs);
    }

    public LinkedList<Evento> readOther(String feed, int from, int to, GlobalState gs) {
        LinkedList<Evento> eventi = new LinkedList<Evento>();
        Document doc;
        try {
            InputStream input = new URL(feed).openStream();
            doc = Jsoup.parse(input, "UTF-8", feed);
            Element container = doc.select("div#evcal_list").first();
            if (container != null) {
                List<Element> righe = container.select(".event");

                if (righe != null && righe.size() > 0) {

                    int k = gs.getK();
                    if (from > righe.size()) {
                        return eventi;
                    } else if (righe.size() < to) {
                        to = righe.size();
                    }

                    for (int i = from; i < to; i++) {
                        Element row = righe.get(i);

                        if (row != null) {
                            Elements titoloCorrente = row.select("span.evcal_event_title");
                            if (titoloCorrente != null && titoloCorrente.size() > 0) {
                                Elements img = row.select("div.evcal_evdata_img");

                                String startDate = "";
                                String endDate = "";
                                Element startDateElement = row.select("span.evo_start").first();
                                if (startDateElement != null) {
                                    startDate = startDateElement.select("em.date").text() + " " + startDateElement.select("em.month").text();
                                }
                                Element endDateElement = row.select("span.evo_end").first();
                                if (endDateElement != null) {
                                    endDate = endDateElement.select("em.date").text() + " " + endDateElement.select("em.month").text();
                                }
                                // LUOGHI E CONTENUTO
                                Elements luogoElement = row.select("span.evcal_event_subtitle");
                                Elements descrizioneElement = row.select("div.eventon_full_description .eventon_desc_in");

                                Evento evento = new Evento();

                                if (img == null || img.size() == 0 || img.get(0) == null) {
                                    img = row.select("span.ev_ftImg");
                                }

                                if (img == null || img.size() == 0 || img.get(0) == null) {
                                    evento.setImageUrl("");
                                } else {
                                    String urlImg = img.get(0).attr("data-img");
                                    evento.setImageUrl(urlImg.trim());
                                }

                                String titolo = titoloCorrente.text();
                                String luogo = luogoElement.text();
                                if (luogo.equals("Festa patronale")) {
                                    luogo = titolo;
                                    titolo = "Festa patronale";
                                }

                                evento.setNome(titolo);
                                evento.setDataInizio(startDate.trim());
                                evento.setDataFine(endDate.trim());
                                evento.setLuogo(luogo);
                                String descrizione = descrizioneElement.text();
                                evento.setDescrizione(descrizione);
                                k++;
                                DateUtils.setDates(evento);
                                eventi.add(evento);
                            }
                        }
                    }
                    gs.setK(k);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return eventi;
    }

}
