package generated.test40;

import junit.framework.*;
import java.util.*;

/**
 * Array Test
 *
 * @author Vaidas Gasiunas
 */
public class VCTestCase extends TestCase {

	public VCTestCase() {
		super("test");
	}

	public static final String expectedResult = "(0, 1)";

	public void test() {
		System.out.println("-------> VCTest 40: Type scoping with arrays");

		BoardIfc boardComp = new BoardImpl();

		BoardIfc.Board board = boardComp.makeBoard(2, 2);
		BoardIfc.BoardItem newItem = boardComp.new BoardItem();

		board.putAt(1, 1, newItem);
		newItem.moveTo(0, 1);

		BoardIfc.BoardItem item = board.itemAt(0, 1);

		String result = "(" + item.getX() + ", " + item.getY() + ")";
		System.out.println(result);

		assertEquals(expectedResult, result);

        System.out.println("-------> VCTest 40: end");
	}
}

/**
 * Board interface
 */
public cclass BoardIfc
{
	public Board makeBoard(int nRows, int nCols) { return null; }

	public cclass Board
	{
	    public BoardItem[] getBoardItems() {return null;}
	    public BoardItem[] getBoardItems(BoardItem[] items) 
	    {
	        System.out.println("BoardIfc.Board.getBoardItems()");
	        return null;
	    }
	    
		public void putAt(int x, int y, BoardItem bi) {}
        public BoardItem itemAt(int x, int y) { return null; }
    }

	public cclass BoardItem
	{
		public void moveTo(int x, int y) { }
	    public int getX() { return 0; }
	    public int getY() { return 0; }
	}
}

/**
 * Board Implementation
 */
public cclass BoardImpl extends BoardIfc
{
	public Board makeBoard(int nCols, int nRows)
	{
		Board board = new Board();
		board.init(nRows, nCols);
		return board;
	}

	public cclass Board
	{
		BoardItem[][] _matrix = null;

		public BoardItem[] getBoardItems(BoardItem[] items) 
		{
		    System.out.println("BoardImpl.Board.getBoardItems()");
		    //super.getBordItems(items);
		    return null;
		}
		
		public void init(int nCols, int nRows)
		{
			_matrix = new BoardItem[nCols][nRows];
			
			// TEST: casting of array references to the most specific
			BoardItem[] res = getBoardItems(); // as JVariableDefinition
			res = getBoardItems(); // as CjAssignmentExpression
			res = getBoardItems(new BoardItem[10]);
			try {
			    _matrix[0][0].positionAt(this, 0, 0);
			}
			catch (Exception e) {
                // do nothing
            }
		}

	    public void putAt(int x, int y, BoardItem bi)
	    {
	    	_matrix[x][y] = bi;
	    	bi.positionAt(this, x, y);
	    }

	    public void remove(BoardItem bi)
		{
		   	_matrix[bi.getX()][bi.getY()] = null;
		   	bi.positionAt(null, -1, -1);
	    }

        public BoardItem itemAt(int x, int y)
        {
        	return _matrix[x][y];
        }
    }

	public cclass BoardItem
	{
		Board _board = null;
		int _x = 0;
		int _y = 0;

		public void positionAt(Board board, int x, int y)
		{
			_board = board;
			_x = x;
			_y = y;
		}

		public void moveTo(int x, int y)
		{
			Board brd = _board;
			brd.remove(this);
			brd.putAt(x, y, this);
		}

		public int getX()
		{
			return _x;
		}

		public int getY()
		{
			return _y;
		}
	}
}