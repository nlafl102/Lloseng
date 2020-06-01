// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************

  /**
   * The interface type variable.  It allows the implementation of
   * the display method in the client.
   */
  ChatIF clientUI;
  String username;


  //Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */

  public ChatClient(String username, String host, int port, ChatIF clientUI)
    throws IOException
  {

    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.username = username;




    openConnection();
    sendToServer("#login <" + username + ">");

  }


  //Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg)
  {
    clientUI.display(msg.toString());



  }






  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();


    }


    catch(IOException e) {}
    System.exit(0);
  }

  // OVERRIDDEN METHODS *************************************************

  /**
   * Hook method called after the connection has been closed.
   */
  protected void connectionClosed()
  {
    System.out.println("Connection to server has been terminated.");


  }

  /**
   * Hook method called after an exception
   * is raised by the client listening thread.
   *
   * @param exception the exception raised.
   */
  protected void connectionException(Exception exception)
  {
    System.out.println("WARNING - The server has stopped listening. The server will now shut down.");
quit();


  }


  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
    public void handleMessageFromClientUI(String message) {

      //check if its a command
        if (message.startsWith("#")) {
            String[] parameters = message.split(" ");
            String consoleCommand = parameters[0];



            //switch through all command cases
            switch (consoleCommand) {
                case "#quit":

                    quit();
                    break;

                case "#logoff":

                    try {
                        closeConnection();
                    } catch (IOException e) {
                        System.out.println("Error. Could not close connection.");
                    }
                    break;

                case "#sethost":

                    if (this.isConnected()) {
                        System.out.println("Error. Already connected to server.");
                    } else {
                        System.out.println("sethost");
                        this.setHost(parameters[1]);
                    }
                    break;

                case "#setport":

                    if (this.isConnected()) {
                        System.out.println("Error. Already connected to server.");
                    } else {
                        this.setPort(Integer.parseInt(parameters[1]));

                    }
                    break;

                case "#login":

                    if (this.isConnected()) {
                        System.out.println("Error. Already connected to server.");
                    } else {
                        try {
                            this.openConnection();
                            sendToServer("#login <" + this.username + ">");
                        } catch (IOException e) {
                            System.out.println("Error opening connection to server.");
                        }
                    }
                    break;

                case "#gethost":

                    System.out.println("Current host is " + this.getHost());
                    break;

                case "#getport":
                    System.out.println("Current port is " + this.getPort());
                    break;



                default:

                    System.out.println("Invalid command: '" + consoleCommand+ "'");
                    System.out.println("If you typed in #login <" +username+ ">, simply type in #login to login.");
                    break;
            }
        } else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display
                        ("Could not send message to server.  Terminating client.");
                quit();
            }
        }
    }


}
