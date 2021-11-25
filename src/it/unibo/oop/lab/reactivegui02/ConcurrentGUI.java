package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class ConcurrentGUI extends JFrame {
    
    final JLabel label = new JLabel("0");
    final JButton buttonUp = new JButton("Up");
    final JButton buttonDown = new JButton("Down");
    final JButton buttonStop = new JButton("Stop");

    /**
     * 
     */
    private static final long serialVersionUID = -2058404231798424487L;

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * 0.2), (int) (screenSize.getHeight() * 0.1));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(label);
        panel.add(buttonUp);
        panel.add(buttonDown);
        panel.add(buttonStop);
     
        this.setVisible(true);
        
        final Agent agent = new Agent();
        new Thread(agent).start();
        
        buttonUp.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                agent.doIncrement();
            }
        });
        
        buttonDown.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                agent.doDecrement();
            }
        });
        
        buttonStop.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                agent.stopCount();
            }
        });
    }
    
    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean increase = true;
        private int counter;
        
        @Override
        public void run() {
            while (!stop) {
                try {
                    final int count = this.counter;
                    SwingUtilities.invokeAndWait(new Runnable() {
                        
                        @Override
                        public void run() {
                            ConcurrentGUI.this.label.setText(Integer.toString(count));
                        }
                    });
                    if(increase) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InterruptedException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        
        public void doIncrement() {
            this.increase = true;
        }
        
        public void doDecrement() {
            this.increase = false;
        }
        
        public void stopCount() {
            this.stop = true;
        }
    }
}