/*	Ofek Gila
	February 5th, 2014
	snakeDistributable.java
	This program is a distributable version of Ofek Gila's snake game without his AI.
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class snakeDistributable	extends JApplet{
	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake Distributable");
		frame.setContentPane(new ContentPanel());
		//frame.setSize(1006,1035);
		frame.setSize(1006-150,1035-150);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void init()	{
		setContentPane(	new ContentPanel());
	}
}
class ContentPanel extends JPanel implements ActionListener, KeyListener, FocusListener, MouseListener	{
	public final int snakeSize = 40;	// Size in pixels of default square
	public char[][] field;				// A char array of the field
	public int[][] trail;				// An int array of the field with life values, how many turns there are until the piece of tail in that spot dies
	public int width, height;			// width and height of jpanel
	public int direction, mD;			// direction to move snake, mD is last successfully moved direction
	public boolean initial = true, food = false;	// Used to tell the program when to create a new food
	public Timer t = new Timer((int)1E2, this);		// Speed of snake
	public int trailLength = 0;			// How long the trail is
	public int points = 0;				// How many points the snake has
	public boolean cD = true;			// choose difficulty, don't play with this
	public boolean walls = false;		// Whether or not the walls kill or make snake appear on other side
	public int delay;					// Speed of snake
	public boolean AI = false;			// Whether or not to use artificial inteligence
	public int foodX, foodY;			// X and Y coordinants for the food
	public int AISpeed = 50;			// Speed the AI plays in, less is faster

	public ContentPanel()	{
		setBackground(Color.lightGray);

		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(this);
	}
	public void constructor()	{		// Sets default values
		width = getSize().width;
		height = getSize().height;
		field = new char[width / snakeSize][height / snakeSize];
		for (int i = 0; i < field.length; i ++)
			for (int a = 0; a < field.length; a++)	{
				if (i == 0 || i == field.length -1 || a == 0 || a == field.length - 1) field[i][a] = 'B';
				else	field[i][a] = ' ';
			}
		trail = new int[field.length][field[0].length];
		for (int i = 0; i < field.length; i ++)
			for (int a = 0; a < field.length; a++)
				trail[i][a] = -1;
		field[width/snakeSize/2][height/snakeSize/2] = 'H';
		direction = (int)(Math.random() * 4);
		placeFood('F');
	}
	public void paintComponent(Graphics g)	{	// Excecutes start sequence
		super.paintComponent(g);

		if (initial) constructor();
		initial = false;

		g.setColor(Color.black);

		g.fillRect(0,0,snakeSize, height);
		g.fillRect(0,0,width,snakeSize);
		g.fillRect(width - snakeSize, 0, snakeSize, height);
		g.fillRect(0, height - snakeSize, width, snakeSize);

		draw(g);
		//if (hasFocus())	t.start();
		if (hasFocus() && cD) chooseDifficulty(g);
		else if (hasFocus()) t.start();
		else	{
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 70));
			g.drawString("Click on the Screen!", snakeSize, snakeSize*4);
		}
	}
	public void chooseDifficulty(Graphics g)	{	// Outputs start screen
		g.setFont(new Font("Arial", Font.ITALIC, 90));
		g.setColor(Color.blue);
		g.fillRect(50,50,300,150);
		g.setColor(Color.white);
		g.drawRect(50,50,300,150);
		g.drawString("Easy", 60,160);

		g.setColor(Color.blue);
		g.fillRect(50,250,380,150);
		g.setColor(Color.white);
		g.drawRect(50,250,380,150);
		g.drawString("Medium", 60,360);

		g.setFont(new Font("Arial", Font.BOLD, 180));
		g.setColor(Color.blue);
		g.fillRect(50,450,550,200);
		g.setColor(Color.red);
		g.drawRect(50,450,550,200);
		g.drawString("HARD", 60,610);
		
		g.setFont(new Font("Arial", Font.ITALIC, 90));
		g.setColor(Color.green);
		g.fillRect(420,50,380,150);
		g.setColor(Color.white);
		g.drawRect(420,50,380,150);
		if (walls) g.drawString("Walls", 420,160);
		else g.drawString("No Walls", 420,160);

		g.setFont(new Font("Arial", Font.ITALIC, 90));
		g.setColor(Color.yellow);
		g.fillRect(420,250,380,150);
		g.setColor(Color.magenta);
		g.drawRect(420,250,380,150);
		g.drawString("AI Mode", 435,360);
	}
	public void draw(Graphics g)	{		// Draws field
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.ITALIC, 50));

		for (int row = snakeSize; row < width - snakeSize; row+=snakeSize)
				for (int col = snakeSize; col < height - snakeSize; col+= snakeSize)	{
					if (field[row/snakeSize][col/snakeSize] == 'H')	{
						g.setColor(Color.cyan);
						g.fillRect(row,col,snakeSize,snakeSize);
					}
					if (field[row/snakeSize][col/snakeSize] == 'T')	{
						Color c = new Color(255, 255, 255);
						int d = trail[row/snakeSize][col/snakeSize];
						if (d > 1)	c = c.darker();
						if (d > 3)	c = c.darker(); 
						if (d > 7)	c = c.darker(); 
						if (d > 15)	c = c.darker(); 
						if (d > 31)	c = c.darker(); 
						if (d > 63)	c = c.darker(); 
						if (d > 127)	c = c.darker(); 

						g.setColor(c);
						g.fillRect(row,col,snakeSize,snakeSize);
					}
					if (field[row/snakeSize][col/snakeSize] == 'F')	{
						g.setColor(Color.red);
						g.fillRect(row,col,snakeSize,snakeSize);
					}
					if (field[row/snakeSize][col/snakeSize] == 'B')	{
						g.setColor(Color.black);
						g.fillRect(row,col,snakeSize,snakeSize);
					}
				}	
				g.drawString(points + "", 5 + snakeSize, 50 + snakeSize);
	}
	public void actionPerformed(ActionEvent e)	{	// runs a move
		int i, a, xt, yt;
		boolean moved = false;
		for (i = 0; i < field.length; i++) {
			xt = i;
			yt = 0;
			for (a = 0; a < field[i].length; a++)	{
				yt = a;
				if (field[i][a] == 'H' && !(moved))	{
					if (AI)	direction = AIDirection(xt, yt);	//	IMPORTANT IMPORTANT --- this calls on AIDirection to decide what direction to go
					switch(direction)	{
						case 0: yt += 1; break;
						case 1:	yt -= 1; break;
						case 2: xt += 1; break;
						case 3: xt -= 1; break;
					}
					if ((xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) || field[xt][yt] == 'T')	{
						if (!(walls))	{
							if (xt == 0) xt = field.length - 2;
							else if (xt == field.length - 1) xt = 1;
							else if (yt == 0) yt = field[0].length - 2;
							else if (yt == field[0].length - 1) yt = 1;
							else {
								System.out.println("Great Job! You had:\t" + points + " points!");
								System.exit(0);
							}
						}
						else {
							System.out.println("Great Job! You had:\t" + points + " points!");
							System.exit(0);
						}
					}
					if (!(xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) && field[xt][yt] == 'B')	{
						System.out.println("Great Job! You had:\t" + points + " points!");
						System.exit(0);
					}
					mD = direction;
					field[i][a] = 'T';
					trail[i][a] = trailLength;
					if (field[xt][yt] == 'F') {	trailLength++;	addTL(); food = true;	points++; if (points % 10 == 0) placeFood('B');	}
					field[xt][yt] = 'H';
					moved = true;
				}
			}
		}
		if (food)	placeFood('F');
		deleteTail();
		food = false;
		repaint();
	}
	public int AIDirection(int x, int y)	{	//	Returns which direction the snake should go
		/*	IMPORTANT IMPORTANT

			foodX, foodY --- Coordinates of Food
			x, y         --- Current Coordinates of snake head

			use field.length to get the length of field
			NOTE: The first and last rows and columns of the field are all walls

			In the field Array: 'B' is a black wall, 'H' is the head of the snake, 'T' is a tail of the snake

			To find the length of the tail at a given point, use trail[][]

			DIRECTIONS:

			0 --- y + 1 --- down
			1 --- y - 1 --- up
			2 --- x + 1 --- right
			3 --- x - 1 --- left

			A bit of help, this line checks if there is a bad thing below the rover, and if not, moves the rover down:
			if (field[x][y+1] != 'T' && field[x][y+1] != 'B')	return 0;

			Same for the other directions:
			if (field[x][y-1] != 'T' && field[x][y-1] != 'B')	return 1;
			if (field[x+1][y] != 'T' && field[x+1][y] != 'B')	return 2;
			if (field[x-1][y] != 'T' && field[x-1][y] != 'B')	return 3;
		*/
		return 0;
	}
	public void placeFood(char c)	{	// Places a piece of food or wall down randomly
		int ran1, ran2;
		while (true)	{
			ran1 = (int)(Math.random() * field.length);
			ran2 = (int)(Math.random() * field.length);
			if (field[ran1][ran2] == ' ' && notGonnaHitMeInTheNextMove(ran1, ran2)) break;
		}
		field[ran1][ran2] = c;
		if (c == 'F') foodX = ran1; foodY = ran2;
	}
	public boolean notGonnaHitMeInTheNextMove(int x, int y)	{	// Makes sure that the placed piece is not directly in front of you
		if (mD == 0)	if (field[x][y-1] == 'H') return false;
		if (mD == 1) if (field[x][y + 1] == 'H') return false;
		if (mD == 2) if (field[x - 1][y] == 'H') return false;
		if (mD == 3) if (field[x + 1][y] == 'H') return false;
		return true;
	}
	public void addTL()	{	// Used when collecting food
		int i, a;
		for (i = 0; i < field.length; i ++)
			for (a = 0; a < field.length; a++)
				if (field[i][a] == 'T')
					if (trail[i][a] != trailLength)	trail[i][a]++;
	}
	public void deleteTail()	{	// Deletes the last piece of trail
		int i, a;
		for (i = 0; i < field.length; i ++)
			for (a = 0; a < field.length; a++)
				if (field[i][a] == 'T')	{
					if (trail[i][a] == 0)	field[i][a] = ' ';
					trail[i][a]--;
				}
	}
	public void focusGained(FocusEvent evt) {
		repaint();
	}
	public void focusLost(FocusEvent evt) {	
	//	repaint();
	}
	public void keyTyped(KeyEvent evt) {	// pauses or unpauses game
		char key = evt.getKeyChar();
		if (key == 'p' || key == 'P')	t.stop();
		if (key == 'u' || key == 'U') {	t.setDelay(delay);	repaint();	}
	}
	public void keyPressed(KeyEvent evt) {	
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_LEFT && mD	!= 2)	{
			direction = 3;
		}
		else if (key == KeyEvent.VK_UP && mD != 0)	{
			direction = 1;
		}
		else if (key == KeyEvent.VK_DOWN && mD != 1)	{
			direction = 0;
		}
		else if (key == KeyEvent.VK_RIGHT && mD != 3)	{
			direction = 2;
		}
		else Toolkit.getDefaultToolkit().beep();
	}
	public void keyReleased(KeyEvent evt) {	}
	public void mousePressed(MouseEvent evt) {	// Checks where on the screen the user clicked and does stuff accordingly
		int x = evt.getX();
		int y = evt.getY();
		if (hasFocus())	{
			if (x <= 350 && x >= 50 && y >= 50 && y <= 200 && cD)	{
				delay = (int)1.5E2;
				t.setDelay(delay);
				cD = false;
				repaint();
			}
			if (x <= 430 && x >= 50 && y >= 250 && y <= 400 && cD)	{
				delay = (int)1E2;
				t.setDelay(delay);
				cD = false;
				repaint();
			}
			if (x <= 600 && x >= 50 && y >= 450 && y <= 650 && cD)	{
				delay = (int)0.5E2;
				t.setDelay(delay);
				cD = false;
				repaint();
			}
			if (x <= 800 && x >= 420 && y>= 50 && y <= 200 && cD)	{
				if (walls) walls = false;
				else walls = true;
				repaint();
			}
			if (x <= 800 && x >= 420 && y>=250 && y <= 400 && cD)	{
				delay = AISpeed;
				t.setDelay(delay);
				cD = false;
				AI = true;
				repaint();
			}
		}
		else	requestFocus();
	}
	public void mouseEntered(MouseEvent evt) { } 
    public void mouseExited(MouseEvent evt) { } 
    public void mouseReleased(MouseEvent evt) { } 
    public void mouseClicked(MouseEvent evt) { }
}