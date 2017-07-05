import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TimerTask;

/**
 * Created by Bill Chheu on 6/22/2017.
 */
public class PomodoroFrame extends JFrame {

    private JButton start = new JButton("Start");
    private JButton browse = new JButton("Browse");
    private JLabel timeleft = new JLabel("Click to Go!", SwingConstants.CENTER);
    private JLabel status = new JLabel("", SwingConstants.CENTER);

    private Clip clip = null;
    private File soundpath = new File("/Users/Bill Chheu/Downloads/bell.wav");

    private Timer timer;

    private int rsec = 0;
    private int min = 0;
    private int sec = 1;
    private int looped = 0;

    private boolean isBreak = false;



    public PomodoroFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Pomodoro");
        JPanel mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.WHITE);
        mainpanel.setPreferredSize( new Dimension( 500, 500 ));
        mainpanel.setLayout(null);

        Font font = new Font("Verdana", Font.BOLD, 70);
        Font butfont =  new Font("Verdana", Font.BOLD, 20);
        Font statfont = new Font("Roboto", Font.BOLD, 50);

        timeleft.setForeground(Color.BLACK);

        timeleft.setFont(font);
        status.setFont(statfont);
        start.setFont(butfont);

        start.setBounds(125,300,250,75);
        browse.setBounds(400,0,100,50);
        timeleft.setBounds(0,125,500,150);
        status.setBounds(0,10,500, 150);


        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sec--;
                min = sec / 60;
                rsec = sec % 60;

                if (rsec >= 10) {
                    timeleft.setText(Integer.toString(min) + ":" + Integer.toString(rsec));
                } else {
                    timeleft.setText(Integer.toString(min) + ":0" + Integer.toString(rsec));
                }

                if (sec <= 0) {
                    timer.stop();
                    looped++;


                    try {
                        makeSound();
                    } catch (Exception p) {
                        System.out.println("Invalid audio, please try a different one.");
                    }


                    if (!isBreak) {
                        isBreak = true;

                        status.setText("ON BREAK");
                        start.setText("Start Break");

                        status.setForeground(Color.GREEN);

                        if (looped < 4) {
                            sec = 2;
                        } else {
                            sec = 5;
                            looped = 0;
                        }

                    } else if (isBreak) {
                        sec = 4;
                        isBreak = false;

                        start.setText("Start");
                        status.setText("WORKING");
                        status.setForeground(Color.RED);

                    }
                }
            }
        });

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) throws NullPointerException {
                if (!timer.isRunning()) {
                    timer.start();

                    clip.stop();

                    timeleft.setText("BUG IT UP!");
                    start.setText("Pause");
                } else {
                   timer.stop();
                   clip.stop();

                   start.setText("Start");
                }
                }
        });

        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser();
                int returnVal = fileChooser.showOpenDialog(PomodoroFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    soundpath = fileChooser.getSelectedFile();
                }

            }
        });

        mainpanel.add(browse);
        mainpanel.add(start);
        mainpanel.add(timeleft);
        mainpanel.add(status);




        getContentPane().add( mainpanel );
        pack();
    }

    private void makeSound() throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundpath);
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();


    }




    public void display() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
            }
        });
    }
}
