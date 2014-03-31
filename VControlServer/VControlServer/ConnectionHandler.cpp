#include "stdafx.h"
#include "ConnectionHandler.h"
#include <iostream>

ConnectionHandler::ConnectionHandler(SOCKET clientSocket, InputSimulator* inputSimulator)
	:mClientSocket(clientSocket), mInputSimulator(inputSimulator)
{
}

ConnectionHandler::~ConnectionHandler(void)
{
	delete mConnectionThread;
}

void ConnectionHandler::Start()
{
	mConnectionThread = new std::thread(&ConnectionHandler::Thread, this);
	mConnectionThread->detach();
}

void ConnectionHandler::Thread()
{
	int iResult;
	std::string recvStr;
	char recvBuf[512] = {0};
    int recvbuflen = 512;

	// Receive until the peer shuts down the connection
    do {
		for(int i = 0; i < 512; i++)
			recvBuf[i] = 0;
		iResult = recv(mClientSocket, recvBuf, recvbuflen, 0);
        if (iResult > 0) {
			recvStr = recvBuf;
			std::cout << "Bytes received:" << recvStr.c_str() << std::endl;
			mInputSimulator->Simulate(recvStr);
        }
        else if (iResult == 0)
            std::cout << "Connection closing...\n";
        else  {
            std::cout << "recv failed with error: " << WSAGetLastError() << std::endl;
            closesocket(mClientSocket);
            WSACleanup();
            return;
        }

    } while (iResult > 0);

    // shutdown the connection since we're done
    iResult = shutdown(mClientSocket, SD_SEND);
    if (iResult == SOCKET_ERROR) {
        std::cout << "shutdown failed with error: " << WSAGetLastError() << std::endl;
        closesocket(mClientSocket);
        WSACleanup();
        return;
    }

    // cleanup
    closesocket(mClientSocket);
	delete(this);
}