package edu.upvictoria.fpoo;

import edu.upvictoria.fpoo.exceptions.NotADBException;
import edu.upvictoria.fpoo.utils.CommandInterpreter;

import javax.naming.NoPermissionException;
import java.io.FileNotFoundException;
import java.nio.file.NotDirectoryException;

public class App
{
    public static void main( String[] args )
    {
        CommandInterpreter commandInterpreter = null;
        try {
            commandInterpreter = new CommandInterpreter("/home/soriane/Desktop/new_db");
            while (true)
                commandInterpreter.readCommand();
        } catch (NotDirectoryException | FileNotFoundException | NoPermissionException | NotADBException e) {
            System.err.println(e.getMessage());
        }

    }
}
