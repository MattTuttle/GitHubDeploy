import java.io.*;
import java.util.*;
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
			String[] commands = args[i].split("=");
			if (commands[0].compareTo("-f") == 0)
			{
				filename = commands[1];
			}
			else if (commands[0].compareTo("-d") == 0)
			{
				description = commands[1];
			}
			else if (commands[0].compareTo("-l") == 0)
			{
				String[] login = commands[1].split(":");

				service = new DownloadService();
				service.getClient().setCredentials(login[0], login[1]);
			}
			else if (commands[0].compareTo("-r") == 0)
			{
				String[] repo = commands[1].split(":");
				repository = new RepositoryId(repo[0], repo[1]);
			}
		}
	}

	public void usage()
	{
		System.out.println("deploy -l=user:pass -r=id:repo -f=filename.txt [-d=Description]");
	}

	public void deploy() throws IOException
	{
		File file = new File(filename);

		List<Download> downloads = service.getDownloads(repository);
		Iterator<Download> it = downloads.iterator();
		while (it.hasNext())
		{
			Download download = it.next();
			if (download.getName().compareTo(filename) == 0)
			{
				service.deleteDownload(repository, download.getId());
				break;
			}
		}

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