
Function FillRoom_Room2_Tesla(r.Rooms)
	Local w.WayPoints,w2.WayPoints
	
	r\Objects[0] = CreatePivot()
	PositionEntity(r\Objects[0], r\x, 0.0, r\z)
	EntityParent(r\Objects[0], r\obj)	
	
	r\Objects[1] = CreateSprite()
	EntityTexture (r\Objects[1], TeslaTexture)
	SpriteViewMode(r\Objects[1],2)
	EntityBlend (r\Objects[1], 3) 
	EntityFX(r\Objects[1], 1 + 8 + 16)
	
	PositionEntity(r\Objects[1], r\x, 1.3, r\z)
	
	HideEntity r\Objects[1]
	EntityParent(r\Objects[1], r\obj)
	
	w.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z + 292.0 * RoomScale, Null, r)
	w2.WayPoints = CreateWaypoint(r\x, r\y + 66.0 * RoomScale, r\z - 284.0 * RoomScale, Null, r)
	w\connected[0] = w2 : w\dist[0] = EntityDistance(w\obj, w2\obj) ;TODO waypoints Squared?
	w2\connected[0] = w : w2\dist[0] = w\dist[0]
	
	r\Objects[2] = CreateSprite()
	PositionEntity(r\Objects[2], r\x - 32 * RoomScale, 568 * RoomScale, r\z)
	ScaleSprite(r\Objects[2], 0.03, 0.03)
	EntityTexture(r\Objects[2], LightSpriteTex[1])
	EntityBlend (r\Objects[2], 3)
	EntityParent(r\Objects[2], r\obj)
	HideEntity r\Objects[2]
	
	r\Objects[3] = CreateButton(r\x-769*RoomScale,r\y+93*RoomScale,r\z-252*RoomScale,90,0,270)
	EntityParent(r\Objects[3],r\obj)
	
	r\Objects[4] = CreateSprite()
	PositionEntity(r\Objects[4], r\x - 32 * RoomScale, 568 * RoomScale, r\z)	
	ScaleSprite(r\Objects[4], 0.03, 0.03)
	EntityTexture(r\Objects[4], LightSpriteTex[3])
	EntityBlend (r\Objects[4], 3)
	EntityParent(r\Objects[4], r\obj)
	HideEntity r\Objects[4]
	
	r\RoomDoors[0] = CreateDoor(r\zone,r\x+399*RoomScale,r\y,r\z,0,r,False,DOOR_WINDOWED,False)
	FreeEntity_Strict r\RoomDoors[0]\buttons[0] : r\RoomDoors[0]\buttons[0] = 0
	FreeEntity_Strict r\RoomDoors[0]\buttons[1] : r\RoomDoors[0]\buttons[1] = 0
	
	r\RoomDoors[1] = CreateDoor(r\zone,r\x-574*RoomScale,r\y,r\z+247*RoomScale,270,r,False,DOOR_DEFAULT,KEY_CARD_3)
	PositionEntity(r\RoomDoors[1]\buttons[0],r\x-531*RoomScale,EntityY(r\RoomDoors[1]\buttons[0],True),r\z+382*RoomScale,True)
	RotateEntity r\RoomDoors[1]\buttons[0],0,180,0
	PositionEntity(r\RoomDoors[1]\buttons[1],r\x-618*RoomScale,EntityY(r\RoomDoors[1]\buttons[1],True),r\z+115*RoomScale,True)
	RotateEntity r\RoomDoors[1]\buttons[1],0,0,0
	
End Function

