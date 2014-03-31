#pragma once
#undef UNICODE

#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <stdlib.h>

enum ServerType{
	WIFI,
	BLUETOOTH
};

class Server
{
	SOCKET mListenSocket;
	int InitTcpServer();
public:
	Server(ServerType type);
	~Server(void);

	SOCKET AcceptConnection();
};

