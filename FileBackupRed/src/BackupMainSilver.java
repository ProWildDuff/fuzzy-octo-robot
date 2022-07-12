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
import javax.swing.UIManager;

public class BackupMainSilver 
{
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH;mm;ss");
    
    public static void main(String[] args) throws Exception 
    {
    	try {
    		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    	} 
    	catch (Exception e) {}
    	
    	List<String> listFilePath=new ArrayList<String>();
    	
    	try (Scanner myObj = new Scanner(System.in)) 
    	{
    		do
    		{
    			String path = selectDirectory(args);
				if(path == null)
				{
					System.out.println("Selection aborted. Proceeding to zipping");
					break;
				}
				listFilePath.add(path);				
				System.out.println("end Y/N");
    		}
			while (!myObj.next().equalsIgnoreCase("Y"));
    		myObj.close();
				
			if(listFilePath.size() > 0)
			{
				System.out.println("Ending list. Starting backup");
				String backupDir = selectDirectory(args);
				if(backupDir == null)
				{
					System.out.println("Destination selection aborted. Exiting...");
					return;
				}
				String MainBackupDirectory = backupDir + dtf.format(LocalDateTime.now());
					
				new File(MainBackupDirectory).mkdirs();

				int numFiles = 0;
					
				for (String FilePath: listFilePath)
				{
					File fileToZip = new File(FilePath);
		
					String listFileName = fileToZip.getName();
						
					FileOutputStream fos = new FileOutputStream(MainBackupDirectory + "\\" + listFileName + ".zip");
					ZipOutputStream zipOut = new ZipOutputStream(fos);

					numFiles += zipFile(fileToZip, listFileName, zipOut);
					    
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
    
	public static String selectDirectory(String[] args) 
	{
		String filePath;
		
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select a directory to zip");
		if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) 
		{
			filePath = chooser.getSelectedFile().getAbsolutePath();
			System.out.println("You chose to open this directory: " + filePath);
			return filePath;
		}
		return null;
	}
}
