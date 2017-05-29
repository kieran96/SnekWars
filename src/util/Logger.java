package util;

import Server.Game;

import java.io.*;
import java.util.Date;

/**
 * Created by Mitchell on 26-May-17.
 */
public class Logger implements Runnable {

    private BoundedBuffer<LoggingPacket> loggingPacketBoundedBuffer;
    private File file;
    public Logger() {}

    public Logger(BoundedBuffer<LoggingPacket> bb) {
        loggingPacketBoundedBuffer = bb;
        Date date = new Date();
        String filename = ("Log " + date.toString().replace(":", "-") + ".txt");
        //Create Files:
        File dir = new File("C:/tmp/test");
        dir.mkdirs();
        file = new File(dir, filename);
        try {
            file.createNewFile();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //Setup:


        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8")
        )) {
            System.out.println("Logger started succesfully");
            while(Game.running) {
                if(file.isFile()) {
                    LoggingPacket packet = loggingPacketBoundedBuffer.get();
                    writer.write(new Date().toString() + " " + packet.getMessage());
                    writer.newLine();
                    writer.flush();
                }
            }
            writer.close();
            System.out.println("Logger successfully closed.");
        }  catch(Exception e) {
            e.printStackTrace();
        }
        System.out.println("Destroying Logger instance");
    }
}
