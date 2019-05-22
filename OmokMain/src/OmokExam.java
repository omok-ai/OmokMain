import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;

class Stone {
	int x; int y;
	Color color;
	
	Stone(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color =  color;
	}	
}

public class OmokExam extends Frame implements MouseListener, ActionListener, WindowListener, KeyListener, ItemListener {
	/**
	 * author : hjszero2@naver.com
	 * date : Nov. 8, 2013
	 * CCL3.0 BY-NC-SA Some rights reserved.
	 */

	private static final long serialVersionUID = 1L;
	final int NONE = 0;	final int BLACK = 1; final int WHITE = 2; final int WALL = 3;
	final int nWALL = 4;
	final float spc_x = 20;
	final float spc_y = 20;
	final float mrg_x = spc_x/2;
	final float mrg_y = spc_y/2;
	final float width = 600;
	final float height = 600;
	final int dExit_Width = 270, dExit_Height = 130;
	final int dColor_Width = 100, dColor_Height = 300;
	private boolean isPlaying = true ;
	
	private static int n =0;
	ArrayList<Stone> arrStones = new ArrayList<Stone>();
	private int arr[][] = new int[(int) (width/spc_x+1) + nWALL * 2][(int) (height/spc_y+1) + nWALL * 2];
	
	public TextArea taLog;
	protected CanvasBoard cvBoard;
	protected Button btRetry, btUndo, btSetColor;
	private int isWhiteWin = 0; //set 1 when white player win.
	private Dialog dExit; 
	private Dialog dColor;
	private Button btYes, btNo;
	private Panel pLeft, pRight, pButton;
	private List liColor;
	
	public void Main() {
		super("Omok Beta 0." + Integer.toString((int)serialVersionUID));
	
		arrClear(); arrStones.clear();
		setBackground(Color.lightGray);
		pLeft = new Panel();
		pRight = new Panel();
		
		cvBoard = new CanvasBoard();
		cvBoard.setSize((int)width, (int)height);
		cvBoard.setBackground(Color.yellow);
		pLeft.add(cvBoard);
		cvBoard.addMouseListener(this);
		cvBoard.addKeyListener(this);
		btRetry = new Button("Retry");
		btUndo = new Button("Undo");
		btSetColor = new Button("Set Background");
		taLog = new TextArea(30, 10);
		
		pRight.setLayout(new BorderLayout());
		pButton = new Panel();
		pButton.add(btRetry); pButton.add(btUndo); pButton.add(btSetColor);
		pRight.add(pButton, "North");
		pRight.add(taLog);
		
		add(pLeft, "West"); add(pRight, "East");
		taLog.append("====Program Started====\n");
		taLog.setEditable(false);
		setVisible(true);
		setSize((int) (width+240), (int) (height+30));	
		
		btRetry.addActionListener(this); btUndo.addActionListener(this); btSetColor.addActionListener(this);
		
		addWindowListener(this);
		addKeyListener(this);
		dExit = new Dialog(this, "Exit ?", true);
		Panel pExit = new Panel();
		pExit.setLayout(new GridLayout());
		
		btYes = new Button("Yes");
		btNo = new Button("No");
		pExit.add(btYes); pExit.add(btNo);
		btYes.addActionListener(this);
		btNo.addActionListener(this);
		
		dExit.add(new Label("Would you really like to Exit the game?", Label.CENTER), "Center");
		dExit.add(pExit, "South");
		
		dExit.setBounds(this.getX() + this.getWidth() / 2 - dExit_Width / 2,
				this.getY() + this.getHeight() /2 - dExit_Height / 2,
				dExit_Width, dExit_Height);
		
		
		dColor = new Dialog(this, "Choose", true);
		Panel pColor = new Panel();
		liColor = new List(10);
		liColor.add("Red");
		liColor.add("Green");
		liColor.add("Gray");
		liColor.add("Black");
		pColor.add(liColor);
		dColor.add(pColor);
		dColor.setBounds(this.getX() + this.getWidth() / 2 - dColor_Width / 2,
				this.getY() + this.getHeight() /2 - dColor_Height / 2,
				dColor_Width, dColor_Height);
		liColor.addItemListener(this);
		dColor.addWindowListener(this);
		dExit.addWindowListener(this);
		dColor.pack();
		this.setResizable(false);
	}
	
