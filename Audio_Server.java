import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import javax.sound.midi.*;



public class Audio_Server {

    public static void main(String[] args) throws Exception 
	{
        System.out.println("The capitalization server is running.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try 
		{
            while (true) 
			{
                new Audio_Srv(listener.accept(), clientNumber++).start();
            }
        } finally 
		{
            listener.close();
        }
    }

    private static class Audio_Srv extends Thread 
	{
        private Socket socket;
        private int clientNumber;
		
        public Audio_Srv(Socket socket, int clientNumber) 
		{
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }
		
		 public OutputStream FindAudio(String url) throws FileNotFoundException, IOException
		{
			URLConnection conn = new URL(url).openConnection();
            InputStream is = conn.getInputStream();

            OutputStream outstream = new FileOutputStream(new File("C:/Users/Desktop/"+Integer.toString(clientNumber)+".mp3"));
            byte[] buffer = new byte[4096*100];
            int len;
            long t = System.currentTimeMillis();
            while ((len = is.read(buffer)) > 0 && System.currentTimeMillis() - t <= 180000) 
            {
                outstream.write(buffer, 0, len);
            }
            outstream.close();
			
            return outstream;
		}
		
		public byte[] FindAudio_Version2(String audioname)
		{
		URL url = new URL(audioname);
		AudioInputStream ain = null;  
        byte[  ] buffer;
		int framesize;
		int numbytes = 0;
        try 
		{

            ain=AudioSystem.getAudioInputStream(url);

            AudioFormat format = ain.getFormat( );
            DataLine.Info info=new DataLine.Info(SourceDataLine.class,format);

            if (!AudioSystem.isLineSupported(info)) 
			{

                AudioFormat pcm =
                    new AudioFormat(format.getSampleRate( ), 16,
                                    format.getChannels( ), true, false);


                ain = AudioSystem.getAudioInputStream(pcm, ain);

                format = ain.getFormat( ); 
                info = new DataLine.Info(SourceDataLine.class, format);
            }

            framesize = format.getFrameSize( );
            buffer = new byte[4 * 1024 * framesize];
           
            for(;;) 
			{  

                int bytesread=ain.read(buffer,numbytes,buffer.length-numbytes);

                if (bytesread == -1) break;
                numbytes += bytesread;
                       
			}
        finally 
		{ 

        if (ain != null) ain.close( );
        }
		return buffer; 
    }

        public void run() 
		{
            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                while (true) 
				{

                    String input = in.readLine();

                    if (input == null || input.equals(".")) {
                        break;
                    }
                    out.write(FindAudio_Version2(input));
					
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        private void log(String message) {
            System.out.println(message);
        }
    }
}