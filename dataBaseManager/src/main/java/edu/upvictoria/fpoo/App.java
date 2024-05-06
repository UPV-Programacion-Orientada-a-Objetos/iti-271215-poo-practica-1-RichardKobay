package edu.upvictoria.fpoo;

import edu.upvictoria.fpoo.utils.BrScanner;
import edu.upvictoria.fpoo.utils.CommandInterpreter;
import edu.upvictoria.fpoo.utils.FileUtils;

public class App
{
    public static void main( String[] args )
    {
        CommandInterpreter commandInterpreter = new CommandInterpreter();
        commandInterpreter.readCommand();
    }
}
