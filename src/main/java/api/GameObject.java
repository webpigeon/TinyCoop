package api;

public interface GameObject {

	boolean isWalkable(ObservableGameState state, int playerId);

	int getSignal();

	int getType();

}