/* Purpose of this program to backup select directories on a Windows PC
 * */

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

public class BackupMainSilver 
{
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH;mm;ss");
    
    public static void main(String[] args) throws Exception 
    {
    	List<String> list=new ArrayList<String>();
    	
    	String onward = "N"; // Read user input
    	
    	try (Scanner myObj = new Scanner(System.in)) {
			{

				while (!onward.equalsIgnoreCase("Y"))
				{
					list.add (selectDirectory(args));
					 System.out.println("end Y/N");
					 onward = myObj.next();
				}
				
				System.out.println("ending list");
				
				String destination = selectDirectory(args);;
				
				
					 
				System.out.println("Starting backup");
				String TimeOfDay = dtf.format(LocalDateTime.now());
				String MainBackupDirectory = (destination + TimeOfDay);
				
				new File(destination + TimeOfDay).mkdirs();

				int numFiles = 0;
				
				for (String FileName: list)
				{
				    FileOutputStream fos = new FileOutputStream(MainBackupDirectory + "\\" + ".zip");
				    ZipOutputStream zipOut = new ZipOutputStream(fos);
				    File fileToZip = new File(FileName);

				    numFiles += zipFile(fileToZip, fileToZip.getName(), zipOut);
				    zipOut.close();
				    fos.close();
				}
				System.out.println("Backup finished. Zipped " + numFiles + " files");
				}
		}
		}

    private static int zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException 
    {
        System.out.println("Zipping \"" + fileToZip.getPath() + "\"");
        if (fileToZip.isDirectory()) 
        {
            zipOut.putNextEntry(fileName.endsWith("/") ? new ZipEntry(fileName) : new ZipEntry(fileName + "/"));
            zipOut.closeEntry();
            int numZipped = 0;
            for (File childFile : fileToZip.listFiles())
                numZipped += zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            return numZipped;
        }
        else
        {
            FileInputStream fis = new FileInputStream(fileToZip);
            zipOut.putNextEntry(new ZipEntry(fileName));
            zipOut.write(fis.readAllBytes());
            fis.close();
            return 1;
        }
    }
    
	public static String selectDirectory(String[] args) {
		String filePath;
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = chooser.showOpenDialog(chooser);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		   System.out.println("You chose to open this directory: " +
		        chooser.getSelectedFile().getAbsolutePath());
	}
		filePath = chooser.getSelectedFile().getAbsolutePath();
		return filePath;
}
}