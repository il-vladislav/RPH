"""
implements class rph maze

all the I/O are kept in its most siple form in order
to avoid necessary additional instalation

- input maps are in txt form
(one could apply convert map.bmp map.txt from imagemagick)
- Tkinter is applied for visualisation, nothing else
- The maze is represented in a simple 2D array,
  'white' means empty space (you can go there)
  'black' means wall
  'red' means starting point
  'blue' means target point

Tomas Svoboda, svobodat@fel.cvut.cz, 2010
"""

# import support for Regular Expressions (regexp)
import re

# colors from bitmap
EMPTY = "white"
WALL = "black"
START = "red"
FINAL = "blue"


class RPHMaze:
    def __init__(self,filename,startpos=[],finalpos=[]):
        """RPHMaze constructor, filename contains the maze image in txt form"""
        self.startpos = startpos
        self.finalpos = finalpos
        # the parse function sets all init parameters
        self.__parse_txtimage(filename)
        if self.startpos == []: #i.e. no start pos found in the image
            self.startpos = (0,1) # (almost )upper left corner 
        if self.finalpos == []:
            self.finalpos = (self.numrows-1,self.numcols-2) # almost lower right corner
        
    def __parse_txtimage(self,filename):
        """reads the image and fills the class variables.
           The main goal is to process the data and set variables
           im memory saving way"""
        try:
            f = open(filename,'r')
        except:
            raise IOError("could not open %s for reading"%filename)
        # read the first line and get the image dimension
        dataexpression = re.compile( r"# ImageMagick pixel enumeration: "
                                     r"(?P<numcols>\d{1,}),(?P<numrows>\d{1,}),(?P<bitdepth>\d{1,})")
        iminfo = dataexpression.search(f.readline())
        self.numrows = int(iminfo.group('numrows'))
        self.numcols = int(iminfo.group('numcols'))
        print "image of size ",self.numrows,"x",self.numcols
        ###
        # allocate space for the binary matrix
        # it is a list (rows) of lists (columns)
        # first index will represent row
        # second index will represent column of the maze
        ###
        self.binplan = []
        for i in range(self.numrows):
            print 'allocating memory for %03d-th row'%i
            mazerow = []
            for j in range(self.numcols): # fill the row i
                mazerow.append(False) # False means wall
            self.binplan.append(mazerow) # add the t-th row to the list
        ###
        # scan the rows of the image file and read coordinates
        # and the color
        ###
        coordinates_expr = re.compile(r"(?P<col>\d{1,}),(?P<row>\d{1,}):")
        color_expr = re.compile(r"\#[0-9A-F]{6}  (?P<color>[a-z]{2,})")
        print 'reading the image file, plase be patient, ...'
        for line in f:
            coordinates = coordinates_expr.search(line)
            col = int(coordinates.group('col'))
            row = int(coordinates.group('row'))
            # color on that position
            colorstring = color_expr.search(line)
            try:
                color = colorstring.group('color')
            except: # purple color represents error in uderstanding
                raise IOError("color of cell not found")
            if color==EMPTY: # empty space
                self.binplan[row][col]=True
            if color==START: # start point
                self.startpos = (row,col)
                self.binplan[row][col]=True
            if color==FINAL: # final position
                self.finalpos = (row,col)
                self.binplan[row][col]=True
        print 'reading of the image file finished'


    def __str__(self):
        """diplay info about the maze, perhaps useful for debug?"""
        return "__str__ not yet implemented, sorry"

    def inside(self,pos):
        """is the pos inside the maze?"""
        return(pos[0]>=0 and pos[0]<self.numrows and pos[1]>=0 and pos[1]<self.numcols)


    def isfinal(self,pos):
        """is the pos the final one?"""
        if self.finalpos[0] == pos[0] and self.finalpos[1] == pos[1]:
            return(True)
        else:
            return(False)
        
    def ispassable(self,pos):
        return self.binplan[pos[0]][pos[1]]

