import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
public class Main extends JFrame{
	static Game game;
	public Main()
	{
		super("Epidemic");

		setSize(800,600);
		setVisible(true);
		super.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args)
	{
		Main main = new Main();
		game = new Game(main);
		main.add(game, BorderLayout.CENTER);
		main.setResizable(false);
		Action updateActionListener = new AbstractAction()
        {
                
                public void actionPerformed(ActionEvent e) 
                {
                        game.update();
                }


        };
        /*Timer tick determines FPS*/
        Timer timer = new Timer(21, updateActionListener);
        timer.start();
	}
}
