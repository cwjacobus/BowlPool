package actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

import dao.DAO;
import data.BowlGame;

public class UpdateScoreAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private Integer favoriteScore;
	private Integer underDogScore;
	private String favorite;
	private String underdog;
	private Integer gameId;
	private String year;
	private boolean champGame;

	public String execute() throws Exception {
		DAO.updateScore(favoriteScore, underDogScore, gameId, favorite, underdog);
		
		List<BowlGame> bowlGameList = DAO.getBowlGamesList();
		
		ValueStack stack = ActionContext.getContext().getValueStack();
	    Map<String, Object> context = new HashMap<String, Object>();

	    context.put("bowlGameList", bowlGameList);
	    stack.push(context);
		
	    return "success";
	}
	   
	public Integer getFavoriteScore() {
		return favoriteScore;
	}

	public void setFavoriteScore(Integer favoriteScore) {
		this.favoriteScore = favoriteScore;
	}

	public Integer getUnderDogScore() {
		return underDogScore;
	}

	public void setUnderDogScore(Integer underDogScore) {
		this.underDogScore = underDogScore;
	}

	public Integer getGameId() {
		return gameId;
	}

	public void setGameId(Integer gameId) {
		this.gameId = gameId;
	}
	
	public String getYear() {
		return year;
	}

	public void setYear(String year) {
	   this.year = year;
	}

	public String getFavorite() {
		return favorite;
	}

	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}

	public String getUnderdog() {
		return underdog;
	}

	public void setUnderdog(String underdog) {
		this.underdog = underdog;
	}

	public boolean isChampGame() {
		return champGame;
	}

	public void setChampGame(boolean champGame) {
		this.champGame = champGame;
	}

}
