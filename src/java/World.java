import jason.asSyntax.*;
import jason.environment.*;
import jason.environment.grid.Location;

import java.util.List;
import java.util.logging.*;

public class World extends Environment {

	private Logger logger = Logger.getLogger("secretagent."+World.class.getName());
	
	private static final Literal drawThree = Literal.parseLiteral("drawThree");
	private static final Literal passCards = Literal.parseLiteral("passCards");
	private static final Literal passKernel = Literal.parseLiteral("passKernel");
	private static final Literal playAntiVirus = Literal.parseLiteral("playAntiVirus");
	private static final Literal discardAntiVirus = Literal.parseLiteral("discardAntiVirus");
	private static final Literal playVirus = Literal.parseLiteral("playVirus");
	private static final Literal discardVirus = Literal.parseLiteral("discardVirus");
	private static final Literal voteYes = Literal.parseLiteral("voteYes");
	private static final Literal voteNo = Literal.parseLiteral("voteNo");
	private static final Literal wait = Literal.parseLiteral("wait");
	
	Model model;

	/** Called before the MAS execution with the args informed in .mas2j */
	
	@Override
	public void init(String[] args) {
		super.init(args);
		
		int num_players;
		boolean use_gui = false;
		//first optional argument is the number of players
		//second optional argument is the 
		if (args.length > 0){
			try{
				num_players = Integer.valueOf(args[0]);
				use_gui = (args.length > 1 && args[1].equals("gui"));
			}catch(NumberFormatException e){
				//if it wasnt a number, use the default number
				num_players = Model.DEFAULT_PLAYERS;
				//check if the argument was to use a gui
				use_gui = args[0].equals("gui");
			}
		}
		else{
			num_players = Model.DEFAULT_PLAYERS;
		}
		
		//create the model
		model = new Model(num_players);
		
		if (use_gui)
		{
			View view = new View(model);
			model.setView(view);
		}
		try { Thread.sleep(2000); } catch (InterruptedException x) { }
		updatePercepts();
	}

	@Override
	public boolean executeAction(String agName, Structure action) {
		
		boolean result = false;
		int agentId = Integer.parseInt(agName.substring(agName.length() - 1));
		
		if (action.equals(drawThree))
			result = model.drawThree(agentId);
		if (action.equals(passCards))
			result = model.passCards(agentId);
		if (action.equals(passKernel))
			result = model.passKernel(agentId);
		if (action.equals(playAntiVirus))
			result = model.playAntiVirus(agentId);
		if (action.equals(discardAntiVirus))
			result = model.discardAntiVirus(agentId);
		if (action.equals(playVirus))
			result = model.playVirus(agentId);
		if (action.equals(discardVirus))
			result = model.discardVirus(agentId);
		if (action.equals(voteYes))
			result = model.voteYes(agentId);
		if (action.equals(voteNo))
			result = model.voteNo(agentId);
		if (action.equals(wait))
			result = true;//do nothing
		
		else
			logger.info("executing: "+action+", but not implemented!");
		
		if (result)
		{
			try { Thread.sleep(2000); } catch (InterruptedException x) { }
			updatePercepts();
		}
		
		return result;
	}

	/** Called before the end of MAS execution */
	@Override
	public void stop() {
		super.stop();
	}
	
	private void updatePercepts()
	{
		clearPercepts();
		
		//tell all players about the board state
		addPercept(Literal.parseLiteral("virusPlayed(" + model.getNumVirus() + ")"));
		addPercept(Literal.parseLiteral("antiVirusPlayed(" + model.getNumAntiVirus() + ")"));
		
		//tell all players who the kernel is
		int kernelID = model.getKernel();
		if(kernelID != -1){
			addPercept(Literal.parseLiteral("kernel(" + kernelID + ")"));
			//tell the kernel about the contents of their hand
			if(model.getHandSize(kernelID) > 0){
				addPercept("player"+kernelID, Literal.parseLiteral("heldVirus(" + model.getHeldVirus(kernelID) + ")"));
				addPercept("player"+kernelID, Literal.parseLiteral("heldAntiVirus(" + model.getHeldAntiVirus(kernelID) + ")"));
			}
		}
		//tell all players who the ex kernel is
		int exKernelID = model.getExKernel();
		if(exKernelID != -1)
			addPercept(Literal.parseLiteral("exKernel(" + exKernelID + ")"));
		
		//tell all players who the scheduler is
		int schedulerID = model.getScheduler();
		if(schedulerID != -1){
			addPercept(Literal.parseLiteral("scheduler(" + schedulerID + ")"));
			if(model.getHandSize(schedulerID) > 0){
				addPercept("player"+schedulerID, Literal.parseLiteral("heldVirus(" + model.getHeldVirus(schedulerID) + ")"));
				addPercept("player"+schedulerID, Literal.parseLiteral("heldAntiVirus(" + model.getHeldAntiVirus(schedulerID) + ")"));
			}
		}
		
		//tell players who the ex scheduler is
		int exSchedulerID = model.getExScheduler();
		if(exSchedulerID != -1)
			addPercept(Literal.parseLiteral("exScheduler(" + exSchedulerID + ")"));
		
		//tell each player what their role is
		for(int i = 0; i < model.getNumPlayers(); i++)
			addPercept("player"+i, Literal.parseLiteral("role("+model.getRole(i)+")"));
		
		//tell each player what their number is
		for(int i = 0; i < model.getNumPlayers(); i++)
			addPercept("player"+i, Literal.parseLiteral("player("+i+")"));
	}
	
}
