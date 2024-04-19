import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MazeSolver implements IMazeSolver {
	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int[][] DELTAS = new int[][] {
		{ -1, 0 }, // North
		{ 1, 0 }, // South
		{ 0, 1 }, // East
		{ 0, -1 } // West
	};

	private Maze maze;
	private boolean[][] visited;
	private int[][] minSteps;

	public MazeSolver() {
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		visited = new boolean[maze.getRows()][maze.getColumns()];
		minSteps = new int[maze.getRows()][maze.getColumns()];
	}
	private boolean canGo(int row, int col, int dir) {
		// not needed since our maze has a surrounding block of wall
		// but Joe the Average Coder is a defensive coder!
		if (row + DELTAS[dir][0] < 0 || row + DELTAS[dir][0] >= maze.getRows()) return false;
		if (col + DELTAS[dir][1] < 0 || col + DELTAS[dir][1] >= maze.getColumns()) return false;

		switch (dir) {
			case NORTH:
				return !maze.getRoom(row, col).hasNorthWall();
			case SOUTH:
				return !maze.getRoom(row, col).hasSouthWall();
			case EAST:
				return !maze.getRoom(row, col).hasEastWall();
			case WEST:
				return !maze.getRoom(row, col).hasWestWall();
		}

		return false;
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}

		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		// set all visited flag to false
		// before we begin our search
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				this.visited[i][j] = false;
				minSteps[i][j] = -1;
				maze.getRoom(i, j).onPath = false;
			}
		}
		return solve(startRow, startCol, endRow, endCol);
	}

	public void setOnPath(ArrayList<int[]> currentP) {
		for (int[] a: currentP) {
			maze.getRoom(a[0], a[1]).onPath = true;
		}
	}
	public Integer solve(int startRow, int startCol, int endRow, int endCol) {
		Queue<ArrayList<int[]>> queue = new LinkedList<>();
		ArrayList<int[]> initalPath = new ArrayList<>();
		initalPath.add(new int[]{startRow, startCol});
		queue.offer(initalPath);
		minSteps[startRow][startCol] = 0;
		Integer finalDistance = null;
		boolean firstTimeFlag = true;
		while (!queue.isEmpty()) {
			ArrayList<int[]> currentP = queue.poll();
			int s = currentP.size();
			int[] currentR = currentP.get(s - 1);
			int row = currentR[0];
			int col = currentR[1];
			visited[row][col] = true;
			if (row == endRow && col == endCol && firstTimeFlag) {
				firstTimeFlag = false;
				setOnPath(currentP);
				finalDistance = s-1;
			}

			for (int d = 0; d < 4; d++) {
				if (canGo(row, col, d)) {
					int newRow = row + DELTAS[d][0];
					int newCol = col + DELTAS[d][1];
					if (!visited[newRow][newCol]) {
						ArrayList<int[]> newP = (ArrayList<int[]>) currentP.clone();
						newP.add(new int[]{newRow, newCol});
						minSteps[newRow][newCol] = newP.size()-1;
						queue.offer(newP);
					}
				}
			}
		}
		return finalDistance;
	}

	@Override
	public Integer numReachable(int k) throws Exception {
		if (maze == null) {
			throw new Exception("illegal maze");
		}
		int rn = 0;
		for (int i = 0; i < minSteps.length; i++) {
			for (int j = 0; j < minSteps[i].length; j++) {
				if (minSteps[i][j] == k) {
					rn += 1;
				}
			}
		}
		return rn;
	}

	public static void main(String[] args) {
		// Do remember to remove any references to ImprovedMazePrinter before submitting
		// your code!
		try {
			Maze maze = Maze.readMaze("maze-sample.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);

			System.out.println(solver.pathSearch(0, 0, 3, 3));
			MazePrinter.printMaze(maze);

			for (int i = 0; i <= 9; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
