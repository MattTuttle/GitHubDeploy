import java.io.*;

import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.service.*;

public class GitHubDeploy
{

	private String filename = "";
	private String description = "";

	private RepositoryId repository = null;
	private DownloadService service = null;

	public GitHubDeploy(String[] args)
	{
		try
		{
			parseArgs(args);
			if (repository == null || service == null || filename == "")
			{
				usage();
			}
			else
			{
				deploy();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void parseArgs(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].compareTo("-f") == 0)
			{
				i += 1;
				filename = args[i];
			}
			else if (args[i].compareTo("-d") == 0)
			{
				description = args[i];
			}
			else if (args[i].compareTo("-l") == 0)
			{
				i += 1;
				String[] login = args[i].split(":");

				service = new DownloadService();
				service.getClient().setCredentials(login[0], login[1]);
			}
			else if (args[i].compareTo("-r") == 0)
			{
				i += 1;
				String[] repo = args[i].split(":");
				repository = new RepositoryId(repo[0], repo[1]);
			}
		}
	}

	public void usage()
	{
		System.out.println("deploy -l user:pass -r id:repo -f filename.txt [-d Description]");
	}

	public void deploy() throws IOException
	{
		File file = new File(filename);

		Download download = new Download();
		download.setSize(file.length());
		download.setContentType("application/zip");
		download.setName(filename);
		download.setDescription(description);
		DownloadResource resource = service.createResource(repository, download);

		FileInputStream stream = new FileInputStream(file);
		service.uploadResource(resource, stream, download.getSize());
	}

	public static void main(String[] args)
	{
		new GitHubDeploy(args);
	}

}