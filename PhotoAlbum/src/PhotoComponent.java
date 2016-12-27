import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Will on 9/29/2016.
 */
public class PhotoComponent extends JComponent{
    public boolean flipped = false;
    boolean hasBeenClicked = false;
    long currentTime;
    long prevTime;
    int notes = 0;

    Point pStart = null;
    Point pEnd = null;

    ArrayList<Point> points = new ArrayList<Point>();
    ArrayList<Point[]> boxPoints = new ArrayList<>();
    ArrayList<String> boxStrings = new ArrayList<>();

    ArrayList<Point> gesturePoints = new ArrayList<Point>();

    //vacation, family, school, work
    boolean[] tags = {false,false,false,false};

    boolean selectionMode = false;
    Point startingPoint = new Point();
    double maxX = 0;
    double maxY = 0;
    double minX = getSize().getWidth();
    double minY = getSize().getHeight();

    BufferedImage img;
    public PhotoComponent() {

        boxStrings.add("");

        /*try {
            img = ImageIO.read(new File("C:\\Users\\Will\\IdeaProjects\\UIHW2\\src\\charlie.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        this.setFocusable(true);
        this.requestFocus();






    }



    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHints(rh);






        if(flipped && img != null) {

            g.drawRect(0,0,img.getWidth(),img.getHeight());



            //System.out.println(points.size());
            g.setColor(Color.black);
            for(int i = 0; i < points.size(); i++) {

                if(points.get(i).x +1 < maxX && points.get(i).x - 1 > minX && points.get(i).y + 1 < maxY && points.get(i).y - 1 > minY && selectionMode) {
                //if(selectionMode) {
                    g.setColor(Color.blue);
                } else {
                    g.setColor(Color.black);

                }

                //draw black line
                g.drawLine(points.get(i).x - 1, points.get(i).y - 1, points.get(i).x + 1, points.get(i).y + 1);
                g.fillRect(points.get(i).x - 1, points.get(i).y - 1, 3, 3);
            }



            if(pEnd != null) {


                Color postIt = new Color(255, 255, 204);
                g.setColor(postIt);

                g.drawRect(pStart.x, pStart.y, pEnd.x - pStart.x, pEnd.y - pStart.y);
                for(int i = 0; i < boxPoints.size(); i++) {
                    if(boxPoints.get(i)[1].x < maxX && boxPoints.get(i)[0].x > minX && boxPoints.get(i)[1].y < maxY && boxPoints.get(i)[0].y > minY && selectionMode) {
                        //if(selectionMode) {
                        g.setColor(new Color(81,184,255));
                    } else {
                        g.setColor(postIt);
                    }

                    g.fillRect(boxPoints.get(i)[0].x, boxPoints.get(i)[0].y, boxPoints.get(i)[1].x - boxPoints.get(i)[0].x, boxPoints.get(i)[1].y - boxPoints.get(i)[0].y);

                }



            }

            if(boxStrings.size() > 0 && boxPoints.size() > 0) {
                for(int i = 0; i < notes; i++) {
                    g.setColor(Color.BLACK);


                    if(g.getFontMetrics().stringWidth(boxStrings.get(i)) > boxPoints.get(i)[1].x) {
                        //boxStrings.set(i, boxStrings.get(i) + "\n");
                        //System.out.println("TEST");
                    }

                    for(int j = 0; j < 1 +((g.getFontMetrics().stringWidth(boxStrings.get(i))) / boxPoints.get(i)[1].x) ; j++) {
                        g.drawString(boxStrings.get(i), boxPoints.get(i)[0].x, boxPoints.get(i)[0].y + 10 + 10*j);
                    }



                }
            }


        } else if(img != null) {


            g.drawImage(img, 0,0, img.getWidth(), img.getHeight(), this);

        }


        g.setColor(Color.red);
        for(int i = 0; i < gesturePoints.size(); i++) {

            //draw black line
            g.drawLine(gesturePoints.get(i).x - 1, gesturePoints.get(i).y - 1, gesturePoints.get(i).x + 1, gesturePoints.get(i).y + 1);
            g.fillRect(gesturePoints.get(i).x - 1, gesturePoints.get(i).y - 1, 3, 3);
        }
        g.setColor(Color.black);

    }

}
