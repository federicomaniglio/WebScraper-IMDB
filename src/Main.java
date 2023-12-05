import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Main {


    public static void main(String[] args) {
        webScraper();
    }

    public static void webScraper() {
        Connection conn = Jsoup.connect("https://www.imdb.com/chart/moviemeter/");
        Document doc;
        try {
            doc = conn.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    int counter = 0;
        for (Element ul : doc.select("ul.ipc-metadata-list.ipc-metadata-list--dividers-between.sc-9d2f6de0-0.iMNUXk.compact-list-view.ipc-metadata-list--base")) {
            for (Element title : ul.select("h3.ipc-title__text")) {
                counter++;
                System.out.println(counter + " " + title.text());
            }
        }
    }
}