package com.allendowney.thinkdast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class WikiNodeExample {
	
	public static void main(String[] args) throws IOException {
		/*
String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
Document doc = Jsoup.parse(html);
Element link = doc.select("a").first();

String text = doc.body().text(); // "An example link"
String linkHref = link.attr("href"); // "http://example.com/"
String linkText = link.text(); // "example""

String linkOuterH = link.outerHtml(); 
    // "<a href="http://example.com"><b>example</b></a>"
String linkInnerH = link.html(); // "<b>example</b>"
		 */
		
		String url = "https://en.wikipedia.org/wiki/Java_(programming_language)";
		URL realURL = new URL(url);
		System.out.println(realURL.getProtocol());
		System.out.println(realURL.getHost());
		System.out.println(realURL.getPath());
		System.out.println("-------------");

		// download and parse the document
		Connection conn = Jsoup.connect(
				realURL.getProtocol() + "://" + realURL.getHost() + realURL.getPath());
		Document doc = conn.get();
		
		//System.out.println("title: " + doc.title());
		
		//all text from the page
		String text = doc.body().text();
			
		
		// select the content text and pull out the paragraphs.
		Element content = doc.getElementById("mw-content-text");
				
		// TODO: avoid selecting paragraphs from sidebars and boxouts
		// 1. Find all hrefs
		// 2. remove already exit hrefs
		
		Elements links = content.select("a");
		List<String> listHref = links.stream()
				.map(a->a.attr("href"))
				.filter(h->h.startsWith("/wiki/"))
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		listHref.forEach(System.out::println);
		
		
		Elements paras = content.select("p");
		/*
		paras.stream()
				.map(p->p.text().trim())
				.filter(s->!s.isEmpty())
				.forEach(System.out::println);
		*/
		/*
		Iterator<Element> iterator = paras.iterator();
		while(iterator.hasNext()) {
			iterativeDFS(iterator.next());
			System.out.println();
		}*/
		
		
		//Element firstPara = paras.get(0);
		//System.out.println(firstPara.text());
		
		//recursiveDFS(firstPara);
		
		//iterativeDFS(paras); //firstPara
		
		//iterableMethod(firstPara);
		

	}
	
	private static void iterableMethod(Node root) {
		System.out.println("iterableMethod()");
		Iterable<Node> iter = new WikiNodeIterable(root);
		for (Node node: iter) {
			if (node instanceof TextNode) {
				System.out.print(node);
			}
		}
		System.out.println();
	}
	

	private static void iterativeDFS(Node root) {
		int count = 0;
		System.out.println("iterativeDFS()");
		Deque<Node> stack = new ArrayDeque<Node>();
		stack.push(root);

		// if the stack is empty, we're done
		while (!stack.isEmpty()) {

			// otherwise pop the next Node off the stack
			Node node = stack.pop();
			if (node instanceof TextNode) {
				System.out.println("[" + count++ + "] " + node);
			}

			// push the children onto the stack in reverse order
			List<Node> nodes = new ArrayList<Node>(node.childNodes());
			Collections.reverse(nodes);
			
			for (Node child: nodes) {
				stack.push(child);
			}
		}
		System.out.println();
	}

	private static void recursiveDFS(Node node) {
		System.out.println("recursiveDFS()");
		if (node instanceof TextNode) {
			System.out.print(node);
		}
		for (Node child: node.childNodes()) {
			recursiveDFS(child);
		}
		System.out.println();
	}
}
