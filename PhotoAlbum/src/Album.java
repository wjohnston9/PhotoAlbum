import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Will Johnston
 * wjohnston9@gatech.edu
 *
 */





public class Album extends JFrame{

    long prevTime;
    long currentTime;
    boolean hasBeenClicked = false;
    boolean flipped = false;
    boolean canDraw = true;
    static File file;


    JFrame frame = (JFrame) SwingUtilities.getRoot(this);

    //Menu Panel
    JPanel p=new JPanel();
    JMenuBar menuBar = new JMenuBar();
    JPanel content = new JPanel();


    JMenu fileMenu = new JMenu("File");

    JMenuItem imp = new JMenuItem("Import");
    JMenuItem del = new JMenuItem("Delete");
    JMenuItem exit = new JMenuItem("Exit");

    JMenu viewMenu = new JMenu("View");



    JRadioButton photo = new JRadioButton("Photo View");
    JRadioButton grid = new JRadioButton("Grid View");
    JRadioButton split = new JRadioButton("Split View");

    JFileChooser fileChooser = new JFileChooser();
    //End of Menu Panel

    //Left Control Panel
    JPanel leftPanel = new JPanel();





    JRadioButton drawingRadio = new JRadioButton("Drawing");
    JRadioButton textRadio = new JRadioButton("Text");
    Dimension pageButtonDims = new Dimension(100,100);

    JButton pgForward = new JButton("Page Forward");
    JButton pgBackward = new JButton("Page Backward");
    //End of Left Control Panel

    //Bottom Text Panel
    JPanel bottomPanel = new JPanel();

    JTextArea statusText = new JTextArea();
    //End of Bottom Text Panel


    //Photo Component and its Scroll Pane
    JScrollPane scrollPane = new JScrollPane();
    static PhotoComponent photoComponent = new PhotoComponent();
    LightTable lightTable = new LightTable();

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

    String vacPattern = "[SC][EBN]";
    Pattern vacImg = Pattern.compile(vacPattern);

    String workPattern = "[NB][ESC]";
    Pattern workImg = Pattern.compile(workPattern);

    String famPattern = "[NBE][WDS]";
    Pattern famImg = Pattern.compile(famPattern);

    String schoolPattern = "[WDS][NBE]";
    Pattern schoolImg = Pattern.compile(schoolPattern);

    String circlePattern = "[EC]+[SD]+[WA]+[NB]+[EC]";
    Pattern circled = Pattern.compile(circlePattern);


    JCheckBox vacationButton = new JCheckBox("Vacation");
    JCheckBox familyButton = new JCheckBox("Family");
    JCheckBox schoolButton = new JCheckBox("School");
    JCheckBox workButton = new JCheckBox("Work");

    JRadioButton magnetModeButton = new JRadioButton("Magnet Mode");
    JButton vacationMagButton = new JButton("Vacation Magnet");
    JButton familyMagButton = new JButton("Family Magnet");
    JButton schoolMagButton = new JButton("School Magnet");
    JButton workMagButton = new JButton("Work Magnet");





    /*boolean selectionMode = false;
    Point startingPoint = new Point();
    double maxX = 0;
    double maxY = 0;
    double minX = getSize().getWidth();
    double minY = getSize().getHeight();*/

    public static void compMatch(PhotoComponent p1, PhotoComponent p2) {
        p1.boxPoints = p2.boxPoints;
        p1.points = p2.points;
        p1.boxStrings = p2.boxStrings;
        p1.notes = p2.notes;
        p1.pStart = p2.pStart;
        p1.pEnd = p2.pEnd;
        p1.img = p2.img;
        p1.tags = p2.tags;

    }

