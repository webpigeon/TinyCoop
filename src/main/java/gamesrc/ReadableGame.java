package gamesrc;

import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import Controllers.ArrowController;
import Controllers.Controller;
import Controllers.MCTS;
import Controllers.RandomController;
import Controllers.WASDController;
import FastGame.Action;
import FastGame.CoopGame;

public class ReadableGame {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		GameLevel level = buildLevel("maps/level1.txt");
		GameState initalStateS = new SimpleGame(level);
		
		Controller c1 = new WASDController();
		Controller c2 = new ArrowController();
		
		runGraphicalGame(initalStateS, c1, c2);
	}
	
	public static void runGraphicalGame(GameState initial, Controller p1, Controller p2) throws InterruptedException {
		System.out.println("graphical game");
		JFrame frame = new JFrame("TinyCoop - GRIDWORLD");
		frame.setPreferredSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		ObservableGameState state = (ObservableGameState)initial.getClone();
		JComponent viewer = new Viewer(state);
		viewer.setFocusable(true);
		
		if (p1 instanceof KeyListener) {
			viewer.setFocusable(true);
			viewer.requestFocus();
			viewer.addKeyListener((KeyListener)p1);
		}
		
		if (p2 instanceof KeyListener) {
			viewer.setFocusable(true);
			viewer.requestFocus();
			viewer.addKeyListener((KeyListener)p2);
		}

		frame.add(viewer);
		
		frame.pack();
		frame.setVisible(true);
		
		while(!state.hasWon()) {
			Action a1 = p1.get(state.getClone());
			Action a2 = p2.get(state.getClone());
			state.update(a1, a2);
			frame.repaint();
			Thread.sleep(1000);
		}
		
		System.out.println("game over");
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
