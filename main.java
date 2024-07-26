package RayCaster;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.TreeUI;

import RayCaster.raycast.player;

import java.math.*;
import java.util.HashSet;
import java.util.Set;

public class main {
    static int WIDTH = 1500;
    static int HEIGHT = 1200;

    static int CASTED_RAYS = 100;
    static double FOV = Math.PI / 3;
    static double halfFOV = FOV / 2;
    static double stepAngle = FOV / CASTED_RAYS;

    static player player = new player(100, 100, 0);
    
    static Color[] colors = {
        new Color(255, 0, 0),
        new Color(150, 0, 0),
        new Color(0, 190, 0),
        new Color(0, 100, 0),
        new Color(0, 0, 120),
        new Color(0, 0, 255),
        new Color(255, 255, 120),
        new Color(255, 0, 255),
        new Color(0, 255, 255),
        new Color(255, 255, 255),
        new Color(0, 0, 0),
    };
    static Color lightBlue = (new Color(135, 206, 250));
    static Set<Integer> pressedKeys = new HashSet<Integer>();
    static int mapX = 8;
    static int mapY = 8;
    static int mapS = 64;
    static int[] map = {
        1,1,1,1,1,1,1,1,
        1,0,5,0,0,0,0,1,
        1,0,5,0,0,0,0,1,
        1,0,5,0,0,0,0,1,
        1,0,0,0,0,3,0,1,
        1,0,0,0,3,3,0,1,
        1,0,0,0,0,0,0,1,
        1,1,1,1,1,1,1,1,
    };

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Ray Caster");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setVisible(true);

        JPanel panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(lightBlue);
                g.fillRect(0, 0, getWidth(), getHeight() / 2);
                g.setColor(Color.darkGray);
                g.fillRect(0, getHeight() / 2, getWidth(), getHeight() / 2);
                rayCast(g);
            }
        };

        frame.add(panel);

        panel.setFocusable(true);
        panel.requestFocusInWindow();
        
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        

        Timer timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePlayerState();
                panel.repaint();
            }
        });
        timer.start();
    }

    public static void rayCast(Graphics g) {
        int r, mx, my, mp, dof, side;
        double vx, vy, rx, ry, ra, xo, yo, disV, disH;

        ra = player.angle - halfFOV;
        for (r = 0; r < CASTED_RAYS; r++) {
            ra %= Math.PI * 2;
            // Vertical
            dof = 0; side = 0; disV = 100000;
            double tan = Math.tan(ra);
            if (Math.cos(ra) > 0.001) {
                rx = (((int)player.x >> 6) << 6) + 64;
                ry = (player.x - rx) * tan + player.y;
                xo = 64;
                yo = -xo * tan;
            } else if (Math.cos(ra) < -0.001) {
                rx = (((int)player.x >> 6) << 6) - 0.0001;
                ry = (player.x - rx) * tan + player.y;
                xo = -64;
                yo = -xo * tan;
            } else {
                xo = 64;
                yo = 0;
                rx = player.x;
                ry = player.y;
                dof = 8;
            }
            
            int wallTypeV = 0;
            while (dof < 8) {
                mx = (int)(rx) >> 6;
                my = (int)(ry) >> 6;
                mp = my * mapX + mx;
                if (mp > 0 && mp < mapX * mapY && map[mp] != 0) {
                    wallTypeV = map[mp];
                    // disV = Math.cos(ra) * (rx - player.x) - Math.sin(ra) * (ry - player.y);
                    disV = (rx - player.x) * (rx - player.x) + (ry - player.y) * (ry - player.y);
                    disV = Math.sqrt(disV);
                    dof = 8;
                } else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }
            vx = rx;
            vy = ry;

            // HORIZONTAL
            dof = 0; disH = 100000;
            tan = 1 / tan;
            if (Math.sin(ra) > 0.001) {
                ry = (((int)player.y >> 6) << 6) -0.001;
                rx = (player.y - ry) * tan + player.x;
                yo = -64;
                xo = -yo * tan;
            } else if (Math.sin(ra) < -0.001) {
                ry = (((int)player.y >> 6) << 6) + 64;
                rx = (player.y - ry) * tan + player.x;
                yo = 64;
                xo = -yo * tan;
            } else {
                xo = 0;
                yo = 64;
                rx = player.x;
                ry = player.y;
                dof = 8;
            }
            
            // CAST HORIZONTAL RAY
            int wallTypeH = 0;
            while (dof < 8) {
                mx = (int) (rx) >> 6;
                my = (int) (ry) >> 6;
                mp = my * mapX + mx;
                if (mp > 0 && mp < mapX * mapY && map[mp] != 0) {
                    wallTypeH = map[mp];
                    // disH = Math.cos(ra) * (rx - player.x) - Math.sin(ra) * (ry - player.y);
                    disH = (rx - player.x) * (rx - player.x) + (ry - player.y) * (ry - player.y);
                    disH = Math.sqrt(disH);
                    dof = 8;
                } else {
                    rx += xo;
                    ry += yo;
                    dof += 1;
                }
            }
            
            if (disV < disH) {
                rx = vx;
                ry = vy;
                disH = disV;
                side = 1;
                wallTypeH = wallTypeV;
            }

            // Fix Fisheye effect
            double ca = player.angle - ra;
            disH = disH * Math.cos(ca);
            int wallHeight = (int) ((mapS * 1500) / disH);
            wallHeight = Math.min(wallHeight, HEIGHT);
            int lineOff = (HEIGHT / 2) - wallHeight / 2;

            // Draw Wall
            g.setColor(colors[wallTypeH + side - 1]);
            g.fillRect(r * (WIDTH / CASTED_RAYS), lineOff, 20, wallHeight);
            

            ra += stepAngle;
            // System.out.println(ra);
        }
    }

    public static void updatePlayerState() {
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            player.rotate(-0.1);
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            player.rotate(0.1);
            System.out.println(player.angle);
        }
        if (pressedKeys.contains(KeyEvent.VK_UP)) {
            player.move(2 * Math.cos(player.angle), -2 * Math.sin(player.angle));
        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
            player.move(-2 * Math.cos(player.angle), 2 * Math.sin(player.angle));
        }
    }
}
