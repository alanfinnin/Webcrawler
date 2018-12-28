import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {
    /**
     * Checks the files essential files for
     * the operation and creates them if they
     * are not
     */
    public static void fileCheck(String filepath) {
        filepath = correctFileExtension(filepath);

        try {
            File crawledUrlFile = new File(filepath);
            if(!(crawledUrlFile.exists()))
                crawledUrlFile.createNewFile();

        } catch(IOException e) {
            System.out.println("File IO Error: " + e.getMessage());
        }
    }

    /**
     * Checks the filepath for the correct
     * file extension, adds it if incorrect
     *
     * @param filepath Path being checked
     * @return Filepath corrected
     */
    private static String correctFileExtension(String filepath) {
        if(!filepath.contains(".txt"))
            filepath += ".txt";
        return filepath;
    }


    /**
     * Takes URLs from the files
     * then puts them in an arraylist
     * as strings and returns them
     *
     * @return parsedUrls ArrayList
     */
    public static ArrayList<String> readFromUrlFile(String inputFilepath) {
        String filepath = correctFileExtension(inputFilepath);
        ArrayList<String> parsedUrls = new ArrayList<>();

        File stockFile = new File(filepath);
        try {
            Scanner in = new Scanner(stockFile);
            while(in.hasNext()) {
                parsedUrls.add(in.next());
            }
        } catch(IOException e) {
            System.out.println("IOException in URL file\n" + e);
        } catch(Exception e) {
            System.out.println("Error in URL File Read\n" + e);
            //parsedUrls.add("Empty file");
        }
        return parsedUrls;
    }

    public static void writeUrlsToFile(String inputFilepath, ArrayList<String> listOfUrls)
    {
        String filepath = correctFileExtension(inputFilepath);

        try
        {
            File urlFile = new File(filepath);
            FileWriter writer = new FileWriter(urlFile, true);
            for(String currentUrl : listOfUrls)
                writer.write(currentUrl + "\r\n");
            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println("Error in writing URLS to file\n" + e);
        }
    }
}
