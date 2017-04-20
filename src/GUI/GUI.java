package GUI;

import Encryption.Encryption;
import PacketHandling.*;
import com.sun.xml.internal.ws.resources.HandlerMessages;
import jdk.internal.org.objectweb.asm.Handle;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
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
    private JTextPane textOutput;
    private JTextField textInput;
    private JButton sendButton;
    private JTextArea connectedUserSTextArea;
    private JScrollPane jscrollpanel;
    private JButton sendImageButton;
    private JComboBox privateUser;

    private JLabel lb = new JLabel();
    private String prefix = "";

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("GUIFrame");
        GUI gui = new GUI();
        frame.setContentPane(gui.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        gui.textInput.grabFocus();
        Encryption.startUp("SuperSecretPassWord1092837081");
        OutBuffer.checkOutStanding();
    }

    public GUI() throws IOException {
        jscrollpanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jscrollpanel.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

        lb.setText("<html>The commands in this chatbox are: " +
                "<br>cleartext - deletes all the messages in the chatbox " +
                "<br>setname {name} - changes the username " +
                "<br>fontcolor {red, black, white, green, blue, cyan, pink, orange, yellow} - changes the color of the font<br>");
        textOutput.insertComponent(lb);

        textInput.addActionListener(new sendActionListener());
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textInput.getText().contains("setname ")) {
                    String arr[] = textInput.getText().split(" ");
                    Handlemsg.nodenames.put(Network.nodenr, arr[1]);
                    textOutput.setText(textOutput.getText() + Handlemsg.nodenames.get(Network.nodenr) + ": name changed to " + arr[1] + "\n");
                } else if (textInput.getText().contains("cleartext")) {
                    System.out.println("ik ko hier");
                    //textOutput.setText("");
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
                    textOutput.setText(textOutput.getText() + Handlemsg.nodenames.get(Network.nodenr) + ": " + textInput.getText() + "\n");
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
        Handlemsg.nodenames.put(Network.nodenr, "Koos Naamloos");

        Thread threadComboBox = new Thread(){
            public void run() {
                while(true) {
                    privateUser.removeAllItems();
                    privateUser.addItem("All users");
                    for(Integer node : Handlemsg.nodenames.keySet()) {
                        privateUser.addItem("User " + node);
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        threadComboBox.start();

        class ItemChangeListener implements ItemListener {
            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    Object item = event.getItem();
                    String temp = event.getItem().toString();
                    String arr[] = temp.split(" ", 2);
                    if(!temp.equals("All users")) {
                        prefix = "@user" + arr[1] + " ";
                    } else {
                        prefix = "";
                    }
                    System.out.println("prefix: " + prefix);
                }
            }
        }

        privateUser.addItemListener(new ItemChangeListener());

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
                            connectedUserSTextArea.append("\nUser " + Network.nodenr + ": " + Handlemsg.nodenames.get(Network.nodenr));
                            bool = true;
                        }
                    }
                    if(!bool) {
                        connectedUserSTextArea.append("\nUser " + Network.nodenr + ": " + Handlemsg.nodenames.get(Network.nodenr));
                    }
                    try {
                        Thread.sleep(1000);
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
            String input = prefix + textInput.getText();
            if (input.contains("setname ")) {
                String arr[] = input.split(" ");
                if(arr.length != 1) {
                    Handlemsg.nodenames.put(Network.nodenr, arr[1]);
                    lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": name changed to " + arr[1] + "<br>");
                    textOutput.insertComponent(lb);
                } else {
                    lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": invalid name.<br>");
                    textOutput.insertComponent(lb);
                }
            } else if (input.contains("cleartext")) {
                lb.setText("");
                textOutput.insertComponent(lb);
            } else if (input.contains("fontcolor ")) {
                String arr[] = textInput.getText().split(" ");
                System.out.println("Array length: " + arr.length);
                if(arr.length != 1) {
                    arr[1] = arr[1].toLowerCase();
                    switch (arr[1].toLowerCase()) {
                        case "red":     lb.setText(lb.getText() + "<html><p color='red'>");
                                        textOutput.insertComponent(lb);
                                        break;
                        case "black":   lb.setText(lb.getText() + "<html><p color='black'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "green":   lb.setText(lb.getText() + "<html><p color='green'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "yellow":  lb.setText(lb.getText() + "<html><p color='yellow'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "pink":    lb.setText(lb.getText() + "<html><p color='pink'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "blue":    lb.setText(lb.getText() + "<html><p color='blue'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "cyan":    lb.setText(lb.getText() + "<html><p color='cyan'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "white":   lb.setText(lb.getText() + "<html><p color='white'>");
                            textOutput.insertComponent(lb);
                                        break;
                        case "orange":  lb.setText(lb.getText() + "<html><p color='orange'>");
                            textOutput.insertComponent(lb);
                                        break;
                        default:        lb.setText(lb.getText() + "<html><p color='black'>");
                            textOutput.insertComponent(lb);
                            lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": invalid color.<br>");
                            textOutput.insertComponent(lb);
                                        break;
                    }
                } else {
                    lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": invalid color.<br>");
                    textOutput.insertComponent(lb);
                }
            } else if (input.contains(":)")) {
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/happy.png' height=30 width=30></img><br>");
                textOutput.insertComponent(lb);
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                OutBuffer.addPacket(packet);
            }  else if (input.contains(":(")) {
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/sad.png' height=30 width=30></img><br>");
                textOutput.insertComponent(lb);
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                OutBuffer.addPacket(packet);
            }  else if (input.contains(":P")) {
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/tongue-out-1.png' height=30 width=30></img><br>");
                textOutput.insertComponent(lb);
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                OutBuffer.addPacket(packet);
            }  else if (input.contains(":O")) {
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/bored.png' height=30 width=30></img><br>");
                textOutput.insertComponent(lb);
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                OutBuffer.addPacket(packet);
            }  else if (input.contains(":S")) {
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/confused-1.png' height=30 width=30></img><br>");
                textOutput.insertComponent(lb);
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                OutBuffer.addPacket(packet);
            } else if (input.contains("@user")) {
                String arr[] = input.split("r");
                String number = arr[1].split("")[0];
                int receiveNode = Integer.parseInt(number);
                System.out.println("receive node: " + receiveNode);

                String[] data = input.split(" ", 2);
                System.out.println("Data: " + data[1]);
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + " > " + Handlemsg.nodenames.get(receiveNode) + ":  " + data[1] + "<br>");
                textOutput.insertComponent(lb);

                EZPacket packet = new EZPacket(Network.nodenr, receiveNode, 2, data[1].getBytes());
                OutBuffer.addPacket(packet);
            } else if (input.contains("sendpng")) {
                String arr[] = input.split(" ");
                if(arr.length != 1) {
                    OutBuffer.sendPacket(new File(arr[1]));
                    lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": send image: " + arr[1] + "<br>");
                    textOutput.insertComponent(lb);
                } else {
                    lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": invalid image.<br>");
                    textOutput.insertComponent(lb);
                }
            } else {
                lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + textInput.getText() + "<br>");
                textOutput.setText("");
                textOutput.insertComponent(lb);
                EZPacket packet = new EZPacket(Network.nodenr, 0, 2, textInput.getText().getBytes());
                OutBuffer.addPacket(packet);
            }
            textInput.setText("");
        }
    }

    public void message(String name, String msg) {
        if(msg.contains(":)")) {
            lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/happy.png' height=30 width=30></img><br>");
            textOutput.insertComponent(lb);
        } else if (msg.contains(":(")) {
            lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/sad.png' height=30 width=30></img><br>");
            textOutput.insertComponent(lb);
        } else if (msg.contains(":P")) {
            lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/tongue-out-1.png' height=30 width=30></img><br>");
            textOutput.insertComponent(lb);
        } else if (msg.contains(":O")) {
            lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/bored.png' height=30 width=30></img><br>");
            textOutput.insertComponent(lb);
        } else if (msg.contains(":S")) {
            lb.setText(lb.getText() + "<html>" + Handlemsg.nodenames.get(Network.nodenr) + ": " + "<img src='file:src/emoticons/confused-1.png' height=30 width=30></img><br>");
            textOutput.insertComponent(lb);
        } else {
            lb.setText(lb.getText() + "<html>" + name + ": " + msg + "<br>");
            textOutput.insertComponent(lb);
            textOutput.setCaretPosition(textOutput.getDocument().getLength());
        }
    }
}
