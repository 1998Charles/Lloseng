// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import ocsf.server.*;
import common.*;

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

  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the server.
   */
  ChatIF serverUI; 
  
  /**
   * The boolean type variable.  It checks whether the #login <loginid> 
   * is firstly received as the first command.
   */
  private boolean firstChecker = false; 

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) throws IOException
  {
    super(port);
    this.serverUI = serverUI;
    listen();
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
  (Object msg, ConnectionToClient client)
  {
    if (firstChecker = true){  
      try
      {
        String string = new String("");
        string = string + msg;
        char[] msgArray = string.toCharArray();

        if (msgArray[0] == '#') {
          String [] stringArray = string.split("\\s+");
          if (stringArray[0].equals("#login") && stringArray.length == 2) {
                
                if (client.getInfo("ID") == null){
                  System.out.println(msg + " has logged on.");
                  client.setInfo("ID", stringArray[1]);
                  firstChecker = true;

                }
                else 
                {
                  try
                  {
                  client.sendToClient("SERVER MSG> Error: Could not reset ID information.");
                  }
                  catch(IOException e)
                  {}
                  // client.close();              
                }  
          }

          else
          {
            System.out.println("Message received: " + msg + " from " + client + " " + client.getInfo("ID").toString());
            this.sendToAllClients(client.getInfo("ID").toString() + ": " + msg);
          }
        }


        else
        {
          System.out.println("Message received: " + msg + " from " + client + " " + client.getInfo("ID").toString());
          this.sendToAllClients(client.getInfo("ID").toString() + ": " + msg);
        }    
      }

      catch(ArrayIndexOutOfBoundsException e)
      {
        System.out.println("Message received: " + msg + " from " + client + " " + client.getInfo("ID").toString());
        this.sendToAllClients(client.getInfo("ID").toString() + ": " + msg);
      }
    }
    else
    {
      try
      {
        client.sendToClient("SERVER MSG> Error: #login <loginid> command MUST be the first command received.");
        client.close();
      }
      catch(IOException e)
      {
        try
        {
          client.close();
        }
        catch(Exception ex)
        {}
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
  }
  
  // //Class methods ***************************************************
  
  // /**
  //  * This method is responsible for the creation of 
  //  * the server instance (there is no UI in this phase).
  //  *
  //  * @param args[0] The port number to listen on.  Defaults to 5555 
  //  *          if no argument is entered.
  //  */
  // public static void main(String[] args) 
  // {
  //   int port = 0; //Port to listen on

  //   try
  //   {
  //     port = Integer.parseInt(args[0]); //Get port from command line
  //   }
  //   catch(Throwable t)
  //   {
  //     port = DEFAULT_PORT; //Set port to 5555
  //   }

  //   EchoServer sv = new EchoServer(port);
    
  //   try 
  //   {
  //     sv.listen(); //Start listening for connections
  //   } 
  //   catch (Exception ex) 
  //   {
  //     System.out.println("ERROR - Could not listen for clients!");
  //   }
  // }

//Was the end of EchoServer class

// METHODS DESIGNED TO BE OVERRIDDEN BY CONCRETE SUBCLASSES ---------

  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client is attempting to connect to the server.");  //Indicates a new client is attempting to connect to the server..
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
    System.out.println(client.getInfo("ID").toString() + " is disconnected by server.");  //Indicates a client has disconnected.
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
    System.out.println(client.getInfo("ID").toString() + " has disconnected.");  //Indicates the client has disconnected.
  }

  /**
   * Hook method called when the server stops accepting
   * connections because an exception has been raised.
   * The default implementation does nothing.
   * This method may be overriden by subclasses.
   *
   * @param exception the exception raised.
   */
  protected void listeningException(Throwable exception) {
    System.out.println("The server stops accepting.");
  }

  /**
   * Hook method called when the server is clased.
   * The default implementation does nothing. This method may be
   * overriden by subclasses. When the server is closed while still
   * listening, serverStopped() will also be called.
   */
  protected void serverClosed() {
    System.out.println("The server has closed.");
  }

    /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message)
  {
    try
    {
      char[] messageArray = message.toCharArray();

      if (messageArray[0] == '#') {
        String [] stringArray = message.split("\\s+");
        if (stringArray[0].equals("#setport")) {
          if (isListening() == false && getNumberOfClients() == 0){
            setPort(Integer.parseInt(stringArray[1]));
          }
          else {
            System.out.println("Only allowed if the server is closed.");
          }
        }

        else if (stringArray[0] != "#setport" && stringArray[0] != "#setport"){ 
          switch(message){
            case "#quit" :
            quit();
            break;
            case "#stop" :
            stopListening();
            break;
            case "#close" :
            close();
            break;
            case "#start" :
            if (isListening() == false)
            {
              listen();
            }
            else
            {
              System.out.println("Only allowed if the server is stopped.");
            }
            break;
            case "#getport" :
            System.out.println("The current port number is " + getPort() + ".");
            break;
            default :
            sendToAllClients("SERVER MSG> " + message);
            serverUI.display(message);
          }
        }
        else {
          sendToAllClients("SERVER MSG> " + message);
          serverUI.display(message);
        }
      }
      else {
        sendToAllClients("SERVER MSG> " + message);
        serverUI.display(message);
      }
    }

    catch(IOException e)
    {
      serverUI.display
      ("Could not send message to clients.  Terminating server.");
      quit();
    }

    catch(ArrayIndexOutOfBoundsException e)
    {
      try
      {
        sendToAllClients("SERVER MSG> " + message);
        serverUI.display(message);
      }
      catch(Exception ex)
      {
        serverUI.display
        ("Could not send message to clients.  Terminating server.");
        quit();
      }
    }
  }
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      close();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}


