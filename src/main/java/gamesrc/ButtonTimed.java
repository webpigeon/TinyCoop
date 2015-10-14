package gamesrc;


import FastGame.ObjectTypes;

/**
 * Created by pwillic on 13/10/2015.
 */
public class ButtonTimed extends Button {
    Integer index;

    public ButtonTimed(Integer signal, Integer index) {
        super(signal);
        this.index = index;
    }

    @Override
    public void onContact(ObservableGameState state, int playerId) {
        state.activateTimedSignal(this.getSignal(), index);

    }

    @Override
    public void onContactEnd(ObservableGameState state, int pid) {
    }

    @Override
    public int getType() {
        return ObjectTypes.TIMEDBUTTON;
    }
}
