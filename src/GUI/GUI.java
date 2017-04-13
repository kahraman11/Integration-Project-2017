package GUI;

import PacketHandling.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

        textInput.addActionListener(new sendActionListener());
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": " + textInput.getText() + "\n");
                textOutput.setCaretPosition(textOutput.getDocument().getLength());
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                Network.outBuffer.addPacket(packet);
                if(textInput.getText().contains("setname")) {
                    String arr[] = textInput.getText().split(" ");
                    Handlemsg.nodenames.put(Network.nodenr, arr[1]);
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
    }

    private class sendActionListener implements ActionListener {
        public sendActionListener() {

        }

        public void actionPerformed(ActionEvent e) {
            textOutput.append(Handlemsg.nodenames.get(Network.nodenr) + ": " + textInput.getText() + "\n");
            textOutput.setCaretPosition(textOutput.getDocument().getLength());
            EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
            Network.outBuffer.addPacket(packet);
            if(textInput.getText().contains("setname")) {
                String arr[] = textInput.getText().split(" ");
                Handlemsg.nodenames.put(Network.nodenr, arr[1]);
            }
            textInput.setText("");
        }
    }

    public void message(String name, String msg) {
        textOutput.append(name + ": " + msg + "\n");
        textOutput.setCaretPosition(textOutput.getDocument().getLength());
    }
}
