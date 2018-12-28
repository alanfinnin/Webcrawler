import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Scanner;
import java.util.ArrayList;

public class WebCrawler {
    /*
        The parameters for the webcrawler
     */
    private final static boolean READ_URL_FROM_CMD_LINE = true;
    private final static int NUMBER_OF_URLS_TO_TRAVERSE = 10;
    private final static String URL_PREFIX = "http:";
    private final static String URL_INPUT_FILEPATH = "URLs_TO_CRAWL";
    private final static String CRAWLED_URLS_FILEPATH = "URLs_CRAWLED";
    private final static String ERROR_LOG_FILEPATH = "ERROR_LOG_CRAWLER";
	private final static String EMAILS_GRABBED_FILEPATH = "EMAILS_GRABBED";
    private static ArrayList<String> errorLog = new ArrayList<>();
    private static ArrayList<String> successfullyCrawledUrls = new ArrayList<>();
	private static ArrayList<String> emailLog = new ArrayList<String>();
	private static ArrayList<String> emailArray = new ArrayList<>();


    public static void main(String[] args) {
        ArrayList<String> urlsToCrawl = initialiseCrawler(READ_URL_FROM_CMD_LINE);

        for(String currentCrawlUrl : urlsToCrawl){
            crawler(currentCrawlUrl); // Traverse the Web from the a starting url
        }
        dumpArraylistsToFile();
        System.out.println("Crawling Complete.");
    }

    private static ArrayList<String> initialiseCrawler(boolean readUrlsFromCmdline){
        FileIO.fileCheck(URL_INPUT_FILEPATH);
        FileIO.fileCheck(CRAWLED_URLS_FILEPATH);
        FileIO.fileCheck(ERROR_LOG_FILEPATH);
		FileIO.fileCheck(EMAILS_GRABBED_FILEPATH);
        ArrayList<String> urlsToCrawl = new ArrayList<>();
        if(readUrlsFromCmdline){
            Scanner input = new Scanner(System.in);
            System.out.print("Enter a URL: ");
            String url = input.nextLine();
            urlsToCrawl.add(url);
        }else {
            urlsToCrawl = FileIO.readFromUrlFile(URL_INPUT_FILEPATH);
        }
        return urlsToCrawl;
    }

    private static void dumpArraylistsToFile(){
        FileIO.writeUrlsToFile(CRAWLED_URLS_FILEPATH, successfullyCrawledUrls);
        FileIO.writeUrlsToFile(ERROR_LOG_FILEPATH, errorLog);
		FileIO.writeUrlsToFile(EMAILS_GRABBED_FILEPATH, emailArray);
    }

    public static void crawler(String startingURL) {
        System.out.println("Crawling...");
        ArrayList<String> listOfPendingURLs = new ArrayList<>();
        ArrayList<String> listOfTraversedURLs = new ArrayList<>();


        listOfPendingURLs.add(startingURL);
        while (!listOfPendingURLs.isEmpty() &&
                listOfTraversedURLs.size() <= NUMBER_OF_URLS_TO_TRAVERSE) {
            String urlString = listOfPendingURLs.remove(0);
			if(!(urlString.contains("w3")))
			{
				System.out.println(urlString);
				listOfTraversedURLs.add(urlString);
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
			String subCheck = "";
			String email = "";
			String emailLine = "";
            while (input.hasNext()) {
                String line = input.nextLine();
				if(line.contains("@"))
				{
					if(line.contains(".com") || line.contains(".net") || line.contains(".ie"))
					{
						try
						{
							emailLine = line;
							if(!(emailLine.contains(" ")))
							{
								if(emailLine.contains("gmail"))
								{
									if(emailLine.contains("ie"))
										emailLine = line.substring((0), (line.indexOf("@")+9));
									else
										emailLine = line.substring((0), (line.indexOf("@")+10));
									emailLine = emailLine.trim();
									emailArray.add(emailLine);
								}
								else if (emailLine.contains("yahoo"))
								{
									if(emailLine.contains("ie"))
										emailLine = line.substring((0), (line.indexOf("@")+9));
									else
										emailLine = line.substring((0), (line.indexOf("@")+10));
									emailLine = emailLine.trim();
									emailArray.add(emailLine);
								}
								else if (emailLine.contains("outlook"))
								{
									if(emailLine.contains("ie"))
										emailLine = line.substring((0), (line.indexOf("@")+11));
									else
										emailLine = line.substring((0), (line.indexOf("@")+12));
									emailLine = emailLine.trim();
									emailArray.add(emailLine);
								}
								else if (emailLine.contains("studentmail"))
								{
									emailLine = line.substring((0), (line.indexOf("@")+18));
									emailArray.add(emailLine);
								}
							}
							else if(emailLine.contains("gmail"))
							{
								if(emailLine.contains("ie"))
									emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+8));
								else
									emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+9));
								emailLine = emailLine.trim();
								emailArray.add(emailLine);
							}
							else if (emailLine.contains("yahoo"))
							{
								if(emailLine.contains("ie"))
									emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+9));
								else
									emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+10));
								emailLine = emailLine.trim();
								emailArray.add(emailLine);
							}
							else if (emailLine.contains("outlook"))
							{
								if(emailLine.contains("ie"))
									emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+11));
								else
									emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+12));
								emailLine = emailLine.trim();
								emailArray.add(emailLine);
							}
							else if (emailLine.contains("studentmail"))
							{
								emailLine = line.substring((line.lastIndexOf(" ")), (line.indexOf("@")+18));
								emailLine = emailLine.trim();
								emailArray.add(emailLine);
							}
						}
						catch(Exception e)
						{
							System.out.println("Error\n" + emailLine + "\n" + e);
						}
					}
				}
                current = line.indexOf(URL_PREFIX, current);
                while (current > 0) {
                    int endIndex = line.indexOf("\"", current);
                    if (endIndex > 0) { // Ensure that a correct URL is found
                        list.add(line.substring(current, endIndex));
                        current = line.indexOf(URL_PREFIX, endIndex);
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
