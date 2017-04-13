package GUI;

import Encryption.Encryption;
import PacketHandling.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

/**
 * Created by mathay on 10-4-17.
 */
public class GUI {
    private JPanel panel1;
    private JTextArea textOutput;
    private JTextField textInput;
    private JButton sendButton;
    private JTextArea connectedUserSTextArea;
    private JScrollPane jscrollpanel;
    private JButton sendImageButton;

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("GUIFrame");
        GUI gui = new GUI();
        frame.setContentPane(gui.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        gui.textInput.grabFocus();
    }

    public GUI() throws IOException {
        jscrollpanel.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        textOutput.setLineWrap(true);
        textOutput.append("The commands in this chatbox are:\n" +
                "cleartext - deletes all the messages in the chatbox\n" +
                "setname {name} - changes the username\n" +
                "fontcolor {red, black, white, green, blue, cyan, pink, orange, yellow} - changes the color of the font\n");

        textInput.addActionListener(new sendActionListener());
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textInput.getText().contains("setname ")) {
                    String arr[] = textInput.getText().split(" ");
                    Handlemsg.nodenames.put(Network.nodenr, arr[1]);
                    textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": name changed to " + arr[1] + "\n");
                } else if (textInput.getText().contains("cleartext")) {
                    System.out.println("ik ko hier");
                    textOutput.setText("");
                } else if (textInput.getText().contains("fontcolor ")) {
                    String arr[] = textInput.getText().split(" ");
                    switch (arr[1].toLowerCase()) {
                        case "red":     textOutput.setForeground(Color.RED);
                            break;
                        case "black":   textOutput.setForeground(Color.black);
                            break;
                        case "green":   textOutput.setForeground(Color.green);
                            break;
                        case "yellow":  textOutput.setForeground(Color.yellow);
                            break;
                        case "pink":    textOutput.setForeground(Color.pink);
                            break;
                        case "blue":    textOutput.setForeground(Color.blue);
                            break;
                        case "cyan":    textOutput.setForeground(Color.cyan);
                            break;
                        case "white":   textOutput.setForeground(Color.white);
                            break;
                        case "orange":  textOutput.setForeground(Color.orange);
                            break;
                        default:        textOutput.setForeground(Color.black);
                            break;
                    }
                } else {
                    textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": " + textInput.getText() + "\n");
                    textOutput.setCaretPosition(textOutput.getDocument().getLength());
                    EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                    Network.outBuffer.addPacket(packet);
                }
                textInput.setText("");
            }
        });

        //network
        Network n = new Network(this);
        n.setConnection();
        new RecThread().start();

        connectedUserSTextArea.append("\nUser" + Network.nodenr + ": " + Handlemsg.nodenames.get(Network.nodenr));
        Thread thread = new Thread(){
            public void run(){
                while(true) {
                    connectedUserSTextArea.setText("Connected user(s):");
                    List nodenumbers = new ArrayList();
                    nodenumbers.addAll(Handlemsg.nodenames.keySet());
                    System.out.println("Nodenumbers: " + nodenumbers);
                    Boolean bool = false;
                    for(int i = 0; i<nodenumbers.size(); i++) {
                        if(!nodenumbers.get(i).equals(Network.nodenr)) {
                            connectedUserSTextArea.append("\nUser " + (nodenumbers.get(i)) + ": " + Handlemsg.nodenames.get(nodenumbers.get(i)));
                        } else {
                            System.out.println("ik kom hier");
                            connectedUserSTextArea.append("\nUser " + Network.nodenr + ": " + Handlemsg.nodenames.get(Network.nodenr));
                            bool = true;
                        }
                    }
                    if(!bool) {
                        connectedUserSTextArea.append("\nUser " + Network.nodenr + ": " + Handlemsg.nodenames.get(Network.nodenr));
                    }
                    try {
                        Thread.currentThread().sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();

        sendImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                }
            }
        });
    }

    private class sendActionListener implements ActionListener {
        public sendActionListener() {

        }

        public void actionPerformed(ActionEvent e) {
            if (textInput.getText().contains("setname ")) {
                String arr[] = textInput.getText().split(" ");
                if(arr.length != 1) {
                    Handlemsg.nodenames.put(Network.nodenr, arr[1]);
                    textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": name changed to " + arr[1] + "\n");
                } else {
                    textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": invalid name.\n");
                }
            } else if (textInput.getText().contains("cleartext")) {
                System.out.println("ik ko hier");
                textOutput.setText("");
            } else if (textInput.getText().contains("fontcolor ")) {
                String arr[] = textInput.getText().split(" ");
                System.out.println("Array length: " + arr.length);
                if(arr.length != 1) {
                    arr[1] = arr[1].toLowerCase();
                    switch (arr[1].toLowerCase()) {
                        case "red":     textOutput.setForeground(Color.RED);
                                        break;
                        case "black":   textOutput.setForeground(Color.black);
                                        break;
                        case "green":   textOutput.setForeground(Color.green);
                                        break;
                        case "yellow":  textOutput.setForeground(Color.yellow);
                                        break;
                        case "pink":    textOutput.setForeground(Color.pink);
                                        break;
                        case "blue":    textOutput.setForeground(Color.blue);
                                        break;
                        case "cyan":    textOutput.setForeground(Color.cyan);
                                        break;
                        case "white":   textOutput.setForeground(Color.white);
                                        break;
                        case "orange":  textOutput.setForeground(Color.orange);
                                        break;
                        default:        textOutput.setForeground(Color.black);
                                        textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": invalid color.\n");
                                        break;
                    }
                } else {
                    textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": invalid color.\n");
                }
            } else {
                textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": " + textInput.getText() + "\n");
                textOutput.setCaretPosition(textOutput.getDocument().getLength());
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                Network.outBuffer.addPacket(packet);
            }
            textInput.setText("");
        }
    }

    public void message(String name, String msg) {
        textOutput.append(name + ": " + msg + "\n");
        textOutput.setCaretPosition(textOutput.getDocument().getLength());
    }
}
