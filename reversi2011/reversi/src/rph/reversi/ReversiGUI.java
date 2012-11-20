package rph.reversi;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ReversiGUI extends JFrame {
	
	private static final long serialVersionUID = -4058579359403964844L;

    PlaygroundPanel playgroundPanel = new PlaygroundPanel();
    JPanel infoPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel jLabel1 = new JLabel();
    JLabel statusLabel = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel gamesLabel = new JLabel();

    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel6 = new JLabel();

    JLabel p1NameLabel = new JLabel();
    JLabel p1StonesLabel = new JLabel();
    JLabel p1TotalGamesLabel = new JLabel();
    JLabel p1TotalStonesLabel = new JLabel();
    JLabel p1TotalTimeLabel = new JLabel();

    JLabel p2NameLabel = new JLabel();
    JLabel p2StonesLabel = new JLabel();
    JLabel p2TotalGamesLabel = new JLabel();
    JLabel p2TotalStonesLabel = new JLabel();
    JLabel p2TotalTimeLabel = new JLabel();
	
    JButton startStopButton = new JButton();
    JButton playPauseButton = new JButton();
    JButton loadConfigurationButton = new JButton();
	
	ReversiGUI(final Reversi reversi) {
		setTitle("Reversi");
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(playgroundPanel,
				new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(infoPanel,
				new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		this.getContentPane().add(buttonPanel,
				new GridBagConstraints(1, 1, 1, 1, 0.1, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		
		infoPanel.setLayout(new GridBagLayout());
        jLabel1.setText("Status : ");
        jLabel2.setText("Games played : ");
        
        jLabel3.setText("Current stones : ");
        jLabel4.setText("Games won : ");
        jLabel5.setText("Total stones : ");
        jLabel6.setText("Total time : ");

        infoPanel.add(jLabel1,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
        infoPanel.add(statusLabel,
				new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        infoPanel.add(jLabel2,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(gamesLabel,
				new GridBagConstraints(1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));

        infoPanel.add(jLabel3,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel4,
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel5,
				new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel6,
				new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        
        infoPanel.add(p1NameLabel,
				new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(30, 0, 0, 30), 0, 0));
        infoPanel.add(p1StonesLabel,
				new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 30), 0, 0));
        infoPanel.add(p1TotalGamesLabel,
				new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 30), 0, 0));
        infoPanel.add(p1TotalStonesLabel,
				new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 30), 0, 0));
        infoPanel.add(p1TotalTimeLabel,
				new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 30), 0, 0));

        infoPanel.add(p2NameLabel,
				new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(30, 0, 0, 10), 0, 0));
        infoPanel.add(p2StonesLabel,
				new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(p2TotalGamesLabel,
				new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(p2TotalStonesLabel,
				new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(p2TotalTimeLabel,
				new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        
        startStopButton.setText("Start Game");
        startStopButton.setToolTipText("Starts a set of the games");
        startStopButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	reversi.startStopButtonAction();
            }
        });
        playPauseButton.setText("Pause");
        playPauseButton.setToolTipText("Pauses the game");
        playPauseButton.setEnabled(false);
        playPauseButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	reversi.playPauseButtonAction();
            }
        });
        loadConfigurationButton.setText("Load Configuration");
        loadConfigurationButton.setToolTipText("Reloads new configuration from the configuration file");
        loadConfigurationButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
            	reversi.loadConfigurationButtonAction();
            }
        });
        
        buttonPanel.add(startStopButton,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        buttonPanel.add(playPauseButton,
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(loadConfigurationButton,
				new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setResizable(true);
					setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					pack();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}  

	void init(final int left, final int top, final int width, final int height) {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setLocation(left,top);
					setSize(width,height);
					setVisible(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void initPlayground(int[][] pg) {
		playgroundPanel.initPlayground(pg);
	}
	
	void update(int[][] pg) {
		playgroundPanel.updatePlayground(pg);
	}
	
}