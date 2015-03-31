# MC ticker

This project uses Java's reflection to dynamically load minecraft.jar and use its inner functions to simulate the game
from within another interface. The main aim of this project is to speed up the development of redstone circuits and
technical builds, as well as providing methods to help share schematics.

![This image was generated using MC ticker's export function](/doc/MC_ticker_logo.gif)

![The interface of the editor](/doc/Interface.png)

## Features
- Simulate all redstone components 100% accurate.
- Work with different schematics separately.
- Top-down, front and right perspectives.
- Place, rotate and activate blocks.
- Export images.
- Go tick by tick and backwards in time.
- Compatible with modded clients.

## Usage
* ![Activate](/img/tools/cursor.png) Send a right click to toggle levers, push buttons, switch comparator modes,
change repeater delay, ...
* ![Place](/img/tools/block.png) Right click places the selected block on the left side at the selected coordinates. Left
click replaces it with an air block. By default, surrounding blocks are updated. If the shift key is being held, nothing
is updated.
* ![Rotate](/img/tools/rotate.png) Rotates the selected block, left and right click will rotate in different directions.
* ![Select](/img/tools/select.png) Creates a selection of multiple blocks, similar to spreadsheet software, but in 3
dimensions. Shift will take the previous origin, if possible. Control or command will add to the previous selection, and
when done on an already selected region, will make a negative selection inside the previous selection. Selecting blocks
has currently no use, though. >.<
* The time bar allows manipulating the time of each schematic separately. When ticking, the previous schematic is
internally buffered, this enables going back in time. Be careful though, there is no branching of timelines. If you go
back in time and modify anything, the future is discarded and this is the new end of the timeline.

## Extensibility
MC ticker is able to handle modded clients, but currently has no graphics for any. Graphics are configured by XML files
that are located in the /conf folder. Detailed instructions on how to modify them can be found in said folder.

## Other redstone simulators
There have been a couple of them that I know of, but all their approaches were by manually recreating the behaviors.
This made them not 100% accurate, nor feature complete. The difference is that by using minecraft itself, all behaviors
are simualated (block and entity bahaviors, not bothering with weather and such), so in essence the GUI becomes the
bottleneck.

* Baezon's redstone simulator: http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-tools/1260528-baezons-redstone-simulator-v2-2
* Mordritch's JavaScript Redstone Simulator: http://mordritch.com/mc_rss/
* Rek's Circuit Simulator: http://www.minecraftforum.net/forums/minecraft-discussion/redstone-discussion-and/342852-circuit-simulator-v0-81-rek

## Credits
* Where I've been updating my progress so far: http://forum.openredstone.org/showthread.php?tid=1606
* The best redstone community: http://openredstone.org/
* Lots of nice icons: http://p.yusukekamiyamane.com/
* The background: http://iwithered.deviantart.com/
* His Circular Byte Buffer proved very useful: http://ostermiller.org/utils/src/CircularByteBuffer.java.html
* Their Tag class to parse and write schematics: http://minecraft.gamepedia.com/Development_resources/Example_NBT_Class
* The MC wiki for information and their NBT icons: http://minecraft.gamepedia.com/Minecraft_Wiki