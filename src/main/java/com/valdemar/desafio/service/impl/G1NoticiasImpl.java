package com.valdemar.desafio.service.impl;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valdemar.desafio.model.entity.Noticia;
import com.valdemar.desafio.model.repository.NoticiaRepository;
import com.valdemar.desafio.utils.Utils;
import com.valdemar.desafio.utils.json.g1.responseGetNoticias.ResponseG1;


public class G1NoticiasImpl implements Runnable{
	private int paginasExtras = 2;
	private int nTotalNoticias = 0;
	private final NoticiaRepository noticiaRepository;
	
	public G1NoticiasImpl(NoticiaRepository repo) {
		noticiaRepository = repo;
	}
	//@Autowired
	public void run() {
		System.out.println("************ o Iniciando processamento das notícias do site G1 ************");
		try {
			Document doc;
			List<Noticia> todasNoticias = new ArrayList<Noticia>();
			//Trata página principal
			List<Noticia> noticiasPaginaInicial = processaPagina("https://g1.globo.com/");
			todasNoticias.addAll(noticiasPaginaInicial);
			
			//Trata páginas adicionais
			List<Noticia> noticiasPaginasSecundarias = new ArrayList<Noticia>();
			doc = Jsoup.connect("https://g1.globo.com/").get();			
			Elements menus = doc.select(".menu-item-link");
			String anterior = "-1";
			int paginasVisitadas = 0;
			for (Element menu: menus) {
				if (paginasVisitadas < paginasExtras && menu.attr("href")!= null && menu.attr("href")!= "") {
					String url = menu.attr("href");
					if (!url.contains(anterior)) {
						anterior = url;
						paginasVisitadas++;
						noticiasPaginasSecundarias.addAll(processaPagina(url));						
					}
				}
			}
			todasNoticias.addAll(noticiasPaginasSecundarias);
			System.out.println("************ Persistindo notícias do site G1 ************");
			noticiaRepository.saveAll(todasNoticias);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("************ Finalizando o Iniciando processamento das notícias do site G1 ************");
			System.out.println("************ Foram Processadas um total de "+ nTotalNoticias + " notícias do site G1 ************");
		}
	}
	
	
	private List<Noticia> processaPagina(String url) {
		List<Noticia> listaNoticias = new ArrayList<Noticia>();
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			for (Element elem: doc.select("script")){
				if (elem.html().contains("SETTINGS.BASTIAN[\"RESOURCE_URI\"]")) {
					String urlGetNoticias = elem.html().split(";")[3].split("=")[1].replace("\"","");
					listaNoticias.addAll(processaPorPage(urlGetNoticias, 1));
					listaNoticias.addAll(processaPorPage(urlGetNoticias, 2));
					listaNoticias.addAll(processaPorPage(urlGetNoticias, 3));
				}
			}			
			return listaNoticias;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Noticia> processaPorPage(String url, int page) {
		List<Noticia> listaNoticias = new ArrayList<Noticia>();
		try {
			URL urlGet = new URL(url + "/page/" + page);
			HttpURLConnection con = (HttpURLConnection) urlGet.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("accept", "application/json");
			InputStream responseStream = con.getInputStream();					
			ObjectMapper mapper = new ObjectMapper();
			ResponseG1 response = mapper.readValue(responseStream, ResponseG1.class);
			response.getItems().forEach(item ->{
				listaNoticias.add(processaNoticia(item.getContent().getUrl()));						
			});
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return listaNoticias;
	}
	
	
	private Noticia processaNoticia (String url) {
		System.out.println(url);
		Noticia noticia = new Noticia();
		try {
			Document doc = Jsoup.connect(url).get();
			String titulo = doc.select(".content-head__title").text();
			String subtitulo = doc.select(".content-head__subtitle").text();
			String autor = doc.select(".content-publication-data__from").attr("title");
			String dataString = doc.select(".content-publication-data__updated time").attr("datetime");
			Date dataPublicacao = Utils.getDataGMT(dataString , "yyyy-MM-dd'T'HH:mm:ss.SSS");
			String conteudo = doc.select("article").text();
			noticia = new Noticia(url, titulo, subtitulo, autor, dataPublicacao, conteudo);
			nTotalNoticias++;
			return noticia;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
}
