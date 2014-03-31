// VControlServer.cpp : Defines the entry point for the console application.
//
#include "stdafx.h"
#include "ConnectionHandler.h"
#include <iostream>
#include <list>

int _tmain(int argc, _TCHAR* argv[])
{
	InputSimulator mInputSim;
	Server mServer(WIFI);

	while(true)
	{
		SOCKET mSocket = mServer.AcceptConnection();
		ConnectionHandler* mConnect = new ConnectionHandler(mSocket, &mInputSim);
		mConnect->Start();
	}
	return 0;
}

