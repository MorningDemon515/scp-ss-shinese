;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ SCP - Security Stories
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This is a modification of the game "SCP - Containment Breach"
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ Version 0.0.1 and onwards are stand-alone versions of the game, which means that all the assets from the original game are
; ~ included within this mod, unless these assets have been removed as they are unused.
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This mod was developed by the "Northern Wolf Industries"
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ https://discord.gg/ARgRhhB8ub (DISCORD)
; ~ https://sites.google.com/view/phantomic-games-studios/main-page (WEBSITE)
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This mod released under the CC-BY-SA 3.0 license as it is a derivative work based on SCP - Containment Breach and the SCP Foundation.
;----------------------------------------------------------------------------------------------------------------------------------------------------
; ~ This is the main file of the mod, you need to compile this file in order to be able to play the game.
;----------------------------------------------------------------------------------------------------------------------------------------------------

CheckForDlls()

; ~ This file needs to be created inside the SourceCode folder and named "Key.bb" and has to contain a variable named "Key" with a value. Example: Function ENCRYPTION_KEY$() : Return "MyKey"

Include "SourceCode/Key.bb"



Type GlobalVariables
	Field OptionFile$
	Field OSBit%
	Field WeaponFile$
End Type

Type Options
	Field GraphicWidth%
	Field GraphicHeight%
	Field LauncherEnabled%
	Field ShowFPS%
	Field TotalGFXModes%
	Field DisplayMode%
	Field EnableRoomLights%
	Field TextureDetails%
	Field TextureFiltering%
	Field ConsoleOpening%
	Field SFXVolume#
	Field MusicVol#
	Field MasterVol#
	Field VoiceVol#
	Field EnableSFXRelease%
	Field EnableSFXRelease_Prev%
	Field ConsoleEnabled%
	Field RenderCubeMapMode%
	Field MouseSmooth#
	Field HoldToAim%
	Field HoldToCrouch%
	Field BMSBossMusic%
	Field MainMenuMusic%
	Field PlayStartupVideos%
	Field PlayerModelEnabled%
	Field ElevatorMusicEnabled%
	Field AutoSaveEnabled%
	Field LanguageVal%
	Field RenderScope%
	Field IntroEnabled%
	Field Menu3DEnabled%
End Type

Type GameConstants
	Field CurrZone%
	Field CurrGamemode%
End Type

; ~ Include the controls file

Include "SourceCode\Controls.bb"

; ~ Include the global type instance accessors

Include "SourceCode\Type_Instances.bb"

InitGlobalVariables()

; ~ Create the folder in the AppData if it doesn't exist

