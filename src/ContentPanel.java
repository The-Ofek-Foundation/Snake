import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.*;

public class ContentPanel extends JPanel implements ActionListener, KeyListener, FocusListener, MouseListener	{
	public final int snakeSize = 40;
	public char[][] field;
	public int[][] trail;
	public int width, height;
	public int direction, mD, mD2;
	public boolean initial = true, food;
	public Color color;
	public Timer t = new Timer((int)1E2, this);
	public int trailLength, trailLength2;
	public int points, points2;
	public boolean cD;
	public boolean walls = false;
	public int delay;
	public boolean AI, AI2 = false;
	public double deltaX, deltaY;
	public int foodX, foodY;
	public int d0, d1, d2, d3;
	public boolean edgeHugger = true;
	public boolean mazeSolver = true;
	public int waitToHug, wTH;
	public boolean onSite;
	public int keepAt;
	public int AINormalSpeed;
	public int AIHuggerSpeed;
	public boolean accumulateSpeed;
	public Graphics g;
	public boolean dead, dead2;
	public int direction2;
	public boolean twoPeeps = false;
	public boolean tie;
	public boolean chooseD;
	boolean again = false;
	boolean gonnadie;

	public ContentPanel()	{
		setBackground(Color.lightGray);

		addKeyListener(this);
		addFocusListener(this);
		addMouseListener(this);
	}
	public void constructor()	{
		dead = onSite = AI = food = tie = dead2 = gonnadie = false;
		cD  = true;
		AINormalSpeed = 50;
		AIHuggerSpeed = 30;
		t.setDelay((int)1E2);
		trailLength = trailLength2 = 1;
		points = points2 = 0;
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
		field[width/snakeSize/2 + 3][height/snakeSize/2 - 3] = 'H';
		direction = 0;
		direction2 = 1;
		placeFood('F');
	}
	public void paintComponent(Graphics a)	{
		g = a;
		super.paintComponent(a);

		if (initial)	{	
			constructor();
			/*if (edgeHugger) delay = AIHuggerSpeed;
			else			delay = AINormalSpeed;
			t.setDelay(delay);
			cD = false;
			AI = true;*/
		}
		initial = false;

		if (twoPeeps && cD) field[width/snakeSize/2 - 3][height/snakeSize/2 + 3] = '2';

		g.setColor(Color.black);

		g.fillRect(0,0,snakeSize, height);
		g.fillRect(0,0,width,snakeSize);
		g.fillRect(width - snakeSize, 0, snakeSize, height);
		g.fillRect(0, height - snakeSize, width, snakeSize);

		draw();
		//if (hasFocus())	t.start();
		if (dead || dead2)	{
			//draw();
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 70));
			if (tie) g.drawString("You're both dead!", snakeSize - 40, snakeSize*4);
			else if (dead && twoPeeps) g.drawString("You lost with " + points + " points :/", snakeSize - 40, snakeSize*4);
			else if (dead2) g.drawString("You won with: " + points + " points!", snakeSize - 40, snakeSize*4);
			else g.drawString("Great Job! You had: " + points + " points!", snakeSize - 40, snakeSize*4);

			g.setColor(Color.blue);
			g.fillRect(50,250,480,150);
			g.setColor(Color.white);
			g.drawRect(50,250,480,150);
			g.drawString("Play Again?", 60,350);

			g.setColor(Color.green);
			g.fillRect(50,450,180,150);
			g.setColor(Color.cyan);
			g.drawRect(50,450,180,150);
			g.drawString("Yes", 60,550);

