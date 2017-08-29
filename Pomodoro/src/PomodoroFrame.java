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
    private JButton settings = new JButton("Settings");
    private JButton confirm = new JButton("Confirm");


    private JLabel timeleft = new JLabel("Click to Go!", SwingConstants.CENTER);
    private JLabel status = new JLabel("WORKING", SwingConstants.CENTER);

    private JTextField inputWorkTime;
    private JTextField inputBreakTime;
    private JTextField inLongBreak;

    private Clip clip = null;
    private File soundpath = new File("/Users/Bill Chheu/Downloads/bell.wav");

    private Timer timer;

    private int rsec = 0;
    private int min;
    private int sec = 1500;
    private int looped = 0;
    private int btime = 300;
    private int wtime = 1500;
    private int lbtime = 1800;


    private boolean isBreak = false;

    private JPanel settingspanel;
    private JPanel mainpanel;


    public PomodoroFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Pomodoro");
        mainpanel = new JPanel(new BorderLayout());
        mainpanel.setBackground(Color.WHITE);
        mainpanel.setPreferredSize( new Dimension( 500, 500 ));
        mainpanel.setLayout(null);

        settingspanel = new JPanel();
        settingspanel.setBackground(Color.WHITE);
        settingspanel.setPreferredSize( new Dimension( 500, 500 ));
        settingspanel.setLayout(null);

        Font font = new Font("Verdana", Font.BOLD, 70);
        Font butfont =  new Font("Verdana", Font.BOLD, 20);
        Font statfont = new Font("Roboto", Font.BOLD, 50);

        inputWorkTime = new JTextField("Enter Work Time in Seconds");
        inputBreakTime = new JTextField("Enter Break Time in Seconds");
        inLongBreak = new JTextField("Enter Long Break Time in Seconds");

        timeleft.setForeground(Color.BLACK);
        status.setForeground(Color.RED);

        timeleft.setFont(font);
        status.setFont(statfont);
        start.setFont(butfont);

        start.setBounds(125,300,250,75);
        browse.setBounds(400,0,100,50);
        settings.setBounds(150,0,150,50);


        timeleft.setBounds(0,125,500,150);
        status.setBounds(0,10,500, 150);

        confirm.setBounds(150,350,150,50);
        inputBreakTime.setBounds(100,50,300,50);
        inputWorkTime.setBounds(100, 150, 300,50);
        inLongBreak.setBounds(100,250,300,50 );





        timer = new Timer(1000, new ActionListener() {               // The countdown Timer actionListener
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
                        System.out.println("Invalid audio, please try a different one. (Try a .wav file)");
                    }


                    if (!isBreak) {
                        isBreak = true;

                        status.setText("ON BREAK");
                        start.setText("Start Break");

                        status.setForeground(Color.GREEN);

                        if (looped < 4) {
                            sec = btime;
                        } else {
                            sec = lbtime;
                            looped = 0;
                        }

                    } else if (isBreak) {
                        sec = wtime;
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
                    start.setText("Pause");

                    try {
                        clip.stop();
                    }catch (Exception i){

                    }


                } else {
                   timer.stop();
                    start.setText("Start");

                    try {
                        clip.stop();
                    }catch (Exception io) {

                    }



                }
                }
        });
        ActionListener swap = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              /*   if ( x % 2 == 1) {
                        remove(settingspanel);
                        add(mainpanel);
                        x++;
                    } else {
                        add(settingspanel);
                        remove(mainpanel);
                        x++;
                    }
                    repaint();
                    revalidate();

                */
              if (e.getSource().equals(settings)) {
                  remove(mainpanel);
                  add(settingspanel);
              } else {
                  remove(settingspanel);
                  add(mainpanel);

                  try {
                      btime = (Integer.parseInt(inputBreakTime.getText()));
                      wtime = (Integer.parseInt(inputWorkTime.getText()));
                      lbtime = (Integer.parseInt(inLongBreak.getText()));

                      sec = wtime;
                  } catch (Exception io){
                    System.out.println("Enter actual numbers");
                  }
              }
              repaint();
              revalidate();

                }

            };

        settings.addActionListener(swap);
        confirm.addActionListener(swap);

        browse.addActionListener(new ActionListener() {             // adds the browsing of .wav files (add support for mp3s later)
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
        mainpanel.add(settings);

        settingspanel.add(confirm);
        settingspanel.add(inputBreakTime);
        settingspanel.add(inputWorkTime);
        settingspanel.add(inLongBreak);




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
