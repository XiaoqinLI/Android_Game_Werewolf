package edu.utexas.LI.wherewolf;

public class LeaveGameResponse extends BasicResponse{
	
	protected Game game = null;

	public LeaveGameResponse(String status, String message){
		super (status, message);
		
	}

	public LeaveGameResponse(String username, String password, Game game){
		super (username, password);
		
		this.game = game;
	}

	public LeaveGameResponse(String status, String message, int gameId){
		super (status, message);
		
		game.setGameId(gameId);
	}

	public Game getGame() {
		return game;
	}
	
	public int getGameId(){
		return game.getGameId();
	}
	
	
}
