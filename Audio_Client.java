import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import javax.sound.midi.*

public class Audio_Client {

    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Capitalize Client");
    //private JTextField dataField = new JTextField(40);
    private JTextArea messageArea = new JTextArea(8, 60);
	private JButton Play = new JButton("Play");
	
	public SourceDataLine line = null;   // For playing the audio.
	public AudioFormat audioFormat;
    /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Enter in the
     * listener sends the textfield contents to the server.
     */
    public Audio_Client() 
	{

        // Layout GUI
        messageArea.setEditable(false);
        frame.getContentPane().add(dataField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");

        Play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
			{
				audioFormat = new AudioFormat(44100, 8, 1, true, true);
                out.println(dataField.getText());
                   String response;
                try {
                    response = in.readLine();
                    if (response == null || response.equals("")) {
                          System.exit(0);
                      }
                } catch (IOException ex) 
				{
                       response = "Error: " + ex;
                }
				line.open(audioFormat);
				line.start();
				int framesize = 144; // For 1152 mp3 / 8 = 144
				int numbytes = buffer.size();
				int bytestowrite = (numbytes/framesize)*framesize;
                line.write(buffer, 0, bytestowrite);

            line.drain( );
			 finally { 

            if (line != null) line.close( );
			 }			
			
            }
        });
    }
	
	private void temp()
	{

                if (!started) 
				{
                    line.start( );
                    started = true;
                }

                int bytestowrite = (numbytes/framesize)*framesize;

                line.write(buffer, 0, bytestowrite);

                int remaining = numbytes - bytestowrite;
                if (remaining &gt; 0)
                    System.arraycopy(buffer,bytestowrite,buffer,0,remaining);
                numbytes = remaining;

            line.drain( );
        
        finally { 

            if (line != null) line.close( );
            if (ain != null) ain.close( );
        }
		}
		
    public void connectToServer() throws IOException {

        String serverAddress = JOptionPane.showInputDialog(
            frame,

            JOptionPane.QUESTION_MESSAGE);

        Socket socket = new Socket(serverAddress, 9898);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        for (int i = 0; i < 3; i++) {
            messageArea.append(in.readLine() + "\n");
        }
    }

    public static final int END_OF_TRACK = 47;

    public static void streamMidiSequence(URL url)
        throws IOException, InvalidMidiDataException, MidiUnavailableException
    {
        Sequencer sequencer=null;   
        Synthesizer synthesizer=null; 

        try {

            sequencer = MidiSystem.getSequencer( );
            sequencer.open( );  
            synthesizer = MidiSystem.getSynthesizer( );
            synthesizer.open( );
            sequencer.getTransmitter( ).setReceiver(synthesizer.getReceiver( ));

            sequencer.setSequence(url.openStream( ));  
 
            final Object lock = new Object( );

            sequencer.addMetaEventListener(new MetaEventListener( ) {
                    public void meta(MetaMessage e) {
                        if (e.getType( ) == END_OF_TRACK) {
                            synchronized(lock) { 
                                lock.notify( );
                            }
                        }
                    }
                });
            
            sequencer.start( );

            synchronized(lock) {
                while(sequencer.isRunning( )) {
                    try { lock.wait( ); } catch(InterruptedException e) {  }
                }
            }
        }
        finally {

            if (sequencer != null) sequencer.close( );
            if (synthesizer != null) synthesizer.close( );
        }
    }

   
    public static void main(String[] args) throws Exception 
	{
        Audio_Client client = new Audio_Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.pack();
        client.frame.setVisible(true);
        client.connectToServer();
    }
}