If (FileType(GetEnv$("AppData")+"\scp-ss\")<>2) Then
	CreateDir(GetEnv$("AppData")+"\scp-ss")
EndIf
; ~ Create the options.ini file in the AppData folder if it doesn't exist

If (FileType(GetEnv$("AppData")+"\scp-ss\options.ini")<>1) Then
	WriteFile(GetEnv$("AppData")+"\scp-ss\options.ini")
EndIf

InitOptions()

BlitzcordCreateCore("905859463645376522")

; ~ Include the Main.bb file which is the core of the game

Include "SourceCode\Game_Core.bb"

Function CheckForDlls()
	Local InitErrorStr$ = ""
	
	If FileSize("Blitzcord.dll")=0 Then InitErrorStr=InitErrorStr+ "Blitzcord.dll"+Chr(13)+Chr(10)
	If FileSize("BlitzHash.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzHash.dll"+Chr(13)+Chr(10)
	If FileSize("BlitzMovie.dll")=0 Then InitErrorStr=InitErrorStr+ "BlitzMovie.dll"+Chr(13)+Chr(10)
	
	If FileSize("d3dim700.dll")=0 Then InitErrorStr=InitErrorStr+ "d3dim700.dll"+Chr(13)+Chr(10)
	If FileSize("discord_game_sdk.dll")=0 Then InitErrorStr=InitErrorStr+ "discord_game_sdk.dll"+Chr(13)+Chr(10)
	If FileSize("fmod.dll")=0 Then InitErrorStr=InitErrorStr+ "fmod.dll"+Chr(13)+Chr(10)
	
	
	If Len(InitErrorStr)>0 Then
		RuntimeError "在游戏目录中找不到以下DLL:"+Chr(13)+Chr(10)+Chr(13)+Chr(10)+InitErrorStr
	EndIf
	
	
	
End Function

Function InitGlobalVariables()
	
	gv\OptionFile$ = GetEnv("AppData")+"\scp-ss\options.ini"
	If GetEnv("ProgramFiles(X86)")>0 Then
		gv\OSBit = 64
	Else
		gv\OSBit = 32
	EndIf
	
	gv\WeaponFile$ = "Data\Weapons.ini"
	
End Function

Function InitOptions()
	
	opt\EnableSFXRelease% = GetINIInt(gv\OptionFile, "audio", "sfx release", 1)
	opt\EnableSFXRelease_Prev% = opt\EnableSFXRelease%
	opt\ConsoleEnabled% = GetINIInt(gv\OptionFile, "console", "enabled", 0)
	opt\ConsoleOpening% = GetINIInt(gv\OptionFile, "console", "auto opening", 0)
	opt\GraphicWidth% = GetINIInt(gv\OptionFile, "options", "width", DesktopWidth())
	opt\GraphicHeight% = GetINIInt(gv\OptionFile, "options", "height", DesktopHeight())
	opt\ShowFPS = GetINIInt(gv\OptionFile, "options", "show FPS", 0)
	opt\DisplayMode% = GetINIInt(gv\OptionFile, "options", "display mode", 1)
	opt\RenderCubeMapMode% = GetINIInt(gv\OptionFile, "options", "cubemaps", 0)
	opt\EnableRoomLights% = GetINIInt(gv\OptionFile, "options", "room lights enabled", 1)
	opt\TextureDetails% = GetINIInt(gv\OptionFile, "options", "texture details", 2)
	opt\TextureFiltering% = GetINIInt(gv\OptionFile, "options", "texture filtering", 2)
	opt\LauncherEnabled% = GetINIInt(gv\OptionFile, "options", "launcher enabled", 1)
	opt\TotalGFXModes% = CountGfxModes3D()
	opt\SFXVolume# = GetINIFloat(gv\OptionFile, "audio", "sound volume", 1.0)
	opt\VoiceVol# = GetINIFloat(gv\OptionFile, "audio", "voice volume", 1.0)
	opt\MasterVol# = GetINIFloat(gv\OptionFile, "audio", "master volume", 1.0)
	opt\MainMenuMusic = GetINIFloat(gv\OptionFile, "audio", "mainmenu music", 0)
	opt\MusicVol# = GetINIFloat(gv\OptionFile, "audio", "music volume", 1.0)
	opt\MouseSmooth# = GetINIFloat(gv\OptionFile, "options", "mouse smoothing", 1.0)
	opt\HoldToAim% = GetINIInt(gv\OptionFile, "options", "hold to aim", 1)
	opt\HoldToCrouch% = GetINIInt(gv\OptionFile, "options", "hold to crouch", 1)
	opt\PlayStartupVideos = GetINIInt(gv\OptionFile, "options", "play startup videos", 1)
	opt\LanguageVal = GetINIInt(gv\OptionFile, "options", "language", 0)
	opt\RenderScope = GetINIInt(gv\OptionFile, "options", "render scope", 1)
	opt\Menu3DEnabled = GetINIInt(gv\OptionFile, "options", "enabled 3d menu", 1)
	
	opt\IntroEnabled = GetINIInt(gv\OptionFile, "options", "intro enabled", 1)
	opt\ElevatorMusicEnabled = GetINIInt(gv\OptionFile, "audio", "elevator music", 1)
	opt\PlayerModelEnabled = GetINIInt(gv\OptionFile, "options", "enable player model", 1)
	
	LoadResolutions()
	
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D