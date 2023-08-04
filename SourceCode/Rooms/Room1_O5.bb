
Function FillRoom_Room1_O5(r.Rooms)
	Local d.Doors,it.Items,tex
	
	tex = LoadTexture_Strict("GFX\map\textures\Door_O5.jpg")
	r\RoomDoors[0] = CreateDoor(r\zone, r\x, r\y, r\z - 240.0 * RoomScale, 0, r, False, DOOR_LCZ, 0, AccessCode[1])
	r\RoomDoors[0]\AutoClose = False : r\RoomDoors[0]\locked = True : r\RoomDoors[0]\dir = 1
	EntityTexture r\RoomDoors[0]\obj, tex
	DeleteSingleTextureEntryFromCache(tex)
	
	r\Objects[0] = CreatePivot()
	PositionEntity r\Objects[0],r\x-5*RoomScale,r\y+200*RoomScale,r\z-420*RoomScale,True
	EntityParent r\Objects[0],r\obj
	
	r\Objects[1] = CreatePivot()
	PositionEntity r\Objects[1],r\x,r\y+132*RoomScale,r\z+1253*RoomScale,True
	EntityParent r\Objects[1],r\obj
	
	r\Objects[2] = LoadMesh_Strict("GFX\Map\rooms\Room1_O5\room1_o5_screen.b3d")
	PositionEntity r\Objects[2],r\x,r\y,r\z,True
	ScaleEntity r\Objects[2],RoomScale,RoomScale,RoomScale
	EntityParent r\Objects[2],r\obj
	HideEntity r\Objects[2]
	
	r\Objects[3] = LoadRMesh("GFX\map\rooms\room1_o5\room1_o5.rmesh",Null)
	ScaleEntity (r\Objects[3],RoomScale,RoomScale,RoomScale)
	EntityType GetChild(r\Objects[3],2), HIT_MAP
	EntityPickMode GetChild(r\Objects[3],2), 2
	PositionEntity(r\Objects[3],r\x,r\y,r\z,True)
	EntityParent(r\Objects[3], r\obj)
	
	it = CreateItem("Ballistic Helmet", "helmet", r\x+225.0*RoomScale, r\y+140.0*RoomScale, r\z-907.0*RoomScale)
	EntityParent(it\collider, r\obj)
	
	it = CreateItem("Laboratory Log #1", "paper", r\x+80.0*RoomScale, r\y+130.0*RoomScale, r\z+930.0*RoomScale)
	EntityParent(it\collider, r\obj)
	
	If ecst\ChanceToSpawnWolfNote = 1 Then
		it = CreateItem("Wolfnaya's Room Note", "paper", r\x+878.0*RoomScale, r\y+140.0*RoomScale, r\z-926.0*RoomScale)
		EntityParent(it\collider, r\obj)
	EndIf
	
	InitFluLight(0,FLU_STATE_FLICKER,r)
	InitFluLight(1,FLU_STATE_OFF,r)
	InitFluLight(2,FLU_STATE_ON,r)
End Function

