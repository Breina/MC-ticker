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

* Baezon's redstone simulator: http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1260528-baezons-redstone-simulator-v2-2
* Mordritch's JavaScript Redstone Simulator: http://mordritch.com/mc_rss/
* Rek's Circuit Simulator: http://www.minecraftforum.net/forums/minecraft-discussion/redstone-discussion-and/342852-circuit-simulator-v0-81-rek

If we inspect these examples of fine programming, we can notice 2 trends:
1. They're all called redstone simulators.
2. They view 2 layers of blocks at once.
	
I'm not following either of these trends. Because this simulator will simulate
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
- Normal block logic always works (wires, repeaters, torches, ...)
- TileEntities work (comparators, hoppers, droppers, ...)
- Item entities work, but not mobs or other special cases
- Light updates are completely ignored

Usage notes and bugs
--------------------
- When placing a block that hangs off another one, you'll have to rotate it
correctly before updating.
- You can't simulate 2 worlds at once
- The tick counter can get messed up

Installation
------------
Requirements:
* MCP version 9.10
* Minecraft version 1.8

Steps:
	
1. Clone the source code from here
2. Copy your MCP/conf folder to the 'mcp' directory. Tile files used are:
    * fields.csv
    * methods.csv
    * joined.srg
3. Copy the contents of your .minecraft folder into 'minecraft'. Using:
    * libraries
    * versions
		
That's it! You should now be able to compile and run.

Links
-----
* Where I've been updating my progress so far: http://forum.openredstone.org/showthread.php?tid=1606
* The best redstone community: http://openredstone.org/
* Lots of nice icons: http://p.yusukekamiyamane.com/
* The background: http://iwithered.deviantart.com/
* His Circular Byte Buffer proved very useful: http://ostermiller.org/utils/src/CircularByteBuffer.java.html
* Their Tag class to parse and write schematics: http://minecraft.gamepedia.com/Development_resources/Example_NBT_Class