# Distributed-Shared-White-Board
COMP90015-2024S1-Assignment2

**How to Run**
- java CreateWhiteBoard <serverIPAddress> <serverPort> username
- java JoinWhiteBoard <serverIPAddress> <serverPort> username

**Phase 1:**
 - [x] Task A - Implement a client that allows a user to draw all the expected elements.
	 - [x] Shapes: at least your white board should support for line, circle, oval, and rectangle.
	 - [x] Free draw and erase must be implemented (it will be more convenient if there are several sizes of eraser)
	 - [x] Text inputting– allow user to type text anywhere inside the white board.
	 - [x] User should be able choose their favourite colour to draw the above features. At least **16** colours should be available.
	 - [x] Chat Window (text based): To allow users to communicate with each other by typing a text.
	 - [x] A “File” menu with new, open, save, saveAs and close should be provided (only the manager can control this)
 - [x] Task B - Implement a server so that client and server are able to communicate entities created in Task A

**Phase 2:**
 - [x] Allow the manager to create a whiteboard
 - [x] Allow other peers to connect and join in by getting approval from the manager
 - [x] Allow the manager to choose whether a peer can join in
	 - [x] join in means the peer's name will appear in the user list
 - [x] Allow the joined peer to choose quit
 - [x] Allow the manager to close the application, and all peers get notified
 - [x] Allow the manager to kick out a certain peer/user

**Phase 3:**
 - [x] Integrate the whiteboard with the user management skeleton (phases 1 and 2)
