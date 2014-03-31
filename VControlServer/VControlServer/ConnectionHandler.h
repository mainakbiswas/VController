#pragma once
#include "Server.h"
#include "InputSimulator.h"
#include <thread>

class ConnectionHandler
{
	SOCKET mClientSocket;
	InputSimulator* mInputSimulator;
	std::thread* mConnectionThread;
	void Thread();
public:
	ConnectionHandler(SOCKET clientSocket, InputSimulator* inputSimulator);
	~ConnectionHandler(void);

	void Start();
};

