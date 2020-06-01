// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com

import java.io.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************

  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port)
  {
    super(port);
  }


  //Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
   public void handleMessageFromClient(Object msg, ConnectionToClient client) {


          //check if its a command for setting the user info

           String message = msg.toString();
           if (message.startsWith("#")) {

               String[] parameters = message.substring(1).split(" ");



               //check that there is a login followed by a username
               if (parameters[0].equals("login") && parameters.length > 1) {


                  //check if username has been set. if yes, send the set the inffo and send the information to the other clients on the server
                   if (client.getInfo("username") == null) {

                       client.setInfo("username", parameters[1]);

                       System.out.println("Message received #login " + parameters[1]);
                       this.sendToAllClients(parameters[1] + " has logged on.");


                   } else { //if the username is already set, it is an invalid command and must return an error
                       try {
                           client.sendToClient("Error. You already have a username.");
                       } catch (IOException e) {
                       }
                   }

               }
           } else {//check if the login was the first command. If not, we return an error.
               if (client.getInfo("username") == null) {
                   try {
                       client.sendToClient("Error. A username must be set before sending messages to the server.");
                       client.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               } else {//if the username is already set, we can send the message to the clients and we print the appropriate statement to the server.
                   System.out.println("Message received: " + msg + " from " + client.getInfo("username"));
                   this.sendToAllClients(client.getInfo("username") + " > " + message);
               }
           }
       }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
      sendToAllClients("WARNING - Server has stopped listening for connections.");
  }

  // OVERRIDDEN METHODS *************************************************

  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client)
  {

    String welcome = "A new client is attempting to connect to the server";
    System.out.println(welcome);


  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {


      this.sendToAllClients(client.getInfo("username") + " has disconnected.");

      System.out.println(client.getInfo("username") + " has disconnected.");


    }


    /**
     * Hook method called each time an exception is thrown in a
     * ConnectionToClient thread.
     * The method may be overridden by subclasses but should remains
     * synchronized.
     *
     * @param client the client that raised the exception.
     * @param Throwable the exception thrown.
     */
    synchronized protected void clientException(
      ConnectionToClient client, Throwable exception) {

        this.sendToAllClients(client.getInfo("username") + " has disconnected.");

        System.out.println(client.getInfo("username") + " has disconnected.");


      }


      public void handleMessageFromServerConsole(String message) {
    if (message.startsWith("#")) {
        String[] parameters = message.split(" ");


        String consoleCommand = parameters[0];

        //we go through all cases for potential commands if the console line starts with a #.
        switch (consoleCommand) {
            case "#quit":
                //closes the server and then exits it
                try {
                    this.close();
                } catch (IOException e) {
                    System.exit(1);
                }
                System.exit(0);
                break;

            case "#stop":
                this.stopListening();
                break;

            case "#close":
                try {
                    this.close();
                } catch (IOException e) {
                }
                break;

            case "#setport":
                if (!this.isListening() && this.getNumberOfClients() < 1) {
                    super.setPort(Integer.parseInt(parameters[1]));
                    System.out.println("Port set to " + Integer.parseInt(parameters[1]));
                } else {
                    System.out.println("Can't do that now. Server is connected.");
                }
                break;

            case "#start":
                if (!this.isListening()) {
                    try {
                        this.listen();
                    } catch (IOException e) {
                        //error listening for clients
                    }
                } else {
                    System.out.println("We are already started and listening for clients!.");
                }
                break;

            case "#getport":
                System.out.println("Current port is " + this.getPort());
                break;

            default:
                System.out.println("Invalid command: '" + consoleCommand+ "'");
                break;
        }
    } else {//if it isnt a command, we simply send it to the other clients as a server message.

        message = "SERVER MSG> " + message;
        this.sendToAllClients(message);
    }
}
  //Class methods ***************************************************

  /**
   * This method is responsible for the creation of
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555
   *          if no argument is entered.
   */

}
//End of EchoServer class
