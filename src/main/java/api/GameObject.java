package api;

public interface GameObject {

	int getSignal();

	int getType();

	boolean isWalkable(ObservableGameState state, int playerId);

}