Function UpdateEvent_Room2_Tesla(e.Events)
	Local e2.Events,p.Particles,n.NPCs,d.Decals
	Local temp%
	Local i
		
	If e\EventState[3] = 0 Then
		
		UpdateButton(e\room\Objects[3])
		
		e\room\RoomDoors[0]\locked = True : e\room\RoomDoors[0]\open = False
		
		If d_I\ClosestButton = e\room\Objects[3] Then
			If KeyHitUse Then
				e\room\RoomDoors[0]\locked = False : e\room\RoomDoors[0]\open = True
				PlaySound2(LoadTempSound("SFX\Room\Tesla\Disable.ogg"),Camera,e\room\Objects[1])
				e\EventState[3] = fps\Factor[0]
			EndIf
		EndIf
	EndIf
	
	If e\EventState[3] > 0 Then
		e\EventState[3] = e\EventState[3] + fps\Factor[0]
	EndIf
	
	If e\EventState[3] >= 70*60 Then
		e\EventState[3] = 0
		PlaySound2(LoadTempSound("SFX\Room\Tesla\PowerUp.ogg"),Camera,e\room\Objects[1])
	EndIf
	
	If e\room\dist < 16 Then
		If e\EventState[3] = 0 Then
			
			HideEntity e\room\Objects[4]
			
			If (MilliSecs() Mod 1500) < 800 And e\EventState[0] = 0 Lor (MilliSecs() Mod 100) < 50 And e\EventState[0] = 1 Then
				ShowEntity e\room\Objects[2]
			Else
				HideEntity e\room\Objects[2]
			EndIf
		Else
			HideEntity e\room\Objects[2]
			ShowEntity e\room\Objects[4]
		EndIf
		
		If e\EventState[3] = 0 Then
			Select e\EventState[0]
				Case 0 ; Idle state
					HideEntity e\room\Objects[1]
					e\SoundCHN = LoopSound2(TeslaIdleSFX,e\SoundCHN,Camera,e\room\Objects[1],4.0,0.5)
					e\EventState[1] = 0
					If Abs(EntityX(Collider,True)-EntityX(e\room\Objects[0],True)) < 1.0 And (e\room\angle Mod 180 = 90) Lor Abs(EntityZ(Collider,True)-EntityZ(e\room\Objects[0],True)) < 1.0 And (e\room\angle Mod 180 = 0) Then
						If EntityDistanceSquared(Collider,e\room\Objects[0]) < PowTwo(300.0*RoomScale) And KillTimer => 0 Then
							e\EventState[0] = 1
							StopChannel(e\SoundCHN)
							e\SoundCHN = PlaySound2(TeslaActivateSFX, Camera, e\room\Objects[1],4.0,0.5)
						EndIf
					EndIf
					For n.NPCs = Each NPCs
						If n\Collider <> 0 And n\IsDead = False And n\HP > 0 And n\NPCtype <> NPCtype966 Then
							If Abs(EntityX(n\Collider,True)-EntityX(e\room\Objects[0],True)) < 0.8 And (e\room\angle Mod 180 = 90) Lor Abs(EntityZ(n\Collider,True)-EntityZ(e\room\Objects[0],True)) < 0.8 And (e\room\angle Mod 180 = 0) Then
								If EntityDistanceSquared(n\Collider,e\room\Objects[0]) < PowTwo(300.0*RoomScale)
									e\EventState[0] = 1
									StopChannel(e\SoundCHN)
									e\SoundCHN = PlaySound2(TeslaActivateSFX, Camera, e\room\Objects[1],4.0,0.5)
								EndIf
							EndIf	
						EndIf	
					Next
				Case 1 ; Charge state
					e\EventState[1] = e\EventState[1] + fps\Factor[0]
					If e\EventState[1] >= 35 Then
						e\EventState[0] = 2
						PlaySound2(LoadTempSound("SFX\Room\Tesla\Shock.ogg"),Camera,e\room\Objects[1])
					EndIf	
				Case 2 ; Zap state
					If Abs(EntityX(Collider,True)-EntityX(e\room\Objects[0],True)) < 0.75 And (e\room\angle Mod 180 = 90) Lor Abs(EntityZ(Collider,True)-EntityZ(e\room\Objects[0],True)) < 0.75 And (e\room\angle Mod 180 = 0) Then
						If EntityDistanceSquared(Collider,e\room\Objects[0]) < PowTwo(300.0*RoomScale) And KillTimer => 0 Then
							LightFlash = 0.4
							CameraShake = 1.0
							Kill()
							m_msg\DeathTxt = GetLocalStringR("Singleplayer", "tesla_death", Designation)
						EndIf	
					EndIf
					For n.NPCs = Each NPCs
						If n\Collider <> 0 And n\IsDead = False And n\HP > 0 Then
							If Abs(EntityX(n\Collider,True)-EntityX(e\room\Objects[0],True)) < 0.5 And (e\room\angle Mod 180 = 90) Lor Abs(EntityZ(n\Collider,True)-EntityZ(e\room\Objects[0],True)) < 0.5 And (e\room\angle Mod 180 = 0) Then
								If EntityDistanceSquared(n\Collider,e\room\Objects[0]) < PowTwo(300.0*RoomScale) Then
									Select n\NPCtype
										Case NPCtype106
											n\State = 70 * 60 * Rand(10,13)
											TranslateEntity(n\Collider,0,-10.0,0,True)
											GiveAchievement(AchvTesla)
											d.Decals = CreateDecal(DECAL_DECAY, EntityX(n\Collider, True), 0.01, EntityZ(n\Collider, True), 90, Rand(360), 0)
											d\Size = 0.05 : d\SizeChange = 0.005 : EntityAlpha(d\obj, 0.8)
										Case NPCtypeD2
											n\HP = 0
											n\IsDead = True
											n\State2 = Rand(0,3)
										Case NPCtypeCI
											n\HP = 0
											n\IsDead = True
											n\State2 = Rand(0,3)
										Case NPCtype049_2,NPCtype049_3,NPCtype049_4,NPCtype049_5,NPCType049_6,NPCtype008,NPCtype008_2,NPCtype008_3,NPCtype008_4
											n\HP = 0
										Case NPCtype049
											n\HP = -1000
											n\State = SCP049_STUNNED
										Case NPCtype096
											n\HP = -1000
											n\State = SCP096_STUNNED
										Case NPCtype076
											;n\HP = n\HP - 5
											n\State = SCP076_STUNNED
									End Select
									If e\room\dist < 8 And EntityInView(e\room\Objects[0],Camera) Then
										LightFlash = 0.4
									EndIf	
								EndIf	
							EndIf
						EndIf	
					Next	
					If Rand(5)<5 Then 
						PositionTexture TeslaTexture,0.0,Rnd(0,1.0)
						ShowEntity e\room\Objects[1]
					EndIf
					e\EventState[1] = e\EventState[1] - fps\Factor[0] * 1.5
					If e\EventState[1] <= 0 Then
						e\EventState[0] = 3
						e\EventState[1] = -70
						StopChannel(e\SoundCHN)
						e\SoundCHN = PlaySound2(TeslaPowerUpSFX, Camera, e\room\Objects[1],4.0,0.5)
					EndIf	
				Case 3 ; Recharge state
					e\EventState[1] = e\EventState[1] + fps\Factor[0]
					HideEntity e\room\Objects[1]
					If e\EventState[1] >= 0 Then
						e\EventState[0] = 0
					EndIf	
			End Select
		EndIf
	EndIf
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D