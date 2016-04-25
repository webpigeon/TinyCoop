package gamesrc;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import Controllers.Controller;
import Controllers.MCTS;
import Controllers.NoOp;
import Controllers.PassiveController;
import Controllers.RandomController;
import Controllers.SortOfRandomController;
import Controllers.WASDController;
import Controllers.VariGA.VariGA;
import actions.Action;
import gamesrc.viewer.Viewer;

public class ReadableGame {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		String[] levels = new String[]{
			//"maps/level8.txt",
			"maps/level1.txt",
			"maps/level2.txt",
			"maps/level3.txt",
			"maps/level4.txt",
			"maps/level5.txt",
			"maps/level6.txt",
			"maps/level7.txt"
		};
		
		GameLevel[] levelList = new GameLevel[levels.length];
		for (int i=0; i<levelList.length; i++) {
			levelList[i] = buildLevel(levels[i]);
		}
		
		System.out.println("graphical game");
		JFrame frame = new JFrame("TinyCoop - GRIDWORLD");
		frame.setPreferredSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Viewer viewer = new Viewer(null);
		viewer.setFocusable(true);
		frame.add(viewer);
		
		frame.pack();
		frame.setVisible(true);
		
		/*List<Controller> controllers = Arrays.asList(
				new MCTS(true, 500),
				new MCTS(true, 250),
				new RandomController(),
				new WASDController()
				);*/
		
		Controller c1 = new SortOfRandomController();
		//Controller c1 = new MCTS(true, 5000);
		for (GameLevel level : levelList) {
			GameState initalStateS = new SimpleGame(level);
			
			//for (Controller c1 : controllers) {
				Controller c2 = new PassiveController();
				//Controller c2 = new RandomController();
				//Controller c2 = new NoOp();
				
				frame.setTitle("TinyCoop "+c1.getSimpleName()+" and "+c2.getSimpleName());
				
				System.out.println(level);
				runGraphicalGame(initalStateS, viewer, c1, c2);
			//}
		}
	}
	
	public static void runGraphicalGame(GameState initial, Viewer viewer, Controller p1, Controller p2) throws InterruptedException {
		
		ObservableGameState state = (ObservableGameState)initial.getClone();

		viewer.setState(state);
		
		viewer.setFocusable(true);
		if (p1 instanceof KeyListener) {
			viewer.setFocusable(true);
			viewer.requestFocus();
			viewer.addKeyListener((KeyListener)p1);
			if (p1 instanceof MouseListener) {
				viewer.addMouseListener((MouseListener)p1);
			}
		}
		
		if (p2 instanceof KeyListener) {
			viewer.setFocusable(true);
			viewer.requestFocus();
			viewer.addKeyListener((KeyListener)p2);
			if (p1 instanceof MouseListener) {
				viewer.addMouseListener((MouseListener)p2);
			}
		}
		
		p1.startGame(0);
		p2.startGame(1);
		
		int ticks = 0;
		while(!state.hasWon()) {
			Action a1 = p1.get(state.getClone());
			Action a2 = p2.get(state.getClone());
			state.update(a1, a2);
			//viewer.repaint();
			//Thread.sleep(500);
			ticks++;
		}
		
		System.out.println("Complete: "+ticks);
	}
	
	public static double runGames(GameState initialState, int runs, int tickLimit, Controller p1, Controller p2) {
		List<Long> timings = new ArrayList<Long>(runs);
		
		for (int i=0; i<runs; i++) {
			int ticks = 0;
					
			long startPoint = System.nanoTime();
			
			GameState game = initialState.getClone();
			while(!game.hasWon() && ticks < tickLimit) {
				Action act1 = p1.get(game.getClone());
				Action act2 = p2.get(game.getClone());
				game.update(act1, act2);
				ticks++;
				System.out.println(ticks+" "+act1+" "+act2);
			}
			
			long duration = System.nanoTime() - startPoint;
			timings.add(duration);
		}
		
		long totalDuration = 0;
		for (Long duration : timings) {
			totalDuration += duration;
		}
		
		return totalDuration/(timings.size()*1.0);
	}
	
	public static GameLevel buildLevel(String filename) throws IOException {
		return LevelParser.buildParser(filename);
	}

}
