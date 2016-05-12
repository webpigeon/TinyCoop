package api;

import gamesrc.level.ObjectType;

public interface GameObject {

	int getSignal();

	ObjectType getType();

	boolean isWalkable(ObservableGameState state, int playerId);

}