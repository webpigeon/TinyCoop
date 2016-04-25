package Controllers.ga;

import actions.Action;
import gamesrc.GameState;

import java.util.Random;

/**
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class GAIndividual
{
    public Action[] m_genome;
    public boolean isFirst;
    public double m_fitness;
    public final double MUTATION_PROB = 0.2; //0.834=5/6   //0.2;
    public Random random;

    public GAIndividual(int a_genomeLength)
    {
        m_genome = new Action[a_genomeLength];
        m_fitness = 0;
        this.random = new Random();
    }

    public void randomize(Random a_rnd, GameState state, int a_numActions)
    {
        for(int i = 0; i < m_genome.length; ++i)
        {   
            m_genome[i] = getAction(a_rnd, state);
        }
    }

    public Action getAction(Random rnd, GameState state) {
    	Action[] legalActions = state.getLegalActions(isFirst?0:1);
        return legalActions[rnd.nextInt(legalActions.length)];
    }
    
    public Action getOppAction(Random rnd, GameState state) {
    	Action[] legalActions = state.getLegalActions(-1);
        return legalActions[rnd.nextInt(legalActions.length)];
    }
    
    public GameState evaluate(GameState a_gameState, boolean isFirst)
    {
        GameState thisGameCopy = a_gameState.getClone();
        this.isFirst = isFirst;
        boolean end = false;
        for(int i = 0; i < m_genome.length; ++i)
        {
            Action thisAction = m_genome[i];
            for(int j =0; !end && j < GAConstants.MACRO_ACTION_LENGTH; ++j)
            {
                if (isFirst) {
                    thisGameCopy.update(thisAction, getOppAction(random, a_gameState));
                } else {
                    thisGameCopy.update(getOppAction(random, a_gameState), thisAction);
                }
                end = thisGameCopy.hasWon();
            }
        }
        m_fitness = thisGameCopy.getScore();
        return thisGameCopy;
    }

    public void mutate(Random a_rnd, GameState state)
    {
        for (int i = 0; i < m_genome.length; i++) {
            if(a_rnd.nextDouble() < MUTATION_PROB)
            {
                m_genome[i] = getOppAction(a_rnd, state);
            }
        }
    }

    /**
     * Returns a NEW INDIVIDUAL, crossed uniformly from this and the received parent.
     */
    public GAIndividual uniformCross(GAIndividual ind, Random a_rnd)
    {
        GAIndividual newInd = new GAIndividual(this.m_genome.length);

        for(int i = 0; i < this.m_genome.length; ++i)
        {
            if(a_rnd.nextFloat() < 0.5f)
            {
                newInd.m_genome[i] = this.m_genome[i];
            }else{
                newInd.m_genome[i] = ind.m_genome[i];
            }
        }

        return newInd;
    }


    public GAIndividual copy()
    {
        GAIndividual gai = new GAIndividual(this.m_genome.length);
        for(int i = 0; i < this.m_genome.length; ++i)
        {
            gai.m_genome[i] = this.m_genome[i];
        }
        return gai;
    }

    @Override
	public String toString()
    {
        String st = new String();
        for(int i = 0; i < m_genome.length; ++i)
            st += m_genome[i];
        return st;
    }


}
