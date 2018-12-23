package com.allendowney.thinkdast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WikiPhilosophy {

	final static String host = "https://en.wikipedia.org";
    final static Set<String> visited = new HashSet<String>();
    final static WikiFetcher wf = new WikiFetcher();
    static List<String> wikies = new ArrayList<String>();

    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     *
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     *
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        
        testConjecture(destination, source, 10);

        //testConjectureWithStreams(destination, source, 2);     
        //wikies.forEach(System.out::println);
        //"https://en.wikipedia.org/wiki/Java_(programming_language)" ->
        //https://en.wikipedia.org/wiki/Research ->
        //https://en.wikipedia.org/wiki/Portal:Philosophy ->
        //"https://en.wikipedia.org/wiki/Philosophy"
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @param limit
     * @throws IOException
     */
	public static void testConjecture(String destination, String source, int limit) throws IOException {
		String url = source;
		for (int i=0; i<limit; i++) {
			if (visited.contains(url)) {
				System.err.println("We're in a loop, exiting.");
				return;
			} else {
				visited.add(url);
			}
			Element elt = getFirstValidLink(url);
			if (elt == null) {
				System.err.println("Got to a page with no valid links.");
				return;
			}
			
			System.out.println("**" + elt.text() + "**");
			url = elt.attr("abs:href");
			
			if (url.equals(destination)) {
				System.out.println("Eureka!");
				break;
			}
		}
	}
	
	/**
	 * Loads and parses a URL, then extracts the first link.
	 * 
	 * @param url
	 * @return the Element of the first link, or null.
	 * @throws IOException
	 */
	public static Element getFirstValidLink(String url) throws IOException {
		print("Fetching %s...", url);
		Elements paragraphs = wf.fetchWikipedia(url);
		WikiParser wp = new WikiParser(paragraphs);
		Element elt = wp.findFirstLink();
		return elt;
	}

	/**
	 * Formats and print the arguments.
	 * 
	 * @param msg
	 * @param args
	 */
	private static void print(String msg, Object... args) {
		System.out.println(String.format(msg, args));
	}
    
    public static void testConjectureWithStreams(String destination, String source, int limit) {
    	wikies.add(source);
    	
    	while(limit-- > 0) 
    	{	
    		visited.addAll(wikies);
    		wikies = wikies.stream()
    			.map(wiki->wf.findWiki(wiki, destination))
    			.flatMap(List::stream)
    			.distinct()
    			.filter(wiki->!visited.contains(wiki))
    			.collect(Collectors.toList());
    	};
    	    	
    }
}
