/*
 * @(#)SelectTx.java	1.7  98/12/03
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextLayout;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import javax.swing.*;


/**
 * The SelectTx class demonstrates scaling, shearing or rotating an image 
 * and rectangle.
 */
public class SelectTx extends JApplet {

    Demo demo;

    public void init() {
        getContentPane().add(demo = new Demo());
        getContentPane().add("North", new DemoControls(demo));
    }

    public void start() {
        demo.start();
    }
  
    public void stop() {
        demo.stop();
    }



    /**
     * The Demo class performs the painting and the transformations.
     */
    static class Demo extends JPanel implements Runnable {

        private static final int LEFT = 1;
        private static final int XMIDDLE = 2;
        private static final int DOWN = 3;
        private static final int UP = 4;
        private static final int YMIDDLE = 5;
        private static final int XupYup = 6;
        private static final int XdownYdown = 7;
        private static final String[] title = { "Scale" , "Shear", "Rotate" };
        private Image img, original;
        private int iw, ih;
        private Thread thread;
        private BufferedImage bimg;
    
        // the directions of the transformations
        public static final int RIGHT = 0;
        public static final int SCALE = 0;
        public static final int SHEAR = 1;
        public static final int ROTATE = 2;
        public int transformType = ROTATE;
        public double sx, sy;
        public double angdeg;
        public int direction = RIGHT;
    
    
        public Demo() {
            setBackground(Color.white);
            original = getToolkit().getImage(SelectTx.class.getResource("playership.png"));
            try {
                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(original, 0);
                tracker.waitForID(0);
            } catch (Exception e) {}
            iw = original.getWidth(this);
            ih = original.getHeight(this);
        }
    

