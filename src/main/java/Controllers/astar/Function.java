package Controllers.astar;

public interface Function <T,R> {
	
    public R apply(T gameNode);
    
}
