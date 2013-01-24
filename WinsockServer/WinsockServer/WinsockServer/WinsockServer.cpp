// WinsockServer.cpp : Definiert den Einstiegspunkt f�r die Konsolenanwendung.
//

#include "stdafx.h"
#include <WinSock.h>
#include <iostream>

int _tmain(int argc, _TCHAR* argv[])
{

	WSADATA WsaDat;
	SOCKET Socket; 
	SOCKADDR_IN SockAddr; 
	SOCKADDR Addr; 

	// initialize the api
    if (WSAStartup(MAKEWORD(2, 0), &WsaDat) != 0)
	{
		printf("WSA Initialization failed. \n");
	} 
	else
	{
		printf("WSA initialized. \n");
	}

	
	// initialize the socket
	Socket = socket(AF_INET, SOCK_STREAM, 0);
    if (Socket == INVALID_SOCKET) 
	{
        printf("Socket creation failed. \n");
    } 
	else
	{
		printf("Socket creation succeeded. \n");
	}
	// setup port, address and connection type
	SockAddr.sin_family = AF_INET; 
	SockAddr.sin_port = htons(7777); 
	
	SockAddr.sin_addr.S_un.S_addr = INADDR_ANY;
	//SockAddr.sin_addr.S_un.S_un_b.s_b1 = 127; 
	//SockAddr.sin_addr.S_un.S_un_b.s_b2 = 0; 
	//SockAddr.sin_addr.S_un.S_un_b.s_b3 = 0; 
	//SockAddr.sin_addr.S_un.S_un_b.s_b4 = 1; 

	if (bind(Socket, (SOCKADDR *)(&SockAddr), sizeof(SockAddr)) == SOCKET_ERROR)
	{
		printf("Attempt to bind failed. \n");
	}
	else
	{
		printf("Binding succeeded. \n");
	}

	// listen to one connection
	listen(Socket, 1);

	// wait vor a connection on a temporary socket
	int timeout = 1000;
	SOCKET TempSock = SOCKET_ERROR; 
	while (TempSock == SOCKET_ERROR) 
	{	
		TempSock = accept(Socket, NULL, NULL);
	} 
	
	printf("Connection found! \n");
	Socket = TempSock; 

	// connection found! accept it and find out the client's adress		
    int size = sizeof(Addr);
	accept(Socket, &Addr, &size); 	

	// receive a char and answer
	char buffer[1] = "";
	int bytesRecv = recv(Socket, buffer, 1, 0);
	std::cout << "Received a message from client: " << bytesRecv << std::endl;

	std::cout << "Sending a message to client: " << "B" << std::endl;
	send(Socket, "B", 1, 0);

	while(true);	
	return 0;
}

