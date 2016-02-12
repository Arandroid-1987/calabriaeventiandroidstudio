package com.calabriaeventi.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.calabriaeventi.core.Evento;
import com.calabriaeventi.core.GlobalState;
import com.calabriaeventi.utils.DateUtils;

public class JBlasaParser {

	public List<Evento> read(String feed, boolean provincia, GlobalState gs) {
		return readOther(feed, 0, 10, gs);
	}

	public List<Evento> readOther(String feed, int from, int to, GlobalState gs) {
		List<Evento> eventi = new LinkedList<Evento>();
		Document doc;
		try {
			String url = feed;
			InputStream input = new URL(url).openStream();
			doc = Jsoup.parse(input, "UTF-8", url);
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
//					for (Element row : righe) {

						if (row != null) {

							Elements titoloCorrente = row
									.select("span.evcal_event_title");

							if (titoloCorrente != null
									&& titoloCorrente.size() > 0) {

								Elements img = row.select("div.evcal_evdata_img");

								Element dataElement = row.select("em.evo_date")
										.first();

								String data = dataElement.text();

								// LUOGHI E CONTENUTO
								Elements luogoElement = row
										.select("span.evcal_event_subtitle");
								Elements descrizioneElement = row
										.select("div.eventon_full_description .eventon_desc_in");

								Evento evento = new Evento();

								if (img == null || img.get(0) == null) {
									evento.setImageUrl("");
								} else {
									String urlImg = img.get(0).attr("style");
									urlImg = urlImg.substring(urlImg
											.indexOf("(") + 1);
									urlImg = urlImg.substring(0,
											urlImg.indexOf(")"));
									evento.setImageUrl(urlImg);
								}

								String titolo = titoloCorrente.text();
								String luogo = luogoElement.text();
								if (luogo.equals("Festa patronale")) {
									luogo = titolo;
									titolo = "Festa patronale";
								}

								evento.setNome(titolo);
								evento.setData(data);

								evento.setLuogo(luogo);

								String descrizione = descrizioneElement.text();
//								descrizione = descrizione.substring(40);
								evento.setDescrizione(descrizione);
								k++;
								List<Date> date = DateUtils.getDatas(data);
								evento.setDate(date);

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
