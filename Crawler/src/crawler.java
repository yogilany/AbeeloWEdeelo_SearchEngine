import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class crawler extends Thread {


    BlockingQueue<String> linksQueue;
    ArrayList<String> visited;

    FileWriter outputfile;

    inc numberoflinks;

    int max_number_of_links;

    public crawler(BlockingQueue<String> linksQueue, ArrayList<String> visited, inc numberoflinks, FileWriter outputfile, int max_number_of_links)
    {
        this.linksQueue = linksQueue;
        this.visited = visited;
        this.numberoflinks = numberoflinks;
        this.outputfile = outputfile;
        this.max_number_of_links = max_number_of_links;
    }



    public void run()
    {


        while (numberoflinks.getnumber() <= max_number_of_links)
        {



            String link2 = null;
            try {
                link2 = linksQueue.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Crawling: " + link2);


            visited.add(link2);
            Document doc = null;
            try {
                doc = Jsoup.connect(link2).ignoreHttpErrors(true).get();
            } catch (IOException e) {
                System.out.println(e);
            }
            if (doc == null)
                continue;


            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String linkUrl = link.attr("abs:href");
                if (visited.contains(linkUrl) || linkUrl == null )
                    continue;

                try {
                    linksQueue.put(linkUrl);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (numberoflinks)
                {
                    numberoflinks.addtonumber();
                System.out.print("Added link number");
                System.out.println(numberoflinks.getnumber());}


                synchronized (visited)
                {visited.add(linkUrl);}


                synchronized (outputfile)
                {
                    try {
                        outputfile.write(linkUrl + "\n");
                        outputfile.flush();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        }


    }





}
