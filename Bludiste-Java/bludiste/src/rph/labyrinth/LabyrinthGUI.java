package rph.labyrinth;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class LabyrinthGUI extends JFrame {
	
	private static final long serialVersionUID = -4058579359403964844L;

    PlaygroundPanel playgroundPanel = new PlaygroundPanel();
    JPanel infoPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    JLabel jLabel3 = new JLabel();
    JLabel jLabel4 = new JLabel();
    JLabel jLabel5 = new JLabel();
    JLabel jLabel6 = new JLabel();
    JLabel jLabel7 = new JLabel();
    JLabel jLabel8 = new JLabel();

    JLabel playerLabel = new JLabel();
    JLabel mapLabel = new JLabel();
    JLabel statusLabel = new JLabel();
    JLabel curTimeLabel = new JLabel();
    JLabel curPathLabel = new JLabel();
    JLabel totalMapsLabel = new JLabel();
    JLabel totalPathLabel = new JLabel();
    JLabel totalTimeLabel = new JLabel();
	
    JButton planMapButton = new JButton();
    JButton makeStepsButton = new JButton();
    JButton loadConfigurationButton = new JButton();
	
	LabyrinthGUI(final Labyrinth labyrinth) {
		setTitle("Labyrinth");
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(playgroundPanel,
				new GridBagConstraints(0, 0, 1, 2, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		this.getContentPane().add(infoPanel,
				new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		this.getContentPane().add(buttonPanel,
				new GridBagConstraints(1, 1, 1, 1, 0.1, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 10, 10), 0, 0));
		
		infoPanel.setLayout(new GridBagLayout());
        jLabel1.setText("Player : ");
        jLabel2.setText("Map : ");
        
        jLabel3.setText("Status : ");
        jLabel4.setText("Current time : ");
        jLabel5.setText("Current path length : ");
        jLabel6.setText("Total maps completed / fails : ");
        jLabel7.setText("Total path length : ");
        jLabel8.setText("Total time : ");

        infoPanel.add(jLabel1,
				new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel2,
				new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel3,
				new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel4,
				new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel5,
				new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel6,
				new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel7,
				new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(jLabel8,
				new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        
        infoPanel.add(playerLabel,
				new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
        infoPanel.add(mapLabel,
				new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(statusLabel,
				new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(curTimeLabel,
				new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(curPathLabel,
				new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(totalMapsLabel,
				new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(totalPathLabel,
				new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        infoPanel.add(totalTimeLabel,
				new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 10), 0, 0));
        
        
        planMapButton.setText("Find Path");
        planMapButton.setToolTipText("Calls find path method a proceeds it without any stop to its return");
        planMapButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	labyrinth.planAction(false);
            }
        });
        makeStepsButton.setText("Plan Next Step");
        makeStepsButton.setToolTipText("Calls find path method a proceeds it with user defined stops to show debugging paths");
        makeStepsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	labyrinth.planAction(true);
            }
        });
        loadConfigurationButton.setText("Load Configuration");
        loadConfigurationButton.setToolTipText("Reloads new configuration from the configuration file");
        loadConfigurationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	labyrinth.loadConfigurationButtonAction();
            }
        });
        
        buttonPanel.add(planMapButton,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        buttonPanel.add(makeStepsButton,
				new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        buttonPanel.add(loadConfigurationButton,
				new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        
		try {
			SwingUtilities.invokeLater(new Runnable() {
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
	
	void updatePaths(LinkedList<Path> paths) {
		playgroundPanel.updatePaths(paths);
	}
	
}