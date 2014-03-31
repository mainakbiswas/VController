#include "stdafx.h"
#include "InputSimulator.h"
#include <sstream>
#include <iostream>

InputSimulator::InputSimulator(void)
{
	mKeybdIp.type = INPUT_KEYBOARD;
    mKeybdIp.ki.wScan = 0;
    mKeybdIp.ki.time = 0;
    mKeybdIp.ki.dwExtraInfo = 0;

	mKeyMaps["Left"] = 0xe04b;
	mKeyMaps["Right"] = 0xe04d;
}

void InputSimulator::SendInputSim(std::string key)
{
	WORD scanCode = mKeyMaps[key];
	bool extended = false;
	if((scanCode | 0xff00) == 0xe000)
		extended = true;

	mKeybdIp.ki.wScan = mKeyMaps[key]; // 0x14 = T for example
	mKeybdIp.ki.dwFlags = (int) KEYEVENTF_SCANCODE;
	if(extended)
		mKeybdIp.ki.dwFlags |= KEYEVENTF_EXTENDEDKEY;

	SendInput(1, &mKeybdIp, sizeof(INPUT));
}

void InputSimulator::PressKey(std::string key)
{
    SendInputSim(key);
}

void InputSimulator::ReleaseKey(std::string key)
{
	mKeybdIp.ki.dwFlags |= KEYEVENTF_EXTENDEDKEY;

    SendInputSim(key);
}

void InputSimulator::TapKey(std::string key)
{
	PressKey(key);
 
    ReleaseKey(key);
}

void InputSimulator::Simulate(std::string KeyList)
{
	std::istringstream str(KeyList);
	std::string line;
	while(std::getline(str, line))
	{
		if(line.find(" Press") != std::string::npos)
		{
			size_t index = line.find(" ");
			line.erase(index);
			//std::cout << "here press\n";
			PressKey(line);
		}
		else if(line.find("Release") != std::string::npos)
		{
			size_t index = line.find(" ");
			line.erase(index);
			//std::cout << "here release\n";
			ReleaseKey(line);
		}
	}
}

/*void InputSimulator::Simulate(std::string pressedKeyList)
{
	std::list<WORD>::iterator pressedKeysIter = mPressedKeys.begin();
	while(pressedKeysIter != mPressedKeys.end())
	{
		char key = *pressedKeysIter;
		size_t keyIndex = pressedKeyList.find(key);
		if(keyIndex == std::string::npos)
		{
			ReleaseKey(key);
			pressedKeysIter = mPressedKeys.erase(pressedKeysIter);
		}
		else
		{
			pressedKeyList.erase(keyIndex, 1);
			PressKey(key);
			pressedKeysIter++;
		}

		const char* buf = pressedKeyList.c_str();
		for(int i = 0; i < pressedKeyList.length(); i++)
		{
			mPressedKeys.push_back(buf[i]);
			PressKey(buf[i]);
		}
	}

	/*std::string::iterator keyListIter = pressedKeyList.begin();
	while(keyListIter != pressedKeyList.end())
	{
		WORD key = *keyListIter;
		std::set<WORD> pressedKeysIter = mPressedKeys.find(key);
	}
}*/