        public void reset(int w, int h) {
            iw = w/3;
            ih = h/3;
            img = createImage(iw, ih);
            Graphics big = img.getGraphics();
            big.drawImage(original, 0, 0, iw, ih, Color.orange, null);

            if (transformType == SCALE) {
                direction = RIGHT;
                sx = sy = 1.0;
            } else if (transformType == SHEAR) {
                direction = RIGHT;
                sx = sy = 0;
            } else {
                angdeg = 0;
            }
        }

    
        public void step(int w, int h) {
            int rw = iw + 10;
            int rh = ih + 10;
    
            if (transformType == SCALE && direction == RIGHT) {
                sx += .05;
                if (w * .5 - iw * .5 + rw * sx + 10 > w) {
                    direction = DOWN;
                }
            } else if (transformType == SCALE && direction == DOWN) {
               sy += .05;
               if (h * .5 - ih * .5 + rh * sy + 20 > h) {
                   direction = LEFT;
                }
            } else if (transformType == SCALE && direction == LEFT) {
                sx -= .05;
                if (rw * sx - 10 <= -(w * .5 - iw * .5)) {
                    direction = UP;
                }
            } else if (transformType == SCALE && direction == UP) {
                sy -= .05;
                if (rh * sy - 20 <= -(h * .5 - ih * .5)) {
                    direction = RIGHT;
                }
            }
    
            if (transformType == SHEAR && direction == RIGHT) {
                sx += .05;
                if (rw + 2 * rh * sx + 20 > w) {
                    direction = LEFT;
                    sx -= .1;
                }
            } else if (transformType == SHEAR && direction == LEFT) {
                sx -= .05;
                if (rw - 2 * rh * sx + 20 > w) {
                    direction = XMIDDLE;
                }
            } else if (transformType == SHEAR && direction == XMIDDLE) {
                sx += .05;
                if (sx > 0) {
                    direction = DOWN;
                    sx = 0;
                }
            } else if (transformType == SHEAR && direction == DOWN) {
                sy -= .05;
                if (rh - 2 * rw * sy + 20 > h) {
                    direction = UP;
                    sy += .1;
                }
            } else if (transformType == SHEAR && direction == UP) {
                sy += .05;
                if (rh + 2 * rw * sy + 20 > h) {
                    direction = YMIDDLE;
                }
            } else if (transformType == SHEAR && direction == YMIDDLE) {
                sy -= .05;
                if (sy < 0) {
                    direction = XupYup;
                    sy = 0;
                }
            } else if (transformType == SHEAR && direction == XupYup) {
                sx += .05; sy += .05;
                if (rw + 2 * rh * sx + 30 > w || rh + 2 * rw * sy + 30 > h) {
                    direction = XdownYdown;
                }
            } else if (transformType == SHEAR && direction == XdownYdown) {
                sy -= .05; sx -= .05;
                if (sy < 0) {
                    direction = RIGHT;
                    sx = sy = 0.0;
                }
            }
    
            if (transformType == ROTATE) {
                angdeg += 5;
                if (angdeg == 360) { 
                    angdeg = 0;
                }
            }
        }

    
        /* 
         * draws the transformed image, the String describing the current
         * transform, and the current transformation factors.
         */
        public void drawDemo(int w, int h, Graphics2D g2) {
    
            Font font = g2.getFont();
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout tl = new TextLayout(title[transformType], font, frc);
            g2.setColor(Color.black);
            tl.draw(g2, (float) (w/2-tl.getBounds().getWidth()/2), 
                (float) (tl.getAscent()+tl.getDescent()));
    
            if (transformType == ROTATE) {
                String s = Double.toString(angdeg);
                g2.drawString("angdeg=" + s, 2, h-4);
            } else {
                String s = Double.toString(sx);
                s = (s.length() < 5) ? s : s.substring(0,5);
                TextLayout tlsx = new TextLayout("sx=" + s, font, frc);
                tlsx.draw(g2, 2, h-4);
    
                s = Double.toString(sy);
                s = (s.length() < 5) ? s : s.substring(0,5);
                g2.drawString("sy=" + s,(int)(tlsx.getBounds().getWidth()+4), h-4);
            }
    
            if (transformType == SCALE) {
                g2.translate(w/2-iw/2, h/2-ih/2);
                g2.scale(sx, sy);
            } else if (transformType == SHEAR) {
                g2.translate(w/2-iw/2,h/2-ih/2);
                g2.shear(sx, sy);
            } else {
                g2.rotate(Math.toRadians(angdeg),w/2,h/2);
                g2.translate(w/2-iw/2,h/2-ih/2);
            }
            
            g2.setColor(Color.orange);
            g2.fillRect(0, 0, iw+10, ih+10);
            g2.drawImage(img, 5, 5, this);
        }
    
    
        public Graphics2D createGraphics2D(int w, int h) {
            Graphics2D g2 = null;
            if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
                bimg = (BufferedImage) createImage(w, h);
                reset(w, h);
            } 
            g2 = bimg.createGraphics();
            g2.setBackground(getBackground());
            g2.clearRect(0, 0, w, h);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                                RenderingHints.VALUE_RENDER_QUALITY);
            return g2;
        }
    
    
        public void paint(Graphics g) {
            Dimension d = getSize();
            step(d.width, d.height);
            Graphics2D g2 = createGraphics2D(d.width, d.height);
            drawDemo(d.width, d.height, g2);
            g2.dispose();
            g.drawImage(bimg, 0, 0, this);
        }
    
    
        public void start() {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    
    
        public synchronized void stop() {
            thread = null;
        }
    
    
        public void run() {
            Thread me = Thread.currentThread();
            while (thread == me) {
                repaint();
                try {
                    thread.sleep(10);
                } catch (InterruptedException e) { break; }
            }
            thread = null;
        }
    }  // End Demo class



    /**
     * The DemoControls class provides buttons for selecting either
     * scaling, shearing or rotating of the image.
     */
    static class DemoControls extends JPanel implements ActionListener {

        Demo demo;
        JToolBar toolbar;

        public DemoControls(Demo demo) {
            this.demo = demo;
            setBackground(Color.gray);
            add(toolbar = new JToolBar());
            toolbar.setFloatable(false);
            addTool("Scale", false);
            addTool("Shear", true);
            addTool("Rotate", false);
        }

        public void addTool(String str, boolean state) {
            JButton b = (JButton) toolbar.add(new JButton(str));
            b.setBackground(state ? Color.green : Color.lightGray);
            b.addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            for (int i = 0; i < toolbar.getComponentCount(); i++) {
                JButton b = (JButton) toolbar.getComponentAtIndex(i);
                b.setBackground(Color.lightGray);
            }
            JButton b = (JButton) e.getSource();
            b.setBackground(Color.green);
            if (b.getText().equals("Scale")) {
                demo.transformType = demo.SCALE;
                demo.direction = demo.RIGHT;
                demo.sx = demo.sy = 1;
            } else if (b.getText().equals("Shear")) {
                demo.transformType = demo.SHEAR;
                demo.direction = demo.RIGHT;
                demo.sx = demo.sy = 0;
            } else if (b.getText().equals("Rotate")) {
                demo.transformType = demo.ROTATE;
                demo.angdeg = 0;
            }
        }
    } // End DemoControls class


    public static void main(String argv[]) {
        final SelectTx demo = new SelectTx();
        demo.init();
        JFrame f = new JFrame("Java 2D(TM) Demo - SelectTx");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { demo.start(); }
            public void windowIconified(WindowEvent e) { demo.stop(); }
        });
        f.getContentPane().add("Center", demo);
        f.pack();
        f.setSize(new Dimension(400,300));
        f.setVisible(true);
        demo.start();
    }
}
