package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 */
public final class AnotherConcurrentGUI extends JFrame {

    private final JLabel label = new JLabel("0");
    private final JButton buttonUp = new JButton("Up");
    private final JButton buttonDown = new JButton("Down");
    private final JButton buttonStop = new JButton("Stop");
    private static final double[] PROPORTIONS = {0.2, 0.1};
    /**
     * 
     */
    private static final long serialVersionUID = 4223841777480471754L;

    /**
     * 
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * PROPORTIONS[0]), (int) (screenSize.getHeight() * PROPORTIONS[1]));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        this.add(panel);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(label);
        panel.add(buttonUp);
        panel.add(buttonDown);
        panel.add(buttonStop);
        this.setVisible(true);
        // Starting agents.
        final CounterAgent counter = new CounterAgent();
        new Thread(counter).start();
        final TimeLimitAgent limiter = new TimeLimitAgent(counter, 10_000);
        new Thread(limiter).start();
        // Adding listeners to the buttons.
        buttonUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                counter.doIncrement();
            }
        });
        buttonDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                counter.doDecrement();
            }
        });
        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                AnotherConcurrentGUI.this.disableButtons();
                counter.stopCount();
            }
        });
    }

    /**
     *
     */
    public void disableButtons() {
        this.buttonUp.setEnabled(false);
        this.buttonDown.setEnabled(false);
        this.buttonStop.setEnabled(false);
    }

    private class TimeLimitAgent implements Runnable {

        private final CounterAgent agent;
        private final int waitTime;

        /**
         * @param agent
         *      The agent to stop once enough time has passed.
         * @param waitTime
         *      The time that needs to pass before stopping the counter.
         */
        TimeLimitAgent(final CounterAgent agent, final int waitTime) {
            this.agent = agent;
            this.waitTime = waitTime;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(waitTime);
                if (!agent.isStopped()) {
                    AnotherConcurrentGUI.this.disableButtons();
                    agent.stopCount();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class CounterAgent implements Runnable {

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
                            AnotherConcurrentGUI.this.label.setText(Integer.toString(count));
                        }
                    });
                    if (increase) {
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

        public boolean isStopped() {
            return this.stop;
        }
    }
}
