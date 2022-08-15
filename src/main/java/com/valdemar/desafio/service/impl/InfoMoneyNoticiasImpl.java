package com.valdemar.desafio.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valdemar.desafio.model.entity.Noticia;
import com.valdemar.desafio.model.repository.NoticiaRepository;
import com.valdemar.desafio.utils.Utils;
import com.valdemar.desafio.utils.json.ResponseInfiniteScroll;

import org.jsoup.select.Elements;


public class InfoMoneyNoticiasImpl implements Runnable{
	private int paginasExtras = 2;
	private int nTotalNoticias = 0;

	private final NoticiaRepository noticiaRepository;
	
	public InfoMoneyNoticiasImpl(NoticiaRepository repo) {
		noticiaRepository = repo;
	}
	
	public void run() {
		System.out.println("************ o Iniciando processamento das notícias do site Infomoney ************");
		List<Noticia> noticias = new ArrayList<Noticia>();	
		//Trata página principal
		noticias.addAll(processaPagina("https://www.infomoney.com.br/mercados/", "mercados"));
		
		//Trata páginas adicionais
		try {
			Document doc = Jsoup.connect("https://www.infomoney.com.br/mercados/").get();
			String anterior = "https://www.infomoney.com.br/mercados/";
			int paginasVisitadas = 0;
			for (Element menuPai: doc.select(".menu-item-has-children")){
				
				if (paginasVisitadas < paginasExtras && menuPai.child(0).ownText().equals( "Notícias")) {
					for (Element categoria: menuPai.select(".menu-item a")) {
						String url = categoria.attr("href");
						String nomeCategoria = url.replace("https://www.infomoney.com.br/", "");
						if (paginasVisitadas < paginasExtras && !url.contains(anterior) && !nomeCategoria.equals("ultimas-noticias/") ) {
							anterior = url;
							nomeCategoria = nomeCategoria.substring(0, nomeCategoria.length()-1);						
							noticias.addAll(processaPagina(url, nomeCategoria));
							paginasVisitadas++;
						}
					}
				}				
			}
			System.out.println("************ Persistindo notícias do site Infomoney ************");
			noticiaRepository.saveAll(noticias);
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("************ Finalizando o processamento das notícias do site Infomoney ************");
			System.out.println("************ Foram Processadas um total de "+ nTotalNoticias + " notícias do site Infomoney************");
		}
		
	}
	
	private List<Noticia> processaPagina(String url, String categoria){
		try {
			List<Noticia> noticias = new ArrayList<Noticia>();
			Connection connection = Jsoup.connect(url).userAgent("Mozilla/5.0");
			Response response = connection.method(Connection.Method.GET).execute();
			Document doc = response.parse();	
			
			noticias = processaNoticiasFeed(doc.select(".article-card__asset a, .hl-title a"));
			noticias.addAll(carregarMais(2, categoria));
			noticias.addAll(carregarMais(3, categoria));
			
			return noticias;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Noticia> carregarMais(int page, String categoria) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Connection connectionMais = Jsoup.connect("https://www.infomoney.com.br/?infinity=scrolling");
			Response responseMais = connectionMais
									.userAgent("Mozilla/5.0")
									.ignoreContentType(true)
									.header("Content-Type","application/x-www-form-urlencoded;charset=UTF-8")
									.data("action","infinite_scroll")						
									.data("page", String.valueOf(page))						
									.data("order","DESC")
									.data("query_args[category_name]", categoria)
									.data("query_args[posts_per_page]","10")
									.followRedirects(true)
									.referrer("https://www.infomoney.com.br/" + categoria)
									.method(Connection.Method.POST).execute();	
			
			ResponseInfiniteScroll responseInfiniteScroll = objectMapper.readValue(responseMais.body(), ResponseInfiniteScroll.class);
			Document doc2 = responseMais.parse();
			doc2.body().html(responseInfiniteScroll.getHtml());
			Elements noticiasNovas = doc2.select(".article-card__asset-link, .hl-title a");
			return processaNoticiasFeed(noticiasNovas);
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private List<Noticia> processaNoticiasFeed(Elements noticiasFeed) {
		List<Noticia> noticiasList = new ArrayList<Noticia>(); 
		noticiasFeed.forEach(noticiaFeed -> {
			String urlNoticia = noticiaFeed.attr("href");
			Noticia noticia = processaNoticia(urlNoticia);
			noticiasList.add(noticia);
		});		
		return noticiasList;
	}
	
	private Noticia processaNoticia (String url) {
		Noticia noticia = new Noticia();
		try {
			Document doc = Jsoup.connect(url).get();
			System.out.println(url);
			String titulo = doc.select(".single__title").text();
			String subtitulo = doc.select(".single__excerpt").text();
			String autor = doc.select(".single__author-info a").text();
			String dataString = doc.select(".entry-date").attr("datetime");			
			Date dataPublicacao = Utils.getData(dataString , "yyyy-MM-dd'T'HH:mm:ssXXX");
			String conteudo = doc.select(".grid__article").text();
			
			noticia = new Noticia(url, titulo, subtitulo, autor, dataPublicacao, conteudo);	

			nTotalNoticias++;
			return noticia;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
