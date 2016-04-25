package Controllers.ga;

import actions.Action;
import gamesrc.GameState;

import java.util.ArrayList;
import java.util.Random;

/**
 * PTSP-Competition
 * Created by Diego Perez, University of Essex.
 * Date: 17/10/12
 */
public class GA
{
    public final int NUM_INDIVIDUALS = 10;
    static public int NUM_ACTIONS_INDIVIDUAL = 15;
    static public int TOURNAMENT_SIZE = 3;
    public final int ELITISM = 2;
    public ArrayList<MacroAction> m_actionList;             //List of available actions to govern the ship.
    public Random m_rnd;                                    //Random number generator
    public int m_numGenerations;

    public GAIndividual[] m_individuals;

    public GA(Action[] actions)
    {
        m_rnd = new Random();

        m_actionList = new ArrayList<MacroAction>();
        for (Action action : actions) {
            m_actionList.add(new MacroAction(action, GAConstants.MACRO_ACTION_LENGTH));
        }

        m_individuals = new GAIndividual[NUM_INDIVIDUALS];
        //init(a_gameState);
    }

    public void init(GameState a_gameState, boolean first)
    {
        //System.out.println(" --------- Starting GA --------- ");
        m_numGenerations = 0;
        for(int i = 0; i < NUM_INDIVIDUALS; ++i)
        {
            m_individuals[i] = new GAIndividual(NUM_ACTIONS_INDIVIDUAL);
            m_individuals[i].randomize(m_rnd, a_gameState, m_actionList.size());
            m_individuals[i].evaluate(a_gameState, first);
            //System.out.format("individual i: " + i + ", fitness: %.3f, actions: %s \n", m_individuals[i].m_fitness, m_individuals[i].toString());
        }

        sortPopulationByFitness();
    }

    public MacroAction run(GameState a_gameState, int generations, boolean first)
    {
        int currGen = 0;
        while(currGen < generations)
        {
            GAIndividual[] nextPop = new GAIndividual[m_individuals.length];
            //System.out.println(" --------- New generation " + m_numGenerations + " --------- ");

            int i;
            for(i = 0; i < ELITISM; ++i)
            {
                nextPop[i] = m_individuals[i];
            }

            for(;i<m_individuals.length;++i)
            {
                //System.out.println("######################");
                nextPop[i] = breed(); // m_individuals[i-ELITISM].copy();
                nextPop[i].mutate(m_rnd, a_gameState);
                //System.out.print("c-m: " + nextPop[i].toString());
                nextPop[i].evaluate(a_gameState, first);
                //System.out.println(", " + nextPop[i].m_fitness);
            }

            m_individuals = nextPop;
            sortPopulationByFitness();

            /*for(i = 0; i < m_individuals.length; ++i)
                System.out.format("individual i: " + i + ", fitness: %.3f, actions: %s \n", m_individuals[i].m_fitness, m_individuals[i].toString());     */


            m_numGenerations++;
            currGen++;
        }

        //System.out.println("number of generations this tick: "+currGen);

        //Return the first macro action of the best individual.
       /* System.out.println(m_numGenerations);
        System.out.println("Best: " + m_individuals[0]);
        Game f =  m_individuals[0].evaluate(a_gameState,m_gameEvaluator);

        System.out.println(" --------- Last generation " + m_numGenerations + " " +  f.getWaypointsVisited() + "--------- ");
        for(int i = 0; i < m_individuals.length; ++i)
                 System.out.format("individual i: " + i + ", fitness: %.3f, actions: %s \n", m_individuals[i].m_fitness, m_individuals[i].toString());
         */

        return new MacroAction(m_individuals[0].m_genome[0],GAConstants.MACRO_ACTION_LENGTH);
    }


    private GAIndividual breed()
    {
        GAIndividual gai1 = getParent(null);        //First parent.
        GAIndividual gai2 = getParent(gai1);        //Second parent.

        //System.out.println();
        //System.out.println("p1:  " + gai1.toString());
        //System.out.println("p2:  " + gai2.toString());

        GAIndividual newBorn = gai1.uniformCross(gai2, m_rnd);             //Cross and return.
        //System.out.println("c:   " + newBorn.toString());
        return newBorn;
    }

    private GAIndividual getParent(GAIndividual first)
    {
        GAIndividual best = null;
        int[] tour= new int[TOURNAMENT_SIZE];
        for(int i = 0; i < TOURNAMENT_SIZE; ++i)
            tour[i] = -1;

        int i = 0;
        while(tour[TOURNAMENT_SIZE-1] == -1)
        {
            int part = (int) (m_rnd.nextFloat()*NUM_INDIVIDUALS);
            boolean valid = m_individuals[part] != first;  //Check it is not the same selected first.
            for(int k = 0; valid && k < i; ++k)
            {
                valid = (part != tour[k]);                 //Check it is not in the tournament already.
            }

            if(valid)
            {
                tour[i++] = part;
                if(best == null || (m_individuals[part].m_fitness > best.m_fitness))
                    best = m_individuals[part];
            }
        }

        /*for(int j = 0; j < TOURNAMENT_SIZE; ++j)
            System.out.format("%d (%.3f) ", tour[j], m_individuals[tour[j]].m_fitness);
        System.out.print(", ");    */

        return best;
    }

    private void sortPopulationByFitness() {
        for (int i = 0; i < m_individuals.length; i++) {
            for (int j = i + 1; j < m_individuals.length; j++) {
                if (m_individuals[i].m_fitness < m_individuals[j].m_fitness) {
                    GAIndividual gcache = m_individuals[i];
                    m_individuals[i] = m_individuals[j];
                    m_individuals[j] = gcache;
                }
            }
        }
    }

}
