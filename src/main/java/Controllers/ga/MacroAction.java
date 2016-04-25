package Controllers.ga;

import api.Action;

/**
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 16/10/12
 */
public class MacroAction
{
    public Action action;
    public int m_repetitions;

    public MacroAction(Action action, int a_rep)
    {
        this.action = action;
        this.m_repetitions = a_rep;
    }

    public Action getAction() {
        return action;
    }

}
