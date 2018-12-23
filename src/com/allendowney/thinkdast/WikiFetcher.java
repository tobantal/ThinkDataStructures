package com.allendowney.thinkdast;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class WikiFetcher { // implements Supplier<WikiFetcher> 
	private long lastRequestTime = -1;
	private long minInterval = 1100;
	
	final static String host = "https://en.wikipedia.org";
	
	final static BiConsumer<String,String> destinationChecker =
			(d,s)-> {
				if(d.equals(s)) {
					System.out.println("FIND !!!");
					System.exit(0);
				}
			};
			
	/**
	 *  Create a Singleton class
	 */
	/*
	private WikiFetcher wikiFetcher;
	private WikiFetcher() {}
	
	@Override
	public WikiFetcher get() {
		if(wikiFetcher!=null) {
			return new WikiFetcher();
		}
		return wikiFetcher;
	}
	*/

	/**
	 * Parses a URL string, returning a list of string (href).
	 * If destination is get then stop.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public List<String> findWiki(String url, String destination) {
		sleepIfNeeded();
		System.out.println(url);
		List<String> result = new ArrayList<>();
		try {
			result = Jsoup.connect(url)
					.get()
					.getElementById("mw-content-text")
					.select("a")
					.stream()
					.map(a->a.attr("href").trim())
					.filter(h->h.startsWith("/wiki/"))
					.distinct()
					.map(s->host+s)
					.peek(s->destinationChecker.accept(s, destination))
					.collect(Collectors.toList());
		} catch(IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Fetches and parses a URL string, returning a list of paragraph elements.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements fetchWikipedia(String url) throws IOException {
		sleepIfNeeded();

		// download and parse the document
		Connection conn = Jsoup.connect(url);
		Document doc = conn.get();

		// select the content text and pull out the paragraphs.
		Element content = doc.getElementById("mw-content-text");

		// TODO: avoid selecting paragraphs from sidebars and boxouts
		Elements paras = content.select("p");
		return paras;
	}

	/**
	 * Reads the contents of a Wikipedia page from src/resources.
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public Elements readWikipedia(String url) throws IOException {
		URL realURL = new URL(url);

		/*
		// assemble the file name
		String slash = File.separator;
		String filename = "resources" + slash + realURL.getHost() + realURL.getPath();

		// read the file
		InputStream stream = WikiFetcher.class.getClassLoader().getResourceAsStream(filename);
		Document doc = Jsoup.parse(stream, "UTF-8", filename);
		*/
		
		// download and parse the document
		Connection conn = Jsoup.connect(realURL.getProtocol() + "://" + realURL.getHost() + realURL.getPath());
		Document doc = conn.get();

		// parse the contents of the file
		Element content = doc.getElementById("mw-content-text");
		Elements paras = content.select("p");
		return paras;
	}

	/**
	 * Rate limits by waiting at least the minimum interval between requests.
	 */
	private void sleepIfNeeded() {
		if (lastRequestTime != -1) {
			long currentTime = System.currentTimeMillis();
			long nextRequestTime = lastRequestTime + minInterval;
			if (currentTime < nextRequestTime) {
				try {
					System.out.println("waiting...");
					Thread.sleep(nextRequestTime - currentTime);
				} catch (InterruptedException e) {
					System.err.println("Warning: sleep interrupted in fetchWikipedia.");
				}
			}
		}
		lastRequestTime = System.currentTimeMillis();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		WikiFetcher wf = new WikiFetcher();
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		Elements paragraphs = wf.readWikipedia(url);
		for (Element paragraph : paragraphs) {
			System.out.println(paragraph);
		}
	}

}