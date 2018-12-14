import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Scanner;
import java.util.Timer;
import java.util.ArrayList;
import java.net.URI;
import java.io.IOException;

public class WebCrawler {
    /*
        The parameters for the webcrawler
     */
    private final static boolean readUrlFromCmdLine = true;
    private static int numberOfUrlsToTraverse = 100;
    private final static String urlPrefix = "http:";
    private final static String urlInputFilepath = "URLs_TO_CRAWL";
    private final static String crawledUrlsFilepath = "URLs_CRAWLED";
    private final static String errorLogFilepath = "ERROR_LOG_CRAWLER";
	private static String wordToFind = "";
	private static String search = "n";
    private static ArrayList<String> errorLog = new ArrayList<>();
    private static ArrayList<String> successfullyCrawledUrls = new ArrayList<>();


    public static void main(String[] args) {
        ArrayList<String> urlsToCrawl = initialiseCrawler(readUrlFromCmdLine);
		try
		{
		   for(String currentCrawlUrl : urlsToCrawl){
				crawler(currentCrawlUrl); // Traverse the Web from the a starting url
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception caught:\n" + e);
		}
		finally
		{
			dumpArraylistsToFile();
			System.out.println("Crawling Complete.");
		}
    }

    private static ArrayList<String> initialiseCrawler(boolean readUrlsFromCmdline){
        FileIO.fileCheck(urlInputFilepath);
        FileIO.fileCheck(crawledUrlsFilepath);
        FileIO.fileCheck(errorLogFilepath);
        ArrayList<String> urlsToCrawl = new ArrayList<>();
		
		Scanner input = new Scanner(System.in);
		System.out.print("Do you want to enter from cmd? (y/n): ");
		String checkBool = input.nextLine();
		if(checkBool.equals("n"))
			readUrlsFromCmdline = false;
        if(readUrlsFromCmdline){
			int numberToTraverse;;
			
			System.out.print("Enter a URL: ");
			String url = input.nextLine();
			
			System.out.print("How many traversels do you want?: ");
			String StringOfTreverse = input.nextLine();
			
			System.out.print("Do you want to find a word? (y/n): ");
			search = input.nextLine();
			
			if(search.equals("y")){
				System.out.print("What word?: ");
				wordToFind = input.nextLine();
			}
			try
			{
				numberToTraverse = Integer.parseInt(StringOfTreverse);
				numberOfUrlsToTraverse = numberToTraverse;
			}
			catch(NumberFormatException e)
			{}
			urlsToCrawl.add(url);
        }else {
            urlsToCrawl = FileIO.readFromUrlFile(urlInputFilepath);
        }
        return urlsToCrawl;
    }

    private static void dumpArraylistsToFile(){
        FileIO.writeUrlsToFile(crawledUrlsFilepath, successfullyCrawledUrls);
        FileIO.writeUrlsToFile(errorLogFilepath, errorLog);
    }

    public static void crawler(String startingURL) {
        System.out.println("Crawling...");
        ArrayList<String> listOfPendingURLs = new ArrayList<>();
        ArrayList<String> listOfTraversedURLs = new ArrayList<>();

		listOfPendingURLs.add(startingURL);
		while(!listOfPendingURLs.isEmpty() &&
				listOfTraversedURLs.size() <= numberOfUrlsToTraverse) {
			String urlString = listOfPendingURLs.remove(0);
			if(!(urlString.contains("w3") || urlString.contains("<") || urlString.length() > 80))
			{
				if(urlString.contains(wordToFind) && search.equals("y"))
				{
					try{
					java.awt.Desktop.getDesktop().browse(new URI(urlString));
					System.exit(0);
					}
					catch(Exception e)
					{}
				}
				listOfTraversedURLs.add(urlString);
				System.out.println(listOfTraversedURLs.size() + 1 + ": " + urlString);
				
				successfullyCrawledUrls.add(urlString + "\r\n");

				for (String s: getSubURLs(urlString)) {
					if (!listOfTraversedURLs.contains(s) &&
							!listOfPendingURLs.contains(s))
						listOfPendingURLs.add(s);
				}
			}
		}
    }

    public static ArrayList<String> getSubURLs(String urlString) {
        ArrayList<String> list = new ArrayList<>();
        try {
            java.net.URL url = new java.net.URL(urlString);
            Scanner input = new Scanner(url.openStream());
            int current = 0;
			int endIndex = 0;
            while (input.hasNext()) {
                String line = input.nextLine();
                current = line.indexOf(urlPrefix, current);
                while (current > 0) {
                    endIndex = line.indexOf("\"", current);
                    if (endIndex > 0) { // Ensure that a correct URL is found
                        list.add(line.substring(current, endIndex));
                        current = line.indexOf(urlPrefix, endIndex);
                    }
                    else
                        current = -1;
                }
            }
        }
        catch (Exception ex) {
            Instant errorTime = Instant.now();
            DateTimeFormatter formatter = DateTimeFormatter
                            .ofLocalizedTime( FormatStyle.LONG )
                            .withLocale( Locale.UK)
                            .withZone( ZoneId.systemDefault() );

            String outputTime = formatter.format(errorTime);
            System.out.println(outputTime + "; Error: " + ex.toString());
            errorLog.add(outputTime + ": " + ex.toString() + "\r\n");
        }
        return list;
    }
}
