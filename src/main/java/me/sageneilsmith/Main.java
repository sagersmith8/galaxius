package me.sageneilsmith;

import de.articdive.jnoise.generators.noise_parameters.simplex_variants.Simplex2DVariant;
import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static final int SEED = Integer.parseInt(JOptionPane.showInputDialog("SEED", "50"));
    private static final int SPEED = Integer.parseInt(JOptionPane.showInputDialog("SPEED", "1"));

    private static final double SCALE = Double.parseDouble(JOptionPane.showInputDialog("SCALE", ".0007"));

    private static final int MAP_SIZE = Integer.parseInt(JOptionPane.showInputDialog("MAP_SIZE", "900"));
    private static final int GUY_SIZE = Integer.parseInt(JOptionPane.showInputDialog("GUY_SIZE", "5"));

    private static final int NUM_TERRAINS = Integer.parseInt(JOptionPane.showInputDialog("NUM_TERRAINS", "10"));

    private static final Dimension DIMENSION = new Dimension(MAP_SIZE, MAP_SIZE);

    private static final Set<Action> USER_ACTIONS = new HashSet<>();

    enum Action {
        MOVE_LEFT,
        MOVE_RIGHT,
        MOVE_UP,
        MOVE_DOWN
    }

    private static final JNoise NOISE = JNoise.newBuilder()
            .scale(SCALE)
            .abs()
            .fastSimplex(FastSimplexNoiseGenerator.newBuilder()
                    .setSeed(SEED)
                    .setVariant2D(Simplex2DVariant.IMPROVE_X)
                    .build())
            .build();
    private static final JFrame FRAME = new JFrame("Galaxius");
    private static final JPanel PANEL = new JPanel();
    private static final BufferedImage IMAGE = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_RGB);
    private static final JLabel MAP = new JLabel(new ImageIcon(IMAGE));
    private static final Point POSITION = new Point(0, 0);
    private static final JPanel INVENTORY_PANEL = new JPanel();


    static {
        INVENTORY_PANEL.setLayout(new BorderLayout());
        INVENTORY_PANEL.add(new JLabel("Inventory"), BorderLayout.NORTH);
        PANEL.setLayout(new BorderLayout());
        PANEL.add(INVENTORY_PANEL, BorderLayout.SOUTH);
        PANEL.add(MAP, BorderLayout.CENTER);
        FRAME.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {}

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_D) {
                    USER_ACTIONS.add(Action.MOVE_RIGHT);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_A) {
                    USER_ACTIONS.add(Action.MOVE_LEFT);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_UP || keyEvent.getKeyCode() == KeyEvent.VK_W) {
                    USER_ACTIONS.add(Action.MOVE_UP);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN || keyEvent.getKeyCode() == KeyEvent.VK_S) {
                    USER_ACTIONS.add(Action.MOVE_DOWN);
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT || keyEvent.getKeyCode() == KeyEvent.VK_D) {
                    USER_ACTIONS.remove(Action.MOVE_RIGHT);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT || keyEvent.getKeyCode() == KeyEvent.VK_A) {
                    USER_ACTIONS.remove(Action.MOVE_LEFT);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_UP || keyEvent.getKeyCode() == KeyEvent.VK_W) {
                    USER_ACTIONS.remove(Action.MOVE_UP);
                }
                if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN || keyEvent.getKeyCode() == KeyEvent.VK_S) {
                    USER_ACTIONS.remove(Action.MOVE_DOWN);
                }
            }
        });
        FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PANEL.setPreferredSize(DIMENSION);
        IMAGE.setRGB(MAP_SIZE /2, MAP_SIZE /2, 255255255);
        FRAME.getContentPane().add(PANEL);
        FRAME.pack();
        FRAME.setVisible(true);
    }

    private static int noiseToTerrain(double noise) {
        return 255255255/((int)(NUM_TERRAINS*noise)+1);
    }

    private static void drawTerrain() {
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                IMAGE.setRGB(x, y, noiseToTerrain(NOISE.evaluateNoise(x+POSITION.x, y+POSITION.y)));
                if ((x+POSITION.x) % 500 == 0 && (y+POSITION.y) % 500 == 0) {
                    IMAGE.setRGB(x, y, 0);
                }
            }
        }
        for (int x = (MAP_SIZE/2)-(GUY_SIZE/2); x < (MAP_SIZE/2)+(GUY_SIZE/2); x++) {
            for (int y = (MAP_SIZE/2)-(GUY_SIZE/2); y < (MAP_SIZE/2)+(GUY_SIZE/2); y++) {
                IMAGE.setRGB(x, y, 0);
            }
        }
        MAP.repaint();
        MAP.revalidate();
    }

    private static void calculateUserActions() {
        for (Action action: USER_ACTIONS) {
            switch (action) {
                case MOVE_UP:
                    POSITION.y-=SPEED;
                    break;
                case MOVE_DOWN:
                    POSITION.y+=SPEED;
                    break;
                case MOVE_LEFT:
                    POSITION.x-=SPEED;
                    break;
                case MOVE_RIGHT:
                    POSITION.x+=SPEED;
                    break;
            }
        }
    }

    public static void main(String[] args) {
        Timer time = new Timer();
        time.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                calculateUserActions();
                drawTerrain();
            }
        }, 0, 10);
    }
}