#pragma once

#include <windows.h>
#include <string>
#include <list>
#include <map>

class InputSimulator
{
	INPUT mKeybdIp;
	INPUT mMouseIp;

	//std::list<WORD> mPressedKeys;
	std::map<std::string, WORD> mKeyMaps;

	void SendInputSim(std::string key);

public:
	InputSimulator(void);
	void PressKey(std::string key);
	void ReleaseKey(std::string key);

	void TapKey(std::string key);

	void Simulate(std::string KeyList);
};

