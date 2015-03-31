# Configuration files

These files are meant to be extended to provide new graphics and additional block information. blocks.xml depends on
tilemaps.xml to determine how blocks are rendered graphically. This document is best viewed with a fixed-width font. A
'*' are for tags and '-' for attributes. Default values are contained in [square brackets], if no default value is
given, it's a mandatory field.

## blocks.xml

All information on blocks, how they are rotated and if and how they are displayed in the block panel.

* <category>: A generic category used for the block panel.
  - name: [] The name of the category.
  * <block>: Properties of a single block.
    - id: The block id. 
    - hidden: [false] A boolean to hide the block from the block panel.
    * <name>: The name of the block, displayed if the mouse hovers over its button.
    * <icon>: How the button's icon should be rendered.
      - orientation: [top] Either 'top', 'front' or 'right', the side that the button icon is rendered.
      - data: [0] The data value of the block icon.
    * <rotation>: Determines the data values set by the rotate tool.
      - mask: [15] The bits of the block's data that contain its rotation information.
      - min: [0] The lower bound of the block's rotation data value.
      - max: [15] The upper bound of the block's rotation data value.
      - sides: [] If a rotation depends on surrounding solid blocks, points to the required block from min to max. This
               is a string of characters of one of "wensud", standing for west, east, north, south, up and down
               respectively. If this is present, rotations will be limited to only valid ones.

## tilemaps.xml

The link between block data and their images. The aim was to have as few as possible duplicate images whilst
also minimizing xml code. It builds up file names part by part, separated with dashes ('-'). All of these images are
contained in the /img/tiles folder of the project. It's useful to first understand how this matching works before naming
the images, as there is some thought put into minimizing xml code. Note that having multiple tests of the same value
will have unpredictable effects.

* <tileset>: The root tag.
  - name: The name of the tileset.
  - extension: The extension of the image files.
  * <entry>: A single entry builds a single filename. If the block id matches one inside this entry, all further
             conditions are executed and the resulting filename is used. Otherwise the entry is skipped.
    - rotation: [0] The angle in degrees that the tile is rotated to the right, can only be '0', '90', '180' or '270'.
    - mirror: [] Indicates the tile needs to be mirrored, either 'horizontal' or 'vertical'. This happens after
                 rotating the tile. 
    * <name>: A part of the filename to be appended.
    * <condition>: A generic condition, entered its test matches, can only be one of either kinds. Conditions can occur
                   within conditions, though the id condition can only occur within <entry>. Inherits everything from
                   <entry>, thus it can contain both its attributes and elements.
      - id: The block id, at least one is mandatory within the root of the <entry> tag.
      - data: Matches the block's data to the given value.
      - mask: [15] Used with the data condition to mask the bits that need to be checked.
    * <top>/<front>/<right>: Used for orientation-dependent names, like condition, inherits <entry>.