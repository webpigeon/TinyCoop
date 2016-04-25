package api;

import gamesrc.SimpleGame;

public interface GameObject {

	boolean isWalkable(SimpleGame state, int playerId);

	void onContact(SimpleGame state, int playerId);

	void onContactEnd(SimpleGame state, int pid);

	int getSignal();

	int getType();

}