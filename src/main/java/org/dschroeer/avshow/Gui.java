/*
 *  Avshow is a slideshow program.
 *  Copyright (C) 2019-2020  David Schröer <tengcomplexATgmail.com>
 *
 *
 *  This file is part of Avshow.
 *
 *  Avshow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Avshow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Avshow.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.dschroeer.avshow;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class Gui {
  static private final Logger L = Logger.getLogger(Gui.class.getName());

  private ImageAndTextComponent stage;
  private JFrame frame;
  private AvConsumer consumer;
  private final Font font = new Font("Dialog", Font.PLAIN, 16);
  private final Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit()
      .getDesktopProperty("awt.font.desktophints");
  private boolean showFileNames = true;

  // Transparent 16 x 16 pixel cursor image.
  private final BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

  // Create a new blank cursor.
  private final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
      cursorImg, new Point(0, 0), "blank cursor");

  private final ActionListener mouseDisappearer = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      frame.setCursor(blankCursor);
    }
  };

  // Make mouse cursor disappear after 3 seconds
  private final Timer mouseTimer = new Timer(3000, mouseDisappearer);

  @SuppressWarnings("serial")
  class ImageAndTextComponent extends JComponent {
    public static final long RUNNING_TIME = 500;

    private String audioTrackName = "Initializing...";
    private String pictureName = "";

    private float alpha = 0f;
    private long startTime = -1;

    private BufferedImage inImage;
    private BufferedImage outImage;

    public void setImg(Image img) {
      alpha = 0f;
      Timer timer = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (startTime < 0) {
            startTime = System.currentTimeMillis();
            outImage = (BufferedImage) img;
          } else {
            long time = System.currentTimeMillis();
            long duration = time - startTime;
            if (duration >= RUNNING_TIME) {
              startTime = -1;
              ((Timer) e.getSource()).stop();
              inImage = outImage;
              alpha = 0f;
            } else {
              alpha = 1f - ((float) duration / (float) RUNNING_TIME);
            }
            repaint();
          }
        }
      });
      timer.start();
    }

    public void setFileNames(String audioTrackName, String pictureName) {
      this.audioTrackName = audioTrackName;
      this.pictureName = pictureName;
      repaint();
    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

      Graphics2D g2d = (Graphics2D) g;
      if (desktopHints != null) {
        g2d.setRenderingHints(desktopHints);
      } else {
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      }
      int x;
      int y;
      if (inImage != null) {
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        x = (getWidth() - inImage.getWidth()) / 2;
        y = (getHeight() - inImage.getHeight()) / 2;
        g2d.drawImage(inImage, x, y, this);
      }
      if (outImage != null) {
        g2d.setComposite(AlphaComposite.SrcOver.derive(1f - alpha));
        x = (getWidth() - outImage.getWidth()) / 2;
        y = (getHeight() - outImage.getHeight()) / 2;
        g2d.drawImage(outImage, x, y, this);
      }

      g.setFont(font);
      g.setColor(Color.WHITE);
      if (showFileNames) {
        g.drawString(audioTrackName, 45, 45);
        g.drawString(pictureName, 45, 60);
      }
      g2d.dispose();
    }
  }

  public Gui() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();
    frame = new JFrame(gs[Config.DISPLAY_NUMBER].getDefaultConfiguration());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    stage = new ImageAndTextComponent();
    frame.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        L.info("keyChar: " + e.getKeyChar());
        if (e.getKeyCode() == KeyEvent.VK_N) {
          consumer.cleanup();
          synchronized (consumer) {
            consumer.notify();
          }
        } else if (e.getKeyCode() == KeyEvent.VK_V) {
          showFileNames = !showFileNames;
          stage.repaint();
        } else if (e.getKeyCode() == KeyEvent.VK_Q || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          L.info("Quit program");
          System.exit(0);
        }
      }
    });
    frame.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        if(frame.getCursor().equals(blankCursor)) {
          frame.setCursor(Cursor.getDefaultCursor());
        }
      }
    });
    frame.getContentPane().add(stage, BorderLayout.CENTER);

    if(Config.DISPLAY_WIDTH != -1 && Config.DISPLAY_HEIGHT != -1) {
      frame.setSize(Config.DISPLAY_WIDTH, Config.DISPLAY_HEIGHT);
      frame.setLocation(Config.DISPLAY_POSITION_X, Config.DISPLAY_POSITION_Y);
    } else {
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);
      gs[Config.DISPLAY_NUMBER].setFullScreenWindow(frame);
    }
    frame.setVisible(true);
    mouseTimer.start();
  }

  public void setConsumer(AvConsumer consumer) {
    this.consumer = consumer;
  }

  public void setFileNames(String audioTrackName, String pictureName) {
    stage.setFileNames(audioTrackName, pictureName);
  }

  public void setPicture(String picturePath) throws IOException, IllegalArgumentException, NullPointerException {
    L.info("Picture " + picturePath + " loading...");
    BufferedImage image = ImageIO.read(new File(picturePath));
    L.info("Picture " + picturePath + " loaded");
    Dimension origDim = new Dimension(image.getWidth(), image.getHeight());
    Dimension targetDim = new Dimension(frame.getWidth(), frame.getHeight());
    Dimension newDim = getScaledDimension(origDim, targetDim);
    ImageIcon imageIcon = new ImageIcon(resize(image, newDim.width, newDim.height));
    L.info("Picture " + picturePath + " scaled from " + origDim + " to " + newDim);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        stage.setImg(imageIcon.getImage());
      }
    });
  }

  private Image resize(Image originalImage, int newWidth, int newHeight) {
    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = resizedImage.createGraphics();
    g.setComposite(AlphaComposite.Src);
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
    g.dispose();
    return resizedImage;
  }

  private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
    int original_width = imgSize.width;
    int original_height = imgSize.height;
    int bound_width = boundary.width;
    int bound_height = boundary.height;
    int new_width = original_width;
    int new_height = original_height;
    // first check if we need to scale width
    if (original_width != bound_width) {
      // scale width to fit
      new_width = bound_width;
      // scale height to maintain aspect ratio
      new_height = (new_width * original_height) / original_width;
    }
    // then check if we need to scale even with the new height
    if (new_height > bound_height) {
      // scale height to fit instead
      new_height = bound_height;
      // scale width to maintain aspect ratio
      new_width = (new_height * original_width) / original_height;
    }
    return new Dimension(new_width, new_height);
  }
}