    public void nextImage() {
        statusText.setText("Forward");
        if(lightTable.imageIndex < lightTable.images.size() - 1) {

            lightTable.thumbnails.get(lightTable.imageIndex).isSelected = false;

            lightTable.imageIndex++;
            lightTable.thumbnails.get(lightTable.imageIndex).isSelected = true;
            photoComponent.img = lightTable.images.get(lightTable.imageIndex).img;
            compMatch(photoComponent, lightTable.images.get(lightTable.imageIndex));

            vacationButton.setSelected(photoComponent.tags[0]);
            familyButton.setSelected(photoComponent.tags[1]);
            schoolButton.setSelected(photoComponent.tags[2]);
            workButton.setSelected(photoComponent.tags[3]);

            photoComponent.repaint();
            lightTable.repaint();
        }

    }

    public void prevImage() {
        statusText.setText("Backward");
        if(lightTable.imageIndex > 0) {
            lightTable.thumbnails.get(lightTable.imageIndex).isSelected = false;

            lightTable.imageIndex--;
            lightTable.thumbnails.get(lightTable.imageIndex).isSelected = true;
            photoComponent.img = lightTable.images.get(lightTable.imageIndex).img;
            compMatch(photoComponent, lightTable.images.get(lightTable.imageIndex));

            vacationButton.setSelected(photoComponent.tags[0]);
            familyButton.setSelected(photoComponent.tags[1]);
            schoolButton.setSelected(photoComponent.tags[2]);
            workButton.setSelected(photoComponent.tags[3]);

            photoComponent.repaint();
            lightTable.repaint();
        }
    }

    public void deleteImage() {
        statusText.setText("Delete");
        if(lightTable.imageIndex > -1) {
            photoComponent.img = null;
            lightTable.images.remove(lightTable.imageIndex);
            lightTable.thumbnails.remove(lightTable.imageIndex);

            lightTable.imageIndex--;

            scrollPane.repaint();
        }
    }

    public static double getAngle(Point center, Point target) {
        double angle = Math.toDegrees(Math.atan2(target.x - center.x, target.y - center.y));


        if(angle < 0) {
            angle += 360;
        }
        return angle;
    }




    public static void main(String[] args) {
        Album pictureSelect = new Album();
    }

    public Album() {


        super("Album");
        setSize(1500, 900);
        setMinimumSize(new Dimension(600,600));
        setResizable(true);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);

        setFocusable(true);

        vacationMagButton.setVisible(false);
        familyMagButton.setVisible(false);
        schoolMagButton.setVisible(false);
        workMagButton.setVisible(false);

        // --Listeners--
        class ImportActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                statusText.setText("Import");
                int returnVal = fileChooser.showOpenDialog(p);

                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    try {

                        lightTable.addPhoto(ImageIO.read(file));

                        //System.out.println(lightTable.imageIndex);

                        /*if(lightTable.imageIndex > 0) {
                            compMatch(lightTable.images.get(lightTable.imageIndex - 1), photoComponent);
                        }
                        photoComponent.img = lightTable.images.get(lightTable.imageIndex).img;

                        if(lightTable.mode == 1) {
                            lightTable.add(lightTable.thumbnails.get(lightTable.imageIndex));
                            System.out.println(lightTable.imageIndex);
                            lightTable.repaint();
                        }*/

                    }  catch (IOException i) {
                        i.printStackTrace();
                    }

                    if(lightTable.imageIndex > 0) {
                        compMatch(lightTable.images.get(lightTable.imageIndex - 1), photoComponent);
                    }
                    photoComponent.img = lightTable.images.get(lightTable.imageIndex).img;

