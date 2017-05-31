package Server;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.util.*;
 
import util.BoundedBuffer;
import util.MovePacket;
import util.Snake;
 
public class Highscores extends JDialog {
    JDialog dialog = new JDialog();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    JTextArea hsTextArea;
    JTextArea kcTextArea;
   CopyOnWriteArrayList<Snake> playerList;
    public Highscores() {
    }
     
    public Highscores(CopyOnWriteArrayList<Snake> playerList, int x, int y) {
    	this.playerList = playerList;
        //dialog.setSize(x, y-350);
        dialog.setSize(x-350, y);
        dialog.setLocation(dim.width/2-dialog.getSize().width/2+285, (dim.height/2-dialog.getSize().height/2));
        textArea();
		//Container f = dialog.getContentPane();
        dialog.setUndecorated(true);
        dialog.setVisible(true);
    }
     
    Integer[] sortedList;
    public Integer[] getHighestScores() {
        sortedList = new Integer[playerList.size()];
        int counter = 0;
        for (Snake snake : playerList) {
            sortedList[counter] = snake.getLength();
            counter += 1;
        }
        Arrays.sort(sortedList, Collections.reverseOrder());
        //System.out.println(Arrays.toString(list));
        return sortedList;
    }
     
    public void updateHighscores() {
    	//int size = 0;
        List<Integer> list = Arrays.asList(getHighestScores());
        System.out.println("LIST:"+list);
        //hsTextArea.append("test");
        hsTextArea.setText("");
        for (int i=0; i<list.size(); i++) {
          hsTextArea.append(i+1+ "|"+list.get(i).toString()+"\n");
        }
    }
     
    private void textArea() {
        JPanel highscoresTextArea = new JPanel();
        //highscoresTextArea.getContentPane().setBackground(Color.RED);
        highscoresTextArea.setBackground(Color.RED);
        highscoresTextArea.setLayout(new GridLayout(0, 1));
        hsTextArea = new JTextArea("highscore");
        kcTextArea = new JTextArea("kills");
        
        highscoresTextArea.add(hsTextArea, BorderLayout.CENTER);
        //highscoresTextArea.add(kcTextArea, BorderLayout.WEST);
        dialog.add(highscoresTextArea);
    }
 
}
