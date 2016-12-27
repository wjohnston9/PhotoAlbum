import javafx.scene.effect.Light;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Will on 10/27/2016.
 */
public class ThumbnailComponent extends JComponent {
    long prevTime;
    long currentTime;
    boolean hasBeenClicked = false;

    boolean isSelected = false;
    int number;

    ArrayList<Point> gesturePoints = new ArrayList<Point>();

    boolean[] tags = {false,false,false,false};



    String regex = "";
    int pointCounter = 0;

    String nextPicPattern = "[CSE][DW]";
    Pattern nextImg = Pattern.compile(nextPicPattern);

    String prevPicPattern = "[DSW][CE]";
    Pattern prevImg = Pattern.compile(prevPicPattern);

    //Pattern delImg = Pattern.compile(new String("S+C+E+B+N+A+W+D+S"));
    String delPattern = "[DSC]+[BE]+[NA]+[WD]+[SC]";
    //String delPattern = "S+C+E+B+N+A+W+D+S";
    Pattern delImg = Pattern.compile(delPattern);



    BufferedImage img;
    public ThumbnailComponent(BufferedImage img, int number) {
        this.img = img;
        this.number = number;

        //LightTable parent = (LightTable) getParent();
        MouseAdapter mouseAdapter = new MouseAdapter() {

            //Point pStart = null;
            //Point pEnd = null;
            @Override
            public void mouseClicked(MouseEvent e) {
                LightTable parent = new LightTable();
                if(getParent() instanceof LightTable) {
                    parent = (LightTable) getParent();
                } else {
                    parent = (LightTable) getParent().getParent();
                }
                if(hasBeenClicked) {
                    currentTime = System.nanoTime();

                    if ((currentTime - prevTime) > 1000000000) {
                        hasBeenClicked = false;
                    } else {
                        System.out.println("asdf");

                        parent.photoLayout(number);


                        hasBeenClicked = false;
                    }
                } else {
                    prevTime = System.nanoTime();
                    hasBeenClicked = true;
                }


                //System.out.println(currentTime - prevTime);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    gesturePoints.add(e.getPoint());

                    pointCounter++;

                    if(pointCounter > 10  && gesturePoints.size() > 10) {

                        //System.out.println(getAngle(photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 5), photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 1)));
                        double angle = Album.getAngle(gesturePoints.get(gesturePoints.size() - 9), gesturePoints.get(gesturePoints.size() - 1));

                        if (angle < 22.5 || angle > 337.5) {


                            //System.out.println(photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 1).x -  photoComponent.gesturePoints.get(photoComponent.gesturePoints.size()- 5).x);
                            regex = regex + "S";

                            pointCounter = 0;
                        } else if( 67.5 > angle && angle > 22.5) {

                            regex = regex + "C";

                            pointCounter = 0;
                        } else if(112.5 > angle && angle > 67.5) {
                            regex = regex + "E";

                            pointCounter = 0;

                        } else if(157.5> angle && angle > 112.5) {

                            regex = regex + "B";

                            pointCounter = 0;
                        } else if(202.5 > angle && angle > 157.5) {
                            regex = regex + "N";
                            pointCounter = 0;

                        } else if(247.5 > angle && angle > 202.5) {
                            regex = regex + "A";
                            pointCounter = 0;
                        } else if(292.5 > angle && angle > 247.5) {
                            regex = regex + "W";
                            pointCounter = 0;
                        } else if(337.5 > angle && angle > 292.5) {
                            regex = regex + "D";
                            pointCounter = 0;
                        }
                    }

                    repaint();

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    //System.out.println(regex);
                    gesturePoints.clear();
                    repaint();

                    LightTable lightTable = new LightTable();
                    if(getParent() instanceof LightTable) {
                        lightTable = (LightTable) getParent();
                    } else {
                        lightTable = (LightTable) getParent().getParent();
                    }

                    if(lightTable.mode == 1) {
                        regex = lightTable.thumbnails.get(lightTable.imageIndex).regex;
                    }

                    Matcher m = delImg.matcher(regex);

                    if(m.find()) {
                        //deleteImage();
                        regex = "";
                        lightTable.thumbnails.get(lightTable.imageIndex).regex = "";

                    }

                    m = nextImg.matcher(regex);

                    if(m.find())
                    {

                        if(lightTable.imageIndex < lightTable.images.size() - 1) {

                            lightTable.thumbnails.get(lightTable.imageIndex).isSelected = false;

                            lightTable.imageIndex++;
                            lightTable.thumbnails.get(lightTable.imageIndex).isSelected = true;
                            //photoComponent.img = lightTable.images.get(lightTable.imageIndex).img;
                            //compMatch(photoComponent, lightTable.images.get(lightTable.imageIndex));
                            //photoComponent.repaint();
                            lightTable.repaint();
                        }
                        regex = "";
                        lightTable.thumbnails.get(lightTable.imageIndex).regex = "";
                    }

                    m = prevImg.matcher(regex);

                    if(m.find())
                    {
                        //prevImage();
                        regex = "";
                        lightTable.thumbnails.get(lightTable.imageIndex).regex = "";

                    }




                }



                isSelected = true;
                if(getParent() instanceof LightTable) {
                    LightTable parent = (LightTable) getParent();
                    parent.imageIndex = number;
                    for (int i = 0; i < parent.thumbnails.size(); i++) {
                        if (i != number) {
                            parent.thumbnails.get(i).isSelected = false;
                        }

                    }

                    parent.repaint();
                } else {
                    LightTable parent = (LightTable) getParent().getParent();
                    parent.imageIndex = number;
                    for (int i = 0; i < parent.thumbnails.size(); i++) {
                        if (i != number) {
                            parent.thumbnails.get(i).isSelected = false;
                        }

                    }

                    parent.splitLayout(number);

                    parent.repaint();
                }
                repaint();
            }


        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        /*if(isSelected) {
            g.setColor(Color.red);
            g.fillRect(0,0, img.getWidth() + 10, img.getHeight() + 10);
        } else {
            g.setColor(Color.white);
            g.fillRect(0,0, img.getWidth() + 10, img.getHeight() + 10);
        }*/
        //g.drawImage(img, 5,5, img.getWidth(), img.getHeight(), this);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(.5,.5);

        if(isSelected) {
            g2d.setColor(Color.red);
            g2d.fillRect(0,0, img.getWidth() + 10, img.getHeight() + 10);
        } else {
            g2d.setColor(Color.white);
            g2d.fillRect(0,0, img.getWidth() + 10, img.getHeight() + 10);
        }
        g2d.drawImage(img, 5,5, img.getWidth(), img.getHeight(), this);

        g.setColor(Color.red);
        for(int i = 0; i < gesturePoints.size(); i++) {

            //draw black line
            g.drawLine(gesturePoints.get(i).x - 1, gesturePoints.get(i).y - 1, gesturePoints.get(i).x + 1, gesturePoints.get(i).y + 1);
            g.fillRect(gesturePoints.get(i).x - 1, gesturePoints.get(i).y - 1, 3, 3);
        }
        g.setColor(Color.black);
    }



}
