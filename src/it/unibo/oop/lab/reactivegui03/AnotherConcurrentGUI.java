package it.unibo.oop.lab.reactivegui03;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import it.unibo.oop.lab.reactivegui02.ConcurrentGUI;

public final class AnotherConcurrentGUI extends JFrame {

    public AnotherConcurrentGUI() {
        
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
    }
    
    public static void main(final String...args) {
        
    }
    
}
