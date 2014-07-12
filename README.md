MCsim
=====

This project uses Java's reflection to dynamically load minecraft.jar and use
its inner functions to simulate the game from within another interface. The
main aim of this project is to speed up the development of redstone circuits
and technical builds, as well as providing methods to help share schematics.

There are 2 main parts to this project; the simulator itself and the editor.
The plan is to decouple the simulator when it's ready and provide a library
that any Java project can use. Though simulator is aimed at progressing time
in a schematic and not so about world generating. The editor is the whole
graphical interface that will have tools to quickly build your schematic.

Other redstone simulators
-------------------------
There have been a couple of them that I know of, but all their approaches were
by manually recreating the behaviors. This made them not 100% accurate, nor
feature complete. The difference is that by using minecraft itself, all
behaviors are simualated (block and entity bahaviors, not bothering with
weather and such), so in essence the GUI becomes the bottleneck.

Baezon's redstone simulator:
http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1260528-baezons-redstone-simulator-v2-2

Mordritch's JavaScript Redstone Simulator
http://mordritch.com/mc_rss/

Rek's Circuit Simulator
http://www.minecraftforum.net/forums/minecraft-discussion/redstone-discussion-and/342852-circuit-simulator-v0-81-rek

If we inspect these examples of fine programming, we can notice 2 trends:
	- They're all called redstone simulators.
	- They view 2 layers of blocks at once.
	
I'm not following either of these treds. Because this simulator will simulate
everything, not just redstone. It would take a lot more work to provide
graphics for cases where the block is above and such, so I just chose not to.

The simulator
-------------
It uses some files from MCP that deobfuscate minecraft's classes, methods and
fields. After everything is linked, some of minecraft's methods will be ran to
prepare it for later. When a schematic is loaded, it is first converted to the
data structures that minecraft uses; the world is divided into chunks and
entities are read in as-is. At that point, the only thing the simulator does
is to tick and use the reverse process to give back a schematic, 1 tick later.

The editor
----------
The editor also loads in a schematic to provide a positive editing and viewing
experience. It's got 3 possible perspectives and aims to be as modifyable as
possible. The block's images are loaded in dynamically by separate images that
are linked by tilemaps.xml. More options like the blocks shown in the block-
picker, how blocks can rotate and right click actions (levers, repeaters, ...)
are configured by blocks.xml. I found that this is both better time-wise, as
well as more resiliant against updates. It is be possible to add new
blocks and the simulator should still work. If minecraft.jar is modded and
graphics are provided and configured, those should work the same way.

What is done and what is not done
---------------------------------
So I'm making this open source and didn't clean up at all, dear me! Just so
that there is no confusion about buttons not working and such. I was in the
middle of a couple things before I got distracted/busy. Moreso I was working
on the part between the simulator ticking and the GUI displaying it. So sadly
in its current state you won't see much change. But when it's done, there
will be awesome time-controls.

New schematic:		Unimplemented
Block placement:	Should mostly work, but no logic is updated (no wires
					going on).
Time controls:		Time can be simulated forward for blocks and some
					tileEntities (no items yet due to current
					incompatibilities). The actual time controls aren't hooked
					up yet though.
Export:				Works
Publish:			Last time I did anything, they were uploading to imgur,
					but nothing is done with the links yet.
					
I didn't start on entities yet, as I wasn't properly done with TileEntities.
Creating entities will crash the sim, so don't do it yet (inproper torch
placement, water washing stuff away).

Installation
------------
Requirements:
	- MCP version 9.08
	- Minecraft version 1.7.10

Steps:	
	1. Clone the source code from here
	2. Copy your MCP/conf folder to the 'mcp' directory. Tile files used are:
		- fields.csv
		- methods.csv
		- joined.srg
	3. Copy the contents of your .minecraft folder into 'minecraft'. Using:
		- libraries
		- versions
		
That's it! You should now be able to compile and run.

Links
-----
Where I've been updating my progress so far:
http://forum.openredstone.org/showthread.php?tid=1606

The best redstone community:
http://openredstone.org/

Lots of nice icons:
http://p.yusukekamiyamane.com/

His Circular Byte Buffer proved very useful:
http://ostermiller.org/utils/src/CircularByteBuffer.java.html

Their Tag class to parse and write schematics:
http://minecraft.gamepedia.com/Development_resources/Example_NBT_Class