package uk.me.webpigeon.phd.tinycoop.api;

import uk.me.webpigeon.phd.tinycoop.engine.level.ObjectType;

public interface GameObject {

	int getSignal();

	ObjectType getType();

	boolean isWalkable(ObservableGameState state, int playerId);

}