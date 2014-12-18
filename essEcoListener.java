import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.api.Economy;
import com.vexsoftware.votifier.Votifier;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;

/**
 * A VoteListener that rewards via EssEco.
 * 
 * @author spacetrain31 
 *
 */
public class essEcoListener implements VoteListener
{
	/** The logger instance. */
	private static Logger logger = Logger.getLogger("essEcoListener");
	
	/** The amount to reward. */
	private int amount = 100;
	private String broadcast = "&4{player} just voted on {site}";
	private String thanks = "&4Thanks for voting on {site}";
	private String transfer = "&4{amount} has been awarded to your account!";

	/**
	 * Instantiates a new listener.
	 */
	public essEcoListener()
	{
		final Properties props = new Properties();
		try
		{
			// Create the file if it doesn't exist.
			final File configFile = new File("./plugins/Votifier/essEcoListener.conf");
			if (!configFile.exists())
			{
				configFile.createNewFile();

				// Load the configuration.
				props.load(new FileReader(configFile));

				// Write the default configuration.
				props.setProperty("reward_amount", Integer.toString(amount));
				props.setProperty("msg-broadcast", broadcast);
				props.setProperty("msg-thanks", thanks);
				props.setProperty("msg-transfer", transfer);
				props.store(new FileWriter(configFile), "essEco Listener Configuration");
			}
			else
			{
				// Load the configuration.
				props.load(new FileReader(configFile));
			}

			amount = Integer.parseInt(props.getProperty("reward_amount", "100"));
			broadcast = props.getProperty("msg-broadcast", broadcast);
			thanks = props.getProperty("msg-thanks", thanks);
			transfer = props.getProperty("msg-transfer", transfer);
		}
		catch (Exception ex)
		{
			logger.log(Level.WARNING, "Unable to load essEcoListener.conf, using default reward value of: " + amount);
		}
	}

	public void voteMade(Vote vote)
	{
		final String username = vote.getUsername();
		if (Economy.playerExists(username))
		{

			try
			{
				Economy.add(username, amount);
			}
			catch (Exception ex)
			{
				System.out.println("[Votifier] essEconomy error: " + ex.getMessage());
			}

			// Tell the player how awesome they are.
			final Player player = Bukkit.getServer().getPlayer(username);
			if (player != null)
			{
				if (thanks != "")
				{
					player.sendMessage(format(thanks, username, amount, vote));
				}
				if (transfer != "")
				{
					player.sendMessage(format(transfer, username, amount, vote));
				}
			}
			if (broadcast != "")
			{
				Votifier.getInstance().getServer().broadcastMessage(format(broadcast, username, amount, vote));
			}
		}
	}

	private String format(String message, String player, Integer amount, Vote site)
	{
		return message.replace('&', '§').replace("§§", "&").replace("{player}", player).replace("{amount}", amount.toString()).replace("{site}", site.getServiceName());
	}
	
	
	//TODO: log the vote when we recieve one.
	/*@EventHandler(priority=EventPriority.NORMAL)
    	public void onVotifierEvent(VotifierEvent event) {
        	Vote vote = event.getVote();
         	
    	}*/

}

