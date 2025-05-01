1. Introduction
In this project, we implemented a whiteboard that can be shared among multiple users using Java Remote Method Invocation (RMI). A shared whiteboard is a system that allows multiple users to draw on canvas and enter text at the same time.
RMI is a distributed object technology that allows us to call methods for remote objects over the network. This allowed us to understand and implement the main concepts of distributed systems, such as remote method calling, network communication, synchronization, and more.

RMI is an API that Java provides for building distributed systems, allowing remote JVMs to call methods for objects running on local JVMs. This allows developers to call remote objects as if they were calling local objects, simplifying the implementation of distributed applications. RMI consists of the following key components:
Remote Interface: Defines the method provided by the remote object.
Remote Object: An object that implements a remote interface.
RMI Registry: A registry that allows you to register and retrieve remote objects.
The main advantages of RMI are that object-oriented designs can be applied to distributed systems by abstracting network communication, and that they reduce the burden on developers by generating automated stubs. Moreover, the object-oriented properties of Java can be used as they are, providing a consistent programming model.
However, since RMI communicates using specific ports, communication may be blocked depending on firewall settings, and method calls over the network may suffer from poor performance compared to local calls. Furthermore, RMI is Java dependent, resulting in a lack of interoperability with other languages. Despite these shortcomings, RMI is a powerful tool for easily building distributed systems in Java environments.

Through this project, I was able to learn in-depth how to build distributed systems using RMI. While implementing the shared whiteboard system, various computer science concepts such as user management, concurrency control, GUI design, etc. could be integrated and applied. Through this, I was able to gain practical experience in the design and implementation of distributed systems using RMI, and I was able to understand the strengths and limitations of RMI.
This project has been a great help in increasing our understanding of distributed system deployment using RMI, and we have been able to build our overall knowledge of distributed systems through the process of designing and implementing distributed applications that actually work.
2. Method

2.1 Components of the System
The system is a multi-user whiteboard application based on a client-server architecture, with its main components divided into managers and clients. System design focuses on allowing multiple users to access servers simultaneously and perform tasks such as drawing or entering text efficiently.

①	Manager (Create White Board)
The manager is responsible for creating and managing shared whiteboards.
The manager's key functions and responsibilities include:

Create Whiteboard: When you first start the system, the manager creates a new whiteboard.
User Management: Managers can approve or reject new users when they make access requests; managers also have the right to expel certain users from the system.
File Management: Manager can save the contents of a whiteboard as a file, or retrieve an existing file. Manager can also create a new whiteboard or close the whiteboard that you are currently working on.
Manage synchronization: Manager synchronizes the current state of the whiteboard to all clients so that all users can see the same screen.
Draw and enter text: The manager can do everything the client can, for example, draw on a whiteboard or enter text. These changes are synchronized to all users in real time.

②	Client (Join White Board)
Clients are responsible for users accessing and interacting with the whiteboard.
The key features and responsibilities of the client are:

Access Request: The user sends the access request to the manager through the client. Once approved by the manager, you can access the system as a user.
Draw and enter text: Users can draw or enter text on the whiteboard via the client interface. These changes are synchronized to all users in real time.
Chatting: Clients can communicate with other users by chatting. The entered chat will be sent to all users in real time.
Check User List: Clients can view the list of users who currently have access to the whiteboard, which is updated in real time.
Keep synchronization: Clients periodically receive the current status of the whiteboard from their manager and update their screens.
