import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Will on 10/24/2016.
 */
public class LightTable extends JComponent {

    //0 is photo view, 1 is grid view, 2 is split view
    int mode = 0;

    BorderLayout layout = new BorderLayout();

    ArrayList<PhotoComponent> images = new ArrayList<>();
    ArrayList<ThumbnailComponent> thumbnails = new ArrayList<>();
    JScrollPane scrollPane = new JScrollPane();
    JPanel horizPanel = new JPanel();

    ArrayList<Point> points = new ArrayList<Point>();

    ArrayList<Magnet> magnets = new ArrayList<>();


    int imageIndex = -1;
    //setLayout(layout);

    public LightTable() {

        if(mode == 0) {
            BorderLayout layout = new BorderLayout();
        } else if(mode == 1) {
            GridLayout layout = new GridLayout();
        } else if(mode == 2) {
            BorderLayout layout = new BorderLayout();
        }


        setLayout(layout);

        /*MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            public void mouseDragged(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)) {
                    points.add(e.getPoint());

                }
            }


        };*/

        //this.addMouseListener(mouseAdapter);
        //this.addMouseMotionListener(mouseAdapter);


    }

    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        for(int i = 0; i < points.size(); i++) {


            //draw black line
            System.out.println("asdf");
            g.drawLine(points.get(i).x - 1, points.get(i).y - 1, points.get(i).x + 1, points.get(i).y + 1);
            g.fillRect(points.get(i).x - 1, points.get(i).y - 1, 3, 3);
        }

    }


    public void addPhoto(BufferedImage img) {
        PhotoComponent tempComp = new PhotoComponent();
        tempComp.img = img;
        images.add(tempComp);
        imageIndex++;
        thumbnails.add(new ThumbnailComponent(img, imageIndex));
    }

    public void photoLayout(int index) {
        mode = 0;
        BorderLayout layout = new BorderLayout();
        removeAll();
        setLayout(layout);
        if(images.size() > 0) {
            scrollPane.add(images.get(index));

            Album.compMatch(Album.photoComponent, images.get(index));
        }
        add(scrollPane);
        repaint();
    }

    public void splitLayout(int index) {
        mode = 2;
        BorderLayout layout = new BorderLayout();
        removeAll();
        setLayout(layout);
        if(images.size() > 0) {
            scrollPane.add(images.get(index));
            Album.compMatch(Album.photoComponent, images.get(index));
        }
        add(scrollPane, BorderLayout.CENTER);
        BoxLayout boxLayout = new BoxLayout(horizPanel, BoxLayout.X_AXIS);
        horizPanel.setLayout(boxLayout);
        for(int i = 0; i < thumbnails.size(); i++) {
            horizPanel.add(thumbnails.get(i));
        }
        //horizPanel.add(thumbnails.get(0));
        horizPanel.setPreferredSize(new Dimension(200,200));



        add(horizPanel, BorderLayout.SOUTH);
        horizPanel.setVisible(true);
        revalidate();
        repaint();
    }






}