			g.setColor(Color.red);
			g.fillRect(250,450,180,150);
			g.setColor(Color.cyan);
			g.drawRect(250,450,180,150);
			g.drawString("No", 260,550);
		}
		else if (again)	{
			t.setDelay(0);
			cD = false;
			AI = true;
		}
		if (hasFocus() && cD) chooseDifficulty();
		else if (hasFocus() || again)	t.start();
		else	{
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 70));
			g.drawString("Click on the Screen!", snakeSize, snakeSize*4);
		}
	}
	public void chooseDifficulty()	{
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

		g.setFont(new Font("Arial", Font.ITALIC, 60));
		g.setColor(Color.yellow);
		g.fillRect(600,450,200,200);
		g.setColor(Color.magenta);
		g.drawRect(600,450,200,200);
		if (mazeSolver)	{	g.drawString("Maze", 620, 510); g.drawString("Solver", 605, 600);	}
		else if (edgeHugger) {	g.drawString("Edge", 635, 510); g.drawString("Hugger", 600, 600);	}
		else {	g.drawString("Middle", 610, 510);	g.drawString("Player", 610, 600);	}
		
		g.setFont(new Font("Arial", Font.ITALIC, 60));
		g.setColor(Color.gray);
		g.fillRect(50,700,540,100);
		g.setColor(Color.blue);
		g.drawRect(50,700,540,100);
		if (accumulateSpeed) {	g.drawString("Accumulate", 55, 760); g.drawString("Speed", 405, 760);	}
		else {	g.drawString("Set", 55, 760);	g.drawString("Speed", 405, 760);	}

		g.setFont(new Font("Arial", Font.BOLD, 60));
		g.setColor(Color.yellow);
		g.fillRect(50,820,440,100);
		g.setColor(Color.green);
		g.drawRect(50,820,440,100);
		if (twoPeeps) 	g.drawString("Multi Player", 55, 890);
		else 	g.drawString("Single Player", 55, 890);

		if (twoPeeps)	{
			g.setFont(new Font("Arial", Font.BOLD, 60));
			g.setColor(Color.yellow);
			g.fillRect(550,820,310,100);
			g.setColor(Color.green);
			g.drawRect(550,820,310,100);
			if (AI2) 	g.drawString("P2 AI", 555, 890);
			else 	g.drawString("P2 Human", 555, 890);
		}

	}
	public void killTheSnake(boolean secondPeep)	{
		System.out.println("Great Job! You had:\t" + points + " points!");
		writePoints();
		//System.out.println(points);
		if (points < 160 && again)	{
			initial = true;
			t.stop();
			repaint();
		}
		else {
			if (secondPeep) dead2 = true;
			else dead = true;
			t.stop();
			repaint();
		}
		//System.exit(0);
	}
	PrintWriter output;
	Scanner input;
	public void writePoints()	{
		try {
			input = new Scanner(new File("Score.txt"));// tries to open the file
		}
		catch (FileNotFoundException e)	{
			System.err.println("ERROR: Cannot open file Score.txt");
			System.exit(97);
		}		
		String str = "";
		while (input.hasNext())
			str += input.nextLine() + "\n";
		//System.out.println(str);
		input.close();
		try {
			output = new PrintWriter(new File("Score.txt"));
		}
		catch (IOException e)	{
			System.err.println("ERROR: Cannot open file Score.txt");
			System.exit(99);
		}
		
		output.println(points);	
		output.println(str);
		output.close();
	}
	public void draw()	{
		g.setColor(Color.white);
		g.setFont(new Font("Arial", Font.ITALIC, 50));

		for (int row = snakeSize; row < width - snakeSize; row+=snakeSize)
				for (int col = snakeSize; col < height - snakeSize; col+= snakeSize)	{
					if (field[row/snakeSize][col/snakeSize] == 'H')	{
						g.setColor(Color.cyan);
						g.fillRect(row,col,snakeSize,snakeSize);
					}
					if (twoPeeps && field[row/snakeSize][col/snakeSize] == '2') {
						g.setColor(Color.green);
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
						if (d > 255)	c = c.darker();

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
				g.setColor(Color.cyan);
				g.drawString(points + "", 5 + snakeSize, 50 + snakeSize);
				g.setColor(Color.green);
				g.drawString(points2 + "", width - 2 * snakeSize, 50 + snakeSize);
				//g.drawString("snake moves every " + t.getDelay() + " ms.", 105 + snakeSize, 50 + snakeSize);
	}
	public void actionPerformed(ActionEvent e)	{
		int i, a, xt, yt;
		boolean moved = false, moved2 = false;
		for (i = 0; i < field.length; i++) {
			for (a = 0; a < field[i].length; a++)	{
				xt = i;
				yt = a;
				if (field[i][a] == 'H' && !(moved))	{
					if (AI)	direction = AIDirection(xt, yt, 'H');
					switch(direction)	{
						case 0: yt += 1; break;
						case 1:	yt -= 1; break;
						case 2: xt += 1; break;
						case 3: xt -= 1; break;
					}
					if (dead || dead2) return;
					if ((xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) || field[xt][yt] == 'T' || field[xt][yt] == '2')	{
						if (!(walls))	{
							if (xt == 0) xt = field.length - 2;
							else if (xt == field.length - 1) xt = 1;
							else if (yt == 0) yt = field[0].length - 2;
							else if (yt == field[0].length - 1) yt = 1;
							else killTheSnake(false);
						}
						//else if (field[xt][yt] == '2') tie = true;
						else killTheSnake(false);
					}
					if (!(xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) && field[xt][yt] == 'B')
						killTheSnake(false);
					if (dead || dead2) return;
					mD = direction;
					field[i][a] = 'T';
					trail[i][a] = trailLength;
					if (field[xt][yt] == 'F') {	trailLength++;	addTL(); food = true;	points++; if ((points + points2) % 10 == 0) placeFood('B');	accumulateSpeed();	}
					field[xt][yt] = 'H';
					moved = true;
				}
				if (field[i][a] == '2' && !(moved2) && twoPeeps)	{
					if (AI2) direction2 = AIDirection(xt, yt, '2');
					switch(direction2)	{
						case 0: yt += 1; break;
						case 1:	yt -= 1; break;
						case 2: xt += 1; break;
						case 3: xt -= 1; break;
					}
					if (dead2 || dead) return;
					if ((xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) || field[xt][yt] == 'T' || field[xt][yt] == 'H')	{
						if (!(walls))	{
							if (xt == 0) xt = field.length - 2;
							else if (xt == field.length - 1) xt = 1;
							else if (yt == 0) yt = field[0].length - 2;
							else if (yt == field[0].length - 1) yt = 1;
							else killTheSnake(true);
						}
						//if (field[xt][yt] == 'H') tie = true;
						else killTheSnake(true);
					}
					if (!(xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) && field[xt][yt] == 'B')
						killTheSnake(true);
					if (dead || dead2) return;
					mD2 = direction2;
					field[i][a] = 'T';
					trail[i][a] = trailLength2;
					if (field[xt][yt] == 'F') {	trailLength2++;	addTL(); food = true;	points2++; if ((points + points2) % 10 == 0) placeFood('B');	accumulateSpeed();	}
					field[xt][yt] = '2';
					moved2 = true;
				}
			}
		}
		if (dead || dead2) return;
		if (food)	placeFood('F');
		deleteTail();
		food = false;
		repaint();
	}
	public void accumulateSpeed()	{
		if (points == 2 || points == 4 || points == 8 || points == 16 || points == 32 || points == 64 || points == 128)
			if (t.getDelay() != 1 && accumulateSpeed) 
				t.setDelay(t.getDelay() - 4);
	}
	public void getDValues(int x, int y)	{
		int i;
		for(i = 1; i > 0; i++)	{
			if (trail[x][y+i] >= i || field[x][y+i] == 'B' || i > field.length - 2 || field[x][y+i] == 'F' || (field[x][y+i] == 'H' || field[x][y+i] == '2' && twoPeeps)) break;
		}
		d0 = i;
		if (field[x][y+i] == 'F'  && !(foodSuicide())) {	d0 = 50;	}
		for(i = 1; i > 0; i++)
			if (trail[x][y-i] >= i || field[x][y-i] == 'B' || i > field.length - 2 || field[x][y-i] == 'F' || (field[x][y-i] == 'H' || field[x][y-i] == '2' && twoPeeps)) break;
		d1 = i;
		if (field[x][y-i] == 'F' && !(foodSuicide())) {d1 = 50;	}
		for(i = 1; i > 0; i++)
			if (trail[x+i][y]  >= i || field[x+i][y] == 'B' || i > field.length - 2 || field[x+i][y] == 'F' || (field[x+i][y] == 'H' || field[x+i][y] == '2' && twoPeeps)) break;
		d2 = i;
		if (field[x+i][y] == 'F' && !(foodSuicide())) {d2 = 50;	}
		for(i = 1; i > 0; i++)
			if (trail[x-i][y]  >= i || field[x-i][y] == 'B' || i > field.length - 2 || field[x-i][y] == 'F' || (field[x-i][y] == 'H' || field[x-i][y] == '2' && twoPeeps)) break;
		d3 = i;
		if (field[x-i][y] == 'F' && !(foodSuicide())) {d3 = 50;	}
		//System.out.println(d0+ " " + d1 + " " + d2 + " " + d3);
	}
	
	public boolean foodSuicide()	{
		int walls = 0;
		if (field[foodX][foodY+1] == 'T' || field[foodX][foodY+1] == 'B') walls++;
		if (field[foodX][foodY-1] == 'T' || field[foodX][foodY-1] == 'B') walls++;
		if (field[foodX+1][foodY] == 'T' || field[foodX+1][foodY] == 'B') walls++;
		if (field[foodX-1][foodY] == 'T' || field[foodX-1][foodY] == 'B') walls++;
		if (walls > 2) return true;
		return false;
	}
	public int normalAI(int x, int y, char s)	{
		int d;
		deltaX = foodX - x;
		deltaY = foodY - y;
		//if (Math.abs(deltaX) > Math.abs(deltaY))	{
		if (foodX != x)	{
			if (deltaX > 0) d = 2;
			else d = 3;
		}
		else if (deltaY > 0) d = 0;
		else d = 1;
		int safety;
		//Scanner keyboard = new Scanner(System.in);
		//System.out.println(d0+ " " + d1 + " " + d2 + " " + d3 + " " + d);
		//String S = keyboard.nextLine();
		
		if (foodX == x || foodY == y) {
			if (d == 2 && doNotGoHere(x+1, y, s))	{
				if (d0 > d1) d = 0;
				else d = 1;
			}
			else if (d == 3 && doNotGoHere(x-1, y, s))	{
				if (d0 > d1) d = 0;
				else d = 1;
			}
			else if (d == 0 && doNotGoHere(x, y+1, s))	{
				if (d2 > d3) d = 2;
				else d = 3;
			}
			else if (d == 1 && doNotGoHere(x, y-1, s))	{
				if (d2 > d3) d = 2;
				else d = 3;
			}
		}
		//System.out.print("0 ");
		if (!twoPeeps)	{
		for (safety = 6; safety > 3; safety --)	{
			if ((d == 2 && d2 < safety) || (d == 3 && d3 < safety))	{
				if (deltaY > 0 && d0 >= safety && !doNotGoHere(x, y+1, s))  return 0;
				else if (d1 >= safety && !doNotGoHere(x, y-1, s)) return 1;
				//System.out.println(d);
				//break;
			}
			if ((d == 0 && d0 < safety) || (d == 1 && d1 < safety))	{
				if (deltaX > 0 && d2 >= safety && !doNotGoHere(x+1, y, s))  return 2;
				else	if (d3 >= safety && !doNotGoHere(x-1, y, s)) return 3;
				//System.out.println(d);
				//break;
			}
		}
		//System.out.print("1 ");
		for (safety = 6; safety > 1; safety --)	{
			if ((d == 2 && d2 < safety) || (d == 3 && d3 < safety))	{
				if (deltaY < 0 && d0 >= safety && !doNotGoHere(x, y+1, s))  return 0;
				else if (d1 >= safety && !doNotGoHere(x, y-1, s)) return 1;
				//System.out.println(d);
				//break;
			}
			if ((d == 0 && d0 < safety) || (d == 1 && d1 < safety))	{
				if (deltaX < 0 && d2 >= safety && !doNotGoHere(x+1, y, s))  return 2;
				else	if (d3 >= safety && !doNotGoHere(x-1, y, s)) return 3;
				//System.out.println(d);
				//break;
			}
		}}
		//System.out.print("2 ");

		if (d == 0 && !doNotGoHere(x, y+1, s)) return 0;
		if (d == 1 && !doNotGoHere(x, y-1, s)) return 1;
		if (d == 2 && !doNotGoHere(x+1, y, s)) return 2;
		if (d == 3 && !doNotGoHere(x-1, y, s)) return 3;
		//System.out.print("3 ");

	//	System.out.println("later" + points);
	//	System.out.println(d0+ " " + d1 + " " + d2 + " " + d3);
	//	Scanner keyboard = new Scanner(System.in);
	//	String s = keyboard.nextLine();
		
		if (d0 > d1 && d0 > d2 && d0 > d3 && !doNotGoHere(x, y+1, s))	return 0;
		if (d1 > d2 && d1 > d3 && !doNotGoHere(x, y-1, s))	return 1;
		if (d2 > d3 && !doNotGoHere(x+1, y, s)) return 2;
		if (d3 != 1 && !doNotGoHere(x-1, y, s))	return 3;
		//System.out.print("4 ");
		
		if (x == 1 && !doNotGoHere(x-1, y, s)) return 3;
		if (x == field.length - 2 && !doNotGoHere(x+1, y, s)) return 2;
		if (y == 1 && !doNotGoHere(x, y-1, s)) return 1;
		if (y == field[0].length - 2 && !doNotGoHere(x, y+1, s)) return 0;
		//System.out.print("5 ");

		int c = 0;
		while (true)	{
			if (d == 0 && doNotGoHere(x, y+1, s)) d=3;
			else if (d == 1 && doNotGoHere(x, y-1, s)) d--;
			else if (d == 2 && doNotGoHere(x+1, y, s)) d--;
			else if (d == 3 && doNotGoHere(x-1, y, s)) d--;
			else return d;
			//System.out.println(d);
			c ++;
			if (c == 4) break;
		}
		//System.out.print("6 ");
		
		return 0;
	}
	int count, depth, nextFreeSpot;
	public int shortestPathSolutionAlgorithm(int x, int y, char s)	{
		int fx = x;
		int fy = y;
		int d = 0;
		count = 0;
		depth = 0;
		boolean initial = true;
		boolean[][][] wasHere = new boolean[field.length][field[0].length][500];
		nextFreeSpot = 1;
		int[][] queue = new int[80000][4];
		//for (int i = 0; i < queue.length; i++)
		//	queue[i][0] = -1;
		for (int i = 0; i < wasHere.length; i++)
			for (int a = 0; a < wasHere[i].length; a++)
				for (int f = 0; f < wasHere[i][a].length; f++)
					wasHere[i][a][f] = false;
		queue[count][0] = x;
		queue[count][1] = y;
		queue[count][2] = d;
		queue[count][3] = depth;
		while (field[x][y] != 'F')	{
			x = queue[count][0];
			y = queue[count][1];
			d = queue[count][2];
			depth = queue[count][3];
			if (!(walls))	{
				if (x == 0) x = field.length - 2;
				else if (x == field.length - 1) x = 1;
				else if (y == 0) y = field[0].length - 2;
				else if (y == field[0].length - 1) y = 1;
			}
			if (field[x][y] == ' ' || initial || (field[x][y] == 'T' && trail[x][y] + 2 < depth))	{
				if (!wasHere[x+1][y][depth])	{
					wasHere[x+1][y][depth] = true;
					queue[nextFreeSpot][0] = x + 1;
					queue[nextFreeSpot][1] = y;
					if (initial) queue[nextFreeSpot][2] = 2;
					else		 queue[nextFreeSpot][2] = d;
					queue[nextFreeSpot][3] = depth + 1;
					nextFreeSpot++;
				}
				if (!wasHere[x-1][y][depth])	{
					wasHere[x-1][y][depth] = true;
					queue[nextFreeSpot][0] = x - 1;
					queue[nextFreeSpot][1] = y;
					if (initial) queue[nextFreeSpot][2] = 3;
					else		 queue[nextFreeSpot][2] = d;
					queue[nextFreeSpot][3] = depth + 1;
					nextFreeSpot++;
				}
				if (!wasHere[x][y+1][depth])	{
					wasHere[x][y+1][depth] = true;
					queue[nextFreeSpot][0] = x;
					queue[nextFreeSpot][1] = y + 1;
					if (initial) queue[nextFreeSpot][2] = 0;
					else         queue[nextFreeSpot][2] = d;
					queue[nextFreeSpot][3] = depth + 1;
					nextFreeSpot++;
				}
				if (!wasHere[x][y-1][depth])	{
					wasHere[x][y-1][depth] = true;
					queue[nextFreeSpot][0] = x;
					queue[nextFreeSpot][1] = y - 1;
					if (initial) queue[nextFreeSpot][2] = 1;
					else		 queue[nextFreeSpot][2] = d;
					queue[nextFreeSpot][3] = depth + 1;
					nextFreeSpot++;
				}
			}
			initial = false;
			count++;
		}
		switch (d) {
			case 0: fy += 1; break;
			case 1:	fy -= 1; break;
			case 2: fx += 1; break;
			case 3: fx -= 1; break;
		}
		if (doNotGoHere(fx, fy, s))	return normalAI(x, y, s);
		return d;
	}
	/*int nextFreeSpot()	{
		for (int i = 0; i < queue.length; i++)
			if (queue[i][0] == -1)
				return i;
		return -1;
	}*/
	public boolean doNotGoHere(int x, int y, char s)	{
		if (field[x][y] == 'T') return true;
		if (!walls)	{
			if (x == 0) x = field.length - 2;
			else if (x == field.length - 1) x = 1;
			else if (y == 0) y = field[0].length - 2;
			else if (y == field[0].length - 1) y = 1;
		}
		if (field[x][y] == 'B' && walls) return true;
		if (field[x][y] == 'H') return true;
		if (field[x][y] == '2') return true;
		if (s == '2')	{
			if (field[x][y-1] == 'H' && mD == 0) return true;
			if (field[x][y+1] == 'H' && mD == 1) return true;
			if (field[x-1][y] == 'H' && mD == 2) return true;
			if (field[x+1][y] == 'H' && mD == 3) return true;
		}
		else	{
			if (field[x][y-1] == '2' && mD2 == 0) return true;
			if (field[x][y+1] == '2' && mD2 == 1) return true;
			if (field[x-1][y] == '2' && mD2 == 2) return true;
			if (field[x+1][y] == '2' && mD2 == 3) return true;
		}
		return false;
	}
	public int edgeHugger(int x, int y, char s)	{
		// Consistently gets high 40s low 50s
		if ((d0 == 50 || d1 == 50 || d2 == 50 || d3 == 50) && !(onSite))	{
			// the following is called when it has food in it's direct line of site
			if (!(onSite))	{	wTH = 0;	chooseD = true;	}
			wTH++;
			if (d0 == 50) keepAt = 0;
			if (d1 == 50) keepAt = 1;
			if (d2 == 50) keepAt = 2;
			if (d3 == 50) keepAt = 3;

			if (field[x][y+1] == 'F') onSite = true;
			if (field[x][y-1] == 'F') onSite = true;
			if (field[x+1][y] == 'F') onSite = true;
			if (field[x-1][y] == 'F') onSite = true;
			//System.out.println(keepAt);
			return keepAt;
		}
		/*if (chooseD) {	waitToHug = points + 100;
			if (d0 > d1 && d0 > d2 && d0 > d3) keepAt = 0;
			else if (d1 > d2 && d1 > d3) keepAt = 1;
			else if (d2 > d3) keepAt = 2;
			else keepAt = 3; 
			chooseD = false;
		}*/
		if (chooseD)	{
			if (keepAt == 0) keepAt = 2;
			else if (keepAt == 1) keepAt = 3;
			else if (keepAt == 2) keepAt = 1;
			else if (keepAt == 3) keepAt = 0;
			chooseD = false;
		}
		//if (Math.random()*50 == 0) onSite = true;
	//	wTH = 0;
		if (onSite) {
			if (keepAt == 0)	if (!doNotGoHere(x, y+1, s))	return 0;
			if (keepAt == 1)	if (!doNotGoHere(x, y-1, s))	return 1;
			if (keepAt == 2)	if (!doNotGoHere(x+1, y, s))	return 2;
			if (keepAt == 3)	if (!doNotGoHere(x-1, y, s))	return 3;
			//t.setDelay(100);
			//System.out.println(keepAt);
			onSite = false;
		}

		waitToHug--;
		//t.setDelay(AISpeed);
		//System.out.println("hug");
			// the following makes it go in a square around the border
		//System.out.println("down");
		if (y > field[0].length / 2) if (!doNotGoHere(x, y+1, s))	return 0;
		if (y > field[0].length / 2) if (!doNotGoHere(x-1, y, s))	return 3;
		//System.out.println("up");
		if (y < field[0].length / 2) if (!doNotGoHere(x, y-1, s))	return 1;
		if (y < field[0].length / 2) if (!doNotGoHere(x+1, y, s))	return 2;
		//System.out.println("left");
		if (x < field.length / 2)	if (!doNotGoHere(x-1, y, s))	return 3;
		if (x < field.length / 2)	if (!doNotGoHere(x, y-1, s))	return 1;
		//System.out.println("right");
		if (x > field.length / 2)	if (!doNotGoHere(x+1, y, s))	return 2;
		if (x > field.length / 2)	if (!doNotGoHere(x, y+1, s))	return 0;
		
		//System.out.println("later");
		if (Math.random() * 2 < 1) {
			// Very good against loops
			if (x == 1 && field[field.length - 2][y] != 'T' && field[field.length-2][y] != 'B') return 3;
			if (x == field.length - 2 && field[1][y] != 'T' && field[1][y] != 'B') return 2;
			if (y == 1 && field[x][field[0].length-2]!= 'T' && field[x][field[0].length-2]!= 'B') return 1;
			if (y == field[0].length - 2 && field[x][1] != 'T' && field[x][1] != 'B') return 0;
		}
		else if (Math.random() * 2 < 1)	{
			if (y == 1 && field[x][field[0].length-2]!= 'T' && field[x][field[0].length-2]!= 'B') return 1;
			if (y == field[0].length - 2 && field[x][1] != 'T' && field[x][1] != 'B') return 0;
			if (x == 1 && field[field.length - 2][y] != 'T' && field[field.length-2][y] != 'B') return 3;
			if (x == field.length - 2 && field[1][y] != 'T' && field[1][y] != 'B') return 2;
		}
		// the following is called when something is in it's way

		if (y >	field[0].length / 2) if (!doNotGoHere(x, y-1, s))	return 1;
		if (x > field.length / 2)	if (!doNotGoHere(x-1, y, s))	return 3;
		if (y < field[0].length / 2) if (!doNotGoHere(x, y+1, s))	return 0;
		if (x < field.length / 2)	if (!doNotGoHere(x+1, y, s))	return 2;

		if (x == 1) return 3;
		if (x == field.length - 2) return 2;
		if (y == 1) return 1;
		if (y == field[0].length - 2) return 0;
			
		if (x > field.length / 2)	if (!doNotGoHere(x, y-1, s))	return 1;
		if (y > field[0].length / 2) if (!doNotGoHere(x+1, y, s))	return 2;
		if (x < field.length / 2)	if (!doNotGoHere(x, y+1, s))	return 0;
		if (y < field[0].length / 2) if (!doNotGoHere(x-1, y, s))	return 3;

		//System.out.println("nathing");
		return 0;	
	}
	public int AIDirection(int x, int y, char s)	{
		getDValues(x, y);
		if (mazeSolver)	{	
			try	{
				return shortestPathSolutionAlgorithm(x, y, s);	
			} catch (java.lang.ArrayIndexOutOfBoundsException e)	{
				//System.out.println("Array Index Out of Bounds!");
				if (!gonnadie)	{
					System.out.println("dead " + points);
				}
				gonnadie = true;
				return normalAI(x, y, s);
			}
		}
		else if (edgeHugger) {	return edgeHugger(x, y, s);	}
		else return normalAI(x, y, s);
	}
	public void placeFood(char c)	{
		int ran1, ran2;
		while (true)	{
			ran1 = (int)(Math.random() * field.length);
			ran2 = (int)(Math.random() * field.length);
			if (field[ran1][ran2] == ' ' && notGonnaHitMeInTheNextMove(ran1, ran2)) break;
		}
		field[ran1][ran2] = c;
		if (c == 'F') foodX = ran1; foodY = ran2;
	}
	public boolean notGonnaHitMeInTheNextMove(int x, int y)	{
		if (mD == 0) if (field[x][y-1] == 'H') return false;
		if (mD == 1) if (field[x][y + 1] == 'H') return false;
		if (mD == 2) if (field[x - 1][y] == 'H') return false;
		if (mD == 3) if (field[x + 1][y] == 'H') return false;
		return true;
	}
	public void addTL()	{
		int i, a;
		for (i = 0; i < field.length; i ++)
			for (a = 0; a < field.length; a++)
				if (field[i][a] == 'T')
					if (trail[i][a] != trailLength)	trail[i][a]++;
	}
	public void deleteTail()	{
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
	public void keyTyped(KeyEvent evt) {	
		char key = evt.getKeyChar();
		if (key == 'p' || key == 'P')	t.stop();
		if (key == 'u' || key == 'U') {	t.setDelay(delay);	repaint();	}

		if (key == 'a')	{
			if (twoPeeps && mD2 != 2) direction2 = 3;
			else if (!(twoPeeps) && mD != 2)direction = 3;
		}
		else if (key == 'w')	{
			if (twoPeeps && mD2 != 0) direction2 = 1;
			else if (!(twoPeeps) && mD != 0)direction = 1;
		}
		else if (key == 's')	{
			if (twoPeeps && mD2 != 1) direction2 = 0;
			else if (!(twoPeeps) && mD != 1)direction = 0;
		}
		else if (key == 'd')	{
			if (twoPeeps && mD2 != 3) direction2 = 2;
			else if (!(twoPeeps) && mD != 3)direction = 2;
		}
		key = 'U';
	}
	public void keyPressed(KeyEvent evt) {	
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_LEFT && mD	!= 2)
			direction = 3;
		else if (key == KeyEvent.VK_UP && mD != 0)
			direction = 1;
		else if (key == KeyEvent.VK_DOWN && mD != 1)
			direction = 0;
		else if (key == KeyEvent.VK_RIGHT && mD != 3)
			direction = 2;
		if (twoPeeps && AI)	{
			if (key == KeyEvent.VK_LEFT && mD2	!= 2)
				direction2 = 3;
			else if (key == KeyEvent.VK_UP && mD2 != 0)
				direction2 = 1;
			else if (key == KeyEvent.VK_DOWN && mD2 != 1)
				direction2 = 0;
			else if (key == KeyEvent.VK_RIGHT && mD2 != 3)
				direction2 = 2;
		}
		//customMove();
		//else Toolkit.getDefaultToolkit().beep();
	}
	public void customMove()	{
		int i, a, xt, yt;
		boolean moved = false, moved2 = false;
		for (i = 0; i < field.length; i++) {
			for (a = 0; a < field[i].length; a++)	{
				xt = i;
				yt = a;
				if (field[i][a] == 'H' && !(moved))	{
					if (AI)	direction = AIDirection(xt, yt, 'H');
					switch(direction)	{
						case 0: yt += 1; break;
						case 1:	yt -= 1; break;
						case 2: xt += 1; break;
						case 3: xt -= 1; break;
					}
					if (dead || dead2) return;
					if ((xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) || field[xt][yt] == 'T' || field[xt][yt] == '2')	{
						if (!(walls))	{
							if (xt == 0) xt = field.length - 2;
							else if (xt == field.length - 1) xt = 1;
							else if (yt == 0) yt = field[0].length - 2;
							else if (yt == field[0].length - 1) yt = 1;
							//else killTheSnake(false);
							else return;
						}
						//else if (field[xt][yt] == '2') tie = true;
						//else killTheSnake(false);
						else return;
					}
					if (!(xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) && field[xt][yt] == 'B')
						//killTheSnake(false);
						return;
					if (dead || dead2) return;
					mD = direction;
					field[i][a] = 'T';
					trail[i][a] = trailLength;
					if (field[xt][yt] == 'F') {	trailLength++;	addTL(); food = true;	points++; if ((points + points2) % 10 == 0) placeFood('B');	accumulateSpeed();	}
					field[xt][yt] = 'H';
					moved = true;
				}
				if (field[i][a] == '2' && !(moved2) && twoPeeps)	{
					if (AI2) direction2 = AIDirection(xt, yt, '2');
					switch(direction2)	{
						case 0: yt += 1; break;
						case 1:	yt -= 1; break;
						case 2: xt += 1; break;
						case 3: xt -= 1; break;
					}
					if (dead2 || dead) return;
					if ((xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) || field[xt][yt] == 'T' || field[xt][yt] == 'H')	{
						if (!(walls))	{
							if (xt == 0) xt = field.length - 2;
							else if (xt == field.length - 1) xt = 1;
							else if (yt == 0) yt = field[0].length - 2;
							else if (yt == field[0].length - 1) yt = 1;
							else killTheSnake(true);
						}
						//if (field[xt][yt] == 'H') tie = true;
						else killTheSnake(true);
					}
					if (!(xt == 0 || xt == field.length - 1 || yt == 0 || yt == field[0].length - 1) && field[xt][yt] == 'B')
						killTheSnake(true);
					if (dead || dead2) return;
					mD2 = direction2;
					field[i][a] = 'T';
					trail[i][a] = trailLength2;
					if (field[xt][yt] == 'F') {	trailLength2++;	addTL(); food = true;	points2++; if ((points + points2) % 10 == 0) placeFood('B');	accumulateSpeed();	}
					field[xt][yt] = '2';
					moved2 = true;
				}
			}
		}
		if (dead || dead2) return;
		if (food)	placeFood('F');
		deleteTail();
		food = false;
		repaint();
	}
	public void keyReleased(KeyEvent evt) {	}
	public void mousePressed(MouseEvent evt) {	
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
			if (x <= 590 && x >= 50 && y>=700 && y<= 800 && cD)	{
				accumulateSpeed = !accumulateSpeed;
				repaint();
			}
			if (x <= 800 && x >= 420 && y>=250 && y <= 400 && cD)	{
				if (!twoPeeps || (twoPeeps && AI2))	{
					if (edgeHugger || mazeSolver) delay = AIHuggerSpeed;
					else			delay = AINormalSpeed;
					t.setDelay(75);
				}
				cD = false;
				AI = true;
				repaint();
			}
			if (x <= 800 && x >= 600 && y>=450 && y <= 650 && cD)	{
				if (edgeHugger && mazeSolver)	mazeSolver = false;
				else if (edgeHugger)			edgeHugger = false;
				else                 			edgeHugger = mazeSolver = true;
				repaint();
			}
			if (x <= 200 && x >= 50 && y>= 450 && y <= 600 && (dead || dead2 || tie))	{
				initial = true;
				t.stop();
				repaint();
			}
			if (x >= 50 && x <= 500 && y >= 820 && y <= 920)	{
				twoPeeps = !twoPeeps;
				repaint();
			}
			if (x >= 550 && x <= 860 && y >= 820 && y <= 920)	{
				AI2 = !AI2;
				repaint();
			}
			if (x <= 400 && x >= 250 && y>=450 && y <= 600 && (dead || dead2 || tie))
				System.exit(0);
		}
		else	requestFocus();
	}
	public void mouseEntered(MouseEvent evt) { } 
    public void mouseExited(MouseEvent evt) { } 
    public void mouseReleased(MouseEvent evt) { } 
    public void mouseClicked(MouseEvent evt) { }
}