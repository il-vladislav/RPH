"""
MazeGr class:
uses Canvas from Tkinter for drawing rectangular maze. A single cell
is represented by a color rectangle. Maze is composed from cells organized
in a grid.

inspired (probably, I might forget the proper source) by http://www.py.cz/SachovniceMrizka

Tomas Svoboda, 2010
"""

import os
from Tkinter import Canvas

# few constants
WIN_WIDTH = 800 # win height will be computed according to maze dimensions ...
MAX_WIN_HEIGHT = 800 # ... but do not go beyond the limit
BG_COLOR = 'white' # background color
BORDER_WIDTH = 50# border around the maze
LEGEND_HEIGHT = 100 # space for the text legend
FONT_FAMILY = 'helvetica'
FONT_SIZE = "12"
SNAPS_DIR = "maze_snaps" # where the maze snaps are to be printed, see print_maze method

# colors for the maze
# the colors are the same as in the bitmaps
# but they can be well different, no problem
# the colors here are just for displaying, no logic
# is tight with them
EMPTY = "white"
WALL = "black"
START = "red"
FINAL = "blue"

class MazeGr:
    def __init__(self,frame,maze,full_legend=False):
        """create a canvas object within the specified frame (graphical window)"""
        self.full_legend = full_legend
        self.maze = maze
        # self.maze_colors = maze.colors  
        self.__init_dimensions()
        # print "Maze size is",self.maze.numrows,"rows and",self.maze.numcols,"columns"
        self.maze_canvas = Canvas(frame,bg=BG_COLOR,
                             width=self.maze.numcols*self.cellw+2*BORDER_WIDTH,
                             height=self.maze.numrows*self.cellh+2*BORDER_WIDTH+LEGEND_HEIGHT)
        # self.maze_canvas.grid(row=0,column=0)
        self.maze_canvas.pack(expand='NO')
        self.__init_handlers()
        self.__draw_maze()
        self.__print_legend()
        self.id_text = 0

    def __init_dimensions(self):
        """computes some internal variables"""
        win_height = min(MAX_WIN_HEIGHT,round(self.maze.numcols/self.maze.numrows*WIN_WIDTH))
        self.cellw = (WIN_WIDTH-2*BORDER_WIDTH)/self.maze.numcols # cell width
        self.cellh = (win_height-2*BORDER_WIDTH)/self.maze.numrows # cell height
        self.fontsize = int(min(self.cellw,self.cellh)*0.2) # for printing characters into cells 

    def __init_handlers(self):
        """creates 2D arrays for cell handlers"""
        self.cells = []
        self.textcells = []
        for i in range(self.maze.numrows):
            # maze.append(array('c'))
            mazerow = []
            textrow = []
            for j in range(self.maze.numcols):
                mazerow.append(-1)
                textrow.append(-1)
            self.cells.append(mazerow)
            self.textcells.append(textrow)

    def __print_legend(self):
        """print some explanations to the graphical window"""
        upper_base = self.maze.numrows*self.cellh+1.5*BORDER_WIDTH
        right_base = BORDER_WIDTH/2
        height_step = 15;
        self.maze_canvas.create_text(right_base,upper_base+0*height_step, 
                                     anchor="nw",text="q->quit the program")
        self.maze_canvas.create_text(right_base,upper_base+1*height_step, 
                                     anchor="nw",text="d->find dummy path")
        if self.full_legend:
            self.maze_canvas.create_text(right_base,upper_base+2*height_step,
                                         anchor="nw",text="[1-9]->n steps ahead")
            self.maze_canvas.create_text(right_base,upper_base+3*height_step,
                                         anchor="nw",text="0,s->solve to the end")

    def __draw_maze(self):
        """draw the maze step by step by drawing the cells (draw_cell method)"""
        for i in range(self.maze.numrows):
            # row labels
            self.maze_canvas.create_text(BORDER_WIDTH-10,(i*self.cellh)+BORDER_WIDTH+self.cellh/2, text=str(i))
            for j in range(self.maze.numcols):
                if i==0: # 0-th row draw column labels
                    self.maze_canvas.create_text((j*self.cellw)+BORDER_WIDTH+self.cellw/2,BORDER_WIDTH-10,text=str(j))
                if self.maze.ispassable((i,j)):
                    self.draw_cell(i,j,EMPTY)
                else:
                    self.draw_cell(i,j,WALL)
        # draw the final and the start pos
        self.draw_start()
        self.draw_final()
                

    def update(self):
        self.maze_canvas.update()

    def print_maze(self,id):
        """Dump the current state of the maze to disk as an eps file"""
        # id is just a numerical increment to be included in the file name
        #    and title of the canvas 
        if self.id_text>0:
            self.maze_canvas.itemconfigure(self.id_text,text="Expansion step: %03d"%id)
        else:
            self.id_text = self.maze_canvas.create_text(BORDER_WIDTH/2,10, anchor="nw",
                                                        font=(FONT_FAMILY,FONT_SIZE),
                                                        text="Expansion step: %03d"%id)
        # check the existences of the target dir and create it if necessary
        if not( os.path.isdir(SNAPS_DIR) ):
            os.makedirs(SNAPS_DIR)
        eps_filename = os.path.join(SNAPS_DIR,"mazesnap%03d.eps"%id)
        self.maze_canvas.update()
        self.maze_canvas.postscript(file=eps_filename,colormode="color")

    def draw_start(self):
        """draw start position"""
        self.draw_cell(self.maze.startpos[0],self.maze.startpos[1],START)

    def draw_final(self):
        """draw final (target) position"""
        self.draw_cell(self.maze.finalpos[0],self.maze.finalpos[1],FINAL)

    def draw_cell(self,row,col,color='white'):
        '''draws a colored rectangle at the specified position'''
        if(self.cells[row][col]>0): # if already exists, just change the color ...
            # ... but only if not already exist
            if not(self.maze_canvas.itemcget(self.cells[row][col],"fill")==color):
                self.maze_canvas.itemconfigure(self.cells[row][col],fill=color)
            else: # if it does than leave it as it is
                pass
        else: # then draw a new one and update the handler
            # compute the upper left and lower right coordinates
            left = col*self.cellw+BORDER_WIDTH
            right = left+self.cellw
            upper = row*self.cellh+BORDER_WIDTH
            lower = upper+self.cellh
            # print left,upper,right,lower
            self.cells[row][col] = self.maze_canvas.create_rectangle(left,upper,right,lower,fill=color)

    def draw_text(self,row,col,text_to_draw):
        """prints text to the maze, text overlays the underlaying color"""
        if(self.textcells[row][col]>0): # if already exists, just re-draw the text
            self.maze_canvas.itemconfigure(self.textcells[row][col],text=text_to_draw)
        else:
            xpos = (col*self.cellw)+BORDER_WIDTH+self.cellw/2
            ypos = (row*self.cellh)+BORDER_WIDTH+self.cellh/2
            self.textcells[row][col] = self.maze_canvas.create_text(xpos,ypos,text=text_to_draw,
                                                                font=(FONT_FAMILY,str(self.fontsize)))