	public static void main(String[] args) throws IOException {
		new Main();
	}
	public class CanvasBoard extends Canvas {
		public void paint(Graphics g) {
			
			int i;
			for (i=0; i<width/spc_x; i++) {
				g.drawLine((int)(mrg_x+i*spc_x), (int)mrg_y, (int)(mrg_x+i*spc_x), (int)(mrg_x+height-spc_y));	
			}
			for (i=0; i<height/spc_y; i++) {
				g.drawLine((int)mrg_x, (int)(mrg_y+i*spc_y), (int)(mrg_x+width-spc_x), (int)(mrg_y+i*spc_y));
			}
			
			for (i=0; i<arrStones.size(); i++) {
				g.setColor(arrStones.get(i).color);
				g.fillOval((int) (mrg_x + (arrStones.get(i).x-nWALL)* spc_x - spc_x/2) ,
						(int) (mrg_y + (arrStones.get(i).y-nWALL)* spc_y -spc_y/2),
						(int) spc_x, (int) spc_y);
			}	
		}	
	}
	@Override
	public void mouseReleased(MouseEvent me) {
		if (isPlaying) {
		// TODO Auto-generated method stub
			int x = me.getX();
			int y = me.getY();
			
			x = (int)( (x-mrg_x+spc_x/2) / spc_x) + nWALL;
			y = (int)( (y-mrg_y+spc_y/2) / spc_y) + nWALL;
			if (arr[x][y] == NONE) {
				int t = (n+isWhiteWin+1) %2 ;
				
				if ( is33(x, y, (t%2==0)?WHITE:BLACK) ) {
					cvBoard.repaint();
					return;
				}
				n++;
				 
				if (t==0) {
					taLog.append(" W : ");
					arr[x][y] = WHITE;
					arrStones.add(new Stone(x, y, Color.white));
				}
				else {
					taLog.append(" B : ");
					arr[x][y]= BLACK;
					arrStones.add(new Stone(x,y, Color.black));
				}
				cvBoard.repaint();
				taLog.append("(" + Integer.toString(x-nWALL) + ", " + Integer.toString(y-nWALL) + ")\n");
				
				if (isOmok(x,y, ( t==0 )? WHITE:BLACK) ) {
					taLog.append( ( (t==0)?"White":"Black" ) + " Win!\n");
					if (t==0) isWhiteWin = 1;
					else isWhiteWin= 0;
					arrClear();	
					isPlaying = false;
				}	
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btRetry) {
			arrClear();
			arrStones.clear();
			cvBoard.repaint();
			taLog.append("=======Clear=======\n");
			isPlaying = true;
		}
		else if (e.getSource() == btUndo) {
			//Back
			int t = (n+isWhiteWin)%2; 
			if (n>isWhiteWin) {
				n--;
				taLog.append(" Undo. " + (( t==0 )? "W":"B") + " : (" +
						Integer.toString(arrStones.get(arrStones.size()-1).x -nWALL) + ", " +
						Integer.toString(arrStones.get(arrStones.size()-1).y -nWALL) + ")\n");
				
				arr[arrStones.get(arrStones.size() -1).x][arrStones.get(arrStones.size() -1).y] = NONE;
				arrStones.remove(arrStones.size() - 1);
				cvBoard.repaint();
			}
			else {
				//err msg
			}
		}
		else if (e.getSource() == btSetColor) {
			dColor.setVisible(true);
		}
		else if (e.getSource() == btYes) {
			System.exit(0);
		}
		else if (e.getSource() == btNo) {
			dExit.setVisible(false);
		}
	}
	private void arrClear() {
		n=0;
		int i, j;
		for (i=0; i<(int) (width/spc_x+1) + 2*nWALL; i++) {
			for (j=0; j<(int) (height/spc_y+1) + nWALL; j++) {
				arr[i][j] = WALL ;
			}
		}
		for (i=nWALL; i<(int) (width/spc_x+1) + nWALL; i++) {
			for (j=nWALL; j<(int) (height/spc_y+1) + nWALL; j++) {
				arr[i][j] = NONE;
			}
		}
	}
	
	private boolean isOmok(int x, int y, int type) {
		final int[] dx = new int[]{ 0,  1, 1, 1, 0,-1,-1,-1};
		final int[] dy = new int[]{-1, -1, 0, 1, 1, 1, 0,-1};
		int[] cnt = new int[8];
		int cur_x, cur_y;
		for (int i=0; i<8; i++) {
			cnt[i] = 0;
			cur_x = x; cur_y=y;
			while(true) {
				cur_x+=dx[i]; cur_y+=dy[i];
				if (!( (0<=cur_x && cur_x<width/spc_x) && (0<=cur_y && cur_y<height/spc_y))) break;
				if (arr[cur_x][cur_y] == type) cnt[i]++;
				else break;
			}
			
		}
		for (int i = 0; i<8/2; i++) {
			if ( cnt[i] + cnt[i+4] == 4) {
				return true;
			}
		}
		return false;
	}
	private boolean is33(int x, int y, int type) {
		final int[] dx = new int[]{ 0,  1, 1, 1, 0,-1,-1,-1};
		final int[] dy = new int[]{-1, -1, 0, 1, 1, 1, 0,-1};
		int[] cnt = new int[4];
		int cur_x, cur_y;
		
		boolean[] flag = new boolean[4];
	
		for (int i=0; i<4; i++) {
			cnt[i]=0;
			flag[i]=false;
		}
		for (int i=0; i<8; i++) {
			
			cur_x = x; cur_y=y;
			while(true) {
				cur_x+=dx[i]; cur_y+=dy[i];
				if (!( (0<=cur_x && cur_x<width/spc_x) && (0<=cur_y && cur_y<height/spc_y))) break;
				if (arr[cur_x][cur_y] == type) cnt[i%4]++;
				else break;
			}
			
		}
		int cnt_3=0;
		
		for (int i=0; i<8; i++) {
			if (flag[i%4] == false) {
				if (cnt[i%4]==2) {
					if (arr[x-dx[i]][y-dy[i]]==NONE &&
						arr[x+3*dx[i]][y+3*dy[i]] == NONE) {
						flag[i%4]=true; cnt_3 ++;
					}
					else if (arr[x-2*dx[i]][y-2*dy[i]]==NONE &&
							arr[x+2*dx[i]][y+2*dy[i]]==NONE ) {
						flag[i%4]=true;cnt_3 ++;
					}
				
				}
				else if ( arr[x+dx[i]][y+dy[i]] == type &&
						arr[x+2*-dx[i]][y-2*dy[i]] == type &&
						arr[x-dx[i]][y-dy[i]] == NONE &&
						arr[x+2*dx[i]][y+2*dy[i]] == NONE &&
						arr[x+2*-dx[i]] [y-3*dy[i]] == NONE) { 
					
					flag[i%4] = true;
					cnt_3++;
				}
				
				else if ( arr[x+dx[i]][y+dy[i]] == type &&
						arr[x+2*dx[i]][y+2*dy[i]] == NONE &&
						arr[x+3*dx[i]][y+3*dy[i]] == type &&
						arr[x+4*dx[i]][y+4*dy[i]] == NONE &&
						arr[x-dx[i]][y-dy[i]] == NONE ) {
					flag[i%4] = true;
					cnt_3++;
				}
				else if ( arr[x+dx[i]][y+dy[i]] == NONE &&
						arr[x+2*dx[i]][y+2*dy[i]] == type &&
						arr[x+3*dx[i]][y+3*dy[i]] == type &&
						arr[x+4*dx[i]][y+4*dy[i]] == NONE &&
						arr[x-dx[i]][y-dy[i]] == NONE ) {
					flag[i%4] = true;
					cnt_3++;
				}
				
				
			}
		}
		
		System.out.println(cnt_3);
		if (cnt_3>=2) return true;
		else return false;
	}

	@Override
	public void windowClosing(WindowEvent we) {
		// TODO Auto-generated method stub
		if (we.getSource() == this) {
			dExit.setBounds(this.getX() + this.getWidth() / 2 - dExit_Width / 2,
				this.getY() + this.getHeight() /2 - dExit_Height / 2,
				dExit_Width, dExit_Height);
			dExit.setVisible(true);
		}
		else if (we.getSource() == dColor) {
			dColor.setVisible(false);
			
		}
		else if (we.getSource() == dExit) {
			dExit.setVisible(false);
		}
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		// TODO Auto-generated method stub
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dExit.setVisible(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent we) {
	/*	// TODO Auto-generated method stub
		if (we.getSource() == this) {
			arrClear();
			arrStones.clear();
			taLog.append("=======Clear=======\n");
			isPlaying = true;
			cvBoard.repaint();
		}*/
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		int x = me.getX();
		int y = me.getY();
		x = (int)( (x-mrg_x+spc_x/2) / spc_x) + nWALL;
		y = (int)( (y-mrg_y+spc_y/2) / spc_y) + nWALL;
		
		if (isPlaying && arr[x][y] == NONE) {
		// TODO Auto-generated method stub
			
			
			int t = (n+isWhiteWin+1) %2 ;
			
			if ( is33(x, y, (t%2==0)? WHITE:BLACK) ) {
				
				Graphics g = cvBoard.getGraphics();
				g.setColor(Color.red);
				g.drawOval((int) (mrg_x + (x-nWALL) * spc_x - spc_x/2) ,
						(int) (mrg_y + (y-nWALL) * spc_y -spc_y/2),
						(int) spc_x-1, (int) spc_y-1);
				g.fillOval((int) (mrg_x + (x-nWALL) * spc_x - spc_x/2) ,
						(int) (mrg_y + (y-nWALL) * spc_y -spc_y/2),
						(int) spc_x, (int) spc_y);
			}
			
		}

	}

	@Override
	public void itemStateChanged(ItemEvent ie) {
		// TODO Auto-generated method stub
		if (ie.getSource() == liColor) {
			String color = liColor.getSelectedItem();
			System.out.println(color);
			if (color == "Red") {
				pLeft.setBackground(Color.RED);
				setBackground(Color.RED);
				pButton.setBackground(Color.RED);
			}
			else if (color == "Green") {
				pLeft.setBackground(Color.GREEN);
				setBackground(Color.GREEN);
				pButton.setBackground(Color.GREEN);
			}
			else if (color == "Gray") {
				pLeft.setBackground(Color.gray);
				setBackground(Color.gray);
				pButton.setBackground(Color.gray);
			}
			else if (color == "Black") {
				pLeft.setBackground(Color.black);
				setBackground(Color.black);
				pButton.setBackground(Color.black);
			}
			
		}
	}
}