                    if(lightTable.mode == 1) {

                        lightTable.add(lightTable.thumbnails.get(lightTable.imageIndex));
                        System.out.println(lightTable.imageIndex);
                        repaint();
                        setVisible(true);
                    } else if(lightTable.mode == 2) {
                        if(lightTable.imageIndex > 0) {
                            compMatch(photoComponent, lightTable.images.get(0));
                        }

                        lightTable.splitLayout(lightTable.imageIndex);
                        setVisible(true);
                    }
                    repaint();
                    photoComponent.repaint();

                }



            }
        }

        class ExitActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }
        }

        class DeleteActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                deleteImage();

            }
        }

        class ForwardActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                nextImage();

            }
        }

        class BackwardActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                prevImage();

            }
        }

        class TagActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JCheckBox temp = (JCheckBox) e.getSource();



                if(temp.getText() == "Vacation" ) {
                    photoComponent.tags[0] = temp.isSelected();
                    lightTable.thumbnails.get(lightTable.imageIndex).tags[0] = temp.isSelected();
                } else if(temp.getText() == "Family" ) {
                    photoComponent.tags[1] = temp.isSelected();
                    lightTable.thumbnails.get(lightTable.imageIndex).tags[1] = temp.isSelected();

                } else if(temp.getText() == "School" ) {
                    photoComponent.tags[2] = temp.isSelected();
                    lightTable.thumbnails.get(lightTable.imageIndex).tags[2] = temp.isSelected();

                } else if(temp.getText() == "Work" ) {
                    photoComponent.tags[3] = temp.isSelected();
                    lightTable.thumbnails.get(lightTable.imageIndex).tags[3] = temp.isSelected();

                }

            }
        }

        class MagnetButtonListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JButton temp = (JButton) e.getSource();


                if(temp.getText() == "Vacation Magnet" ) {
                    //System.out.println("hi");
                    Point point = new Point(100,100);
                    Magnet newMag = new Magnet(point, 0);
                    lightTable.magnets.add(newMag);


                } else if(temp.getText() == "Family Magnet" ) {
                    Point point = new Point(350,100);
                    Magnet newMag = new Magnet(point, 1);
                    lightTable.magnets.add(newMag);

                } else if(temp.getText() == "School Magnet" ) {
                    Point point = new Point(100,350);
                    Magnet newMag = new Magnet(point, 2);
                    lightTable.magnets.add(newMag);

                } else if(temp.getText() == "Work Magnet" ) {
                    Point point = new Point(350,350);
                    Magnet newMag = new Magnet(point, 3);
                    lightTable.magnets.add(newMag);

                }

                for(int i = 0; i < lightTable.magnets.size(); i++) {

                    Magnet mag = lightTable.magnets.get(i);
                    mag.setLocation(mag.coord);
                    mag.setSize(80,20);
                    lightTable.add(mag);
                    //lightTable.magnets.get(i).setLocation(lightTable.magnets.get(i).coord);
                    System.out.println("TEST");
                }

                repaint();
            }
        }

        class ViewActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JRadioButton item = (JRadioButton) e.getSource();
                statusText.setText(item.getText());

                if(item.equals(grid)) {
                    photo.setSelected(false);
                    split.setSelected(false);
                    lightTable.mode = 1;
                    lightTable.removeAll();
                    GridLayout gridLayout = new GridLayout(3,3,5,5);
                    lightTable.setLayout(gridLayout);
                    lightTable.repaint();

                    for(int i = 0; i < lightTable.thumbnails.size(); i++) {
                        lightTable.add(lightTable.thumbnails.get(i));

                    }
                    //lightTable.repaint();

                } else if(item.equals(split)) {
                    photo.setSelected(false);
                    grid.setSelected(false);
                    lightTable.mode = 2;
                    BorderLayout borderLayout = new BorderLayout();
                    FlowLayout flowLayout = new FlowLayout();

                    JPanel horizPanel = new JPanel();
                    horizPanel.setLayout(flowLayout);
                    for(int i = 0; i < lightTable.thumbnails.size(); i++) {
                        horizPanel.add(lightTable.thumbnails.get(i));
                    }
                    horizPanel.setVisible(true);

                    lightTable.setLayout(borderLayout);
                    lightTable.add(horizPanel,BorderLayout.SOUTH);
                    lightTable.splitLayout(lightTable.imageIndex);
                    //lightTable.add(new JButton("asdf"), BorderLayout.SOUTH);

                    lightTable.repaint();

                } else {
                    grid.setSelected(false);
                    split.setSelected(false);
                    lightTable.mode = 0;
                    BorderLayout borderLayout = new BorderLayout();
                    lightTable.setLayout(borderLayout);
                    lightTable.photoLayout(lightTable.imageIndex);
                    lightTable.repaint();
                }

            }
        }

        class RadioActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JRadioButton item = (JRadioButton) e.getSource();

                if(item.equals(drawingRadio)) {
                    textRadio.setSelected(false);
                    canDraw = true;
                } else {
                    drawingRadio.setSelected(false);
                    canDraw = false;
                    photoComponent.requestFocusInWindow();
                }

            }
        }

        class MagnetToggleActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {

                if(magnetModeButton.isSelected()) {
                    vacationMagButton.setVisible(true);
                    familyMagButton.setVisible(true);
                    schoolMagButton.setVisible(true);
                    workMagButton.setVisible(true);
                    lightTable.setLayout(null);
                } else {
                    vacationMagButton.setVisible(false);
                    familyMagButton.setVisible(false);
                    schoolMagButton.setVisible(false);
                    workMagButton.setVisible(false);
                    GridLayout gridLayout = new GridLayout(3,3,5,5);
                    lightTable.setLayout(gridLayout);
                }
                repaint();
            }
        }




        //Add Listeners to their elements
        imp.addActionListener(new ImportActionListener());
        exit.addActionListener(new ExitActionListener());
        del.addActionListener(new DeleteActionListener());
        photo.addActionListener(new ViewActionListener());
        grid.addActionListener(new ViewActionListener());
        split.addActionListener(new ViewActionListener());

        Dimension pageButtonDims = new Dimension(100,100);
        pgBackward.setSize(pageButtonDims);
        pgForward.setSize(pageButtonDims);
        pgBackward.addActionListener(new BackwardActionListener());
        pgForward.addActionListener(new ForwardActionListener());

        textRadio.addActionListener(new RadioActionListener());
        drawingRadio.addActionListener(new RadioActionListener());

        vacationButton.addActionListener(new TagActionListener());
        familyButton.addActionListener(new TagActionListener());
        workButton.addActionListener(new TagActionListener());
        schoolButton.addActionListener(new TagActionListener());

        magnetModeButton.addActionListener(new MagnetToggleActionListener());

        vacationMagButton.addActionListener(new MagnetButtonListener());
        familyMagButton.addActionListener(new MagnetButtonListener());
        workMagButton.addActionListener(new MagnetButtonListener());
        schoolMagButton.addActionListener(new MagnetButtonListener());



        //Create the Menu Bar
        fileMenu.add(imp);
        fileMenu.add(del);
        fileMenu.add(exit);
        menuBar.add(fileMenu);

        photo.setSelected(true);
        viewMenu.add(photo);
        viewMenu.add(grid);
        viewMenu.add(split);
        menuBar.add(viewMenu);

        //Create Left Control Panel
        //GridLayout gridLayout = new GridLayout(7, 0);
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        leftPanel.setLayout(gridBagLayout);
        leftPanel.add(vacationButton, c);
        c.gridx = 1;
        c.gridy = 0;
        leftPanel.add(familyButton,c);
        c.gridx = 0;
        c.gridy = 1;
        leftPanel.add(schoolButton,c);
        c.gridx = 1;
        c.gridy = 1;
        leftPanel.add(workButton,c);

        drawingRadio.setSelected(true);
        c.gridx = 0;
        c.gridy = 2;
        leftPanel.add(drawingRadio, c);
        c.gridx = 1;
        c.gridy = 2;
        leftPanel.add(textRadio, c);
        c.gridx = 0;
        c.gridy = 3;
        leftPanel.add(magnetModeButton, c);
        c.gridx = 0;
        c.gridy = 4;
        leftPanel.add(vacationMagButton, c);
        c.gridx = 1;
        c.gridy = 4;
        leftPanel.add(familyMagButton, c);
        c.gridx = 0;
        c.gridy = 5;
        leftPanel.add(schoolMagButton, c);
        c.gridx = 1;
        c.gridy = 5;
        leftPanel.add(workMagButton, c);

        c.gridx = 0;
        c.gridy = 6;
        c.insets = new Insets(200,0,0,0);  //top padding


        leftPanel.add(pgBackward, c);
        c.gridx = 1;
        c.gridy = 6;
        leftPanel.add(pgForward,c );


        //content.add(photoComponent);
        //Dimension dimension = new Dimension(photoComponent.img.getWidth(),photoComponent.img.getHeight());
        //photoComponent.setPreferredSize(dimension);



        Magnet mag = new Magnet(new Point(50,50), 0);
        lightTable.add(mag);
        mag.setLocation(50,50);


        scrollPane.getViewport().add(photoComponent);



        MouseAdapter mouseAdapter = new MouseAdapter() {

            //Point pStart = null;
            //Point pEnd = null;
            @Override
            public void mouseClicked(MouseEvent e) {


                    if (hasBeenClicked) {
                        currentTime = System.nanoTime();

                        if ((currentTime - prevTime) > 1000000000) {
                            hasBeenClicked = false;
                        } else {
                            flipped = !flipped;

                            photoComponent.flipped = !photoComponent.flipped;
                            photoComponent.repaint();


                            hasBeenClicked = false;
                        }
                    } else {
                        prevTime = System.nanoTime();
                        hasBeenClicked = true;
                    }


                    //System.out.println(currentTime - prevTime);

            }

            @Override
            public void mousePressed(MouseEvent e) {

                if(photoComponent.selectionMode) {
                    photoComponent.startingPoint = e.getPoint();
                } else {

                    if (photoComponent.flipped && canDraw) {
                        photoComponent.points.add(e.getPoint());


                    } else if (photoComponent.flipped && !canDraw) {
                        photoComponent.pStart = e.getPoint();
                    }

                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    //System.out.println(regex);
                    //photoComponent.gesturePoints.clear();
                    //repaint();

                    boolean patternRecognized = false;

                    //if(lightTable.mode == 1) {
                        //regex = lightTable.thumbnails.get(lightTable.imageIndex).regex;
                    //}


                    Matcher m = delImg.matcher(regex);

                    if(m.find()) {

                        if(photoComponent.selectionMode)
                        {
                            for(int i = 0; i < photoComponent.points.size(); i ++) {
                                if(photoComponent.points.get(i).x +1 < photoComponent.maxX &&
                                        photoComponent.points.get(i).x - 1 > photoComponent.minX &&
                                        photoComponent.points.get(i).y + 1 < photoComponent.maxY &&
                                        photoComponent.points.get(i).y - 1 > photoComponent.minY) {

                                    photoComponent.points.set(i, new Point(0,0));

                                }
                            }

                            for(int i = 0; i < photoComponent.boxPoints.size(); i++) {
                                if(photoComponent.boxPoints.get(i)[1].x < photoComponent.maxX &&
                                        photoComponent.boxPoints.get(i)[0].x > photoComponent.minX &&
                                        photoComponent.boxPoints.get(i)[1].y < photoComponent.maxY &&
                                        photoComponent.boxPoints.get(i)[0].y > photoComponent.minY &&
                                        photoComponent.selectionMode) {

                                    Point pt1 = new Point(0,0);
                                    Point pt2 = new Point(1,1);
                                    Point[] temp = {pt1,pt2};
                                    photoComponent.boxPoints.set(i, temp);
                                }

                            }
                            regex = "";
                            //lightTable.thumbnails.get(lightTable.imageIndex).regex = "";
                            patternRecognized = true;

                            statusText.setText("Annotations Deleted");
                        } else {
                            deleteImage();
                            regex = "";
                            //lightTable.thumbnails.get(lightTable.imageIndex).regex = "";
                            patternRecognized = true;
                        }
                    }

                    m = circled.matcher(regex);

                    if(m.find()) {
                        statusText.setText("Annotations Selected");
                        regex = "";
                        photoComponent.selectionMode = true;
                        //lightTable.thumbnails.get(lightTable.imageIndex).regex = "";

                        for(int i = 0; i < photoComponent.gesturePoints.size() ; i++) {
                            Point temp = photoComponent.gesturePoints.get(i);
                            if(temp.x > photoComponent.maxX)
                                photoComponent.maxX = temp.x;

                            if(temp.y > photoComponent.maxY)
                                photoComponent.maxY = temp.y;

                            if(temp.x < photoComponent.minX)
                                photoComponent.minX = temp.x;

                            if(temp.y < photoComponent.minY)
                                photoComponent.minY = temp.y;

                        }
                        patternRecognized = true;
                    } else {
                        photoComponent.selectionMode = false;

                        photoComponent.maxX = 0;
                        photoComponent.maxY = 0;
                        photoComponent.minX = getSize().getWidth();
                        photoComponent.minY = getSize().getHeight();
                    }

                    photoComponent.gesturePoints.clear();
                    repaint();

                    m = delImg.matcher(regex);

                    if(m.find()) {

                        if(photoComponent.selectionMode)
                        deleteImage();
                        regex = "";
                        //lightTable.thumbnails.get(lightTable.imageIndex).regex = "";
                        patternRecognized = true;
                    }

                    m = nextImg.matcher(regex);

                    if(m.find())
                    {
                        nextImage();
                        regex = "";
                        //lightTable.thumbnails.get(lightTable.imageIndex).regex = "";
                        patternRecognized = true;

                    }

                    m = prevImg.matcher(regex);

                    if(m.find())
                    {
                        prevImage();
                        regex = "";
                        //lightTable.thumbnails.get(lightTable.imageIndex).regex = "";
                        patternRecognized = true;


                    }

                    m = vacImg.matcher(regex);

                    if(m.find()) {
                        statusText.setText("Tag: Vacation");
                        vacationButton.setSelected(!vacationButton.isSelected());
                        photoComponent.tags[0] = !photoComponent.tags[0];
                        patternRecognized = true;

                        regex = "";
                    }

                    m = workImg.matcher(regex);

                    if(m.find()) {
                        statusText.setText("Tag: Work");
                        workButton.setSelected(!workButton.isSelected());
                        photoComponent.tags[3] = !photoComponent.tags[3];
                        patternRecognized = true;

                        regex = "";
                    }

                    m = schoolImg.matcher(regex);

                    if(m.find()) {
                        statusText.setText("Tag: School");
                        schoolButton.setSelected(!schoolButton.isSelected());
                        photoComponent.tags[2] = !photoComponent.tags[2];
                        patternRecognized = true;

                        regex = "";
                    }

                    m = famImg.matcher(regex);

                    if(m.find()) {
                        statusText.setText("Tag: Family");
                        familyButton.setSelected(!familyButton.isSelected());
                        photoComponent.tags[1] = !photoComponent.tags[1];
                        regex = "";
                        patternRecognized = true;

                    }

                    if(!patternRecognized) {
                        statusText.setText("Unrecognized Pattern");
                        regex = "";

                    }



                } else {

                    if(photoComponent.selectionMode) {
                        int xDistance = e.getPoint().x - photoComponent.startingPoint.x;
                        int yDistance = e.getPoint().y - photoComponent.startingPoint.y;

                        for(int i = 0; i < photoComponent.points.size(); i ++) {
                            if(photoComponent.points.get(i).x +1 < photoComponent.maxX &&
                                    photoComponent.points.get(i).x - 1 > photoComponent.minX &&
                                    photoComponent.points.get(i).y + 1 < photoComponent.maxY &&
                                    photoComponent.points.get(i).y - 1 > photoComponent.minY) {

                                photoComponent.points.get(i).x = photoComponent.points.get(i).x + xDistance;
                                photoComponent.points.get(i).y = photoComponent.points.get(i).y + xDistance;


                            }
                        }

                        for(int i = 0; i < photoComponent.boxPoints.size(); i++) {
                            if(photoComponent.boxPoints.get(i)[1].x < photoComponent.maxX &&
                                    photoComponent.boxPoints.get(i)[0].x > photoComponent.minX &&
                                        photoComponent.boxPoints.get(i)[1].y < photoComponent.maxY &&
                                            photoComponent.boxPoints.get(i)[0].y > photoComponent.minY &&
                                                photoComponent.selectionMode) {

                                photoComponent.boxPoints.get(i)[0].x = photoComponent.boxPoints.get(i)[0].x + xDistance;
                                photoComponent.boxPoints.get(i)[0].y = photoComponent.boxPoints.get(i)[0].y + xDistance;
                                photoComponent.boxPoints.get(i)[1].x = photoComponent.boxPoints.get(i)[1].x + xDistance;
                                photoComponent.boxPoints.get(i)[1].y = photoComponent.boxPoints.get(i)[1].y + xDistance;


                            }

                        }

                        photoComponent.selectionMode = false;
                        repaint();
                    } else {


                        if (photoComponent.flipped && canDraw) {
                            photoComponent.points.add(e.getPoint());


                        } else if (photoComponent.flipped && !canDraw) {

                            if (e.getPoint().y < photoComponent.img.getHeight() && e.getPoint().x < photoComponent.img.getWidth()) {
                                photoComponent.pEnd = e.getPoint();

                            }

                            Point[] pts = {photoComponent.pStart, photoComponent.pEnd};
                            photoComponent.boxPoints.add(pts);
                            repaint();
                            photoComponent.notes++;
                            photoComponent.boxStrings.add("");

                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {


                if (SwingUtilities.isRightMouseButton(e)) {
                    photoComponent.gesturePoints.add(e.getPoint());

                    pointCounter++;

                    if(pointCounter > 10  && photoComponent.gesturePoints.size() > 10) {

                        //System.out.println(getAngle(photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 5), photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 1)));
                        double angle = getAngle(photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 9), photoComponent.gesturePoints.get(photoComponent.gesturePoints.size() - 1));

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

                } else if(!photoComponent.selectionMode) {


                    if (photoComponent.flipped && canDraw) {
                        if (e.getPoint().y < photoComponent.img.getHeight() && e.getPoint().x < photoComponent.img.getWidth()) {

                            photoComponent.points.add(e.getPoint());

                        }


                        repaint();
                    } else if (photoComponent.flipped && !canDraw) {

                        if (e.getPoint().y < photoComponent.img.getHeight() && e.getPoint().x < photoComponent.img.getWidth()) {
                            photoComponent.pEnd = e.getPoint();

                            repaint();
                        }
                    }

                }
            }
        };
        scrollPane.addMouseListener(mouseAdapter);
        scrollPane.addMouseMotionListener(mouseAdapter);
        photoComponent.setFocusable(true);
        photoComponent.requestFocus();
        //lightTable.images.get(lightTable.imageIndex).setFocusable(true);
        //lightTable.images.get(lightTable.imageIndex).requestFocus();


        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

                photoComponent.requestFocus();
                int index = photoComponent.boxStrings.size();


                photoComponent.boxStrings.set(photoComponent.notes - 1,photoComponent.boxStrings.get(photoComponent.notes - 1) + e.getKeyChar());
                repaint();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //System.out.println("asdfas");
            }

            @Override
            public void keyReleased(KeyEvent e) {
               // System.out.println("asdfas");
            }
        };
        photoComponent.addKeyListener(keyListener);
        //lightTable.images.get(lightTable.imageIndex).addKeyListener(keyListener);

        //lightTable.add(photoComponent);

        scrollPane.getViewport().add(photoComponent);

        if(lightTable.mode == 0) {
            lightTable.add(scrollPane, BorderLayout.CENTER);
        }
        lightTable.scrollPane = scrollPane;

        lightTable.setVisible(true);
        //Create Bottom Text Panel
        bottomPanel.add(statusText);


        //Add all of the panels to the frame
        add(leftPanel, BorderLayout.LINE_START);
        p.add(menuBar);


        add(p, BorderLayout.NORTH);
        add(lightTable, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        //add(photoComponent, BorderLayout.CENTER);

        setVisible(true);
        photoComponent.requestFocusInWindow();
    }
}
