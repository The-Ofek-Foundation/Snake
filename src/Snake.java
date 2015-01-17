/*	Ofek Gila
	February 3rd, 2014
	Snake.java
	This program will attempt to play the game "snake"
*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.*;

public class Snake	extends JApplet{
	public static void main(String[] args) {
		JFrame frame = new JFrame("Snake");
		frame.setContentPane(new ContentPanel());
		frame.setSize(1006,1035);
		//frame.setSize(1006-150,1035-150);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void init()	{
		setContentPane(	new ContentPanel());
	}
}