Function UpdateEvent_Room1_O5(e.Events)
	Local n.NPCs
	
	If (Not clm\GlobalMode) Then
		
		If (Not ecst\WasInCaves) Then
			
			If TaskExists(TASK_FIND_ROOM1_O5) Then
				If e\room\NPC[0] = Null Then
					e\room\NPC[0] = CreateNPC(NPCTypeJanitor,EntityX(e\room\Objects[0],True),EntityY(e\room\Objects[0],True),EntityZ(e\room\Objects[0],True))
					e\room\NPC[0]\State = STATE_SCRIPT
					RotateEntity(e\room\NPC[0]\Collider,90,e\room\angle+0,0)
					If e\EventState[1] <> 2 Then
						SetNPCFrame(e\room\NPC[0],502)
					EndIf
				EndIf
			EndIf
			
			If PlayerRoom = e\room Then
				
				If e\EventState[1] = 0 Then
					If TaskExists(TASK_FIND_ROOM1_O5) Then
						EndTask(TASK_FIND_ROOM1_O5)
						e\EventState[1] = 1
					EndIf
				EndIf
				
				If e\room\NPC[0] <> Null Then
					PointEntity(e\room\NPC[0]\Collider,Collider)
					If e\EventState[1] = 2 Then
						If e\EventState[2] < 70*15 Then
							AnimateNPC(e\room\NPC[0],502,523,0.1,False)
							If e\room\NPC[0]\Frame >= 522 And e\EventState[2] > 0 Then
								e\room\NPC[0]\State = 0
							EndIf
						ElseIf e\EventState[2] >= 70*15 And e\EventState[2] < 70*15.01 Then
							e\room\NPC[0]\State = 0
							BeginTask(TASK_FIND_AREA_076)
							ecst\WasInO5 = True
						EndIf
					EndIf
					
					If e\EventState[1] = 1 Then
						PlayNewDialogue(11,%01)
						e\EventState[1] = 2
					EndIf
					
					If e\EventState[1] = 2 Then
						e\EventState[2] = e\EventState[2] + fps\Factor[0]
					EndIf
				EndIf
				
			EndIf
			
		Else
			
			e\room\RoomDoors[0]\locked = False
			
			ShowEntity e\room\Objects[2]
			
			If e\room\NPC[0] <> Null Then
				RemoveNPC(e\room\NPC[0])
			EndIf
			
			If PlayerRoom = e\room Then
				
				If ecst\WasInBCZ Then
					e\EventState[0] = 3
				EndIf
				
				If e\EventState[0] = 3 Then
					If TaskExists(TASK_COME_BACK_TO_O5_AGAIN) Then
						EndTask(TASK_COME_BACK_TO_O5_AGAIN)
					EndIf
					If (Not TaskExists(TASK_SEARCH_O5_AGAIN)) Then
						BeginTask(TASK_SEARCH_O5_AGAIN)
					EndIf
				EndIf
				
				If e\EventState[0] = 0 Then
					If TaskExists(TASK_COME_BACK_TO_O5) Then
						EndTask(TASK_COME_BACK_TO_O5)
					EndIf
					If (Not TaskExists(TASK_SEARCH_O5)) Then
						BeginTask(TASK_SEARCH_O5)
						e\EventState[0] = 1
					EndIf
				EndIf
				
				If e\EventState[0] = 1 Then
					If InteractWithObject(e\room\Objects[1],1) Then
						PlaySound_Strict(LoadTempSound("SFX\General\PC_typing.ogg"))
						e\EventState[0] = 2
					EndIf
				EndIf
				
				If ecst\WasInBCZ Then
					If e\EventState[0] = 3 Then
						If InteractWithObject(e\room\Objects[1],1) Then
							PlaySound_Strict(LoadTempSound("SFX\General\PC_typing.ogg"))
							e\EventState[3] = 1
						EndIf
					EndIf
				EndIf
					
				If e\EventState[0] = 2 Then
					e\EventState[1] = e\EventState[1] + fps\Factor[0]
				EndIf
				
				If e\EventState[3] = 1 Then
					e\EventState[2] = e\EventState[2] + fps\Factor[0]
				EndIf
				
				If e\EventState[1] < 70*5 Then
					EntityTexture e\room\Objects[2], O5_Screen[0]
				Else
					EntityTexture e\room\Objects[2], O5_Screen[2], Floor(((e\EventState[1]-70*5)/70) Mod 2.0)
				EndIf
				
				;If e\EventState[0] > 2 Then
				If ecst\WasInBCZ Then
					If e\EventState[3] = 1 Then
						If e\EventState[2] < 70*5 Then
							EntityTexture e\room\Objects[2], O5_Screen[0]
						Else
							EntityTexture e\room\Objects[2], O5_Screen[1], Floor(((e\EventState[2]-70*5)/70) Mod 2.0)
							e\SoundCHN = LoopSound2(AlarmSFX[7], e\SoundCHN, Camera, e\room\Objects[1], 15.0, 1)
						EndIf
					EndIf
				EndIf
				
				If e\EventState[1] >= 70*5 And e\EventState[1] < 70*6 Then
					If TaskExists(TASK_SEARCH_O5) Then
						FailTask(TASK_SEARCH_O5)
					EndIf
					
					If (Not TaskExists(TASK_GO_TO_BCZ)) Then
						BeginTask(TASK_GO_TO_BCZ)
					EndIf
				EndIf
				
				If e\EventState[2] >= 70*5 And e\EventState[2] < 70*6 Then
					If TaskExists(TASK_SEARCH_O5_AGAIN) Then
						FailTask(TASK_SEARCH_O5_AGAIN)
					EndIf
					If (Not TaskExists(TASK_FIND_PERSONNEL_OFFICES)) Then
						BeginTask(TASK_FIND_PERSONNEL_OFFICES)
					EndIf
				EndIf
				
				If e\EventState[1] > 70*6 Then
					ecst\WasInO5Again = True
				EndIf
				
			EndIf
			
		EndIf
		
	EndIf
	
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D